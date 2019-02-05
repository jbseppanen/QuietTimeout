package com.jbseppanen.quiettimeout;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.ArrayList;

public class MonitorViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Monitor>> monitorList;
    private MonitorRepository repo;

    public LiveData<ArrayList<Monitor>> getNotesList() {
        if(monitorList == null) {
            loadList();
        }
        return monitorList;
    }

    private void loadList() {
        repo = new MonitorRepository();
        monitorList = repo.getMonitors();
    }

    public void addMonitor(Monitor monitor) {
        if(monitorList != null) {
            repo.addMonitor(monitor);
        }
    }

    public void updateMonitor(Monitor monitor) {
        if(monitorList != null) {
            repo.updateMonitor(monitor);
        }
    }

    public void deleteMonitor(Monitor monitor) {
        if(monitorList != null) {
            repo.deleteMonitor(monitor);
        }
    }

}
