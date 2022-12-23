package org.timeTable.CommunicationLayer;

import org.timeTable.CommunicationLayer.exceptions.moreThenOneStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.noStudentFoundException;
import org.timeTable.LiteSQL;
import org.timeTable.TimeTableScraper.TimeTableScrapper;
import org.timeTable.models.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommunicationLayer {
    ArrayList<CommunicationService> comServices;
    public CommunicationLayer(TimeTableScrapper timeTableScrapper) {
        comServices = new ArrayList<>();
    }

    public void subscribeTimtableNews(Student student, int typeID) {


        LiteSQL.onUpdate("INSERT INTO subscriptions (student_id, type_id) VALUES ('" + student.getId() + "', '" + typeID + "')");
        
    }

    //Student which student to get the courses for

    //public unsubscribeTimtableNews (Student student)

    //private sendTimetableNews (TimetableScrapper timetableScrapper)

    public CommunicationLayer registerCommunicationService(CommunicationService service) {
        comServices.add(service);
        return this;
    }

    public Student findStudentByName(String prename, String surname) throws noStudentFoundException, moreThenOneStudentFoundException {
        Student student = null;

        ResultSet set = LiteSQL.onQuery("SELECT id FROM student WHERE prename = '" +  prename + "' AND surname = '" + surname +"'");
        try {
            if(set != null){
                if (set.next()){
                    System.out.println(set.getInt("id"));

                }   else {
                    throw new moreThenOneStudentFoundException("tes2");
                }
            } else {
                throw new noStudentFoundException("test");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return student;
    }
    //private sendNewsTimer

}


