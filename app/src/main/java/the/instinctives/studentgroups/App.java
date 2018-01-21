package the.instinctives.studentgroups;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by michaeljaison on 1/17/18.
 */

public class App extends Application
{
    public static Database db;
    public static SharedPreferences shared;
    public static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        shared=getSharedPreferences("studentgroups", MODE_PRIVATE);
        editor=shared.edit();
        db=new Database(getApplicationContext());
    }
}
