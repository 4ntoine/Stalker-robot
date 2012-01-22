package name.antonsmirnov.android.stalker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.*;
import android.widget.*;
import name.antonsmirnov.android.stalker.bluetooth.BluetoothThread;
import name.antonsmirnov.android.stalker.bluetooth.DevicesAdapter;
import name.antonsmirnov.android.stalker.log.LogAdapter;
import name.antonsmirnov.android.stalker.log.LogRecord;
import name.antonsmirnov.android.stalker.log.LogRecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class MainActivity extends Activity {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // ui
    private Button disconnectButton;
    private Button searchDevicesButton;
    private ToggleButton bluetoothStatus;
    private ListView devicesListView;
    private ListView logListView;
    private View sendBar;
    private EditText sendData;
    private Button sendDataButton;

    // misc
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BroadcastReceiver btDeviceListener;
    private BroadcastReceiver btDiscoveryStatusReceiver;
    private BroadcastReceiver btStatusReceiver;

    private void registerBtDeviceReceiver() {
        btDeviceListener = new BluetoothDeviceReceiver();
        registerReceiver(btDeviceListener, new IntentFilter("android.bluetooth.device.action.FOUND"));
    }

    private void registerBtDiscoveryStatusReceiver() {
        btDiscoveryStatusReceiver = new BluetoothDiscoveryStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btDiscoveryStatusReceiver, filter);
    }

    private void registerBtStatusReceiver() {
        btStatusReceiver = new BluetoothStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(btStatusReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerBtDeviceReceiver();
        registerBtDiscoveryStatusReceiver();
        registerBtStatusReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(btDeviceListener);
        unregisterReceiver(btDiscoveryStatusReceiver);
        unregisterReceiver(btStatusReceiver);
    }

    /**
     * BluetoothDeviceReceiver
     */
    private class BluetoothDeviceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            addBluetoothDevice(device);
        }
    }

    /**
     * BluetoothDiscoveryStatusReceiver
     */
    private class BluetoothDiscoveryStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                // started
               enableDiscoveryStatus(false);
                Toast.makeText(context,
                        MessageFormat.format(
                                context.getString(R.string.discovery),
                                context.getString(R.string.started).toUpperCase()),
                        Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                // finished
                enableDiscoveryStatus(true);
                Toast.makeText(context,
                        MessageFormat.format(
                                context.getString(R.string.discovery),
                                context.getString(R.string.finished).toUpperCase()),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * BluetoothStatusReceiver
     */
    private class BluetoothStatusReceiver extends BroadcastReceiver {

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
                displayBluetoothStatus(status.isOn());
                enabledChangeStatus(status.isEnabled());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        bindControls();
        initControls();

        onConnected(false);
    }

    public void displayBluetoothStatus(boolean on) {
        bluetoothStatus.setChecked(on);
    }

    public void enabledChangeStatus(boolean enabled) {
        bluetoothStatus.setEnabled(enabled);
    }

    private ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    private DevicesAdapter devicesAdapter;

    private void log(LogRecord logRecord) {
        log.add(logRecord);

        runOnUiThread(new Runnable() {
            public void run() {
                logAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addDevice(BluetoothDevice device) {
        devices.remove(device);
        LogRecord logRecord = new LogRecord(
            MessageFormat.format(getString(R.string.adding), device.getName(), device.getAddress()),
            LogRecordType.INFO);
        log(logRecord);
        logger.debug(logRecord.getMessage());
        devices.add(device);
    }

    public void addBluetoothDevice(BluetoothDevice device) {
        addDevice(device);
        updateList();
    }

    private void updateList() {
        runOnUiThread(new Runnable() {
            public void run() {
                devicesAdapter.notifyDataSetChanged();
            }
        });
    }

    public void enableDiscoveryStatus(boolean enabled) {
        LogRecord logRecord = new LogRecord(
            MessageFormat.format(getString(R.string.discovery),
                getString(enabled
                    ? R.string.finished
                    : R.string.started)),
            LogRecordType.INFO);
        log(logRecord);
        logger.debug(logRecord.getMessage());

        searchDevicesButton.setEnabled(enabled);
    }

    private List<LogRecord> log = new ArrayList<LogRecord>();
    private LogAdapter logAdapter;

    private void searchDevices() {
        devices.clear();
        devicesAdapter.notifyDataSetChanged();

        if (!adapter.isEnabled()) {
            adapter.enable();
        }

        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        for (BluetoothDevice eachDevice : pairedDevices)
            addDevice(eachDevice);
        devicesAdapter.notifyDataSetChanged();

        LogRecord logRecord = new LogRecord(
            MessageFormat.format(getString(R.string.discovery), getString(R.string.starting)),
            LogRecordType.INFO);
        log(logRecord);
        logger.debug(logRecord.getMessage());

        adapter.startDiscovery();
    }

    private static final String DEVICES_KEY = "DEVICES";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DEVICES_KEY, devices);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        devices.clear();
        ArrayList<? extends Parcelable> parcelableArrayList = savedInstanceState.getParcelableArrayList(DEVICES_KEY);
        devices.addAll((ArrayList<BluetoothDevice>)parcelableArrayList);
        updateList();
    }

    private void bindControls() {
        disconnectButton = (Button) findViewById(R.id.disconnect);
        searchDevicesButton = (Button) findViewById(R.id.searchDevices);
        bluetoothStatus = (ToggleButton) findViewById(R.id.bluetoothStatus);

        devicesListView = (ListView) findViewById(R.id.devicesList);
        logListView = (ListView) findViewById(R.id.logList);

        sendBar = findViewById(R.id.sendBar);
        sendData = (EditText) findViewById(R.id.sendData);
        sendDataButton = (Button) findViewById(R.id.sendButton);
    }

    private void initControls() {
        searchDevicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                searchDevices();
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                disconnect();
            }
        });

        bluetoothStatus.setChecked(adapter.isEnabled());
        bluetoothStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    adapter.enable();
                else
                    adapter.disable();
            }
        });

        initDevices();
        initLog();

        sendDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    LogRecord logRecord = new LogRecord(
                        MessageFormat.format(getString(R.string.sending), sendData.getText().toString()),
                        LogRecordType.TO_DEVICE);
                    log(logRecord);
                    logger.debug(logRecord.getMessage());

                    thread.write(sendData.getText().toString().getBytes());
                    sendData.setText("");
                } catch (IOException e) {
                    LogRecord logRecord = new LogRecord(
                        getString(R.string.sendFailed),
                        LogRecordType.INFO);
                    log(logRecord);
                    logger.error(logRecord.getMessage(), e);
                }
            }
        });
    }

    private void initLog() {
        logAdapter = new LogAdapter(this, log);
        logListView.setAdapter(logAdapter);

        logListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View view, final ContextMenu.ContextMenuInfo menuInfo) {
                menu.clear();

                // edit
                MenuItem clearLogItem = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.clearLog);
                clearLogItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        clearLog();
                        return true;
                    }
                });
            }
        });
    }

    private void clearLog() {
        log.clear();
        logAdapter.notifyDataSetChanged();
    }

    private void initDevices() {
        devicesAdapter = new DevicesAdapter(this, devices);
        devicesListView.setAdapter(devicesAdapter);
        devicesListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View view, final ContextMenu.ContextMenuInfo menuInfo) {
                menu.clear();

                // connect menu item
                MenuItem connectItem = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.connect);
                connectItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                        BluetoothDevice selectedDevice = devicesAdapter.getItem(info.position);
                        connectDevice(selectedDevice);
                        return true;
                    }
                });
            }
        });
    }

    private void disconnect() {
        if (thread == null)
            return;

        try {
            thread.cancel();
        } catch (IOException e) {
            LogRecord logRecord = new LogRecord(getString(R.string.disconnectFailed), LogRecordType.INFO);
            logger.error(logRecord.getMessage(), e);
            Toast.makeText(this, R.string.failedToDisconnect, Toast.LENGTH_SHORT).show();
        }
    }

    private BluetoothSocket socket;
    private BluetoothThread thread;

    private static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private void connectDevice(BluetoothDevice device) {
        if (thread != null) {
            disconnect();
        }

        if (adapter.isDiscovering())
            adapter.cancelDiscovery();

        try {
            LogRecord logRecord = new LogRecord(
                    MessageFormat.format(getString(R.string.pairing), device.getName(), device.getAddress()),
                    LogRecordType.INFO);
            log(logRecord);
            logger.debug(logRecord.getMessage());

            socket = device.createRfcommSocketToServiceRecord(SSP_UUID);
            LogRecord paired = new LogRecord(getString(R.string.paired), LogRecordType.INFO);
            log(paired);
            logger.debug(paired.getMessage());

            LogRecord connecting = new LogRecord(getString(R.string.connecting), LogRecordType.INFO);
            log(connecting);
            logger.debug(connecting.getMessage());

            thread = BluetoothThread.newInstance(socket, bluetoothListener);
        } catch (Exception e) {
            LogRecord logRecord = new LogRecord(
                getString(R.string.connectFailed),
                LogRecordType.INFO);

            log(logRecord);
            logger.error(logRecord.getMessage(), e);
            Toast.makeText(this, R.string.failedToConnect, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    private void onReceivedString(String stringData) {
        LogRecord logRecord = new LogRecord(
            MessageFormat.format(getString(R.string.received), stringData.replaceAll("\r\n", " ")),
            LogRecordType.FROM_DEVICE);
        log(logRecord);
        logger.debug(logRecord.getMessage());
    }

    private void onConnected(boolean connected) {
        int visibility = connected ? View.VISIBLE : View.GONE;

        sendBar.setVisibility(visibility);
        disconnectButton.setVisibility(visibility);
    }

    private BluetoothThread.Listener bluetoothListener = new BluetoothThread.Listener() {
        public void onConnected() {
            LogRecord logRecord = new LogRecord(
                getString(R.string.connected),
                LogRecordType.INFO);
            log(logRecord);
            logger.debug(logRecord.getMessage());

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.onConnected(true);
                }
            });
        }

        public void onDisconnected() {
            LogRecord logRecord = new LogRecord(
                getString(R.string.disconnected),
                LogRecordType.INFO);
            log(logRecord);
            logger.debug(logRecord.getMessage());

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.onConnected(false);
                }
            });
        }

        public void onError(IOException e) {
            LogRecord logRecord = new LogRecord(
                getString(R.string.ioError),
                LogRecordType.FROM_DEVICE);
            log(logRecord);
            logger.error(logRecord.getMessage(), e);
        }

        public void onReceived(byte[] buffer, int length) {
            // copy to string
            final String stringData = new String(buffer, 0, length);
            runOnUiThread(new Runnable() {
                public void run() {
                    onReceivedString(stringData);
                }
            });
        }
    };


}
