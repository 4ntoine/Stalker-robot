package name.antonsmirnov.android.stalker.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import name.antonsmirnov.android.stalker.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Log listView adapter
 */
public class LogAdapter extends ArrayAdapter<LogRecord> {

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public LogAdapter(Context context, List<LogRecord> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.log, null);
            convertView.setTag(new LogHolder(convertView));
        }

        LogRecord logRecord = getItem(position);
        int textColor = getContext().getResources().getColor(logRecord.getType().getTextColor());

        LogHolder holder = (LogHolder) convertView.getTag();
        holder.getTime().setText(format.format(logRecord.getTime()));
        holder.getTime().setTextColor(textColor);

        holder.getType().setText(logRecord.getType().getTitle());
        holder.getType().setTextColor(textColor);

        holder.getMessage().setText(logRecord.getMessage());
        holder.getMessage().setTextColor(textColor);

        return convertView;
    }
}
