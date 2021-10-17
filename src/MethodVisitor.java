import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MethodVisitor {

    private static final String FILE_PATH = "./Files/Animal.java";

    public static void main(String[] args) throws Exception {
        File dir = new File("./Files/WeblogFiles");

        //WMC 1 - SIMPLE

        LCOM1Calculator lcomCalculator = new LCOM1Calculator(dir);
        HashMap<String, Integer> lcomResults = lcomCalculator.getResults();

        for (String key : lcomResults.keySet()) {
            System.out.println(key + ": " + lcomResults.get(key));
        }
    }

}
