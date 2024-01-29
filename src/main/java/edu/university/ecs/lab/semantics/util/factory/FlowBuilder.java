package edu.university.ecs.lab.semantics.util.factory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.university.ecs.lab.semantics.entity.*;
import edu.university.ecs.lab.semantics.entity.graph.*;
import edu.university.ecs.lab.semantics.util.MsCache;

/**
 * This builds flows of controller down to repository, keeps track of where it's been and where it goes
 * between controller to service to repository. Logs all function calls, etc. etc.
 */
public class FlowBuilder {

    public void buildFlows(){
        List<MsFlowEntity> msFlowEntities = findControllerMethods();
        for (MsFlowEntity msFlowEntity: msFlowEntities
             ) {
            // 1. get controller
            msFlowEntity.setMsController(findController(msFlowEntity));
            
            
//            System.err.println("CONTROLLER : " + msFlowEntity.getMsControllerMethod().getMethodName());
            // 2. get service method call in controller method
            Optional<MsMethodCall> serviceMethodCall = findServiceCall(msFlowEntity.getMsControllerMethod());
            if (serviceMethodCall.isPresent()) {
                msFlowEntity.setMsServiceMethodCall(serviceMethodCall.get());
                
                // 3. get service field variable in controller class by method call
                Optional<MsField> serviceField = findServiceField(msFlowEntity.getMsServiceMethodCall());
                if (serviceField.isPresent()) {
                    msFlowEntity.setMsControllerServiceField(serviceField.get());
                    // 4. get service class
                    Optional<MsClass> msServiceClass = findService(msFlowEntity.getMsControllerServiceField());
                    if (msServiceClass.isPresent()) {
                        msFlowEntity.setMsService(msServiceClass.get());
                        // 5. find service method name
                        Optional<MsMethod> msServiceMethod = findMsServiceMethod(msFlowEntity.getMsService(), msFlowEntity.getMsServiceMethodCall());
                        if (msServiceMethod.isPresent()) {
                            msFlowEntity.setMsServiceMethod(msServiceMethod.get());
//                            System.err.println("SERVICE : " + msServiceMethod.get().getMethodName());
                            // 5. get rest calls
                            List<MsRestCall> restCalls = findRestCalls(msFlowEntity.getMsServiceMethod());
//                            System.err.println("REST CALLS = " + restCalls.size());
                            msFlowEntity.setMsRestCalls(restCalls);
                            // 6. find method call in the service
                            Optional<MsMethodCall> repositoryMethodCall =
                                    findMsRepositoryMethodCall(msFlowEntity.getMsService(), msFlowEntity.getMsServiceMethod());
                            if (repositoryMethodCall.isPresent()) {
                                msFlowEntity.setMsRepositoryMethodCall(repositoryMethodCall.get());
                                // 7. find repository variable
                                Optional<MsField> repositoryField = findRepositoryField(msFlowEntity.getMsService(), msFlowEntity.getMsRepositoryMethodCall());
                                if (repositoryField.isPresent()) {
                                    msFlowEntity.setMsServiceRepositoryField(repositoryField.get());
                                    // 8. find repository class
                                    Optional<MsClass> repositoryClass = findRepositoryClass(msFlowEntity.getMsServiceRepositoryField());
                                    if (repositoryClass.isPresent()) {
                                        msFlowEntity.setMsRepository(repositoryClass.get());
                                        // 9. find repository method
                                        Optional<MsMethod> repositoryMethod = findRepositoryMethod(msFlowEntity.getMsRepository(), msFlowEntity.getMsRepositoryMethodCall());
                                        if (repositoryMethod.isPresent()) {
                                            msFlowEntity.setMsRepositoryMethod(repositoryMethod.get());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
//            if (msFlowEntity.getMsController() != null) {
//            	System.err.println(msFlowEntity.getMsController().getPackageName());
//            }
        }
        MsCache.msFlows = msFlowEntities;
    }

    private Optional<MsMethod> findRepositoryMethod(MsClass msRepository, MsMethodCall msRepositoryMethodCall) {
        return MsCache.msMethodList.stream()
                .filter(n -> n.getMsId().getPath().equals(msRepository.getMsId().getPath()) && n.getMethodName().equals(msRepositoryMethodCall.getCalledMethodName()))
                .findFirst();
    }

    private Optional<MsClass> findRepositoryClass(MsField msServiceRepositoryField) {
        return MsCache.msClassList.stream()
                .filter(n -> n.getClassName().equals(msServiceRepositoryField.getFieldClass()))
                .findFirst();
    }

    private Optional<MsField> findRepositoryField(MsClass msService, MsMethodCall repositoryMethodCall) {
        return MsCache.msFieldList.stream()
                .filter(n -> n.getMsId().getPath().equals(msService.getMsId().getPath()) && n.getFieldVariable().equals(repositoryMethodCall.getCalledServiceId()))
                .findFirst();
    }

    private Optional<MsMethodCall> findMsRepositoryMethodCall(MsClass msService, MsMethod msServiceMethod) {
        return MsCache.msMethodCallList.stream()
                .filter(n -> n.getMsId().getPath().equals(msService.getMsId().getPath()) && n.getParentMethodName().equals(msServiceMethod.getMethodName()))
                .findFirst();
    }

    private Optional<MsMethod> findMsServiceMethod(MsClass msService, MsMethodCall controllerServiceMethodCall) {
        return MsCache.msMethodList.stream()
                .filter(n -> n.getMsId().getPath().equals(msService.getMsId().getPath()) && n.getMethodName().equals(controllerServiceMethodCall.getCalledMethodName()))
                .findFirst();
    }

    private List<MsRestCall> findRestCalls(MsMethod msMethodService) {
//    	System.err.println("PARENT = " + msMethodService.getClassName() + "  " + msMethodService.getMethodName() );
//    	for (MsRestCall n : MsCache.msRestCallList) {
//    		System.err.println("CACH = " + n.getParentClassName() + "  " + n.getParentMethodName());
//    	}
        return MsCache.msRestCallList.stream()
                .filter(n -> n.getParentClassName().equals(msMethodService.getClassName()) && n.getParentMethodName().equals(msMethodService.getMethodName()))
                .collect(Collectors.toList());
    }

    private Optional<MsClass> findService(MsField msControllerServiceField) {
        return MsCache.msClassList.stream()
                .filter(n -> n.getClassName().equals(msControllerServiceField.getFieldClass() + "Impl"))
                .findFirst();
    }

    private Optional<MsField> findServiceField(MsMethodCall msMethodCall) {
        return MsCache.msFieldList.stream()
                .filter(n -> n.getMsId().getPath().equals(msMethodCall.getMsId().getPath()) && n.getFieldVariable().equals(msMethodCall.getCalledServiceId()))
                .findFirst();
    }

    public List<MsFlowEntity> findControllerMethods()  {
        return MsCache.msMethodList
                .stream()
                .filter(n -> n.getMsId().getPath().toLowerCase().contains(MsClassRoles.CONTROLLER.toString().toLowerCase()))
                .map(MsFlowEntity::new)
                .collect(Collectors.toList());
    }

    public MsClass findController(MsFlowEntity msFlowEntity) {
        return MsCache.msClassList
                .stream()
                .filter(n -> n.getMsId().getPath().equals(msFlowEntity.getMsControllerMethod().getMsId().getPath()))
                .findFirst()
                .get();
    }

    private Optional<MsMethodCall> findServiceCall(MsMethod msControllerMethod) {
        return MsCache.msMethodCallList
                .stream()
                .filter(n -> n.getParentMethodName().equals(msControllerMethod.getMethodName()) && n.getMsId().getPath().equals(msControllerMethod.getMsId().getPath()))
                .findFirst();
    }


}
