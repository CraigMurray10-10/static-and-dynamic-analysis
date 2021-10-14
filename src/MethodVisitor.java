import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
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


        System.out.format("%s %32s %16s %16s %16s %16s\n", "Class", "WMC", "WMC(Complex)", "RFC", "CBO", "LCOM");

        // File file = new File("./Files/Field.java");
       // for (File file : listFiles) {

            File file = new File("./Files/MerchantBank.java");
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));


            System.out.print(file.getPath());
            //WMC 1 - SIMPLE

            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);
            //methodNames.forEach(n -> System.out.println("Method Name Collected: " + n));
            //System.out.println("No. of methods in " + file.getPath() + ": " + methodNames.size());

            System.out.format("%16d",methodNames.size());

            //WMC 2 - WEIGHTED

            List<String> methodDecisions = new ArrayList<>();
            VoidVisitor<List<String>> wmc2Metric = new WMC2();
            wmc2Metric.visit(cu, methodDecisions);

            System.out.format("%16d",  methodDecisions.size());
            //System.out.println("WMC count for " + file.getPath() + ": " + methodDecisions.size());


            //RFC
            List<String> methodCalls = new ArrayList<>();
            VoidVisitor<List<String>> methodCallCollector = new MethodCallCollector();
            methodCallCollector.visit(cu, methodCalls);
            System.out.format("%16d", methodCalls.size());

            // methodCalls.forEach(n -> System.out.println("Method Call Collected: " + n));
            //System.out.println("No. of method calls in " + file.getPath() + ": " + methodCalls.size());
            //   }

            //CBO
            List<String> coupledClasses = new ArrayList<>();
            VoidVisitor<List<String>> cBOCalculator = new CBOCalculator();
            cBOCalculator.visit(cu, coupledClasses);
            System.out.format("%16s", coupledClasses.size() );

            System.out.format("%16s \n", "-" );
        }
    //}

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


            Expression expr = cExpr.getCondition();
            collector = this.checkExpression(cExpr.getCondition(), collector);

        }

        //checks for loops and adds to list
        public void visit(ForStmt stmt, List<String> collector){
            super.visit(stmt, collector);

            Expression expr = stmt.asForStmt().getCompare().get();
            collector = this.checkExpression(expr, collector);
            collector.add(stmt.toString());
        }

        //checks for do loops and adds to list
        public void visit(DoStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());

            collector = this.checkExpression(stmt.getCondition(), collector);

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

            //checking for ands / or ors

            //gets condition
            Expression expr = stmt.getCondition();

            collector = this.checkExpression(stmt.getCondition(), collector);
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

            collector = this.checkExpression(stmt.getCondition(), collector);
        }

        //checks try statements

        public void visit(TryStmt stmt, List<String> collector){
            super.visit(stmt, collector);
            collector.add(stmt.toString());



        }

        public List<String> checkExpression(Expression expr, List<String> collector){
            if(expr.isBinaryExpr()){
                if(expr.asBinaryExpr().getOperator().asString() == "||" || expr.asBinaryExpr().getOperator().asString() == "&&"){
                    collector.add(expr.toString());
                }
                if (expr.asBinaryExpr().getLeft().isBinaryExpr()){
                    collector = checkExpression(expr.asBinaryExpr().getLeft(), collector);
                }
                if(expr.asBinaryExpr().getRight().isBinaryExpr()){
                    collector = checkExpression(expr.asBinaryExpr().getLeft(), collector);
                } else if (expr.asBinaryExpr().getRight().isEnclosedExpr()){
                    expr = expr.asBinaryExpr().getRight().asEnclosedExpr().getInner();
                    collector = checkExpression(expr, collector);
                }

            }

            return collector;
        }



    }

    //RFC
    private static class MethodCallCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodCallExpr mce, List<String> collector) {
            super.visit(mce, collector);

            if(!collector.contains(mce.getNameAsString())){
                collector.add(mce.getNameAsString());
            }

        }

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);

            if(!collector.contains(md.getNameAsString())){
                collector.add(md.getNameAsString());
            }

        }

    }

    //CBO

    private static class CBOCalculator extends VoidVisitorAdapter<List<String>> {

        // checks if variables declared in fields are type of other objects
        @Override
        public void visit(FieldDeclaration dExpr, List<String> collector){
            super.visit(dExpr, collector);

            for(VariableDeclarator v : dExpr.getVariables()){

                    Type x = v.getType().getElementType();

                    if(x.isClassOrInterfaceType() && x.asClassOrInterfaceType().getTypeArguments().isPresent()){
                       for(Type t : x.asClassOrInterfaceType().getTypeArguments().get()){
                           if(!collector.contains(t) && !t.isPrimitiveType()){
                               collector.add(t.asString());
                           }
                       }

                    } else {
                        if(!collector.contains(x) && !x.isPrimitiveType()){
                            collector.add(x.asString());
                        }
                    }

            }

        }

        @Override
        public void visit(VariableDeclarationExpr dExpr, List<String> collector){
            super.visit(dExpr, collector);

            List<VariableDeclarator> x= dExpr.getVariables();


            for(VariableDeclarator v : dExpr.getVariables()){
                if(!collector.contains(v.getTypeAsString()) && !v.getType().isPrimitiveType() && v.getType().isClassOrInterfaceType()){
                    collector.add(v.getTypeAsString());
                }
            }


        }












    }


}
