package ru.burningcourier.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ProgressDialogFragment extends DialogFragment {
    
    public static final String TAG = "progress-auth-dialog";
    private AuthCancellerListener listener;
    private String message;
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AuthCancellerListener) context;
        } catch (ClassCastException e) {
            Log.d(TAG, "AuthCancellerListener not implemented by caller");
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        return progressDialog;
    }
    
    
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) {
            listener.cancelAuthorization();
        }
    }
    
    public void updateProgressDialogMessage(String message) {
        if (getDialog() == null) return;
        this.message = message;
        ((ProgressDialog) getDialog()).setMessage(message);
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
    public interface AuthCancellerListener {
        void cancelAuthorization();
    }
}
