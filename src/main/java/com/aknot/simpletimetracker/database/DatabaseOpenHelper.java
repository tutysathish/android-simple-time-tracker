package com.aknot.simpletimetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper to create sql lite tables.
 * 
 * @author Aknot
 * 
 */
public final class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;

	public static final String CATEGORY_TABLE = "stt_category";
	public static final String TIMER_TABLE = "stt_timer";

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param databaseName
	 */
	DatabaseOpenHelper(final Context context, final String databaseName) {
		super(context, databaseName, null, DATABASE_VERSION);
	}

	/**
	 * On create : create the database.
	 */
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + CATEGORY_TABLE + " (_id integer primary key autoincrement, "
				+ "category_name text, description text)");
		Log.i(this.getClass().toString(), "Created table : " + CATEGORY_TABLE);

		db.execSQL("CREATE TABLE " + TIMER_TABLE + " (_id integer primary key autoincrement, "
				+ "category_id integer, start_time date, end_time date)");
		Log.i(this.getClass().toString(), "Created table : " + TIMER_TABLE);

		upgradeToVersion2(db);
		upgradeToVersion3(db);
	}

	/**
	 * On upgrade : upgrade the database.
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Log.i(this.getClass().toString(), "Upgrading database from version " + oldVersion + " to " + newVersion);
		if (oldVersion < 2) {
			upgradeToVersion2(db);
		}
		if (oldVersion < 3) {
			upgradeToVersion3(db);
		}
	}

	private void upgradeToVersion2(final SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + CATEGORY_TABLE + " ADD COLUMN target_hour integer");
		Log.i(this.getClass().toString(), "Upgraded table (col target_hour added) : " + TIMER_TABLE);
	}

	private void upgradeToVersion3(final SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + CATEGORY_TABLE + " ADD COLUMN targeted_hour real");
		Log.i(this.getClass().toString(), "Upgraded table (col targeted_hour added) : " + TIMER_TABLE);
	}

}
