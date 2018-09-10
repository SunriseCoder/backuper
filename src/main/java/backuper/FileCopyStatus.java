package backuper;

public class FileCopyStatus {
    private long currentFileCopiedSize;
    private long currentFileTotalSize;
    private long allFilesCopiedSize;
    private long allFilesTotalSize;
    private long lastPrintTime;
    private long lastPrintAllFilesCopiedSize;

    public void reset() {
        currentFileCopiedSize = 0;
        currentFileTotalSize = 0;
        allFilesCopiedSize = 0;
        allFilesTotalSize = 0;
        lastPrintAllFilesCopiedSize = 0;
    }

    public void addAllFilesTotalSize(long delta) {
        this.allFilesTotalSize += delta;
    }

    public void addCopiedSize(long delta) {
        this.currentFileCopiedSize += delta;
        this.allFilesCopiedSize += delta;
    }

    public void printCopyProgress() {
        long now = System.currentTimeMillis();
        long timeDelta = now - lastPrintTime;
        if (timeDelta >= 1000) {
            long currentPercent = currentFileCopiedSize * 100 / currentFileTotalSize;
            long totalPercent = allFilesCopiedSize * 100 / allFilesTotalSize;
            long copiedDelta = allFilesCopiedSize - lastPrintAllFilesCopiedSize;
            long speed = copiedDelta * 1000 / timeDelta;

            System.out.print(currentPercent + "% / " + totalPercent + "%, speed: " + speed + "\r");

            lastPrintTime = now;
            lastPrintAllFilesCopiedSize = allFilesCopiedSize;
        }
    }

    public long getCurrentFileCopiedSize() {
        return currentFileCopiedSize;
    }

    public void setCurrentFileCopiedSize(long currentFileCopiedSize) {
        this.currentFileCopiedSize = currentFileCopiedSize;
    }

    public long getCurrentFileTotalSize() {
        return currentFileTotalSize;
    }

    public void setCurrentFileTotalSize(long currentFileTotalSize) {
        this.currentFileTotalSize = currentFileTotalSize;
    }

    public long getAllFilesCopiedSize() {
        return allFilesCopiedSize;
    }

    public void setAllFilesCopiedSize(long allFilesCopiedSize) {
        this.allFilesCopiedSize = allFilesCopiedSize;
    }

    public long getAllFilesTotalSize() {
        return allFilesTotalSize;
    }

    public void setAllFilesTotalSize(long allFilesTotalSize) {
        this.allFilesTotalSize = allFilesTotalSize;
    }
}
