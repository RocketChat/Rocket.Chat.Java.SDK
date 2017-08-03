package io.rocketchat.common.network;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sachin on 4/8/17.
 */
public class TaskHandler {
    private Timer timer;
    ArrayList<TimerTask> tasks;

    public TaskHandler() {
        tasks=new ArrayList<TimerTask>();
        timer=new Timer();
    }


    public void postDelayed(TimerTask timerTask, long delay){
        tasks.add(timerTask);
        timer.schedule(timerTask,delay);
    }


    public void scheduleAtFixedRate(TimerTask timerTask, long delay, long period){
        tasks.add(timerTask);
        timer.scheduleAtFixedRate(timerTask,delay,period);
    }

    public void removeLast(){
        if (tasks.size()!=0) {
            tasks.remove(tasks.size() - 1).cancel();
        }
    }

    public void remove(TimerTask task){
        task.cancel();
        tasks.remove(task);
    }

    public void removeAll(){
        for (TimerTask task :tasks){
            task.cancel();
        }
        tasks.clear();
        timer.purge();
    }

    public void cancel(){
        timer.cancel();
        timer.purge();

        System.out.println("size is "+tasks.size());
    }
}
