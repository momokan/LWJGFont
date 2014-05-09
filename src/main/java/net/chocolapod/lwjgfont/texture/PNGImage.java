package net.chocolapod.lwjgfont.texture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PNGImage {
	private static final long	PNG_SIGNATURE;
	static {
		ByteBuffer	buff = ByteBuffer.allocate(8);

		buff.put(new byte[] {(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A});
		buff.flip();

		PNG_SIGNATURE = buff.getLong();
	}
	
	private final Map<Class<? extends PNGChunk>, List<PNGChunk>>	chunkMap;

	public PNGImage() {
		this.chunkMap = new HashMap<>();
	}

	public ByteBuffer getImageData() {
		//	Texture 変換時に byte 配列で渡す必要があるため、byte 配列の最大長以上のデータについては考慮しない
		Inflater				decompresser = new Inflater();
		byte[]					decompressedBytes = new byte[1024 * 1024];
		ByteArrayOutputStream	out = new ByteArrayOutputStream();
		List<IDATChunk>			idatChunks = listChunks(IDATChunk.class);
		IDATChunk				chunk;

		try {
			while (decompresser.needsInput()) {
				if ((idatChunks.size() <= 0) || ((chunk = idatChunks.remove(0)) == null)) {
					System.out.println(decompresser.finished());
					break;
				}

				decompresser.setInput(chunk.getData(), 0, chunk.getDataLength());
				chunk.releaseData();

				int		decompressedSize;

				while (0 < (decompressedSize = decompresser.inflate(decompressedBytes))) {
					//	バイトの総量がわからないため、あとで結合する
					out.write(decompressedBytes, 0, decompressedSize);
				}
			}

			//	filter を適用した上で返す
			return new PNGFilterProcessor(out.toByteArray(), getChunk(IHDRChunk.class)).process();
		} catch (DataFormatException e) {
			throw new RuntimeException(e);
		} finally {
			decompresser.end();
		}
	}
	
	/**
	 *	この PNGImage インスタンスを BufferedImage インスタンスに変換して返す
	 */
	public BufferedImage toImage() {
		int				width = getWidth();
		int				height = getHeight();
		byte[]			srcBytes = getImageData().array();
		BufferedImage	image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int		index = ((y * width) + x) * 4;
				int		red = srcBytes[index] & 0xff;
				int		green = srcBytes[index + 1] & 0xff;
				int		blue = srcBytes[index + 2] & 0xff;
				int		alpha = srcBytes[index + 3] & 0xff;
				int		rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;

				//	TYPE_INT_ARGB のイメージの、指定の座標のピクセルを設定する
				image.setRGB(x, y, rgb);
			}
		}

		return image;
	}
	
	<T extends PNGChunk> void addChunk(T chunk) {
		List<PNGChunk>	list = chunkMap.get(chunk.getClass());
		
		if (list == null) {
			list = new ArrayList<>();
			chunkMap.put(chunk.getClass(), list);
		}
		
		list.add(chunk);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends PNGChunk> List<T> listChunks(Class<T> chunk) {
		return (List<T>)chunkMap.get(chunk);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends PNGChunk> T getChunk(Class<T> chunk) {
		List<PNGChunk>	list = chunkMap.get(chunk);
		
		return (T)list.get(0);
	}

	public int getWidth() {
		IHDRChunk	chunk = getChunk(IHDRChunk.class);

		return chunk.getWidth();
	}

	public int getHeight() {
		IHDRChunk	chunk = getChunk(IHDRChunk.class);

		return chunk.getHeight();
	}
	
	/**
	 *	指定のパスの PNG 画像を読み込んで PNGImage インスタンスとして返す
	 */
	public static PNGImage decode(String filePath) throws IOException {
		return decode(new FileInputStream(filePath));
	}

	/**
	 *	指定の InputStream の PNG 画像を読み込んで PNGImage インスタンスとして返す
	 */
	public static PNGImage decode(InputStream inputStream) throws IOException {
		PNGImage		pngImage = new PNGImage();
		DataInputStream	in = null;
		long			signature;
		byte[]			typeBytes = new byte[4];
		byte[]			dataBytes;
		int				dataSize;
		long			crc;
		PNGChunk		chunk;
		
		try {
			in = new DataInputStream(inputStream);

			//	read PNG sigunature
			signature = in.readLong();
			if (signature != PNG_SIGNATURE) {
				throw new IOException("PNG signature is invalid.");
			}

			while (true) {
				// read the length.
				if ((dataSize = in.readInt()) < 0) {
					throw new IOException("The data size is invalid: " + dataSize);
				}

				//	read the chunk type
				in.readFully(typeBytes);
				
				//	read the data.
				dataBytes = new byte[dataSize];
				in.readFully(dataBytes);
				
				//	read the CRC
				crc = in.readInt() & 0xffffffffl;
				if (verifyCRC(crc, typeBytes, dataBytes) == false) {
					throw new IOException("CRC check failure.");
				}

				//	load as PNGChunk
				if ((chunk = loadPNGChunk(typeBytes, dataBytes)) != null) {
					pngImage.addChunk(chunk);
				}
				
				if (in.available() <= 0) {
					break;
				}
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return pngImage;
	}

	private static boolean verifyCRC(long actualCrc, byte[] ...bytesList) {
		CRC32	crc = new CRC32();

		for (byte[] bytes: bytesList) {
			crc.update(bytes);
		}

		long	expectedCrc = crc.getValue();

		return (expectedCrc == actualCrc);
	}

	@SuppressWarnings("unchecked")
	private static PNGChunk loadPNGChunk(byte[] typeBytes, byte[] dataBytes) {
		String			chunkType = new String(typeBytes).toUpperCase();

		try {
//			String				className = PNGChunk.class.getPackage().getName() + "." + chunkType + "Chunk";
			String				enclosingClassName = PNGImage.class.getCanonicalName();
			String				className = enclosingClassName + "$" + chunkType + "Chunk";
			Class<?>			enclosingClass = PNGChunk.class.getClassLoader().loadClass(enclosingClassName);
			Class<PNGChunk>		clazz = (Class<PNGChunk>)PNGChunk.class.getClassLoader().loadClass(className);
			Constructor<?>		constructor = clazz.getDeclaredConstructor(enclosingClass, byte[].class);
//			PNGChunk			chunk = (PNGChunk)constructor.newInstance(dataBytes);
			PNGChunk			chunk = (PNGChunk)constructor.newInstance(new PNGImage(), dataBytes);

			return chunk;
		} catch (Exception e) {
			System.err.println("Unsupported chunk: " + chunkType);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *	参考
	 *	http://www.w3.org/TR/PNG/#11IHDR
	 *	http://en.wikipedia.org/wiki/Portable_Network_Graphics
	 */
	enum PNGColorType {
		Greyscale(0, allowedDepths(1, 2, 4, 8, 16)),
		TrueColor(2, allowedDepths(8, 16)) {
			@Override
			protected int getSrcColor(PNGFilterProcessor processor, byte[] srcBytes, int srcOffset, int[] srcColor) {
				int		depth = processor.getDepth();
				
				if (depth == 8) {
					srcColor[0] = srcBytes[srcOffset + 0] & 0xff;
					srcColor[1] = srcBytes[srcOffset + 1] & 0xff;
					srcColor[2] = srcBytes[srcOffset + 2] & 0xff;
					srcColor[3] = 0xff;
					return 3;
				}
				
				return 0;
			}
			@Override
			protected void arrange(int[] dstColor) {
				//	Alpha は元データにないので、必ず 0xff とする
				dstColor[3] = 0xff;
			}
		},
		IndexedColor(3, allowedDepths(1, 2, 4, 8)),
		GreyscaleWithAlpha(4, allowedDepths(8, 16)),
		TrueColorWithAlpha(6, allowedDepths(8, 16)) {
			@Override
			protected int getSrcColor(PNGFilterProcessor processor, byte[] srcBytes, int srcOffset, int[] srcColor) {
				int		depth = processor.getDepth();
				
				if (depth == 8) {
					srcColor[0] = srcBytes[srcOffset + 0] & 0xff;
					srcColor[1] = srcBytes[srcOffset + 1] & 0xff;
					srcColor[2] = srcBytes[srcOffset + 2] & 0xff;
					srcColor[3] = srcBytes[srcOffset + 3] & 0xff;
					return 4;
				}
				
				return 0;
			}
		};

		private final int			value;
		private final Set<Integer>	allowedDepths;

		private PNGColorType(int value, Set<Integer> allowedDepths) {
			this.value = value;
			this.allowedDepths = allowedDepths;
		}

		protected int getSrcColor(PNGFilterProcessor processor, byte[] srcBytes, int srcOffset, int[] dstBytes) {
			throw new UnsupportedOperationException("not implemented yet.");
		}
		
		protected void arrange(int[] dstColor) {}
		
		boolean isAllowedDepth(int depth) {
			return allowedDepths.contains(depth);
		}

		static Set<Integer> allowedDepths(Integer ...values) {
			return new HashSet<Integer>(Arrays.asList(values));
		}
		
		static PNGColorType valueOf(int value) {
			for (PNGColorType colorType: values()) {
				if (value == colorType.value) {
					return colorType;
				}
			}
			return null;
		}
	}
	
	/**
	 *	参考
	 *	http://hoshi-sano.hatenablog.com/entry/2013/08/18/113434
	 *	http://www.w3.org/TR/PNG-Filters.html
	 */
	enum PNGFilter {
		None(0),	//	そのまま srcColor を返す
		Sub(1) {
			@Override
			protected int[] filter(PNGFilterProcessor processor, int[] srcColor, int x, int y) {
				//	当該ピクセルの左隣のピクセルの色(フィルタリング前の値)との差分として保存されている
				//	フィルタリング前の値??差分を色として使うはずないので違うと思います

				int[]	leftColor = leftColor(processor, x, y);

				return new int[] {
						leftColor[0] + srcColor[0],
						leftColor[1] + srcColor[1],
						leftColor[2] + srcColor[2],
						leftColor[3] + srcColor[3]
				};
			}
		},
		Up(2) {
			@Override
			protected int[] filter(PNGFilterProcessor processor, int[] srcColor, int x, int y) {
				//	当該ピクセルの直上のピクセルの色(フィルタリング前の値)との差分として保存されている。

				int[]	aboveColor = aboveColor(processor, x, y);
				
				return new int[] {
						aboveColor[0] + srcColor[0],
						aboveColor[1] + srcColor[1],
						aboveColor[2] + srcColor[2],
						aboveColor[3] + srcColor[3]
				};
			}
		},
		Average(3) {
			@Override
			protected int[] filter(PNGFilterProcessor processor, int[] srcColor, int x, int y) {
				int[]	leftColor = leftColor(processor, x, y);
				int[]	aboveColor = aboveColor(processor, x, y);

				return new int[] {
						reverseAverage(srcColor[0], leftColor[0], aboveColor[0]),
						reverseAverage(srcColor[1], leftColor[1], aboveColor[1]),
						reverseAverage(srcColor[2], leftColor[2], aboveColor[2]),
						reverseAverage(srcColor[3], leftColor[3], aboveColor[3])
				};
			}
			private int reverseAverage(int srcColor, int leftColor, int aboveColor) {
				//	Average(x) + floor((Raw(x-bpp)+Prior(x))/2)
				return (int)(srcColor + Math.floor((leftColor + aboveColor) / 2));
			}
		},
		Paeth(4) {
			@Override
			protected int[] filter(PNGFilterProcessor processor, int[] srcColor, int x, int y) {
				int[]	leftColor = leftColor(processor, x, y);
				int[]	aboveColor = aboveColor(processor, x, y);
				int[]	upperLeftColor = upperLeftColor(processor, x, y);

				return new int[] {
						srcColor[0] + predictPaeth(leftColor[0], aboveColor[0], upperLeftColor[0]),
						srcColor[1] + predictPaeth(leftColor[1], aboveColor[1], upperLeftColor[1]),
						srcColor[2] + predictPaeth(leftColor[2], aboveColor[2], upperLeftColor[2]),
						srcColor[3] + predictPaeth(leftColor[3], aboveColor[3], upperLeftColor[3])
				};
			}

			private int predictPaeth(int leftColor, int aboveColor, int upperLeftColor) {
				//	a = left pixel's color value
				//	b = above pixel's color value
				//	c = upper left pixel's color value

				// initial estimate
				int		p = leftColor + aboveColor - upperLeftColor;

				// distances to a, b, c
				int		pa = Math.abs(p - leftColor);
				int		pb = Math.abs(p - aboveColor);
				int		pc = Math.abs(p - upperLeftColor);

				// returns nearest of a,b,c,
				// breaking ties in order a,b,c.
				if ((pa <= pb) && (pa <= pc)) {
					return leftColor;
				} else if (pb <= pc) {
					return aboveColor;
				} else {
					return upperLeftColor;
				}
			}
		};
		
		private final int value;
		
		private PNGFilter(int value) {
			this.value = value;
		}
		
		static PNGFilter valueOf(int value) {
			for (PNGFilter filter: PNGFilter.values()) {
				if (value == filter.value) {
					return filter;
				}
			}
			return null;
		}

		protected int[] filter(PNGFilterProcessor processor, int[] srcColor, int x, int y) {
			return srcColor;
		}
		
		protected int[] leftColor(PNGFilterProcessor processor, int x, int y) {
			if (x - 1 < 0) {
				//	For all filters, the bytes "to the left of" the first pixel in a scanline shall be treated as being zero.
				//	行の先頭ピクセルの左隣のピクセルは、0 として扱う
				return new int[] {0, 0, 0, 0};
			} else {
				return processor.getDstColor(-1, 0);
			}
		}
		protected int[] aboveColor(PNGFilterProcessor processor, int x, int y) {
			if (y - 1 < 0) {
				return new int[] {0, 0, 0, 0};
			} else {
				return processor.getDstColor(0, -1);
			}
		}
		protected int[] upperLeftColor(PNGFilterProcessor processor, int x, int y) {
			if ((y - 1 < 0) || (x - 1 < 0)) {
				return new int[] {0, 0, 0, 0};
			} else {
				return processor.getDstColor(-1, -1);
			}
		}
	}

	/**
	 *	Marker interface for all PNG chunk classes.
	 */
	interface PNGChunk {
	}

	/**
	 *	The chunk represents image header.
	 */
	class IHDRChunk implements PNGChunk {
		private int				width;
		private int				height;
		private int				depth;
		private PNGColorType	colorType;
		private int				compression;
		private int				filter;
		private int				interlace;

		IHDRChunk(byte[] dataBytes) {
			ByteBuffer	buffer = ByteBuffer.wrap(dataBytes);
			
			this.width = buffer.getInt();
			this.height = buffer.getInt();
			this.depth = buffer.get() & 0xff;
			this.colorType = PNGColorType.valueOf(buffer.get() & 0xff);
			this.compression = buffer.get() & 0xff;
			this.filter = buffer.get() & 0xff;
			this.interlace = buffer.get() & 0xff;
		}

		int getWidth() {
			return width;
		}
		int getHeight() {
			return height;
		}
		int getDepth() {
			return depth;
		}
		PNGColorType getColorType() {
			return colorType;
		}
		int getCompression() {
			return compression;
		}
		int getFilter() {
			return filter;
		}
		int getInterlace() {
			return interlace;
		}

	}
	
	/**
	 *	The chunk represents image data
	 */
	class IDATChunk implements PNGChunk {
		private byte[] dataBytes;

		IDATChunk(byte[] dataBytes) {
			this.dataBytes = dataBytes;
		}

		byte[] getData() {
			return dataBytes;
		}
		
		int getDataLength() {
			return dataBytes.length;
		}
		
		void releaseData() {
			dataBytes = new byte[] {};
		}
	}

	/**
	 *	The chunk represents image trailer
	 */
	class IENDChunk implements PNGChunk {

		IENDChunk(byte[] dataBytes) {
			//	The byte data is ignored.
		}

	}

	/**
	 *	The chunk represents physical pixel dimensions
	 */
	class PHYSChunk implements PNGChunk {

		PHYSChunk(byte[] dataBytes) {
			//	The byte data is ignored.
		}

	}

	/**
	 *	The chunk represents image last-modification time
	 */
	class TIMEChunk implements PNGChunk {

		TIMEChunk(byte[] dataBytes) {
			//	The byte data is ignored.
		}

	}
	
	class ICCPChunk implements PNGChunk {

		ICCPChunk(byte[] dataBytes) {
			//	The byte data is ignored.
		}

	}
	
	class CHRMChunk implements PNGChunk {

		CHRMChunk(byte[] dataBytes) {
			//	The byte data is ignored.
		}

	}

	class PNGFilterProcessor {
		private final byte[]	srcBytes;
		private final IHDRChunk	ihdrChunk;

		private byte[]			dstBytes;
		private int				dstOffset;
		private int				srcOffset;

	 	PNGFilterProcessor(byte[] srcBytes, IHDRChunk ihdrChunk) {
			this.srcBytes = srcBytes;
			this.ihdrChunk = ihdrChunk;
		}

		ByteBuffer process() {
			if (!ihdrChunk.getColorType().isAllowedDepth(ihdrChunk.getDepth())) {
				throw new IllegalArgumentException("Unsupported depth " + ihdrChunk.getDepth() + " for " + ihdrChunk.getColorType());
			}

			PNGFilter		filter;

			dstBytes = new byte[getWidth() * getHeight() * 4];
			dstOffset = 0;
			srcOffset = 0;
			for (int y = 0; y < getHeight(); y++) {
				filter = PNGFilter.valueOf(srcBytes[srcOffset]);
				srcOffset++;

				for (int x = 0; x < getWidth(); x++) {
					try {
						int[]	srcColor = new int[4];
						int		readCount = getColorType().getSrcColor(this, srcBytes, srcOffset, srcColor);
						int[]	dstColor = filter.filter(this, srcColor, x, y);

						getColorType().arrange(dstColor);
						srcOffset += readCount;
						
						if (y == 1) {
							System.out.print("");
						}

						//	RGBA の順に 1 byte ずつ書き込む
						dstBytes[dstOffset + 0] = (byte)dstColor[0];
						dstBytes[dstOffset + 1] = (byte)dstColor[1];
						dstBytes[dstOffset + 2] = (byte)dstColor[2];
						dstBytes[dstOffset + 3] = (byte)dstColor[3];
						dstOffset += 4;
					} catch(RuntimeException e) {
						throw e;
					}
				}
			}

			if (srcOffset != srcBytes.length) {
				throw new RuntimeException("calculated size is invalid: expected " + srcBytes.length + " but " + srcOffset);
			}
			
			ByteBuffer		directBuffer = ByteBuffer.allocateDirect(dstOffset);
			
			directBuffer.put(dstBytes);
			directBuffer.flip();

			return directBuffer;
		}

		int[] getDstColor(int xOffset, int yOffset) {
			int		offset = (xOffset * 4) + (yOffset * ihdrChunk.getWidth() * 4);
			
			return new int[] {
					dstBytes[dstOffset + 0 + offset] & 0xff,
					dstBytes[dstOffset + 1 + offset] & 0xff,
					dstBytes[dstOffset + 2 + offset] & 0xff,
					dstBytes[dstOffset + 3 + offset] & 0xff
			};
		}
		
		int getDepth() {
			return ihdrChunk.getDepth();
		}

		PNGColorType getColorType() {
			return ihdrChunk.getColorType();
		}
		
		int getWidth() {
			return ihdrChunk.getWidth();
		}

		int getHeight() {
			return ihdrChunk.getHeight();
		}

	}

}
