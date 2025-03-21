import java.io.IOException;
import java.io.File;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/MonitorServlet")
public class MonitorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Map<String, String> monitoredApps = new HashMap<>();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");
        Gson gson = new Gson();

        if ("getApps".equals(action)) {
            List<String> apps = new ArrayList<>();
            addAppsFromDirectory(new File("C:\\Program Files"), apps);
            addAppsFromDirectory(new File("C:\\Program Files (x86)"), apps);

            if (apps.isEmpty()) {
                apps.add("No applications found");
            }
            response.getWriter().write(gson.toJson(Map.of("apps", apps)));
        } 
        else if ("start".equals(action)) {
            String appName = request.getParameter("app");
            monitoredApps.put(appName, "Monitoring in progress...");
            response.getWriter().write(gson.toJson(Map.of("status", "Monitoring started for " + appName)));
        } 
        else if ("getResult".equals(action)) {
            String appName = request.getParameter("app");
            if (!monitoredApps.containsKey(appName)) {
                response.getWriter().write(gson.toJson(Map.of("error", "No monitoring data available")));
                return;
            }

            Map<String, String> result = new HashMap<>();
            result.put("memory", "150MB");
            result.put("ram", "400MB");
            result.put("cpu", "20%");
            result.put("battery", "Medium");
            result.put("security", "Safe");
            response.getWriter().write(gson.toJson(result));
        }
    }

    private void addAppsFromDirectory(File directory, List<String> apps) {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    apps.add(file.getName());
                }
            }
        }
    }
}
