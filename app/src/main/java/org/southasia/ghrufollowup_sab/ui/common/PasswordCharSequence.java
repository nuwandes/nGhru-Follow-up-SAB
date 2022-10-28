package org.southasia.ghrufollowup_sab.ui.common;

public class PasswordCharSequence implements CharSequence {
    private CharSequence mSource;

    public PasswordCharSequence(CharSequence source) {
        mSource = source; // Store char sequence
    }

    public char charAt(int index) {
        return '*'; // This is the important part
    }

    public int length() {
        return mSource.length(); // Return default
    }

    public CharSequence subSequence(int start, int end) {
        return mSource.subSequence(start, end); // Return default
    }
}