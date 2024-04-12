package org.timeTable.persistence.subscriptions.comServiceWhatsApp;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.subscriptions.Subscription;



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
