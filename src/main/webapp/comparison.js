document.addEventListener("DOMContentLoaded", function () {
    fetchAppList();
});

// Fetch apps from backend and populate dropdowns in alphabetical order
function fetchAppList() {
    fetch('http://localhost:8080/Permission_manager/PermissionsServlet')
        .then(response => response.json())
        .then(apps => {
            // Sort apps alphabetically
            apps.sort((a, b) => a.app_name.localeCompare(b.app_name));

            populateDropdown("app-list-1", apps);
            populateDropdown("app-list-2", apps);
        })
        .catch(error => console.error("❌ Error fetching app list:", error));
}

// Populate dropdown with sorted app names
function populateDropdown(dropdownId, apps) {
    const dropdown = document.getElementById(dropdownId);
    dropdown.innerHTML = '<option value="">Select an App</option>';  // Reset dropdown

    apps.forEach(app => {
        let option = document.createElement("option");
        option.value = app.app_name;
        option.textContent = app.app_name;
        dropdown.appendChild(option);
    });
}

// Fetch and display selected app data
function fetchAppData(appName, targetDiv) {
    if (!appName) {
        document.getElementById(targetDiv).innerHTML = "";
        return;
    }

    fetch('http://localhost:8080/Permission_manager/PermissionsServlet')
        .then(response => response.json())
        .then(apps => {
            const appData = apps.find(app => app.app_name === appName);
            if (appData) {
                displayAppInfo(appData, targetDiv);
            } else {
                document.getElementById(targetDiv).innerHTML = "<p>App data not found.</p>";
            }
        })
        .catch(error => console.error("❌ Error fetching app data:", error));
}

// Display app information in card
function displayAppInfo(app, targetDiv) {
    const container = document.getElementById(targetDiv);
    container.innerHTML = `
        <h2>${app.app_name}</h2>
        <p><strong>Storage:</strong> ${app.storage ? 'Yes' : 'No'}</p>
        <p><strong>Camera:</strong> ${app.camera ? 'Yes' : 'No'}</p>
        <p><strong>Location:</strong> ${app.location ? 'Yes' : 'No'}</p>
        <p><strong>Nearby Devices:</strong> ${app.nearby_devices ? 'Yes' : 'No'}</p>
        <p><strong>Contacts:</strong> ${app.contacts ? 'Yes' : 'No'}</p>
        <p><strong>Microphone:</strong> ${app.microphone ? 'Yes' : 'No'}</p>
    `;
}
