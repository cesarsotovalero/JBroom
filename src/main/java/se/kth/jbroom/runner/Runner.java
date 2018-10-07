package se.kth.jbroom.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import se.kth.jbroom.processor.JBroomProcessor;
import se.kth.jbroom.reporter.CoverageReporter;
import se.kth.jbroom.util.Files;
import spoon.Launcher;

/**
 * This class executes the transformation
 *
 * @author cesarsv
 */
public class Runner {

    /**
     * Returns an adequate {@link String} representing the path to the file.
     *
     * @param file
     * @return
     */
    private static String obtainFilePath(File file) {
        StringBuilder filePath = new StringBuilder(file.getPath());
        if(file.getPath().endsWith(".java")){
            return filePath.substring(15, filePath.length() - 5);
        }
        else if (file.getPath().endsWith(".class")) {
            return filePath.substring(15, filePath.length() - 6);
        }
        return file.getPath();
    }


    public static void main(String[] args) throws IOException {

        /**
         * Path to the Junco coverage reporter.
         */
        final String coveragePath = "target/site/junco";

        /**
         * Path to the built classes.
         */
        final String builtClassesPath = "target/classes";

        JBroomProcessor jbp = new JBroomProcessor(coveragePath, builtClassesPath);
        jbp.setCoverageInformation();

        String inputDir = "src/main";
        String outputDir = "target/classes";

        // create the transformed java files
        final String[] param = {
            "-i", inputDir,
            "-o", outputDir,
            "-p", "se.kth.jbroom.processor.MethodProcessor",
            "-c"
        };

        final Launcher launcher = new Launcher();
        launcher.setArgs(param);
        launcher.run();

//        Print coverage report
        // print class coverage
        CoverageReporter cr = new CoverageReporter(coveragePath, builtClassesPath);
        cr.printClassCoverage();
        cr.printMethodCoverage();

        // classes covered
        TreeSet<String> coveredClasses = jbp.getClassesCovered();
        // compile the transformed java files
        List<File> files = Files.getFilesRecursive(outputDir);

        for (File file : files) {

            if (!coveredClasses.contains(obtainFilePath(file))) {
                // remove classes not covered
                file.delete();
                continue;
            }

//            if (Files.getExtension(file).equals("java")) {
//                // compile the transformed java classes
//                se.kth.jbroom.util.Compiler.compileDir(file.getAbsolutePath());
//                // remove the source files
//                System.out.println("Removing file: " + file.getAbsolutePath());
//                file.delete();
//            }
        }

        System.out.println("\nRecompilation successful, classes not covered were successfully removed!!!");
    }



}
