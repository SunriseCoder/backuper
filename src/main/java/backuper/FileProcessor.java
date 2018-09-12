package backuper;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import backuper.FileMetadata.Type;
import backuper.helpers.FormattingHelper;
import backuper.helpers.PrintHelper;

public class FileProcessor {
    private static final int COPY_BUFFER_SIZE = 1024 * 1024;

    private FileCopyStatus copyStatus;

    private List<FileMetadata> filesToCopy;
    private List<FileMetadata> filesToDelete;

    private String srcPath;
    private String dstPath;

    public FileProcessor() {
        copyStatus = new FileCopyStatus();
        filesToCopy = new ArrayList<>();
        filesToDelete = new ArrayList<>();
    }

    public void reset() {
        copyStatus.reset();
        filesToCopy.clear();
        filesToDelete.clear();
    }

    public void addFilesToCopy(Collection<FileMetadata> files) {
        filesToCopy.addAll(files);
        long filesSize = files.stream().mapToLong(fm -> fm.getSize()).sum();
        copyStatus.addAllFilesTotalSize(filesSize);
    }

    public void addFilesToDelete(Collection<FileMetadata> files) {
        filesToDelete.addAll(files);
    }

    public void start() throws IOException {
        copyStatus.start();
        for (FileMetadata file : filesToCopy) {
            if (Type.DIRECTORY.equals(file.getType())) {
                PrintHelper.println("Creating directory " + file.getPath());
                createDirectory(file);
            } else if (Type.SYMLINK.equals(file.getType())) {
                // Skipping symlink, because if we copy as ordinary file, access denied
            } else {
                String message = "Copying file: " + file.getPath() + " (" + FormattingHelper.humanReadableSize(file.getSize()) + ")";
                PrintHelper.println(message);
                copyFile(file);
            }
        }

        Collections.reverse(filesToDelete);
        for (FileMetadata file : filesToDelete) {
            PrintHelper.println("Deleting: " + file.getPath());
            deleteFile(file);
        }
    }

    private void createDirectory(FileMetadata file) throws IOException {
        Path path = Paths.get(dstPath, file.getRelativePath());
        Files.createDirectories(path);
    }

    private void copyFile(FileMetadata file) throws IOException {
        try (RandomAccessFile inputFile = new RandomAccessFile(srcPath + "/" + file.getRelativePath(), "r");
             RandomAccessFile outputFile = new RandomAccessFile(dstPath + "/" + file.getRelativePath(), "rw");) {

            FileChannel in = inputFile.getChannel();
            FileChannel out = outputFile.getChannel();

            copyStatus.setCurrentFileCopiedSize(0);
            copyStatus.setCurrentFileTotalSize(file.getSize());
            copyStatus.printCopyProgress();

            long read;
            ByteBuffer buffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);
            while ((read = in.read(buffer)) > 0) {
                buffer.flip();
                // TODO Debug here, problems due to copy, maybe use transfers
                out.write(buffer);
                copyStatus.addCopiedSize(read);
                copyStatus.printCopyProgress();
                buffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);
            }
        }
    }

    private void deleteFile(FileMetadata file) throws IOException {
        Path path = Paths.get(dstPath, file.getRelativePath());
        Files.delete(path);
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }
}
