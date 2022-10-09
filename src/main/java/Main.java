

import models.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Main {
    //TODO
    //Change every name to prename + surname

    public static void main(String[] args) throws IOException {
        LiteSQL.connect();

        Database.createTables();
        List<Timetable> timetables = extractData.extractFromPdf("Julian_Stundenplan.pdf");

        timetables.forEach(tt -> System.out.println(tt.toString()));

        timetables.forEach(Timetable::transferToDatabase);




        LiteSQL.disconnect();

//        ManagementOverAll moa = new ManagementOverAll();
//        moa.AlleSchülerAusgeben();
//        System.out.println("-----");
//        Kurs k = moa.getGrundkursVon("Benedikt Gwuzdz");
//        moa.KurslisteVon(k);
//        System.out.println("-----");
//        moa.KurslisteVon(new Kurs("Mu", "1mu1", "DER"));
    }

}
