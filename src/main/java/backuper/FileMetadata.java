package backuper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMetadata {
    private Type type;
    private String name;
    private String path;
    private String relativePath;
    private long size;

    public FileMetadata(Path path, String relativePath) throws IOException {
        if (Files.isRegularFile(path)) {
            this.type = Type.FILE;
        }
        if (Files.isDirectory(path)) {
            this.type = Type.DIRECTORY;
        }
        if (Files.isSymbolicLink(path)) {
            this.type = Type.SYMLINK;
        }
        this.name = path.getFileName().toString();
        this.path = path.toString();
        this.relativePath = relativePath;
        this.size = Files.size(path);
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public long getSize() {
        return size;
    }

    public static enum Type {
        DIRECTORY, FILE, SYMLINK
    }
}
