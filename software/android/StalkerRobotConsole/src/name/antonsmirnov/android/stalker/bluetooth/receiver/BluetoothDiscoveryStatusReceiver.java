package name.antonsmirnov.android.stalker.bluetooth.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import name.antonsmirnov.android.stalker.MainActivity;
import name.antonsmirnov.android.stalker.R;

import java.text.MessageFormat;

/**
 * Listens for Bluetooth discovery status events
 */
public class BluetoothDiscoveryStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
            // started
            MainActivity.get().enableDiscoveryStatus(false);
            Toast.makeText(context,
                    MessageFormat.format(
                        context.getString(R.string.discovery),
                        context.getString(R.string.started).toUpperCase()),
                    Toast.LENGTH_SHORT).show();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
            // finished
            MainActivity.get().enableDiscoveryStatus(true);
            Toast.makeText(context,
                    MessageFormat.format(
                        context.getString(R.string.discovery),
                        context.getString(R.string.started).toUpperCase()),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
