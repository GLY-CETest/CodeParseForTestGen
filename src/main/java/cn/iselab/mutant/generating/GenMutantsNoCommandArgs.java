package cn.iselab.mutant.generating;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.engine.gregor.config.GregorEngineFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Driver class for mutant creation.
 *
 * @author Adian Qian
 */
public class GenMutantsNoCommandArgs {

    // Log info and output at last.
    private static final JsonMutationInfo mutationInfo = new JsonMutationInfo();

    // ---------- Global Variants ----------

    // Loader for source classes to be mutated.
    private static SourceJarLoader sourceJarLoader;

    // Whether copy relevant classes.
    private static boolean copyInner;

    // Common classes copied for every mutants.
    private static List<String> commonClasses = new ArrayList<>();

    public static void main(String[] args) {
        List<String> sourceJarPaths = new ArrayList<>();
        sourceJarPaths.add("C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\Nextday-0.0.1-SNAPSHOT.jar");
        generateMutantsAndOutput(sourceJarPaths, "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants");

    }


    public static void generateMutantsAndOutput(List<String> sourceJarPaths, String outputDir){
        prepareOutputDirectory(new File(outputDir));

        try {
            // Load original classes to be mutated.
            sourceJarLoader = new SourceJarLoader(pathsToFileArray(sourceJarPaths));
            ClassloaderByteArraySource source = new ClassloaderByteArraySource(sourceJarLoader);

            List<String> mutators = null;
            // Initiate mutater.
            Mutater mutater = instantiateMutater(source, mutators);
//            System.out.println("mutators: " + cmdArgs.mutators.toString());

            // Compute classes to be mutated.
            List<String> inclusions = null;
            List<String> exclusions = null;
            List<String> targetClassFQNs = computeTargetClassFQNs(
                    sourceJarLoader.getLoadedClassFQNs(), inclusions, exclusions);

            // Create mutants and output.
            File outputDirFile = new File(outputDir);
            createMutantsAndOutput(targetClassFQNs, mutater, outputDirFile);

        } catch (IOException e) {
            System.out.println("Error when doing mutations!");
            e.printStackTrace();
            System.exit(3);
        }

        // Dump mutation info if successful.
        try {
            dumpInfo(new File(outputDir));
        } catch (IOException e) {
            System.out.println(
                    "Error when dump mutation info! Message: " + e.getMessage());
            System.exit(4);
        }
    }

    /**
     * Exclude classes specified in {@link CmdArgs#exclusions}.
     *
     * @param classFQNs     FQNs for all loaded classes.
     * @param inclusions    Regex for included classes.
     * @param exclusions    Regex for excluded classes.
     */
    private static List<String> computeTargetClassFQNs(
        List<String> classFQNs,
        List<String> inclusions,
        List<String> exclusions
    ) {
        if (inclusions != null)
            classFQNs = include(classFQNs, inclusions);
        if (exclusions != null)
            classFQNs = exclude(classFQNs, exclusions);
        return classFQNs;
    }

    private static List<String> include(List<String> classFQNs, List<String> inclusions) {
        Set<String> includedFQNs = findMatched(classFQNs, inclusions);
        // To list.
        return includedFQNs.stream().sorted().collect(Collectors.toList());
    }

    private static List<String> exclude(List<String> classFQNs, List<String> exclusions) {
        Set<String> excludedFQNs = findMatched(classFQNs, exclusions);
        // Filter out.
        return classFQNs.stream().filter((fqn) -> !excludedFQNs.contains(fqn)).collect(Collectors.toList());
    }

    private static Set<String> findMatched(List<String> classFQNs, List<String> classPrefixes) {
        Set<String> matchedFQNs = new HashSet<>();
        for (String prefix : classPrefixes) {
            List<String> tmp = classFQNs.stream()
                .filter((fqn) -> fqn.startsWith(prefix))
                .collect(Collectors.toList());
            matchedFQNs.addAll(tmp);
        }
        return matchedFQNs;
    }

    /**
     * File path in string to file array.
     */
    private static File[] pathsToFileArray(List<String> paths) {
        File[] files = new File[paths.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(paths.get(i));
        }
        return files;
    }

    /**
     * Delete old data, create empty directory for output.
     */
    private static void prepareOutputDirectory(File outputDir) {
        FileUtils.deleteRecursively(outputDir);
        FileUtils.createNewDirectories(outputDir);
    }

