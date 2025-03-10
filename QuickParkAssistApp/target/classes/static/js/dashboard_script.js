async function getUserDetails() {
    const response = await fetch("/users/getUserDetails");
    if (response.ok) {
        const userDetails = await response.json();
        console.log(userDetails);

        const user = userDetails.user;
        const vehicles = userDetails.vehicles;

        const userContainer = document.getElementById("user-details");

        userContainer.innerHTML = `
            <p><strong>Name:</strong> ${user.firstName} ${user.lastName}</p>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Contact:</strong> ${user.contactNumber}</p>
            <p><strong>Address:</strong> ${user.address}</p>
            <p><strong>Date of Birth:</strong> ${user.formattedDob}</p>
            <p><strong>Registered On:</strong> ${user.formattedDateOfRegister}</p>
            <p><strong>Status:</strong> <span class="text-${user.status === 'ACTIVE' ? 'green' : 'red'}-500">${user.status}</span></p>
            <p><strong>User Type:</strong> ${user.userType.replace("_", " ")}</p>
        `;

        // Create a container for the vehicles
        const vehiclesContainer = document.createElement("div");
        vehiclesContainer.className = "mt-6";

        vehiclesContainer.innerHTML = `
            <h2 class="text-xl font-bold mb-3">Vehicles</h2>
            <div id="vehicles-list" class="space-y-4"></div>
        `;

        userContainer.appendChild(vehiclesContainer);

        const vehiclesList = document.getElementById("vehicles-list");

        vehicles.forEach(vehicle => {
            const vehicleDiv = document.createElement("div");
            vehicleDiv.className = "bg-gray-50 p-4 rounded-lg shadow";

            vehicleDiv.innerHTML = `
                <p><strong>Brand:</strong> ${vehicle.brand}</p>
                <p><strong>Model:</strong> ${vehicle.model}</p>
                <p><strong>Type:</strong> ${vehicle.vehicleType}</p>
                <p><strong>Registration Number:</strong> ${vehicle.registrationNumber}</p>
                <p><strong>Registration Date:</strong> ${vehicle.registrationDate}</p>
                <p><strong>Status:</strong> <span class="text-${vehicle.active ? 'green' : 'red'}-500">${vehicle.active ? 'Active' : 'Inactive'}</span></p>
            `;

            vehiclesList.appendChild(vehicleDiv);
        });
    } else {
        console.error("Failed to fetch user details");
    }
}

document.addEventListener("DOMContentLoaded", getUserDetails);
