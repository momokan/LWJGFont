package net.chocolapod.lwjgfont.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Packager {
	private static final byte[]	buff = new byte [1024 * 1024];
	
	private String	resourceDir = "";
	private String	targetDir = "";

	public void process(String distPath) throws IOException {
		ZipOutputStream	out = null;

		try {
			out = new ZipOutputStream(new FileOutputStream(distPath));
			
			packageFile(targetDir, targetDir + File.separator, out);
			packageFile(resourceDir, resourceDir + File.separator, out);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void packageFile(String filePath, String filePathMask, ZipOutputStream out) throws IOException {
		File		file = new File(filePath);
		
		if (file.isDirectory()) {
			for (File child: file.listFiles()) {
				packageFile(child.getPath(), filePathMask, out);
			}
		} else {
			FileInputStream		in = null;
			int						length;
			String					dstPath = file.getPath();
			
			if (dstPath.startsWith(filePathMask)) {
				dstPath = dstPath.substring(filePathMask.length());
			}

			try {
				in = new FileInputStream(file.getPath());
				out.putNextEntry(new ZipEntry(dstPath));
				
				while (0 < (length = in.read(buff))) {
					out.write(buff, 0, length);
				}
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void setResourceDir(String resourceDir) {
		this.resourceDir = resourceDir;
	}
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
}