    /**
     * Instantiate a mutater to perform mutations.
     */
    private static Mutater instantiateMutater(
        ClassByteArraySource source, List<String> mutators) {
        // Empty arguments. Mean use default mutators, exclude no methods.
        EngineArguments engineArgs = EngineArguments.arguments();
        if (mutators != null)
            engineArgs = engineArgs.withMutators(mutators);
        GregorEngineFactory engineFactory = new GregorEngineFactory();
        MutationEngine engine = engineFactory.createEngine(engineArgs);

        // Build mutater.
        Mutater mutater = engine.createMutator(source);
        mutationInfo.setEngineType(engine.getClass());
        mutationInfo.setMutaterType(mutater.getClass());
        mutationInfo.setMutators(engine.getMutatorNames());

        return mutater;
    }

    /**
     * Create mutants with <code>mutater</code> and output
     * to <code>outputDir</code>
     */
    private static void createMutantsAndOutput(
        List<String> classFQNs, Mutater mutater, File outputDir) throws IOException {

        // Lookup target classes.
        List<ClassName> targetClassNames = toClassNames(classFQNs);

        // Generate all mutation details
        List<MutationDetails> allMutationDetails = detailMutations(mutater, targetClassNames);

        // Get mutation, pick out successful mutations.
        Map<MutantDetailsJson, Mutant> jsonToSuccessfulMutants = new HashMap<>();
        List<MutantDetailsJson> mutantJsons = new ArrayList<>();
        for (MutationDetails md : allMutationDetails) {
            Mutant pitMutant;
            try {
                // Check whether mutation succeeded.
                pitMutant = mutater.getMutation(md.getId());
            } catch (Exception e) {
                mutationInfo.appendFailure(md);
                continue;
            }
            // Put in if success.
            MutantDetailsJson mutantJson = new MutantDetailsJson(md);
            jsonToSuccessfulMutants.put(mutantJson, pitMutant);
            mutantJsons.add(mutantJson);
        }

        // Dump successful mutants in order.
        Collections.sort(mutantJsons);
        for (int i = 0; i < mutantJsons.size(); i++) {
            // Prepare output contents.
            int idx = i + 1;
            MutantDetailsJson mutantJson = mutantJsons.get(i);
            mutantJson.setId(idx);
            Mutant mutant = jsonToSuccessfulMutants.get(mutantJson);

            // Create separate dirs.
            File sepDir = new File(outputDir, String.valueOf(idx));
            FileUtils.createNewDirectories(sepDir);

            // Write mutant details.
            File detailsFile = new File(sepDir, "details.json");
            System.out.println("mutantJson: " + mutantJson.toString());
            FileUtils.writeContentIntoFile(detailsFile, JSON.toJSONString(mutantJson, true));

            //将变异体保存为.class
            // Write (1) mutant class, (2) specified common classes and
            // (3) other relevant classes to disk.
            writeClassesToDisk(mutantJson.getClassName(), sepDir, mutant);
        }

        // Add mutation info
        mutationInfo.setnAllMutants(allMutationDetails.size());
        mutationInfo.setnSuccessMutants(jsonToSuccessfulMutants.size());
    }

    /**
     * Write mutated class along with its other relevant classes.
     *
     * @param mutationTargetClass   the fully qualified name of the mutated source class
     * @param mutantDir             where to output mutated classes.
     * @param mutant                pit mutant class body
     */
    private static void writeClassesToDisk(String mutationTargetClass, File mutantDir, Mutant mutant) throws IOException {
        System.out.println("For mutant: " + mutationTargetClass);

        // Write mutant class to disk.
        dumpClassBytes(mutationTargetClass, mutantDir, mutant.getBytes());

        // Collect other classes to output.
        Set<String> otherClasses = new HashSet<>(commonClasses);

        // Collect relevant inner  classes.
        if (copyInner) {
            // Find inner classes to the mutation target class.
            otherClasses.addAll(collectRelevantInnerClasses(mutationTargetClass));

            // Find inner classes to common classes.
            for (String commonClass : commonClasses) {
                otherClasses.addAll(collectRelevantInnerClasses(commonClass));
            }
        }

        // Exclude mutation target in order to avoid unexpected overwrite.
        otherClasses.remove(mutationTargetClass);

        // Dump other relevant classes.
        for (String otherClass : otherClasses) {
            InputStream resource = sourceJarLoader.getResourceInputStreamByFQN(otherClass);
//            dumpClassBytes(otherClass, mutantDir, resource.readAllBytes());
        }

        // Log separator.
        System.out.println("==========================================================");
    }

