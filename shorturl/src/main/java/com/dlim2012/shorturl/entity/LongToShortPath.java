package com.dlim2012.shorturl.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("long_to_short_path")
public class LongToShortPath {

    @PrimaryKey("long_url")
    private String longURL;

    @Column("short_url_path")
    private String shortURLPath;

    public LongToShortPath(String longURL, String shortURLPath) {
        this.longURL = longURL;
        this.shortURLPath = shortURLPath;
    }

    public String getLongURL() {
        return longURL;
    }

    public void setLongURL(String longURL) {
        this.longURL = longURL;
    }

    public String getShortURLPath() {
        return shortURLPath;
    }

    public void setShortURLPath(String shortURLPath) {
        this.shortURLPath = shortURLPath;
    }
}
