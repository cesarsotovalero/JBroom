package se.kth.jbroom.processor;

import org.jacoco.core.analysis.*;
import org.jacoco.core.tools.ExecFileLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * This class performs the test coverage analysis. It uses Junco to dump the
 * current coverage information and JaCoCo to calculate the class coverage
 * information for each test case.
 *
 * @author cesarsv
 */
public class JBroomProcessor {

    /**
     * Path to the Junco coverage
     */
    private final String coveragePath;

    /**
     * Path to the built classesNotCovered
     */
    private final String builtClassesPath;

    /**
     * Classes covered by the tests suite
     */
    private TreeSet<String> classesCovered;

    /**
     * Classes analyzed
     */
    private TreeSet<String> classesNotCovered;

    /**
     * Methods covered by the tests suite
     */
    private TreeSet<String> methodsCovered;

    /**
     * Methods analyzed
     */
    private TreeSet<String> methodsNotCovered;

    /**
     * Branches covered by the tests suite
     */
    private int branchesCovered;

    /**
     * Branches not covered by the tests suite
     */
    private int branchesNotCovered;

    /**
     * Branches covered by the tests suite
     */
    private int linesCovered;

    /**
     * Branches not covered by the tests suite
     */
    private int linesNotCovered;

    /**
     * Creates the test dependency extractor
     *
     * @param coveragePath Path to the Junco coverage
     * @param builtClassesPath Path to the built classesNotCovered
     */
    public JBroomProcessor(String coveragePath, String builtClassesPath) {
        this.coveragePath = coveragePath;
        this.builtClassesPath = builtClassesPath;

    }

    /**
     * Processes the coverage information.
     *
     * @return
     *
     * @throws java.io.IOException
     */
    protected HashMap<String, Collection<String>> obtainCoveredMethods() throws IOException {

        // the HashMap to return
        HashMap<String, Collection<String>> result = new HashMap<>();

        // check the coverage files
        File fcoverage = new File(coveragePath);
        if (!fcoverage.exists()) {
            throw new FileNotFoundException(fcoverage.getAbsolutePath());
        }

        // foreach file in the coverage files
        for (File f : fcoverage.listFiles()) {

            // obtain the coverage bundle
            if (f.isDirectory() || !f.getName().endsWith(".exec")) {
                continue;
            }

            ExecFileLoader loader = new ExecFileLoader();

            // load the .exec file and create the analyzer
            loader.load(f);
            final CoverageBuilder coverageBuilder = new CoverageBuilder();
            final Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), coverageBuilder);
            analyzer.analyzeAll(new File(builtClassesPath));

            // iterate over the classes in the coverage builder
            for (IClassCoverage c : coverageBuilder.getClasses()) {
//              System.out.println("Analyzing class: " + c.getName());
                Collection<IMethodCoverage> methodsI = c.getMethods();
                Collection<String> coveredMethodsName = new ArrayList<>();
                // iterate over the methods in the classes
                for (IMethodCoverage method : methodsI) {
//                  System.out.println("Analyzing method: " + method.getName());
                    switch (method.getMethodCounter().getStatus()) {
                        case 0:
//                          System.out.println("  ===> EMPTY");
                            break;
                        case 1:
//                          System.out.println("  ===> NOT_COVERED");
                            break;
                        case 3:
//                          System.out.println("  ===> PARTLY_COVERED");
                            coveredMethodsName.add(method.getName());
                            break;
                        case 2:
//                          System.out.println("  ===> FULLY_COVERED");
                            coveredMethodsName.add(method.getName());
                            break;
                    }
                }
                if (coveredMethodsName.size() > 0) {
                    String className = c.getName().split("/")[c.getName().split("/").length - 1];
                    result.put(className, coveredMethodsName);
                }
            }
        }
        return result;
    }

    /**
     * Useful methodsNotCovered to obtain coverage information.
     *
     * @throws IOException
     */
    @SuppressWarnings("empty-statement")
    public void setCoverageInformation() throws IOException {
        classesCovered = new TreeSet<>();
        classesNotCovered = new TreeSet<>();
        methodsCovered = new TreeSet<>();
        methodsNotCovered = new TreeSet<>();
        branchesCovered = 0;
        branchesNotCovered = 0;
        linesCovered = 0;
        linesNotCovered = 0;

        File fcoverage = new File(coveragePath);
        if (!fcoverage.exists()) {
            throw new FileNotFoundException(fcoverage.getAbsolutePath());
        }

        for (File file : fcoverage.listFiles()) {
            // Get the coverage bundle
            if (file.isDirectory() || !file.getName().endsWith(".exec")) {
                continue;
            }
            ExecFileLoader loader = new ExecFileLoader();
            loader.load(file);
            final CoverageBuilder coverageBuilder = new CoverageBuilder();
            final Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), coverageBuilder);
            analyzer.analyzeAll(new File(builtClassesPath));

            for (IClassCoverage c : coverageBuilder.getClasses()) {
                // the class is not covered
                if (c.getClassCounter().getStatus() == ICounter.NOT_COVERED) {
                    classesNotCovered.add(c.getName());
                    for (IMethodCoverage m : c.getMethods()) {
                        if (m.getName().endsWith("init>") || m.getName().equals("main")) {
                            continue;
                        }
                        if (m.getMethodCounter().getStatus() == ICounter.NOT_COVERED) {
                            methodsNotCovered.add(c.getName() + "." + m.getName());
                        } else { // should not be reached                             
                            methodsCovered.add(c.getName() + "." + m.getName());
                        }
                    }
                } else {//the class is covered

                    classesCovered.add(c.getName());

                    if (!classesNotCovered.contains(c.getName())
                            && !classesCovered.contains(c.getName())) {
                        // count branches
                        branchesCovered += c.getBranchCounter().getCoveredCount();
                        branchesNotCovered += c.getBranchCounter().getMissedCount();
                        // count lines
                        linesCovered += c.getLineCounter().getCoveredCount();
                        linesNotCovered += c.getLineCounter().getMissedCount();
                    }

                    for (IMethodCoverage m : c.getMethods()) {
                        if (m.getName().endsWith("init>") || m.getName().equals("main")) {
                            continue;
                        }
                        if (m.getMethodCounter().getStatus() == ICounter.NOT_COVERED) {
                            methodsNotCovered.add(c.getName() + "." + m.getName());
                        } else {// the method is covered                          
                            methodsCovered.add(c.getName().replaceAll("/", ".") + "." + m.getName());
                        }
                    }
                }
            }
        }
    }

    public TreeSet<String> getClassesCovered() {
        return classesCovered;
    }

    public TreeSet<String> getClassesNotCovered() {
        return classesNotCovered;
    }

    public TreeSet<String> getMethodsCovered() {
        return methodsCovered;
    }

    public TreeSet<String> getMethodsNotCovered() {
        return methodsNotCovered;
    }

}
