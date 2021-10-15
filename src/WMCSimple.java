import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WMCSimple {

    HashMap<String, List<String>> filesWMCAllCalls = new HashMap<>();

    public WMCSimple(File dir) throws FileNotFoundException {


        File[] listFiles = dir.listFiles();


        //gets all classes and adds as key to hashmap

        for(File file : listFiles){
            System.out.println(file.getPath());
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            VoidVisitor<HashMap<String, List<String>>> classVisitor = new ClassVisitor();
            classVisitor.visit(cu, filesWMCAllCalls);
        }


        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));


            VoidVisitor<HashMap<String, List<String>>> methodNameCollector = new WMCSimple.MethodNameCollector();
            methodNameCollector.visit(cu, filesWMCAllCalls);

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

    private static class MethodNameCollector extends VoidVisitorAdapter<HashMap<String, List<String>>> {

        String classname = "";

        @Override
        public void visit(ClassOrInterfaceDeclaration cof, HashMap<String, List<String>> s){
            classname = cof.getNameAsString();
            super.visit(cof, s);


        }

        @Override
        public void visit(MethodDeclaration md, HashMap<String, List<String>> s) {
            s.get(classname).add(md.getNameAsString());
            super.visit(md, s);
        }
    }


}
