const navContainer = document.getElementById("nav-links-js");

const navLinks = [
    { text: "Home", link: "/" },
    { text: "Add Vehicle", link: "/vehicles/new", accessible: "VEHICLE_OWNER" },
    { text: "Add Spot", link: "/spots/new", accessible: "SPOT_OWNER" },
    { text: "My Spots", link: "/spots/user", accessible: "SPOT_OWNER" },
    { text: "All Spots", link: "/spots/all" },
    { text: "Contact", link: "/contact" },
    { text: "Profile", link: "/user/profile" },
    { text: "Logout" } // No link needed for logout
];

async function fetchUser(navLinks) {
    try {
        const response = await fetch("/users/", { method: "GET" });
        if (response.ok) {
            const userProfile = await response.json(); // Correct JSON parsing
            addNavLinks(navLinks, userProfile.userType);
        } else {
            console.error("Failed to fetch user profile");
        }
    } catch (error) {
        console.error("Error fetching user data:", error);
    }
}

function addNavLinks(navLinks, userType) {
    navContainer.innerHTML = ""; // Clear previous items before adding new ones

    navLinks.forEach(item => {
        const li = document.createElement("li");

        if (item.text === "Logout") {
            // Create logout button and assign ID
            const button = document.createElement("button");
            button.innerText = item.text;
            button.id = "logout"; // Assign ID to make it selectable
            button.classList.add("text-white", "border", "bg-blue-500", "px-3", "py-1", "rounded-md");
            li.appendChild(button);
            navContainer.appendChild(li);

            // Attach event listener **after** button is created
            button.addEventListener("click", (e) => {
                e.preventDefault();
                fetch("/auth/logout", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({}),
                }).then((response) => {
                    window.location.href = "/login";
                }).catch((error) => {
                    alert("Logout failed");
                    console.error(error);
                });
            });
        } else if (!item.accessible || item.accessible === userType) {
            const a = document.createElement("a");
            a.innerText = item.text;
            a.href = item.link; // Add href to the anchor tag
            li.appendChild(a);
            navContainer.appendChild(li);
        }
    });
}

window.onload = () => fetchUser(navLinks); // Corrected onload function
