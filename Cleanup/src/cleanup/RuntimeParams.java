package cleanup;

import java.io.File;

public class RuntimeParams {
	
	private File rootDir = new File(".");
	
	private boolean recoursive = false;
	
	private boolean listing = false;

	public File getRootDir() {
		return rootDir;
	}

	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}

	public boolean isRecoursive() {
		return recoursive;
	}

	public void setRecoursive(boolean recoursive) {
		this.recoursive = recoursive;
	}
	
	public boolean isListing() {
		return listing;
	}
	
	public void setListing(boolean listing) {
		this.listing = listing;
	}
	
	
}
