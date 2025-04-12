const fileInput = document.getElementById("file-input-js");
const profileImage = document.getElementById("profile-image-js");

fileInput.onchange = async (e) => {
  const file = e.target.files[0];
  if (file) {
    const imageUrl = URL.createObjectURL(file);
    profileImage.src = imageUrl;

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("/user/avatar", {
        method: "POST",
        body: formData,
      });

      const data = await response.json();
      if (data.success) {
        console.log(data);
        triggerChange(document.getElementById("success-message"), data.message);
      } else {
        triggerChange(document.getElementById("error-message"), data.message);
      }
    } catch (error) {
      triggerChange(document.getElementById("success-message", "error uploading image"));
    }
  }
};
