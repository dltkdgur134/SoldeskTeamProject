function filterMenus(category) {
	const menuCards = document.querySelectorAll('.menu-card');
	
	menuCards.forEach(card => {
		const cardCategory = card.getAttribute('data-category');
		if (category === '전체' || cardCategory === category) {
			card.style.display = 'block';
		} else {
			card.style.display = 'none';
		}
	});
	
	const buttons = document.querySelectorAll('.menu-tabs .tab');
	buttons.forEach(btn => btn.classList.remove('active'));
	
	event.target.classList.add('active');
}

let currentSlide = 0;

function showSlide(index) {
	const slides = document.getElementById('slides');
	const dots = document.querySelectorAll('.dot');
	const totalSlides = dots.length;
	
	if (index >= totalSlides) {
		currentSlide = 0;
	} else if (index < 0) {
		currentSlide = totalSlides - 1;
	} else {
		currentSlide = index;
	}

	slides.style.transform = `translateX(-${currentSlide * 100}%)`;
	
	dots.forEach(dot => dot.classList.remove('active'));
	dots[currentSlide].classList.add('active');
}

document.querySelectorAll('.dot').forEach((dot, index) => {
	dot.addEventListener('click', () => {
		showSlide(index);
	});
});

// 자동 슬라이드 (선택사항)
setInterval(() => {
	showSlide(currentSlide + 1);
}, 5000); // 5초마다 넘어가게