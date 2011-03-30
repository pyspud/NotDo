package uk.co.kalgan.app.notdo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotDoItem {
	
	String task;
	Date created;
	
	public String getTask() {
		return task;
	}
	
	public Date getCreated() {
		return created;
	}

	public NotDoItem(String _task) {
		this(_task, new Date(java.lang.System.currentTimeMillis()));
	}
	
	public NotDoItem(String _task, Date _created) {
		task = _task;
		created = _created;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(created);
		return "(" + dateString + ")" + task;
	}
}
