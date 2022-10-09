package models;

public class Teacher {

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

    public String getPrename() {
        return prename;
    }

    public String getSurname() {
        return surname;
    }
}
