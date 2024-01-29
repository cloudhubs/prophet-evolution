// package edu.university.ecs.lab.semantics.util.factory;

// import org.checkerframework.checker.units.qual.A;

// import edu.university.ecs.lab.semantics.SemanticAnalysisCommand;
// import edu.university.ecs.lab.semantics.entity.MsCodeClone;
// import edu.university.ecs.lab.semantics.entity.MsFlowEntity;
// import edu.university.ecs.lab.semantics.entity.graph.MsClass;
// import edu.university.ecs.lab.semantics.entity.graph.MsMethod;
// import edu.university.ecs.lab.semantics.entity.quantification.ModuleClone;
// import edu.university.ecs.lab.semantics.util.MsCache;

// import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.PrintWriter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// // REMOVE THIS

// public class ModuleCloneFactory {
//     /**
//      * 1. take all code clones type a and b
//      * 2. for each module
//      * 3. for each flow in module
//      * 4. look if flow is in A or B of code clone
//      * 5. if it is, increment counter
//      */

//     public void createData(){
//         List<ModuleClone> moduleClones = getModuleClones();
//         List<String[]> modulesDataLines = convertModuleClones(moduleClones);
//         createCSVFile("per-module-clones", modulesDataLines);
        
//         List<MsCodeClone> codeClones = getCodeClonesTypeAB();
//         List<String[]> cfgDataLines = constructClonesAttributesData(codeClones);
//         createCSVFile("per-cfg-clones", cfgDataLines);
        
        
//         List<MsCodeClone> codeNonClones = getCodeClonesTypeC();
//         List<String[]> cfgNonCloneDataLines = constructClonesAttributesData(codeNonClones);
//         createCSVFile("per-cfg-non-clones", cfgNonCloneDataLines);
        
        
// //        File csvOutputFile = new File(SemanticAnalysisCommand.cachePath + "/per-module-clones.csv");
// //        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
// //            dataLines.stream()
// //                    .map(this::convertToCSV)
// //                    .forEach(pw::println);
// //        } catch (FileNotFoundException e) {
// //            e.printStackTrace();
// //        }
//     }
    
//     private void createCSVFile(String fileName, List<String[]> dataLines) {
//     	File csvOutputFile = new File(SemanticAnalysisCommand.cachePath + "/" + fileName + ".csv");
//         try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
//             dataLines.stream()
//                     .map(this::convertToCSV)
//                     .forEach(pw::println);
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         }
//     }

//     public List<String[]> convertModuleClones(List<ModuleClone> moduleClones) {
//         List<String[]> data = new ArrayList<>();
//         data.add(new String[]{"id", "cfg", "percent"});
//         for (ModuleClone moduleClone: moduleClones
//              ) {
//             data.add(getStringArray(moduleClone));
//         }
//         return data;
//     }

//     public String[] getStringArray(ModuleClone clone) {
//         String[] data = new String[3];
//         data[0] = Integer.toString(clone.getModuleId());
//         data[1] = Integer.toString(clone.getCfgNr());
//         data[2] = Double.toString(clone.getPercentageClones());
//         return data;
//     }

//     public String convertToCSV(String[] data) {
//         return Stream.of(data)
//                 .map(this::escapeSpecialCharacters)
//                 .collect(Collectors.joining(","));
//     }

//     public String escapeSpecialCharacters(String data) {
//         String escapedData = data.replaceAll("\\R", " ");
//         if (data.contains(",") || data.contains("\"") || data.contains("'")) {
//             data = data.replace("\"", "\"\"");
//             escapedData = "\"" + data + "\"";
//         }
//         return escapedData;
//     }

//     public List<String[]> constructClonesAttributesData(List<MsCodeClone> codeClones) {
    	
//     	List<String[]> dataList = new ArrayList<>();
    	
// //    	dataList.add(new String[]{"cfg 1", "cfg 2", "ctr", "srv", "rep", "cls", "global", "clone",
// //    			"ctr-method-name", "ctr-args-lit", "ctr-args-sem", "ctr-return-lit", "ctr-return-sem", "ctr-method-http",
// //    			"srv-method-name", "srv-args-lit", "srv-args-sem", "srv-return-lit", "srv-return-sem",
// //    			"rep-method-op", "rep-args-lit", "rep-args-sem", "rep-return-lit", "rep-return-sem",
// //    			"cal-method-http", "cal-url", "cal-return-lit", "cal-return-sem"});
    	
//     	dataList.add(new String[]{"cfg 1", "cfg 2", "ctr", "srv", "rep", "cls", "global", "clone",
//     			"ctr-method-name", "ctr-args-lit", "ctr-return-lit", "ctr-method-http",
//     			"srv-method-name", "srv-args-lit", "srv-return-lit",
//     			"rep-method-op", "rep-args-lit", "rep-return-lit",
//     			"cal-method-http", "cal-url", "cal-return-lit"});
    	
// //    	List<ModuleClone> moduleClones = new ArrayList<>();
// //        List<MsCodeClone> codeClones = getCodeClonesTypeAB();
// //        List<String> codeCloneIds = new ArrayList<>();
//         for (MsCodeClone mcc: codeClones
//              ) {
//         	String[] data = new String[21];
//             String controllerA = mcc.getA().getMsController().getMsId().getPath();
//             String methodA = mcc.getA().getMsControllerMethod().getMethodName();
//             String controllerB = mcc.getB().getMsController().getMsId().getPath();
//             String methodB = mcc.getB().getMsControllerMethod().getMethodName();
//             data[0] = (controllerA + "." + methodA); // A 
//             data[1] = (controllerB + "." + methodB); // B
            
