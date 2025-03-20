class Toast {
    constructor() {
      // Create a container to hold the toasts
      this.toastContainer = document.createElement("div");
      this.toastContainer.style.position = "fixed";
      this.toastContainer.style.top = "20px";
      this.toastContainer.style.right = "20px";
      this.toastContainer.style.zIndex = "9999";
      this.toastContainer.style.display = "flex";
      this.toastContainer.style.flexDirection = "column";
      this.toastContainer.style.gap = "10px";
      document.body.appendChild(this.toastContainer);
    }
  
    showToast(message, type) {
      const toast = document.createElement("div");
      toast.classList.add("toast-message", type);
      toast.innerText = message;
  
      // Style each toast dynamically
      toast.style.padding = "10px 15px";
      toast.style.borderRadius = "5px";
      toast.style.color = "#fff";
      toast.style.fontSize = "14px";
      toast.style.boxShadow = "0 4px 6px rgba(0,0,0,0.1)";
      toast.style.display = "flex";
      toast.style.alignItems = "center";
      toast.style.justifyContent = "space-between";
      toast.style.opacity = "0";
      toast.style.transition = "opacity 0.3s ease-in-out, transform 0.3s ease-in-out";
      toast.style.transform = "translateX(100%)";
  
      // Set colors based on type
      switch (type) {
        case "success":
          toast.style.backgroundColor = "#28a745";
          break;
        case "error":
          toast.style.backgroundColor = "#dc3545";
          break;
        default:
          toast.style.backgroundColor = "#007bff"; // Default (info)
          break;
      }
  
      // Append toast to container
      this.toastContainer.appendChild(toast);
  
      // Animate in
      setTimeout(() => {
        toast.style.opacity = "1";
        toast.style.transform = "translateX(0)";
      }, 50);
  
      // Auto-remove after 3 seconds
      setTimeout(() => {
        toast.style.opacity = "0";
        toast.style.transform = "translateX(100%)";
        setTimeout(() => toast.remove(), 300); // Remove from DOM after animation
      }, 3000);
    }
  
    success(message) {
      this.showToast(message, "success");
    }
  
    error(message) {
      this.showToast(message, "error");
    }
  
    notify(message) {
      this.showToast(message, "info");
    }
  }
  
  // Create an instance
  export default new Toast();
  
  