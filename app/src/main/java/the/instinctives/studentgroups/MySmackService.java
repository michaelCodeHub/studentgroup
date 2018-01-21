package the.instinctives.studentgroups;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.xevent.MessageEventManager;
import org.jivesoftware.smackx.xevent.MessageEventNotificationListener;
import org.jivesoftware.smackx.xevent.MessageEventRequestListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MySmackService extends Service implements StanzaListener {
    String SMACK="SMACK";
    private List<Messenger> mClients = new ArrayList<Messenger>();
    private XMPPTCPConnectionConfiguration config;
    public static XMPPTCPConnection connection;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;
    public static final int MSG_SET_STRING_VALUE = 4;
    private static boolean isRunning = false;
    Messenger mMessenger = new Messenger(new IncomingMessageHandler());
    MessageEventManager m;
    private boolean online;
    private String android_id;
    private MultiUserChat muc;

    @Override
    public void onCreate()
    {
        Log.e(SMACK,"Service Started");

        isRunning = true;
        super.onCreate();

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        config= XMPPTCPConnectionConfiguration
                .builder()
                .setHost("skyroutetravels.com")
                .setPort(5222)
                .setServiceName("skyroutetravels.com")
                .setSecurityMode(SecurityMode.disabled)
                .setDebuggerEnabled(false)
                .build();

        connection=new XMPPTCPConnection(config);

        connection.addAsyncStanzaListener(this, MessageTypeFilter.CHAT);

        new connections().execute(App.shared.getString("user_id", "admin"), App.shared.getString("password", "111001"));

        connection.addConnectionListener(new ConnectionListener() {

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectionFailed(Exception arg0) {
                Log.e(SMACK, "Reconnection Failed");
            }

            @Override
            public void reconnectingIn(int arg0) {
                Log.e(SMACK, "Reconnecting in " + arg0);
            }

            @Override
            public void connectionClosedOnError(Exception arg0) {
                Log.e(SMACK, "Connection cloesd" + arg0.toString());
            }

            @Override
            public void connectionClosed() {
                Log.e(SMACK, "Connection closed");
            }

            @Override
            public void connected(XMPPConnection arg0) {
                Log.e(SMACK, "Connected");
            }

            @Override
            public void authenticated(XMPPConnection arg0, boolean arg1) {
                Log.e(SMACK, "authendicated");

                OfflineMessageManager off = new OfflineMessageManager(connection);
                try {
                    Log.e(SMACK, String.valueOf(off.getMessageCount()));
                } catch (Exception e) {

                }
            }
        });

        connection.addStanzaAcknowledgedListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza arg0) throws NotConnectedException {

            }
        });

        ReconnectionManager manager= ReconnectionManager.getInstanceFor(connection);
        manager.enableAutomaticReconnection();

        m= MessageEventManager.getInstanceFor(connection);

        m.addMessageEventNotificationListener(new MessageEventNotificationListener() {
            @Override
            public void offlineNotification(String arg0, String arg1) {
                Log.e(SMACK, arg0 + arg1 + "offline");
            }

            @Override
            public void displayedNotification(String arg0, String arg1) {
                Log.e(SMACK, arg0 + arg1 + "displayed");
            }

            @Override
            public void deliveredNotification(String arg0, String arg1) {
                Log.e(SMACK, arg0 + arg1 + "deliverd");
            }

            @Override
            public void composingNotification(String arg0, String arg1) {
                Log.e(SMACK, arg0 + arg1 + "composing");

            }

            @Override
            public void cancelledNotification(String arg0, String arg1) {
                Log.e(SMACK, arg0 + arg1 + "cabcel");

            }
        });

        m.addMessageEventRequestListener(new MessageEventRequestListener() {

            @Override
            public void offlineNotificationRequested(String arg0, String arg1,
                                                     MessageEventManager arg2) {
                Log.e(SMACK, arg0 + arg1 + "o111ffline");
            }

            @Override
            public void displayedNotificationRequested(String arg0, String arg1,
                                                       MessageEventManager arg2) {
                Log.e(SMACK, arg0 + arg1 + "d111isplayed");
            }

            @Override
            public void deliveredNotificationRequested(String arg0, String arg1,
                                                       MessageEventManager arg2) throws NotConnectedException {
                Log.e(SMACK, arg0 + arg1 + "d111eliverd");
            }

            @Override
            public void composingNotificationRequested(String arg0, String arg1,
                                                       MessageEventManager arg2) {
                Log.e(SMACK, arg0 + arg1 + "c111omposing");
            }
        });
    }

    public void send(String to, String content)
    {
        Message message = new Message(to+"@skyroutetravels.com", Message.Type.chat);
        message.setSubject("testing");
        message.setBody(content);
        try
        {
            connection.sendStanza(message);
            App.db.open();
            App.db.addmsg(message.getStanzaId(), 0, to , content, getdate(), gettime(), 0);
            App.db.close();
            updateUI(message.getStanzaId());
            Log.e("SMACK","send"+"loosu");
        }
        catch(Exception e)
        {
            Log.e("SMACK",e.toString()+"loosu");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public void processPacket(Stanza arg0) throws NotConnectedException
    {
        Message message = (Message) arg0;
        Log.e(SMACK,message.getBody());

        App.db.open();
        App.db.addmsg(message.getStanzaId(), 1, message.getFrom().substring(0,message.getFrom().indexOf("@")) , message.getBody(), getdate(), gettime(), 0);
        App.db.close();


        if(online) {

            updateUI(message.getStanzaId());
        }
        else
        {
            Intent intent1 = new Intent(getApplicationContext(),ChatPage.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("New task assigned")
                    .setContentIntent(pendingNotificationIntent)
                    .setAutoCancel(true)
                    .setLights(Color.RED, 3000, 3000)
                    .setContentText(message.getBody());

            Notification notification=mBuilder.build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;

            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, notification);
        }

    }

    public static boolean isRunning()
    {
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mMessenger.getBinder();
    }

    private class IncomingMessageHandler extends Handler
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;

                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;

                case 5:
                    if(msg.arg1==0)
                        online=true;
                    else
                        online=false;
                    Log.e(SMACK, String.valueOf(msg.arg1));
                    break;

                case 81:
                    Log.e("Binder","Success");
                    send(msg.getData().getString("to"), msg.getData().getString("message"));
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }


    public class connections extends AsyncTask<String, Void, String>
    {
        String res="OK";
        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                Log.e("SMACK",params[0]+"  user");
                connection.connect();
                connection.login(params[0],params[1]);
            }
            catch(Exception e)
            {
                res=e.toString();
                Log.e(SMACK,res);
            }
            return res;
        }
        @Override
        protected void onPostExecute(String result)
        {

        }
    }


    public String getdate()
    {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdt.format(currentLocalTime);
        return time;
    }

    public String gettime()
    {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat sdt = new SimpleDateFormat("HH:mm:ss a");
        String time = sdt.format(currentLocalTime);
        return time;
    }

    private void updateUI(String content)
    {
        Iterator<Messenger> messengerIterator = mClients.iterator();
        while(messengerIterator.hasNext())
        {
            Messenger messenger = messengerIterator.next();
            try
            {
                Bundle bundle = new Bundle();
                bundle.putString("messageid",content);
                android.os.Message msg = android.os.Message.obtain(null, 82);
                msg.setData(bundle);
                messenger.send(msg);
            }
            catch (RemoteException e)
            {
                mClients.remove(messenger);
                System.out.println(e.toString());
            }
        }
    }
}
