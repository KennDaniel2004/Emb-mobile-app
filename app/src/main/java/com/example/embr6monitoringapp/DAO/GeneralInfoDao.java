package com.example.embr6monitoringapp.DAO;

import com.example.embr6monitoringapp.Models.EstablishmentModel;
import com.example.embr6monitoringapp.Models.ReportInfoModel;
import com.example.embr6monitoringapp.Models.YearCoverdInfoModel;

import java.util.List;

public interface GeneralInfoDao {

    boolean insertReportInfo(ReportInfoModel reportInfo);

    boolean insertEstablishmentInfo(EstablishmentModel establishmentInfo);

    boolean insertYearCoveredInfo(YearCoverdInfoModel yearCoveredInfo);

    List<EstablishmentModel> getAllEstablishments();

}

