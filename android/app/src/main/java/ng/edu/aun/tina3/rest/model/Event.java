package ng.edu.aun.tina3.rest.model;

import ng.edu.aun.tina3.rest.model.abs.Entity;

/**
 * Created by joeyblack on 11/28/16.
 */

public class Event extends Entity {

    public static class Constants {
        public static class Fields {
            public static final String ID = "id",
            USER_ID = "userId", SMART_PLUG_ID = "smartPlugId",
            DATE = "date", START = "start", END = "end", PREDICTED = "predicted", STATUS = "status";
        }
    }

    private String id;
    private Integer userId;
    private String smartPlugId;
    private String date;
    private Integer start;
    private Integer end;
    private Integer predicted;
    private Integer status;

    public String getId() {
        return id;
    }

    public Event setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public Event setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getSmartPlugId() {
        return smartPlugId;
    }

    public Event setSmartPlugId(String smartPlugId) {
        this.smartPlugId = smartPlugId;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Event setDate(String date) {
        this.date = date;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public Event setStart(Integer start) {
        this.start = start;
        return this;
    }

    public Integer getEnd() {
        return end;
    }

    public Event setEnd(Integer end) {
        this.end = end;
        return this;
    }

    public Integer getPredicted() {
        return predicted;
    }

    public Event setPredicted(Integer predicted) {
        this.predicted = predicted;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Event setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public enum Status {
        BUILDING, SCHEDULED, ONGOING, DONE
    }

}
