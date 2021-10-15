import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CBOCalculator {

    //class names & dependencies
    HashMap<String, List<String>> filesCBOMetric = new HashMap<String, List<String>>();

    public CBOCalculator(File dir) throws FileNotFoundException {

        File[] listFiles = dir.listFiles();

        //so this one iterates through all the files first, and uses a class
        //visitor to add all class names as keys. This will be used to keep
        //track of the classes that reference each other & is just setting this
        //up for CBO visitor
        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            VoidVisitor<HashMap<String, List<String>>> classVisitor = new ClassVisitor();
            classVisitor.visit(cu, filesCBOMetric);



        }

        //this then goes through each file again, filling hashmaps with approp classes.
        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            VoidVisitor<HashMap<String, List<String>>> CBOMetric = new CBOVisitor();
            CBOMetric.visit(cu, filesCBOMetric);
        }
    }

    public HashMap<String, Integer> getResults() {
        HashMap<String, Integer> sizeLists = new HashMap<>();

        for(String k : filesCBOMetric.keySet()){
            sizeLists.put(k, filesCBOMetric.get(k).size());
        }
        return sizeLists;
    }

    private static class ClassVisitor extends VoidVisitorAdapter<HashMap<String, List<String>>> {


        public void visit(ClassOrInterfaceDeclaration cof, HashMap<String, List<String>> s){
            super.visit(cof, s);
            s.putIfAbsent(cof.getNameAsString(), new ArrayList<String>());

        }
    }

    private static class CBOVisitor extends VoidVisitorAdapter<HashMap<String, List<String>>> {
        String classname = null;

        //gets class name for this iteration of the visitor
        //this sets class name so we know what key to .get() when adding other references.
        public void visit(ClassOrInterfaceDeclaration cof, HashMap<String, List<String>> s) {
            classname = cof.getNameAsString();
            super.visit(cof, s);

        }

        public void visit(FieldDeclaration fd, HashMap<String, List<String>> s){
            super.visit(fd, s);

            if((!s.get(classname).contains(fd.getElementType().asString())) && (s.containsKey(fd.getElementType().asString()))){
                s.get(classname).add(fd.getElementType().asString());
                String f = fd.getElementType().asString();
                s.get(fd.getElementType().asString()).add(classname);
            }
        }

        public void visit(MethodDeclaration md, HashMap<String, List<String>> s) {
            super.visit(md, s);

            if (s.containsKey(md.getTypeAsString()) && !s.get(classname).contains(md.getTypeAsString())) {
                s.get(classname).add(md.getTypeAsString());
                s.get(md.getTypeAsString()).add(classname);

            }
            for (Parameter p : md.getParameters()) {

                if(s.containsKey(p.getTypeAsString()) && !s.get(classname).contains(p.getTypeAsString())){
                    s.get(classname).add(p.getTypeAsString());
                    s.get(p.getTypeAsString()).add(classname);
                }

            }
        }


        public void visit(VariableDeclarationExpr vd, HashMap<String, List<String>> s){
            super.visit(vd, s);


            if(s.containsKey(vd.getElementType().asString()) && !s.get(classname).contains(vd.getElementType().asString())) {
                s.get(classname).add(vd.getElementType().asString());
                s.get(vd.getElementType().asString()).add(classname);
            }

        }



    }
}


