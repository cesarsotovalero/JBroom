package se.kth.jbroom.coverage;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.MethodInfo;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoverageReport implements ICoverageReport {

    protected CoverageBuilder coverageBuilder;
    private final File executionDataFile;
    private final File classesDirectory;
    protected String classToCover;

    private ExecutionDataStore executionDataStore;
    private SessionInfoStore sessionInfoStore;

    public CoverageReport(String classesDir, File jacocoFile, String classToCover) {
        this.executionDataFile = jacocoFile;
        this.classesDirectory = new File(classesDir);
        this.classToCover = classToCover;
    }

    @Override
    public void create() throws IOException {
        loadExecutionData();
        coverageBuilder = analyzeStructure();
    }

    private void loadExecutionData() throws IOException {
        final FileInputStream fis = new FileInputStream(executionDataFile);
        final ExecutionDataReader executionDataReader = new ExecutionDataReader(fis);
        executionDataStore = new ExecutionDataStore();
        sessionInfoStore = new SessionInfoStore();

        executionDataReader.setExecutionDataVisitor(executionDataStore);
        executionDataReader.setSessionInfoVisitor(sessionInfoStore);

        while (executionDataReader.read()) {
        }
        fis.close();
    }

    protected CoverageBuilder analyzeStructure() throws IOException {
        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);

        analyzer.analyzeAll(classesDirectory);

        return coverageBuilder;
    }

    public double codeFragmentCoverage(CodeFragment stmt) {
        return elementCoverage(stmt.getCtCodeFragment());
    }

    public double elementCoverage(CtElement elem) {
        if (elem instanceof CtIf) {
            CtIf ctIf = (CtIf) elem;
            if (ctIf.getElseStatement() != null) {
                return (coverage(ctIf.getThenStatement()) + coverage(ctIf.getElseStatement())) / 2d;
            } else {
                return coverage(ctIf.getThenStatement());
            }
        }

        if (elem instanceof CtLoop) {
            CtLoop loop = (CtLoop) elem;
            if (loop.getBody() != null) {
                return coverage(loop.getBody());
            } else {
                return coverage(loop);
            }
        }

        return coverage(elem);
    }

    protected double coverage(CtElement operator) {
        CtType<?> cl = operator.getParent(CtType.class);

        if (classToCover != null && !cl.getQualifiedName().equals(classToCover)) {
            return 0d;
        }

        IClassCoverage classCoverage = null;
        if (!(cl == null || cl.getPackage() == null)) {
            String name = cl.getQualifiedName().replace(".", "/");
            for (IClassCoverage cc : coverageBuilder.getClasses()) {
                if (name.equals(cc.getName())) {
                    classCoverage = cc;
                    break;
                }
            }
        }
        if (classCoverage == null) {
            return 0;
        }

        double ret = 0;
        int start = operator.getPosition().getLine();
        int end = operator.getPosition().getEndLine();
        for (int i = start; i <= end; i++) {
            if (classCoverage.getLine(i).getStatus() == ICounter.FULLY_COVERED) {
                ret++;
            }
        }
        return ret / (double) (end - start + 1);
    }

    @Override
    public int opCodeCoverage(CtMethod method, int indexOpcode) {
        IClassCoverage classCoverage = null;
        CtClass cl = method.getDeclaringClass();

        if (classToCover != null && !cl.getName().equals(classToCover)) {
            return 0;
        }

        String name = cl.getName().replace(".", "/");

        for (IClassCoverage cc : coverageBuilder.getClasses()) {
            if (name.equals(cc.getName())) {
                classCoverage = cc;
                break;
            }
        }
        if (classCoverage == null) {
            return 0;
        }

        MethodInfo mInfo = method.getMethodInfo();
        int line = mInfo.getLineNumber(indexOpcode);

        return classCoverage.getLine(line).getStatus();
    }

    public String getFileName() {
        return executionDataFile.getName();
    }

    /**
     * Returns a distribution of coverage from a given statement along the list
     * of coverage files
     *
     * @return An integer list containing the distribution
     */
    public List<Integer> getCoverageDistribution(CodeFragment stmt) {
        ArrayList<Integer> result = new ArrayList<>(1);
        result.add(codeFragmentCoverage(stmt) > 0 ? 1 : 0);
        return result;
    }

    @Override
    public double positionCoverage(SourcePosition position) {
        CtType<?> cl = position.getCompilationUnit().getMainType();

        if (classToCover != null && !cl.getQualifiedName().equals(classToCover)) {
            return 0d;
        }

        IClassCoverage classCoverage = null;
        if (!(cl == null || cl.getPackage() == null)) {
            String name = cl.getQualifiedName().replace(".", "/");
            for (IClassCoverage cc : coverageBuilder.getClasses()) {
                if (name.equals(cc.getName())) {
                    classCoverage = cc;
                    break;
                }
            }
        }
        if (classCoverage == null) {
            return 0;
        }

        double ret = 0;
        int start = position.getLine();
        int end = position.getEndLine();
        for (int i = start; i <= end; i++) {
            if (classCoverage.getLine(i).getStatus() == ICounter.FULLY_COVERED) {
                ret++;
            }
        }
        return ret / (double) (end - start + 1);
    }
}
