package januszek.solver.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Januszek on 2016-04-08.
 */
public class Starter extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Solver.db";

    public Starter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE problems (problem_id INTEGER PRIMARY KEY,target CHARACTER(1) NOT NULL);");
        db.execSQL("CREATE TABLE functions (function_id INTEGER PRIMARY KEY, problem_id INT UNSIGNED NOT NULL, nr TINYINT UNSIGNED NOT NULL,value DOUBLE NOT NULL);");
        db.execSQL("CREATE TABLE equations (equation_id INTEGER PRIMARY KEY, problem_id INT UNSIGNED NOT NULL,nr TINYINT NOT NULL, sign VARCHAR(3) NOT NULL, end DOUBLE NOT NULL);");
        db.execSQL("CREATE TABLE parts (part_id INTEGER PRIMARY KEY, equation_id INT UNSIGNED NOT NULL,nr TINYINT NOT NULL,value DOUBLE NOT NULL);");
        db.execSQL("CREATE TABLE vars (var_id INTEGER PRIMARY KEY, problem_id INT UNSIGNED NOT NULL,nr TINYINT NOT NULL,sign VARCHAR(3) NOT NULL,end DOUBLE NOT NULL );");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