    /**
     * Collect relevant classes due to inner-class relations.
     */
    private static Collection<String> collectRelevantInnerClasses(String className) {
        Set<String> relevantInnerClasses = new HashSet<>();
        if (sourceJarLoader.isInnerClass(className)) {
            // For inner class, relevant classes are its (1) declaring class
            // and (2) other inner classes of that declaring class.

            // Collect declaring class.
            String declaringClass = sourceJarLoader.getDeclaringClass(className);
            relevantInnerClasses.add(declaringClass);

            // Get inner classes of the declaring class.
            relevantInnerClasses.addAll(sourceJarLoader.getInnerClasses(declaringClass));

        } else if (sourceJarLoader.hasInnerClasses(className)){
            // For normal class, relevant classes are its inner classes.
            relevantInnerClasses.addAll(sourceJarLoader.getInnerClasses(className));
        }
        return relevantInnerClasses;
    }

    /**
     * Write bytes to disk as a class file.
     *
     * @param className     fully qualified name of the dumped class
     * @param mutantDir     directory to output mutant class and its relevant classes
     * @param classBytes    bytes of the dumped class.
     */
    private static void dumpClassBytes(String className, File mutantDir, byte[] classBytes) {
        String classChildPath = className.replace(".", File.separator) + FileUtils.CLASS_SUFFIX;
        File classFile = new File(mutantDir, classChildPath);
        FileUtils.createNewDirectories(classFile.getParentFile());
        FileUtils.writeBytesIntoClassFile(classBytes, classFile);

        System.out.println("Write class to: " + classFile.getAbsolutePath());
    }

    /**
     * Transform string FQNs to {@link ClassName} FQNs.
     */
    private static List<ClassName> toClassNames(List<String> classFQNs) {
        return classFQNs.stream()
                        .map(ClassName::fromString)
                        .collect(Collectors.toList());
    }

    /**
     * Find mutation points for each target class and them pack up and return
     *
     * @return Possible mutation points.
     */
    private static List<MutationDetails> detailMutations(
        Mutater mutater, List<ClassName> targetClasses
    ) {
        return targetClasses.stream()
                            .flatMap((c) -> mutater.findMutations(c).stream())
                            .collect(Collectors.toList());
    }


    // ---------- Operating Mutation Information ----------

    private static void dumpInfo(File outputDir) throws IOException {
        FileUtils.writeContentIntoFile(
            new File(outputDir, "mutationInfo" + FileUtils.JSON_SUFFIX),
            JSON.toJSONString(mutationInfo, true));
    }

    /**
     * Arguments for mutant generation command line interface.
     */
    private static class CmdArgs {

        // ---------- Required Arguments ----------

        @Parameter(
            order = 1,
            required = true,
            variableArity = true,
            names = {"--source-jar", "-s"},
            description = "Jars storing .class files to be mutated.")
        List<String> sourceJarPaths;

        @Parameter(
            order = 2,
            required = true,
            names = {"--output-dir", "-o"},
            converter = FileConverter.class,
            description = "Directory to which mutated classes are output.")
        File outputDir;

        // ---------- Optional Arguments ----------

        @Parameter(
            order = 3,
            variableArity = true,
            names = {"--exclusions", "-e"},
            description = "Classes (in fqn) excluded from this mutation.")
        List<String> exclusions;

        @Parameter(
            order = 4,
            variableArity = true,
            names = {"--includes", "-i"},
            description = "Classes (in fqn) included in this mutation.")
        List<String> inclusions;

        @Parameter(
            order = 5,
            variableArity = true,
            names = {"--mutators", "-m"},
            description = "Mutators used for mutant creation. " +
                "See https://pitest.org/quickstart/mutators/ or " +
                "org.pitest.mutationtest.engine.gregor.config.Mutator.")
        List<String> mutators;

        @Parameter(
            order = 6,
            variableArity = true,
            names = {"--common-classes", "-c"},
            description = "Original classes copied for every mutants, specified in fqn.")
        List<String> commonClasses = new ArrayList<>();

        @Parameter(
            order = 7,
            names = {"--copy-inner-classes"},
            description = "Output mutant class along and copied inner classes relevant to its execution." +
                "For example, inner class A$1 for its declaring class A")
        boolean copyInnerClasses = true;


        @Parameter(order = 8, names = {"--help", "-h"}, description = "Show help message.", help = true)
        boolean help = false;

        @Override
        public String toString() {
            return "CmdArgs{" +
                "sourceClassDir=" + sourceJarPaths +
                ", outputDir=" + outputDir +
                ", mutators=" + mutators +
                ", help=" + help +
                '}';
        }
    }



}
