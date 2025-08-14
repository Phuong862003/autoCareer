package com.demo.autocareer.model;

import java.io.Serializable;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.BehaviorType;
import com.demo.autocareer.model.enums.OrganizationType;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "student_behavior")
public class StudentBehavior extends BaseEntity<Long> implements Serializable{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    Job job;

    @Enumerated(EnumType.STRING)
    @Column(name = "behavior_type")
    BehaviorType behaviorType;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }

    
}
