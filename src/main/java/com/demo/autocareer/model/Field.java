package com.demo.autocareer.model;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "field")
public class Field extends BaseEntity<Long> implements Serializable{
    @Column(name = "field_name")
    String field_name;

    @OneToMany(mappedBy = "field", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    List<SubField> subfields;

    public void setSubFields(List<SubField> subfields){
        this.subfields = subfields;
    }
}
