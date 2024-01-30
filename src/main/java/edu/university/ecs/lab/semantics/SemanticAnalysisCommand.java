package edu.university.ecs.lab.semantics;

import edu.university.ecs.lab.semantics.util.MsCache;
import edu.university.ecs.lab.semantics.util.ProcessFiles;
// import edu.university.ecs.lab.semantics.util.entityextraction.EntityContextAdapter;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.strategies.EntityLiteralSimilarityCheckStrategy;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.strategies.EntitySematicSimilarityCheckStrategy;
import edu.university.ecs.lab.semantics.util.factory.*;
import edu.university.ecs.lab.semantics.util.file.CacheManager;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.io.File;

// Program Entry


@QuarkusMain
public class SemanticAnalysisCommand implements QuarkusApplication {

    public static String[] repoUrls;
    public static String cachePath;
    private static String sutPath;
    private static final String REPO_DESTINATION_DIRECTORY = "../repos";

    @Override
    public int run(String... args) throws Exception {
        long start = System.currentTimeMillis();
        initCache();
        initPaths(args);
        // Start here
        cloneRemotes(repoUrls);
        preProcess();
        // processCodeClonesFromCache();
        // conductCalculation();
        persistCache();
        System.out.println(System.currentTimeMillis() - start);
        return 0;
    }

    private void persistCache() {
        CacheManager cacheManager = new CacheManager();
        cacheManager.persistCache(cachePath);
    }

    // private void conductCalculation() {
    //     ModuleCloneFactory moduleCloneFactory = new ModuleCloneFactory();
    //     moduleCloneFactory.createData();
    // }

    private void initPaths(String... args) {
        repoUrls = args[0].split(",");
        cachePath = args[1];
    }

    public void initCache(){
        MsCache.init();
    }

    public void preProcess() {
        ProcessFiles.run(sutPath);
        FlowBuilder flowBuilder = new FlowBuilder();
        flowBuilder.buildFlows();
        
        // Entity Construction
        // MsCache.mappedEntities = EntityContextAdapter.getMappedEntityContext(sutPath);
    }

    private void cloneRemotes(String[] urls) throws Exception {
        File destinationDir = new File(REPO_DESTINATION_DIRECTORY);

        if (!destinationDir.exists()) {
            boolean success = destinationDir.mkdirs();
            if (!success) {
                System.err.println("Failed to create destination directory");
                System.exit(1);
            }
        }

        for (String url : urls) {
            String output = REPO_DESTINATION_DIRECTORY + File.separator + getRepositoryName(url);
            ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", url, output);

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode < 300) {
                System.out.println("Git clone successful for repo: " + output);
            } else {
                throw new Exception("Git clone failed with status code: " + exitCode);
            }
        }

        sutPath = REPO_DESTINATION_DIRECTORY;
    }

//     public void processCodeClonesFromCache() {
// //        CacheManager cacheManager = new CacheManager();
// //        cacheManager.recreateCache(cachePath);
// //        CodeClonesFactory codeClonesFactory = new CodeClonesFactory(new EntityLiteralSimilarityCheckStrategy());
//     	CodeClonesFactory codeClonesFactory = new CodeClonesFactory(new EntitySematicSimilarityCheckStrategy(true));
//         codeClonesFactory.findCodeClones();
//         ModuleClonePairFactory mcpf = new ModuleClonePairFactory();
//         mcpf.printModuleClonePairs();
//     }

    public static void main(String[] args) {
        Quarkus.run(SemanticAnalysisCommand.class, args);
    }
}
