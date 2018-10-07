package se.kth.jbroom.runner;

import se.kth.jbroom.reporter.CoverageReporter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import se.kth.jbroom.processor.JBroomProcessor;
import se.kth.jbroom.util.Files;
import spoon.Launcher;

public class JBroomRunListener extends RunListener {

    /**
     * Path to the Java source code.
     */
    final String inputDir = "src/main";

    /**
     * Path to the compiled Java classes.
     */
    final String outputDir = "target/classes";

    /**
     * Path to the Junco coverage reporter.
     */
    final String coveragePath = "target/site/junco";

    /**
     * Path to the built Java classes.
     */
    final String builtClassesPath = "target/classes";

    /**
     * Constructor.
     */
    public JBroomRunListener() {
        System.out.println("\nCreating the JBroom RunListener...");
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        printCoverageReport();
        processMethod();
        recompileAll();
        System.out.println("\nJBroom finished...");
    }

    /**
     * Removes the methods that are not covered by the test suite.
     * Uses Spoon to perform the code transformation.
     */
    private void processMethod() {
        final String[] param = {
                "-i", inputDir,
                "-o", outputDir,
                "-p", "se.kth.jbroom.processor.MethodProcessor",
                "-c"
        };
        final Launcher launcher = new Launcher();
        launcher.setArgs(param);
        launcher.run();
    }

    /**
     * Prints general metrics about coverage.
     *
     * @throws IOException
     */
    private void printCoverageReport() throws IOException {
        // print class coverage
        CoverageReporter cr = new CoverageReporter(coveragePath, builtClassesPath);
        cr.printClassCoverage();
        cr.printMethodCoverage();
    }

    /**
     * Recompile the program and remove the classes that are not covered by
     * the test suite.
     *
     * @throws IOException
     */
    private void recompileAll() throws IOException {
        JBroomProcessor p = new JBroomProcessor(coveragePath, builtClassesPath);
        p.setCoverageInformation();
        // classes covered
        TreeSet<String> coveredClasses = p.getClassesCovered();
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

    /**
     * Returns an adequate {@link String} representing the path to the file.
     *
     * @param file
     * @return
     */
    private String obtainFilePath(File file) {
        StringBuilder filePath = new StringBuilder(file.getPath());
        if(file.getPath().endsWith(".java")){
            return filePath.substring(15, filePath.length() - 5);
        }
        else if (file.getPath().endsWith(".class")) {
            return filePath.substring(15, filePath.length() - 6);
        }
        return file.getPath();
    }

}
