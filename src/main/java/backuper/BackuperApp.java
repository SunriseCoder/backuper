package backuper;

public class BackuperApp {

	public static void main(String[] args) {
		if (args.length < 2) {
			printUsage();
			System.exit(-1);
		}

		String srcPath = args[0];
		String dstPath = args[1];
		
		Backuper backuper = new Backuper();
		backuper.doBackup(srcPath, dstPath);
	}

	private static void printUsage() {
		System.out.println("Please use: " + BackuperApp.class.getName() + " <source path> <destination path>");
	}
}
