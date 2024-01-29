// package edu.university.ecs.lab.semantics.util.factory;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;

// import edu.university.ecs.lab.semantics.entity.*;
// import edu.university.ecs.lab.semantics.entity.graph.*;
// import edu.university.ecs.lab.semantics.util.MsCache;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.Entity;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.SimilarityUtils;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.SimilarityUtilsImpl;
// import edu.university.ecs.lab.semantics.util.entitysimilarity.strategies.EntitySimilarityCheckStrategy;

// // REMOVE THIS

// public class CodeClonesFactory {
	
// 	private EntitySimilarityCheckStrategy entitySimilarityChecker;
	
// 	public CodeClonesFactory(EntitySimilarityCheckStrategy entitySimilarityChecker) {
// 		this.entitySimilarityChecker = entitySimilarityChecker;
// 	}

//     public void findCodeClones() {

//         for (int i = 0; i < MsCache.modules.size() -1; i++) {
//             for (int j = i + 1; j < MsCache.modules.size(); j++) {
//                 String iModule = MsCache.modules.get(i);
//                 String jModule = MsCache.modules.get(j);
//                 // get flows from i
//                 List<MsFlowEntity> iFlows = getFlowEntities(iModule);
//                 // get flows from j
//                 List<MsFlowEntity> jFlows = getFlowEntities(jModule);
//                 // compare each flow from i with each flow from j
//                 for (int k = 0; k < iFlows.size(); k++) {
//                     for (int l = 0; l < jFlows.size(); l++) {
//                         MsFlowEntity kFlow = iFlows.get(k);
//                         MsFlowEntity lFlow = jFlows.get(l);
//                         MsCodeClone msCodeClone = new MsCodeClone();
//                         msCodeClone.setA(kFlow);
//                         msCodeClone.setB(lFlow);
//                         if (kFlow.getMsControllerMethod() != null && lFlow.getMsControllerMethod() != null) {
//                         	msCodeClone.setSimilarityController(compareController(msCodeClone, kFlow, lFlow));
//                         } else {
                        	
//                         	msCodeClone.setSimilarityController(0.0);
//                         }
                        
//                         if (kFlow.getMsControllerMethod() == null && lFlow.getMsControllerMethod() == null) {
//                         	 msCodeClone.setCtrMethodNameSimilarity(-1.0);
//                              msCodeClone.setCtrReturnTypeLiteralSimilarity(-1.0);
//                              msCodeClone.setCtrHttpMethodSimilarity(-1.0);
//                              msCodeClone.setCtrArgumentsLiteralSimilarity(-1.0);
//                         }
                        
//                         if (kFlow.getMsServiceMethod() != null && lFlow.getMsServiceMethod() != null) {
//                             msCodeClone.setSimilarityService(compareService(msCodeClone, kFlow.getMsServiceMethod(), lFlow.getMsServiceMethod()));
//                         } else {
//                             msCodeClone.setSimilarityService(0.0);
//                         }
                        
//                         if (kFlow.getMsServiceMethod() == null && lFlow.getMsServiceMethod() == null) {
//                             msCodeClone.setSrvMethodNameSimilarity(-1.0);
//                         	msCodeClone.setSrvReturnTypeLiteralSimilarity(-1.0);
//                         	msCodeClone.setSrvArgumentsLiteralSimilarity(-1.0);
//                         }
                        
//                         if (kFlow.getMsRepositoryMethod() != null && lFlow.getMsRepositoryMethod() != null) {
//                             msCodeClone.setSimilarityRepository(compareRepository(msCodeClone, kFlow.getMsRepositoryMethod(), lFlow.getMsRepositoryMethod()));
//                         } else {
//                             msCodeClone.setSimilarityRepository(0.0);
//                         }
                        
//                         if (kFlow.getMsRepositoryMethod() == null && lFlow.getMsRepositoryMethod() == null) {
//                             msCodeClone.setRepReturnTypeLiteralSimilarity(-1.0);
//                         	msCodeClone.setRepOperationTypeSimilarity(-1.0);
//                         	msCodeClone.setRepArgumentsLiteralSimilarity(-1.0);
//                         }
                        
//                         if (kFlow.getMsRestCalls() != null && lFlow.getMsRestCalls() != null) {
//                             msCodeClone.setSimilarityRestCalls(compareRestCalls(msCodeClone, kFlow.getMsRestCalls(), lFlow.getMsRestCalls()));
//                         } else {
//                             msCodeClone.setSimilarityRestCalls(0.0);
//                         }
                        
