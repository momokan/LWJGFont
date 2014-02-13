package lwjgfont.packager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceExtractor {

	private final Map<String, String>			resourcePaths;
	private LinkedHashMap<String, String>	resourceReplaces;
	private String								resourcesDir;

	public ResourceExtractor() {
		resourcePaths = new LinkedHashMap<>();
		resourceReplaces = new LinkedHashMap<>();
		resourcesDir = "";
	}
	
	public void copy() throws IOException {
		for (String resource: resourcePaths.keySet()) {
			File	file = new File(resourcePaths.get(resource));
			File	dstDir = LwjgFontUtil.prepareDirectory(resourcesDir, file.getParent());
				
			copyResource(resource, dstDir.getPath() + File.separator + file.getName());
		}
	}

	private void copyResource(String resource, String dstPath) throws IOException {
		BufferedReader		br = null;
		PrintWriter			pw = null;
		String				line;

		try {
			br = new BufferedReader(new InputStreamReader(ResourceExtractor.class.getResourceAsStream(resource)));
			pw = new PrintWriter(new FileOutputStream(dstPath));
			
			while ((line = br.readLine()) != null) {
				for (String pattern: resourceReplaces.keySet()) {
					line = line.replaceAll(pattern, resourceReplaces.get(pattern));
				}
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

	public void addResourcePath(String srcResourcePath, String dstResourcePath) {
		this.resourcePaths.put(srcResourcePath, dstResourcePath);
	}
	
	public void addReplacePattern(LwjgFontProperties properties, LwjgFontPropertyKey key) {
		String		pattern = LwjgFontPropertyKey.toResourceReplacePattern(key);
		String		replacement = properties.getAsString(key);
		
		resourceReplaces.put(pattern, replacement);
	}
	public void addReplacePatterns(LwjgFontProperties properties, LwjgFontPropertyKey ...keys) {
		for (LwjgFontPropertyKey key : keys) {
			addReplacePattern(properties, key);
		}
	}
	
	public void setResourcesDir(String resourcesDir) {
		this.resourcesDir = resourcesDir;
	}

}
