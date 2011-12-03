package name.antonsmirnov.android.stalker.log;

import android.view.View;
import android.widget.TextView;
import name.antonsmirnov.android.stalker.R;

/**
 * Log ListView item holder
 */
public class LogHolder {

    private TextView time;
    private TextView type;
    private TextView message;

    public TextView getTime() {
        return time;
    }

    public TextView getType() {
        return type;
    }

    public TextView getMessage() {
        return message;
    }

    public LogHolder(View rowView) {
        time = (TextView) rowView.findViewById(R.id.logrecord_time);
        type = (TextView) rowView.findViewById(R.id.logrecord_type);
        message = (TextView) rowView.findViewById(R.id.logrecord_message);
    }
}
