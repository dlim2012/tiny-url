package com.dlim2012.shorturl.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tiny_url")
public class UrlEntity {
    // short URL path as a key is unique
    // long URL and username as a key is unique

    @PrimaryKeyColumn(name = "key", ordinal=0, type= PrimaryKeyType.PARTITIONED)
    private String key;

    @PrimaryKeyColumn(name = "query_name", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String queryName;
    // when key is a short URL path: input is "" for public URLs and username for private URLs
    // when key is a long URL: input is "1" + username for public URLs and "2" + username for private URLs
    // other cases: if length >= 7, could be duplicated with emails

    @Column("value")
    private String value;

    @Column("text")
    private String text;

    @Column("is_active")
    private Boolean isActive;

    public UrlEntity(String key, String queryName, String value, String text, Boolean isActive) {
        this.key = key;
        this.queryName = queryName;
        this.value = value;
        this.text = text;
        this.isActive = isActive;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }


}

