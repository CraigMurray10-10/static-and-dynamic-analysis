import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MethodVisitor {

    private static final String FILE_PATH = "./Files/Animal.java";

    public static void main(String[] args) throws Exception {
        File dir = new File("./Files");
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));



            //WMC 1 - SIMPLE

            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);
            //methodNames.forEach(n -> System.out.println("Method Name Collected: " + n));
            //System.out.println("No. of methods in " + file.getPath() + ": " + methodNames.size());

            //WMC 2 - WEIGHTED

            List<String> methodDecisions = new ArrayList<>();
            VoidVisitor<List<String>> wmc2Metric = new WMC2();
            wmc2Metric.visit(cu, methodDecisions);
            System.out.println("Score of " + file.getPath() + ": " + methodDecisions.size());


            //RFC
            List<String> methodCalls = new ArrayList<>();
            VoidVisitor<List<String>> methodCallCollector = new MethodCallCollector();
            methodCallCollector.visit(cu, methodCalls);
           // methodCalls.forEach(n -> System.out.println("Method Call Collected: " + n));
            //System.out.println("No. of method calls in " + file.getPath() + ": " + methodCalls.size());
        }
    }

    //WMC 1 - SIMPLE
    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }

    //WMC 2 - WEIGHTED

    private static class WMC2 extends VoidVisitorAdapter<List<String>>{

        //gets method declaration
        public void visit(MethodDeclaration md, List<String> collector){
            super.visit(md, collector );
            collector.add(md.getNameAsString());
        }

        //honestly unsure what this does, dont think it does anything
        public void visit(ConditionalExpr cExpr, List<String> collector){
            super.visit(cExpr, collector);
            collector.add(cExpr.toString());
        }

        //checks for loops and adds to list
        public void visit(ForStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());
        }

        //checks for do loops and adds to list
        public void visit(DoStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());
        }

        //checks for each
        public void visit(ForEachStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());
        }

        //checks for ifs
        public void visit(IfStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());

        }

        //checks for switches
        public void visit(SwitchStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());
        }

        //checks for while
        public void visit(WhileStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());
        }



    }

    //RFC
    private static class MethodCallCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodCallExpr mce, List<String> collector) {
            super.visit(mce, collector);
            collector.add(mce.getNameAsString());
        }
    }

}
