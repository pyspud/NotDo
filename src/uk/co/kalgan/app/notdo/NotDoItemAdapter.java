package uk.co.kalgan.app.notdo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotDoItemAdapter extends ArrayAdapter<NotDoItem> {
	
	int resource;

	public NotDoItemAdapter(Context _context, int _resource,
			List<NotDoItem> _items) {
		super(_context, _resource, _items);
		resource = _resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout notDoView;
		
		NotDoItem item = getItem(position);
		
		String taskString = item.getTask();
		Date createdDate = item.getCreated();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(createdDate);
		
		if (convertView == null) {
			notDoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, notDoView, true);
		} else {
			notDoView = (LinearLayout) convertView;
		}
		
		TextView dateView = (TextView)notDoView.findViewById(R.id.rowDate);
		TextView taskView = (TextView)notDoView.findViewById(R.id.row);
		
		dateView.setText(dateString);
		taskView.setText(taskString);
		
		return notDoView;
	}

}
