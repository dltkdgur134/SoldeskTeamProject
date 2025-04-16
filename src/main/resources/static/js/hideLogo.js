window.toggleRoleLogos = function () {
    const logos = document.querySelectorAll('.role-logo');
    logos.forEach(logo => {
        logo.classList.toggle('show');
    });
};