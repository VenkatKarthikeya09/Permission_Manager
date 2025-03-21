import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/UpdateServlet")
public class UpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("✅ Step 1: Inside UpdateServlet");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        List<Map<String, String>> updatesList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            System.out.println("✅ Step 2: Loading MySQL Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("✅ Step 3: Connecting to Database...");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/permissionmanager", "root", "tiger");

            System.out.println("✅ Step 4: Preparing SQL Query...");
            String sql = "SELECT app_name, recent_update_date, stable_update_date FROM updates";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.println("✅ Step 5: Fetching Data...");
            while (rs.next()) {
                Map<String, String> updateRecord = new HashMap<>();
                updateRecord.put("app_name", rs.getString("app_name"));
                updateRecord.put("recent_update_date", rs.getString("recent_update_date"));
                updateRecord.put("stable_update_date", rs.getString("stable_update_date"));
                updatesList.add(updateRecord);
            }

            System.out.println("✅ Step 6: Data Fetch Complete. Total Records: " + updatesList.size());

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(updatesList);
            System.out.println("✅ Step 7: JSON Response Created: " + jsonResponse);

            out.print(jsonResponse);
            out.flush();

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Server error. Check logs for details.\"}");
            out.flush();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
                System.out.println("✅ Step 8: Database Connection Closed.");
            } catch (Exception ex) {
                System.err.println("❌ ERROR Closing Connection: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
