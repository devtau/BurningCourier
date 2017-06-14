package ru.burningcourier.handlers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import ru.burningcourier.sfClasses.SFApplication;

@SuppressLint("ParcelCreator")
public abstract class SFBaseCommand implements Parcelable {

    public static final int RESPONSE_SUCCESS  = 0;
    public static final int RESPONSE_FAILURE  = 1;
    public static final int RESPONSE_PROGRESS = 2;
    public static String EXTRA_PROGRESS = SFApplication.PACKAGE.concat(".EXTRA_PROGRESS");
    protected volatile boolean cancelled = false;
    private ResultReceiver sfCallback;

    public final void execute(Intent intent, Context context, ResultReceiver callback) {
        this.sfCallback = callback;
        doExecute(intent, context, callback);
    }

    protected abstract void doExecute(Intent intent, Context context, ResultReceiver callback);

    protected void notifySuccess(Bundle data) {
        sendUpdate(RESPONSE_SUCCESS, data);
    }

    protected void notifyFailure(Bundle data) {
        sendUpdate(RESPONSE_FAILURE, data);
    }

    protected void sendProgress(int progress) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PROGRESS, progress);
        sendUpdate(RESPONSE_PROGRESS, bundle);
    }

    private void sendUpdate(int resultCode, Bundle data) {
        if (sfCallback != null) {
            sfCallback.send(resultCode, data);
        }
    }

    public synchronized void cancel() {
        cancelled = true;
    }
}
