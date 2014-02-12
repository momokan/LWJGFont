package lwjgfont;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lwjgfont.texture.Texture;
import lwjgfont.texture.TextureLoader;

import com.sun.org.apache.bcel.internal.generic.DSTORE;

public abstract class AbstractFont {
	protected static final FontMap	map = new FontMap();
//	protected final Map<Character, MappedFont>	map = new HashMap<Character, MappedFont>();

	public void drawString(String text, float dstX, float dstY, float dstZ) throws IOException {
		DrawPoint		drawPoint = new DrawPoint(dstX, dstY, dstZ);
		
		for (int i = 0; i < text.length(); i++) {
			drawCharacter(drawPoint, text.charAt(i));
		}
	}
	
	private void drawCharacter(DrawPoint drawPoint, char ch) throws IOException {
		MappedFont	font = getMappedCharacter(ch);
		
		if (font == null) {
			return;
		}

		float	dstX1 = drawPoint.dstX;
		float	dstY1 = drawPoint.dstY + font.getAscent();
		float	dstX2 = drawPoint.dstX + font.getAdvance();
		float	dstY2 = drawPoint.dstY - font.getDescent();
		float	srcX1 = font.getSrcX();
		float	srcY1 = font.getSrcY() - font.getAscent();
		float	srcX2 = font.getSrcX() + font.getAdvance();
		float	srcY2 = font.getSrcY() + font.getDescent();
		
		String		imagePath = getImagePath(font.getImageIndex());
		Texture	texture = TextureLoader.loadTexture(this.getClass(), imagePath);
		
		texture.draw(dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2);
		
		drawPoint.dstX += font.getAdvance();
	}
	
	public MappedFont getMappedCharacter(char ch) {
		return getFontMap().getMappedFont(ch);
	}
	
	private String getImagePath(int index) {
		return map.getImageFile(index);
	}

	protected abstract FontMap getFontMap();

	class DrawPoint {
		private float		dstX;
		private float		dstY;
		private float		dstZ;
		
		private DrawPoint(float dstX, float dstY, float dstZ) {
			this.dstX = dstX;
			this.dstY = dstY;
			this.dstZ = dstZ;
		}
	}
}
