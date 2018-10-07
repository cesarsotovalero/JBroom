package se.kth.jbroom.coverage;


import javassist.CtMethod;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import java.io.IOException;
import java.util.List;

public interface ICoverageReport {

    void create() throws IOException;

    double codeFragmentCoverage(CodeFragment stmt);

    int opCodeCoverage(CtMethod method, int indexOpcode);

    double elementCoverage(CtElement operator);

    /**
     * This method returns for a given code fragment, its distribution along
     * several coverage data files. Each client coverage data is represented by
     * a jacoco file, to whom an index has been assigned.
     *
     * @param stmt CodeFragment for which we want to know distribution
     * @return A list of integers containing the index of the files in which
     * this statement was covered.
     */
    List<Integer> getCoverageDistribution(CodeFragment stmt);

    double positionCoverage(SourcePosition position);
}
