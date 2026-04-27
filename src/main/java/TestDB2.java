import java.sql.*;
public class TestDB2 {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_events_db", "root", "Yogaraj12345@@");
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Events");
        while(rs.next()) {
            System.out.println("ID: " + rs.getInt("event_id"));
        }
    }
}
