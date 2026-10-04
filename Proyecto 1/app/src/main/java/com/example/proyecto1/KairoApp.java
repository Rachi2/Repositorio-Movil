package com.example.proyecto1;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class KairoApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static WeakReference<Activity> currentActivity = new WeakReference<>(null);

    @Nullable
    public static Activity getCurrentActivity() {
        return currentActivity.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContainer.init(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (currentActivity.get() == activity) {
            currentActivity = new WeakReference<>(null);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}