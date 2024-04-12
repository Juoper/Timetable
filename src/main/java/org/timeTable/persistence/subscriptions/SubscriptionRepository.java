package org.timeTable.persistence.subscriptions;

import kotlin.jvm.internal.SerializedIr;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
    List<Subscription> findAllByVerifiedTrue();
}
