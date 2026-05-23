package com.example.embr6monitoringapp.Models;

public class MonitoringRecord {

    private int    id;
    private String employeeId;

    // ── Title block ──────────────────────────────────────────────
    private String embId, reportControl, typeMonitoring, dateOfInspection;
    private String laws;

    // ── Section 1 – General Information ─────────────────────────
    private String nameOfEstablishment, proponent, mailingAddress;
    private String geoN, geoE, projectLocation, natureOfBusiness;
    private String yearEstablish, psicCode;
    private String opHoursDay, opDayWeek, opDayYear;
    private String male, female, numberOfEmployee;
    private String productLines, productionRate, actualProductionRate;
    private String nameOfManagingHead, nameOfPCO;
    private String pcoAccreditationNo, dateOfEffectivity, phoneFaxNo, emailAddress;
    private String yearCovered, volCuM, total;

    // ── Section 2 – Purpose of Inspection ───────────────────────
    private int verifyAccuracy;
    private int pmpinNew,   pmpinRenewal;
    private int hwgidNew,   hwgidRenewal;
    private int hwtrNew,    hwtrRenewal;
    private int hwtsdNew,   hwtsdRenewal;
    private int poapciNew,  poapciRenewal;
    private int dpNew,      dpRenewal;
    private String otherPermit;
    private int determineCompliance;
    private int investigate;
    private int survey;
    private String otherSpecify;
    private String contactName;
    private String position;

    // ── Section 3.1 – DENR Permits / Licenses / Clearances ──────
    // PD 1586 – up to 3 ECCs
    private String pd1586Ecc1, pd1586Ecc1DateFrom, pd1586Ecc1DateTo;
    private String pd1586Ecc2, pd1586Ecc2DateFrom, pd1586Ecc2DateTo;
    private String pd1586Ecc3, pd1586Ecc3DateFrom, pd1586Ecc3DateTo;
    // RA 6969
    private String ra6969DenrRegistry, ra6969DenrDateFrom, ra6969DenrDateTo;
    // RA 8749
    private String ra8749PoNo, ra8749PoDateFrom, ra8749PoDateTo;
    // RA 9275
    private String ra9275DischargePermit, ra9275DischargeDateFrom, ra9275DischargeDateTo;
    // RA 9003
    private String ra9003MoaAgreement, ra9003MoaDateFrom, ra9003MoaDateTo;

    // ── Section 3.3 – Summary of Findings ───────────────────────
    private String s331Status, s331Findings, s331Image1, s331Image2;
    private String s332Status, s332Findings, s332Image1, s332Image2;
    private String s333Status, s333Findings, s333Image1, s333Image2;
    private String s334Status, s334Findings, s334Image1, s334Image2;
    private String s335Status, s335Findings, s335Image1, s335Image2;
    private String s336Status, s336Findings;
    private String s337Status, s337Findings;

    // ── Section 4 – Recommendations ─────────────────────────────
    private int    recConfirmatory;
    private int    recRegularMonitoring;
    private String recRegularMonitoringDesc;
    private int    recIssuanceTempRenewal;
    private int    recAccreditationPco;
    private int    recSubmissionSmrCmr;
    private int    recIssuanceNomTc;
    private int    recIssuanceNov;
    private int    recSuspensionEcc;
    private int    recEndorsementPab;
    private String recOther;

    // ── Signatures ───────────────────────────────────────────────
    private String submittedBy;
    private String dateSubmitted;
    private String dateTravelConcluded;
    private String recommendingApproval1, recommendingApproval1Position;
    private String recommendingApproval2, recommendingApproval2Position;
    private String approvedBy, approvedByPosition;

    // ── Flags ────────────────────────────────────────────────────
    private int isComplete;
    private int isSynced;
    private int isArchived;   // 0 = active, 1 = archived

    // ════════════════════════════════════════════════════════════
    //  GETTERS AND SETTERS
    // ════════════════════════════════════════════════════════════

    public int    getId()            { return id; }
    public void   setId(int v)       { this.id = v; }

    public String getEmployeeId()    { return employeeId; }
    public void   setEmployeeId(String v) { this.employeeId = v; }

    // ── Title block ──────────────────────────────────────────────

    public String getEmbId()              { return embId; }
    public void   setEmbId(String v)      { this.embId = v; }

