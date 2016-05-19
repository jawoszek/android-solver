package januszek.solver.Problems;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import januszek.solver.Database.Base;

/**
 * Created by Januszek on 2016-04-08.
 */
public class Problem {

    public static Problem sideStep;
    public boolean isBasic;
    public boolean isMin;
    public int id;
    public int varEqCount;
    public int varCount;
    public int equCount;
    public List<Equation> equations;
    public List<Double> function;
    public List<String> varSigns;
    public List<Double> varVals;
    public List<Integer> nrs;
    public Problem dual;

    public Problem(boolean basic){
        equCount = 0;
        varCount = 0;
        varEqCount = 0;
        isBasic = basic;
        nrs = new ArrayList<>();
        equations = new ArrayList<>();
        function = new ArrayList<>();
        varSigns = new ArrayList<>();
        varVals = new ArrayList<>();
        id = 0;
    }

    public void setID(int id){
        this.id = id;
    }

    public Equation addEq(String line){
        Equation equation = new Equation(line);
        equations.add(equation);
        equCount++;
        return equation;
    }

    public void addFunc(String line){
        Pattern pattern = Pattern.compile("^([\\d\\.\\+\\-\\sx]*)(->)([\\sminax]*)$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()){
            Pattern patternIns = Pattern.compile("(\\s*[\\+\\-]\\s*[\\d\\.]+)\\s*x\\d+");
            Matcher matcherIns =  patternIns.matcher(matcher.group(1));
            while (matcherIns.find()){
                function.add(Double.parseDouble(matcherIns.group(1).replaceAll("\\s+","")));
                varCount++;
            }
            if(matcher.group(3).contains("min")) isMin = true; else isMin = false;
        }
    }

