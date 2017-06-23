package ru.burningcourier.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.burningcourier.R;
import ru.burningcourier.utils.AppUtils;

public class ConfirmationDialog extends DialogFragment {
    
    public static final String TAG = "ru.burningcourier.ui.ConfirmationDialog";
    public static final String ORDER_ID = "orderId";
    public static final String CONFIRMATION_TYPE = "confirmationType";
    private ConfirmationDialogListener listener;
    private String orderId;
    private ConfirmationType confirmationType;
    
    
    public static void showDialog(String orderId, ConfirmationType type, FragmentManager fragmentManager) {
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(orderId, type);
        dialog.show(fragmentManager, TAG);
    }
    
    private static ConfirmationDialog newInstance(String orderId, ConfirmationType type) {
        ConfirmationDialog fragment = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ORDER_ID, orderId);
        args.putSerializable(CONFIRMATION_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmationDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("ConfirmationDialogListener not implemented by caller");
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString(ORDER_ID);
            confirmationType = (ConfirmationType) getArguments().getSerializable(CONFIRMATION_TYPE);
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmation_dialog, container, false);
        ((ImageView) view.findViewById(R.id.titleIcon)).setImageResource(confirmationType.iconResource);
    
        TextView orderNumber = ((TextView) view.findViewById(R.id.orderNumber));
        TextView title = ((TextView) view.findViewById(R.id.title));
        TextView subTitle = ((TextView) view.findViewById(R.id.subTitle));
        TextView firstButton = ((TextView) view.findViewById(R.id.firstButton));
        TextView secondButton = ((TextView) view.findViewById(R.id.secondButton));
        EditText checkSum = ((EditText) view.findViewById(R.id.checkSum));
        
        switch (confirmationType) {
            case STATUS:
                title.setText(R.string.change_order_status);
                orderNumber.setText(orderId);
                subTitle.setVisibility(View.GONE);
                firstButton.setText(R.string.cancel);
                firstButton.setOnClickListener(v -> dismiss());
                secondButton.setText(R.string.confirm);
                secondButton.setOnClickListener(v -> {
                    if (!AppUtils.checkConnection(getContext())) return;
                    firstButton.setOnClickListener(null);
                    listener.onConfirmed();
                });
                setCancelable(false);
                break;
            case CALL:
                title.setText(R.string.who_you_gonna_call);
                orderNumber.setVisibility(View.GONE);
                subTitle.setVisibility(View.GONE);
                firstButton.setText(R.string.call_center);
                firstButton.setOnClickListener(v -> {
                    listener.onCallCenterCallClicked();
                    dismiss();
                });
                secondButton.setText(R.string.client);
                secondButton.setOnClickListener(v -> {
                    listener.onClientCallClicked();
                    dismiss();
                });
                break;
            case PHOTO:
                title.setVisibility(View.GONE);
                orderNumber.setText(orderId);
                firstButton.setText(R.string.cancel);
                firstButton.setOnClickListener(v -> dismiss());
                secondButton.setText(R.string.camera);
                secondButton.setOnClickListener(v -> {
                    int checkSumValue = 0;
                    try {
                        checkSumValue = Integer.parseInt(checkSum.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(checkSum.getText()) || checkSumValue == 0) {
                        Toast.makeText(getContext(), R.string.check_sum_incorrect, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listener.takePhoto(checkSumValue);
                    dismiss();
                });
                view.findViewById(R.id.checkSumContainer).setVisibility(View.VISIBLE);
                break;
        }
        return view;
    }
    
    
    
    public interface ConfirmationDialogListener {
        void onConfirmed();
        void onCallCenterCallClicked();
        void onClientCallClicked();
        void takePhoto(int checkSum);
    }
    
    public enum ConfirmationType {
        CALL(R.drawable.big_call), PHOTO(R.drawable.camera), STATUS(R.drawable.approve);
        
        public int iconResource;
    
        ConfirmationType(int iconResource) {
            this.iconResource = iconResource;
        }
    }
}
