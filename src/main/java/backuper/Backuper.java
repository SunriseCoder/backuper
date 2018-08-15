package backuper;

import java.io.File;
import java.util.Map;

public class Backuper {
	private FolderScanner folderScanner;

	public Backuper() {
		folderScanner = new FolderScanner();
	}

	public void doBackup(String srcPath, String dstPath) {
		System.out.println("Scanning source folder...");
		Map<String, File> srcFiles = folderScanner.scan(srcPath);

		System.out.println("Scanning destination folder...");
		Map<String, File> dstFiles = folderScanner.scan(dstPath);

		//TODO
	}
}
