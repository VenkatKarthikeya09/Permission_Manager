document.addEventListener("DOMContentLoaded", function () {
    const appSelect = document.getElementById("appSelect");
    const estimatedTime = document.getElementById("estimatedTime");
    const checkBtn = document.getElementById("checkBtn");
    const scanStatus = document.getElementById("scanStatus");
    const scanResults = document.getElementById("scanResults");
    const vulnerableAppsList = document.getElementById("vulnerableApps");

    // Fetch Running Applications
    function loadRunningApps() {
        fetch("DLeakServlet?action=getApps")
            .then(response => response.json())
            .then(data => {
                appSelect.innerHTML = '<option value="">-- Select Running App --</option>'; // Reset dropdown
                if (data.apps.length === 0) {
                    appSelect.innerHTML = '<option value="">No running apps detected</option>';
                }
                data.apps.forEach(app => {
                    let option = document.createElement("option");
                    option.value = app.name;
                    option.textContent = app.name;
                    option.dataset.estimatedTime = app.estimatedTime;
                    appSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error("Error fetching running apps:", error);
                appSelect.innerHTML = '<option value="">Error loading apps</option>';
            });
    }

    // Load running apps on page load
    loadRunningApps();

    // Check Application
    checkBtn.addEventListener("click", function () {
        let selectedApp = appSelect.value;
        if (!selectedApp) {
            alert("Please select an application!");
            return;
        }

        let estimatedScanTime = parseInt(appSelect.options[appSelect.selectedIndex].dataset.estimatedTime);
        estimatedTime.innerText = `${estimatedScanTime} seconds`;
        scanStatus.innerText = `Scanning ${selectedApp}, estimated time: ${estimatedScanTime} seconds...`;
        scanResults.style.display = "none";

        fetch(`DLeakServlet?action=scan&app=${encodeURIComponent(selectedApp)}`)
            .then(response => response.json())
            .then(data => {
                setTimeout(() => {
                    vulnerableAppsList.innerHTML = "";
                    if (data.vulnerable) {
                        let li = document.createElement("li");
                        li.innerHTML = `<strong style="color: red;">${selectedApp} is vulnerable:</strong> ${data.reason}`;
                        vulnerableAppsList.appendChild(li);
                    } else {
                        let li = document.createElement("li");
                        li.innerHTML = `<strong style="color: green;">${selectedApp} is safe. No data leak detected.</strong>`;
                        vulnerableAppsList.appendChild(li);
                    }
                    scanStatus.innerText = "Scan Completed!";
                    scanResults.style.display = "block";
                }, estimatedScanTime * 1000);
            })
            .catch(error => {
                console.error("Error:", error);
                scanStatus.innerText = "Error during scanning.";
            });
    });
});
