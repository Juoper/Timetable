package org.timeTable.CommunicationLayer.exceptions;

public class moreThenOneStudentFoundException extends Exception {
    public moreThenOneStudentFoundException(String errorMessage) {
        super(errorMessage);
    }
}