package se.kth.jbroom.reporter;

import java.io.IOException;

import se.kth.jbroom.processor.JBroomProcessor;

/**
 * Utility class to retrieve coverage information.
 *
 * @author cesarsv
 */
public class CoverageReporter {

    /**
     * Path to the Junco coverage files
     */
    String coveragePath;

    /**
     * Path to the built classes
     */
    String builtClassesPath;

    JBroomProcessor p;

    public CoverageReporter(String coveragePath, String builtClassesPath) throws IOException {
        this.builtClassesPath = builtClassesPath;
        this.coveragePath = coveragePath;
        p = new JBroomProcessor(coveragePath, builtClassesPath);
        p.setCoverageInformation();
    }

    /**
     * Prints the classes covered by the test suite.
     *
     * @throws IOException
     */
    public void printClassCoverage() throws IOException {
        System.out.println();
        System.out.println("****************************");
        System.out.println("****** CLASS COVERAGE ******");
        System.out.println("****************************");
        System.out.println("Classes covered: " + p.getClassesCovered().size());
        System.out.println("Classes not covered: " + (p.getClassesNotCovered().size() - p.getClassesCovered().size()));
        System.out.println("Total classes: " + p.getClassesNotCovered().size());
        System.out.println("----------------------------");

        for (String c : p.getClassesCovered()) {
            System.out.println("Class covered: " + c);
        }

        p.getClassesNotCovered().removeAll(p.getClassesCovered());
        for (String c : p.getClassesNotCovered()) {
            System.out.println("Class not covered: " + c);
        }
    }

    /**
     * Prints the methods covered by the test suite.
     *
     * @throws IOException
     */
    public void printMethodCoverage() throws IOException {
        System.out.println();
        System.out.println("***************************");
        System.out.println("***** METHOD COVERAGE *****");
        System.out.println("***************************");
        System.out.println("Methods covered: " + p.getMethodsCovered().size());
        System.out.println("Methods not covered: " + (p.getMethodsNotCovered().size() - p.getMethodsCovered().size()));
        System.out.println("Total methods: " + p.getMethodsNotCovered().size());

        for (String m : p.getMethodsCovered()) {
            System.out.println("Methods covered: " + m);
        }

        p.getMethodsNotCovered().removeAll(p.getMethodsCovered());
        for (String m : p.getMethodsNotCovered()) {
            System.out.println("Methods not covered: " + m);
        }
    }

}
