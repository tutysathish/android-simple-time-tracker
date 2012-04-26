package com.aknot.simpletimetracker.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalDigitsInputFilter implements InputFilter {

	private final int decimalDigits;

	/**
	 * Constructor.
	 * 
	 * @param decimalDigits
	 *            maximum decimal digits
	 */
	public DecimalDigitsInputFilter(final int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	@Override
	public CharSequence filter(final CharSequence source, final int start, final int end, final Spanned dest, final int dstart, final int dend) {

		int dotPos = -1;
		final int len = dest.length();
		for (int i = 0; i < len; i++) {
			final char c = dest.charAt(i);
			if (c == '.' || c == ',') {
				dotPos = i;
				break;
			}
		}
		if (dotPos > 0) {
			// if the text is entered before the dot
			if (dend <= dotPos) {
				return null;
			}
			if (len - dotPos > decimalDigits) {
				return "";
			}
		}

		return null;
	}

}
