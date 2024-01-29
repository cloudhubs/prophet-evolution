// package edu.university.ecs.lab.semantics.util.entitysimilarity.strategies;

// import java.util.ArrayList;
// import java.util.List;

// import edu.university.ecs.lab.semantics.entity.graph.MsArgument;

// // REMOVE THIS

// public class EntityLiteralSimilarityCheckStrategy implements EntitySimilarityCheckStrategy {

// 	@Override
// 	public double calculateSimilarity(String packageName1, String packageName2, String variableName1, String variableName2) {
// 		return variableName1.toLowerCase().equals(variableName2.toLowerCase()) 
// 				? 1.0 : 0.0;
// 	}
	
// 	@Override
// 	public double calculateArgumentsSimilarity(List<MsArgument> aArguments, List<MsArgument> bArguments) {
		
// 		if (aArguments.isEmpty() && bArguments.isEmpty()) {
// 			return 1.0;
// 		}
		
// 		double argumentSimilarity = 0;
//         double maxNumberArguments = Math.max(aArguments.size(), bArguments.size());
        
//          List<Integer> usedArguments = new ArrayList<>();
//          for (int i = 0; i < aArguments.size(); i++) {
//              for (int j = 0; j < bArguments.size(); j++) {

//                  if (aArguments.get(i).getReturnType() != null
//                          && bArguments.get(j).getReturnType() != null
//                          && !usedArguments.contains(j)
//                          && aArguments.get(i).getReturnType().toLowerCase().equals(bArguments.get(j).getReturnType().toLowerCase())) {
//                              usedArguments.add(j);
// //                             same += 1.0;
//                              argumentSimilarity += 1;
//                      break;
//                  }
           	  
// //           	  if (aArguments.get(i).getReturnType() != null
// //                         && bArguments.get(j).getReturnType() != null
// //                         && !usedArguments.contains(j)) {
// //                          
// //           		  if (aArguments.get(i).getReturnType().toLowerCase()
// //           				  .equals(bArguments.get(j).getReturnType().toLowerCase())) {
// //           			  
// //           			  argumentSimilarity += 1;
// //           			  usedArguments.add(j);
// //           		  }
// //                     break;
// //                 } 
                 
//              }
//          }
//          return argumentSimilarity/maxNumberArguments;
//    }

// }
