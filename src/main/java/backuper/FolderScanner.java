package backuper;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class FolderScanner {
	public Queue<File> foldersToScan;
	public Map<String, File> foundFiles;

	public synchronized Map<String, File> scan(String path) {
		reset();

		File startFolder = new File(path);
		foldersToScan.add(startFolder);

		while (!foldersToScan.isEmpty()) {
			scanNextSourceFolder();
			printStatus();
		}

		System.out.println();

		return foundFiles;
	}

	private void reset() {
		foldersToScan = new LinkedList<>();
		foundFiles = new LinkedHashMap<>();
	}

	private void scanNextSourceFolder() {
		File folder = foldersToScan.poll();
		File[] files = folder.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				foldersToScan.add(file);
			} else {
				foundFiles.put(file.getPath(), file);
			}
		}
	}

	private void printStatus() {
		String message = "Scanning: " + foldersToScan.size() + ", found: " + foundFiles.size();
		System.out.print(message + " \r");
	}
}