//                         if (kFlow.getMsRestCalls() == null && lFlow.getMsRestCalls() == null) {
//                             msCodeClone.setCalURLSimilarity(-1.0);
//                             msCodeClone.setCalReturnTypeLiteralSimilarity(-1.0);
//                             msCodeClone.setCalHttpMethodSimilarity(-1.0);
//                         }
//                         msCodeClone.setGlobalSimilarity(calculateGlobalSimilarity(msCodeClone));
//                         classifyCodeClones(msCodeClone);
//                     }
//                 }
//             }
//         }
//     }

//     private void classifyCodeClones(MsCodeClone msCodeClone) {

//         if (msCodeClone.getGlobalSimilarity() > 0.0) {
//             MsCache.addHighSimilar(msCodeClone);
//         }
//         if (msCodeClone.getSimilarityController() == 1.0) {
//             MsCache.addSameController(msCodeClone);
//         }
//         if (msCodeClone.getSimilarityRepository() > 0.0) {
//             MsCache.addSameRepository(msCodeClone);
//         }
//         if (msCodeClone.getSimilarityRestCalls() >= 3.0) {
//             MsCache.addSameRestCall(msCodeClone);
//         }
// //        if (msCodeClone.getGlobalSimilarity() < 0.8 && msCodeClone.getGlobalSimilarity() >= 0.6) {
// //            MsCache.typeC.add(msCodeClone);
// //        }
//         if (msCodeClone.getGlobalSimilarity() < 0.8) {
//         	msCodeClone.setTypeC(true);
//             MsCache.typeC.add(msCodeClone);
//         }
//         if (msCodeClone.getGlobalSimilarity() < 0.9 && msCodeClone.getGlobalSimilarity() >= 0.8) {
//             msCodeClone.setTypeB(true);
//             MsCache.typeB.add(msCodeClone);
//         }
// //        if (msCodeClone.getGlobalSimilarity() <= 1.0 && msCodeClone.getGlobalSimilarity() >= 0.9) {
//         if (msCodeClone.getGlobalSimilarity() >= 0.9) {
//             msCodeClone.setTypeA(true);
//             MsCache.typeA.add(msCodeClone);
//         }
//         if (msCodeClone.isTypeA()) {
//             System.out.println();
//         }
//         MsCache.addCodeClone(msCodeClone);
//     }

//     private double calculateGlobalSimilarity(MsCodeClone msCodeClone) {
//         return (msCodeClone.getSimilarityController() * 0.8)
//                 + (msCodeClone.getSimilarityService() * 0.05 )
//                 + (msCodeClone.getSimilarityRepository() * 0.05 )
//                 + (msCodeClone.getSimilarityRestCalls() * 0.1);
//     }

//     private double compareRestCalls(MsCodeClone msCodeClone, List<MsRestCall> aMsRestCalls, List<MsRestCall> bMsRestCalls) {
//         double denominator = Math.max(aMsRestCalls.size(), bMsRestCalls.size());
//         double nominator = 0.0;
        
//         double urlSimilarity = 0;
//         double httpSimilarity = 0;
//         double returnTypeSimilarity = 0;
        
//         for (MsRestCall a: aMsRestCalls
//              ) {
//             for (MsRestCall b: bMsRestCalls
//                  ) {
//                 nominator += compareRestCall(msCodeClone, a, b);
                
//                 if (urlSimilarity < msCodeClone.getCalURLSimilarity()) {
//                 	urlSimilarity = msCodeClone.getCalURLSimilarity();
//                 }
                
//                 if (httpSimilarity < msCodeClone.getCalHttpMethodSimilarity()) {
//                 	httpSimilarity = msCodeClone.getCalHttpMethodSimilarity();
//                 }
                
//                 if (returnTypeSimilarity < msCodeClone.getCalReturnTypeLiteralSimilarity()) {
//                 	returnTypeSimilarity = msCodeClone.getCalReturnTypeLiteralSimilarity();
//                 }
                
//                 msCodeClone.setCalURLSimilarity(0);
//                 msCodeClone.setCalReturnTypeLiteralSimilarity(0);
//                 msCodeClone.setCalHttpMethodSimilarity(0);
//             }
//         }
// //        System.err.println("denominator = " + denominator);
        
