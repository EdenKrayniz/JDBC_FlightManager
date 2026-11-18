import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Demonstrates a SQL-injection-safe JDBC update using {@link PreparedStatement}.
 *
 * <p>The previous implementation built SQL by directly concatenating the user input:
 * {@code "UPDATE Arrivals SET delay_status = '" + userInput + "..."}.
 * This was vulnerable because a user could enter a quote or SQL syntax that altered
 * the intended statement. For example, crafted input could modify unexpected rows or
 * append additional SQL operations.</p>
 *
 * <p>This implementation uses two {@code ?} placeholders and
 * {@link PreparedStatement#setString(int, String)} to bind both values. JDBC sends
 * the SQL template and parameter values separately, so parameter values are always
 * treated as data rather than executable SQL. This prevents quotes and SQL syntax in
 * the delay status from changing the structure of the statement.</p>
 */
public class SimpleFlightUpdater {
    /**
     * Prompts for the database password and a new delay status, then securely updates
     * arrivals for flight {@code LX354}.
     *
     * @param args command-line arguments; unused
     */
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/FlightDB"
                + "?allowLoadLocalInfile=true"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Jerusalem"
                + "&useSSL=false";

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print("Enter new delay status for flight LX354: ");
            String userInput = scanner.nextLine();

            String sql = "UPDATE Arrivals SET delay_status = ? "
                    + "WHERE flight_number = ?";

            try (Connection connection =
                         DriverManager.getConnection(url, "root", password);
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(sql)) {

                preparedStatement.setString(1, userInput);
                preparedStatement.setString(2, "LX354");

                int affectedRows = preparedStatement.executeUpdate();
                System.out.println("Affected rows: " + affectedRows);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}