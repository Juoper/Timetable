package org.timeTable.CommunicationLayer.exceptions;

public class noStudentFoundException extends Exception {
    public noStudentFoundException(String errorMessage) {
        super(errorMessage);
    }
}
