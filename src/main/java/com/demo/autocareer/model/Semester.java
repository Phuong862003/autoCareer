package com.demo.autocareer.model;

import java.io.Serializable;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "semester")
public class Semester extends BaseEntity<Long> implements Serializable{
    @Column(name = "code")
    String code;

    @Column(name = "name")
    String name;

    public String getCode(){
        return code;
    }

    public String getName(){
        return name;
    }
}
