package com.example.embr6monitoringapp.Service;

import com.example.embr6monitoringapp.Models.EstablishmentModel;
import com.example.embr6monitoringapp.Models.ReportInfoModel;
import com.example.embr6monitoringapp.Models.YearCoverdInfoModel;
import java.util.List;

public interface GeneralInfoService {
    boolean saveReportInfo(ReportInfoModel reportInfo);
    boolean saveEstablishmentInfo(EstablishmentModel establishmentInfo);
    boolean saveYearCoveredInfo(YearCoverdInfoModel yearCoveredInfo);
    void syncNow();
    List<EstablishmentModel> getAllEstablishments();
}