    public String getReportControl()      { return reportControl; }
    public void   setReportControl(String v) { this.reportControl = v; }

    public String getTypeMonitoring()     { return typeMonitoring; }
    public void   setTypeMonitoring(String v) { this.typeMonitoring = v; }

    public String getDateOfInspection()   { return dateOfInspection; }
    public void   setDateOfInspection(String v) { this.dateOfInspection = v; }

    public String getLaws()               { return laws; }
    public void   setLaws(String v)       { this.laws = v; }

    // ── Section 1 ────────────────────────────────────────────────

    public String getNameOfEstablishment()       { return nameOfEstablishment; }
    public void   setNameOfEstablishment(String v) { this.nameOfEstablishment = v; }

    public String getProponent()                 { return proponent; }
    public void   setProponent(String v)         { this.proponent = v; }

    public String getMailingAddress()            { return mailingAddress; }
    public void   setMailingAddress(String v)    { this.mailingAddress = v; }

    public String getGeoN()                      { return geoN; }
    public void   setGeoN(String v)              { this.geoN = v; }

    public String getGeoE()                      { return geoE; }
    public void   setGeoE(String v)              { this.geoE = v; }

    public String getProjectLocation()           { return projectLocation; }
    public void   setProjectLocation(String v)   { this.projectLocation = v; }

    public String getNatureOfBusiness()          { return natureOfBusiness; }
    public void   setNatureOfBusiness(String v)  { this.natureOfBusiness = v; }

    public String getYearEstablish()             { return yearEstablish; }
    public void   setYearEstablish(String v)     { this.yearEstablish = v; }

    public String getPsicCode()                  { return psicCode; }
    public void   setPsicCode(String v)          { this.psicCode = v; }

    public String getOpHoursDay()                { return opHoursDay; }
    public void   setOpHoursDay(String v)        { this.opHoursDay = v; }

    public String getOpDayWeek()                 { return opDayWeek; }
    public void   setOpDayWeek(String v)         { this.opDayWeek = v; }

    public String getOpDayYear()                 { return opDayYear; }
    public void   setOpDayYear(String v)         { this.opDayYear = v; }

    public String getMale()                      { return male; }
    public void   setMale(String v)              { this.male = v; }

    public String getFemale()                    { return female; }
    public void   setFemale(String v)            { this.female = v; }

    public String getNumberOfEmployee()          { return numberOfEmployee; }
    public void   setNumberOfEmployee(String v)  { this.numberOfEmployee = v; }

    public String getProductLines()              { return productLines; }
    public void   setProductLines(String v)      { this.productLines = v; }

    public String getProductionRate()            { return productionRate; }
    public void   setProductionRate(String v)    { this.productionRate = v; }

    public String getActualProductionRate()      { return actualProductionRate; }
    public void   setActualProductionRate(String v) { this.actualProductionRate = v; }

    public String getNameOfManagingHead()        { return nameOfManagingHead; }
    public void   setNameOfManagingHead(String v) { this.nameOfManagingHead = v; }

    public String getNameOfPCO()                 { return nameOfPCO; }
    public void   setNameOfPCO(String v)         { this.nameOfPCO = v; }

    public String getPcoAccreditationNo()        { return pcoAccreditationNo; }
    public void   setPcoAccreditationNo(String v) { this.pcoAccreditationNo = v; }

    public String getDateOfEffectivity()         { return dateOfEffectivity; }
    public void   setDateOfEffectivity(String v) { this.dateOfEffectivity = v; }

    public String getPhoneFaxNo()                { return phoneFaxNo; }
    public void   setPhoneFaxNo(String v)        { this.phoneFaxNo = v; }

    public String getEmailAddress()              { return emailAddress; }
    public void   setEmailAddress(String v)      { this.emailAddress = v; }

    public String getYearCovered()               { return yearCovered; }
    public void   setYearCovered(String v)       { this.yearCovered = v; }

    public String getVolCuM()                    { return volCuM; }
    public void   setVolCuM(String v)            { this.volCuM = v; }

    public String getTotal()                     { return total; }
    public void   setTotal(String v)             { this.total = v; }

    // ── Section 2 ────────────────────────────────────────────────

