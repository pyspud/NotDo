package uk.co.kalgan.app.notdo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class NotDoListItemView extends TextView {

	public NotDoListItemView(Context context) {
		super(context);
		init();
	}

	public NotDoListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NotDoListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private Paint marginPaint;
	private Paint linePaint;
	private int paperColor;
	private float margin;
	
	private void init() {
		// Get a reference to our resource table
		Resources myResources = getResources();
		
		// Create the paint brushes we will need in the onDraw method.
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		marginPaint.setColor(myResources.getColor(R.color.notepad_margin));
		
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.notepad_lines));
		
		// Get the paper background color and margin width
		paperColor = myResources.getColor(R.color.notepad_paper);
		margin = myResources.getDimension(R.dimen.notepad_margin);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// Color as paper
		canvas.drawColor(paperColor);
		
		// Draw ruled lines
		canvas.drawLine(0, 0, getMeasuredHeight(), 0, linePaint);
		canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(),
				getMeasuredHeight(), linePaint);
		
		// Draw Margin
		canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
		
		// Move the Text across from the margin
		canvas.save();
		canvas.translate(margin, 0);
		
		// Use base TextView to render the text.
		super.onDraw(canvas);
		canvas.restore();
	}
}
