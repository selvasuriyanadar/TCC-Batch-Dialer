package com.github.selvasuriyanadar.tcc.batchdialer.model;

import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;

@Entity
public class ClickToCallResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