    public int    getVerifyAccuracy()            { return verifyAccuracy; }
    public void   setVerifyAccuracy(int v)       { this.verifyAccuracy = v; }

    public int    getPmpinNew()                  { return pmpinNew; }
    public void   setPmpinNew(int v)             { this.pmpinNew = v; }

    public int    getPmpinRenewal()              { return pmpinRenewal; }
    public void   setPmpinRenewal(int v)         { this.pmpinRenewal = v; }

    public int    getHwgidNew()                  { return hwgidNew; }
    public void   setHwgidNew(int v)             { this.hwgidNew = v; }

    public int    getHwgidRenewal()              { return hwgidRenewal; }
    public void   setHwgidRenewal(int v)         { this.hwgidRenewal = v; }

    public int    getHwtrNew()                   { return hwtrNew; }
    public void   setHwtrNew(int v)              { this.hwtrNew = v; }

    public int    getHwtrRenewal()               { return hwtrRenewal; }
    public void   setHwtrRenewal(int v)          { this.hwtrRenewal = v; }

    public int    getHwtsdNew()                  { return hwtsdNew; }
    public void   setHwtsdNew(int v)             { this.hwtsdNew = v; }

    public int    getHwtsdRenewal()              { return hwtsdRenewal; }
    public void   setHwtsdRenewal(int v)         { this.hwtsdRenewal = v; }

    public int    getPoapciNew()                 { return poapciNew; }
    public void   setPoapciNew(int v)            { this.poapciNew = v; }

    public int    getPoapciRenewal()             { return poapciRenewal; }
    public void   setPoapciRenewal(int v)        { this.poapciRenewal = v; }

    public int    getDpNew()                     { return dpNew; }
    public void   setDpNew(int v)                { this.dpNew = v; }

    public int    getDpRenewal()                 { return dpRenewal; }
    public void   setDpRenewal(int v)            { this.dpRenewal = v; }

    public String getOtherPermit()               { return otherPermit; }
    public void   setOtherPermit(String v)       { this.otherPermit = v; }

    public int    getDetermineCompliance()        { return determineCompliance; }
    public void   setDetermineCompliance(int v)  { this.determineCompliance = v; }

    public int    getInvestigate()               { return investigate; }
    public void   setInvestigate(int v)          { this.investigate = v; }

    public int    getSurvey()                    { return survey; }
    public void   setSurvey(int v)               { this.survey = v; }

    public String getOtherSpecify()              { return otherSpecify; }
    public void   setOtherSpecify(String v)      { this.otherSpecify = v; }

    public String getContactName()               { return contactName; }
    public void   setContactName(String v)       { this.contactName = v; }

    public String getPosition()                  { return position; }
    public void   setPosition(String v)          { this.position = v; }

    // ── Section 3.1 ──────────────────────────────────────────────

    // PD 1586
    public String getPd1586Ecc1()                { return pd1586Ecc1; }
    public void   setPd1586Ecc1(String v)        { this.pd1586Ecc1 = v; }

    public String getPd1586Ecc1DateFrom()        { return pd1586Ecc1DateFrom; }
    public void   setPd1586Ecc1DateFrom(String v){ this.pd1586Ecc1DateFrom = v; }

    public String getPd1586Ecc1DateTo()          { return pd1586Ecc1DateTo; }
    public void   setPd1586Ecc1DateTo(String v)  { this.pd1586Ecc1DateTo = v; }

    public String getPd1586Ecc2()                { return pd1586Ecc2; }
    public void   setPd1586Ecc2(String v)        { this.pd1586Ecc2 = v; }

    public String getPd1586Ecc2DateFrom()        { return pd1586Ecc2DateFrom; }
    public void   setPd1586Ecc2DateFrom(String v){ this.pd1586Ecc2DateFrom = v; }

    public String getPd1586Ecc2DateTo()          { return pd1586Ecc2DateTo; }
    public void   setPd1586Ecc2DateTo(String v)  { this.pd1586Ecc2DateTo = v; }

    public String getPd1586Ecc3()                { return pd1586Ecc3; }
    public void   setPd1586Ecc3(String v)        { this.pd1586Ecc3 = v; }

    public String getPd1586Ecc3DateFrom()        { return pd1586Ecc3DateFrom; }
    public void   setPd1586Ecc3DateFrom(String v){ this.pd1586Ecc3DateFrom = v; }

