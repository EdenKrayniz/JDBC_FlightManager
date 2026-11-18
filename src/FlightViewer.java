import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FlightViewer {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/FlightDB"
                + "?allowLoadLocalInfile=true"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Jerusalem"
                + "&useSSL=false";

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print("Enter target terminal number (2, 3, 4, or 5): ");
            String terminal = scanner.nextLine().trim();

            try (Connection conn = DriverManager.getConnection(url, "root", password);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT a.id, a.flight_number, r.from_city, a.scheduled, "
                                 + "a.delay_status, a.terminal "
                                 + "FROM Arrivals a "
                                 + "INNER JOIN FlightRoutes r ON a.flight_number = r.flight_number "
                                 + "WHERE a.terminal = ? "
                                 + "ORDER BY a.scheduled ASC"
                 )) {

                pstmt.setString(1, terminal);

                try (ResultSet rs = pstmt.executeQuery()) {
                    int flightCount = 0;
                    boolean headerPrinted = false;

                    while (rs.next()) {
                        if (!headerPrinted) {
                            System.out.printf(
                                    "%-5s | %-12s | %-15s | %-20s | %-20s | %-8s%n",
                                    "ID", "Flight", "From", "Scheduled", "Status", "Terminal"
                            );
                            System.out.printf(
                                    "%-5s-+-%-12s-+-%-15s-+-%-20s-+-%-20s-+-%-8s%n",
                                    "-----", "------------", "---------------",
                                    "--------------------", "--------------------",
                                    "--------"
                            );
                            headerPrinted = true;
                        }

                        System.out.printf(
                                "%-5d | %-12s | %-15s | %-20s | %-20s | %-8s%n",
                                rs.getInt("id"),
                                rs.getString("flight_number"),
                                rs.getString("from_city"),
                                rs.getString("scheduled"),
                                rs.getString("delay_status"),
                                rs.getString("terminal")
                        );

                        flightCount++;
                    }

                    if (flightCount == 0) {
                        System.out.println("No flights found at terminal " + terminal + ".");
                    } else {
                        System.out.println("Total flights found: " + flightCount);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}