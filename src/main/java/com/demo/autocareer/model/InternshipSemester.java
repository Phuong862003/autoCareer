package com.demo.autocareer.model;

import java.io.Serializable;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.StatusAssign;
import com.demo.autocareer.model.enums.StatusInternSemester;
import com.demo.autocareer.model.enums.StatusRequestFile;

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
@Table(name = "internship_semester")
public class InternshipSemester extends BaseEntity<Long> implements Serializable{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private StatusInternSemester status;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public StatusInternSemester getStatus() {
        return status;
    }

    public void setStatus(StatusInternSemester status) {
        this.status = status;
    }
}
