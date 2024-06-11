package cn.iselab.mutant.generating;

import com.alibaba.fastjson.annotation.JSONField;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationDetails;

import java.io.Serializable;


/**
 * A Json wrapper for {@link MutationDetails}
 *
 * @author Adian Qian
 */

final public class MutantDetailsJson
    implements Serializable, Comparable<MutantDetailsJson> {

    private static final long serialVersionUID = 2L;

    /**
     * Unique index.
     */
    @JSONField(name = "id")
    private int id = 0;

    /**
     * Name of the mutated class.
     */
    @JSONField(name = "className")
    private String className;

    /**
     * Method signature = Method name + method descriptor
     */
    @JSONField(name = "methodSignature")
    private String methodSignature;

    @JSONField(name = "mutator")
    private String mutator;

    @JSONField(name = "sourceFile")
    private String sourceFile;

    /**
     * @see MutationDetails#getLineNumber()
     * the line number on which the mutation occurs as reported
     * within the jvm bytecode
     */
    @JSONField(name = "lineNumber")
    private int lineNumber;

    public MutantDetailsJson(int id, MutationDetails details) {
        this.id = id;
        wrap(details);
    }

    public MutantDetailsJson(MutationDetails details) {
        wrap(details);
    }

    // Just for test
    MutantDetailsJson(int id, String className, String methodSignature, String mutator, String description) {
        this.id = id;
        this.className = className;
        this.methodSignature = methodSignature;
        this.mutator = mutator;
    }

    /**
     * Detailed logic for parsing information from {@link MutationDetails}
     */
    private void wrap(MutationDetails details) {
        // Get className, method signature from details.MutationIdentifier.Location.
        Location location = details.getId().getLocation();
        this.className = location.getClassName().asJavaName();
        this.methodSignature = location.getMethodName() + location.getMethodDesc();

        // Get mutator, description, source file name, line number from MutationDetails.
        this.mutator = details.getMutator();
        this.sourceFile = details.getFilename();
        this.lineNumber = details.getLineNumber();
    }

    @Override
    public int compareTo(MutantDetailsJson that) {
        if (this.mutator.compareTo(that.mutator) != 0)
            return this.mutator.compareTo(that.mutator);

        if (this.className.compareTo(that.className) != 0)
            return this.className.compareTo(that.className);

        if (this.methodSignature.compareTo(that.methodSignature) != 0)
            return this.methodSignature.compareTo(that.methodSignature);

        return this.lineNumber - that.lineNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public String getMutator() {
        return mutator;
    }

    public void setMutator(String mutator) {
        this.mutator = mutator;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "DetailsJsonWrapper{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", methodSignature='" + methodSignature + '\'' +
                ", mutator='" + mutator + '\'' +
                ", sourceFile='" + sourceFile + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
