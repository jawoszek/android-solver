package januszek.solver.Problems;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Januszek on 2016-04-08.
 */
public class Equation {

    public List<Double> equation;
    public String sign;
    public double end;

    public Equation(){
        equation = new ArrayList<>();
    }

    public Equation(String line){
        String beg="";
        String sign="";
        double end=0;
        equation = new ArrayList<>();
        Pattern pattern = Pattern.compile("^([\\d\\.\\+\\-\\sx]*)([^\\d\\.\\+\\-\\sx]+)\\s*([\\d\\.]+)\\s*$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            beg = matcher.group(1);
            sign = matcher.group(2);
            end = Double.parseDouble(matcher.group(3));

        }

        Pattern patternIns = Pattern.compile("(\\s*[\\+\\-]\\s*[\\d\\.]+)\\s*x\\d+");
        Matcher matcherIns =  patternIns.matcher(beg);
        while (matcherIns.find()){
            equation.add(Double.parseDouble(matcherIns.group(1).replaceAll("\\s+","")));
        }
        this.sign=sign;
        this.end=end;
    }

    public List<Double> getList(){
        return equation;
    }

    public String getSign(){
        return sign;
    }

    public double getEnd(){
        return end;
    }

    public String getText(){
        StringBuilder text = new StringBuilder();
        for (int i=0;i<equation.size();i++){
            text.append(equation.get(i)+"x"+(i+1));
            if (i!=equation.size()-1) text.append("+");
        }
        text.append(sign);
        text.append(end);
        return text.toString();
    }

    public int checkEqu(List<Double> input){
        if (input.size()!=equation.size()) return -2;
        int check = -1;
        double sum = 0.0;
        for (int i=0;i<equation.size();i++){
            sum += equation.get(i)*input.get(i);
        }
        if (sign.equals(">=")){
            if (sum>end) check = 1;
            if (sum==end) check = 0;
            if (sum<end) check = -1;
        }else
        if (sign.equals("<=")){
            if (sum<end) check = 1;
            if (sum==end) check = 0;
            if (sum>end) check = -1;
        }else
        if (sign.equals(">")){
            if (sum>end) check = 1;
            if (sum<=end) check = -1;
        }else
        if (sign.equals("<")){
            if (sum<end) check = 1;
            if (sum>=end) check = -1;
        }else
        if (sign.equals("=")){
            if (sum==end) check = 0; else check = -1;
        }
        return check;
    }

    public double getUpperBorder(int cVar){
        double curr = 0.0;
        return 0.0;
    }

    public double getLowerBorder(int cVar){
        return 0.0;
    }
}
