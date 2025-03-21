document.getElementById("scanButton").addEventListener("click", function () {
    document.getElementById("scanResult").innerHTML = "Scanning... Please wait.";

    fetch("UninstallServlet", { method: "GET" })
        .then(response => response.json())
        .then(data => {
            let resultContainer = document.getElementById("scanResult");
            resultContainer.innerHTML = "";

            if (data.untrustedApps.length > 0) {
                let list = document.createElement("ul");
                data.untrustedApps.forEach(app => {
                    let listItem = document.createElement("li");
                    listItem.innerHTML = `<strong>${app.name}</strong> - ${app.reason}`;
                    list.appendChild(listItem);
                });
                resultContainer.appendChild(list);
            } else {
                resultContainer.innerHTML = "<p>You are safe! No harmful apps found.</p>";
            }
        })
        .catch(error => {
            document.getElementById("scanResult").innerHTML = "Error scanning for untrusted apps.";
            console.error("Error:", error);
        });
});
