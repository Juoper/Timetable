package org.timeTable.persistence.subscriptions;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.timeTable.persistence.student.Student;

import java.io.Serializable;
import java.time.LocalTime;

@Entity
public abstract class Subscription implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    public Student student;
    private LocalTime updateTime;
    private int offsetDays;
    boolean verified;

    public Subscription() {

    }

    public Long getId() {
        return id;
    }

    public Subscription(Student student, LocalTime updateTime, int offsetDays) {
        this.student = student;
        this.updateTime = updateTime;
        this.offsetDays = offsetDays;
        this.verified = false;
    }

    public Student getStudent() {
        return student;
    }

    public LocalTime getUpdateTime() {
        return updateTime;
    }

    public int getOffsetDays() {
        return offsetDays;
    }

    public boolean isVerified() {
        return verified;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(student.getId())
                .append(updateTime)
                .append(offsetDays)
                .toHashCode();
    }
}
