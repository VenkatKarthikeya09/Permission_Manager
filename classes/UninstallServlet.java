import java.io.IOException;
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

@WebServlet("/UninstallServlet")
public class UninstallServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Map<String, String>> untrustedApps = getUntrustedApps();

        Map<String, Object> result = new HashMap<>();
        result.put("untrustedApps", untrustedApps);

        String json = new Gson().toJson(result);
        response.getWriter().write(json);
    }

    private List<Map<String, String>> getUntrustedApps() {
        List<Map<String, String>> untrustedApps = new ArrayList<>();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "root", "tiger");
            
            String sql = "SELECT app_name, reason FROM applications WHERE trusted = 0";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> app = new HashMap<>();
                app.put("name", rs.getString("app_name"));
                app.put("reason", rs.getString("reason"));
                untrustedApps.add(app);
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return untrustedApps;
    }
}
