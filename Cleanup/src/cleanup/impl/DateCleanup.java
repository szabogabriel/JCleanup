package cleanup.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateCleanup implements Cleanup {
	
	private static final Map<String, Long> MULTIPLIERS = new HashMap<>();
	
	private static final Pattern PATTERN = Pattern.compile("date( create| edit| access)? ([+-]?)([0-9]+)([shmdwMy])?");
	
	static {
		MULTIPLIERS.put("s", 1000L);
		MULTIPLIERS.put("m", 60L * 1000L);
		MULTIPLIERS.put("h", 60L * 60L * 1000L);
		MULTIPLIERS.put("d", 24L * 60L * 60L * 1000L);
		MULTIPLIERS.put("w", 7L * 24L * 60L * 60L * 1000L);
		MULTIPLIERS.put("M", 30L * 24L * 60L * 60L * 1000L);
		MULTIPLIERS.put("y", 365L * 24L * 60L * 60L * 1000L);
	}
	
	private final String accessType;
	private final boolean greaterThan;
	private final long amount;
	private final long now = System.currentTimeMillis();
	
	public DateCleanup(String line) {
		Matcher m = PATTERN.matcher(line);
		if (m.matches()) {
			accessType = getAccessType(m.group(1));
			greaterThan = isGreaterThan(m.group(2));
			amount = Long.parseLong(m.group(3)) * MULTIPLIERS.get(getMultiplier(m.group(4)));
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private String getMultiplier(String param) {
		String ret = param;
		if (ret.length() == 0) {
			ret = "d";
		}
		return ret;
	}
	
	private String getAccessType(String param) {
		String ret = param;
		if (ret == null || ret.length() == 0) {
			ret = "create";
		}
		return ret;
	}
	
	private boolean isGreaterThan(String param) {
		boolean ret = true;
		if (param.length() != 0 && "-".equals(param)) {
			ret = false;
		}
		return ret;
	}
	
	@Override
	public CleanupFolderResult performCleanup(CleanupFolderParams configuration) {
		File root = configuration.getRootFolder();
		
		List<File> deletedFiles = new LinkedList<>();
		
		Arrays.asList(root.listFiles()).stream()
			.filter(f -> f.isFile())
			.filter(f -> !f.getName().startsWith("."))
			.filter(f -> shouldBeDeleted(getConfiguredDate(f), amount))
			.forEach(f -> {
				deletedFiles.add(f); 
				if (!configuration.isListing()) f.delete(); 
			});
		
		return () -> deletedFiles;
	}
	
	private boolean shouldBeDeleted(long fileDate, long intervalDate) {
		boolean ret = false;
		
		if (greaterThan) {
			ret = (fileDate + intervalDate) < now;
		} else {
			ret = (fileDate + intervalDate) > now;
		}
		
		return ret;
	}
	
	private long getConfiguredDate(File f) {
		long ret = Long.MAX_VALUE;
		switch (accessType) {
		case "create" : try { ret = Files.readAttributes(f.toPath(), BasicFileAttributes.class).creationTime().toMillis(); } catch (IOException e) { e.printStackTrace(); } break;
		case "edit"   : try { ret = Files.readAttributes(f.toPath(), BasicFileAttributes.class).lastModifiedTime().toMillis(); } catch (IOException e) { e.printStackTrace(); } break;
		case "access" : try { ret = Files.readAttributes(f.toPath(), BasicFileAttributes.class).lastAccessTime().toMillis(); } catch (IOException e) { e.printStackTrace(); } break;
		}
		return ret;		
	}
	
}
