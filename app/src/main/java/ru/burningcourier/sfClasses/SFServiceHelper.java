package ru.burningcourier.sfClasses;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import ru.burningcourier.handlers.SFBaseCommand;
import ru.burningcourier.handlers.impl.ApiCommands.AuthorizationCommand;
import ru.burningcourier.handlers.impl.ApiCommands.SendCommand;
import ru.burningcourier.handlers.impl.ApiCommands.TimerCommand;
import ru.burningcourier.handlers.impl.ApiCommands.UpdateCommand;
import ru.burningcourier.service.SFCommandExecutorService;

public class SFServiceHelper {

    private ArrayList<SFServiceCallbackListener> currentListeners = new ArrayList<>();
    private AtomicInteger idCounter = new AtomicInteger();
    private SparseArray<Intent> pendingActivities = new SparseArray<>();
    private Application application;

    
    SFServiceHelper(Application app) {
        this.application = app;
    }

    public void addListener(SFServiceCallbackListener currentListener) {
        currentListeners.add(currentListener);
    }

    public void removeListener(SFServiceCallbackListener currentListener) {
        currentListeners.remove(currentListener);
    }

    //Авторизация пользователя
    public int authCommand(String _url) {
        final int requestId = createId();
        Intent i = createIntent(application, new AuthorizationCommand(_url), requestId);
        return runRequest(requestId, i);
    }

    //Получение списка заказов
    public int ordersCommand(String _url) {
        final int requestId = createId();
        Intent i = createIntent(application, new UpdateCommand(_url), requestId);
        return runRequest(requestId, i);
    }

    //Запуск таймера
    public int timerCommand(int time) {
        final int requestId = createId();
        Intent i = createIntent(application, new TimerCommand(time), requestId);
        return runRequest(requestId, i);
    }

    //Отправка заказа
    public int sendCommand(String _url, int _selectedOrder) {
        final int requestId = createId();
        Intent i = createIntent(application, new SendCommand(_url, _selectedOrder), requestId);
        return runRequest(requestId, i);
    }

    public void cancelCommand(int requestId) {
        Intent i = new Intent(application, SFCommandExecutorService.class);
        i.setAction(SFCommandExecutorService.ACTION_CANCEL_COMMAND);
        i.putExtra(SFCommandExecutorService.EXTRA_REQUEST_ID, requestId);
        application.startService(i);
        pendingActivities.remove(requestId);
    }

    public boolean isPending(int requestId){
        return pendingActivities.get(requestId) != null;
    }

    public boolean check(Intent intent, Class<? extends SFBaseCommand> clazz) {
        Parcelable commandExtra = intent.getParcelableExtra(SFCommandExecutorService.EXTRA_COMMAND);
        return commandExtra != null && commandExtra.getClass().equals(clazz);
    }

    private int createId() {
        return idCounter.getAndIncrement();
    }

    private int runRequest(final int requestId, Intent i) {
        pendingActivities.append(requestId, i);
        application.startService(i);
        return requestId;
    }

    private Intent createIntent(final Context context, SFBaseCommand command, final int requestId) {
        Intent intent = new Intent(context, SFCommandExecutorService.class);
        intent.setAction(SFCommandExecutorService.ACTION_EXECUTE_COMMAND);
        intent.putExtra(SFCommandExecutorService.EXTRA_COMMAND, command);
        intent.putExtra(SFCommandExecutorService.EXTRA_REQUEST_ID, requestId);
        intent.putExtra(SFCommandExecutorService.EXTRA_STATUS_RECEIVER,
                   new ResultReceiver(new Handler()) {
                       @Override
                       protected void onReceiveResult(int resultCode, Bundle resultData) {
                           Intent originalIntent = pendingActivities.get(requestId);
                           if (isPending(requestId)) {
                               if (resultCode != SFBaseCommand.RESPONSE_PROGRESS) {
                                   pendingActivities.remove(requestId);
                               }
                               for (SFServiceCallbackListener currentListener : currentListeners) {
                                   if (currentListener != null) {
                                       currentListener.onServiceCallback(requestId, originalIntent, resultCode, resultData);
                                   }
                               }
                           }
                       }
                   });
        return intent;
    }
}
