package org.timeTable.communicationLayer.exceptions;

public class SubscriptionAlreadyExists extends Exception {
    public SubscriptionAlreadyExists(String errorMessage) {
        super(errorMessage);
    }
}
