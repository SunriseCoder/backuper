package backuper;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class FolderScanner {
    private String path;
    private Queue<File> foldersToScan;
    private Map<String, FileMetadata> foundFiles;

    public synchronized Map<String, FileMetadata> scan(String path) {
        reset();

        File startFolder = new File(path);
        this.path = startFolder.getPath();
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
                String relativePath = getRelativePath(file);
                foundFiles.put(relativePath, new FileMetadata(file));
            }
        }
    }

    private String getRelativePath(File file) {
        String filePath = file.getPath();
        String relativePath = filePath.substring(path.length() + 1);
        return relativePath;
    }

    private void printStatus() {
        String message = "Scanning: " + foldersToScan.size() + ", found: " + foundFiles.size();
        System.out.print(message + " \r");
    }
}
