package org.timeTable.persistence.subscriptions.comServiceWhatsApp;

import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.subscriptions.Subscription;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ComServiceWhatsAppSubscription extends Subscription {

        private String phone_number;

        public ComServiceWhatsAppSubscription() {
        }

        public ComServiceWhatsAppSubscription(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }
}