    public String getPd1586Ecc3DateTo()          { return pd1586Ecc3DateTo; }
    public void   setPd1586Ecc3DateTo(String v)  { this.pd1586Ecc3DateTo = v; }

    // RA 6969
    public String getRa6969DenrRegistry()        { return ra6969DenrRegistry; }
    public void   setRa6969DenrRegistry(String v){ this.ra6969DenrRegistry = v; }

    public String getRa6969DenrDateFrom()        { return ra6969DenrDateFrom; }
    public void   setRa6969DenrDateFrom(String v){ this.ra6969DenrDateFrom = v; }

    public String getRa6969DenrDateTo()          { return ra6969DenrDateTo; }
    public void   setRa6969DenrDateTo(String v)  { this.ra6969DenrDateTo = v; }

    // RA 8749
    public String getRa8749PoNo()                { return ra8749PoNo; }
    public void   setRa8749PoNo(String v)        { this.ra8749PoNo = v; }

    public String getRa8749PoDateFrom()          { return ra8749PoDateFrom; }
    public void   setRa8749PoDateFrom(String v)  { this.ra8749PoDateFrom = v; }

    public String getRa8749PoDateTo()            { return ra8749PoDateTo; }
    public void   setRa8749PoDateTo(String v)    { this.ra8749PoDateTo = v; }

    // RA 9275
    public String getRa9275DischargePermit()     { return ra9275DischargePermit; }
    public void   setRa9275DischargePermit(String v) { this.ra9275DischargePermit = v; }

    public String getRa9275DischargeDateFrom()   { return ra9275DischargeDateFrom; }
    public void   setRa9275DischargeDateFrom(String v) { this.ra9275DischargeDateFrom = v; }

    public String getRa9275DischargeDateTo()     { return ra9275DischargeDateTo; }
    public void   setRa9275DischargeDateTo(String v) { this.ra9275DischargeDateTo = v; }

    // RA 9003
    public String getRa9003MoaAgreement()        { return ra9003MoaAgreement; }
    public void   setRa9003MoaAgreement(String v){ this.ra9003MoaAgreement = v; }

    public String getRa9003MoaDateFrom()         { return ra9003MoaDateFrom; }
    public void   setRa9003MoaDateFrom(String v) { this.ra9003MoaDateFrom = v; }

    public String getRa9003MoaDateTo()           { return ra9003MoaDateTo; }
    public void   setRa9003MoaDateTo(String v)   { this.ra9003MoaDateTo = v; }

    // ── Section 3.3 ──────────────────────────────────────────────

    public String getS331Status()   { return s331Status; }   public void setS331Status(String v)   { this.s331Status = v; }
    public String getS331Findings() { return s331Findings; } public void setS331Findings(String v) { this.s331Findings = v; }
    public String getS331Image1()   { return s331Image1; }   public void setS331Image1(String v)   { this.s331Image1 = v; }
    public String getS331Image2()   { return s331Image2; }   public void setS331Image2(String v)   { this.s331Image2 = v; }

    public String getS332Status()   { return s332Status; }   public void setS332Status(String v)   { this.s332Status = v; }
    public String getS332Findings() { return s332Findings; } public void setS332Findings(String v) { this.s332Findings = v; }
    public String getS332Image1()   { return s332Image1; }   public void setS332Image1(String v)   { this.s332Image1 = v; }
    public String getS332Image2()   { return s332Image2; }   public void setS332Image2(String v)   { this.s332Image2 = v; }

    public String getS333Status()   { return s333Status; }   public void setS333Status(String v)   { this.s333Status = v; }
    public String getS333Findings() { return s333Findings; } public void setS333Findings(String v) { this.s333Findings = v; }
    public String getS333Image1()   { return s333Image1; }   public void setS333Image1(String v)   { this.s333Image1 = v; }
    public String getS333Image2()   { return s333Image2; }   public void setS333Image2(String v)   { this.s333Image2 = v; }

    public String getS334Status()   { return s334Status; }   public void setS334Status(String v)   { this.s334Status = v; }
    public String getS334Findings() { return s334Findings; } public void setS334Findings(String v) { this.s334Findings = v; }
    public String getS334Image1()   { return s334Image1; }   public void setS334Image1(String v)   { this.s334Image1 = v; }
    public String getS334Image2()   { return s334Image2; }   public void setS334Image2(String v)   { this.s334Image2 = v; }

