package ru.burningcourier.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import ru.burningcourier.R;
import ru.burningcourier.ui.AuthInteractionListener;
import ru.burningcourier.utils.AppUtils;

public class AuthenticationFragment extends Fragment {
    
    private AuthInteractionListener listener;
    private EditText login;
    private EditText password;
    
    
    public AuthenticationFragment() { }
    
    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AuthInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AuthInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        initUI(view);
        return view;
    }
    
    private void initUI(View view) {
        login = (EditText) view.findViewById(R.id.login);
        listener.lookForELogin(login);
        password = (EditText) view.findViewById(R.id.password);
        view.findViewById(R.id.authButton).setOnClickListener(v -> login());;
    }
    
    private void login() {
        if (login.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
        } else {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(password.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        
            if (AppUtils.checkConnection(getContext())) {
                listener.doAuth(login.getText().toString(), password.getText().toString());
            } else {
                Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_city).setVisible(true);
    }
}
