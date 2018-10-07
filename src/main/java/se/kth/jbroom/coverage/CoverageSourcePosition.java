package se.kth.jbroom.coverage;

/**
 * A simple data structure to contain basic coverage information
 */
public class CoverageSourcePosition {

    private String className;

    private int line;

    public CoverageSourcePosition(String className, int line) {
        this.className = className;
        this.line = line;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public boolean equals(Object o) {
        CoverageSourcePosition vsp = (CoverageSourcePosition) o;
        return (vsp.className.equals(className) && vsp.line == line);
    }

    @Override
    public int hashCode() {
        return className.hashCode() * 1783 * line % 5009;
    }
}
