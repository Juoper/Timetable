package org.timeTable.persistence.subscriptions.comServiceWhatsApp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComServiceWhatsAppRepository extends CrudRepository<ComServiceWhatsAppSubscription, Long>{

}
