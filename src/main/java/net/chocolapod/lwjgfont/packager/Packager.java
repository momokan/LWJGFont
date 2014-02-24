/**
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2014 momokan (http://lwjgfont.chocolapod.net)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
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
			
			//	Windows 環境ではファイルセパレーターが \ なので、
			//	Jar にパッケージする際のパスでは / に置き換えておく。
			if (File.separatorChar == '\\') {
				dstPath = dstPath.replaceAll("\\\\", "/");
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
