package ndid.omc.recview.model;

/**
 * Created by adikwidiasmono on 17/11/17.
 */

public class History {
    private String historyId;
    private String hospital;
    private String hospitalImgUrl;
    private String doctor;
    private String summary;
    private Long createdDate;

    public History() {
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getHospitalImgUrl() {
        return hospitalImgUrl;
    }

    public void setHospitalImgUrl(String hospitalImgUrl) {
        this.hospitalImgUrl = hospitalImgUrl;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }
}
