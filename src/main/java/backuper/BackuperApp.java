package backuper;

import java.io.IOException;

import backuper.helpers.PrintHelper;

public class BackuperApp {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
            System.exit(-1);
        }

        String srcPath = args[0];
        String dstPath = args[1];

        Backuper backuper = new Backuper();
        backuper.setSrcPath(srcPath);
        backuper.setDstPath(dstPath);

        Options options = new Options();
        backuper.doBackup(options );
    }

    private static void printUsage() {
        PrintHelper.println("Please use: " + BackuperApp.class.getName() + " <source path> <destination path>");
    }
}
