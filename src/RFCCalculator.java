import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
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

    HashMap<String, Integer> filesRFCMetric = new HashMap<String, Integer>();

    public RFCCalculator() throws FileNotFoundException {

        File dir = new File("./Files");
        File[] listFiles = dir.listFiles();

        for (File file : listFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));

            List<String> methodRef = new ArrayList<>();
            VoidVisitor<List<String>> RFCMetric = new RFC();
            RFCMetric.visit(cu, methodRef);


            filesRFCMetric.put(file.getName(), methodRef.size());

        }
    }

    public HashMap<String,Integer> getResults() {
        return filesRFCMetric;
    }

        //RFC
        private static class RFC extends VoidVisitorAdapter<List<String>> {

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

    }



