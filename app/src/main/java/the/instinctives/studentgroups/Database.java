package the.instinctives.studentgroups;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

	private final Context context;
	SQLiteDatabase myDatabase;
	VerificationDBOpenHelper myVerificationHelper;

	private static String TBNAME;
	private static String DIRECTION;
	private static String MESSAGEID;
	private static String PERSON;
	private static String MESSAGE;
	private static String DATE;
	private static String TIME;
	private static String STATUS;
	private static String IMAGE;

	public static String[] fromto;
	public static String[] person;
	public static String[] message;
	public static String[] date;
	public static String[] time;

	public static String t1;
	public static String t2;
	public static String t3;
	public static String t4;
	
	public Database(Context ct)
	{
		context = ct;

		TBNAME = "Messages";
		DIRECTION="DIRECTION";
		IMAGE="IMAGE";
		MESSAGEID="MESSAGEID";
		PERSON="PERSON";
		MESSAGE="MESSAGE";
		TIME="TIME";
		DATE="DATE";
		STATUS="STATUS";
	}

	public Database open() throws SQLException
	{
		myVerificationHelper = new VerificationDBOpenHelper(context);
		myDatabase = myVerificationHelper.getWritableDatabase();
		return this;
	}

	public void close() throws SQLException
	{
			myVerificationHelper.close();
	}

	public void addmsg(String messageid, int direction, String person, String message, String date, String time, int status)
	{
		String where = "MESSAGEID='" + messageid + "'";
		Cursor c = myDatabase.query(TBNAME, null, where, null, null, null, null);
		if (c.moveToNext())
		{

		}
		else
		{
			ContentValues cv = new ContentValues();
			cv.put(MESSAGEID, messageid);
			cv.put(DIRECTION, direction);
			cv.put(PERSON, person);
			cv.put(MESSAGE, message);
			cv.put(DATE, date);
			cv.put(TIME, time);
			cv.put(STATUS, status);
			myDatabase.insert(TBNAME, null, cv);
			Log.e("asion", "addedtodab");
		}
	}

	public void updatemsg(String messageid, String status)
	{
		ContentValues cv = new ContentValues();
		cv.put(STATUS, status);
		String where=MESSAGEID+"='"+messageid+"'";
		myDatabase.update(TBNAME, cv, where, null);
	}

	public void updateall(String person, String status)
	{
		ContentValues cv = new ContentValues();
		cv.put(STATUS, status);
		String where="("+STATUS+"='"+2+"' or "+STATUS+"='"+1+"') and "+PERSON+"='"+person+"' and "+DIRECTION+"='0'";

		Log.e("SMACK", where);

		myDatabase.update(TBNAME, cv, where, null);
	}

	public void chats(String date, String person)
	{
		String where=DATE+"='"+date+"' and "+PERSON+" like '%"+person+"%'";
		String[] columns={DIRECTION,MESSAGE,TIME};
		Cursor c = myDatabase.query(TBNAME, columns, where, null, null, null, null, null);
		int i=0;
		fromto=new String[c.getCount()];
		message=new String[c.getCount()];
		time=new String[c.getCount()];
		while(c.moveToNext())
		{
			fromto[i]=c.getString(0);
			message[i]=c.getString(1);
			time[i]=c.getString(2);
			i++;
		}
		c.close();
	}

	public void getdata(String msgID)
	{
		String where=MESSAGEID+"='"+msgID+"'";
		String[] columns={PERSON,MESSAGE,STATUS,TIME};
		Cursor c = myDatabase.query(TBNAME, columns, where, null, null, null, null, null);
		if(c.moveToNext())
		{
			t1=c.getString(0);
			t2=c.getString(1);
			t3=c.getString(2);
			t4=c.getString(3);
		}
		c.close();
	}

	public Cursor getfailedmessages()
	{
		String where=STATUS+"='0'";
		String[] columns={MESSAGEID,MESSAGE,STATUS,PERSON};
		Cursor c = myDatabase.query(TBNAME, columns, where, null, null, null, null, null);
		return c;
	}

	public Cursor getchattime(String msgID)
	{
		String where=MESSAGEID+"='"+msgID+"'";
		String[] columns={PERSON,MESSAGE,DATE,TIME,STATUS};
		Cursor c = myDatabase.query(TBNAME, columns, where, null, null, null, null, null);
		return c;
	}

	public Cursor chats(String person)
	{
		String condition=PERSON+"='"+person+"'";
		Cursor c = myDatabase.query(TBNAME, null, condition, null, null, null, null, null);
		return c;
	}

	public void updatechatfriends(String xnoid, String lastchat, String date, String time, String status, Long datetime)
	{
		String where="XNOID"+"='"+xnoid+"'";
		Cursor c = myDatabase.query("ChatFriends", null, where, null, null, null, null, null);
		if(c.moveToNext())
		{
			System.out.println(1);
			ContentValues cv = new ContentValues();
			cv.put("LASTCHAT", lastchat);
			cv.put("DATE", date);
			cv.put("TIME", time);
			cv.put("STATUS", status);
			cv.put("DATETIME", datetime);
			myDatabase.update("ChatFriends", cv, where, null);
		}
		else
		{
			System.out.println(1);
			ContentValues cv = new ContentValues();
			cv.put("XNOID", xnoid);
			cv.put("LASTCHAT", lastchat);
			cv.put("DATE", date);
			cv.put("TIME", time);
			cv.put("STATUS", status);
			cv.put("DATETIME", datetime);
			myDatabase.insert("ChatFriends", null, cv);
		}
	}

	public Cursor getchatfriends()
	{
		String condition="1 order by DATETIME DESC";
		String[] columns={"XNOID","LASTCHAT","DATE","TIME","STATUS"};
		Cursor c = myDatabase.query("ChatFriends", columns, condition, null, null, null, null, null);
		return c;
	}

	public boolean isopened()
	{
		return myDatabase.isOpen();
	}

	public String getTableName() {
		return TBNAME;
	}

	public SQLiteDatabase getDatabase() {
		return myDatabase;
	}

}

