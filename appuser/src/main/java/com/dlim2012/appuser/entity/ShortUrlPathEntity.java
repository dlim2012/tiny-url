package com.dlim2012.appuser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "short_url_path")
public class ShortUrlPathEntity {
    @Id
    @Column(name="short_url_path", length=7)
    private String shortUrlPath;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="expire_day")
    private LocalDate expireDay;

    public String getShortUrlPath() {
        return shortUrlPath;
    }

    public void setShortUrlPath(String shortUrlPath) {
        this.shortUrlPath = shortUrlPath;
    }

    @Override
    public String toString() {
        return "ShortUrlPathEntity{" +
                "shortUrlPath='" + shortUrlPath + '\'' +
                ", createdAt=" + createdAt +
                ", expireDay=" + expireDay +
                '}';
    }
}
