import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) throws Exception {
        System.out.println("Connecting...");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_events_db", "root", "Yogaraj12345@@");
        System.out.println("Connected!");
        
        System.out.println("Inserting test event...");
        String sql = "INSERT INTO Events (event_id, name, type, date, time, venue, fee) VALUES (999, 'Test', 'T', 'D', 'T', 'V', 10) "
                   + "ON DUPLICATE KEY UPDATE name=VALUES(name)";
        PreparedStatement ps = conn.prepareStatement(sql);
        int rows = ps.executeUpdate();
        System.out.println("Rows affected: " + rows);
        
        System.out.println("Querying events...");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Events");
        while (rs.next()) {
            System.out.println("Event ID: " + rs.getInt("event_id") + " Name: " + rs.getString("name"));
        }
    }
}
