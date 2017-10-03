package com.amazon.external.elasticmapreduce.s3distcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manifest {

	private Map<String, ManifestEntry> manifestByFullPath = new HashMap<String, ManifestEntry>();
	private List<ManifestEntry> manifests = new ArrayList<ManifestEntry>();
	
	public void addManifest(ManifestEntry entry) {
		manifests.add(entry);
		if (entry.srcPath != null) 
			manifestByFullPath.put(removeScheme(entry.srcPath), entry);
	}
	
	private static String removeScheme(String path) {
		if (path.startsWith("s3n://") || path.startsWith("s3a://"))
			return path.substring(3);
		return path;
	}
	
	public ManifestEntry getManifest(String srcPath, long fileSize) {
		ManifestEntry entry = manifestByFullPath.get(removeScheme(srcPath));
		if (entry != null && entry.size == fileSize)
			return entry;
		return null;
	}
	
	public List<ManifestEntry> getEntries() {
		return manifests;
	}
}
