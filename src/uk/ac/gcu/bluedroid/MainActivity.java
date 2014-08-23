package uk.ac.gcu.bluedroid;

import uk.ac.gcu.bluedroid.game.GameState;
import uk.ac.gcu.bluedroid.game.Player;
import uk.ac.gcu.bluedroid.game.TurnInfo;
import uk.ac.gcu.bluedroid.resources.Camp;
import uk.ac.gcu.bluedroid.resources.Crop;
import uk.ac.gcu.bluedroid.resources.Mine;
import uk.ac.gcu.bluedroid.resources.Resource;
import uk.ac.gcu.bluedroid.units.Archer;
import uk.ac.gcu.bluedroid.units.Paladin;
import uk.ac.gcu.bluedroid.units.Soldier;
import uk.ac.gcu.bluedroid.units.Unit;
import uk.ac.gcu.bluedroid.util.CustomImageVIew;
import uk.ac.gcu.bluedroid.util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Html;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class MainActivity extends Activity implements OnClickListener {
	// Debugging
	private static final String TAG = "MainActivity";
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

	// Game variables

	private static final String START_GAME = "start";
	private static final String END_GAME = "end";

	private Button startButton, connectionButton, mapButton1, mapButton2,
			mapButton3, exitButton; // buttons
	private final Context context = this;
	private boolean gameOn = false;
	private boolean server = false;
	private int player = 0;
	private boolean isMyTurn = false;

	private ScrollView scrollY;
	private HorizontalScrollView scrollX;
	private RelativeLayout mapContainer;

	private GameState state;
	private GestureDetectorCompat mDetector;

	// Selected unit and resource
	private Unit selectedUnit = null;
	private Resource selectedResource = null;

	// Constants
	private static final int ACTION_NONE = 0;
	private static final int ACTION_MOVE = 1;
	private static final int ACTION_ATTACK = 2;

	// Current action
	private int action = ACTION_NONE;

	private ProgressDialog progressDialog = null;

	private View viewMap = null;

	private TurnInfo myTurn = null, enemyTurn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Set up the window layout
		setContentView(R.layout.main);

		startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(this);
		startButton.setVisibility(View.GONE);

		connectionButton = (Button) findViewById(R.id.connectionButton);
		connectionButton.setOnClickListener(this);

		mapButton1 = (Button) findViewById(R.id.mapButton1);
		mapButton1.setOnClickListener(this);

		mapButton2 = (Button) findViewById(R.id.mapButton2);
		mapButton2.setClickable(false);

		mapButton3 = (Button) findViewById(R.id.mapButton3);
		mapButton3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.simpleAlertDialog("Oops!",
						"I'm sorry.\nThis map is not available yet", context);
			}
		});
		
		exitButton = (Button) findViewById(R.id.exit);
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				View view = getLayoutInflater().inflate(R.layout.exit_confirmation, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setView(view);
				final Dialog dialog = builder.show();
				
				view.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MainActivity.this.finish();
						
					}
				});
				
				view.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						
					}
				});
				
			}
		});

		mapButton1.setVisibility(View.GONE);
		mapButton2.setVisibility(View.GONE);
		mapButton3.setVisibility(View.GONE);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == startButton) {
			mapButton1.setVisibility(View.VISIBLE);
			mapButton2.setVisibility(View.VISIBLE);
			mapButton3.setVisibility(View.VISIBLE);

			connectionButton.setVisibility(View.GONE);
			startButton.setVisibility(View.GONE);
		}

		if (v == connectionButton) {
			Log.d(TAG, "----Connections Button----");
			showCustomDialog();
		}

		if (v == mapButton1) {
			setupMap(1);
		}

		if (v == mapButton2) {
			setupMap(2);
		}

		if (v == mapButton3) {
			setupMap(3);
		}
	}

	/**
	 * 
	 * @param mapId
	 */
	@SuppressLint("InflateParams")
	private void setupMap(int mapId) {
		
		Log.d(TAG, "----Start Button----");

		if (server)
			sendStart(mapId);

		gameOn = true;

		viewMap = getLayoutInflater().inflate(R.layout.map, null);

		setContentView(viewMap);

		state = new GameState(this);

		myTurn = new TurnInfo();
		enemyTurn = new TurnInfo();

		scrollY = (ScrollView) findViewById(R.id.scrollY);
		scrollX = (HorizontalScrollView) findViewById(R.id.scrollX);

		mapContainer = (RelativeLayout) findViewById(R.id.map);

		mDetector = new GestureDetectorCompat(this, new MyOnGestureListener());

		findViewById(R.id.endturn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isMyTurn) {
					progressDialog = new ProgressDialog(context);
					progressDialog.setMessage("Waiting for other Player..");
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.show();
					// sendState();
				}
			}
		});

		if (!isMyTurn) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Waiting for other Player..");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}

		drawEverything();
	}

	private void showCustomDialog() {
		final Dialog dialog = new Dialog(this);

		// Gets the dialogs XML file.
		dialog.setContentView(R.layout.menu);
		dialog.setTitle("Connection Menu");
		dialog.setCancelable(true);

		// Intent serverIntent = null;

		Button dialogButton1 = (Button) dialog
				.findViewById(R.id.connectDialogButton);
		dialogButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent serverIntent = new Intent(context,
						DeviceListActivity.class);
				startActivityForResult(serverIntent,
						REQUEST_CONNECT_DEVICE_INSECURE);

				dialog.dismiss();
			}
		});

		Button dialogButton2 = (Button) dialog
				.findViewById(R.id.discoverableDialogButton);
		dialogButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ensureDiscoverable();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	/**
	 * 
	 */
	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean checkConnected() {
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else
			return true;
	}

	/**
	 * 
	 * @param mapId
	 */
	private void sendStart(int mapId) {
		if (checkConnected())
			mChatService.send("string", START_GAME + "," + mapId);
	}

	private void sendTurn() {
		if (checkConnected()) {
			mChatService.send("turn_info", myTurn);
			showProgressDialog();
			myTurn.resetValues();
		}
	}

	/**
	 * TODO: THIS METHOD IS NOT WORKING PROPERLY
	 */
	private void sendState() {
		if (checkConnected()) {
			mChatService.send("state", state);
		}
	}

	/**
	 * The Handler that gets information back from the BluetoothChatService
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					if (server) {
						startButton.setVisibility(View.VISIBLE);
						player = 1;
						isMyTurn = true;
					} else {
						player = 2;
						// isMyTurn = false; //TODO conferir
					}
					break;
				case BluetoothChatService.STATE_CONNECTING:
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					break;
				}
				break;
			case MESSAGE_SENT:
				if (msg.obj instanceof String) {

				} else {
					isMyTurn = false;
				}
				break;
			case MESSAGE_RECEIVED:
				if (msg.obj instanceof String) {
					String message = (String) msg.obj;
					String[] pieces = message.split(",");
					if (pieces[0].equals(START_GAME)) {
						setupMap(Integer.valueOf(pieces[1]));
					}
				} else {
					isMyTurn = true;
					// progressDialog.dismiss();
					enemyTurn = (TurnInfo) msg.obj;
					// Toast.makeText(context, "Enemy moved!",
					// Toast.LENGTH_SHORT).show();

					if (enemyTurn.getHasMoved())
						state.getMap().moveUnit(
								state.getMap().getUnit(
										enemyTurn.getUnitStartPos()),
								enemyTurn.getUnitEndPos().getX(),
								enemyTurn.getUnitEndPos().getY());

					if ((enemyTurn.getHasAttacked())
							&& (enemyTurn.getHasMoved()))
						state.getMap()
								.getUnit(enemyTurn.getUnitTargetPos())
								.takeDemage(
										state.getMap()
												.getUnit(
														enemyTurn
																.getUnitEndPos())
												.getPower());

					if ((enemyTurn.getHasAttacked())
							&& !(enemyTurn.getHasMoved()))
						state.getMap()
								.getUnit(enemyTurn.getUnitTargetPos())
								.takeDemage(
										state.getMap()
												.getUnit(
														enemyTurn
																.getUnitStartPos())
												.getPower());

					if (enemyTurn.getRecruitedUnit() != (TurnInfo.HAS_NOT_RECRUITED)) {
						if (enemyTurn.getRecruitedUnit() == TurnInfo.ARCHER_RECRUITED) {
							state.getMap().addUnit(
									new Archer(player == 0 ? 1 : 0, enemyTurn
											.getRecruitPos()));
						} else if (enemyTurn.getRecruitedUnit() == TurnInfo.PALADIN_RECRUITED) {
							state.getMap().addUnit(
									new Paladin(player == 0 ? 1 : 0, enemyTurn
											.getRecruitPos()));
						} else if (enemyTurn.getRecruitedUnit() == TurnInfo.SOLDIER_RECRUITED) {
							state.getMap().addUnit(
									new Soldier(player == 0 ? 1 : 0, enemyTurn
											.getRecruitPos()));
						}
						((Camp) state.getMap().getResource(
								enemyTurn.getRecruitPos().getX(),
								enemyTurn.getRecruitPos().getY()))
								.setWorking(false);

					}

					// if (enemyTurn.getHasConquered())

					enemyTurn.resetValues();
					// myTurn.resetValues();
					drawEverything();
					progressDialog.dismiss();
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(context, "Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(context, msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	/**
	 * 
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	/**
	 * 
	 * @param data
	 */
	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, false);

		server = true;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		if (gameOn) {
			scrollX.dispatchTouchEvent(event);
			scrollY.onTouchEvent(event);
			mDetector.onTouchEvent(event);
		}
		return true;
	}

	/**
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	int DPtoPX(Context context, float dp) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
	}

	/**
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	float PXtoDP(Context context, float px) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return (px / (metrics.densityDpi / 158.4f));
	}

	/**
	 * 
	 */
	void drawEverything() {
		// Remove views
		for (int i = mapContainer.getChildCount(); i > 0; i--) {
			View child = mapContainer.getChildAt(i - 1);
			if (child.getId() != R.id.mapimg)
				mapContainer.removeViewAt(i - 1);
		}

		// Add views
		addUnits();
		// Organize header
		refreshHeader(this.player);
	}

	/**
	 * 
	 */
	private void addUnits() {
		for (int i = 0; i < state.getMap().getX(); i++) {
			for (int j = 0; j < state.getMap().getY(); j++) {
				Unit u = state.getMap().getUnit(i, j);
				if (u == null)
					continue;

				ImageView unit = new ImageView(this);
				unit.setImageResource(getResources().getIdentifier(
						(u.getPrefix() + u.getOwner()), "drawable",
						getPackageName()));

				ProgressBar lifeBar = new ProgressBar(this, null,
						android.R.attr.progressBarStyleHorizontal);

				if ((Math.round(100 * u.getLife() / (float) u.getMax_life())) < 50) {
					Drawable drawable = lifeBar.getProgressDrawable();
					drawable.setColorFilter(new LightingColorFilter(0x00000000,
							Color.RED));
				}

				lifeBar.setProgress(Math.round(100 * u.getLife()
						/ (float) u.getMax_life()));

				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						DPtoPX(this, 50), DPtoPX(this, 50));
				lp.addRule(RelativeLayout.ALIGN_PARENT_START);
				lp.leftMargin = DPtoPX(this, i * 50f);
				lp.topMargin = DPtoPX(this, j * 50f);

				RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
						DPtoPX(this, 50), DPtoPX(this, 50));
				lp2.addRule(RelativeLayout.BELOW, unit.getId());
				lp2.leftMargin = DPtoPX(this, i * 50f);
				lp2.topMargin = DPtoPX(this, (j * 50f) - 30f);

				mapContainer.addView(unit, lp);
				mapContainer.addView(lifeBar, lp2);
			}
		}
	}

	/**
	 * Refreshes header information of the game
	 * 
	 * @param player
	 *            player who is on turn
	 */
	private void refreshHeader(final int player) {

		Button resources = (Button) viewMap.findViewById(R.id.resourcesButton);
		TextView food = (TextView) viewMap.findViewById(R.id.foodValue);
		TextView gold = (TextView) viewMap.findViewById(R.id.goldValue);
		TextView camps = (TextView) viewMap.findViewById(R.id.campValue);
		TextView player_text = (TextView) viewMap
				.findViewById(R.id.playerValue);

		player_text.setText(String.valueOf(this.player));
		food.setText(String.valueOf(state.getPlayers()[player - 1].getFood()));
		gold.setText(String.valueOf(state.getPlayers()[player - 1].getGold()));
		camps.setText(String.valueOf(state.getPlayers()[player - 1].getCamps()));

		resources.setOnClickListener(new OnClickListener() {

			@SuppressLint("InflateParams")
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				View view = getLayoutInflater().inflate(R.layout.resources,
						null);
				builder.setView(view);
				final Dialog dialog = builder.show();

				TextView crops = (TextView) view.findViewById(R.id.cropsValue);
				TextView mines = (TextView) view.findViewById(R.id.minesValue);
				TextView camps = (TextView) view.findViewById(R.id.campsValue);
				Button ok = (Button) view.findViewById(R.id.ok);

				crops.setText(String.valueOf(state.getPlayers()[player - 1]
						.getCrops()));
				mines.setText(String.valueOf(state.getPlayers()[player - 1]
						.getMines()));
				camps.setText(String.valueOf(state.getPlayers()[player - 1]
						.getCamps()));

				ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

			}
		});
	}

	private void showProgressDialog() {

		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Waiting for Other Player..");
		progressDialog.show();
		progressDialog.setCanceledOnTouchOutside(false);
	}

	@SuppressLint("InflateParams")
	private void unitAction() {
		// Unit dialog creation
		View view = getLayoutInflater().inflate(R.layout.actions, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		TextView name = (TextView) view.findViewById(R.id.name);
		TextView life = (TextView) view.findViewById(R.id.life);
		TextView id = (TextView) view.findViewById(R.id.id);
		TextView player_text = (TextView) view.findViewById(R.id.player);
		TextView power = (TextView) view.findViewById(R.id.power);
		TextView attack_range = (TextView) view.findViewById(R.id.attack_range);
		TextView move_range = (TextView) view.findViewById(R.id.move_range);

		ProgressBar lifebar = (ProgressBar) view.findViewById(R.id.lifebar);

		ImageView unit = (ImageView) view.findViewById(R.id.imageView1);

		unit.setImageResource(getResources().getIdentifier(
				(selectedUnit.getPrefix() + selectedUnit.getOwner()),
				"drawable", getPackageName()));

		if (selectedUnit.getPrefix().equals("a")) { // if it's an archer
			name.setText(R.string.archer);
		} else if (selectedUnit.getPrefix().equals("i")) { // if it's a soldier
			name.setText(R.string.soldier);
		} else if (selectedUnit.getPrefix().equals("c")) { // if it's a paladin
			name.setText(R.string.paladin);
		}
		name.setTypeface(null, Typeface.BOLD);

		player_text.setText(Html.fromHtml("<b>Player ID: </b>"
				+ selectedUnit.getOwner()));
		power.setText(Html.fromHtml("<b>Power: </b>" + selectedUnit.getPower()));
		life.setText(Html.fromHtml("<b>Life: </b>" + selectedUnit.getLife()
				+ "/" + selectedUnit.getMax_life()));
		lifebar.setProgress(Math.round(100 * selectedUnit.getLife()
				/ (float) selectedUnit.getMax_life()));
		Button cancel = (Button) view.findViewById(R.id.cancel);
		Button move = (Button) view.findViewById(R.id.move);
		Button attack = (Button) view.findViewById(R.id.attack);

		cancel.setOnClickListener(new OnClickListener() { // cancel button
															// action
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		if (selectedUnit.getOwner() != player) { // if the unity it's an enemy
			id.setText("ENEMY");
			id.setTypeface(null, Typeface.BOLD);
			id.setTextColor(Color.RED);
			move.setVisibility(View.GONE);
			attack.setVisibility(View.GONE);
			move_range.setVisibility(View.GONE);
			attack_range.setVisibility(View.GONE);
		} else { // if it's a current player unit
			id.setText(Html.fromHtml("<b>Unit ID: </b>" + selectedUnit.getId()));
			attack_range.setText(Html.fromHtml("<b>Attack range: </b>"
					+ selectedUnit.getRange()));
			move_range.setText(Html.fromHtml("<b>Move range: </b>"
					+ selectedUnit.getMove()));

			move.setOnClickListener(new OnClickListener() { // move action
				@Override
				public void onClick(View arg0) {
					action = ACTION_MOVE;
					dialog.dismiss();
				}
			});

			attack.setOnClickListener(new OnClickListener() { // attack action
				@Override
				public void onClick(View arg0) {
					action = ACTION_ATTACK;
					dialog.dismiss();
				}
			});
		}
	}

	@SuppressLint("InflateParams")
	private void resourceAction() {
		// Resource dialog creation
		View view = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (selectedResource instanceof Mine
				|| selectedResource instanceof Crop) {
			view = getLayoutInflater().inflate(R.layout.resource, null);
			builder.setView(view);
			final Dialog dialog = builder.show();

			TextView type = (TextView) view.findViewById(R.id.type);
			TextView owner = (TextView) view.findViewById(R.id.owner);
			Button cancel = (Button) view.findViewById(R.id.cancel);

			owner.setText(Html.fromHtml("<b>Owner: </b>"
					+ (selectedResource.getOwner() == 0 ? "None" : "Player: "
							+ selectedResource.getOwner())));

			if (selectedResource instanceof Mine) {
				type.setText(Html.fromHtml("<b>Type: </b>Mine"));
			} else if (selectedResource instanceof Crop) {
				type.setText(Html.fromHtml("<b>Type: </b>Crop"));
			}

			cancel.setOnClickListener(new OnClickListener() { // cancel action
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

		} else if (selectedResource instanceof Camp) { // if it's a camp
			if (selectedResource.getOwner() == 0) { // if the camp has no owner
				// setup dialog
				view = getLayoutInflater().inflate(R.layout.camp_alert_no_owner, null);
				builder.setView(view);
				final Dialog dialog2 = builder.show();
				Button ok = (Button) view.findViewById(R.id.ok);
				ok.setOnClickListener(new OnClickListener() { // ok button
																// action
					@Override
					public void onClick(View arg0) {
						dialog2.dismiss();
					}
				});
			} else if (((Camp) selectedResource).isWorking() == false) { // if
																			// the
																			// camp
																			// has
																			// a
																			// owner
																			// but
																			// is
																			// not
																			// working
				// setup dialog
				view = getLayoutInflater().inflate(R.layout.camp_alert2, null);
				builder.setView(view);
				final Dialog dialog2 = builder.show();
				Button ok = (Button) view.findViewById(R.id.ok);
				ok.setOnClickListener(new OnClickListener() { // ok button
																// action
					@Override
					public void onClick(View arg0) {
						dialog2.dismiss();
					}
				});
			} else { // if has a owner, can recrute some unit
				view = getLayoutInflater().inflate(R.layout.camp, null);
				builder.setView(view);
				final Dialog dialog3 = builder.show();

				Button cancel = (Button) view.findViewById(R.id.cancel);
				Button paladin = (Button) view.findViewById(R.id.paladin);
				Button archer = (Button) view.findViewById(R.id.archer);
				Button soldier = (Button) view.findViewById(R.id.soldier);

				paladin.setOnClickListener(new OnClickListener() { // paladin
					// recruitment
					// action
					@Override
					public void onClick(View arg0) {
						if (((Camp) selectedResource).hasEnoughResources(
								Camp.PALADIN, state.getPlayers()[player - 1])) {
							state.getMap().addUnit(
									new Paladin(selectedResource.getOwner(),
											selectedResource.getPosition()));
							state.getPlayers()[player - 1].updateResource(
									Player.GOLD, -Camp.PALADIN_COST_GOLD);
							state.getPlayers()[player - 1].updateResource(
									Player.FOOD, -Camp.PALADIN_COST_FOOD);
							state.getPlayers()[player - 1].updateResource(
									Player.CAMPS, -1);
							drawEverything();
							((Camp) selectedResource).setWorking(false);

							myTurn.setRecruitPos(selectedResource.getPosition());
							myTurn.setRecruitedUnit(TurnInfo.PALADIN_RECRUITED);

						} else {
							Toast.makeText(context, R.string.enoughResources,
									Toast.LENGTH_SHORT).show();
						}
						dialog3.dismiss();
					}
				});
				archer.setOnClickListener(new OnClickListener() { // archer
					// recruitment
					// action
					@Override
					public void onClick(View arg0) {
						if (((Camp) selectedResource).hasEnoughResources(
								Camp.ARCHER, state.getPlayers()[player - 1])) {
							state.getMap().addUnit(
									new Archer(selectedResource.getOwner(),
											selectedResource.getPosition()));
							state.getPlayers()[player - 1].updateResource(
									Player.GOLD, -Camp.ARCHER_COST_GOLD);
							state.getPlayers()[player - 1].updateResource(
									Player.FOOD, -Camp.ARCHER_COST_FOOD);
							state.getPlayers()[player - 1].updateResource(
									Player.CAMPS, -1);
							drawEverything();
							((Camp) selectedResource).setWorking(false);

							myTurn.setRecruitPos(selectedResource.getPosition());
							myTurn.setRecruitedUnit(TurnInfo.ARCHER_RECRUITED);

						} else {
							Toast.makeText(context, R.string.enoughResources,
									Toast.LENGTH_SHORT).show();
						}
						dialog3.dismiss();
					}
				});
				soldier.setOnClickListener(new OnClickListener() {// soldier
					// recruitment
					// action
					@Override
					public void onClick(View arg0) {
						if (((Camp) selectedResource).hasEnoughResources(
								Camp.SOLDIER, state.getPlayers()[player - 1])) {
							state.getMap().addUnit(
									new Soldier(selectedResource.getOwner(),
											selectedResource.getPosition()));
							state.getPlayers()[player - 1].updateResource(
									Player.GOLD, -Camp.SOLDIER_COST_GOLD);
							state.getPlayers()[player - 1].updateResource(
									Player.FOOD, -Camp.SOLDIER_COST_FOOD);
							state.getPlayers()[player - 1].updateResource(
									Player.CAMPS, -1);
							drawEverything();
							((Camp) selectedResource).setWorking(false);

							myTurn.setRecruitPos(selectedResource.getPosition());
							myTurn.setRecruitedUnit(TurnInfo.SOLDIER_RECRUITED);

						} else {
							Toast.makeText(context, R.string.enoughResources,
									Toast.LENGTH_SHORT).show();
						}
						dialog3.dismiss();
					}
				});
				cancel.setOnClickListener(new OnClickListener() { // cancel
																	// action
					@Override
					public void onClick(View arg0) {
						dialog3.dismiss();
					}
				});
				refreshHeader(selectedResource.getOwner());
			}
		}
	}

	class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			Point size = new Point();
			Display display = getWindowManager().getDefaultDisplay();
			display.getSize(size);

			if (event.getY() < DPtoPX(context, 50)
					|| event.getY() > size.y - DPtoPX(context, 50))
				return true;

			int tmpX = ((int) event.getX() + scrollX.getScrollX());
			int tmpY = ((int) event.getY() + scrollY.getScrollY() - DPtoPX(
					context, 50));

			int x = (int) Math.floor((PXtoDP(context, tmpX) / 50));
			int y = (int) Math.floor((PXtoDP(context, tmpY) / 50));

			switch (action) {
			case ACTION_NONE:
				selectedUnit = state.getMap().getUnit(x, y);
				selectedResource = state.getMap().getResource(x, y);
				if (selectedUnit != null) {
					if (!myTurn.getHasMoved()) {
						myTurn.setUnitStartPos(selectedUnit.getPosition());
					}
					unitAction();
				} else if (selectedResource != null) {
					resourceAction();
				}
				break;
			case ACTION_MOVE:
				if (selectedUnit != null) {
					Log.e("pos", x + " " + y);
					if (state.getMap().walkable(x, y) == 0)
						Toast.makeText(context, R.string.cantMove,
								Toast.LENGTH_SHORT).show();
					// else if(!map.canWalkTo(selectedUnit.getPosition().x,
					// selectedUnit.getPosition().y, x, y, selectedUnit.move))
					// Toast.makeText(MapActivity.this, R.string.tooFar,
					// Toast.LENGTH_SHORT).show();
					else { // can be a resource
						if (state.getMap().walkable(x, y) == Player.CAMPS) { // if
																				// it's
																				// a
																				// camp
							state.getPlayers()[player - 1].updateResource(
									Player.CAMPS, 1);
						} else if (state.getMap().walkable(x, y) == Player.MINES) { // if
																					// it's
																					// a
																					// mine
							state.getPlayers()[player - 1].updateResource(
									Player.MINES, 1);
						} else if (state.getMap().walkable(x, y) == Player.CROPS) { // if
																					// it's
																					// a
																					// crop
							state.getPlayers()[player - 1].updateResource(
									Player.CROPS, 1);
						}

						state.getMap().moveUnit(selectedUnit, x, y);
						myTurn.setUnitEndPos(selectedUnit.getPosition());
						myTurn.setHasMoved(true);

					}
					selectedUnit = null;
					action = ACTION_NONE;
				}
				break;
			case ACTION_ATTACK:
				if (selectedUnit != null) {
					Unit target = state.getMap().getUnit(x, y);
					if (target != null) {
						if (target.getOwner() == selectedUnit.getOwner()) {
							Toast.makeText(context, R.string.ownUnit,
									Toast.LENGTH_SHORT).show();
						} else if (Math.abs(selectedUnit.getPosition().getX()
								- target.getPosition().getX())
								+ Math.abs(selectedUnit.getPosition().getY()
										- target.getPosition().getY()) <= selectedUnit
									.getRange()) {
							myTurn.setHasAttacked(true);
							myTurn.setUnitTargetPos(target.getPosition());
							target.takeDemage(selectedUnit.getPower());
							Toast.makeText(
									context,
									"Unit " + target.getId() + " from Player "
											+ target.getOwner() + " took "
											+ selectedUnit.getPower()
											+ " points of damage from Unit "
											+ selectedUnit.getId()
											+ " (Player: "
											+ selectedUnit.getOwner() + ")",
									Toast.LENGTH_LONG).show();
							if (target.getLife() == 0) {
								state.getMap().removeUnit(target);
							}
							state.getMap()
									.updateResources(
											state.getPlayers()[selectedUnit
													.getOwner() - 1]);
							state.updateTurn();
							sendTurn();
						} else
							Toast.makeText(context, R.string.tooFar,
									Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(context, R.string.noTarget,
								Toast.LENGTH_SHORT).show();

					state.getMap().updateResources(
							state.getPlayers()[selectedUnit.getOwner() - 1]);
					// refreshHeader(context.player);
					selectedUnit = null;
					// state.updateTurn();
					// sendState();
					action = ACTION_NONE;
					// sendTurn();

				}
				break;
			}

			drawEverything();
			return true;
		}
	}
}