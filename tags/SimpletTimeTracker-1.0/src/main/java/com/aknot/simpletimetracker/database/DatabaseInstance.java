package com.aknot.simpletimetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aknot.simpletimetracker.R;

/**
 * Database instance using database open helper.
 * 
 * @author Aknot
 * 
 */
public final class DatabaseInstance {

	private static DatabaseInstance currentInstance;

	private DatabaseOpenHelper databaseHelper;
	private SQLiteDatabase sqlLiteDatabase;

	public static void initialize(Context context) {
		if (currentInstance == null) {
			currentInstance = new DatabaseInstance();
			currentInstance.databaseHelper = new DatabaseOpenHelper(context, context.getString(R.string.database_name));
			currentInstance.sqlLiteDatabase = currentInstance.databaseHelper.getWritableDatabase();
		}
	}

	public static void open() {
		checkinstance();
	}

	public static void close() {
		checkinstance();
		currentInstance.sqlLiteDatabase.close();
		currentInstance.databaseHelper.close();
	}

	public static SQLiteDatabase getDatabase() {
		checkinstance();
		return currentInstance.sqlLiteDatabase;
	}

	private static void checkinstance() {
		if (currentInstance == null) {
			Log.e("DatabaseInstance", "Databaseinstance not initialized");
			throw new IllegalStateException("DatabaseInstance not initialized");
		}
		if (!currentInstance.sqlLiteDatabase.isOpen()) {
			currentInstance.sqlLiteDatabase = currentInstance.databaseHelper.getWritableDatabase();
		}
	}

}
