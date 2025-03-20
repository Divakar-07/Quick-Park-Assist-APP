async function loadSpots() {
  const response = await fetch("/spot/all");
  if (response.ok) {
    const spots = await response.json();
    console.log(spots);
    const container = document.getElementById("spots-container");
    container.innerHTML = ""; // Clear previous data

    spots.forEach((spot) => {
      const spotDiv = document.createElement("div");
      spotDiv.className = "bg-white p-4 rounded-lg shadow-md";
      spotDiv.innerHTML = `
                <h2 class="text-xl font-semibold mb-2">${spot.spotNumber} - ${
        spot.spotType
      }</h2>
                <p class="text-gray-700"><strong>Location:</strong> ${
                  spot.location.buildingName
                }, ${spot.location.streetAddress}</p>
                <p class="text-gray-700"><strong>Status:</strong> ${
                  spot.status
                }</p>
                <p class="text-gray-700"><strong>Price:</strong> $${
                  spot.price
                } (${spot.priceType})</p>
                <p class="text-gray-700"><strong>Rating:</strong> ⭐${
                  spot.rating
                }</p>
                <p class="text-gray-700"><strong>EV Charging:</strong> ${
                  spot.hasEVCharging ? "Yes" : "No"
                }</p>
                <p class="text-gray-700"><strong>Supported Vehicles:</strong> ${spot.supportedVehicleTypes.join(
                  ", "
                )}</p>
            `;
      container.appendChild(spotDiv);
    });
  } else {
    console.error("Failed to load spots");
  }
}

document.addEventListener("DOMContentLoaded", loadSpots);