package com.dlim2012.longurl.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("short_path_to_long")
public class ShortPathToLong {

    @PrimaryKey("short_url_path")
    private String shortURLPath;

    @Column("long_url")
    private String longURL;

    public ShortPathToLong(String shortURLPath, String longURL) {
        this.shortURLPath = shortURLPath;
        this.longURL = longURL;
    }

    public String getShortURLPath() {
        return shortURLPath;
    }

    public void setShortURLPath(String shortURLPath) {
        this.shortURLPath = shortURLPath;
    }

    public String getLongURL() {
        return longURL;
    }

    public void setLongURL(String longURL) {
        this.longURL = longURL;
    }
}
