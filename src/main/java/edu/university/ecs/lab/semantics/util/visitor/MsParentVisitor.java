package edu.university.ecs.lab.semantics.util.visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import edu.university.ecs.lab.semantics.entity.graph.MsParentMethod;

import java.util.Optional;

public class MsParentVisitor {

    public static MsParentMethod getMsParentMethod(MethodCallExpr n) {
        MsParentMethod msParentMethod = new MsParentMethod();
        Optional<Node> parentNode = n.getParentNode();
        while (parentNode.isPresent() && !(parentNode.get() instanceof MethodDeclaration)) {
            parentNode = parentNode.get().getParentNode();
        }
        if (parentNode.isPresent()) {
            // Set Method
            MethodDeclaration md = (MethodDeclaration) parentNode.get();
            msParentMethod.setParentMethodName(md.getName().getIdentifier());
            //Find Class
            while (parentNode.isPresent() && !(parentNode.get() instanceof ClassOrInterfaceDeclaration)) {
                parentNode = parentNode.get().getParentNode();
            }
            if (parentNode.isPresent()) {
                // Set Class
                ClassOrInterfaceDeclaration cl = (ClassOrInterfaceDeclaration) parentNode.get();
                msParentMethod.setParentClassName(cl.getName().getIdentifier());
                // Find Package
                parentNode = parentNode.get().getParentNode();
                if (parentNode.isPresent() && parentNode.get() instanceof CompilationUnit) {
                    // Set Package
                    CompilationUnit cu = (CompilationUnit) parentNode.get();
                    Optional<PackageDeclaration> pd = cu.getPackageDeclaration();
                    if (pd.isPresent()) {
                        msParentMethod.setParentPackageName(pd.get().getNameAsString());
                    }
                } else {
                	System.err.println("Cannot get the Package Declaration");
                }
            }
        }
        return msParentMethod;
    }

    public static MsParentMethod getMsParentMethod(FieldDeclaration n){
        MsParentMethod msParentMethod = new MsParentMethod();
        Optional<Node> parentNode = n.getParentNode();

        //Find Class
        while (parentNode.isPresent() && !(parentNode.get() instanceof ClassOrInterfaceDeclaration)) {
            parentNode = parentNode.get().getParentNode();
        }
        if (parentNode.isPresent()) {
            // Set Class
            ClassOrInterfaceDeclaration cl = (ClassOrInterfaceDeclaration) parentNode.get();
            msParentMethod.setParentClassName(cl.getName().getIdentifier());
            // Find Package
            parentNode = parentNode.get().getParentNode();
            if (parentNode.isPresent()) {
                // Set Package
                CompilationUnit cu = (CompilationUnit) parentNode.get();
                Optional<PackageDeclaration> pd = cu.getPackageDeclaration();
                if (pd.isPresent()) {
                    msParentMethod.setParentPackageName(pd.get().getNameAsString());
                }
            }
        }

        return msParentMethod;
    }






}
