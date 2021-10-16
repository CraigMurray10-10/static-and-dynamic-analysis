import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class MethodVisitor {

    private static final String FILE_PATH = "./Files/Animal.java";

    public static void main(String[] args) throws Exception {
        File dir = new File("./Files");
        File[] listFiles = dir.listFiles();

        // File file = new File("./Files/Field.java");
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

            //LCOM1
            VoidVisitor<List<Optional<BlockStmt>>> bodyCollector = new MethodBodyCollector(); // get list of individual methods of class
            List<Optional<BlockStmt>> methodBodies = new ArrayList<>();
            bodyCollector.visit(cu, methodBodies);

            VoidVisitor<List<String>> fieldCollector = new FieldCollector();
            List<String> instanceVariables = new ArrayList<>();
            fieldCollector.visit(cu, instanceVariables);

            System.out.println("instance variables: " + instanceVariables);
            for (Optional<BlockStmt> body : methodBodies) {
                try {
                    //String bodyPlaintext = body.get().toString();
                    BlockStmt bodyPlaintext = body.get();
                    System.out.println("Block: " + bodyPlaintext);
                    NodeList<Statement> statements = bodyPlaintext.getStatements(); // break BlockStmt down into javaparser statements
                    NodeList<Node> statementNodes = new NodeList<>();
                    for (Node statement : statements) { // convert statement list to node list to use node operations
                        statementNodes.add(statement);
                    }
                    System.out.println("Statements: " + statements);
                    List<String> children = new ArrayList<>();
                    List<String> leafNodes = getStatementChildren(statementNodes, children); // get individual elements of all statements
                    System.out.println("LeafNodes: " + leafNodes);
                } catch(NoSuchElementException e) {
                    System.out.println("Method body is empty");
                }
            }
        }
    }

    public static List<String> getStatementChildren(List<Node> nodes, List<String> leafNodes) {
        List<Node> listChildren = new ArrayList<>();
        for (Node node : nodes) {
            listChildren = node.getChildNodes();
            if (listChildren.isEmpty()) { // if node has no children, it is a leaf node
                leafNodes.add(node.toString());
            }
            else { // if node has children, call recursively to expand them
                getStatementChildren(listChildren, leafNodes);
            }
        }
        return leafNodes;
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
            collector.add(mce.getNameAsString());
        }
    }

    // LCOM1

    private static class MethodBodyCollector extends VoidVisitorAdapter<List<Optional<BlockStmt>>> {

        @Override
        public void visit(MethodDeclaration md, List<Optional<BlockStmt>> collector) {
            super.visit(md, collector);
            Optional<BlockStmt> body = md.getBody();
            if (!body.isEmpty()) {
                collector.add(body);
            }
        }
    }

    private static class FieldCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(FieldDeclaration fd, List<String> collector) {
            super.visit(fd, collector);
            List<VariableDeclarator> instanceVariables = fd.getVariables();
            for (VariableDeclarator vd : instanceVariables) {
                collector.add(vd.getNameAsString());
            }
        }
    }

    private static class SimpleNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(SimpleName sn, List<String> collector) {
            super.visit(sn, collector);
            collector.add(sn.getIdentifier());
        }
    }
}