//         msCodeClone.setCalURLSimilarity(urlSimilarity);
//         msCodeClone.setCalReturnTypeLiteralSimilarity(returnTypeSimilarity);
//         msCodeClone.setCalHttpMethodSimilarity(httpSimilarity);
        
//         if (denominator == 0.0) {
//             return 0.0;
//         }
        
// //        msCodeClone.setCalURLSimilarity(msCodeClone.getCalURLSimilarity() / denominator);
// //        msCodeClone.setCalReturnTypeLiteralSimilarity(msCodeClone.getCalReturnTypeLiteralSimilarity() / denominator);
// //        msCodeClone.setCalHttpMethodSimilarity(msCodeClone.getCalHttpMethodSimilarity() / denominator);
        
        
        
// //        
// //        System.err.println("URL = " + msCodeClone.getCalURLSimilarity());
// //        System.err.println("URL = " + msCodeClone.getCalReturnTypeLiteralSimilarity());
// //        System.err.println("URL = " + msCodeClone.getCalHttpMethodSimilarity());
// //        System.err.println("Result = " + nominator / denominator);
        
//         return nominator / denominator;
//     }

//     private double compareRestCall(MsCodeClone msCodeClone, MsRestCall a, MsRestCall b) {
    	
// //        private double calReturnTypeSemanticSimilarity = 0;
        
// //    	System.err.println(a.getApi() + " --- " + b.getApi());
// //    	System.err.println(a.getReturnType() + " --- " + b.getReturnType());
// //    	System.err.println(a.getHttpMethod() + " --- " + b.getHttpMethod());
    	
//         double similarity = 0.0;
//         if (a.getApi() != null && b.getApi() != null && a.getApi().replaceAll("\\s", "").toLowerCase().equals(b.getApi().replaceAll("\\s", "").toLowerCase())) {
//             similarity += 1.0;
// //            msCodeClone.setCalURLSimilarity(msCodeClone.getCalURLSimilarity() + 1.0);
//             msCodeClone.setCalURLSimilarity(1.0);
//         }
//         if (a.getReturnType() != null && b.getReturnType() != null && a.getReturnType().toLowerCase().equals(b.getReturnType().toLowerCase())) {
//             similarity += 1.0;
// //            System.err.println(a.getReturnType() + " --- " + b.getReturnType());
//             double similarityValue = this.entitySimilarityChecker.calculateSimilarity(msCodeClone.getA().getPackageName(), msCodeClone.getB().getPackageName(), a.getReturnType(), b.getReturnType());
//             msCodeClone.setCalReturnTypeLiteralSimilarity(similarityValue);
// //            msCodeClone.setCalReturnTypeLiteralSimilarity(msCodeClone.getCalReturnTypeLiteralSimilarity() + similarityValue);
//         }
//         if (a.getHttpMethod() != null && b.getHttpMethod() != null && a.getHttpMethod().equals(b.getHttpMethod())) {
//             similarity += 1.0;
//             msCodeClone.setCalHttpMethodSimilarity(1.0);
// //            msCodeClone.setCalHttpMethodSimilarity(msCodeClone.getCalHttpMethodSimilarity() + 1.0);
//         }
// //        System.err.println(similarity + "");
//         return similarity / 3.0;
//     }

//     private double getRepositoryOperationSimilarity(MsMethod aMethod, MsMethod bMethod) {
    	
//     	if (aMethod.getMethodName().equals(bMethod.getMethodName())
//     			|| aMethod.getMethodName().toLowerCase().contains("update") && bMethod.getMethodName().toLowerCase().contains("save")
//     			|| bMethod.getMethodName().toLowerCase().contains("update") && aMethod.getMethodName().toLowerCase().contains("save")) {
//             return 1.0;
//         }
//     	return 0.0;
//     }
    
//     private double compareRepository(MsCodeClone msCodeClone, MsMethod aMethod, MsMethod bMethod) {
// //    	System.err.println("REPO:  " + aMethod.getClassId() + "  " + aMethod.getMethodId() + " " + aMethod.getMethodName());
// //        return compareService(aMethod, bMethod);
    	
// //        private double repArgumentsSemanticSimilarity;
    	
    	
    	
