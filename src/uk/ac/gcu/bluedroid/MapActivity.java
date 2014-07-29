package uk.ac.gcu.bluedroid;

import uk.ac.gcu.bluedroid.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class MapActivity extends Activity {
	ScrollView scrollY;
	HorizontalScrollView scrollX;
	RelativeLayout map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		scrollY = (ScrollView) findViewById(R.id.scrollY);
		scrollX = (HorizontalScrollView) findViewById(R.id.scrollX);

		map = (RelativeLayout) findViewById(R.id.map);

		ImageView test = new ImageView(this);
		test.setImageResource(R.drawable.test);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_START);
		lp.topMargin = DPtoPX(this, 34);
		lp.leftMargin = DPtoPX(this, 34);

		map.addView(test, lp);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		scrollX.dispatchTouchEvent(event);
		scrollY.onTouchEvent(event);
		return true;
	}

	int DPtoPX(Context context, int dp) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dp, r.getDisplayMetrics());
	}
}
