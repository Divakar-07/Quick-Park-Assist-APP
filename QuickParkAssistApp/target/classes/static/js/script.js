
const logoutBtn = document.getElementById("logout");

logoutBtn.addEventListener("click", (e) => {
    e.preventDefault();
    fetch("/auth/logout", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({}),
    }).then((response) => {
        window.location.href = "/login";
    }).catch((error) => {
        alert("Logout failed");
        console.error(error);
    });
});

function scrollToSlide(slideIndex) {
    const testimonialScroll = document.querySelector('.testimonial-scroll');
    const slideWidth = document.querySelector('.testimonial-slide').clientWidth;
    testimonialScroll.style.transform = `translateX(-${slideWidth * slideIndex}px)`;

    // Update indicators
    const dots = document.querySelectorAll('.dot');
    dots.forEach((dot, index) => {
        dot.classList.toggle('active', index === slideIndex);
    });
}

// login page signin and sighnup
function toggleForm() {
    const container = document.getElementById('container');
    container.classList.toggle('active');
}

// Add this mobile-specific toggle function
function mobileToggleForm() {
    const signin = document.querySelector('.signin');
    const signup = document.querySelector('.signup');
    
    if(signin.style.display === 'none') {
        signin.style.display = 'block';
        signup.style.display = 'none';
    } else {
        signin.style.display = 'none';
        signup.style.display = 'block';
    }
}