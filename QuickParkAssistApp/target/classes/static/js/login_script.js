const loginForm = document.getElementById("login-form");
const loginUsername = document.getElementById("login-username");
const loginPassword = document.getElementById("login-password");

const signupForm = document.getElementById("signup-form");
const signupFullName = document.getElementById("signup-fullname");
const signupEmail = document.getElementById("signup-email");
const signupUsername = document.getElementById("signup-username");
const signupPassword1 = document.getElementById("signup-password1");
const signupPassword2 = document.getElementById("signup-password2");

signupForm.addEventListener("submit", (e) => {
  
  [signupFullName, signupEmail, signupUsername, signupPassword1, signupPassword2].forEach(item => {
    if (!item){
      alert("all fields are required");
      return;
    }
  })
  
  if(signupPassword1.value !== signupPassword2.value){
    alert("password mismatch");
    return;
  }
  e.preventDefault();

  fetch("/users/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      fullName: signupFullName.value,
      email:signupEmail.value,
      username: signupUsername.value,
      password: signupEmail.value,
    }),
  }).then((response) => {
    if (response.ok) {
      window.location.href = "/dashboard";
    } else {
      alert("something went wrong");
    }
  });
})


loginForm.addEventListener("submit", (e) => {
  e.preventDefault();
  fetch("/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: loginUsername.value,
      password: loginPassword.value,
    }),
  }).then((response) => {
    if (response.ok) {
      window.location.href = "/dashboard";
    } else {
      alert("Invalid credentials");
    }
  });
});
