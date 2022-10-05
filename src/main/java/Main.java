import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ManagementOverAll moa = new ManagementOverAll();
        moa.AlleSchülerAusgeben();
        System.out.println("-----");
        Kurs k = moa.getGrundkursVon("Benedikt Gwuzdz");
        moa.KurslisteVon(k);
        System.out.println("-----");
        moa.KurslisteVon(new Kurs("Mu", "1mu1", "DER"));
    }

}
