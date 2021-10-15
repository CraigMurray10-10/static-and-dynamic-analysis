import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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

    HashMap<String, List<String>> filesWMCAllCalls = new HashMap<>();

    public WMCComplex(File dir) throws FileNotFoundException {
        File[] listFiles = dir.listFiles();


        for(File file : listFiles){
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            VoidVisitor<HashMap<String, List<String>>> classVisitor = new ClassVisitor();
            classVisitor.visit(cu, filesWMCAllCalls);
        }



        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            VoidVisitor<HashMap<String, List<String>> > wmc2Metric = new WMC2();
            wmc2Metric.visit(cu, filesWMCAllCalls);


        }
    }

    public HashMap<String, Integer> getResults(){

        HashMap<String, Integer> filesWMCMetric = new HashMap<>();

        for(String className : filesWMCAllCalls.keySet()){

            filesWMCMetric.put(className, filesWMCAllCalls.get(className).size());

        }
        return filesWMCMetric;
    }

    private static class ClassVisitor extends VoidVisitorAdapter<HashMap<String, List<String>>> {


        public void visit(ClassOrInterfaceDeclaration cof, HashMap<String, List<String>> s){
            super.visit(cof, s);
            s.putIfAbsent(cof.getNameAsString(), new ArrayList<String>());

        }
    }


        private static class WMC2 extends VoidVisitorAdapter<HashMap<String, List<String>> > {

            String classname = "";

            @Override
            public void visit(ClassOrInterfaceDeclaration cof, HashMap<String, List<String>> s){
                classname = cof.getNameAsString();
                super.visit(cof, s);
            }


            //gets method declaration
            public void visit(MethodDeclaration md, HashMap<String, List<String>> s){
                super.visit(md, s );
                s.get(classname).add(md.getNameAsString());
            }


            //honestly unsure what this does, dont think it does anything
            public void visit(ConditionalExpr cExpr, HashMap<String, List<String>> s){
                super.visit(cExpr, s);
                s.get(classname).add(cExpr.toString());

                Expression expr = cExpr.getCondition();
                List<String> replace = this.checkExpression(cExpr.getCondition(), s.get(classname));

                s.replace(classname, s.get(classname), replace);
            }

            //checks for loops and adds to list
            public void visit(ForStmt stmt, HashMap<String, List<String>> s){
                super.visit(stmt, s);

                Expression expr = stmt.asForStmt().getCompare().get();
                List<String> collector = this.checkExpression(expr, s.get(classname));
                collector.add(stmt.toString());
                s.replace(classname, s.get(classname), collector);

            }

            //checks for do loops and adds to list
            public void visit(DoStmt stmt, HashMap<String, List<String>> s){
                super.visit(stmt, s);
                s.get(classname).add(stmt.toString());

                List<String> collector = this.checkExpression(stmt.getCondition(), s.get(classname));
                s.replace(classname, s.get(classname), collector);
            }

            //checks for each
            public void visit(ForEachStmt stmt, HashMap<String, List<String>> s){
                super.visit(stmt, s);
                s.get(classname).add(stmt.toString());
            }

            //checks for ifs
            public void visit(IfStmt stmt, HashMap<String, List<String>> s){
                super.visit(stmt, s);
                s.get(classname).add(stmt.toString());

                //checking for ands / or ors

                //gets condition
                Expression expr = stmt.getCondition();

                List<String> collector = this.checkExpression(stmt.getCondition(), s.get(classname));
                s.replace(classname, s.get(classname), collector);

            }

            //checks for switches
            public void visit(SwitchStmt stmt, HashMap<String, List<String>> s){
                super.visit(stmt, s);
                s.get(classname).add(stmt.toString());
            }

            //checks for while
            public void visit(WhileStmt stmt, HashMap<String, List<String>> s) {
                super.visit(stmt, s);
                s.get(classname).add(stmt.toString());

                List<String> collector = this.checkExpression(stmt.getCondition(), s.get(classname));
                s.replace(classname, s.get(classname), collector);
            }
            //checks try statements

            public void visit(TryStmt stmt, HashMap<String, List<String>> s){
                super.visit(stmt,s);
                s.get(classname).add(stmt.toString());



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



