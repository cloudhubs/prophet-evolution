package edu.university.ecs.lab.semantics;

import edu.university.ecs.lab.semantics.util.MsCache;
import edu.university.ecs.lab.semantics.util.ProcessFiles;
import edu.university.ecs.lab.semantics.util.entityextraction.EntityContextAdapter;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.strategies.EntityLiteralSimilarityCheckStrategy;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.strategies.EntitySematicSimilarityCheckStrategy;
import edu.university.ecs.lab.semantics.util.factory.*;
import edu.university.ecs.lab.semantics.util.file.CacheManager;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

// Program Entry


@QuarkusMain
public class SemanticAnalysisCommand implements QuarkusApplication {

    public static String sutPath;
    public static String cachePath;

    @Override
    public int run(String... args) throws Exception {
        long start = System.currentTimeMillis();
        initCache();
        initPaths(args);
        // Start here
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
        String[] split = args[0].split(",");
        sutPath = split[0];
        cachePath = split[1];
//        sutPath = "/Users/me/Development/train-ticket/";
//        cachePath = "/Users/me/Development/data/";
//        sutPath = "C:\\git\\train-ticket";
//        cachePath = "C:\\git\\data";
    }


    public void initCache(){
        MsCache.init();
    }

    // Here we will process files in accordance to ProcessFiles.run
    public void preProcess() {
        ProcessFiles.run(sutPath);
        FlowBuilder flowBuilder = new FlowBuilder();
        flowBuilder.buildFlows();
        
        // Entity Construction
        MsCache.mappedEntities = EntityContextAdapter.getMappedEntityContext(sutPath);
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

}
