import java.sql.*;

public class DebugDelete {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_events_db", "root", "Yogaraj12345@@");
        
        System.out.println("Current Events before delete:");
        ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM Events");
        while(rs1.next()) System.out.println(rs1.getInt("event_id"));
        
        System.out.println("Deleting 101...");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM Events WHERE event_id = ?");
        ps.setInt(1, 101);
        int rows = ps.executeUpdate();
        System.out.println("Rows affected: " + rows);
        
        System.out.println("Current Events after delete:");
        ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM Events");
        while(rs2.next()) System.out.println(rs2.getInt("event_id"));
    }
}
