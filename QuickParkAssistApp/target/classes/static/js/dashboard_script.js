async function getUserDetails() {
  const response = await fetch("/users/getUserDetails");
  if (response.ok) {
    const userDetails = await response.json();
    console.log(userDetails);

    const user = userDetails.user;
    const vehicles = userDetails.vehicles;

    const userContainer = document.getElementById("user-details");

    userContainer.innerHTML = `
        <div class="row g-3 text-dark">
          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">First Name:</label>
            <input type="text" value="${
              user.fullName
            }" class="form-control bg-light" readonly />
          </div>

          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">Contact:</label>
            <input type="number" placeholder="Enter your contact details" value="${
              user.contactNumber ? user.contactNumber : ""
            }" class="form-control bg-light" ${
      user.contactNumber ? "" : "readonly"
    } />
          </div>

          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">Email:</label>
            <input type="text" value="${
              user.email
            }" class="form-control bg-light" readonly />
          </div>

          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">Address:</label>
            <input type="text" placeholder="Enter your address" value="${
              user.address ? user.address : ""
            }" class="form-control bg-light" readonly />
          </div>

          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">Date of Birth:</label>
            <input type="date" value="${
              user.formattedDob
            }" class="form-control bg-light" ${
      user.formattedDob ? "" : "readonly"
    } />
          </div>

          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">Registered On:</label>
            <input type="date" value="${
              user.formattedDateOfRegister
            }" class="form-control bg-light" readonly />
          </div>

          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">Status:</label>
            <input type="text" value="${
              user.status
            }" class="form-control bg-light text-${
      user.status === "ACTIVE" ? "success" : "danger"
    }" readonly />
          </div>

          <div class="col-md-6 col-lg-4">
            <label class="form-label fw-medium">User Type:</label>
            <select class="form-select bg-light" readonly>
              <option value="SLOT OWNER" ${
                user.userType?.replace("_", " ") === "SLOT OWNER"
                  ? "selected"
                  : ""
              }>SLOT OWNER</option>
              <option value="VEHICLE OWNER" ${
                user.userType?.replace("_", " ") === "VEHICLE OWNER"
                  ? "selected"
                  : ""
              }>VEHICLE OWNER</option>
            </select>
          </div>
        </div>
  
        <div class="mt-4">
          <h2 class="fs-5 fw-bold mb-3">Vehicles</h2>
          <div id="vehicles-list" class="row g-3"></div>
        </div>
      `;

    const vehiclesList = document.getElementById("vehicles-list");

    vehicles.forEach((vehicle) => {
      const vehicleDiv = document.createElement("div");
      vehicleDiv.className = "col-md-6 col-lg-4";

      vehicleDiv.innerHTML = `
        <div class="bg-light p-3 rounded shadow-sm">
          <div>
            <label class="form-label fw-medium">Brand:</label>
            <input type="text" value="${
              vehicle.brand
            }" class="form-control bg-light" readonly />
          </div>
          <div>
            <label class="form-label fw-medium">Model:</label>
            <input type="text" value="${
              vehicle.model
            }" class="form-control bg-light" readonly />
          </div>
          <div>
            <label class="form-label fw-medium">Type:</label>
            <input type="text" value="${
              vehicle.vehicleType
            }" class="form-control bg-light" readonly />
          </div>
          <div>
            <label class="form-label fw-medium">Registration Number:</label>
            <input type="text" value="${
              vehicle.registrationNumber
            }" class="form-control bg-light" readonly />
          </div>
          <div>
            <label class="form-label fw-medium">Registration Date:</label>
            <input type="text" value="${
              vehicle.registrationDate
            }" class="form-control bg-light" readonly />
          </div>
          <div>
            <label class="form-label fw-medium">Status:</label>
            <input type="text" value="${
              vehicle.active ? "Active" : "Inactive"
            }" class="form-control bg-light text-${
        vehicle.active ? "success" : "danger"
      }" readonly />
          </div>
        </div>
      `;

      vehiclesList.appendChild(vehicleDiv);
    });
  } else {
    console.error("Failed to fetch user details");
  }
}

document.addEventListener("DOMContentLoaded", getUserDetails);
