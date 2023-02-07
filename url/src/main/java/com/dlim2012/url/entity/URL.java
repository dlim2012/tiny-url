package com.dlim2012.url.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class URL {
    @Id
    @SequenceGenerator(
            name = "url_id_sequence",
            sequenceName =  "url_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "url_id_sequence"
    )
    private Long id;
    @Column(name="long_url", nullable = false)
    private String longURL;
    @Column(name="short_url_path", nullable = false)
    private String shortURLPath;
}