//     	double same = 0.0;
//         if (aMethod.getReturnType() != null && bMethod.getReturnType() != null && aMethod.getReturnType().toLowerCase().equals(bMethod.getReturnType().toLowerCase())) {
//             same += 0.5;
//             double similarityValue = this.entitySimilarityChecker.calculateSimilarity(msCodeClone.getA().getPackageName(), msCodeClone.getB().getPackageName(), aMethod.getReturnType(), bMethod.getReturnType());
//             msCodeClone.setRepReturnTypeLiteralSimilarity(similarityValue); //
//         }
        
// //        if (aMethod.getMethodName().equals(bMethod.getMethodName())){
// //            same += 1.0;
// //        }
        
//         msCodeClone.setRepOperationTypeSimilarity(getRepositoryOperationSimilarity(aMethod, bMethod)); //
        
//         List<MsArgument> aArguments = aMethod.getMsArgumentList();
//         List<MsArgument> bArguments = bMethod.getMsArgumentList();
//         if (aArguments.size() != 0 && bArguments.size() != 0) {
//             double sameArguments = 0.0;
//             for (int i = 0; i < aArguments.size() - 1; i++) {
//                 for (int j = i + 1; j < bArguments.size(); j++) {

//                     if (aArguments.get(i).getReturnType() != null
//                             && aArguments.get(i).getReturnType() != null
//                             && aArguments.get(i).getReturnType().toLowerCase().equals(bArguments.get(j).getReturnType().toLowerCase())) {
//                         sameArguments += 1.0;
//                     }
//                 }
//             }
//             double denominator = Math.max(aArguments.size(), bArguments.size());
//             same += (sameArguments / denominator);
            
//             double argumentSimilarity = this.entitySimilarityChecker.calculateArgumentsSimilarity(aArguments, bArguments);
            
//             msCodeClone.setRepArgumentsLiteralSimilarity(argumentSimilarity);
            
// //            double argumentSimilarity = 0;
// ////          List<MsArgument> aArguments = aCtrl.getMsArgumentList();
// ////          List<MsArgument> bArguments = bCtrl.getMsArgumentList();
// //           int maxNumberArguments = Math.max(aArguments.size(), bArguments.size());
// //           List<Integer> usedArguments = new ArrayList<>();
// //           for (int i = 0; i < aArguments.size(); i++) {
// //               for (int j = 0; j < bArguments.size(); j++) {
// //
// //                   if (aArguments.get(i).getReturnType() != null
// //                           && bArguments.get(j).getReturnType() != null
// //                           && !usedArguments.contains(j)
// //                           && aArguments.get(i).getReturnType().toLowerCase().equals(bArguments.get(j).getReturnType().toLowerCase())) {
// //                               usedArguments.add(j);
// ////                               same += 1.0;
// //                               argumentSimilarity += 1;
// //                       break;
// //                   }
// //               }
// //           }
// //
// //           
// //           msCodeClone.setSrvArgumentsLiteralSimilarity((double)(argumentSimilarity/maxNumberArguments)); //
          
//         }
//         return same;
//     }

//     private double compareService(MsCodeClone msCodeClone, MsMethod aMethod, MsMethod bMethod) {
    	
        
// //        private double srvArgumentsSemanticSimilarity;
// //        private double srvReturnTypeSemanticSimilarity;
       
        
//         double same = 0.0;
//         if (aMethod.getReturnType() != null && bMethod.getReturnType() != null && aMethod.getReturnType().toLowerCase().equals(bMethod.getReturnType().toLowerCase())) {
//             same += 0.5;
//             double similarityValue = this.entitySimilarityChecker.calculateSimilarity(msCodeClone.getA().getPackageName(), msCodeClone.getB().getPackageName(), aMethod.getReturnType(), bMethod.getReturnType());
//             msCodeClone.setSrvReturnTypeLiteralSimilarity(similarityValue); //
//         }
        
//         if (aMethod.getMethodName().equals(bMethod.getMethodName())){
// //            same += 1.0;
//             msCodeClone.setSrvMethodNameSimilarity(1.0); //
//         }
        
//         List<MsArgument> aArguments = aMethod.getMsArgumentList();
//         List<MsArgument> bArguments = bMethod.getMsArgumentList();
//         if (aArguments.size() != 0 && bArguments.size() != 0) {
//             double sameArguments = 0.0;
//             for (int i = 0; i < aArguments.size() - 1; i++) {
//                 for (int j = i + 1; j < bArguments.size(); j++) {

