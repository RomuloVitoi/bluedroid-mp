package uk.ac.gcu.bluedroid;

import uk.ac.gcu.bluedroid.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MapActivity extends Activity {
	ScrollView scrollY;
	HorizontalScrollView scrollX;
	RelativeLayout mapContainer;
	private Map map;
	private GestureDetectorCompat mDetector;
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		map = new Map();
		
		scrollY = (ScrollView) findViewById(R.id.scrollY);
		scrollX = (HorizontalScrollView) findViewById(R.id.scrollX);

		mapContainer = (RelativeLayout) findViewById(R.id.map);

		mDetector = new GestureDetectorCompat(this, new MyOnGestureListener());

		ImageView test = new ImageView(this);
		test.setImageResource(R.drawable.test);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_START);
		lp.topMargin = DPtoPX(this, 2 * 21.35f);
		lp.leftMargin = DPtoPX(this, 27 * 21.35f);

		mapContainer.addView(test, lp);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		scrollX.dispatchTouchEvent(event);
		scrollY.onTouchEvent(event);
		mDetector.onTouchEvent(event);
		return true;
	}

	int DPtoPX(Context context, float dp) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
	}

	class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			int tmpX = ((int) event.getX() + scrollX.getScrollX());
			int tmpY = ((int) event.getY() + scrollY.getScrollY());

			Toast.makeText(
					context,
					"(" + tmpX + "," + tmpY + ")",
					Toast.LENGTH_SHORT).show();
			return true;
		}
	}
	
	public static float convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}
}
