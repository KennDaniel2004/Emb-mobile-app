package com.example.embr6monitoringapp.Service;

import com.example.embr6monitoringapp.Controller.GeneralinfoController;
import com.example.embr6monitoringapp.Controller.MonitoringProgressController;


public class DashboardServiceImpl implements DashboardService {

    @Override
    public String getAddMonitorDestination() {
        return GeneralinfoController.class.getName();
    }

    @Override
    public String getMonitoringProgressDestination() {
        return MonitoringProgressController.class.getName();
    }
}