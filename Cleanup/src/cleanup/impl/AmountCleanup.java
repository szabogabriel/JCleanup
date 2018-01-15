package cleanup.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AmountCleanup implements Cleanup {
	
	private static final Pattern PATTERN = Pattern.compile("amount (name|date)?( asc| desc)? ([0-9]+)");
	
	private final String operation;
	private final boolean desc;
	private final int amount;
	
	public AmountCleanup(String line) {
		Matcher m = PATTERN.matcher(line.trim());
		if (m.matches()) {
			operation = getOperation(m.group(1));
			desc = !"asc".equals(m.group(2));
			amount = Integer.parseInt(m.group(3));
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private String getOperation(String oper) {
		String ret = oper;
		if (oper == null || oper.length() == 0) {
			ret = "date";
		}
		return ret;
	}

	@Override
	public CleanupFolderResult performCleanup(CleanupFolderParams configuration) {
		File root = configuration.getRootFolder();
		
		List<File> tmp = Arrays.asList(root.listFiles()).stream()
			.filter(f -> f.isFile())
			.filter(f -> !f.getName().startsWith("."))
			.sorted((f1, f2) -> compare(f1, f2))
			.collect(Collectors.toList());
		
		if (amount < tmp.size()) {
			for (int i = 0; i < amount; i++) {
				tmp.remove(tmp.size() - 1);
			}
		}
		
		if (!configuration.isListing()) {
			tmp.stream().forEach(f -> f.delete());
		}
		
		
		return () -> tmp;
	}
	
	private int compare(File a, File b) {
		int ret = 0;
		
		switch(operation) {
		case "name" : ret = b.getName().compareTo(a.getName()); break;
		case "date" : ret = (int)(b.lastModified() - a.lastModified()); break;
		}
		
		if (!desc) {
			ret *= -1;
		}
		
		return ret;
	}

}
