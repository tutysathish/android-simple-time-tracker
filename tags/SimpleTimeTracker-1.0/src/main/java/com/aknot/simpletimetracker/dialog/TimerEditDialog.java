package com.aknot.simpletimetracker.dialog;

import android.app.Dialog;
import android.content.Context;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.activity.TimerEditActivity;
import com.aknot.simpletimetracker.model.TimerRecord;

public class TimerEditDialog extends Dialog {

	public TimerEditDialog(Context context) {
		super(context);
	}

	public TimerEditDialog buildEditDialog(final TimerRecord timerRecord, final TimerEditActivity timerActivity) {
		setContentView(R.layout.edit_timer);
		return this;
	}

}
