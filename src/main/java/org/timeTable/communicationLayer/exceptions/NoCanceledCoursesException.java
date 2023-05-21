package org.timeTable.communicationLayer.exceptions;

public class NoCanceledCoursesException extends Exception{
    public NoCanceledCoursesException(String errorMessage) {
        super(errorMessage);
    }

    public NoCanceledCoursesException(){
        this("No Courses got canceled");
    }
}
