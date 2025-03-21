document.addEventListener("DOMContentLoaded", function () {
    console.log("Loading navbar...");
    
    // Load Navbar dynamically
    fetch("navbar.html")
        .then(response => {
            if (!response.ok) {
                throw new Error("Navbar file not found");
            }
            return response.text();
        })
        .then(data => {
            document.getElementById("navbar-container").innerHTML = data;
            console.log("Navbar loaded successfully!");
        })
        .catch(error => console.error("Error loading navbar:", error));

    // Smooth Entry Animation for Option Cards
    const cards = document.querySelectorAll(".option-card");
    
    cards.forEach((card, index) => {
        card.style.opacity = "0";
        card.style.transform = "translateY(20px)";
        
        setTimeout(() => {
            card.style.transition = "opacity 0.8s ease-out, transform 0.8s ease-out";
            card.style.opacity = "1";
            card.style.transform = "translateY(0)";
        }, index * 150); // Delay each card appearance
    });

    // Hover Effect: Slight Bounce
    cards.forEach(card => {
        card.addEventListener("mouseover", () => {
            card.style.transform = "scale(1.05)";
            card.style.transition = "transform 0.3s ease-in-out";
        });

        card.addEventListener("mouseout", () => {
            card.style.transform = "scale(1)";
        });
    });

    // Navbar Fade-In Animation
    const navbar = document.querySelector(".floating-navbar");
    if (navbar) {
        navbar.style.opacity = "0";
        navbar.style.transition = "opacity 0.8s ease-in-out";
        
        setTimeout(() => {
            navbar.style.opacity = "1";
        }, 500); // Fade in after 0.5 seconds
    }
});
