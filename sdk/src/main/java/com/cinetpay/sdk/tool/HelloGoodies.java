package com.cinetpay.sdk.tool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Le Hello Goodies des librarys Istat. pour stecifier l'activation d'un Goodies
 * au Clic sur une View
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
public class HelloGoodies {
	private HelloGoodies() {
	}

	public static void applyGoodies(final View view, final int count,
			final int text) {
		applyGoodies(view, count, view.getContext().getString(text));
	}

	public static void applyGoodies(final View view, final int count,
			final String text) {
		view.setOnClickListener(new OnClickListener() {
			int clicCount = 0;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clicCount++;
				if (clicCount >= count) {
					ToolKit.vibrate(view.getContext(), 90);
					displayGoodies(text, view);
					clicCount = 0;
				}

			}
		});
	}

	static void displayGoodies(String text, View view) {
		Dialogs.displayDialogExclamation(view.getContext(), "Goodies", text,
				"OK");

	}

	public static class Dialogs {
		public static Dialog displayDialogExclamation(Context context,
				String... args) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(args[0])
					.setMessage(args[1])
					.setCancelable(false)
					.setPositiveButton(args[2],
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									try {
										dialog.cancel();
									} catch (Exception e) {
									}
								}
							});
			AlertDialog alert = builder.create();
			// if(!((Activity)(context)).isDestroyed())
			try {
				alert.show();
			} catch (Exception e) {
			}
			return alert;
		}
	}
}
