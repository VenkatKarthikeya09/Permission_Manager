import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class PermissionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Database connection details
        String dbURL = "jdbc:mysql://localhost:3306/permissionmanager";
        String dbUsername = "root";
        String dbPassword = "tiger";

        // SQL query to fetch app data
        String query = "SELECT app_name, storage, camera, location, nearby_devices, contacts, microphone FROM safe_permissions";

        PrintWriter out = response.getWriter();

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                // Build JSON response
                StringBuilder jsonResponse = new StringBuilder();
                jsonResponse.append("[");

                while (rs.next()) {
                    jsonResponse.append("{");
                    jsonResponse.append("\"app_name\": \"" + rs.getString("app_name") + "\", ");
                    jsonResponse.append("\"storage\": " + rs.getBoolean("storage") + ", ");
                    jsonResponse.append("\"camera\": " + rs.getBoolean("camera") + ", ");
                    jsonResponse.append("\"location\": " + rs.getBoolean("location") + ", ");
                    jsonResponse.append("\"nearby_devices\": " + rs.getBoolean("nearby_devices") + ", ");
                    jsonResponse.append("\"contacts\": " + rs.getBoolean("contacts") + ", ");
                    jsonResponse.append("\"microphone\": " + rs.getBoolean("microphone"));
                    jsonResponse.append("}, ");
                }

                // Remove the last comma and close JSON array properly
                if (jsonResponse.length() > 1) {
                    jsonResponse.setLength(jsonResponse.length() - 2);
                }
                jsonResponse.append("]");

                // Send JSON response
                out.print(jsonResponse.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                out.print("[]");  // Return empty JSON array on error
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.print("[]");  // Return empty JSON array if driver is not found
        }
    }
}
