package com.dlim2012.appuser.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

//@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "url")
public class UrlEntity implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "url_id", nullable = false)
    private Integer urlId;

    @Column(name = "short_url_path", length=30, nullable = false)
    private String shortUrlPath;

    @Column(name = "long_url", length=2047, nullable = false)
    private String longUrl;

    @Column(name = "text")
    private String text;

    @Column(name = "url_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "url_expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_private", updatable = false)
    private Boolean isPrivate;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    public String getShortUrlPath() {
        return shortUrlPath;
    }

    public void setShortUrlPath(String shortUrlPath) {
        this.shortUrlPath = shortUrlPath;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
