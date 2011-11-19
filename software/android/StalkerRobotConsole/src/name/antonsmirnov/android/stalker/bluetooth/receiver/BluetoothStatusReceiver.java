package name.antonsmirnov.android.stalker.bluetooth.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import name.antonsmirnov.android.stalker.MainActivity;
import name.antonsmirnov.android.stalker.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Listens for Bluetooth status events
 */
public class BluetoothStatusReceiver extends BroadcastReceiver {

    public BluetoothStatusReceiver() {
        init();
    }

    private Map<Integer, BluetoothStatusDescriptor> states = new HashMap<Integer, BluetoothStatusDescriptor>();

    /**
     *
     */
    private class BluetoothStatusDescriptor {
        private boolean on;
        private boolean enabled;
        private int messageId;

        public int getMessageId() {
            return messageId;
        }

        public boolean isOn() {
            return on;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public BluetoothStatusDescriptor(boolean on, boolean enabled, int messageId) {
            this.on = on;
            this.enabled = enabled;
            this.messageId = messageId;
        }
    }

    private void init() {
        states.put(BluetoothAdapter.STATE_OFF, new BluetoothStatusDescriptor(false, true, R.string.bluetoothStatus_Off));
        states.put(BluetoothAdapter.STATE_ON, new BluetoothStatusDescriptor(true, true, R.string.bluetoothStatus_On));
        states.put(BluetoothAdapter.STATE_TURNING_OFF, new BluetoothStatusDescriptor(false, false, R.string.bluetoothStatus_Turning_Off));
        states.put(BluetoothAdapter.STATE_TURNING_ON, new BluetoothStatusDescriptor(true, false, R.string.bluetoothStatus_Turning_On));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int intentStatus = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
        BluetoothStatusDescriptor status = states.get(intentStatus);
        Toast.makeText(
            context,
            status != null
                ? context.getString(status.getMessageId())
                : context.getString(R.string.bluetoothStatus_Unknown),
            Toast.LENGTH_SHORT)
                .show();

        if (status != null) {
            MainActivity instance = MainActivity.get();
            instance.displayBluetoothStatus(status.isOn());
            instance.enabledChangeStatus(status.isEnabled());
        }
    }
}
