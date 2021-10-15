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

            WMCSimple wmcCalculator = new WMCSimple(dir);
            HashMap<String, Integer> wmcResults = wmcCalculator.getResults();

            //WMC 2 - WEIGHTED
            WMCComplex wmcCalculator2 = new WMCComplex(dir);
            HashMap<String, Integer> wmcComplexResults = wmcCalculator2.getResults();

            //RFC
            RFCCalculator rfcCalc = new RFCCalculator(dir);
            HashMap<String,Integer> rfcResults = rfcCalc.getResults();

            //CBO
             CBOCalculator cboCalc = new CBOCalculator(dir);
             HashMap<String,Integer> cboResults = cboCalc.getResults();



            System.out.format("%10s %20s %25s %20s %20s %20s\n", "CLASS", "WMC", "WMC Complex", "RFC", "LCOM", "CBO");

            for(String s : cboResults.keySet()){

                System.out.format("%10s %20d %25d %20d %20s %20d\n",
                        (s),
                        wmcResults.get(s),
                        wmcComplexResults.get(s),
                        rfcResults.get(s),
                        "-",
                        cboResults.get(s));

            }


    }

}
