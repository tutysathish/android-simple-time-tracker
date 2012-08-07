package com.aknot.simpletimetracker.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * This class give support to notify a fragment of a change.
 * 
 * @author aknot
 * 
 */
public class Notifier extends Observable {

	private static Notifier instance = null;

	protected Notifier() {
		super();
	}

	public static Notifier getInstance() {
		if (instance == null) {
			instance = new Notifier();
		}
		return instance;
	}

	public static void addObservers(final Observer observer) {
		getInstance().addObserver(observer);
	}

	public static void notifyFragments(final String msg) {
		getInstance().setChanged();
		getInstance().notifyObservers(msg);
	}
}