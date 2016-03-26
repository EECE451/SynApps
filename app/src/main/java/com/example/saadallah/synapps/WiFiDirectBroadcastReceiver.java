package com.example.saadallah.synapps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saadallah on 3/25/2016.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Activity mActivity;
    private ArrayList PeerNames= new ArrayList();
    private WifiP2pDeviceList peerlist= new WifiP2pDeviceList();
    private WifiP2pManager.PeerListListener peerListListener= new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            PeerNames.clear();
            PeerNames.addAll(peerlist.getDeviceList());

            // update any where the peers are stored
        }
    };



    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Activity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Boolean isEnabled;

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                isEnabled = true;
                Log.d("p2pIsEnabled=", "true");
            }
            else {
                isEnabled = false;
                Log.d("p2pIsEnabled=", "false");
            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
            Log.d("P2P Notification", "Peers discovered!");
            Toast.makeText(mActivity,"Peers discovered!", Toast.LENGTH_SHORT).show();

            // At that stage all the devices are listed in peerNames.
            // Working till here!

        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                Log.d("P2P Notification", "Connected!");
                Toast.makeText(mActivity,"Connected!", Toast.LENGTH_SHORT).show();


                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        // InetAddress from WifiP2pInfo struct.
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;

                        // After the group negotiation, we can determine the group owner.
                        if (info.groupFormed && info.isGroupOwner) {
                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a server thread and accepting
                            // incoming connections.
                            Log.d("P2P Notification", "test1!");
                            Toast.makeText(mActivity,"test1!", Toast.LENGTH_SHORT).show();
                        } else if (info.groupFormed) {
                            // The other device acts as the client. In this case,
                            // you'll want to create a client thread that connects to the group
                            // owner.
                            Log.d("P2P Notification", "test2!");
                            Toast.makeText(mActivity,"test2!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            }
        }

    }




    public ArrayList getPeerNames() {
        Log.d("mytest", "size peers name"+PeerNames.size());
        Toast.makeText(mActivity, "size peers name"+PeerNames.size(), Toast.LENGTH_SHORT).show();
        return PeerNames;
    }
}
