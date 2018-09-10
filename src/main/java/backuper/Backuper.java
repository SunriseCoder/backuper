package backuper;

import java.io.Console;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Backuper {
    private String srcPath;
    private String dstPath;

    private FolderScanner folderScanner;

    private Map<String, FileMetadata> newFiles;
    private Map<String, FileMetadata> changeSizedFiles;
    private Map<String, FileMetadata> deletedFiles;

    public Backuper() {
        folderScanner = new FolderScanner();
        newFiles = new LinkedHashMap<>();
        changeSizedFiles = new LinkedHashMap<>();
    }

    public void doBackup(Options options) throws IOException {
        System.out.println("Scanning source folder...");
        Map<String, FileMetadata> srcFiles = folderScanner.scan(srcPath, options);

        System.out.println("Scanning destination folder...");
        Map<String, FileMetadata> dstFiles = folderScanner.scan(dstPath, options);

        System.out.println("Comparing the trees...");
        compareFileTrees(srcFiles, dstFiles);
        printTreeDiffs();

        if (isBackupConfirmed()) {
            processFiles();
            System.out.println("Copying is done");
        } else {
            System.out.println("Copying has been cancelled");
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
        printMetadataCollection(newFiles.values(), "\t + ");

        System.out.println("Files with changed size:");
        printMetadataCollection(changeSizedFiles.values(), "\t * ");

        System.out.println("Deleted files:");
        printMetadataCollection(deletedFiles.values(), "\t - ");
    }

    private void printMetadataCollection(Collection<FileMetadata> collection, String prefix) {
        for (FileMetadata element : collection) {
            String message = prefix;

            switch (element.getType()) {
            case DIRECTORY:
                message += "|/ ";
                break;
            case FILE:
                message += "## ";
                break;
            case SYMLINK:
                message += "-> ";
                break;
            default:
                break;
            }

            message += element.getRelativePath();

            System.out.println(message);
        }
    }

    private boolean isBackupConfirmed() {
        Console console = System.console();
        if (console == null) {
            return true; // If console does not support by environment, do not asking for confirmation
        }

        System.out.println("Please type \"yes\" to start syncronization");
        String input = console.readLine();
        boolean result = "yes".equals(input);
        return result;
    }

    private void processFiles() throws IOException {
        System.out.println("Synchronization started...");

        FileProcessor processor = new FileProcessor();
        processor.setSrcPath(srcPath);
        processor.setDstPath(dstPath);

        processor.addFilesToCopy(newFiles.values());
        processor.addFilesToCopy(changeSizedFiles.values());
        processor.addFilesToDelete(deletedFiles.values());

        processor.start();
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }
}
