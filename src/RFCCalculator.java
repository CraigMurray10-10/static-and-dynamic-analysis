import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RFCCalculator {

    HashMap<String, List<String>> filesRFCAllRefs = new HashMap<>();

    public RFCCalculator(File dir) throws FileNotFoundException {

        File[] listFiles = dir.listFiles();


        for(File file : listFiles){
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            VoidVisitor<HashMap<String, List<String>>> classVisitor = new ClassVisitor();
            classVisitor.visit(cu, filesRFCAllRefs);
        }

        int x = 5;

        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            List<String> methodRef = new ArrayList<>();
            VoidVisitor<HashMap<String, List<String>>> RFCMetric = new RFC();
            RFCMetric.visit(cu, filesRFCAllRefs);



        }
    }

    public HashMap<String,Integer> getResults() {

        HashMap<String, Integer> filesRFCMetric = new HashMap<>();

        for(String className : filesRFCAllRefs.keySet()){

            filesRFCMetric.put(className, filesRFCAllRefs.get(className).size());

        }
        return filesRFCMetric;
    }


    private static class ClassVisitor extends VoidVisitorAdapter<HashMap<String, List<String>>> {


        public void visit(ClassOrInterfaceDeclaration cof, HashMap<String, List<String>> s){
            super.visit(cof, s);
            s.putIfAbsent(cof.getNameAsString(), new ArrayList<String>());

        }
    }

        //RFC
        private static class RFC extends VoidVisitorAdapter<HashMap<String, List<String>>> {

        String classname = null;

            @Override
            public void visit(ClassOrInterfaceDeclaration cof, HashMap<String, List<String>> s){
                classname = cof.getNameAsString();
                super.visit(cof, s);
            }

            @Override
            public void visit(MethodCallExpr mce, HashMap<String, List<String>> s) {
                super.visit(mce, s);

                if(!s.get(classname).contains(mce.getNameAsString())){
                    s.get(classname).add(mce.getNameAsString());
                }

            }

            @Override
            public void visit(MethodDeclaration md, HashMap<String, List<String>> s) {
                super.visit(md, s);

                if(!s.get(classname).contains(md.getNameAsString())){
                    s.get(classname).add(md.getNameAsString());
                }
            }

        }

    }



