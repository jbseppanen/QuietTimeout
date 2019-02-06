package com.jbseppanen.quiettimeout;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ConnectionHelper {
    private static final String DEFAULT_SERVICE_NAME = "QuietTimeoutRemoteService";

    private static final String SERVICE_TYPE = "_http._tcp.";
    private ServerSocket mServerSocket;
    private int mLocalPort;
    private String mServiceName;
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private ArrayList<NsdServiceInfo> mDiscoveredServices;
    private NsdServiceInfo mService;
    private String TAG = "InfoTag";
    private Thread receivingThread;


    private void initializeServerSocket() {
        // Initialize a server socket on the next available port.
        try {
            mServerSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Store the chosen port.
        mLocalPort = mServerSocket.getLocalPort();

    }

    public void registerService() {
        initializeRegistrationListener();
        initializeServerSocket();
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        if (mServiceName == null) {
            mServiceName = DEFAULT_SERVICE_NAME;
        }
        serviceInfo.setServiceName(mServiceName);

        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(mLocalPort);
        mNsdManager = (NsdManager) MainActivity.context.getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    private void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed! Put debugging code here to determine why.
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed. Put debugging code here to determine why.
            }
        };
    }

    public void discoverServices() {
        initializeResolveListener();

        if (mNsdManager == null) {
            mNsdManager = (NsdManager) MainActivity.context.getSystemService(Context.NSD_SERVICE);
        }
        mDiscoveredServices = new ArrayList<>();
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {

            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {

            }

            @Override
            public void onDiscoveryStarted(String serviceType) {

            }

            @Override
            public void onDiscoveryStopped(String serviceType) {

            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "onServiceFound: " + serviceInfo);
                if (!serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    Log.i(TAG, "Unknown Service Type: " + serviceInfo.getServiceType());
                } else if (serviceInfo.getServiceName().equals(mServiceName)) {
//                } else {
                    Log.i(TAG, "Same machine: " + mServiceName);
                } else if (serviceInfo.getServiceName().contains(DEFAULT_SERVICE_NAME)) {
//                } else {
                    if (mDiscoveredServices.size() == 0) {
                        mNsdManager.resolveService(serviceInfo, mResolveListener);
                    }
                    mDiscoveredServices.add(serviceInfo);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "onServiceLost: " + serviceInfo);
                mDiscoveredServices.remove(serviceInfo);
            }
        };
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

    }

    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                if (receivingThread != null) {
                    receivingThread.start();
                }
            }
        };
    }

    public void shutdownServices() {
        if (receivingThread != null) {
            if (receivingThread.isAlive()) {
                receivingThread.interrupt();
            }
        }

        if (mNsdManager != null) {
            if (mRegistrationListener!=null) {
                mNsdManager.unregisterService(mRegistrationListener);
            }
            if (mDiscoveryListener != null) {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }
        }
    }

    public void sendMessage(final String message) {
        try {
            if (mServerSocket != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrintWriter out = null;
                        try {
                            Socket clientSocket = mServerSocket.accept();
                            out = new PrintWriter(
                                    new BufferedWriter(
                                            new OutputStreamWriter(clientSocket.getOutputStream())), true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        out.println(message);
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReceiver(final ReceiverCallback callback) {
        receivingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader input;
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Socket clientSocket = new Socket(mService.getHost(), mService.getPort());
                        input = new BufferedReader(new InputStreamReader(
                                clientSocket.getInputStream()));

                        String messageStr = null;
                        messageStr = input.readLine();
                        if (messageStr != null) {
                            Log.i(TAG, "Read from the stream: " + messageStr);
                            callback.returnResult(messageStr);
                        } else {
                            Log.i(TAG, "Null string");
                            break;
                        }
                        input.close();
                    }
                } catch (IOException e) {
                    Log.i(TAG, "Server loop error: ", e);
                }
            }
        });
    }

    public interface ReceiverCallback {
        void returnResult(String result);
    }


}
