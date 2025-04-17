window.toggleRoleLogos = function () {
	console.log("🔄 toggleRoleLogos 실행됨");
    const logoLinks = document.querySelectorAll('.logo');
	console.log("로고 개수:", logoLinks.length);
    logoLinks.forEach(link => {
		console.log("➡️ 링크:", link);
        // Home 버튼은 항상 보이도록 제외
        if (link.querySelector('img')?.src.includes("Logo_ondal_Home.png")) return;

        // toggle visible/hidden class
        if (link.classList.contains('role-logo-hidden')) {
            link.classList.remove('role-logo-hidden');
            link.classList.add('role-logo-visible');
        } else {
            link.classList.remove('role-logo-visible');
            link.classList.add('role-logo-hidden');
        }
    });
};