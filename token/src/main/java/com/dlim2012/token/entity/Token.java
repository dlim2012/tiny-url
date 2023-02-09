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
    @SequenceGenerator(
            name = "token_id_sequence",
            sequenceName =  "token_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "token_id_sequence"
    )
    private int seed;
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name="expire_date", nullable = false)
    private LocalDate expireDate;

}
