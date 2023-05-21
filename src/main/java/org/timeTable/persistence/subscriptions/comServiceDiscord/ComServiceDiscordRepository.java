package org.timeTable.persistence.subscriptions.comServiceDiscord;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.timeTable.persistence.student.Student;

import java.util.List;

@Repository
public interface ComServiceDiscordRepository extends CrudRepository<ComServiceDiscordSubscription, Long> {

    ComServiceDiscordSubscription findByUserId(long userId);

    List<ComServiceDiscordSubscription> findAllByStudentAndChannelTypeAndChannelIdAndUserId(Student student, ChannelType channelType, long idLong, long idLong1);

    List<ComServiceDiscordSubscription> findAllByUserId(long userId);
}
