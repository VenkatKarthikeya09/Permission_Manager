document.addEventListener("DOMContentLoaded", function () {
    fetch("TermsServlet")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Fetched data:", data); // ✅ Debugging: Log data to console
            if (!Array.isArray(data) || data.length === 0) {
                console.error("No valid data received");
                return;
            }

            // Select the existing container
            const container = document.querySelector(".terms-container");
            container.innerHTML = ""; // Clear previous data if any

            data.forEach(app => {
                // Create a separate container for each app
                const appBox = document.createElement("div");
                appBox.classList.add("app-box");

                appBox.innerHTML = `
                    <div class="app-info">
                        <h3>${app.app_name}</h3>
                    </div>
                    <div class="terms-summary">
                        <p>${app.terms_conditions}</p>
                    </div>
                `;

                container.appendChild(appBox);
            });
        })
        .catch(error => console.error("Error fetching terms:", error));
});

// Search Functionality
function filterApps() {
    let input = document.getElementById("searchBar").value.toLowerCase();
    let appContainers = document.querySelectorAll(".app-box"); 

    appContainers.forEach(container => {
        let appName = container.querySelector(".app-info h3").innerText.toLowerCase(); // Ensure h3 contains the app name
        if (appName.includes(input)) {
            container.style.display = "flex"; // Show matching apps
        } else {
            container.style.display = "none"; // Hide non-matching apps
        }
    });
}
