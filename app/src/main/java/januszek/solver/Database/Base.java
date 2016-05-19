package januszek.solver.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import januszek.solver.Problems.Equation;
import januszek.solver.Problems.Problem;

/**
 * Created by Januszek on 2016-04-09.
 */
public class Base {

    private List<Problem> problems;
    private SQLiteDatabase db;

    private static Base ourInstance = new Base();

    public static Base getInstance() {
        return ourInstance;
    }

    private Base() {
        problems = new ArrayList<>();
    }

    public void setDB(SQLiteDatabase db){
        this.db = db;
    }

    public void closeDB(){
        db.close();
    }

    public SQLiteDatabase getDB(){
        return db;
    }

    public List<Problem> getProblems(){
        return problems;
    }

    public void addProblem(Problem problem){
        problems.add(problem);
        int id = insertProblemDB(problem);
        problem.setID(id);
    }

    public int insertProblemDB(Problem problem){
        int id = 0;
        SQLiteDatabase db = Base.getInstance().getDB();
        ContentValues insertValues = new ContentValues();
        if (problem.isMin) insertValues.put("target", "min"); else insertValues.put("target","max");
        id = (int) db.insert("problems",null,insertValues);
        insertValues.clear();
        for(int i=1;i<problem.varEqCount+1;i++){
            insertValues.put("problem_id", id);
            insertValues.put("nr", problem.nrs.get(i-1));
            insertValues.put("sign", problem.varSigns.get(i-1));
            insertValues.put("end", problem.varVals.get(i-1));
            db.insert("vars",null,insertValues);
            insertValues.clear();
        }
        for(int i=1;i<problem.varCount+1;i++){
            insertValues.put("problem_id", id);
            insertValues.put("nr", i);
            insertValues.put("value", problem.function.get(i-1));
            db.insert("functions",null,insertValues);
            insertValues.clear();
        }
        for(int i=1;i<problem.equCount+1;i++){
            Equation e = problem.equations.get(i-1);
            insertValues.put("problem_id", id);
            insertValues.put("nr", i);
            insertValues.put("sign", e.sign);
            insertValues.put("end", e.end);
            int eID =(int) db.insert("equations",null,insertValues);
            insertValues.clear();
            for (int j=1;j<e.equation.size()+1;j++){
                insertValues.put("equation_id", eID);
                insertValues.put("nr", j);
                insertValues.put("value", e.equation.get(j-1));
                db.insert("parts",null,insertValues);
                insertValues.clear();
            }
        }
        return id;
    }

    public void getAllFromDB(){
        if (!problems.isEmpty()) return;
        Cursor c = db.rawQuery("SELECT problem_id,target FROM problems",null);
        while(c.moveToNext()){
            Log.e("e", "problems:"+c.getInt(0)+":"+c.getString(1));
            Problem problem = new Problem(true);
            problem.setID(c.getInt(0));
            if (c.getString(1).equals("min")) problem.isMin = true; else problem.isMin = false;
            Cursor c2 = db.rawQuery("SELECT value FROM functions WHERE problem_id ="+c.getInt(0)+" ORDER BY nr ASC",null);
            while (c2.moveToNext()){
                Log.e("e", "values:"+c2.getDouble(0));
                problem.function.add(c2.getDouble(0));
            }
            c2.close();
            c2 = db.rawQuery("SELECT sign,end,nr FROM vars WHERE problem_id = "+c.getInt(0)+" ORDER BY nr ASC",null);
            int check = -1;
            while (c2.moveToNext()){
                if (check!=c2.getInt(2)){
                    check = c2.getInt(2);
                    problem.varCount++;
                }
                Log.e("e", "vars:"+c2.getString(0)+":"+c2.getDouble(1));
                problem.varVals.add(c2.getDouble(1));
                problem.varSigns.add(c2.getString(0));
                problem.nrs.add(check);
                problem.varEqCount++;
            }
            c2.close();
            c2 = db.rawQuery("SELECT equation_id,sign,end FROM equations WHERE problem_id ="+c.getInt(0)+" ORDER BY nr ASC",null);
            while(c2.moveToNext()){
                Log.e("e", "equs:"+c2.getInt(0)+":"+c2.getString(1)+":"+c2.getDouble(2));
                Equation e = new Equation();
                e.end = c2.getDouble(2);
                e.sign = c2.getString(1);
                Cursor c3 = db.rawQuery("SELECT value FROM parts WHERE equation_id = "+c2.getInt(0)+" ORDER BY nr ASC",null);
                while (c3.moveToNext()){
                    Log.e("e", "equs_value:"+c3.getDouble(0));
                    e.equation.add(c3.getDouble(0));
                }
                c3.close();
                problem.equations.add(e);
                problem.equCount++;
            }
            c2.close();
            problems.add(problem);
        }
        c.close();
    }
}
