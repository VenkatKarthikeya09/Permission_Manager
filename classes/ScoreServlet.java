import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/ScoreServlet")
public class ScoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Step 1: Servlet Started ✅");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        List<HashMap<String, Object>> scores = new ArrayList<>();
        PrintWriter out = response.getWriter();

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/permissionmanager";
        String user = "root";
        String password = "tiger";

        try {
            // Step 2: Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Step 2: JDBC Driver Loaded ✅");

            // Step 3: Establish Connection
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Step 3: Database Connection Successful ✅");

            // Step 4: Prepare SQL Query
            String query = "SELECT id, app_name, security_score FROM score";
            PreparedStatement stmt = conn.prepareStatement(query);
            System.out.println("Step 4: SQL Query Prepared ✅");

            // Step 5: Execute Query
            ResultSet rs = stmt.executeQuery();
            System.out.println("Step 5: SQL Query Executed ✅");

            // Step 6: Process ResultSet
            while (rs.next()) {
                HashMap<String, Object> scoreData = new HashMap<>();
                scoreData.put("id", rs.getInt("id"));
                scoreData.put("app_name", rs.getString("app_name"));
                scoreData.put("security_score", rs.getInt("security_score"));
                scores.add(scoreData);
            }

            // If no data found
            if (scores.isEmpty()) {
                System.out.println("Step 6: No data found ⚠️");
            } else {
                System.out.println("Step 6: Data Retrieved ✅ " + scores.size() + " rows fetched.");
            }

            // Step 7: Convert to JSON
            String json = new Gson().toJson(scores);
            System.out.println("Step 7: Data Converted to JSON ✅");

            // Step 8: Send JSON Response
            out.print(json);
            out.flush();
            System.out.println("Step 8: JSON Response Sent ✅");

            // Step 9: Close Resources
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("Step 9: Database Connection Closed ✅");

        } catch (Exception e) {
            System.out.println("❌ Error Occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
