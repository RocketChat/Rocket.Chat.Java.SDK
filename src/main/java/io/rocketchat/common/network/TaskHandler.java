package io.rocketchat.common.network;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sachin on 4/8/17.
 */
public class TaskHandler {
    private Timer timer;
    private TimerTask task;

    public TaskHandler() {
        timer = new Timer();
    }

    public void postDelayed(TimerTask timerTask, long delay) {
        this.task = timerTask;
        timer.schedule(timerTask, delay);
    }

    public void scheduleAtFixedRate(TimerTask timerTask, long delay, long period) {
        timer.scheduleAtFixedRate(timerTask, delay, period);
    }

    public void removeLast() {
        if (task != null) {
            task.cancel();
        }
    }

    public void remove(TimerTask task) {
        task.cancel();
    }

    public void cancel() {
        removeLast();
        timer.cancel();
        timer.purge();
    }
}
