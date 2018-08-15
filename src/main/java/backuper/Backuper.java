package backuper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Backuper {
    private FolderScanner folderScanner;
    private Map<String, FileMetadata> newFiles;
    private Map<String, FileMetadata> changeSizedFiles;
    private Map<String, FileMetadata> deletedFiles;

    public Backuper() {
        folderScanner = new FolderScanner();
        newFiles = new LinkedHashMap<>();
        changeSizedFiles = new LinkedHashMap<>();
    }

    public void doBackup(String srcPath, String dstPath) {
        System.out.println("Scanning source folder...");
        Map<String, FileMetadata> srcFiles = folderScanner.scan(srcPath);

        System.out.println("Scanning destination folder...");
        Map<String, FileMetadata> dstFiles = folderScanner.scan(dstPath);

        System.out.println("Comparing the trees...");
        compareFileTrees(srcFiles, dstFiles);
        printTreeDiffs();

        if (isBackupConfirmed()) {
            doBackup();
        }
    }

    private void compareFileTrees(Map<String, FileMetadata> srcFiles, Map<String, FileMetadata> dstFiles) {
        for (Entry<String, FileMetadata> srcFile : srcFiles.entrySet()) {
            String srcKey = srcFile.getKey();
            if (!dstFiles.containsKey(srcKey)) {
                newFiles.put(srcKey, srcFile.getValue());
            } else {
                FileMetadata srcMetadata = srcFile.getValue();
                FileMetadata dstMetadata = dstFiles.get(srcKey);
                if (srcMetadata.getSize() != dstMetadata.getSize()) {
                    changeSizedFiles.put(srcKey, srcMetadata);
                }
            }
        }

        deletedFiles = dstFiles.entrySet().stream().filter(e -> !srcFiles.containsKey(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private void printTreeDiffs() {
        System.out.println("New files:");
        printCollection(newFiles.keySet(), "\t + ");

        System.out.println("Files with changed size:");
        printCollection(changeSizedFiles.keySet(), "\t * ");

        System.out.println("Deleted files:");
        printCollection(deletedFiles.keySet(), "\t - ");
    }

    private void printCollection(Collection<String> collection, String prefix) {
        for (String element : collection) {
            System.out.println(prefix + element);
        }
    }

    private boolean isBackupConfirmed() {
        System.out.println("Please type \"yes\" to start syncronization");
        String input = System.console().readLine();
        boolean result = "yes".equals(input);
        return result;
    }

    private void doBackup() {
        System.out.println("Synchronization started...");

        // TODO Auto-generated method stub
    }
}
