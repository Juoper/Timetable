package org.timeTable.CommunicationLayer;

import org.timeTable.CommunicationLayer.exceptions.moreThenOneStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.noStudentFoundException;
import org.timeTable.LiteSQL;
import org.timeTable.TimeTableScraper.TimeTableScrapper;
import org.timeTable.models.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

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

    public int getStudentIdByName(String prename, String surname) throws noStudentFoundException, moreThenOneStudentFoundException {

        boolean prenameGiven = !prename.equals("");
        boolean surnameGiven = !surname.equals("");

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT id FROM student WHERE ");

        if (prenameGiven) {
            builder.append("prename = '").append(prename).append("'");
        }
        if (surnameGiven && prenameGiven) {
            builder.append(" AND ");
        }
        if (surnameGiven) {
            builder.append("surname = '").append(surname).append("'");
        }


        System.out.println(builder.toString());
        ResultSet set = LiteSQL.onQuery(builder.toString());
        int id = 0;
        try {
            if(set != null){
                if (set.next()){
                    id = set.getInt("id");
                    if (set.next()) {
                        set.close();
                        throw new moreThenOneStudentFoundException("Found more then one Student");
                    }
                }
            } else {
                throw new noStudentFoundException("Can't find a student matching the given data");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
    //private sendNewsTimer

}


