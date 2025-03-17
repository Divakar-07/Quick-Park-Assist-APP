let userData = {};
let showEdit = false;

const toggleShowEdit = () => {
  showEdit = !showEdit;
  updateEditableFields();
};

// Function to attach event listeners to input fields
const attachInputListeners = () => {
  document
    .querySelectorAll("#user-details input, #user-details select")
    .forEach((input) => {
      input.addEventListener("input", handleInputChange);
    });
};

// Handles user input changes and updates userData
const handleInputChange = (e) => {
  const { id, value } = e.target;
  userData[id] = value;
};

async function getUserDetails() {
  try {
    const response = await fetch("/users/getUserDetails");
    if (!response.ok) throw new Error("Failed to fetch user details");

    const { user, vehicles } = await response.json();
    userData = { ...user };

    const userContainer = document.getElementById("user-details");
    userContainer.innerHTML = `
      <div class="w-full d-flex justify-content-end items-center mb-4">     
        <button type="button" id="edit-btn" class="btn btn-primary">
          <i class="fas fa-edit"></i> Edit
        </button>
      </div>
      <input type="hidden" id="userId-js" value="${user.userId}" />

      <div class="row g-3 text-dark">
        ${createUserField("First Name", "fullName", user.fullName, "text")}
        ${createUserField(
          "Contact",
          "contactNumber",
          user.contactNumber || "",
          "number"
        )}
        ${createUserField("Email", "email", user.email, "text", "", true)}
        ${createUserField("Address", "address", user.address || "", "text")}
        ${createUserField("Date of Birth", "dob", user.formattedDob, "date")}
        ${createUserField(
          "Registered On",
          "registeredOn",
          user.formattedDateOfRegister,
          "date",
          "",
          true
        )}
        ${createUserField(
          "Status",
          "status",
          user.status,
          "text",
          "",
          true,
          user.status === "ACTIVE" ? "success" : "danger"
        )}

        <div class="col-md-6 col-lg-4">
          <label class="form-label fw-medium text-dark d-block">User Type:</label>
          <select class="form-select bg-light text-dark" id="userType" disabled>
            <option value="SLOT_OWNER" ${
              user.userType === "SLOT_OWNER" ? "selected" : ""
            }>SLOT OWNER</option>
            <option value="VEHICLE_OWNER" ${
              user.userType === "VEHICLE_OWNER" ? "selected" : ""
            }>VEHICLE OWNER</option>
          </select>
        </div>
      </div>

      
      <div id="edit-actions" class="w-full justify-content-end items-center gap-2 mb-4" style="display: none;">     
      <button class="btn btn-primary" type="submit">Save</button>
      <button type="reset" class="btn btn-secondary" onclick="toggleShowEdit()">Cancel</button>
      </div>
      
      
      <div class="mt-4">
        <h2 class="fs-5 fw-bold mb-3 text-black">Vehicles</h2>
        <div id="vehicles-list" class="w-100"></div>
      </div>
    `;

    document
      .getElementById("edit-btn")
      .addEventListener("click", toggleShowEdit);
    document
      .getElementById("user-details")
      .addEventListener("submit", handleFormSubmit);

    renderVehicles(vehicles);
    attachInputListeners();
    updateEditableFields(); // Ensure fields start as read-only
  } catch (error) {
    console.error(error.message);
  }
}

// Function to create input fields with correct attributes
const createUserField = (
  label,
  id,
  value,
  type,
  placeholder = "",
  readonly = true,
  textColor = ""
) => `
  <div class="col-md-6 col-lg-4">
    <label class="form-label fw-medium text-dark d-block">${label}:</label>
    <input type="${type}" id="${id}" value="${value}" class="form-control bg-light text-dark ${
  textColor ? `text-${textColor}` : ""
}" ${readonly ? "readonly" : ""} ${
  placeholder ? `placeholder='${placeholder}'` : ""
} />
  </div>
`;

const createVehicleField = (
  label,
  id,
  value,
  type,
  placeholder = "",
  readonly = true,
  textColor = ""
) => `
  <div class="col-md-6 col-lg-4">
    <label class="form-label fw-medium text-dark d-block">${label}:</label>
    <input type="${type}" id="${id}" value="${value}" class="form-control bg-light text-dark ${
  textColor ? `text-${textColor}` : ""
}"  "readonly" ${placeholder ? `placeholder='${placeholder}'` : ""} />
  </div>
`;

// Function to create input fields with correct attributes

const renderVehicles = (vehicles) => {
  const vehiclesList = document.getElementById("vehicles-list");
  vehiclesList.innerHTML = vehicles
    .map(
      (vehicle) => `
    <div class="bg-light p-3 rounded shadow-sm mb-3">
      <div class="w-full d-flex justify-content-end items-center mb-3">     
        <a href="/vehicles/edit/${vehicle.vehicleId}" class="btn btn-primary">
          <i class="fas fa-edit"></i> Edit
        </a>
      </div>
      <div class="row g-3 text-dark">
        ${createVehicleField(
          "Brand",
          `brand-${vehicle.registrationNumber}`,
          vehicle.brand,
          "text"
        )}
        ${createVehicleField(
          "Model",
          `model-${vehicle.registrationNumber}`,
          vehicle.model,
          "text"
        )}
        ${createVehicleField(
          "Type",
          `type-${vehicle.registrationNumber}`,
          vehicle.vehicleType,
          "text"
        )}
        ${createVehicleField(
          "Registration Number",
          `registration-${vehicle.registrationNumber}`,
          vehicle.registrationNumber,
          "text"
        )}
        ${createVehicleField(
          "Registration Date",
          `registrationDate-${vehicle.registrationNumber}`,
          vehicle.registrationDate,
          "date"
        )}
        ${createVehicleField(
          "Status",
          `status-${vehicle.registrationNumber}`,
          vehicle.active ? "Active" : "Inactive",
          "text",
          "",
          true,
          vehicle.active ? "success" : "danger"
        )}
      </div>
    </div>
  `
    )
    .join("");
};

const handleFormSubmit = async (e) => {
  e.preventDefault();
  const userId = Number(document.getElementById("userId-js").value);

  try {
    const response = await fetch(`/users/update/${userId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userData),
    });

    if (response.ok) {
      toast.success("Profile updated successfully");
      toggleShowEdit();
    } else {
      alert("Failed to update user details.");
    }
  } catch (error) {
    alert("Error updating details: " + error.message);
  }
};

// Function to toggle input fields between editable and read-only
function updateEditableFields() {
  document
    .querySelectorAll("#user-details input, #user-details select")
    .forEach((input) => {
      if (!["email", "status", "registeredOn"].includes(input.id)) {
        input.toggleAttribute("readonly", !showEdit);
        input.toggleAttribute("disabled", !showEdit);
        input.classList.toggle("bg-light", !showEdit);
      }
    });

  document.getElementById("edit-actions").style.display = showEdit
    ? "flex"
    : "none";
}

document.addEventListener("DOMContentLoaded", getUserDetails);
