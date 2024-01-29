// package edu.university.ecs.lab.semantics.entity;

// import lombok.Data;

// import java.io.Serializable;

// // REMOVE THIS
// @Data
// public class MsCodeClone implements Serializable {
//     private MsFlowEntity a;
//     private MsFlowEntity b;
//     private double similarityController = 0;
//     private double similarityService = 0;
//     private double similarityRepository = 0;
//     private double similarityRestCalls = 0;
//     private double globalSimilarity = 0;
//     private boolean typeA;
//     private boolean typeB;
//     private boolean typeC;
    
//     private double ctrMethodNameSimilarity = 0;
//     private double ctrArgumentsLiteralSimilarity = 0;
// //    private double ctrArgumentsSemanticSimilarity = 0;
//     private double ctrReturnTypeLiteralSimilarity = 0;
// //    private double ctrReturnTypeSemanticSimilarity = 0;
//     private double ctrHttpMethodSimilarity = 0;
    
//     private double srvMethodNameSimilarity = 0;
//     private double srvArgumentsLiteralSimilarity = 0;
// //    private double srvArgumentsSemanticSimilarity = 0;
//     private double srvReturnTypeLiteralSimilarity = 0;
// //    private double srvReturnTypeSemanticSimilarity = 0;
    
//     private double repOperationTypeSimilarity = 0;
//     private double repArgumentsLiteralSimilarity = 0;
// //    private double repArgumentsSemanticSimilarity = 0;
//     private double repReturnTypeLiteralSimilarity = 0;
// //    private double repReturnTypeSemanticSimilarity = 0;
    
//     private double calHttpMethodSimilarity = 0;
//     private double calURLSimilarity = 0;
//     private double calArgumentsLiteralSimilarity = 0;
// //    private double calArgumentsSemanticSimilarity = 0;
//     private double calReturnTypeLiteralSimilarity = 0;
// //    private double calReturnTypeSemanticSimilarity = 0;
    

//     @Override
//     public String toString() {
//         return a.toString() + " | " + b.toString() + " : " + globalSimilarity;
//     }
// }
