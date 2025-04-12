function togglePassword(id, icon) {
  const input = document.getElementById(id);
  if (input.type === "password") {
    input.type = "text";
    icon.innerText = "visibility_off";
  } else {
    input.type = "password";
    icon.innerText = "visibility";
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const overlay = document.getElementById("overlay-container");
  const overlayText = document.getElementById("overlay-text");
  const overlayBtn = document.getElementById("overlay-btn");
  overlayBtn.onclick = () => {
    overlay.classList.toggle("-translate-x-full");
    document
      .querySelectorAll("#overlay-content")
      .forEach((ele) => ele.classList.toggle("hidden"));
    if (overlayText.innerText === "Already have an account?") {
      overlayText.innerText = "Don't have an account?";
    } else {
      overlayText.innerText = "Already have an account?";
    }
    if (overlayBtn.innerText === "Sign In") {
      overlayBtn.innerText = "Sign up";
    } else {
      overlayBtn.innerText = "Sign In";
    }
  };

  // Initially hide the login form
});

document
  .getElementById("submitBtn")
  .addEventListener("click", function () {
    document.getElementById("btnText").textContent = "Registering...";
    document.getElementById("loader").classList.remove("hidden");


  });

document.getElementById("sign-in-link").onclick = () => {
  document.getElementById("sign-up").classList.add("max-lg:hidden");
  document.getElementById("sign-in").classList.remove("max-lg:hidden");
};

document.getElementById("sign-up-link").onclick = () => {
  document.getElementById("sign-up").classList.remove("max-lg:hidden");
  document.getElementById("sign-in").classList.add("max-lg:hidden");
};
