package org.timeTable.models;

import org.timeTable.LiteSQL;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private String prename;
    private String surname;
    private Timetable timetable;
    public List<Course> courses;
    private Lesson[][] lessons; // 5 Tage, 11 Stunden

    public Student(int id, String name) {
        this.id = id;

        courses = new ArrayList<>();
        lessons = new Lesson[5][11];

        int i = name.trim().lastIndexOf(" ");
        String[] split = {name.substring(0, i), name.substring(i + 1)};
        this.prename = split[0];
        this.surname = split[1];

    }

    public void addTimetable(Timetable timetable) {
        this.timetable = timetable;
    }

    public String getPrename() {
        return prename;
    }

    public String getSurname() {
        return surname;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public void transferToDatabase() {

    }

    public String toString() {
        return String.join(" ", prename, surname);
    }

    public static String formatTextLine(String text){
        String ausgabe =  text
                .replace(" c ", " C ")
                .substring(6)
                .replace("| ", " ")
                .replace("  ", " ")
                .replace("g9e0", "geo")
                .replace("QAWU", "QWU")
                .replace("1m?", "1m7")
                .replace("Kw", "Ku/")
                .replace("9", "g")
                .replace("14", "1d")
                .replace(" 6", " C")
                .replace("/w", "/W")
                .replace("I/", "/")
                .replace("Cc", "C")
                .replace("Smwi", "Smw")
                .replace("Wi", "W1")
                .replace("Kuw", "Ku")
                .replace("WRI", "WR")
                .replace("Ww", "W")
                .replace("Kk", "k")
                .replace("kK", "k")
                .replace("1K3", "1k3")
                .replace("Twr", "wr")
                .replace("PA", "P1")
                .replace("/p", "/P")
                .replace("/0", "/O")
                .replace("KuP", "Ku/P")
                .replace("ge0", "geo")
                .replace("15-11", "1s-t1")
                .replace("MIW", "M/W")
                .replace("_psylps2", "psy/ps2")
                .replace("Pi", "P1")
                .replace("/Ps", "/ps")
                .replace("1smw7?", "1smw7")
                .replace("7?", "7")
                .replace("?", "")
                .replace("iwr", "1wr")
                .replace("185", "1e5")
                .replace("TurF", "Tuf")
                .replace("OO", "OE")
                .replace("]", "")
                .replace("voS", "vo5")
                .replace("GW1", "G/W1")
                .replace("Tuf", "TuF")
                .replace("Tur", "TuF")
                .replace("wS", "w5")
                .replace("//", "/")
                .replace("1smw ", "1smw7 ")
                .replace("/OE", "/O")
                .replace("HAl", "HA")
                .replace("HE)", "HE")
                .replace("nSm", "n Sm")
                ;
        if(ausgabe.startsWith(" ")){
            ausgabe = ausgabe.substring(1, ausgabe.length()-1);
        }
        if(ausgabe.startsWith("6 ")){
            ausgabe = "C " + ausgabe.substring(2);
        }
        /*
        for (int i = 1; i < ausgabe.length()-1; i++) {
            boolean b = isCap(ausgabe.charAt(i-1)) &&
                    isCap(ausgabe.charAt(i)) &&
                    !isCap(ausgabe.charAt(i+1)) &&
                            isBuchstabe(ausgabe.charAt(i+1));
            if(b){
                ausgabe = ausgabe.substring(0, i+1) + " " + ausgabe.substring(i+1);
            }
            //System.out.println(" --> " + ausgabe);
        }
        */
        return ausgabe;
    }

}
