package uk.ac.gcu.bluedroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity implements OnClickListener
{
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_RECEIVED = 2;
    public static final int MESSAGE_SENT = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    private static final String START_GAME = "start";
    private static final String END_GAME = "end";
    
    private Button startButton, connectionButton, mapButton1, mapButton2, mapButton3;
	private Context context;
	private boolean gameOn = false;
	private boolean server = false;
	private int player = 0;
	private boolean myTurn = false;
	
	ScrollView scrollY;
	HorizontalScrollView scrollX;
	RelativeLayout mapContainer;
	private GameState state;
	private GestureDetectorCompat mDetector;
	
	Unit selected = null;
	
	private final int ACTION_NONE = 0;
	private final int ACTION_MOVE = 1;
	private final int ACTION_ATTACK = 2;
	
	private int action = ACTION_NONE;
	    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        context = this;
        
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set up the window layout
        setContentView(R.layout.main);
        
		startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(this);
		startButton.setVisibility(View.GONE);
		
		connectionButton = (Button) findViewById(R.id.connectionButton);
		connectionButton.setOnClickListener(this);

		mapButton1 = (Button) findViewById(R.id.mapButton1);
		mapButton1.setOnClickListener(this);
		/*
		mapButton2 = (Button) findViewById(R.id.mapButton2);
		mapButton2.setOnClickListener(this);
		mapButton3 = (Button) findViewById(R.id.mapButton3);
		mapButton3.setOnClickListener(this);
		*/
		mapButton1.setVisibility(View.GONE);
		/*
		mapButton2.setVisibility(View.GONE);
		mapButton3.setVisibility(View.GONE);
		*/

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) 
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    
	@Override
	public void onClick(View v) 
	{
		if (v==startButton) 
		{
			mapButton1.setVisibility(View.VISIBLE);
			/*
			mapButton2.setVisibility(View.VISIBLE);
			mapButton3.setVisibility(View.VISIBLE);
			*/
			connectionButton.setVisibility(View.GONE);
			startButton.setVisibility(View.GONE);
		}

		if(v==connectionButton)
		{
			Log.d(TAG, "----Connections Button----");
			showcustomDialog();
		}

		if(v==mapButton1)
		{
			setupMap(1);
		}

		if(v==mapButton2)
		{
			setupMap(2);
		}

		if(v==mapButton3)
		{
			setupMap(3);
		}
	}
	
	private void setupMap(int mapId){
		Log.d(TAG, "----Start Button----");
		
		if(server)
			sendStart(mapId);
		
		gameOn = true;
		
		setContentView(R.layout.map);
		
		state = new GameState();
		
		scrollY = (ScrollView) findViewById(R.id.scrollY);
		scrollX = (HorizontalScrollView) findViewById(R.id.scrollX);

		mapContainer = (RelativeLayout) findViewById(R.id.map);

		mDetector = new GestureDetectorCompat(this, new MyOnGestureListener());
		
		findViewById(R.id.endturn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(myTurn)
					sendState();
			}
		});
			
		drawEverything();
	}
	
	private void showcustomDialog()
	{
		final Dialog dialog = new Dialog(this);

		//Gets the dialogs XML file.
		dialog.setContentView(R.layout.dialog_info);
		dialog.setTitle("Connection Menu");
		dialog.setCancelable(true);

		//Intent serverIntent = null;

		Button dialogButton1 = (Button) dialog.findViewById(R.id.connectDialogButton);			
		dialogButton1.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent serverIntent = new Intent(context, DeviceListActivity.class);
				startActivityForResult(serverIntent,
						REQUEST_CONNECT_DEVICE_INSECURE);

				dialog.dismiss();
			}
		});

		Button dialogButton2 = (Button) dialog.findViewById(R.id.discoverableDialogButton);			
		dialogButton2.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				ensureDiscoverable();
				dialog.dismiss();
			}
		});
		dialog.show();	
	}

    @Override
    public void onStart() 
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() 
    {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() 
    {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() 
    {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() 
    {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() 
    {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() 
    {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private boolean checkConnected() {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) 
        {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return false;
        } else
        	return true;
    }
    
    private void sendStart(int mapId) {
    	if(checkConnected())
    		mChatService.send("string", START_GAME + "," + mapId);
    }
    
    private void sendState() 
    {
    	if(checkConnected())
            mChatService.send("state", state);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) 
        {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    if(server) {
                    	startButton.setVisibility(View.VISIBLE);
                    	player = 1;
                    	myTurn = true;
                    } else
                    	player = 2;
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    break;
                }
                break;
            case MESSAGE_SENT:
            	if(msg.obj instanceof String) {
            		
            	} else {
            		myTurn = false;
            	}
                break;
            case MESSAGE_RECEIVED:
            	if(msg.obj instanceof String) {
            		String message = (String) msg.obj;
            		String[] pieces = message.split(",");

            		if(pieces[0].equals(START_GAME)) {
            			setupMap(Integer.valueOf(pieces[1]));
            		}
            	} else {
            		myTurn = true;
            		state = (GameState) msg.obj;
            		drawEverything();
            	}
            	
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(context, "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(context, msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) 
        {
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) 
            {
                connectDevice(data);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) 
            {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data) 
    {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, false);

        server = true;
    }
    
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		if(gameOn) {
			scrollX.dispatchTouchEvent(event);
			scrollY.onTouchEvent(event);
			mDetector.onTouchEvent(event);
		}
		return true;
	}

	int DPtoPX(Context context, float dp) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
	}
	
	float PXtoDP(Context context, float px){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    return (px / (metrics.densityDpi / 158.4f));
	}
	
	void drawEverything() {
		// Remove views
		for(int i = mapContainer.getChildCount(); i > 0 ; i--) {
			View child = mapContainer.getChildAt(i-1);
			if(child.getId() != R.id.mapimg)
				mapContainer.removeViewAt(i-1);
		}
		
		// Add views
		addUnits();
	}
	
	void addUnits() {
		for(int i = 0; i < state.map.getX(); i++) {
			for(int j = 0; j < state.map.getY(); j++) {
				Unit u = state.map.getUnit(i, j);
				if(u == null)
					continue;
				
				ImageView unit = new ImageView(this);
						
				unit.setImageResource(getResources().getIdentifier((u.prefix + u.getOwner()), "drawable", getPackageName()));
				
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(DPtoPX(this, 50), DPtoPX(this, 50));
				lp.addRule(RelativeLayout.ALIGN_PARENT_START);
				lp.leftMargin = DPtoPX(this, i * 50f);
				lp.topMargin = DPtoPX(this, j * 50f);
		
				mapContainer.addView(unit, lp);
			}
		}
	}

	class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			if(!myTurn)
				return true;
			
			Point size = new Point();
			Display display = getWindowManager().getDefaultDisplay();
			display.getSize(size);

			if(event.getY() < DPtoPX(context, 50) || event.getY() > size.y - DPtoPX(context, 50))
				return true;

			int tmpX = ((int) event.getX() + scrollX.getScrollX());
			int tmpY = ((int) event.getY() + scrollY.getScrollY() - DPtoPX(context, 50));

			int x = (int) Math.floor((PXtoDP(context, tmpX)/50));
			int y = (int) Math.floor((PXtoDP(context, tmpY)/50));

			switch(action) {
			case ACTION_NONE:
				selected = state.map.getUnit(x, y);
				if(selected != null) { 
					if(selected.getOwner() != player)
						return true;
					
					View view = getLayoutInflater().inflate(R.layout.actions, null);
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setView(view);
					final Dialog dialog = builder.show();
					
					TextView name = (TextView) view.findViewById(R.id.name);
					TextView life = (TextView) view.findViewById(R.id.life);
					ProgressBar lifebar = (ProgressBar) view.findViewById(R.id.lifebar);
					
					if(selected.prefix.equals("a"))
						name.setText(R.string.archer);
					else if(selected.prefix.equals("i"))
						name.setText(R.string.soldier);
					else if(selected.prefix.equals("c"))
						name.setText(R.string.paladin);
					
					life.setText(selected.life + "/" + selected.max_life);
					lifebar.setProgress(Math.round(100*selected.life/(float)selected.max_life));
					
					Button move = (Button) view.findViewById(R.id.move);
					Button attack = (Button) view.findViewById(R.id.attack);
					
					move.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							action = ACTION_MOVE;
							dialog.dismiss();
						}
					});
					
					attack.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							action = ACTION_ATTACK;
							dialog.dismiss();
						}
					});
				}
				break;
			case ACTION_MOVE:
				if(selected != null) {
					Log.e("pos", x + " " + y);
					if(!state.map.walkable(x, y))
						Toast.makeText(context, R.string.cantMove, Toast.LENGTH_SHORT).show();
					//else if(!map.canWalkTo(selected.getPosition().x, selected.getPosition().y, x, y, selected.move))
					//	Toast.makeText(MapActivity.this, R.string.tooFar, Toast.LENGTH_SHORT).show();
					else
						state.map.moveUnit(selected, x, y);
					
					selected = null;
					action = ACTION_NONE;
				}
				break;
			case ACTION_ATTACK:
				if(selected != null) {
					Unit target = state.map.getUnit(x, y);
					if(target != null) {
						if(Math.abs(selected.getPosition().x - target.getPosition().x) + Math.abs(selected.getPosition().y - target.getPosition().y) <= selected.range) {
						target.takeDemage(selected.power);
						
						if(target.life == 0)
							state.map.removeUnit(target);
						} else
							Toast.makeText(context, R.string.tooFar, Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(context, R.string.noTarget, Toast.LENGTH_SHORT).show();
					
					selected = null;
					action = ACTION_NONE;
				}
				break;
			}
			
			drawEverything();
			return true;
		}
	}
}