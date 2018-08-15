package backuper;

import java.io.File;

public class FileMetadata {
    private String name;
    private String path;
    private long size;

    public FileMetadata(File file) {
        this.name = file.getName();
        this.path = file.getPath();
        this.size = file.length();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }
}
