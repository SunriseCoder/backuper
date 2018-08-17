package backuper;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class FolderScanner {
    private Options options;
    private Path startPath;
    private Queue<Path> foldersToScan;
    private Map<String, FileMetadata> foundFiles;

    public synchronized Map<String, FileMetadata> scan(String path, Options options) throws IOException {
        reset(options);

        this.startPath = Paths.get(path);
        foldersToScan.add(startPath);

        while (!foldersToScan.isEmpty()) {
            scanNextSourceFolder();
            printStatus();
        }

        System.out.println();

        return foundFiles;
    }

    private void reset(Options options) {
        this.options = options;
        foldersToScan = new LinkedList<>();
        foundFiles = new LinkedHashMap<>();
    }

    private void scanNextSourceFolder() throws IOException {
        Path folder = foldersToScan.poll();

        if (Files.isSymbolicLink(folder) && !options.isSet(Options.Names.FOLLOW_SYMLINKS)) {
            return; // No need to scan Symlinks
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder)) {
            for (Path path : ds) {
                if (Files.isDirectory(path)) {
                    foldersToScan.add(path);
                }

                String relativePath = startPath.relativize(path).toString();
                foundFiles.put(relativePath, new FileMetadata(path, relativePath));
            }
        } catch (AccessDeniedException e) {
            return; // No scan of the folder, where we don't have permissions
        }
    }

    private void printStatus() {
        String message = "Scanning: " + foldersToScan.size() + ", found: " + foundFiles.size();
        System.out.print(message + " \r");
    }
}
