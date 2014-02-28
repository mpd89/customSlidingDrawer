package mil.darpa.mesa.sdk.android.widgets.drawer;

import android.view.View;

public class ListEntry {
    public int icon;
    public String title;
    public View.OnClickListener listener;

    public ListEntry() {
        super();

    }

    public ListEntry(int icon, String title, View.OnClickListener listener) {
        super();
        this.icon = icon;
        this.title = title;
        this.listener = listener;
    }
}
