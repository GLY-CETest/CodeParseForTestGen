package cn.iselab.mutant.generating;

import com.alibaba.fastjson.annotation.JSONField;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class records message from mutation in json style mutation info,
 * convenient for parsing.
 *
 * @author Adian Qian
 */
final public class JsonMutationInfo {

    private static final long serialVersionUID = 3L;

    @JSONField(ordinal = 1, name = "nSuccessMutants")
    private int nSuccessMutants;

    @JSONField(ordinal = 2, name = "nAllMutants")
    private int nAllMutants;

    @JSONField(ordinal = 3, name = "nMutators")
    private int nMutators;

    @JSONField(ordinal = 4, name = "commonClasses")
    private Collection<String> commonClasses = new ArrayList<>();

    @JSONField(ordinal = 5, name = "exclusions")
    private Collection<String> exclusions = new ArrayList<>();

    @JSONField(ordinal = 6, name = "inclusions")
    private Collection<String> inclusions = new ArrayList<>();

    @JSONField(ordinal = 7, name = "mutators")
    private Collection<String> mutators = new ArrayList<>();

    @JSONField(ordinal = 8, name = "engineType")
    private String engineType;

    @JSONField(ordinal = 9, name = "mutaterType")
    private String mutaterType;


    @JSONField(ordinal = 10, name = "failures")
    private List<String> failures = new ArrayList<>();


    public void appendFailure(MutationDetails md) {
        appendFailure(String.format("%s-%s-%s",
                md.getFilename(),
                md.getMutator(),
                md.getLineNumber()));
    }

    public void appendFailure(String failure) {
        this.failures.add(failure);
    }

    // ---------- Getters and Setters ----------


    public Collection<String> getCommonClasses() {
        return commonClasses;
    }

    public void setCommonClasses(Collection<String> commonClasses) {
        this.commonClasses = commonClasses;
    }

    public Collection<String> getInclusions() {
        return inclusions;
    }

    public void setInclusions(Collection<String> inclusions) {
        this.inclusions = inclusions;
    }

    public void setExclusions(Collection<String> exclusions) {
        this.exclusions = exclusions;
    }

    public Collection<String> getExclusions() {
        return exclusions;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public void setEngineType(Class<?> engineType) {
        setEngineType(engineType.toString());
    }

    public String getMutaterType() {
        return mutaterType;
    }

    public void setMutaterType(String mutaterType) {
        this.mutaterType = mutaterType;
    }

    public void setMutaterType(Class<?> mutaterType) {
        setEngineType(mutaterType.toString());
    }

    public Collection<String> getMutators() {
        return mutators;
    }

    public void setMutators(Collection<String> mutators) {
        this.mutators = mutators;
        setnMutators(mutators.size());
    }

    public int getnMutators() {
        return nMutators;
    }

    public void setnMutators(int nMutators) {
        this.nMutators = nMutators;
    }

    public int getnAllMutants() {
        return nAllMutants;
    }

    public void setnAllMutants(int nAllMutants) {
        this.nAllMutants = nAllMutants;
    }

    public int getnSuccessMutants() {
        return nSuccessMutants;
    }

    public void setnSuccessMutants(int nSuccessMutants) {
        this.nSuccessMutants = nSuccessMutants;
    }

    public List<String> getFailures() {
        return failures;
    }

    public void setFailures(List<String> failures) {
        this.failures = failures;
    }
}