//             data[2] = String.valueOf(mcc.getSimilarityController());
//             data[3] = String.valueOf(mcc.getSimilarityService());
//             data[4] = String.valueOf(mcc.getSimilarityRepository());
//             data[5] = String.valueOf(mcc.getSimilarityRestCalls());
//             data[6] = String.valueOf(mcc.getGlobalSimilarity());
//             data[7] = mcc.isTypeA()? "A" : (mcc.isTypeB()? "B" : "N"); // bcz all are A or B in this list
//             data[8] = String.valueOf(mcc.getCtrMethodNameSimilarity());
//             data[9] = String.valueOf(mcc.getCtrArgumentsLiteralSimilarity());
// //            data[10] = String.valueOf(mcc.getCtrArgumentsSemanticSimilarity());
//             data[10] = String.valueOf(mcc.getCtrReturnTypeLiteralSimilarity());
// //            data[12] = String.valueOf(mcc.getCtrReturnTypeSemanticSimilarity());
//             data[11] = String.valueOf(mcc.getCtrHttpMethodSimilarity());
            
//             data[12] = String.valueOf(mcc.getSrvMethodNameSimilarity());
//             data[13] = String.valueOf(mcc.getSrvArgumentsLiteralSimilarity());
// //            data[16] = String.valueOf(mcc.getSrvArgumentsSemanticSimilarity());
//             data[14] = String.valueOf(mcc.getSrvReturnTypeLiteralSimilarity());
// //            data[18] = String.valueOf(mcc.getSrvReturnTypeSemanticSimilarity());
            
//             data[15] = String.valueOf(mcc.getRepOperationTypeSimilarity());
//             data[16] = String.valueOf(mcc.getRepArgumentsLiteralSimilarity());
// //            data[21] = String.valueOf(mcc.getRepArgumentsSemanticSimilarity());
//             data[17] = String.valueOf(mcc.getRepReturnTypeLiteralSimilarity());
// //            data[23] = String.valueOf(mcc.getRepReturnTypeSemanticSimilarity());
            
//             data[18] = String.valueOf(mcc.getCalHttpMethodSimilarity());
//             data[19] = String.valueOf(mcc.getCalURLSimilarity());
// //            data[26] = String.valueOf(mcc.getCalArgumentsLiteralSimilarity());
// //            data[27] = String.valueOf(mcc.getCalArgumentsSemanticSimilarity());
//             data[20] = String.valueOf(mcc.getCalReturnTypeLiteralSimilarity());
// //            data[27] = String.valueOf(mcc.getCalReturnTypeSemanticSimilarity());
            
//             dataList.add(data);
//         }
//         return dataList;
//     }
    
//     public List<ModuleClone> getModuleClones(){
//         List<ModuleClone> moduleClones = new ArrayList<>();
//         List<MsCodeClone> codeClones = getCodeClonesTypeAB();
//         List<String> codeCloneIds = new ArrayList<>();
//         for (MsCodeClone mcc: codeClones
//              ) {
//             String controllerA = mcc.getA().getMsController().getMsId().getPath();
//             String methodA = mcc.getA().getMsControllerMethod().getMethodName();
//             String controllerB = mcc.getB().getMsController().getMsId().getPath();
//             String methodB = mcc.getB().getMsControllerMethod().getMethodName();
//             codeCloneIds.add(controllerA + "." + methodA);
//             codeCloneIds.add(controllerB + "." + methodB);
//         }
//         for (int i = 0; i < MsCache.modules.size(); i++) {
//             String module = MsCache.modules.get(i);
//             ModuleClone moduleClone = new ModuleClone();
//             moduleClone.setModuleId(i+1);
//             moduleClone.setModuleName(module);
//             int counter = 0;
//             List<MsFlowEntity> flowEntities = getModuleFlowEntity(module);
//             for (MsFlowEntity mf: flowEntities
//                  ) {
//                 String msFlowId = mf.getMsController().getMsId().getPath() + "." + mf.getMsControllerMethod().getMethodName();
//                 if (codeCloneIds.contains(msFlowId)) {
//                     counter += 1;
//                 }
//             }
//             moduleClone.setClonedCfg(counter);
//             moduleClone.setCfgNr(flowEntities.size());
//             moduleClone.setPercentageClones(((double) counter / (double) flowEntities.size() * 100));
//             moduleClones.add(moduleClone);
//         }
//         return moduleClones;
//     }

//     private List<MsFlowEntity> getModuleFlowEntity(String module){
//         return MsCache.msFlows.stream()
//                 .filter(n -> n.getMsController().getMsId().getPath().contains(module))
//                 .collect(Collectors.toList());
//     }

//     private List<MsCodeClone> getCodeClonesTypeAB() {
//         return MsCache.msCodeClones.stream()
//                 .filter(n -> n.isTypeA() || n.isTypeB())
//                 .collect(Collectors.toList());
//     }
    
//     private List<MsCodeClone> getCodeClonesTypeC() {
//         return MsCache.msCodeClones.stream()
//                 .filter(n -> n.isTypeC())
//                 .collect(Collectors.toList());
//     }

// }
