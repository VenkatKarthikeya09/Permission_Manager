document.addEventListener("DOMContentLoaded", function () {
    console.log("Step 1: Starting background.js");

    document.getElementById("scanButton").addEventListener("click", function () {
        console.log("Step 2: Scan Button Clicked");

        fetch("http://localhost:8080/Permission_manager/BackgroundServlet")
            .then(response => response.text())  // Expecting HTML response
            .then(data => {
                console.log("Step 3: Response received", data);
                document.getElementById("activities").innerHTML = data;  // Directly update with HTML
            })
            .catch(error => {
                console.error("Step 7: Error in fetch operation", error);
                document.getElementById("activities").innerHTML = "Error retrieving running applications.";
            });
    });
});
