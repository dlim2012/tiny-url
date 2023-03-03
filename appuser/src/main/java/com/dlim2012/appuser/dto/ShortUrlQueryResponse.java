package com.dlim2012.appuser.dto;

public class ShortUrlQueryResponse {
    String publicShortUrl = "";
    String publicDescription = "";
    String publicExpireDate = "";
    Boolean publicIsActive = false;

    String privateShortUrl = "";
    String privateDescription = "";
    String privateExpireDate = "";
    Boolean privateIsActive = false;

    public void set(Boolean isPrivate, String shortUrl, String description, String expireDate, Boolean isActive) {
        if (isPrivate){
            this.privateShortUrl = shortUrl;
            this.privateDescription = description;
            this.privateExpireDate = expireDate;
            this.privateIsActive = isActive;
        } else {
            this.publicShortUrl = shortUrl;
            this.publicDescription = description;
            this.publicExpireDate = expireDate;
            this.publicIsActive = isActive;
        }
    }

    @Override
    public String toString() {
        return "ShortUrlQueryResponse{" +
                "publicShortUrl='" + publicShortUrl + '\'' +
                ", publicDescription='" + publicDescription + '\'' +
                ", publicExpireDate=" + publicExpireDate +
                ", publicIsActive=" + publicIsActive +
                ", privateShortUrl='" + privateShortUrl + '\'' +
                ", privateDescription='" + privateDescription + '\'' +
                ", privateExpireDate=" + privateExpireDate +
                ", privateIsActive=" + privateIsActive +
                '}';
    }

    public String getPublicShortUrl() {
        return publicShortUrl;
    }

    public void setPublicShortUrl(String publicShortUrl) {
        this.publicShortUrl = publicShortUrl;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    public String getPublicExpireDate() {
        return publicExpireDate;
    }

    public void setPublicExpireDate(String publicExpireDate) {
        this.publicExpireDate = publicExpireDate;
    }

    public Boolean getPublicIsActive() {
        return publicIsActive;
    }

    public void setPublicIsActive(Boolean publicIsActive) {
        this.publicIsActive = publicIsActive;
    }

    public String getPrivateShortUrl() {
        return privateShortUrl;
    }

    public void setPrivateShortUrl(String privateShortUrl) {
        this.privateShortUrl = privateShortUrl;
    }

    public String getPrivateDescription() {
        return privateDescription;
    }

    public void setPrivateDescription(String privateDescription) {
        this.privateDescription = privateDescription;
    }

    public String getPrivateExpireDate() {
        return privateExpireDate;
    }

    public void setPrivateExpireDate(String privateExpireDate) {
        this.privateExpireDate = privateExpireDate;
    }

    public Boolean getPrivateIsActive() {
        return privateIsActive;
    }

    public void setPrivateIsActive(Boolean privateIsActive) {
        this.privateIsActive = privateIsActive;
    }
}