    public void addVars(String line){
        for(int i = 1;i<varCount+1;i++){
            Pattern pattern = Pattern.compile("\\s*x"+i+"([^\\s\\dx]+)\\s*([\\d\\.]+)\\s*");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()){
                varSigns.add(matcher.group(1));
                varVals.add(Double.parseDouble(matcher.group(2)));
                nrs.add(i);
                varEqCount++;
                while(matcher.find()){
                    varSigns.add(matcher.group(1));
                    varVals.add(Double.parseDouble(matcher.group(2)));
                    nrs.add(i);
                    varEqCount++;
                }
            }else{
                varSigns.add("any");
                varVals.add(0.0);
                nrs.add(i);
                varEqCount++;
            }
        }
    }

    public String getVars(){
        StringBuilder sB = new StringBuilder();
        for (int i=1;i<varEqCount+1;i++){
            if (i!=varEqCount){
                if(varSigns.get(i-1).equals("any")){
                    sB.append("x"+nrs.get(i-1)+"-any,");
                }else{
                    sB.append("x"+nrs.get(i-1)+varSigns.get(i-1)+varVals.get(i-1)+",");
                }
            }else{
                if(varSigns.get(i-1).equals("any")){
                    sB.append("x"+nrs.get(i-1)+"-any");
                }else{
                    sB.append("x"+nrs.get(i-1)+varSigns.get(i-1)+varVals.get(i-1));
                }
            }
        }
        return sB.toString();
    }

    public String getFunc(){
        StringBuilder sB = new StringBuilder();
        for (int i=0;i<function.size();i++){
            if(i!=function.size()-1){
                sB.append(function.get(i)+"x"+(i+1)+"+");
            }else{
                sB.append(function.get(i)+"x"+(i+1));
            }
        }
        sB.append("->");
        if (isMin) sB.append("min"); else sB.append("max");
        return sB.toString();
    }

    public List<String> getEqus(){
        List<String> list = new ArrayList<>();
        for (Equation i:equations){
            list.add(i.getText());
        }
        return list;
    }

    public String getEqusString(){
        StringBuilder sB = new StringBuilder();
        for (int i=0;i<equations.size();i++){
            if (i!=equations.size()-1) sB.append(equations.get(i).getText()+"\n"); else sB.append(equations.get(i).getText());
        }
        return sB.toString();
    }

    public Problem revertProblem(){
        Problem newP = new Problem(false);
        if (this.isMin) newP.isMin=false; else newP.isMin=true;
        newP.varCount=this.equCount;
        newP.equCount=this.varCount;
        for (int i = 0;i<this.equations.size();i++){
            if ((this.equations.get(i).sign.equals("<=")&&!this.isMin)||((this.equations.get(i).sign.equals(">=")&&this.isMin))){
                newP.varSigns.add(">=");
                newP.varVals.add(0.0);
                newP.nrs.add(i+1);
            }else if (!this.equations.get(i).sign.equals("=")){
                newP.varSigns.add("<=");
                newP.varVals.add(0.0);
                newP.nrs.add(i+1);
            }
        }
        for (int i=0;i<this.varCount;i++){
            int j = -1;
            for (int k =0;k<this.varEqCount;k++){
                if (nrs.get(k)==(i+1)){
                    j = k;
                    break;
                }
            }
            Equation eq = new Equation();
            if (this.varSigns.get(j).equals(">=")||this.varSigns.get(j).equals(">")){
                if (newP.isMin){
                    eq.sign=">=";
                }else{
                    eq.sign="<=";
                }
            }
            if (this.varSigns.get(j).equals("=")||this.varSigns.get(j).equals("any")){
                eq.sign="=";
            }
            if (this.varSigns.get(j).equals("<=")||this.varSigns.get(j).equals("<")){
                if (newP.isMin){
                    eq.sign="<=";
                }else{
                    eq.sign=">=";
                }
            }
            eq.end=this.function.get(nrs.get(j)-1);
            for (int g = 0;g<this.equCount;g++){
                eq.equation.add(this.equations.get(g).equation.get(nrs.get(j)-1));
            }
            newP.equations.add(eq);
        }
        for(int g=0;g<newP.varCount;g++){
            newP.function.add(this.equations.get(g).end);
        }
        newP.id = -1;
        this.dual = newP;
        newP.dual = this;
        return newP;
    }

    public List<Double> solveProblem(){
        List<Double> list = new ArrayList<>();
        if (varCount!=2||equations.size()<2){
            return list;
        }
        double szukana = 0;
        Double bufor = null;
        double x1s=0;
        double x2s=0;
        boolean flaga = true;
        Log.e("e","eq.size:"+equations.size());
        for(int i =0;i<equations.size();i++){
            for(int j = 0;j<equations.size();j++){
                if (i==j) continue;
                if (-equations.get(j).equation.get(0)/equations.get(j).equation.get(1)==(-equations.get(i).equation.get(0)/equations.get(i).equation.get(1))) {
                    continue;
                }
                double x1 = ((equations.get(j).end/equations.get(j).equation.get(1))-(equations.get(i).end/equations.get(i).equation.get(1)))/((-equations.get(i).equation.get(0)/equations.get(i).equation.get(1))-(-equations.get(j).equation.get(0)/equations.get(j).equation.get(1)));
                double x2 = (equations.get(j).end-equations.get(j).equation.get(0)*x1)/equations.get(j).equation.get(1);
                flaga = true;
                Log.e("e","x1:"+x1+"x2:"+x2);
                for (int r=0;r<varEqCount;r++){
                    if(nrs.get(r)==1){
                        if(varSigns.get(r).equals(">=")&&(x1<varVals.get(r))) {flaga = false; break;}
                        if(varSigns.get(r).equals("<=")&&(x1>varVals.get(r))) {flaga = false; break;}
                    }else if (nrs.get(r)==2){
                        if(varSigns.get(r).equals(">=")&&(x2<varVals.get(r))) {flaga = false; break;}
                        if(varSigns.get(r).equals("<=")&&(x2>varVals.get(r))) {flaga = false; break;}
                    }
                }
                if (flaga){
                    szukana = function.get(0)*x1+function.get(1)*x2;
                    if ((bufor==null)||(isMin&&szukana<bufor)||(!isMin&&bufor<szukana)){
                        bufor = szukana;
                        x1s=x1;
                        x2s=x2;
                    }
                }

            }
        }
        list.add(bufor);
        list.add(x1s);
        list.add(x2s);
        return list;
    }

    public List<Integer> getZeros(List<Double> score){
        List<Integer> list = new ArrayList<>();
        double x1 = score.get(1);
        double x2 = score.get(2);
        for (int i=0;i<equations.size();i++){
            Equation eq = equations.get(i);
            if (eq.sign.equals(">=")||eq.sign.equals(">")){
                Log.e("e",eq.equation.get(0)*x1+eq.equation.get(1)*x2+">="+eq.end);
                if ((eq.equation.get(0)*x1+eq.equation.get(1)*x2)>eq.end) list.add(i);
            }
            if (eq.sign.equals("<=")||eq.sign.equals("<")){
                if ((eq.equation.get(0)*x1+eq.equation.get(1)*x2)<eq.end) list.add(i);
            }
        }

        return list;
    }

}
