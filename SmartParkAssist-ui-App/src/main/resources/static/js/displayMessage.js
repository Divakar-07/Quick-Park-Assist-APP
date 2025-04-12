document.addEventListener("DOMContentLoaded", function () {
  const notyf = new Notyf();

  // Get the elements
  const successMessageElement = document.getElementById("success-message");
  const errorMessageElement = document.getElementById("error-message");

  if (!successMessageElement || !errorMessageElement) {
    console.error("One or both elements are missing!");
    return;
  }
  if (successMessageElement.value) {
    notyf.success(successMessageElement.value);
  }

  if (errorMessageElement.value) {
    notyf.error(errorMessageElement.value);
  }
  // Function to trigger change event manually

  // Event listeners for changes
  successMessageElement.addEventListener("input", (e) => {
    console.log("inside the success-message input event");
    if (e.target.value) {
      notyf.success(e.target.value);
    }
  });

  errorMessageElement.addEventListener("input", (e) => {
    if (e.target.value) {
      notyf.error(e.target.value);
    }
  });
});

function triggerChange(element, value) {
  element.value = value;
  element.dispatchEvent(new Event("input", { bubbles: true })); // Use "input" instead of "change"
}
