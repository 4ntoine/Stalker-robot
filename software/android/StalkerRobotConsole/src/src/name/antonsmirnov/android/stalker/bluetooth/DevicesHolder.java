package name.antonsmirnov.android.stalker.bluetooth;

import android.view.View;
import android.widget.TextView;
import name.antonsmirnov.android.stalker.R;

/**
 * Devices listView item holder
 */
public class DevicesHolder {

    private TextView deviceName;
    private TextView deviceMac;
    private TextView deviceStatus;

    public TextView getDeviceMac() {
        return deviceMac;
    }

    public TextView getDeviceName() {
        return deviceName;
    }

    public TextView getDeviceStatus() {
        return deviceStatus;
    }

    public DevicesHolder(View rowView) {
        deviceName = (TextView) rowView.findViewById(R.id.deviceName);
        deviceMac = (TextView) rowView.findViewById(R.id.deviceMac);
        deviceStatus = (TextView) rowView.findViewById(R.id.deviceStatus);
    }
}
