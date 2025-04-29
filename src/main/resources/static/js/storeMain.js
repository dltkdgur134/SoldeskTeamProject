document.addEventListener('DOMContentLoaded', function () {
	// 메뉴 필터링
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
	}

	window.filterMenus = function (category, btn) {
		filterMenus(category);
		btn.classList.add('active');
	};

	// 슬라이더 세팅
	let currentSlide = 0;
	const slidesContainer = document.getElementById('slides');
	const slideItems = document.querySelectorAll('#slides .slide');
	const dots = document.querySelectorAll('.dot');
	const totalSlides = slideItems.length;
	const slideWidth = slideItems.length > 0 ? slideItems[0].clientWidth : 0;

	function showSlide(index) {
		if (index >= totalSlides) {
			currentSlide = 0;
		} else if (index < 0) {
			currentSlide = totalSlides - 1;
		} else {
			currentSlide = index;
		}

		slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;

		dots.forEach(dot => dot.classList.remove('active'));
		if (dots[currentSlide]) {
			dots[currentSlide].classList.add('active');
		}
	}

	document.querySelectorAll('.dot').forEach((dot, index) => {
		dot.addEventListener('click', () => {
			showSlide(index);
		});
	});

	// 슬라이드 드래그
	let isDragging = false;
	let startX = 0;
	let currentTranslate = 0;
	let prevTranslate = 0;
	let animationID;
	const dragThreshold = 50;

	function touchStart(index) {
		return function (event) {
			isDragging = true;
			startX = getPositionX(event);
			animationID = requestAnimationFrame(animation);
			slidesContainer.classList.remove('transition');
		}
	}

	function touchMove(event) {
		if (!isDragging) return;
		const currentPosition = getPositionX(event);
		currentTranslate = prevTranslate + currentPosition - startX;
	}

	function touchEnd() {
		isDragging = false;
		cancelAnimationFrame(animationID);

		const movedBy = currentTranslate - prevTranslate;

		if (movedBy < -dragThreshold) {
			currentSlide++;
		} else if (movedBy > dragThreshold) {
			currentSlide--;
		}

		if (currentSlide < 0) {
			currentSlide = totalSlides - 1;
		} else if (currentSlide >= totalSlides) {
			currentSlide = 0;
		}

		setPositionByIndex();
	}

	function getPositionX(event) {
		return event.type.includes('mouse') ? event.pageX : event.touches[0].clientX;
	}

	function animation() {
		setSliderPosition();
		if (isDragging) {
			requestAnimationFrame(animation);
		}
	}

	function setSliderPosition() {
		slidesContainer.style.transform = `translateX(${currentTranslate}px)`;
	}

	function setPositionByIndex() {
		slidesContainer.classList.add('transition');
		currentTranslate = -currentSlide * slideWidth;
		prevTranslate = currentTranslate;
		slidesContainer.style.transform = `translateX(${currentTranslate}px)`;

		dots.forEach(dot => dot.classList.remove('active'));
		if (dots[currentSlide]) {
			dots[currentSlide].classList.add('active');
		}
	}

	slideItems.forEach((slide, index) => {
		const slideImage = slide.querySelector('img');

		slide.addEventListener('mousedown', touchStart(index));
		slide.addEventListener('mouseup', touchEnd);
		slide.addEventListener('mouseleave', () => {
			if (isDragging) touchEnd();
		});
		slide.addEventListener('mousemove', touchMove);

		slide.addEventListener('touchstart', touchStart(index));
		slide.addEventListener('touchend', touchEnd);
		slide.addEventListener('touchmove', touchMove);

		slideImage.addEventListener('dragstart', (e) => e.preventDefault());
	});

	setPositionByIndex(); // 초기 슬라이드 세팅

	// 메뉴 카드 클릭 시 모달 띄우기
	const modal = document.getElementById('menu-modal');
	const modalMenuName = document.getElementById('menu-name');
	const modalMenuDescription = document.getElementById('menu-description');
	const modalMenuPrice = document.getElementById('menu-price');
	const modalMenuImage = document.getElementById('menu-image');
	const closeModalFooterBtn = document.getElementById('close-modal-footer');
	const closeModalTopBtn = document.getElementById('close-modal-top');

	if (modal && modalMenuName && modalMenuDescription && modalMenuPrice && modalMenuImage) {
		document.querySelectorAll('.menu-card').forEach(card => {
			card.addEventListener('click', () => {
				const name = card.getAttribute('data-name');
				const description = card.getAttribute('data-description');
				const price = card.getAttribute('data-price');
				const imageUrl = card.getAttribute('data-image');

				const option1 = card.getAttribute('data-option1');
				const option1Price = card.getAttribute('data-option1-price');
				const option2 = card.getAttribute('data-option2');
				const option2Price = card.getAttribute('data-option2-price');
				const option3 = card.getAttribute('data-option3');
				const option3Price = card.getAttribute('data-option3-price');

				modalMenuName.textContent = name;
				modalMenuDescription.textContent = description;
				modalMenuPrice.textContent = price + '원';
				modalMenuImage.src = imageUrl;

				const optionsContainer = document.getElementById('menu-options');
				optionsContainer.innerHTML = '';

				function createOptionCheckbox(optionName, optionPrice) {
					if (optionName) {
						const label = document.createElement('label');
						const checkbox = document.createElement('input');
						checkbox.type = 'checkbox';
						checkbox.name = 'menuOption';
						checkbox.value = optionName.trim();

						checkbox.addEventListener('change', updateTotalOptionPrice); // 체크할 때 합산

						label.appendChild(checkbox);
						label.appendChild(document.createTextNode(
							`${optionName.trim()} (+${optionPrice.trim()}원)`));

						optionsContainer.appendChild(label);
						optionsContainer.appendChild(document.createElement('br'));
					}
				}

				// ② ★ 그리고 그 밑에 function updateTotalOptionPrice ~ 함수도 넣기 ★
				function updateTotalOptionPrice() {
					const checkboxes = optionsContainer.querySelectorAll('input[name="menuOption"]:checked');
					let total = 0;
					checkboxes.forEach(checkbox => {
						const label = checkbox.parentElement.textContent;
						const priceMatch = label.match(/\+\s*([\d,]+)원/);
						if (priceMatch) {
							const price = parseInt(priceMatch[1].replace(',', ''));
							total += price;
						}
					});
					document.getElementById('menu-total-price').textContent = `추가 금액: ${total.toLocaleString()}원`;
				}
				
/*				function createOptionCheckbox(optionName, optionPrice) {
					if (optionName) {
						const label = document.createElement('label');
						const checkbox = document.createElement('input');
						checkbox.type = 'checkbox';
						checkbox.name = 'menuOption';
						checkbox.value = optionName.trim();

						label.appendChild(checkbox);
						label.appendChild(document.createTextNode(
							`${optionName.trim()} (+${optionPrice.trim()}원)`));

						optionsContainer.appendChild(label);
						optionsContainer.appendChild(document.createElement('br'));
					}
				}

				if (option1) createOptionCheckbox(option1, option1Price);
				if (option2) createOptionCheckbox(option2, option2Price);
				if (option3) createOptionCheckbox(option3, option3Price);

				if (!option1 && !option2 && !option3) {
					const noOption = document.createElement('p');
					noOption.textContent = '선택 가능한 옵션이 없습니다.';
					optionsContainer.appendChild(noOption);
				}*/
				
				if (option1) createOptionCheckbox(option1, option1Price);
				if (option2) createOptionCheckbox(option2, option2Price);
				if (option3) createOptionCheckbox(option3, option3Price);

				if (!option1 && !option2 && !option3) {
					const noOption = document.createElement('p');
					noOption.textContent = '선택 가능한 옵션이 없습니다.';
					optionsContainer.appendChild(noOption);
				}


				modal.style.display = 'flex';
				document.body.style.overflow = 'hidden';
			});
		});

		function closeModal() {
			modal.style.display = 'none';
			document.body.style.overflow = 'auto';
		}

		if (closeModalFooterBtn) {
			closeModalFooterBtn.addEventListener('click', (event) => {
				event.stopPropagation();
				closeModal();
			});
		}

		if (closeModalTopBtn) {
			closeModalTopBtn.addEventListener('click', (event) => {
				event.stopPropagation();
				closeModal();
			});
		}

		window.addEventListener('click', (event) => {
			if (event.target === modal) {
				closeModal();
			}
		});

		window.addEventListener('keydown', (event) => {
			if (event.key === "Escape") {
				closeModal();
			}
		});
	}
});