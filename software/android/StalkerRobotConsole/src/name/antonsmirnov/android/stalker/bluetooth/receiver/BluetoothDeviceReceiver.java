package name.antonsmirnov.android.stalker.bluetooth.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import name.antonsmirnov.android.stalker.MainActivity;

/**
 * Listens for Bluetooth devices during discovering
 */
public class BluetoothDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        MainActivity.get().addBluetoothDevice(device);
    }
}
