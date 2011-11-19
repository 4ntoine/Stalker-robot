package name.antonsmirnov.android.stalker.log;

import name.antonsmirnov.android.stalker.R;

/**
 * LogRecotd Type
 */
public enum LogRecordType {
    INFO("info", R.color.info),
    FROM_DEVICE("<<", R.color.fromDevice),
    TO_DEVICE(">>", R.color.toDevice);

    private String title;
    private int textColor;

    public String getTitle() {
        return title;
    }

    public int getTextColor() {
        return textColor;
    }

    private LogRecordType(String title, int textColor) {
        this.title = title;
        this.textColor = textColor;
    }
}
