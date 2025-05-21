document.addEventListener('DOMContentLoaded', () => {
	const modal = document.getElementById('optionModal');
	const modalOverlay = document.getElementById('option-modal-overlay');
	const closeBtn = document.querySelector('.close-btn');
	const saveBtn = document.getElementById('save-options-btn');
	const optionListEl = document.getElementById('optionList');
	let currentIndex = null;

	function closeModal() {
		modal.style.display = 'none';
		document.body.style.overflow = 'auto';
	}

	closeBtn?.addEventListener('click', closeModal);
	modalOverlay?.addEventListener('click', closeModal);
	window.addEventListener('keydown', (e) => {
		if (e.key === 'Escape') closeModal();
	});

	window.addEventListener('click', (e) => {
		if (e.target === modalOverlay) closeModal();
	});

	document.addEventListener('click', (e) => {
		if (!e.target.classList.contains('btn-change-option')) return;

		const index = e.target.dataset.index;
		const cart = getFromLocalStorage();
		const item = cart[index];
		currentIndex = index;

		optionListEl.innerHTML = '';

		const grouped = {};
		item.options.forEach(opt => {
			if (!grouped[opt.groupName]) grouped[opt.groupName] = [];
			grouped[opt.groupName].push(opt);
		});

		Object.entries(grouped).forEach(([groupName, opts]) => {
			const container = document.createElement('div');
			container.className = 'modal-option-group';
			const title = document.createElement('div');
			title.className = 'group-title';
			title.innerHTML = `<strong>${groupName}</strong>`;
			container.appendChild(title);

			opts.forEach(opt => {
				const row = document.createElement('label');
				row.className = 'modal-option-row';
				row.innerHTML = `
					<input type="checkbox" name="options" value="${opt.name}" data-price="${opt.price}" checked>
					<span class="option-name">${opt.name}</span>
					<span class="price">(${opt.price.toLocaleString()}원)</span>
				`;
				container.appendChild(row);
			});

			optionListEl.appendChild(container);
		});

		modal.style.display = 'flex';
		document.body.style.overflow = 'hidden';
	});

	saveBtn?.addEventListener('click', () => {
		if (currentIndex == null) return;

		const checked = document.querySelectorAll('input[name="options"]:checked');
		const newOptions = Array.from(checked).map(input => ({
			name: input.value,
			price: parseInt(input.dataset.price, 10),
			groupName: input.closest('.modal-option-group').querySelector('.group-title').textContent.trim()
		}));

		const cart = getFromLocalStorage();
		cart[currentIndex].options = newOptions;
		localStorage.setItem("user-cart", CryptoJS.AES.encrypt(JSON.stringify(cart), "ondal-secret-key").toString());

		closeModal();
		renderCart(); // 전역 함수 호출로 새로고침 없이 UI 갱신
	});
});
