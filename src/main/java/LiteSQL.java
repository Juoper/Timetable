
import java.io.File;
import java.io.IOException;
import java.sql.*;


public class LiteSQL {

    private static Connection connection;
    private static Statement stmt;

    public static void connect(){
        connection = null;

        try {
            File file = new File("datenbank.db");
            if (!file.exists()){
                file.createNewFile();
            }

            String url = "jdbc:sqlite:" + file.getPath();
            connection = DriverManager.getConnection(url);

            System.out.println("Successfully connected to database");

            stmt = connection.createStatement();
            stmt.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }
    public static void disconnect(){
        try {
            if (connection != null){
                connection.close();
                System.out.println("Verbindung zur Datenbank getrennt.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void onUpdate(String sql){
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ResultSet onQuery(String sql){

        try {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }




}

