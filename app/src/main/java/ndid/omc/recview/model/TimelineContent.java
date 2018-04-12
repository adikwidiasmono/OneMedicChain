package ndid.omc.recview.model;

import java.util.Date;

/**
 * Created by adikwidiasmono on 17/11/17.
 */

public class TimelineContent {
    private String timelineId;
    private String creator;
    private String creatorImgUrl;
    private String title;
    private String content;
    private Long createdDate;
    private String hospital;

    public TimelineContent() {
    }

    public String getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(String timelineId) {
        this.timelineId = timelineId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorImgUrl() {
        return creatorImgUrl;
    }

    public void setCreatorImgUrl(String creatorImgUrl) {
        this.creatorImgUrl = creatorImgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
