import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MethodVisitor {

    private static final String FILE_PATH = "./Files/Simulator.java";

    public static void main(String[] args) throws Exception {
        File dir = new File("./Files");
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            //VoidVisitor<Void> methodNameVisitor = new MethodNamePrinter();
            //methodNameVisitor.visit(cu, null);
           // List<String> methodNames = new ArrayList<>();
           // VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
           // methodNameCollector.visit(cu, methodNames);
            //methodNames.forEach(n -> System.out.println("Method Name Collected: " + n));
            //System.out.println("No. of methods in " + file.getPath() + ": " + methodNames.size());

            //List<String> methodCalls = new ArrayList<>();
            //VoidVisitor<List<String>> methodCallCollector = new MethodCallCollector();
            //methodCallCollector.visit(cu, methodCalls);
            //methodCalls.forEach(n -> System.out.println("Method Call Collected: " + n));
            //System.out.println("No. of method calls in " + file.getPath() + ": " + methodCalls.size());

            List<String> coupledClasses = new ArrayList<>();
            VoidVisitor<List<String>> classRefCollector = new ClassRefCollector();
            classRefCollector.visit(cu, coupledClasses);

            coupledClasses.forEach(n -> System.out.println("Coupling detected with class: " + n));


        }
    }

    private static class MethodNamePrinter extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            System.out.println("Method Name Printed: " + md.getName());
        }
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }

    private static class MethodCallCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodCallExpr mce, List<String> collector) {
            super.visit(mce, collector);
            collector.add(mce.getNameAsString());
        }
    }

    private static class ClassRefCollector extends VoidVisitorAdapter<List<String>> {

        //used to check method declarations


        //used to check for variables
        public void visit(VariableDeclarationExpr n, List<String> collector){
            for (VariableDeclarator var : n.getVariables()){

                if(!collector.contains(var.getTypeAsString())){
                    collector.add(var.getTypeAsString());
                }
            }
            super.visit(n, collector);
        }

    }

}
