package org.timeTable.communicationLayer.exceptions;

public class MoreThanOneStudentFoundException extends Exception{
    public MoreThanOneStudentFoundException(String errorMessage) {
        super(errorMessage);
    }
}
