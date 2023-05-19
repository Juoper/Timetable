package org.timeTable.persistence.subscriptions.comServiceDiscord;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.subscriptions.Subscription;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ComServiceDiscordSubscription extends Subscription{

    long userId;
    private long channelId;
    private ChannelType channelType;
    int user_verified;

    public ComServiceDiscordSubscription(Student student, LocalTime updateTime, int offsetDays, long userId, long channelId, ChannelType channelType, int user_verified) {
        super(student, updateTime, offsetDays);
        this.userId = userId;
        this.channelId = channelId;
        this.channelType = channelType;
        this.user_verified = user_verified;
    }

    public ComServiceDiscordSubscription() {
        super();
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}


