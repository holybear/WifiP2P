package github.leavesczy.wifip2p.utils

import android.net.wifi.p2p.WifiP2pDevice

/**
 * @Author: leavesCZY
 * @Desc:
 */
object WifiP2pUtils {

    fun getDeviceStatus(deviceStatus: Int): String {
        return when (deviceStatus) {
            WifiP2pDevice.AVAILABLE -> "Available"
            WifiP2pDevice.INVITED -> "Inviting"
            WifiP2pDevice.CONNECTED -> "connected"
            WifiP2pDevice.FAILED -> "Failure"
            WifiP2pDevice.UNAVAILABLE -> "unavailable"
            else -> "unknown"
        }
    }

}