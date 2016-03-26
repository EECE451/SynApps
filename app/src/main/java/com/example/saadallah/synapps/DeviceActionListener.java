package com.example.saadallah.synapps;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by Saadallah on 3/26/2016.
 */
public interface DeviceActionListener {
    void connect(int i);
    void disconnect(WifiP2pDevice device);
    void disconnect();
}