    public String getS335Status()   { return s335Status; }   public void setS335Status(String v)   { this.s335Status = v; }
    public String getS335Findings() { return s335Findings; } public void setS335Findings(String v) { this.s335Findings = v; }
    public String getS335Image1()   { return s335Image1; }   public void setS335Image1(String v)   { this.s335Image1 = v; }
    public String getS335Image2()   { return s335Image2; }   public void setS335Image2(String v)   { this.s335Image2 = v; }

    public String getS336Status()   { return s336Status; }   public void setS336Status(String v)   { this.s336Status = v; }
    public String getS336Findings() { return s336Findings; } public void setS336Findings(String v) { this.s336Findings = v; }

    public String getS337Status()   { return s337Status; }   public void setS337Status(String v)   { this.s337Status = v; }
    public String getS337Findings() { return s337Findings; } public void setS337Findings(String v) { this.s337Findings = v; }

    // ── Section 4 ────────────────────────────────────────────────

    public int    getRecConfirmatory()              { return recConfirmatory; }
    public void   setRecConfirmatory(int v)         { this.recConfirmatory = v; }

    public int    getRecRegularMonitoring()         { return recRegularMonitoring; }
    public void   setRecRegularMonitoring(int v)    { this.recRegularMonitoring = v; }

    public String getRecRegularMonitoringDesc()     { return recRegularMonitoringDesc; }
    public void   setRecRegularMonitoringDesc(String v) { this.recRegularMonitoringDesc = v; }

    public int    getRecIssuanceTempRenewal()       { return recIssuanceTempRenewal; }
    public void   setRecIssuanceTempRenewal(int v)  { this.recIssuanceTempRenewal = v; }

    public int    getRecAccreditationPco()          { return recAccreditationPco; }
    public void   setRecAccreditationPco(int v)     { this.recAccreditationPco = v; }

    public int    getRecSubmissionSmrCmr()          { return recSubmissionSmrCmr; }
    public void   setRecSubmissionSmrCmr(int v)     { this.recSubmissionSmrCmr = v; }

    public int    getRecIssuanceNomTc()             { return recIssuanceNomTc; }
    public void   setRecIssuanceNomTc(int v)        { this.recIssuanceNomTc = v; }

    public int    getRecIssuanceNov()               { return recIssuanceNov; }
    public void   setRecIssuanceNov(int v)          { this.recIssuanceNov = v; }

    public int    getRecSuspensionEcc()             { return recSuspensionEcc; }
    public void   setRecSuspensionEcc(int v)        { this.recSuspensionEcc = v; }

    public int    getRecEndorsementPab()            { return recEndorsementPab; }
    public void   setRecEndorsementPab(int v)       { this.recEndorsementPab = v; }

    public String getRecOther()                     { return recOther; }
    public void   setRecOther(String v)             { this.recOther = v; }

    // ── Signatures ───────────────────────────────────────────────

    public String getSubmittedBy()                      { return submittedBy; }
    public void   setSubmittedBy(String v)              { this.submittedBy = v; }

    public String getDateSubmitted()                    { return dateSubmitted; }
    public void   setDateSubmitted(String v)            { this.dateSubmitted = v; }

    public String getDateTravelConcluded()              { return dateTravelConcluded; }
    public void   setDateTravelConcluded(String v)      { this.dateTravelConcluded = v; }

    public String getRecommendingApproval1()            { return recommendingApproval1; }
    public void   setRecommendingApproval1(String v)    { this.recommendingApproval1 = v; }

    public String getRecommendingApproval1Position()    { return recommendingApproval1Position; }
    public void   setRecommendingApproval1Position(String v) { this.recommendingApproval1Position = v; }

    public String getRecommendingApproval2()            { return recommendingApproval2; }
    public void   setRecommendingApproval2(String v)    { this.recommendingApproval2 = v; }

    public String getRecommendingApproval2Position()    { return recommendingApproval2Position; }
    public void   setRecommendingApproval2Position(String v) { this.recommendingApproval2Position = v; }

    public String getApprovedBy()                       { return approvedBy; }
    public void   setApprovedBy(String v)               { this.approvedBy = v; }

