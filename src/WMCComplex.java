import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WMCComplex {

    HashMap<String, Integer> filesWMCMetric = new HashMap<String, Integer>();

    public WMCComplex() throws FileNotFoundException {

        File dir = new File("./Files");
        File[] listFiles = dir.listFiles();

        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            List<String> methodDecisions = new ArrayList<>();
            VoidVisitor<List<String>> wmc2Metric = new WMC2();
            wmc2Metric.visit(cu, methodDecisions);


            filesWMCMetric.put(file.getName(), methodDecisions.size());

        }
    }

    public HashMap<String, Integer> getResults(){
        return filesWMCMetric;
    }

        private static class WMC2 extends VoidVisitorAdapter<List<String>> {

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

    }



