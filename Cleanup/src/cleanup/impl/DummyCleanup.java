package cleanup.impl;

import java.io.File;
import java.util.LinkedList;

public class DummyCleanup implements Cleanup {

	@Override
	public CleanupFolderResult performCleanup(CleanupFolderParams configuration) {
		return () -> new LinkedList<File>();
	}

}
