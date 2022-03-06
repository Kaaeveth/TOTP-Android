package de.bofloos.totpandroid.util;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Queue für kleine, kurze Tasks welche in einem Hintergrundthread laufen
 */
public class EventQueue {

    private final ExecutorService eventExecutor;
    private static EventQueue instance;

    private EventQueue() {
        eventExecutor = Executors.newSingleThreadExecutor();
        // Looper für Toasts u.a. - Brauchen keinen vollständigen Looper aufsetzten
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
