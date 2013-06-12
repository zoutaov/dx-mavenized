package net.hanjava.dx;

import com.android.dx.command.DxConsole;

import java.io.*;

/**
 * @author Alan Goo
 */
public class Toy {
    /**
     * dump file name for methods.
     */
    public static String fileDumpMethods = null;
    /**
     * dump file name for fields.
     */
    public static String fileDumpFields = null;

    static {
        String envDumpMethods = System.getenv("DX_FILE_DUMP_METHODS");
        if (envDumpMethods != null && envDumpMethods.length() > 0) {
            fileDumpMethods = envDumpMethods;
        }
        String envDumpFields = System.getenv("DX_FILE_DUMP_FIELDS");
        if (envDumpFields != null && envDumpFields.length() > 0) {
            fileDumpFields = envDumpFields;
        }
    }

    private Toy() {
        // do not create an instance
    }

    public static PrintWriter createDumpWriter(String name) {
        String outName = null;
        if ("method_ids".equals(name)) {
            outName = fileDumpMethods;
        } else {
            outName = fileDumpFields;
        }
        if (outName == null) return null;
        try {
            File outFile = new File(outName);
            DxConsole.out.println("Writing " + outFile.getAbsolutePath());
            return new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
