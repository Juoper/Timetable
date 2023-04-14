package org.timeTable.persistence.subscriptions;

import org.timeTable.persistence.student.Student;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalTime;

@Entity
public abstract class Subscription implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    public Student student;
    int typeId;
    public LocalTime updateTime;
    public int offsetDays;
    boolean verified;

    public Long getId() {
        return id;
    }

    public int getTypeId() {
        return typeId;
    }

    public Subscription(Student student, int typeId, LocalTime updateTime, int offsetDays) {
        this.student = student;
        this.typeId = typeId;
        this.updateTime = updateTime;
        this.offsetDays = offsetDays;
        this.verified = false;
    }
}
