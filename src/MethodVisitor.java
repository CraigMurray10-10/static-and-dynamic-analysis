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
        File dir = new File("./Files");
        File[] listFiles = dir.listFiles();


       // System.out.format("%s %32s %16s %16s %16s %16s\n", "Class", "WMC", "WMC(Complex)", "RFC", "CBO", "LCOM");

        // File file = new File("./Files/Field.java");
        // for (File file : listFiles) {

           // File file = new File("./Files/MerchantBank.java");
          //  CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));


           // System.out.print(file.getPath());
            //WMC 1 - SIMPLE

            WMCSimple wmcCalculator = new WMCSimple();
            HashMap<String, Integer> wmcResults = wmcCalculator.getResults();

            for(String s : wmcResults.keySet()){
                //System.out.println("File Name: " + s + " WMC Result: " + wmcResults.get(s));
            }

            //WMC 2 - WEIGHTED
             WMCComplex wmcCalculator2 = new WMCComplex();
             HashMap<String, Integer> wmcComplexResults = wmcCalculator2.getResults();
            for(String s : wmcComplexResults.keySet()){
                 // System.out.println("File Name: " + s + " WMC Result: " + wmcComplexResults.get(s));
            }

            //RFC

             RFCCalculator rfcCalc = new RFCCalculator();
            HashMap<String,Integer> rfcResults = rfcCalc.getResults();
            for(String s : rfcResults.keySet()){
                //System.out.println("File Name: " + s + " RFC Result: " + rfcResults.get(s));

              }

            //CBO

        CBOCalculator cboCalc = new CBOCalculator();
        HashMap<String,Integer> cboResults = cboCalc.getResults();
        for(String s : cboResults.keySet()){
            System.out.println("File Name: " + s + " CBO Result: " + cboResults.get(s));

        }


        /*
            //CBO
            List<String> coupledClasses = new ArrayList<>();
            VoidVisitor<List<String>> cBOCalculator = new CBOCalculator();
            cBOCalculator.visit(cu, coupledClasses);
           // System.out.format("%16s", coupledClasses.size() );


         */
           // System.out.format("%16s \n", "-" );
      //  }
    }



    //CBO

    /*
    private static class CBOCalculator extends VoidVisitorAdapter<List<String>> {



        @Override
        public void visit(VariableDeclarationExpr dExpr, List<String> collector){
            super.visit(dExpr, collector);

            for(VariableDeclarator v : dExpr.getVariables()){
                if(!collector.contains(v.getTypeAsString()) && !v.getType().isPrimitiveType() && v.getType().isClassOrInterfaceType()){
                    collector.add(v.getTypeAsString());
                }
            }
        }

        @Override
        public void visit(MethodDeclaration md, List<String> collector){
            super.visit(md, collector);

            if(!collector.contains(md.getTypeAsString())){
                collector.add(md.getTypeAsString());
            }

            for(Parameter p : md.getParameters()){
                if(!collector.contains(p.getTypeAsString())){
                    collector.add(p.getTypeAsString());
                }
            }
        }

     */












  //  }


}
