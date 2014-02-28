package mil.darpa.mesa.sdk.android.widgets.drawer;

import com.example.animationtest.R;
import com.example.animationtest.R.id;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class SlidingAdapter extends ArrayAdapter<ListEntry> {

	ListEntry data[] = null;
	Context mCtx;
	int layoutResourceId;
	int imageSize;
	ScaleType mScaleType;

	public SlidingAdapter(Context context, int layoutResourceId,
			ListEntry[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mCtx = context;
		this.data = data;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ListEntryHolder holder = null;

		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mCtx).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ListEntryHolder();
			holder.imgIcon = (Button) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			holder.listener = null;
			row.setTag(holder);

		} else {
			holder = (ListEntryHolder) row.getTag();
			Button image = (Button) row.findViewById(R.id.imgIcon);
			image.setOnClickListener(holder.listener);

		}
		ListEntry entry = data[position];
		holder.txtTitle.setText(entry.title);
		holder.imgIcon.setBackgroundResource(entry.icon);
		holder.imgIcon.setOnClickListener(entry.listener);

		return row;
	}

	

	static class ListEntryHolder {
		Button imgIcon;
		TextView txtTitle;
		View.OnClickListener listener;
	}

}