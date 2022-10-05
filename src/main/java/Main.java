import org.apache.commons.text.StringEscapeUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        //Loading an existing document
        File file = new File("Stundenplan.pdf");
        PDDocument document = PDDocument.load(file);
        PDPage page = document.getDocumentCatalog().getPages().get(160);

        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition( true );

//        Rectangle rect = new Rectangle( 500, 309, 385, 130);
//        Rectangle rect2 = new Rectangle( 500 + 385, 309 + 130, 385, 130);
//        stripper.addRegion( "class2", rect2 );

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x <5; x++) {
                Rectangle rect = new Rectangle( 500 + 385*x, 309 + 130*y, 385, 130);
                stripper.addRegion( "class" + x + y, rect );
            }
        }

        PDPageTree allPages = document.getDocumentCatalog().getPages();
        PDPage firstPage = allPages.get( 159 );
        stripper.extractRegions( firstPage );
        System.out.println("---------------------------------------------------------------------------------------------------");

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 5; x++) {
                String text = stripper.getTextForRegion( "class" + x + y);
                text = text.replaceFirst("^ ", "");

                if (text.equals("\r\n")) {
                    text = "Freistunde ";
                }

                text = text.replace("\r\n", "");
                text += " ".repeat(18- text.length());
                text = text.replace("195", "1g5");
                System.out.print(text + " | ");
            }
            System.out.println();
            System.out.println("---------------------------------------------------------------------------------------------------");
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
