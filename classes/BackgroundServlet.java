import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@WebServlet("/BackgroundServlet")
public class BackgroundServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get the unique list of running applications with count
            Map<String, Integer> runningApps = getRunningProcesses();

            // Print output as HTML
            out.println("<html><head>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; text-align: center; background-color: #cce7ff; }"); // Light blue background
            out.println(".app-container {"
                    + "display: flex; flex-wrap: wrap; gap: 15px; "
                    + "justify-content: center; padding: 20px; }");

            // Container styles
            out.println(".app-box {"
                    + "padding: 15px; border-radius: 8px; "
                    + "box-shadow: 3px 3px 10px rgba(0,0,0,0.2);"
                    + "text-align: center; font-size: 18px; min-width: 180px; "
                    + "transition: transform 0.2s, box-shadow 0.2s; }");

            // Hover effect
            out.println(".app-box:hover {"
                    + "transform: scale(1.1);"
                    + "box-shadow: 5px 5px 15px rgba(0,0,0,0.3); }");

            // Assign different colors to apps
            out.println(".color1 { background-color: #ff9999; color: #000; }"); // Light Red
            out.println(".color2 { background-color: #99ff99; color: #000; }"); // Light Green
            out.println(".color3 { background-color: #9999ff; color: #fff; }"); // Light Blue
            out.println(".color4 { background-color: #ffff99; color: #000; }"); // Light Yellow
            out.println(".color5 { background-color: #ffcc99; color: #000; }"); // Light Orange

            out.println("</style>");
            out.println("</head><body>");
            out.println("<h2>Running Applications</h2>");
            out.println("<div class='app-container'>");

            int colorIndex = 1;
            for (Map.Entry<String, Integer> entry : runningApps.entrySet()) {
                String colorClass = "color" + (colorIndex % 5 + 1); // Cycle through color classes
                out.println("<div class='app-box " + colorClass + "'>" + entry.getKey() + " (" + entry.getValue() + ")</div>");
                colorIndex++;
            }

            out.println("</div>");
            out.println("</body></html>");
        } catch (Exception e) {
            out.println("<p style='color: red;'>Error retrieving running applications.</p>");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    // Function to get unique running applications with count
    private Map<String, Integer> getRunningProcesses() throws IOException {
        Map<String, Integer> processCount = new HashMap<>();

        Process process = Runtime.getRuntime().exec("tasklist");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(".exe")) {  // Filter only executable processes
                String[] parts = line.split("\\s+");
                if (parts.length > 0) {
                    String appName = parts[0];

                    // Count occurrences of each application
                    processCount.put(appName, processCount.getOrDefault(appName, 0) + 1);
                }
            }
        }
        reader.close();
        return processCount;
    }
}
