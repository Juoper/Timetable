package org.timeTable.CommunicationLayer.exceptions;

public class subscriptionAlreadyExists extends Exception {
    public subscriptionAlreadyExists(String errorMessage) {
        super(errorMessage);
    }
}
