import toast from "../Toast.js";

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
  fetch("/api/users/register", {
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
loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  try {
    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: loginUsername.value.trim(),
        password: loginPassword.value,
      }),
    });
    const data = await response.json();
    console.log(data);

    if (response.ok) {
      toast.success("login successful");
      // window.location.href = "/dashboard";
    } else {
      toast.error("Invalid credentials ", response.text);
    }
  } catch (error) {
    console.log(error);
    toast.error("Internal server error");
  }
});

// Function to handle user type selection
