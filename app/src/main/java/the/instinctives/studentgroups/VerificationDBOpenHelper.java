package the.instinctives.studentgroups;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class VerificationDBOpenHelper extends SQLiteOpenHelper
{
	public VerificationDBOpenHelper(Context context)
	{
		super(context, context.getFilesDir()+"/database/bravemanagement", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + "Messages" + "(" +"ID"
				+ " INTEGER PRIMARY KEY AUTOINCREMENT ,"+"MESSAGEID"
				+ " TEXT ,"+"DIRECTION"
				+ " INTEGER ,"+"IMAGE"
				+ " TEXT ,"+"PERSON"
				+ " TEXT ,"+"MESSAGE"
				+ " TEXT ,"+"DATE"
				+ " TEXT ,"+"TIME"
				+ " TEXT ,"+"STATUS"
				+ " INT );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

}