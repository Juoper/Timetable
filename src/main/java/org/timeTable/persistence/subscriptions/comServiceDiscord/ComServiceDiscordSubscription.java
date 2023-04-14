package org.timeTable.persistence.subscriptions.comServiceDiscord;

import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.subscriptions.Subscription;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ComServiceDiscordSubscription extends Subscription{

    long userId;
    long channelId;
    String channel_type;
    int user_verified;

    public ComServiceDiscordSubscription(Student student, int typeId, LocalTime updateTime, int offsetDays, long userId, long channelId, String channel_type, int user_verified) {
        super(student, typeId, updateTime, offsetDays);
        this.userId = userId;
        this.channelId = channelId;
        this.channel_type = channel_type;
        this.user_verified = user_verified;
    }
}


