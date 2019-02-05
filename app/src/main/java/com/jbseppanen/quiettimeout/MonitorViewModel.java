package com.jbseppanen.quiettimeout;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.ArrayList;

public class MonitorViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Monitor>> monitorList;
    private MonitorRepository repo;

    public LiveData<ArrayList<Monitor>> getNotesList(Context context) {
        if(monitorList == null) {
            loadList(context);
        }
        return monitorList;
    }

    private void loadList(Context context) {
        repo = new MonitorRepository();
        monitorList = repo.getMonitors(context);
    }

    public void addMonitor(Monitor monitor, Context context) {
        if(monitorList != null) {
            repo.addMonitor(monitor);
        }
    }

    public void deleteMonitor(Monitor monitor) {
        if(monitorList != null) {
            repo.deleteMonitor(monitor);
        }
    }

}
