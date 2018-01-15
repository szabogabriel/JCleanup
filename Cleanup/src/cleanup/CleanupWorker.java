package cleanup;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cleanup.impl.CleanupFolderParams;

public class CleanupWorker {
	
	private final RuntimeParams PARAMS;
	
	public CleanupWorker(RuntimeParams params) {
		this.PARAMS = params;
	}
	
	public void performCleanup() {
		cleanup(PARAMS.getRootDir());
	}
	
	private List<File> cleanup(File root) {
		List<File> ret = new LinkedList<>();
		if (root.isDirectory()) {
			FolderConfiguration folderConfig = new FolderConfiguration(createConfigFile(root));
			ret.addAll(folderConfig.createCleanup().performCleanup(new CleanupFolderParams() {
				
				@Override
				public boolean isListing() {
					return false;
				}
				
				@Override
				public File getRootFolder() {
					return root;
				}
			}).deletedFiles());

			if (PARAMS.isRecoursive()) {
				for (File it : root.listFiles()) {
					if (it.isDirectory()) {
						ret.addAll(cleanup(it));
					}
				}
			}
		}
		
		return ret;
	}
	
	private File createConfigFile(File root) {
		return new File(root.getAbsolutePath() + "/.cleanup");
	}

}
