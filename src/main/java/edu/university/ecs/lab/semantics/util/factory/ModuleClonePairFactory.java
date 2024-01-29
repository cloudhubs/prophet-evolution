// package edu.university.ecs.lab.semantics.util.factory;

// import org.checkerframework.checker.units.qual.A;

// import edu.university.ecs.lab.semantics.SemanticAnalysisCommand;
// import edu.university.ecs.lab.semantics.entity.MsCodeClone;
// import edu.university.ecs.lab.semantics.entity.MsFlowEntity;
// import edu.university.ecs.lab.semantics.entity.quantification.ModuleClonePair;
// import edu.university.ecs.lab.semantics.util.MsCache;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.strategies.EntityLiteralSimilarityCheckStrategy;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.strategies.EntitySematicSimilarityCheckStrategy;

// import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.PrintWriter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// // REMOVE THIS

// public class ModuleClonePairFactory {

//     private CodeClonesFactory codeClonesFactory;

//     public ModuleClonePairFactory(){
//         codeClonesFactory = new CodeClonesFactory(new EntityLiteralSimilarityCheckStrategy());
// //    	codeClonesFactory = new CodeClonesFactory(new EntitySematicSimilarityCheckStrategy(false));
        
//     }

//     public void printModuleClonePairs(){
//         List<ModuleClonePair> moduleClonePairs = createModuleClonePairs();
//         File csvOutputFile = new File(SemanticAnalysisCommand.cachePath +"/module-pair-clones.txt");
//         try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
//             moduleClonePairs.stream()
//                     .map(this::convertToString)
//                     .forEach(pw::println);
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         }

//     }

//     public String convertToString(ModuleClonePair pair) {
//         return pair.toString();
//     }

//     /**
//      * compare ever module with the other
//      * look at how similar those two modules are
//      */
//     public List<ModuleClonePair> createModuleClonePairs() {
//         List<ModuleClonePair> moduleClonePairs = new ArrayList<>();
//         for (int i = 0; i < MsCache.modules.size(); i++) {
//             for (int j = 0; j < MsCache.modules.size(); j++) {
//                 String iModule = MsCache.modules.get(i);
//                 String jModule = MsCache.modules.get(j);
//                 if (!iModule.equals(jModule)) {
//                     ModuleClonePair moduleClonePair = new ModuleClonePair();
//                     moduleClonePair.setModuleA(iModule);
//                     moduleClonePair.setModuleB(jModule);
//                     moduleClonePair.setModuleAFlows(codeClonesFactory.getFlowEntities(iModule));
//                     moduleClonePair.setModuleBFlows(codeClonesFactory.getFlowEntities(jModule));
//                     moduleClonePair.setCodeClones(getAssociatedCodeClones(iModule, jModule));
//                     moduleClonePair.setPercentClonesModuleA(flowRepresentation(moduleClonePair.getModuleAFlows(), moduleClonePair.getCodeClones()));
//                     moduleClonePairs.add(moduleClonePair);
//                 }
//             }
//         }
//         List<ModuleClonePair> greater = moduleClonePairs.stream().filter(n -> n.getPercentClonesModuleA() > 0.0).collect(Collectors.toList());
//         return greater;
//     }

// //    private List<MsCodeClone> getTypeABCodeClones(){
// //        return MsCache.msCodeClones.
// //    }

//     /**
//      * Find code clones such that A has moduleA and B has moduleB
//      */
//     private List<MsCodeClone> getAssociatedCodeClones(String moduleA, String moduleB){
//         return MsCache.msCodeClones.stream()
//                 .filter(n ->
//                         (n.getA().getMsController().getMsId().getPath().contains(moduleA) && n.getB().getMsController().getMsId().getPath().contains(moduleB) && (n.isTypeA() || n.isTypeB())) ||
//                         (n.getA().getMsController().getMsId().getPath().contains(moduleB) && n.getB().getMsController().getMsId().getPath().contains(moduleA) && (n.isTypeA() || n.isTypeB()))
//                         )
//                 .collect(Collectors.toList());
//     }


//     /**
//      * How many Flows are represented in each module
//      */
//     private double flowRepresentation(List<MsFlowEntity> flowEntities, List<MsCodeClone> codeClones) {
//         // iterate through module A flows in code clones and look if it is present in the code clones (a)
//         int same = 0;

// //        for (MsCodeClone codeClone: codeClones
// //             ) {
// //            for (:
// //                 ) {
// //
// //            }
// //        }
//         for (MsFlowEntity msFlowEntity: flowEntities
//              ) {
//             for (MsCodeClone msCodeClone: codeClones
//                  ) {

//                 if (msFlowEntity.getMsController().getClassName().equals(msCodeClone.getA().getMsController().getClassName()) &&
//                         msFlowEntity.getMsControllerMethod().getMethodName().equals(msCodeClone.getA().getMsControllerMethod().getMethodName())) {
//                     same += 1;
//                     break;
//                 }

// //                if (msFlowEntity
// //                        .getMsController()
// //                        .getMsId().getPath()
// //                        .contains(msCodeClone.getA().getMsController().getMsId().getPath()) ||
// //                        msFlowEntity.getMsController().getMsId().getPath().contains(msCodeClone.getB().getMsController().getMsId().getPath())
// //                ){
// //                    same += 1;
// //                    break;
// //                }
//             }
//         }
//         if (flowEntities.size() == 0) {
//             return 0.0;
//         } else {
//             if (same == 0.0) {
//                 return same;
//             } else {
//                 return ((double) same / (double) flowEntities.size()) * 100;
//             }
//         }

//     }
// }
