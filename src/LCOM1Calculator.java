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
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class LCOM1Calculator {

    HashMap<String,Integer> mapScores = new HashMap<>();

    public LCOM1Calculator(File dir) throws FileNotFoundException {
        File[] listFiles = dir.listFiles();

        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new LCOM1Calculator.MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);

            VoidVisitor<List<Optional<BlockStmt>>> bodyCollector = new LCOM1Calculator.MethodBodyCollector(); // get list of individual methods of class
            List<Optional<BlockStmt>> methodBodies = new ArrayList<>();
            bodyCollector.visit(cu, methodBodies);

            VoidVisitor<List<String>> fieldCollector = new LCOM1Calculator.FieldCollector(); // get instance variables of class
            List<String> instanceVariables = new ArrayList<>();
            fieldCollector.visit(cu, instanceVariables);

            HashMap<String, List<String>> mapElementsToMethods = new HashMap<>();

            for (int i = 0; i < methodBodies.size(); i++) {
                Optional<BlockStmt> currentBody = methodBodies.get(i);
                String currentName = methodNames.get(i);

                try {
                    BlockStmt bodyPlaintext = currentBody.get(); // retrieve BlockStmt from Optional collection

                    NodeList<Statement> statements = bodyPlaintext.getStatements(); // break BlockStmt down into javaparser statements
                    NodeList<Node> statementNodes = new NodeList<>();
                    for (Node statement : statements) { // convert statement list to node list to use node operations
                        statementNodes.add(statement);
                    }

                    List<String> children = new ArrayList<>();
                    List<String> leafNodes = getStatementChildren(statementNodes, children); // get individual elements of all statements

                    List<String> leafNodesCopy = new ArrayList<>(leafNodes);
                    for (String element : leafNodesCopy) {
                        if (!instanceVariables.contains(element)) // remove elements that are not instance variables
                            leafNodes.remove(element);
                    }

                    mapElementsToMethods.put(currentName, leafNodes); // correlate method name with its elements
                } catch (NoSuchElementException e) {
                    mapElementsToMethods.put(currentName, new ArrayList<>()); // empty list for methods with 0 elements
                }
            }

            List<List<String>> methodPairs = createMethodPairs(methodNames);
            int score = calculateLCOM(methodPairs, mapElementsToMethods);

            String fileName = file.getName();
            if (fileName.indexOf(".") > 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

            mapScores.put(fileName, score);
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

    public static List<List<String>> createMethodPairs(List<String> methodNames) { // pair up methods without duplicate/equivalent pairs
        List<List<String>> methodPairs = new ArrayList<>();
        for(int i = 0 ; i < methodNames.size(); i ++){
            for(int j = i+1 ; j < methodNames.size(); j ++){
                List<String> pair = new ArrayList<>();
                pair.add(methodNames.get(i));
                pair.add(methodNames.get(j));
                methodPairs.add(pair);
            }
        }
        return methodPairs;
    }

    public static int calculateLCOM(List<List<String>> methodPairs, HashMap<String,List<String>> map) {
        int score = 0;

        for (List<String> pair : methodPairs) {
            boolean sharedVariables = false;
            String methodA = pair.get(0);
            String methodB = pair.get(1);
            for (String instanceVariable : map.get(methodA)) {
                if (map.get(methodB).contains(instanceVariable) ) { // if method B's instance variable list contains the current instance variable of method A
                    sharedVariables = true;
                }
            }
            if (sharedVariables == true) { // if methods are shared, they are cohesive, so Lack of Cohesion score deceases
                score--;
            }
            else {
                score++;
            }
        }
        if (score < 0) // score should not be less than 0
            score = 0;

        return score;
    }

    public HashMap<String,Integer> getResults() {
        return mapScores;
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }

    private static class MethodBodyCollector extends VoidVisitorAdapter<List<Optional<BlockStmt>>> {

        @Override
        public void visit(MethodDeclaration md, List<Optional<BlockStmt>> collector) {
            super.visit(md, collector);
            Optional<BlockStmt> body = md.getBody();
            collector.add(body);
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
}




