import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DLeakServlet")
public class DLeakServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        JSONObject responseObj = new JSONObject();

        try {
            if ("getApps".equals(action)) {
                JSONArray appArray = getRunningApps();  // Fetch currently running applications
                responseObj.put("apps", appArray);
            } 
            else if ("scan".equals(action)) {
                String appName = request.getParameter("app");
                JSONObject scanResult = scanApplication(appName);
                responseObj = scanResult;
            } 
            else {
                responseObj.put("error", "Invalid action");
            }
        } catch (Exception e) {
            responseObj.put("error", "Server error: " + e.getMessage());
        }

        out.print(responseObj.toString());
    }

    // Fetch currently running applications
    private JSONArray getRunningApps() {
        JSONArray appList = new JSONArray();
        try {
            Process process = Runtime.getRuntime().exec("tasklist"); // Windows command to get running tasks
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains(".exe")) {  // Filtering only applications
                    String appName = line.split("\\s+")[0];
                    JSONObject appObj = new JSONObject();
                    appObj.put("name", appName);
                    appObj.put("estimatedTime", getEstimatedTime(appName));
                    appList.put(appObj);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching running apps: " + e.getMessage());
        }
        return appList;
    }

    // Generate estimated scan time based on the app
    private int getEstimatedTime(String appName) {
        if (appName.toLowerCase().contains("chrome") || appName.toLowerCase().contains("firefox")) {
            return 180;  // Browsers take longer to scan
        }
        return 90;  // Default scan time
    }

    // Perform deep scan of the selected application
    private JSONObject scanApplication(String appName) {
        JSONObject result = new JSONObject();
        boolean isVulnerable = checkVulnerabilities(appName);

        if (isVulnerable) {
            result.put("vulnerable", true);
            result.put("reason", "Detected weak encryption and data leaks.");
        } else {
            result.put("vulnerable", false);
        }
        return result;
    }

    // Dummy vulnerability check (enhance this later)
    private boolean checkVulnerabilities(String appName) {
        List<String> vulnerableApps = Arrays.asList("chrome.exe", "firefox.exe", "vlc.exe");
        return vulnerableApps.contains(appName.toLowerCase());
    }
}
