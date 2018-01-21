package the.instinctives.studentgroups;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.xevent.MessageEventManager;
import org.jivesoftware.smackx.xevent.MessageEventNotificationListener;
import org.jivesoftware.smackx.xevent.MessageEventRequestListener;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by michaeljaison on 1/17/18.
 */

public class EjabberdService extends Service{

    String SMACK = "LogSmack";

    private XMPPTCPConnection connection;

    @Override
    public void onCreate() {
        super.onCreate();
        XMPPTCPConnectionConfiguration config =
                XMPPTCPConnectionConfiguration
                        .builder()
                        .setUsernameAndPassword("admin", "Mike8097565701@")
                        .setHost("skyroutetravels.com")
                        .setServiceName("skyroutetravels.com")
                        .setPort(5222)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setDebuggerEnabled(false)
                        .build();

        connection = new XMPPTCPConnection(config);

        connection.addConnectionListener(myConnectionListener);

        try {
            boolean result = new ConnectToXmpp().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(connection.isAuthenticated())
        {
            Log.e("jaosm", "authendicated");

            ReconnectionManager manager= ReconnectionManager.getInstanceFor(connection);
            manager.enableAutomaticReconnection();

            MessageEventManager m = MessageEventManager.getInstanceFor(connection);

            m.addMessageEventNotificationListener(myMessageEventNotificationListener);

            m.addMessageEventRequestListener(myMessageEventRequestListener);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    ConnectionListener myConnectionListener = new ConnectionListener() {

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
    };

    MessageEventNotificationListener myMessageEventNotificationListener = new MessageEventNotificationListener() {
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
    };

    MessageEventRequestListener myMessageEventRequestListener = new MessageEventRequestListener() {

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
                                                   MessageEventManager arg2) throws SmackException.NotConnectedException {
            Log.e(SMACK, arg0 + arg1 + "d111eliverd");
        }

        @Override
        public void composingNotificationRequested(String arg0, String arg1,
                                                   MessageEventManager arg2) {
            Log.e(SMACK, arg0 + arg1 + "c111omposing");
        }
    };

    private class ConnectToXmpp extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean success = true;

            try {
                connection.connect().login();
            } catch (XMPPException e) {
                success = false;
                e.printStackTrace();
            } catch (SmackException e) {
                success = false;
                e.printStackTrace();
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }

            return success;
        }
    }
}