//                     if (aArguments.get(i).getReturnType() != null
//                             && aArguments.get(i).getReturnType() != null
//                             && aArguments.get(i).getReturnType().equals(bArguments.get(j).getReturnType())) {
//                         sameArguments += 1.0;
//                     }
//                 }
//             }
//             double denominator = Math.max(aArguments.size(), bArguments.size());
//             same += (sameArguments / denominator);
//         }
        
        
//         double argumentSimilarity = this.entitySimilarityChecker.calculateArgumentsSimilarity(aArguments, bArguments);
//         msCodeClone.setSrvArgumentsLiteralSimilarity(argumentSimilarity);
        
// //        double argumentSimilarity = 0;
// ////        List<MsArgument> aArguments = aCtrl.getMsArgumentList();
// ////        List<MsArgument> bArguments = bCtrl.getMsArgumentList();
// //        int maxNumberArguments = Math.max(aArguments.size(), bArguments.size());
// //         List<Integer> usedArguments = new ArrayList<>();
// //         for (int i = 0; i < aArguments.size(); i++) {
// //             for (int j = 0; j < bArguments.size(); j++) {
// //
// //                 if (aArguments.get(i).getReturnType() != null
// //                         && bArguments.get(j).getReturnType() != null
// //                         && !usedArguments.contains(j)
// //                         && aArguments.get(i).getReturnType().toLowerCase().equals(bArguments.get(j).getReturnType().toLowerCase())) {
// //                             usedArguments.add(j);
// ////                             same += 1.0;
// //                             argumentSimilarity += 1;
// //                     break;
// //                 }
// //             }
// //         }
// //
// //         
// //         msCodeClone.setSrvArgumentsLiteralSimilarity((double)(argumentSimilarity/maxNumberArguments)); //
        
//         return same;
//     }

//     public List<MsFlowEntity> getFlowEntities(String module) {
//         return MsCache.msFlows
//                 .stream()
//                 .filter(n -> n.getMsController().getMsId().getPath().contains(module)).collect(Collectors.toList());
//     }

//     private double compareController(MsCodeClone msCodeClone, MsFlowEntity a, MsFlowEntity b) {
//         double same = 0.0;
//         if (a == null || b == null) {
//             return 0.0;
//         }
//         MsMethod aCtrl = a.getMsControllerMethod();
//         MsMethod bCtrl = b.getMsControllerMethod();
//         if (aCtrl == null || bCtrl == null) {
//             return 0.0;
//         }

        
// //        private double ctrArgumentsSemanticSimilarity;
// //        private double ctrReturnTypeSemanticSimilarity;
// //        private double ctrHttpMethodSimilarity;
        
// //        System.err.println("CTR - " + aCtrl.getMethodName() + " --- " + bCtrl.getMethodName());
        
//         if (aCtrl.getMethodName().toLowerCase().equals(bCtrl.getMethodName().toLowerCase())){
//             same += 1.0;
//             msCodeClone.setCtrMethodNameSimilarity(1.0); //
//         }

//         if (aCtrl.getReturnType().toLowerCase().equals(bCtrl.getReturnType().toLowerCase())){
//             same += 1.0;
// //            double similarityValue = this.entitySimilarityChecker.calculateSimilarity(aCtrl.getReturnType(), bCtrl.getReturnType());
// //            msCodeClone.setCtrReturnTypeLiteralSimilarity(similarityValue); //
//         }
        
//         msCodeClone.setCtrReturnTypeLiteralSimilarity(this.entitySimilarityChecker.calculateSimilarity(msCodeClone.getA().getPackageName(), msCodeClone.getB().getPackageName(), aCtrl.getReturnType(), bCtrl.getReturnType())); //

//         double annotationsSimilarity = 0;
//         int maxNumberAnnotations = Math.max(aCtrl.getMsAnnotations().size(), bCtrl.getMsAnnotations().size());
        
//         for (MsAnnotation aA: aCtrl.getMsAnnotations()
//              ) {
//             for (MsAnnotation bA: bCtrl.getMsAnnotations()
//                  ) {
//                 if (aA.getAnnotationName().toLowerCase().equals(bA.getAnnotationName().toLowerCase())) {
//                     if (aCtrl.getReturnType().equals(bCtrl.getReturnType())) {
//                         same += 1.0;
//                     }
//                     annotationsSimilarity += 1;
//                 }
//             }
//         }
        
//         msCodeClone.setCtrHttpMethodSimilarity(annotationsSimilarity/maxNumberAnnotations); //
        
