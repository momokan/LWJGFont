package lwjgfont.packager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PackagedResources {
	private static final List<String>	resources = new ArrayList<String>();
	private static final String			resourcePathMask = "packagedResources/";
	static {
		resources.add("packagedResources/META-INF/maven/lwjgfont/lwjgfont/pom.properties");
		resources.add("packagedResources/META-INF/maven/lwjgfont/lwjgfont/pom.xml");
		resources.add("packagedResources/META-INF/MANIFEST.MF");
	}

	private String	resourcesDir = "";
	
	
	public void copy() throws IOException {
		for (String resource: resources) {
			File	file = new File(resource.replace(resourcePathMask, ""));
			File	dstDir = LwjgFontUtil.prepareDirectory(resourcesDir, file.getParent());
				
			copyResource(resource, dstDir.getPath() + File.separator + file.getName());
		}
	}

	private void copyResource(String resource, String dstPath) throws IOException {
		BufferedReader		br = null;
		PrintWriter			pw = null;
		String				line;

		try {
			br = new BufferedReader(new InputStreamReader(PackagedResources.class.getResourceAsStream(resource)));
			pw = new PrintWriter(new FileOutputStream(dstPath));
			
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
		
		// TODO Auto-generated method stub
		
	}

	public void setResourcesDir(String resourcesDir) {
		this.resourcesDir = resourcesDir;
	}
}
