package com.dlim2012.token.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="expire_date")
    private LocalDate expireDate;
}
