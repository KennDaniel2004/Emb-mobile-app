package com.example.embr6monitoringapp.Service;

import android.content.Context;

import com.example.embr6monitoringapp.DAO.GeneralInfoDao;
import com.example.embr6monitoringapp.DAO.GeneralInfoDaoImpl;
import com.example.embr6monitoringapp.Models.EstablishmentModel;
import com.example.embr6monitoringapp.Models.ReportInfoModel;
import com.example.embr6monitoringapp.Models.YearCoverdInfoModel;
import com.example.embr6monitoringapp.Utils.SyncManager;

import java.util.List;


public class GeneralInfoServiceImpl implements GeneralInfoService {

    private final GeneralInfoDao generalInfoDao;
    private final SyncManager    syncManager;

    public GeneralInfoServiceImpl(Context context) {
        generalInfoDao = new GeneralInfoDaoImpl(context);
        syncManager    = new SyncManager(context);
    }


    @Override
    public boolean saveReportInfo(ReportInfoModel reportInfo) {
        return generalInfoDao.insertReportInfo(reportInfo);
    }

    @Override
    public boolean saveEstablishmentInfo(EstablishmentModel establishmentInfo) {
        return generalInfoDao.insertEstablishmentInfo(establishmentInfo);
    }

    @Override
    public boolean saveYearCoveredInfo(YearCoverdInfoModel yearCoveredInfo) {
        return generalInfoDao.insertYearCoveredInfo(yearCoveredInfo);
    }


    public void syncNow() {
        syncManager.syncIfOnline();
    }


    @Override
    public List<EstablishmentModel> getAllEstablishments() {
        return generalInfoDao.getAllEstablishments();
    }
}