import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
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


    HashMap<String, Integer> filesWMCMetric = new HashMap<String, Integer>();

    public WMCSimple() throws FileNotFoundException {

        File dir = new File("./Files");
        File[] listFiles = dir.listFiles();

        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new WMCSimple.MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);


            filesWMCMetric.put(file.getName(), methodNames.size());

        }


        }

        public HashMap<String, Integer> getResults(){
                return filesWMCMetric;
        }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }


    }



