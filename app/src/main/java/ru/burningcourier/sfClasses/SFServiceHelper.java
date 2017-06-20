package ru.burningcourier.sfClasses;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import ru.burningcourier.handlers.SFBaseCommand;
import ru.burningcourier.handlers.TimerCommand;
import ru.burningcourier.service.TimerService;

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

    //Запуск таймера
    public int timerCommand(int time) {
        final int requestId = createId();
        Intent i = createIntent(application, new TimerCommand(time), requestId);
        return runRequest(requestId, i);
    }

    public void cancelCommand(int requestId) {
        Intent i = new Intent(application, TimerService.class);
        i.setAction(TimerService.ACTION_CANCEL_COMMAND);
        i.putExtra(TimerService.EXTRA_REQUEST_ID, requestId);
        application.startService(i);
        pendingActivities.remove(requestId);
    }

    public boolean isPending(int requestId){
        return pendingActivities.get(requestId) != null;
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
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_EXECUTE_COMMAND);
        intent.putExtra(TimerService.EXTRA_COMMAND, command);
        intent.putExtra(TimerService.EXTRA_REQUEST_ID, requestId);
        intent.putExtra(TimerService.EXTRA_STATUS_RECEIVER,
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
