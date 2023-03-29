package org.timeTable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {

    public static Properties prop = new Properties();

    public static String ownerId;
    public static String discordToken;
    public static String whatsAppToken;



    public static void loadConfig(){

        String fileName = "data/Stundenplan.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException ignored) {
        }

        discordToken = prop.getProperty("Stundenplan.discordToken");
        whatsAppToken = prop.getProperty("Stundenplan.whatsAppToken");
        ownerId = prop.getProperty("Stundenplan.ownerId");

    }


}
