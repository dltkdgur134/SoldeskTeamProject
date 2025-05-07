document.addEventListener('DOMContentLoaded', function () {
	// 메뉴 필터링
	function filterMenus(category) {
		const menuCards = document.querySelectorAll('.menu-card');
		menuCards.forEach(card => {
			const cardCategory = card.getAttribute('data-category');
			card.style.display = (category === '전체' || cardCategory === category) ? 'block' : 'none';
		});
		document.querySelectorAll('.menu-tabs .tab').forEach(btn => btn.classList.remove('active'));
	}
	window.filterMenus = function (category, btn) {
		filterMenus(category);
		btn.classList.add('active');
	};

	// 슬라이더
	let currentSlide = 0;
	const slidesContainer = document.getElementById('slides');
	const slideItems = document.querySelectorAll('#slides .slide');
	const dots = document.querySelectorAll('.dot');
	const totalSlides = slideItems.length;
	const slideWidth = slideItems.length > 0 ? slideItems[0].clientWidth : 0;

	function showSlide(index) {
		currentSlide = (index + totalSlides) % totalSlides;
		slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;
		dots.forEach(dot => dot.classList.remove('active'));
		if (dots[currentSlide]) dots[currentSlide].classList.add('active');
	}

	dots.forEach((dot, index) => {
		dot.addEventListener('click', () => showSlide(index));
	});

	// 드래그 슬라이더
	let isDragging = false, startX = 0, currentTranslate = 0, prevTranslate = 0, animationID;
	function getPositionX(event) {
		return event.type.includes('mouse') ? event.pageX : event.touches[0].clientX;
	}
	function animation() {
		setSliderPosition();
		if (isDragging) requestAnimationFrame(animation);
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
		if (dots[currentSlide]) dots[currentSlide].classList.add('active');
	}

	slideItems.forEach((slide, index) => {
		const slideImage = slide.querySelector('img');
		slide.addEventListener('mousedown', start(index));
		slide.addEventListener('mouseup', end);
		slide.addEventListener('mouseleave', () => { if (isDragging) end(); });
		slide.addEventListener('mousemove', move);
		slide.addEventListener('touchstart', start(index));
		slide.addEventListener('touchend', end);
		slide.addEventListener('touchmove', move);
		slideImage.addEventListener('dragstart', e => e.preventDefault());
	});
	function start(index) {
		return function (event) {
			isDragging = true;
			startX = getPositionX(event);
			animationID = requestAnimationFrame(animation);
			slidesContainer.classList.remove('transition');
		}
	}
	function move(event) {
		if (!isDragging) return;
		const currentPosition = getPositionX(event);
		currentTranslate = prevTranslate + currentPosition - startX;
	}
	function end() {
		isDragging = false;
		cancelAnimationFrame(animationID);
		const movedBy = currentTranslate - prevTranslate;
		if (movedBy < -50) currentSlide++;
		if (movedBy > 50) currentSlide--;
		setPositionByIndex();
	}
	setPositionByIndex(); // 초기 세팅

	// 메뉴 모달
	const modal = document.getElementById('menu-modal');
	const modalMenuName = document.getElementById('menu-name');
	const modalMenuDescription = document.getElementById('menu-description');
	const modalMenuPrice = document.getElementById('menu-price');
	const modalMenuImage = document.getElementById('menu-image');
	const quantityEl = document.getElementById('menu-quantity');
	const closeModalTopBtn = document.getElementById('close-modal-top');
	const optionsContainer = document.getElementById('menu-options');
	let quantity = 1;
	let basePrice = 0;

	document.querySelectorAll('.menu-card').forEach(card => {
		card.addEventListener('click', () => {
			const name = card.getAttribute('data-name');
			const description = card.getAttribute('data-description');
			const price = card.getAttribute('data-price');
			const imageUrl = card.getAttribute('data-image');

			// 모달 정보 초기화
			quantity = 1;
			basePrice = parseInt(price);
			quantityEl.textContent = '1';
			modalMenuName.textContent = name;
			modalMenuDescription.textContent = description;
			modalMenuPrice.textContent = price + '원';
			modalMenuImage.src = imageUrl;
			setupCartButton(card);
			optionsContainer.innerHTML = '';

			let hasOption = false;

			for (let i = 1; i <= 3; i++) {
				const rawGroup = card.getAttribute(`data-option${i}`);
				const rawPrices = card.getAttribute(`data-option${i}-price`);

				if (!rawGroup || !rawPrices) continue;

				const [groupName, optionRaw] = rawGroup.split(":");
				const names = optionRaw?.split("@@__@@") || [];
				const prices = rawPrices.split("@@__@@");

				if (names.length === 0 || prices.length === 0) continue;

				// 옵션 그룹 박스 생성
				const groupBox = document.createElement("div");
				groupBox.className = "option-group-box";

				// 그룹명 제목
				const groupTitle = document.createElement("p");
				groupTitle.textContent = groupName;
				groupTitle.className = "option-group-title";
				groupBox.appendChild(groupTitle);

				names.forEach((optName, idx) => {
					const optPrice = prices[idx];
					if (!optName || !optPrice || isNaN(optPrice)) return;

					const label = document.createElement("label");
					label.className = "option-label";

					const checkbox = document.createElement("input");
					checkbox.type = 'checkbox';
					checkbox.name = `menuOption${i}`;
					checkbox.value = optName.trim();
					checkbox.addEventListener('change', updateTotal);

					const nameSpan = document.createElement("span");
					nameSpan.className = "option-name";
					nameSpan.textContent = optName.trim();

					const priceSpan = document.createElement("span");
					priceSpan.className = "option-price";
					priceSpan.textContent = `+${parseInt(optPrice).toLocaleString()}원`;

					label.appendChild(checkbox);
					label.appendChild(nameSpan);
					label.appendChild(priceSpan);

					const itemDiv = document.createElement("div");
					itemDiv.className = "option-item";
					itemDiv.appendChild(label);

					groupBox.appendChild(itemDiv);
					hasOption = true;
				});

				optionsContainer.appendChild(groupBox);
			}

			if (!hasOption) {
				const p = document.createElement('p');
				p.textContent = '선택 가능한 옵션이 없습니다.';
				optionsContainer.appendChild(p);
			}

			modal.style.display = 'flex';
			document.body.style.overflow = 'hidden';
		});
	});

	function createOption(name, price) {
		const cleanName = name.replace(/[\[\]]/g, '').trim();
		const cleanPrice = price.replace(/[\[\]]/g, '').trim();
		const label = document.createElement('label');
		const checkbox = document.createElement('input');
		checkbox.type = 'checkbox';
		checkbox.name = 'menuOption';
		checkbox.value = name.trim();
		checkbox.addEventListener('change', updateTotal);
		label.appendChild(checkbox);
		label.append(` ${cleanName} +${cleanPrice}원`);
		optionsContainer.appendChild(label);
		optionsContainer.appendChild(document.createElement('br'));
	}

	function isValidOption(name, price) {
		return name && price &&
			name.trim().toLowerCase() !== 'null' &&
			price.trim().match(/^\d+$/);
	}

	function updateTotal() {
		const selected = document.querySelectorAll('#menu-options input[type="checkbox"]:checked');
		let optionTotal = 0;
		selected.forEach(cb => {
			const itemDiv = cb.closest('.option-item');
			const priceSpan = itemDiv?.querySelector('.option-price');
			if (!priceSpan) return;

			const priceText = priceSpan.textContent.match(/\+([\d,]+)원/);
			if (priceText) {
				optionTotal += parseInt(priceText[1].replace(/,/g, ""));
			}
		});
		const total = (basePrice + optionTotal) * quantity;
		document.getElementById('add-to-cart-btn').textContent = `${total.toLocaleString()}원 담기`;
	}

	function setupCartButton(card) {
		const oldBtn = document.getElementById('add-to-cart-btn');
		const newBtn = oldBtn.cloneNode(true);
		oldBtn.parentNode.replaceChild(newBtn, oldBtn);

		const decreaseBtn = document.getElementById('decrease-btn');
		const increaseBtn = document.getElementById('increase-btn');

		const newDecreaseBtn = decreaseBtn.cloneNode(true);
		const newIncreaseBtn = increaseBtn.cloneNode(true);

		decreaseBtn.parentNode.replaceChild(newDecreaseBtn, decreaseBtn);
		increaseBtn.parentNode.replaceChild(newIncreaseBtn, increaseBtn);

		newDecreaseBtn.addEventListener('click', () => {
			if (quantity > 1) {
				quantity--;
				quantityEl.textContent = quantity;
				updateTotal();
			}
		});
		newIncreaseBtn.addEventListener('click', () => {
			if (quantity < 99) {
				quantity++;
				quantityEl.textContent = quantity;
				updateTotal();
			}
		});

		newBtn.addEventListener('click', () => {
			const selectedOptions = Array.from(document.querySelectorAll('input[name^="menuOption"]:checked')).map(cb => {
				const label = cb.closest('.option-label');
				const group = cb.closest('.option-group-box')?.querySelector('.option-group-title')?.textContent?.trim();
				const name = label.querySelector('.option-name')?.textContent?.trim() || cb.value;
				const priceText = label.querySelector('.option-price')?.textContent || '';
				const match = priceText.match(/[\+]?[\s]*([\d,]+)\s*원/);
				const price = match ? parseInt(match[1].replace(/,/g, '')) : 0;
				
				console.log('📦 Parsed option:', {
					groupName: group,
					name,
					priceRaw: priceText,
					priceParsed: price
				});

				return {
					groupName: group || '',
					name,
					price
				};
			});

			const optionTotal = selectedOptions.reduce((sum, opt) => sum + opt.price, 0);

			const unitPrice = basePrice + optionTotal;
			const totalPrice = unitPrice * quantity;

			const data = {
				menuId: card.getAttribute('data-id'),
				storeId: card.getAttribute('data-store-id'),
				quantity: quantity,
				options: selectedOptions,
			};
			fetch('/cart/add', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(data)
			})
			.then(res => res.json())
			.then(result => {
				alert(result.message || '장바구니에 담겼습니다.');
			})
			.catch(err => {
				console.error(err);
				alert('담기에 실패했습니다.');
			});
		});

		updateTotal();
		const optionsContainer = document.getElementById('menu-options');
		optionsContainer.innerHTML = '';

		const option1 = card.getAttribute('data-option1');
		const option1Price = card.getAttribute('data-option1-price');
		const option2 = card.getAttribute('data-option2');
		const option2Price = card.getAttribute('data-option2-price');
		const option3 = card.getAttribute('data-option3');
		const option3Price = card.getAttribute('data-option3-price');

		function isValidOption(name, price) {
			const trimmedName = String(name ?? '').trim();
			let trimmedPrice = String(price ?? '').trim();
			trimmedPrice = trimmedPrice.replace(/[\[\]]/g, '');
			return (
				trimmedName !== '' &&
				trimmedName.toLowerCase() !== 'null' &&
				trimmedName.toLowerCase() !== 'undefined' &&
				trimmedPrice !== '' &&
				/^\d+$/.test(trimmedPrice)
			);
		}

		function createOptionCheckbox(optionName, optionPrice) {
			if (!isValidOption(optionName, optionPrice)) return;

			const cleanName = String(optionName).replace(/[\[\]]/g, '').trim();
			const cleanPrice = String(optionPrice).replace(/[\[\]]/g, '').trim();

			const label = document.createElement('label');
			const checkbox = document.createElement('input');
			checkbox.type = 'checkbox';
			checkbox.name = 'menuOption';
			checkbox.value = optionName.trim();

			checkbox.addEventListener('change', updateTotal);

			label.appendChild(checkbox);
			label.appendChild(document.createTextNode(`${cleanName} +${cleanPrice}원`));

			optionsContainer.appendChild(label);
			optionsContainer.appendChild(document.createElement('br'));
		}

		if (isValidOption(option1, option1Price)) createOptionCheckbox(option1, option1Price);
		if (isValidOption(option2, option2Price)) createOptionCheckbox(option2, option2Price);
		if (isValidOption(option3, option3Price)) createOptionCheckbox(option3, option3Price);

		if (
			!isValidOption(option1, option1Price) &&
			!isValidOption(option2, option2Price) &&
			!isValidOption(option3, option3Price)
		) {
			const noOption = document.createElement('p');
			noOption.textContent = '선택 가능한 옵션이 없습니다.';
			optionsContainer.appendChild(noOption);
		}
	}
	
	
	
	

	// 모달 닫기
	function closeModal() {
		modal.style.display = 'none';
		document.body.style.overflow = 'auto';
	}
	closeModalTopBtn?.addEventListener('click', closeModal);
	window.addEventListener('click', e => { if (e.target === modal) closeModal(); });
	window.addEventListener('keydown', e => { if (e.key === 'Escape') closeModal(); });
});
