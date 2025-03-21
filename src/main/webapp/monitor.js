document.addEventListener("DOMContentLoaded", function () {
    const monitorBtn = document.getElementById("monitorBtn");
    const appSelect = document.getElementById("appSelect");
    const appSearch = document.getElementById("appSearch");
    const monitorDuration = document.getElementById("monitorDuration");
    const monitorStatus = document.getElementById("monitorStatus");
    const monitorResults = document.getElementById("monitorResults");

    let appList = [];

    // Fetch installed applications
    fetch("MonitorServlet?action=getApps")
        .then(response => response.json())
        .then(data => {
            if (!data.apps || data.apps.length === 0) {
                appSelect.innerHTML = "<option>No applications found</option>";
            } else {
                appList = data.apps;
                displayApps(appList);
            }
        })
        .catch(error => {
            console.error("Error fetching apps:", error);
            appSelect.innerHTML = "<option>Error loading apps</option>";
        });

    // Display app list
    function displayApps(apps) {
        appSelect.innerHTML = "";
        apps.forEach(app => {
            let option = document.createElement("option");
            option.value = app;
            option.textContent = app;
            appSelect.appendChild(option);
        });
    }

    // Search Functionality
    appSearch.addEventListener("input", function () {
        let searchTerm = appSearch.value.toLowerCase();
        let filteredApps = appList.filter(app => app.toLowerCase().includes(searchTerm));
        displayApps(filteredApps);
    });

    // Start Monitoring
    monitorBtn.addEventListener("click", function () {
        let appName = appSelect.value;
        let duration = parseInt(monitorDuration.value) * 1000; // Convert to milliseconds

        if (!appName) {
            alert("Please select an application to monitor.");
            return;
        }

        // Display Scanning Message
        monitorStatus.innerHTML = `Scanning <strong>${appName}</strong> for ${monitorDuration.options[monitorDuration.selectedIndex].text}...`;
        monitorStatus.style.display = "block";
        monitorResults.style.display = "none"; // Hide previous results

        // Start Monitoring
        fetch(`MonitorServlet?action=start&app=${encodeURIComponent(appName)}&duration=${duration}`)
            .then(response => response.json())
            .then(data => {
                console.log("Monitoring started:", data);

                // Fetch monitoring result after selected time
                setTimeout(() => {
                    fetch(`MonitorServlet?action=getResult&app=${encodeURIComponent(appName)}`)
                        .then(response => response.json())
                        .then(result => {
                            // Check if the app is actually running
                            if (!result.running) {
                                document.getElementById("memoryUsage").innerHTML = `<strong>Memory Usage:</strong> <span style="color:gray;">Not in use</span>`;
                                document.getElementById("ramUsage").innerHTML = `<strong>RAM Usage:</strong> <span style="color:gray;">Not in use</span>`;
                                document.getElementById("cpuUsage").innerHTML = `<strong>CPU Usage:</strong> <span style="color:gray;">Not in use</span>`;
                                document.getElementById("batteryUsage").innerHTML = `<strong>Battery Consumption:</strong> <span style="color:gray;">Not in use</span>`;
                                document.getElementById("securityStatus").innerHTML = `<strong>Security Status:</strong> <span style="color:gray;">No data</span>`;
                            } else {
                                // Display actual results
                                document.getElementById("memoryUsage").innerHTML = `<strong>Memory Usage:</strong> ${result.memory}`;
                                document.getElementById("ramUsage").innerHTML = `<strong>RAM Usage:</strong> ${result.ram}`;
                                document.getElementById("cpuUsage").innerHTML = `<strong>CPU Usage:</strong> ${result.cpu}`;
                                document.getElementById("batteryUsage").innerHTML = `<strong>Battery Consumption:</strong> ${result.battery}`;
                                document.getElementById("securityStatus").innerHTML = `<strong>Security Status:</strong> ${result.security}`;
                            }

                            // Update scanning status to "Scan completed"
                            monitorStatus.innerHTML = `<strong>Scan completed for ${appName}.</strong> Results displayed below.`;
                            monitorResults.style.display = "block";
                        })
                        .catch(error => {
                            console.error("Error fetching results:", error);
                            monitorResults.innerHTML = "<p style='color:red;'>Error fetching monitoring results.</p>";
                            monitorResults.style.display = "block";
                        });
                }, duration);
            })
            .catch(error => {
                console.error("Error:", error);
                monitorStatus.innerHTML = "<p style='color:red;'>Error starting monitoring.</p>";
            });
    });
});
