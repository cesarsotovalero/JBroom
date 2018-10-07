package se.kth.jbroom.processor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

/**
 * This class removes the methods that are not covered by the test suite in a
 * given class. It uses Spoon to perform the code transformation and Junco to
 * retrieve the coverage results
 *
 * @author cesarsv
 */
public class MethodProcessor extends AbstractProcessor<CtClass> {

    /**
     * Path to the Junco coverage path.
     */
    String coveragePath = "target/site/junco";

    /**
     * Path to the built classes
     */
    String builtClassesPath = "target/classes";

    HashMap<String, Collection<String>> coveredMethods;

    JBroomProcessor p = new JBroomProcessor(coveragePath, builtClassesPath);

    /**
     * Constructor that gets the coverage information of the test suite for the
     * bundle.
     *
     * @throws IOException
     */
    public MethodProcessor() throws IOException {
        coveredMethods = p.obtainCoveredMethods();
    }

    @Override
    public void process(CtClass ctClass) {
        if (coveredMethods.get(ctClass.getSimpleName()) != null) {
            // get all the methods in the class
            Set<CtMethod> methods = ctClass.getMethods();
            // for each method in the class
            for (CtMethod method : methods) {
                // check if the method is not in the list of covered methods
                if (!coveredMethods.get(ctClass.getSimpleName()).contains(method.getSimpleName())) {
                    // remove the method from the class
                    ctClass.removeMethod(method);
                    // add a comment at the top of the class with the name of the removed method/s
                    ctClass.addComment(getFactory().Core().createComment().setContent("method \"" + method.getSimpleName() + "\" was removed from this class because it is not covered by the test suite"));
                }
            }
        }
    }
}
