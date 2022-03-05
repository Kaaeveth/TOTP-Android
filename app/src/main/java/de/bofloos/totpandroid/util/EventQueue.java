package de.bofloos.totpandroid.util;

import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventQueue {

    private final Executor eventExecutor;
    private static EventQueue instance;

    private EventQueue() {
        // Nur einen Thread, welcher Tasks in eine Queue legt
        eventExecutor = Executors.newSingleThreadExecutor();

        // Looper für Toasts - Brauchen keinen vollständigen Looper aufsetzten
        eventExecutor.execute(Looper::prepare);
    }

    public void post(Runnable task) {
        eventExecutor.execute(task);
    }

    public static EventQueue getInstance() {
        if(instance == null)
            instance = new EventQueue();
        return instance;
    }
}
