document.addEventListener("DOMContentLoaded", function () {
    const statusMessage = document.getElementById("statusMessage");
    const errorMessage = document.getElementById("errorMessage");
    const scoreContainer = document.getElementById("scoreContainer");
    const searchBar = document.getElementById("searchBar");

    if (!scoreContainer) {
        console.error("❌ Element 'scoreContainer' not found!");
        return;
    }

    statusMessage.textContent = "🔄 Fetching scores...";

    fetch("http://localhost:8080/Permission_manager/ScoreServlet")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("✅ Data received:", data);

            if (!Array.isArray(data) || data.length === 0) {
                console.warn("⚠️ No scores found.");
                statusMessage.textContent = "⚠️ No data available.";
                return;
            }

            // Clear previous data
            scoreContainer.innerHTML = "";

            // Insert data
            data.forEach(app => {
                const row = document.createElement("tr");

                row.innerHTML = `
                    <td>${app.app_name || "Unknown App"}</td>
                    <td>${app.security_score || "N/A"}</td>
                `;

                scoreContainer.appendChild(row);
            });

            statusMessage.textContent = "✅ Scores loaded successfully!";
        })
        .catch(error => {
            console.error("❌ Error fetching data:", error);
            statusMessage.textContent = "❌ Error loading data!";
            errorMessage.style.display = "block";
        });

    // Search Functionality
    searchBar.addEventListener("keyup", function () {
        const searchInput = searchBar.value.toLowerCase();
        const rows = document.querySelectorAll("#scoreContainer tr");

        rows.forEach(row => {
            const appName = row.cells[0].textContent.toLowerCase();
            if (appName.includes(searchInput)) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });
});
