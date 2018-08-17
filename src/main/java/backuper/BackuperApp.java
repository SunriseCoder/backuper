package backuper;

import java.io.IOException;

public class BackuperApp {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
            System.exit(-1);
        }

        String srcPath = args[0];
        String dstPath = args[1];

        Backuper backuper = new Backuper();
        Options options = new Options();
        backuper.doBackup(srcPath, dstPath, options );
    }

    private static void printUsage() {
        System.out.println("Please use: " + BackuperApp.class.getName() + " <source path> <destination path>");
    }
}
