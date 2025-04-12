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