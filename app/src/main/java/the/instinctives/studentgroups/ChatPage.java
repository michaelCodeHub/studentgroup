package the.instinctives.studentgroups;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatPage extends AppCompatActivity implements ServiceConnection
{
	public static boolean isActive = true;

	EditText chattext;
	ImageView send;
	String date;
	ListView list;
	ArrayList<Messages> chats;
	ChatAdapter adapter;
		
	private Messenger mServiceMessenger = null;
	boolean mIsBound;
	private static final String LOGTAG = "MainActivity";
	private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());
	private ServiceConnection mConnection = this;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatpage);

        chattext=(EditText) findViewById(R.id.editText1);
        send=(ImageView) findViewById(R.id.send);
        list=(ListView) findViewById(R.id.listView1);
		final ImageView emojiButton = (ImageView) findViewById(R.id.imageView1);

		new Font().overrideFonts(getApplicationContext(),chattext);

		getSupportActionBar().setTitle("Michqel Jason");

        send.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				sendMessage(App.shared.getString("friend_id", "admin"), chattext.getText().toString());
				chattext.setText("");
			}
		});
        
        chats=new ArrayList<Messages>();
        adapter=new ChatAdapter(getApplicationContext(), chats);
        list.setAdapter(adapter);

        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(dt);
		automaticBind();
        updateUI();
    }

	private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId)
	{
		iconToBeChanged.setImageResource(drawableResourceId);
	}

    
	private void automaticBind() 
	{
			Log.e("BIND", "2");
			doBindService();
	}

	private void doBindService() 
	{
		bindService(new Intent(this, MySmackService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		sendMessageToService(0);
		Log.e("BIND", "3");
	}

	private void doUnbindService() 
	{
		if (mIsBound) 
		{
			if (mServiceMessenger != null) 
			{
				try
				{
					android.os.Message msg = android.os.Message.obtain(null, MySmackService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} 
				catch (RemoteException e)
				{
					// There is nothing special we need to do if the service has crashed.
				}
			}
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		mServiceMessenger = new Messenger(service);
		try 
		{
			android.os.Message msg = android.os.Message.obtain(null, MySmackService.MSG_REGISTER_CLIENT);
			msg.replyTo = mMessenger;
			mServiceMessenger.send(msg);
		} 
		catch (RemoteException e)
		{
			// In this case the service has crashed before we could even do anything with it
		} 
		sendMessageToService(0);
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		mServiceMessenger = null;
	}

	private class IncomingMessageHandler extends Handler
	{		
		@Override
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
				case MySmackService.MSG_SET_INT_VALUE:
					break;
			
				case MySmackService.MSG_SET_STRING_VALUE:
					break;

				case 50:
//					addtolist(msg.getData().getString("chatbody"));
					break;

				case 82:
					String id=msg.getData().getString("messageid");
					updateUI();
					break;
				
				default:
					super.handleMessage(msg);
			}
		}
	}	
	
//	public void addtolist(String stanzaid)
//	{
//		chats.add(new Messages(stanzaid));
//		adapter.notifyDataSetChanged();
//		list.refreshDrawableState();
//		list.setSelection(chats.size()-1);
//	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		if (savedInstanceState != null) 
		{
			
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void sendMessageToService(int intvaluetosend) 
	{
		if (mIsBound) 
		{
			Log.e("BIND","4");
			if (mServiceMessenger != null) 
			{
				Log.e("BIND","5");
				try 
				{
					Log.e("BIND", "6");
					android.os.Message msg = android.os.Message.obtain(null, 5, intvaluetosend, 0);
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} 
				catch (RemoteException e)
				{
					Log.e("BIND", e.toString());
				}
			}
		}
	}

	private void sendMessage(String to, String message)
	{
		if (mIsBound)
		{
			if (mServiceMessenger != null)
			{
				try
				{
					// Send data as a String
					Bundle bundle = new Bundle();
					bundle.putString("message", message);
					bundle.putString("to", to);
					android.os.Message msg = android.os.Message.obtain(null, 81);
					msg.setData(bundle);
					mServiceMessenger.send(msg);
					updateUI();
				}
				catch (Exception e)
				{
					System.out.println(e.toString());
				}

			}
		}
	}
	
    public void updateUI()
    {
    	try
    	{
    		Log.e("tets", "updateuicalled");
	    	chats.clear();
			adapter.notifyDataSetChanged();
			list.refreshDrawableState();
	    	App.db.open();
	    	Cursor c=App.db.chats(App.shared.getString("friend_id","jk"));
	    	while(c.moveToNext())
	    	{
	    		Messages message = new Messages();
	    		message.setMessageid(c.getString(1));
				message.setMessage(c.getString(5));
				message.setDirection(c.getInt(2));
				message.setPerson(c.getString(4));
				message.setMesgdate(c.getString(6));
				message.setMsgtime(c.getString(7));
				message.setStatus(c.getInt(8));
	    		chats.add(message);
	    	}
			list.setSelection(chats.size()-1);
			c.close();
			App.db.close();
    	}
    	catch(Exception e)
    	{
    		Log.e("error", e.toString());
    	}
    }
	
	@Override
	public void onStop()
	{
		isActive = false;
		sendMessageToService(1);
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		try 
		{
			doUnbindService();
			isActive = false;
		} 
		catch (Throwable t)
		{
			Log.e(LOGTAG, "Failed to unbind from the service", t);
		}
		super.onDestroy();
	}
	
	@Override
	public void onResume()
	{
		isActive = true;
    	updateUI();
		sendMessageToService(0);
		super.onResume();
	}
}
