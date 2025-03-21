function fetchApps() {
    fetch('http://localhost:8080/Permission_manager/PermissionsServlet')  // Adjust this URL if needed
        .then(response => response.json())
        .then(data => {
            console.log("✅ Data fetched successfully:", data);
            displayApps(data);
        })
        .catch(error => console.error('❌ Error fetching app data:', error));
}

function displayApps(apps) {
    const resultsDiv = document.getElementById('results');
    
    if (!resultsDiv) {
        console.error("❌ Error: 'results' div not found in the HTML!");
        return;
    }

    resultsDiv.innerHTML = ''; // Clear previous content

    if (apps.length === 0) {
        resultsDiv.innerHTML = '<p>No apps found.</p>';
        return;
    }

    apps.forEach(app => {
        const appCard = document.createElement('div');
        appCard.className = 'app-card'; // Apply CSS class for styling

        appCard.innerHTML = `
            <h2 class="app-name">${app.app_name}</h2>
            <p><strong>Storage:</strong> ${app.storage ? 'Yes' : 'No'}</p>
            <p><strong>Camera:</strong> ${app.camera ? 'Yes' : 'No'}</p>
            <p><strong>Location:</strong> ${app.location ? 'Yes' : 'No'}</p>
            <p><strong>Nearby Devices:</strong> ${app.nearby_devices ? 'Yes' : 'No'}</p>
            <p><strong>Contacts:</strong> ${app.contacts ? 'Yes' : 'No'}</p>
            <p><strong>Microphone:</strong> ${app.microphone ? 'Yes' : 'No'}</p>
        `;

        resultsDiv.appendChild(appCard);
    });
}

// 🔎 Function to filter apps based on search input
function searchApps() {
    const query = document.getElementById("searchBar").value.toLowerCase();
    const appCards = document.querySelectorAll(".app-card");

    appCards.forEach(card => {
        const appName = card.querySelector(".app-name").innerText.toLowerCase();
        if (appName.includes(query)) {
            card.style.display = "block";
        } else {
            card.style.display = "none";
        }
    });
}

// Fetch apps when the page loads
window.onload = fetchApps;
