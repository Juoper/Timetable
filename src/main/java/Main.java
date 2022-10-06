

import models.Timetable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        //Change every name to prename + surname
        //Loading an existing document
        File file = new File("Stundenplan.pdf");
        PDDocument document = PDDocument.load(file);
        int pageCount = document.getDocumentCatalog().getPages().getCount();
        
        List<Timetable> timetables = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            timetables.add(extractData.generateTimetable(document.getDocumentCatalog().getPages().get(i), i));
        }
        
        document.close();
        


//        ManagementOverAll moa = new ManagementOverAll();
//        moa.AlleSchülerAusgeben();
//        System.out.println("-----");
//        Kurs k = moa.getGrundkursVon("Benedikt Gwuzdz");
//        moa.KurslisteVon(k);
//        System.out.println("-----");
//        moa.KurslisteVon(new Kurs("Mu", "1mu1", "DER"));
    }

}
