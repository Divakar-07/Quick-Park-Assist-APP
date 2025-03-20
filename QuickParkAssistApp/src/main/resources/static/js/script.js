import toast from "./Toast.js";
import axios from 'https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js';

const logoutBtn = document.getElementById("logout");
const fileInput = document.getElementById("file-input-js");
const profileImage = document.getElementById("profile-image-js");

logoutBtn.addEventListener("click", async (e) => {
  e.preventDefault();
  try {
    await axios.post("/api/auth/logout");
    window.location.href = "/login";
  } catch (error) {
    alert("Logout failed");
    console.error(error);
  }
});

function scrollToSlide(slideIndex) {
  const testimonialScroll = document.querySelector(".testimonial-scroll");
  const slideWidth = document.querySelector(".testimonial-slide").clientWidth;
  testimonialScroll.style.transform = `translateX(-${slideWidth * slideIndex}px)`;

  // Update indicators
  const dots = document.querySelectorAll(".dot");
  dots.forEach((dot, index) => {
    dot.classList.toggle("active", index === slideIndex);
  });
}

window.onload = async () => {
  try {
    const response = await axios.get("/api/users/");
    const userProfile = response.data;
    console.log(userProfile);
    document.getElementById("username-dashboard-js").innerText = userProfile.fullName;
    if (userProfile.imageUrl) profileImage.src = userProfile.imageUrl;
  } catch (error) {
    console.error("Error fetching user data:", error);
  }
};

fileInput.onchange = async (e) => {
  const file = e.target.files[0];
  if (file) {
    const imageUrl = URL.createObjectURL(file);
    profileImage.src = imageUrl;

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await axios.post("/api/users/image/upload", formData);
      toast.success("Image uploaded successfully");
      console.log("Image uploaded successfully:", response);
    } catch (error) {
      toast.error("Error uploading image");
      console.error("Error uploading image:", error);
    }
  }
};
