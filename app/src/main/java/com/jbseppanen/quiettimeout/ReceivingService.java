package com.jbseppanen.quiettimeout;

import android.app.Service;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceivingService extends Service {

    public static final String SERVICE_INFO_KEY = "server_socket_key";
    public static final String MESSAGE_KEY = "message_key";
    public static final String MESSENGER_KEY = "messenger_key";


    private String TAG = "InfoTag";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final NsdServiceInfo mService = (NsdServiceInfo) intent.getParcelableExtra(SERVICE_INFO_KEY);
        final Messenger messenger = intent.getParcelableExtra(MESSENGER_KEY);

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader input;
                try {
                    while (mService != null) {
                        Socket clientSocket = new Socket(mService.getHost(), mService.getPort());
                        input = new BufferedReader(new InputStreamReader(
                                clientSocket.getInputStream()));

                        String messageStr = null;
                        messageStr = input.readLine();
                        if (messageStr != null) {
                            Log.i(TAG, "Read from the stream: " + messageStr);

                            Message msg = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putString(MESSAGE_KEY, messageStr);
                            msg.setData(bundle);
                            try {
                                messenger.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }


//                            callback.returnResult(messageStr);
                        } else {
                            Log.i(TAG, "Null string");
                            break;
                        }
                        input.close();
                    }
                    stopSelf();
                } catch (IOException e) {
                    Log.i(TAG, "Server loop error: ", e);
                } finally {
                    stopSelf();
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}
