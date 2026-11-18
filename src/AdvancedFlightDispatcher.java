import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdvancedFlightDispatcher {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/FlightDB"
                + "?allowLoadLocalInfile=true"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Jerusalem"
                + "&useSSL=false";

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            try (Connection conn = DriverManager.getConnection(url, "root", password)) {
                updateMultipleDelays(
                        conn,
                        Arrays.asList(1, 3, 5),
                        "Landed - Final"
                );
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void updateMultipleDelays(
            Connection conn,
            List<Integer> arrivalIds,
            String newStatus
    ) throws SQLException {
        try (PreparedStatement pstmt =
                     conn.prepareStatement(
                             "UPDATE Arrivals SET delay_status = ? WHERE id = ?"
                     )) {
            pstmt.setString(1, newStatus);

            for (Integer arrivalId : arrivalIds) {
                pstmt.setInt(2, arrivalId);
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            int totalAffectedRows = 0;

            for (int result : results) {
                if (result > 0) {
                    totalAffectedRows += result;
                }
            }

            System.out.println(
                    "Batch update complete: " + totalAffectedRows
                            + " arrival row(s) affected."
            );
        }
    }
}