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
    @Column(name = "short_url_path", length=100)
    private String shortUrlPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    @Column(name = "is_private", updatable = false)
    private Boolean isPrivate;

    @Column(name = "is_active")
    private Boolean isActive;

    public String getShortUrlPath() {
        return shortUrlPath;
    }

    public void setShortUrlPath(String shortUrlPath) {
        this.shortUrlPath = shortUrlPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
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
