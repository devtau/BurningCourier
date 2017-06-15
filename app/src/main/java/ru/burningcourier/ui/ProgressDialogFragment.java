package ru.burningcourier.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
    
    public static final String TAG = "progress-auth-dialog";
    private AuthInteractionListener authListener;
    private AuthCancellerListener authCancellerListener;
    private String message;
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context.getClass().equals(AuthenticationActivity.class)) {
            try {
                authListener = (AuthInteractionListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement AuthInteractionListener");
            }
        } else if (context.getClass().equals(OrdersListActivity.class)) {
            try {
                authCancellerListener = (AuthCancellerListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement AuthCancellerListener");
            }
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        return progressDialog;
    }
    
    
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (authListener != null) {
            authListener.cancelCommand();
        } else if (authCancellerListener != null) {
            authCancellerListener.cancelCommand();
        }
    }
    
    public void updateProgressDialogMessage(String message) {
        this.message = message;
        ((ProgressDialog) getDialog()).setMessage(message);
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
    public interface AuthCancellerListener {
        void cancelCommand();
    }
}
