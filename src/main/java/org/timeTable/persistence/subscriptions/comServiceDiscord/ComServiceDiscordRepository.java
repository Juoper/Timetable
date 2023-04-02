package org.timeTable.persistence.subscriptions.comServiceDiscord;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComServiceDiscordRepository extends CrudRepository<ComServiceDiscordSubscription, Long> {

}