    public String getApprovedByPosition()               { return approvedByPosition; }
    public void   setApprovedByPosition(String v)       { this.approvedByPosition = v; }

    // ── Flags ────────────────────────────────────────────────────

    public int  getIsComplete()   { return isComplete; }
    public void setIsComplete(int v) { this.isComplete = v; }

    public int  getIsSynced()     { return isSynced; }
    public void setIsSynced(int v)   { this.isSynced = v; }

    public int  getIsArchived()   { return isArchived; }
    public void setIsArchived(int v) { this.isArchived = v; }

    // ── Aliases kept for backward compatibility ───────────────────
    // These delegate to the canonical getters above so existing
    // callers (other activities, adapters, the database helper) all
    // continue to compile without changes.

    /** @deprecated use {@link #getRecommendingApproval1()} */
    public String getRecommending1()                     { return recommendingApproval1; }
    public void   setRecommending1(String v)             { this.recommendingApproval1 = v; }

    /** @deprecated use {@link #getRecommendingApproval1Position()} */
    public String getRecommending1Position()             { return recommendingApproval1Position; }
    public void   setRecommending1Position(String v)     { this.recommendingApproval1Position = v; }

    /** @deprecated use {@link #getRecommendingApproval2()} */
    public String getRecommending2()                     { return recommendingApproval2; }
    public void   setRecommending2(String v)             { this.recommendingApproval2 = v; }

    /** @deprecated use {@link #getRecommendingApproval2Position()} */
    public String getRecommending2Position()             { return recommendingApproval2Position; }
    public void   setRecommending2Position(String v)     { this.recommendingApproval2Position = v; }

    /** @deprecated use {@link #getRecConfirmatory()} */
    public int  getRecConfirmatorysampling()             { return recConfirmatory; }
    public void setRecConfirmatorysampling(int v)        { this.recConfirmatory = v; }

    /** @deprecated use {@link #getRecIssuanceTempRenewal()} */
    public int  getRecIssuanceTempRenewalPoaDp()         { return recIssuanceTempRenewal; }
    public void setRecIssuanceTempRenewalPoaDp(int v)    { this.recIssuanceTempRenewal = v; }

    /** @deprecated use {@link #getRecSuspensionEcc()} */
    public int  getRecSuspensionEcc5DayCdo()             { return recSuspensionEcc; }
    public void setRecSuspensionEcc5DayCdo(int v)        { this.recSuspensionEcc = v; }

    // ── Convenience read-only helpers ────────────────────────────

    /**
     * Returns the first non-empty findings string across all sections,
     * useful for summary card display.
     */
    public String getFindings() {
        if (s331Findings != null && !s331Findings.isEmpty()) return s331Findings;
        if (s332Findings != null && !s332Findings.isEmpty()) return s332Findings;
        if (s333Findings != null && !s333Findings.isEmpty()) return s333Findings;
        if (s334Findings != null && !s334Findings.isEmpty()) return s334Findings;
        if (s335Findings != null && !s335Findings.isEmpty()) return s335Findings;
        if (s336Findings != null && !s336Findings.isEmpty()) return s336Findings;
        if (s337Findings != null && !s337Findings.isEmpty()) return s337Findings;
        return null;
    }

    /**
     * Returns the first non-empty evidence image URI across all sections,
     * useful for thumbnail display on list cards.
     */
    public String getEvidenceImageUri() {
        if (s331Image1 != null && !s331Image1.isEmpty()) return s331Image1;
        if (s332Image1 != null && !s332Image1.isEmpty()) return s332Image1;
        if (s333Image1 != null && !s333Image1.isEmpty()) return s333Image1;
        if (s334Image1 != null && !s334Image1.isEmpty()) return s334Image1;
        if (s335Image1 != null && !s335Image1.isEmpty()) return s335Image1;
        if (s331Image2 != null && !s331Image2.isEmpty()) return s331Image2;
        if (s332Image2 != null && !s332Image2.isEmpty()) return s332Image2;
        if (s333Image2 != null && !s333Image2.isEmpty()) return s333Image2;
        if (s334Image2 != null && !s334Image2.isEmpty()) return s334Image2;
        if (s335Image2 != null && !s335Image2.isEmpty()) return s335Image2;
        return null;
    }
}