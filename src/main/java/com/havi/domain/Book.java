package com.havi.domain;

import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by KimYJ on 2017-08-29.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Book implements Serializable {
    private static final long serialVersionUID = 8530213963961662300L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column
    private Timestamp publishedAt;
}
