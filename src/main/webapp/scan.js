document.addEventListener("DOMContentLoaded", function () {
    let scannedApps = []; // Store scanned results for searching

    document.getElementById("scanButton").addEventListener("click", function () {
        document.getElementById("scanResults").innerHTML = "Scanning...";
        document.getElementById("scanResults").style.display = "block"; // Show scan results container
        document.getElementById("scanResults").classList.add("fade-in");

        fetch("ScanAppsServlet")
            .then(response => response.json())
            .then(data => {
                scannedApps = data; // Store scanned results for searching
                displayResults(data); // Call function to display results
            })
            .catch(error => {
                console.error("Error scanning apps:", error);
                document.getElementById("scanResults").innerHTML = "Error fetching data.";
            });
    });

    function displayResults(apps) {
        let resultDiv = document.getElementById("scanResults");
        resultDiv.innerHTML = "<h3>Scanned Applications:</h3><ul>";

        apps.forEach(app => {
            let appContainerClass = app.status; // Get status (green, red, grey)
            let appHTML = `
                <li class="${appContainerClass}" data-app-name="${app.appName.toLowerCase()}">
                    <strong>${app.appName} Permissions:</strong><br>
                    <ul>
                        ${app.permissions.map(permission => `<li>${permission}</li>`).join('')}
                    </ul>`;

            if (app.status === "red") {
                appHTML += `
                    <div class="permissions-container">
                        <div class="permissions-box">
                            <strong>Excessive Permissions Used:</strong>
                            <ul>
                                ${app.excessivePermissions.map(permission => `<li>${permission}</li>`).join('')}
                            </ul>
                        </div>
                        <div class="permissions-box">
                            <strong>Secure Permissions:</strong>
                            <ul>
                                ${app.dbPermissions.map(permission => `<li>${permission}</li>`).join('')}
                            </ul>
                        </div>
                    </div>`;
            }

            appHTML += `</li>`;
            resultDiv.innerHTML += appHTML;
        });

        resultDiv.innerHTML += "</ul>";
    }

    // 🔍 Search Functionality (Filters results dynamically)
    document.getElementById("searchBar").addEventListener("input", function () {
        let searchValue = this.value.toLowerCase();
        let appItems = document.querySelectorAll("#scanResults li");

        let found = false;
        appItems.forEach(item => {
            let appName = item.getAttribute("data-app-name");
            if (appName.includes(searchValue)) {
                item.style.display = "block";
                found = true;
            } else {
                item.style.display = "none";
            }
        });

        let resultDiv = document.getElementById("scanResults");
        if (!found && searchValue !== "") {
            resultDiv.innerHTML = "<p>No matching applications found.</p>";
        }
    });
});
