package se.kth.jbroom.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class compiles java source files using <b>javac</b> .
 *
 * @author cesarsv
 */
public class Compiler {

    /**
     * Compiles all the .java files in the given directory.
     *
     * @param javaFile a java file
     */
    public static void compileDir(String javaFile) {
        System.out.println("recompiling file: " + javaFile);

        String log = "";
        try {
            String s = null;
            Process p1 = Runtime.getRuntime().exec("javac" + " " + javaFile);
            Process p2 = Runtime.getRuntime().exec("javac" + " " + javaFile);

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
            boolean error = false;

            log += "\n....\n";
            while ((s = stdError.readLine()) != null) {
                log += s;
                error = true;
                log += "\n";
            }
            if (error == false) {
                log += "Compilation successful !!!";
            }
        } catch (IOException e) {
            System.out.println(log);
        }
    }
}
