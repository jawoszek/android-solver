package januszek.solver.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import januszek.solver.Database.Base;
import januszek.solver.Problems.Problem;
import januszek.solver.R;

public class AddNewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
    }

    public void pushNew(View view){
        Problem problem = new Problem(true);
        EditText editFunc = (EditText) findViewById(R.id.editFunc);
        EditText editEqus = (EditText) findViewById(R.id.editEqus);
        EditText editVars = (EditText) findViewById(R.id.editVars);
        String func = editFunc.getText().toString();
        String equs = editEqus.getText().toString();
        String vars = editVars.getText().toString();
        problem.addFunc(func);
        String tab[] = equs.split("\n");
        for (String i:tab) problem.addEq(i);
        problem.addVars(vars);
        Base.getInstance().addProblem(problem);
    }
}