//         double argsSimilarity = this.entitySimilarityChecker.calculateArgumentsSimilarity(aCtrl.getMsArgumentList(), bCtrl.getMsArgumentList());
        
//         msCodeClone.setCtrArgumentsLiteralSimilarity(Double.isNaN(argsSimilarity) ? 0.0 : argsSimilarity);
        
// //        msCodeClone.setCtrArgumentsLiteralSimilarity(compareArguments(aCtrl.getMsArgumentList(), bCtrl.getMsArgumentList())); //
        
// //        double argumentSimilarity = 0;
// //        List<MsArgument> aArguments = aCtrl.getMsArgumentList();
// //        List<MsArgument> bArguments = bCtrl.getMsArgumentList();
// //        double maxNumberArguments = Math.max(aArguments.size(), bArguments.size());
// //         List<Integer> usedArguments = new ArrayList<>();
// //         for (int i = 0; i < aArguments.size(); i++) {
// //             for (int j = 0; j < bArguments.size(); j++) {
// //
// //                 if (aArguments.get(i).getReturnType() != null
// //                         && bArguments.get(j).getReturnType() != null
// //                         && !usedArguments.contains(j)
// //                         && aArguments.get(i).getReturnType().toLowerCase().equals(bArguments.get(j).getReturnType().toLowerCase())) {
// //                             usedArguments.add(j);
// ////                             same += 1.0;
// //                             argumentSimilarity += 1;
// //                     break;
// //                 }
// //             }
// //         }

// //         msCodeClone.setCtrArgumentsLiteralSimilarity((double)(argumentSimilarity/maxNumberArguments)); //
         
// //        for (MsArgument msA: aCtrl.getMsArgumentList()
// //             ) {
// //            for (MsArgument msB: bCtrl.getMsArgumentList()
// //                 ) {
// //                if (msA.getReturnType().equals(msB.getReturnType())){
// //                    same += 1.0;
// //                }
// //            }
// //        }
//         return same / 4;

//     }
    
// //    private double compareArguments(List<MsArgument> aArguments, List<MsArgument> bArguments) {
// //    	 double argumentSimilarity = 0;
// //         double maxNumberArguments = Math.max(aArguments.size(), bArguments.size());
// //         
// //          List<Integer> usedArguments = new ArrayList<>();
// //          for (int i = 0; i < aArguments.size(); i++) {
// //              for (int j = 0; j < bArguments.size(); j++) {
// //
// ////                  if (aArguments.get(i).getReturnType() != null
// ////                          && bArguments.get(j).getReturnType() != null
// ////                          && !usedArguments.contains(j)
// ////                          && aArguments.get(i).getReturnType().toLowerCase().equals(bArguments.get(j).getReturnType().toLowerCase())) {
// ////                              usedArguments.add(j);
// //////                              same += 1.0;
// ////                              argumentSimilarity += 1;
// ////                      break;
// ////                  }
// //            	  
// //            	  if (aArguments.get(i).getReturnType() != null
// //                          && bArguments.get(j).getReturnType() != null
// //                          && !usedArguments.contains(j)) {
// //                           
// //            		  if (aArguments.get(i).getReturnType().toLowerCase()
// //            				  .equals(bArguments.get(j).getReturnType().toLowerCase())) {
// //            			  
// //            			  argumentSimilarity += 1;
// //            			  usedArguments.add(j);
// //            		  }
// //                      break;
// //                  } 
// //            	  
// //                  
// //                  
// //              }
// //          }
// //          return argumentSimilarity/maxNumberArguments;
// //    }
    
// //    private double calcualteSimilarity(String variableName1, String variableName2, boolean useWuPalmer) {
// //    	
// //    	double similarityValue = 0;
// //    	
// //    	if (MsCache.mappedEntities.containsKey(variableName1) && MsCache.mappedEntities.containsKey(variableName2)) {
// //    		Entity entity1 = MsCache.mappedEntities.get(variableName1);
// //    		Entity entity2 = MsCache.mappedEntities.get(variableName2);
// //    		similarityValue = this.similarityUtils.calculateSimilarity(entity1, entity2, useWuPalmer);
// //    	} else {
// //    		similarityValue = this.similarityUtils.nameSimilarity(variableName1, variableName2, useWuPalmer);
// //    	}
// //    	return similarityValue;
// //    }
// }
