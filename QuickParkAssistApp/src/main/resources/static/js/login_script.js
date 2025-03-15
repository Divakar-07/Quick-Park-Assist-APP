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

// Form elements
const loginForm = document.getElementById("login-form");
const loginUsername = document.getElementById("login-username");
const loginPassword = document.getElementById("login-password");

const signupForm = document.getElementById("signup-form");
const signupUserType = document.getElementById("signup-userType");
const signupFullName = document.getElementById("signup-fullname");
const signupEmail = document.getElementById("signup-email");
const signupUsername = document.getElementById("signup-username");
const signupPassword1 = document.getElementById("signup-password1");
const signupPassword2 = document.getElementById("signup-password2");

// Signup form event listener
signupForm.addEventListener("submit", (e) => {
  e.preventDefault(); // Prevent form submission before validation

  // Check if all fields are filled
  const fields = [
    signupUserType,
    signupFullName,
    signupEmail,
    signupUsername,
    signupPassword1,
    signupPassword2,
  ];
  if (fields.some((field) => !field.value.trim())) {
    alert("All fields are required");
    return;
  }

  // Check password match
  if (signupPassword1.value !== signupPassword2.value) {
    alert("Passwords do not match");
    return;
  }

  // Send registration data
  fetch("/users/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      fullName: signupFullName.value.trim(),
      email: signupEmail.value.trim(),
      username: signupUsername.value.trim(),
      password: signupPassword1.value,
      userType: signupUserType.value,
    }),
  }).then((response) => {
    if (response.ok) {
      window.location.href = "/dashboard";
    } else {
      alert("Something went wrong");
    }
  });
});

// Login form event listener
loginForm.addEventListener("submit", (e) => {
  e.preventDefault();

  fetch("/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: loginUsername.value.trim(),
      password: loginPassword.value,
    }),
  })
    .then((response) => {
      console.log(response);
      if (response.ok) {
        window.location.href = "/dashboard";
      } else {
        alert("Invalid credentials");
      }
    })
    .catch((error) => {
      alert("invalid credentials" + error);
    });
});
// Function to handle user type selection
function selectUserType(type) {
  // Remove 'active' class from all buttons
  document
    .querySelectorAll(".user-type-btn")
    .forEach((btn) => btn.classList.remove("active"));

  // Add 'active' class to selected button
  document
    .querySelectorAll(`.user-type-btn[data-type="${type}"]`)
    .forEach((btn) => btn.classList.add("active"));

  // Update userType input value
  const userTypeInput = document.getElementById("signup-userType");
  if (userTypeInput) {
    userTypeInput.value = type;
    userTypeInput.setAttribute("value", type); // Reflects in DevTools
  }

  console.log("Selected user type:", userTypeInput.value);
}
