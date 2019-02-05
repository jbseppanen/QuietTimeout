package com.jbseppanen.quiettimeout;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.MonthDisplayHelper;

import java.util.ArrayList;

public class MonitorRepository {
    MutableLiveData<ArrayList<Monitor>> liveDataList;

    public MutableLiveData<ArrayList<Monitor>> getMonitors() {
        liveDataList = new MutableLiveData<>();
        MonitorsDbDao.initializeInstance();
        liveDataList.postValue(getNotesFromCache());
        return liveDataList;
    }

    private ArrayList<Monitor> getNotesFromCache() {
        return MonitorsDbDao.readAllMonitors();
    }

    public void addMonitor(Monitor monitor) {
        MonitorsDbDao.createMonitor(monitor);
        liveDataList.postValue(getNotesFromCache());
    }

    public void updateMonitor(Monitor monitor) {
        MonitorsDbDao.updateMonitor(monitor);
        liveDataList.postValue(getNotesFromCache());
    }

    public void deleteMonitor(Monitor monitor) {
        MonitorsDbDao.deleteMonitor(monitor);
        liveDataList.postValue(getNotesFromCache());
    }
}
