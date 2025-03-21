import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ScanAppsServlet")
public class ScanAppsServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/permissionmanager";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "tiger";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        List<Map<String, Object>> appsWithPermissions = getInstalledApplicationsWithPermissions();
        out.print("[");

        for (int i = 0; i < appsWithPermissions.size(); i++) {
            Map<String, Object> app = appsWithPermissions.get(i);
            String appName = (String) app.get("appName");
            List<String> appPermissions = (List<String>) app.get("permissions");

            // Compare with database permissions
            String permissionStatus = compareWithDatabasePermissions(appName, appPermissions);

            // Prepare the response JSON (escaping the permissions array)
            out.print("{");
            out.print("\"appName\": \"" + appName + "\",");
            out.print("\"permissions\": " + toJsonArray(appPermissions) + ",");
            out.print("\"status\": \"" + permissionStatus + "\"");

            // If the app is red, include excessive permissions and database permissions
            if (permissionStatus.equals("red")) {
                List<String> excessivePermissions = getExcessivePermissions(appPermissions, appName);
                out.print(",\"excessivePermissions\": " + toJsonArray(excessivePermissions));
                out.print(",\"dbPermissions\": " + toJsonArray(getDbPermissions(appName)));
            }
            out.print("}");

            if (i < appsWithPermissions.size() - 1) {
                out.print(",");
            }
        }

        out.print("]");
        out.flush();
    }

    private List<Map<String, Object>> getInstalledApplicationsWithPermissions() {
        List<Map<String, Object>> appList = new ArrayList<>();
        try {
            ProcessBuilder builder = new ProcessBuilder("powershell", "Get-StartApps | Select-Object -ExpandProperty Name");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Map<String, Object> appDetails = new HashMap<>();
                    appDetails.put("appName", line.trim());

                    // For demo purposes, assign mock permissions for each app.
                    List<String> permissions = generateMockPermissions();
                    appDetails.put("permissions", permissions);

                    appList.add(appDetails);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appList;
    }

    private List<String> generateMockPermissions() {
        List<String> permissionsList = new ArrayList<>();
        permissionsList.add("Storage");
        permissionsList.add("Camera");
        permissionsList.add("Location");
        permissionsList.add("Nearby Devices (WiFi, Bluetooth, Network)");
        permissionsList.add("Contacts");
        permissionsList.add("Microphone");
        Collections.shuffle(permissionsList);
        return permissionsList;
    }

    private String compareWithDatabasePermissions(String appName, List<String> appPermissions) {
        String permissionStatus = "grey";  // Default to grey for unknown apps
        
        // Establish DB connection
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM app_permissions WHERE app_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, appName);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    // Retrieve permissions from the database
                    List<String> dbPermissions = getDbPermissions(appName);

                    // Compare system permissions with DB permissions
                    if (dbPermissions.equals(appPermissions)) {
                        permissionStatus = "green"; // Exact match
                    } else if (dbPermissions.size() < appPermissions.size()) {
                        permissionStatus = "red"; // Excessive permissions
                    } else {
                        permissionStatus = "green"; // Match with fewer permissions, considered safe
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissionStatus;
    }

    private List<String> getDbPermissions(String appName) {
        List<String> dbPermissions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM app_permissions WHERE app_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, appName);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    if (rs.getBoolean("storage")) dbPermissions.add("Storage");
                    if (rs.getBoolean("camera")) dbPermissions.add("Camera");
                    if (rs.getBoolean("location")) dbPermissions.add("Location");
                    if (rs.getBoolean("nearby_devices")) dbPermissions.add("Nearby Devices (WiFi, Bluetooth, Network)");
                    if (rs.getBoolean("contacts")) dbPermissions.add("Contacts");
                    if (rs.getBoolean("microphone")) dbPermissions.add("Microphone");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbPermissions;
    }

    private List<String> getExcessivePermissions(List<String> appPermissions, String appName) {
        List<String> excessivePermissions = new ArrayList<>();
        List<String> dbPermissions = getDbPermissions(appName);
        
        // Find excessive permissions (permissions in app that aren't in DB)
        for (String permission : appPermissions) {
            if (!dbPermissions.contains(permission)) {
                excessivePermissions.add(permission);
            }
        }
        return excessivePermissions;
    }

    // Helper method to convert the List<String> of permissions to a valid JSON array string
    private String toJsonArray(List<String> permissions) {
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < permissions.size(); i++) {
            jsonArray.append("\"").append(permissions.get(i)).append("\"");
            if (i < permissions.size() - 1) {
                jsonArray.append(", ");
            }
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }
}
