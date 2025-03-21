document.addEventListener("DOMContentLoaded", function () {
    const statusMessage = document.getElementById("statusMessage");
    const errorMessage = document.getElementById("errorMessage");
    const appContainers = document.getElementById("appContainers");
    const searchBar = document.getElementById("searchBar");

    if (!appContainers) {
        console.error("❌ Mistake: Element 'appContainers' not found!");
        return;
    }

    // Display status message
    statusMessage.textContent = "🔄 Fetching data...";

    fetch("http://localhost:8080/Permission_manager/UpdateServlet")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("✅ Data received:", data);

            if (!Array.isArray(data) || data.length === 0) {
                console.warn("⚠️ Warning: No data received.");
                statusMessage.textContent = "⚠️ No data available.";
                return;
            }

            // Clear existing rows
            appContainers.innerHTML = "";

            // Insert data into the table
            data.forEach(app => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${app.app_name || "Unknown App"}</td>
                    <td>${app.recent_update_date || "N/A"}</td>
                    <td>${app.stable_update_date || "N/A"}</td>
                `;
                appContainers.appendChild(row);
            });

            statusMessage.textContent = "✅ Data loaded successfully!";
        })
        .catch(error => {
            console.error("❌ Error fetching data:", error);
            statusMessage.textContent = "❌ Error loading data!";
            errorMessage.style.display = "block";
        });

    // Search Functionality (Prevent Shrinking)
    searchBar.addEventListener("keyup", function () {
        const searchInput = searchBar.value.toLowerCase();
        const rows = document.querySelectorAll("#appContainers tr");

        let found = false;

        rows.forEach(row => {
            const appName = row.cells[0].textContent.toLowerCase();
            if (appName.includes(searchInput)) {
                row.style.display = ""; // Show row
                found = true;
            } else {
                row.style.display = "none"; // Hide row
            }
        });

        // Show message if no results found
        if (!found) {
            statusMessage.textContent = "⚠️ No matching results!";
        } else {
            statusMessage.textContent = "✅ Data loaded successfully!";
        }
    });
});
