package edu.university.ecs.lab.semantics.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import edu.university.ecs.lab.semantics.entity.graph.MsClassRoles;
import edu.university.ecs.lab.semantics.entity.graph.MsId;
import edu.university.ecs.lab.semantics.util.visitor.MsVisitor;

public class ProcessFiles {

    /*
     * Core logic for exploring a directory, recursively finding files that end in .java and seperating them based on role
     * then uses JavaParser's VoidVisitorAdapter within MsVisitor to pick apart methods, fields, etc within 
     * services, repositories and controllers
     */
    public static void processFile(File projectDir) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
//            System.out.println(path);
//            System.out.println(Strings.repeat("=", path.length()));
            MsClassRoles role = null;
            if (path.contains("Controller") && (!path.contains("Test"))){
                role = MsClassRoles.CONTROLLER;
            }
            if (path.contains("Service") && (!path.contains("Test"))) {
                role = MsClassRoles.SERVICE;
            }
            if (path.contains("Repository") && (!path.contains("Test"))) {
                role = MsClassRoles.REPOSITORY;
            }
            MsId msId = new MsId(path);
            if (role != null) {
                if (role.equals(MsClassRoles.CONTROLLER) || role.equals(MsClassRoles.SERVICE)) {
                    // CLASS
                    MsVisitor.visitClass(file, path, role, msId);
                    // METHOD
                    MsVisitor.visitMethods(file, role, path, msId);
                    // METHOD CALLS
                    MsVisitor.visitMethodCalls(file, path, msId);
                    // FIELDS
                    MsVisitor.visitFields(file, path, msId);
                } else if (role.equals(MsClassRoles.REPOSITORY)){
                    // CLASS
                    MsVisitor.visitClass(file, path, role, msId);
                    // METHOD
                    MsVisitor.visitMethods(file, role, path, msId);
                } else if (path.contains("entity")) {
                    // visitFieldDeclaration
                }
            } else {
//                System.out.println();
            }
        }).explore(projectDir);
        // PRINT CACHE
    }

    public static void run(String path) {

        String myDirectoryPath = path;
        File file = new File(myDirectoryPath);

        // Filter directories out if they contain "ts accept them"
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                boolean isDirectory = new File(current, name).isDirectory();
                boolean isModule = name.contains("ts");
                return isDirectory && isModule;
            }
        });
        MsCache.modules = Arrays.asList(directories);
        File projectDir = new File(path);

        // processFile more like processDirectory selected that contains "ts"
        processFile(projectDir);
//        System.out.println();
    }
}