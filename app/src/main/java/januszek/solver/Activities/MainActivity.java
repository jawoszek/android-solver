package januszek.solver.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import januszek.solver.Database.Base;
import januszek.solver.Database.Starter;
import januszek.solver.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Base.getInstance().setDB(new Starter(getApplicationContext()).getWritableDatabase());
        Base.getInstance().getAllFromDB();
    }

    public void showList(View view) {
        Intent intent = new Intent(this, ShowListActivity.class);
        startActivity(intent);
    }

    public void addNew(View view) {
        Intent intent = new Intent(this, AddNewActivity.class);
        startActivity(intent);
    }
}
