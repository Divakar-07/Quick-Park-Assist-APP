// Login page: Sign-in and Sign-up handling
let state = localStorage.getItem("state") || "signin";

function toggleForm() {
  state = state === "signin" ? "signup" : "signin";
  localStorage.setItem("state", state);
}

function setState() {
  const container = document.getElementById("container");
  if (state === "signin") {
    container.classList.remove("active"); // Fixed incorrect class toggling
  } else {
    container.classList.add("active");
  }
}

// Mobile-specific toggle function
function setStateMobile() {
  const signin = document.querySelector(".signin");
  const signup = document.querySelector(".signup");

  if (signin && signup) {
    signin.style.display = state === "signin" ? "block" : "none";
    signup.style.display = state === "signup" ? "block" : "none";
  }
}

// Call toggle function based on screen size when page loads
function handleScreenToggle() {
  if (window.innerWidth > 768) {
    toggleForm();
    setState(); // Large screens
  } else {
    toggleForm();
    setStateMobile(); // Small screens
  }
}

function handleScreenToggleRefresh() {
  if (window.innerWidth > 768) {
    setState(); // Large screens
  } else {
    setStateMobile(); // Small screens
  }
}

// Run on page load
window.addEventListener("DOMContentLoaded", handleScreenToggleRefresh);

// Re-run when window resizes (optional)
window.addEventListener("resize", handleScreenToggleRefresh);

