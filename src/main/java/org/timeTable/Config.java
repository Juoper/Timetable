package org.timeTable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {

    public static Properties prop = new Properties();

    public static String prefix;
    public static String ownerId;
    public static String token;



    public static void loadConfig(){

        String fileName = "data/Stundenplan.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException ignored) {
        }

        token = prop.getProperty("Stundenplan.token");
        ownerId = prop.getProperty("Stundenplan.ownerId");

    }


}
