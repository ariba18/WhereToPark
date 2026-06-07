package com.example.wheretoparkproject.utils;

import android.app.Activity;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.example.wheretoparkproject.R;

/**
 * Shows a non-cancelable progress dialog while fetching or updating Firebase data.
 * Use show(), then dismiss() when the operation completes.
 */
public final class ProgressDialogHelper {

    private ProgressDialogHelper() {}

    /**
     * Show a progress dialog with the given message.
     *
     * @param activity the activity (used for context and UI thread)
     * @param message  e.g. "Loading parking...", "Booking...", "Updating..."
     * @return the AlertDialog so the caller can dismiss it; null if activity is finishing
     */
    public static AlertDialog show(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) return null;
        android.view.LayoutInflater inflater = activity.getLayoutInflater();
        android.view.View view = inflater.inflate(R.layout.dialog_progress, null);
        TextView msg = view.findViewById(R.id.progress_message);
        if (message != null) msg.setText(message);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(false)
                .create();
        dialog.show();
        return dialog;
    }

    /**
     * Dismiss the progress dialog safely (checks isShowing and runs on UI thread if needed).
     */
    public static void dismiss(Activity activity, AlertDialog dialog) {
        if (dialog == null || !dialog.isShowing()) return;
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(dialog::dismiss);
        }
    }
}
