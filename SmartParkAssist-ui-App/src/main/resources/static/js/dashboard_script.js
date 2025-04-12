document.addEventListener("DOMContentLoaded", function () {
  // Get references to necessary DOM elements
  const editBtn = document.getElementById("edit-btn");
  const cancelBtn = document.getElementById("cancel-btn");
  const actionButtons = document.getElementById("action-buttons");
  const editableFields = document.querySelectorAll(".editable");

  /**
   * Enables editing mode by removing the readonly attribute
   * and applying a border highlight.
   */
  function enableEditing() {
    editableFields.forEach((field) => {
      field.dataset.originalValue = field.value; // Store the original value before editing
      field.removeAttribute("readonly"); // Make field editable
      field.classList.add("border-primary"); // Add styling to indicate edit mode
    });

    actionButtons.classList.remove("d-none"); // Show the action buttons (Save, Cancel)
    editBtn.classList.add("d-none"); // Hide the edit button
  }

  /**
   * Disables editing mode by restoring the readonly attribute,
   * removing the border highlight, and optionally resetting the values.
   * @param {boolean} reset - If true, restores the original values.
   */
  function disableEditing(reset = false) {
    editableFields.forEach((field) => {
      field.setAttribute("readonly", true); // Make field non-editable again
      field.classList.remove("border-primary"); // Remove edit styling
      if (reset) field.value = field.dataset.originalValue; // Restore original value if reset is true
    });

    actionButtons.classList.add("d-none"); // Hide action buttons
    editBtn.classList.remove("d-none"); // Show edit button again
  }

  // Attach event listeners
  editBtn.addEventListener("click", enableEditing);
  cancelBtn.addEventListener("click", () => disableEditing(true)); // Reset values on cancel
});
