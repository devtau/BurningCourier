package ru.burningcourier.ui;

import android.widget.TextView;

public interface AuthInteractionListener {
    
    void cancelCommand();
    void lookForELogin(TextView login);
    void doAuth(String login, String password);
    void saveCity(int cityId);
}
