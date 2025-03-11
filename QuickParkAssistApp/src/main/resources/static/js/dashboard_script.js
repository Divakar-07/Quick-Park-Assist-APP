async function getUserDetails() {
  const response = await fetch("/users/getUserDetails");
  if (response.ok) {
    const userDetails = await response.json();
    console.log(userDetails);

    const user = userDetails.user;
    const vehicles = userDetails.vehicles;

    const userContainer = document.getElementById("user-details");

    userContainer.innerHTML = `
        <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label class="block font-medium">First Name:</label>
            <input type="text" value="${
              user.firstName
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Last Name:</label>
            <input type="text" value="${
              user.lastName
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Contact:</label>
            <input type="text" value="${
              user.contactNumber
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Email:</label>
            <input type="text" value="${
              user.email
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Address:</label>
            <input type="text" value="${
              user.address
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Date of Birth:</label>
            <input type="text" value="${
              user.formattedDob
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Registered On:</label>
            <input type="text" value="${
              user.formattedDateOfRegister
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Status:</label>
            <input type="text" value="${
              user.status
            }" class="w-full p-2 border rounded-md bg-gray-100 text-${
      user.status === "ACTIVE" ? "green" : "red"
    }-500" readonly />
          </div>
          <div>
            <label class="block font-medium">User Type:</label>
            <input type="text" value="${user.userType.replace(
              "_",
              " "
            )}" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
        </div>
  
        <div class="mt-6">
          <h2 class="text-xl font-bold mb-3">Vehicles</h2>
          <div id="vehicles-list" class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4"></div>
        </div>
      `;

    const vehiclesList = document.getElementById("vehicles-list");

    vehicles.forEach((vehicle) => {
      const vehicleDiv = document.createElement("div");
      vehicleDiv.className = "bg-gray-50 p-4 rounded-lg shadow-md";

      vehicleDiv.innerHTML = `
          <div>
            <label class="block font-medium">Brand:</label>
            <input type="text" value="${
              vehicle.brand
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Model:</label>
            <input type="text" value="${
              vehicle.model
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Type:</label>
            <input type="text" value="${
              vehicle.vehicleType
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Registration Number:</label>
            <input type="text" value="${
              vehicle.registrationNumber
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Registration Date:</label>
            <input type="text" value="${
              vehicle.registrationDate
            }" class="w-full p-2 border rounded-md bg-gray-100" readonly />
          </div>
          <div>
            <label class="block font-medium">Status:</label>
            <input type="text" value="${
              vehicle.active ? "Active" : "Inactive"
            }" class="w-full p-2 border rounded-md bg-gray-100 text-${
        vehicle.active ? "green" : "red"
      }-500" readonly />
          </div>
        `;

      vehiclesList.appendChild(vehicleDiv);
    });
  } else {
    console.error("Failed to fetch user details");
  }
}

document.addEventListener("DOMContentLoaded", getUserDetails);
