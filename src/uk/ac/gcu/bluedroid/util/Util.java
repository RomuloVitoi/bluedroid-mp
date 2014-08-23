package uk.ac.gcu.bluedroid.util;

import java.security.MessageDigest;

import uk.ac.gcu.bluedroid.MainActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Util {

	public static String hash(String message) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(message.getBytes());
			return new String(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String resourceType(int type) {
		switch (type) {
		case 2:
			return "camp";
		case 3:
			return "mine";
		case 4:
			return "crop";
		default:
			return "";
		}
	}
	
	public static void simpleAlertDialog(String title, String message, Context context){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Ok", null);

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
