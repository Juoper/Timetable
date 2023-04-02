package org.timeTable.persistence.subscriptions.comServiceDiscord;

import org.timeTable.persistence.subscriptions.Subscription;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ComServiceDiscordSubscription {

    @Id
    @OneToOne
    Subscription subscription;

    long userId;
    long channelId;
    String channel_type;
    int user_verified;

}
