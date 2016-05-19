package januszek.solver.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import januszek.solver.Problems.Problem;
import januszek.solver.R;

public class SingleProblem extends AppCompatActivity {
    TextView singleFunc;
    TextView singleEqus;
    TextView singleVars;
    TextView singleScore;
    TextView singleDual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_problem);
        singleFunc = (TextView)findViewById(R.id.single_func);
        singleEqus = (TextView)findViewById(R.id.single_equs);
        singleVars = (TextView)findViewById(R.id.single_vars);
        singleDual = (TextView)findViewById(R.id.dual_text);
        singleScore = (TextView)findViewById(R.id.score);
        Log.e("e","varCount:"+Problem.sideStep.varCount);
        Log.e("e","eqCount"+Problem.sideStep.equCount);
        if (Problem.sideStep.id>-1) singleDual.setText("Problem pierwotny"); else singleDual.setText("Problem dualny");
        singleFunc.setText(Problem.sideStep.getFunc());
        Log.e("e",Problem.sideStep.getFunc());
        singleEqus.setText(Problem.sideStep.getEqusString());
        Log.e("e",Problem.sideStep.getEqusString());
        singleVars.setText(Problem.sideStep.getVars());
        Log.e("e",Problem.sideStep.getVars());
    }

    public void fillScore(View v){
        List<Double> list = Problem.sideStep.solveProblem();
        if (list.size()==3) singleScore.setText("Score:"+list.get(0)+"  x1:"+list.get(1)+"   x2:"+list.get(2)); else{
            singleScore.setText("Can't evaluate");
        }
    }

    public void showPlot(View v){
        if (Problem.sideStep.varCount==2&&Problem.sideStep.equCount>=2) {
            Intent intent = new Intent(this, PlotActivity.class);
            startActivity(intent);
        }else{
            singleScore.setText("Plot inaccessible!");
        }
    }

    public void switchProblem(View v){
        if(Problem.sideStep.dual == null) Problem.sideStep = Problem.sideStep.revertProblem(); else{
            Problem.sideStep = Problem.sideStep.dual;
        }
        Intent intent = new Intent(this, SingleProblem.class);
        startActivity(intent);
        finish();
    }

    public void getHard(View v){
        Problem pr = Problem.sideStep;
        if (!pr.isBasic||pr.equations.size()!=2||(pr.varCount!=3&&pr.varCount!=4)) {
            singleScore.setText("Can't evaluate");
            return;
        }
        if (pr.dual==null) pr.dual = pr.revertProblem();
        List<Double> score = pr.dual.solveProblem();
        List<Integer> zeros = pr.dual.getZeros(score);
        if (zeros.size()>0)singleScore.setText("zerujemy x"+zeros.get(0));else
            singleScore.setText("nie zerujemy");
    }
}
