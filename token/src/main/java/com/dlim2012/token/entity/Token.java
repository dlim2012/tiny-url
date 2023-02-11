package com.dlim2012.token.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    private int seed;
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name="expire_date", nullable = false)
    private LocalDate expireDate;

}
