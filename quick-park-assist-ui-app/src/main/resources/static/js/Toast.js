class Toast {
  constructor() {
    // Create a container to hold the toasts
    this.toastContainer = document.createElement("div");
    this.toastContainer.style.position = "fixed";
    this.toastContainer.style.top = "20px"; // Positioning at the top
    this.toastContainer.style.right = "20px"; // Positioning at the right
    this.toastContainer.style.zIndex = "9999"; // Ensure it appears above other elements
    this.toastContainer.style.display = "flex";
    this.toastContainer.style.flexDirection = "column"; // Stack toasts vertically
    this.toastContainer.style.gap = "10px"; // Space between toasts
    document.body.appendChild(this.toastContainer); // Add container to the DOM
  }

  /**
   * Displays a toast message with animation.
   * @param {string} message - The message to display.
   * @param {string} type - The type of message ('success', 'error', or 'info').
   */
  showToast(message, type) {
    // Create the toast element
    const toast = document.createElement("div");
    toast.classList.add("toast-message", type);
    toast.innerText = message;

    // Apply dynamic styles to each toast
    toast.style.padding = "10px 15px";
    toast.style.borderRadius = "5px";
    toast.style.color = "#fff";
    toast.style.fontSize = "14px";
    toast.style.boxShadow = "0 4px 6px rgba(0,0,0,0.1)";
    toast.style.display = "flex";
    toast.style.alignItems = "center";
    toast.style.justifyContent = "space-between";
    toast.style.opacity = "0"; // Initially hidden
    toast.style.transition = "opacity 0.3s ease-in-out, transform 0.3s ease-in-out";
    toast.style.transform = "translateX(100%)"; // Slide in animation

    // Set background color based on message type
    switch (type) {
      case "success":
        toast.style.backgroundColor = "#28a745"; // Green for success
        break;
      case "error":
        toast.style.backgroundColor = "#dc3545"; // Red for error
        break;
      default:
        toast.style.backgroundColor = "#007bff"; // Blue for default/info
        break;
    }

    // Append toast to the container
    this.toastContainer.appendChild(toast);

    // Animate toast in
    setTimeout(() => {
      toast.style.opacity = "1";
      toast.style.transform = "translateX(0)";
    }, 50);

    // Auto-remove the toast after 3 seconds
    setTimeout(() => {
      toast.style.opacity = "0";
      toast.style.transform = "translateX(100%)";
      setTimeout(() => toast.remove(), 300); // Remove from DOM after animation
    }, 3000);
  }

  /**
   * Show a success toast.
   * @param {string} message - Success message.
   */
  success(message) {
    this.showToast(message, "success");
  }

  /**
   * Show an error toast.
   * @param {string} message - Error message.
   */
  error(message) {
    this.showToast(message, "error");
  }

  /**
   * Show an informational toast.
   * @param {string} message - Info message.
   */
  notify(message) {
    this.showToast(message, "info");
  }
}

// Instantiate the Toast class globally
const toast = new Toast();
window.toast = toast; // Make it accessible globally
