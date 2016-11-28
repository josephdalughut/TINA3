package ng.edu.aun.tina3.rest.model;

import ng.edu.aun.tina3.rest.model.abs.Entity;

/**
 * Created by joeyblack on 11/21/16.
 */

public class Token extends Entity {

    private String id;
    private String type;
    private Integer userId;
    private Long expiresAt;

    public String getId() {
        return id;
    }

    public Token setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Token setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public Token setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public Token setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }
}
