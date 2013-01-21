/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.dx.command;

import com.android.dx.Version;

/**
 * Main class for dx. It recognizes enough options to be able to dispatch
 * to the right "actual" main.
 */
public class Main {
    /** dump file name for methods. */
    public static String fileDumpMethods = null;
    /** dump file name for fields. */
    public static String fileDumpFields = null;

    static {
        String envDumpMethods = System.getenv("DX_FILE_DUMP_METHODS");
        if(envDumpMethods!=null && envDumpMethods.length()>0) {
            Main.fileDumpMethods = envDumpMethods;
        }
        String envDumpFields = System.getenv("DX_FILE_DUMP_FIELDS");
        if(envDumpFields!=null && envDumpFields.length()>0) {
            Main.fileDumpFields = envDumpFields;
        }
    }

    private static String USAGE_MESSAGE =
        "usage:\n" +
        "  dx --dex [--debug] [--verbose] [--positions=<style>] " +
        "[--no-locals]\n" +
        "  [--no-optimize] [--statistics] [--[no-]optimize-list=<file>] " +
        "[--no-strict]\n" +
        "  [--keep-classes] [--output=<file>] [--dump-to=<file>] " +
        "[--dump-width=<n>]\n" +
        "  [--dump-method=<name>[*]] [--verbose-dump] [--no-files] " +
        "[--core-library]\n" +
        "  [--num-threads=<n>] [--incremental] [--force-jumbo]\n" +
        "  [<file>.class | <file>.{zip,jar,apk} | <directory>] ...\n" +
        "    Convert a set of classfiles into a dex file, optionally " +
        "embedded in a\n" +
        "    jar/zip. Output name must end with one of: .dex .jar " +
        ".zip .apk. Positions\n" +
        "    options: none, important, lines.\n" +
        "  dx --annotool --annotation=<class> [--element=<element types>]\n" +
        "  [--print=<print types>]\n" +
        "  dx --dump [--debug] [--strict] [--bytes] [--optimize]\n" +
        "  [--basic-blocks | --rop-blocks | --ssa-blocks | --dot] " +
        "[--ssa-step=<step>]\n" +
        "  [--width=<n>] [<file>.class | <file>.txt] ...\n" +
        "    Dump classfiles, or transformations thereof, in a " +
        "human-oriented format.\n" +
        "  dx --find-usages <file.dex> <declaring type> <member>\n" +
        "    Find references and declarations to a field or method.\n" +
        "    declaring type: a class name in internal form, like " +
        "Ljava/lang/Object;\n" +
        "    member: a field or method name, like hashCode\n" +
        "  dx -J<option> ... <arguments, in one of the above " +
        "forms>\n" +
        "    Pass VM-specific options to the virtual machine that " +
        "runs dx.\n" +
        "  dx --version\n" +
        "    Print the version of this tool (" + Version.VERSION +
        ").\n" +
        "  dx --help\n" +
        "    Print this message.";

    /**
     * This class is uninstantiable.
     */
    private Main() {
        // This space intentionally left blank.
    }

    /**
     * Run!
     */
    public static void main(String[] args) {
        boolean gotCmd = false;
        boolean showUsage = false;

        try {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equals("--") || !arg.startsWith("--")) {
                    gotCmd = false;
                    showUsage = true;
                    break;
                }

                gotCmd = true;
                if (arg.equals("--dex")) {
                    com.android.dx.command.dexer.Main.main(without(args, i));
                    break;
                } else if (arg.equals("--dump")) {
                    com.android.dx.command.dump.Main.main(without(args, i));
                    break;
                } else if (arg.equals("--annotool")) {
                    com.android.dx.command.annotool.Main.main(
                            without(args, i));
                    break;
                } else if (arg.equals("--find-usages")) {
                    com.android.dx.command.findusages.Main.main(without(args, i));
                    break;
                } else if (arg.equals("--version")) {
                    version();
                    break;
                } else if (arg.equals("--help")) {
                    showUsage = true;
                    break;
                } else {
                    gotCmd = false;
                }
            }
        } catch (UsageException ex) {
            showUsage = true;
        } catch (RuntimeException ex) {
            System.err.println("\nUNEXPECTED TOP-LEVEL EXCEPTION:");
            ex.printStackTrace();
            System.exit(2);
        } catch (Throwable ex) {
            System.err.println("\nUNEXPECTED TOP-LEVEL ERROR:");
            ex.printStackTrace();
            if ((ex instanceof NoClassDefFoundError)
                    || (ex instanceof NoSuchMethodError)) {
                System.err.println(
                        "Note: You may be using an incompatible " +
                        "virtual machine or class library.\n" +
                        "(This program is known to be incompatible " +
                        "with recent releases of GCJ.)");
            }
            System.exit(3);
        }

        if (!gotCmd) {
            System.err.println("error: no command specified");
            showUsage = true;
        }

        if (showUsage) {
            usage();
            System.exit(1);
        }
    }

    /**
     * Prints the version message.
     */
    private static void version() {
        System.err.println("dx version " + Version.VERSION);
        System.exit(0);
    }

    /**
     * Prints the usage message.
     */
    private static void usage() {
        System.err.println(USAGE_MESSAGE);
    }

    /**
     * Returns a copy of the given args array, but without the indicated
     * element.
     *
     * @param orig {@code non-null;} original array
     * @param n which element to omit
     * @return {@code non-null;} new array
     */
    private static String[] without(String[] orig, int n) {
        int len = orig.length - 1;
        String[] newa = new String[len];
        System.arraycopy(orig, 0, newa, 0, n);
        System.arraycopy(orig, n + 1, newa, n, len - n);
        return newa;
    }
}
