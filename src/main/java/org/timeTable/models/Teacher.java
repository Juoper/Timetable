package org.timeTable.models;

public class Teacher {
    int id;
    String abbreviation;
    String prename;
    String surname;

    public Teacher(String abbreviation) {
        this.abbreviation = abbreviation;
        this.prename = "";
        this.surname = "";
    }

    public String getAbbreviation() {
        return abbreviation;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrename() {
        return prename;
    }

    public String getSurname() {
        return surname;
    }
}
