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
	modalOverlay?.addEventListener('click', (e) => {
		if (e.target === modalOverlay) closeModal();
	});
	window.addEventListener('keydown', (e) => {
		if (e.key === 'Escape') closeModal();
	});

	document.addEventListener('click', (e) => {
		if (!e.target.classList.contains('btn-change-option')) return;

		/*const userUUID = document.body.dataset.useruuid;*/
		const userUUID = document.getElementById('user-uuid')?.value;
		const index = e.target.dataset.index;
		const wrapper = getFromLocalStorage(userUUID);
		const item = wrapper.items[index];
		currentIndex = index;

		optionListEl.innerHTML = '';
		const options = item.options || [];

		if (options.length === 0) {
			optionListEl.innerHTML = '<p>선택 가능한 옵션이 없습니다.</p>';
		} else {
			const grouped = {};
			options.forEach(opt => {
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
					const checked = opt.selected ? 'checked' : '';
					row.innerHTML = `
						<input type="checkbox" name="options" value="${opt.name}" data-price="${opt.price}" ${checked}>
						<span class="option-name">${opt.name}</span>
						<span class="price">(${opt.price.toLocaleString()}원)</span>
					`;
					container.appendChild(row);
				});

				optionListEl.appendChild(container);
			});
		}

		modal.style.display = 'flex';
		document.body.style.overflow = 'hidden';
	});

	saveBtn?.addEventListener('click', () => {
		if (currentIndex == null) return;

		/*const userUUID = document.body.dataset.useruuid;*/
		const userUUID = document.getElementById('user-uuid')?.value;
		const wrapper = getFromLocalStorage(userUUID);
		const original = wrapper.items[currentIndex];
		const allOptions = original.options || [];

		const selectedNames = new Set(
			Array.from(document.querySelectorAll('input[name="options"]:checked')).map(input => input.value)
		);

		const updatedOptions = allOptions.map(opt => ({
			...opt,
			selected: selectedNames.has(opt.name)
		}));

		// ✅ 기존 항목 제거
		wrapper.items.splice(currentIndex, 1);

		// ✅ 병합 처리할 항목 추가
		const updatedItem = {
			...original,
			options: updatedOptions
		};

		// ✅ 수정된 wrapper와 함께 저장 (중복 시 병합됨)
		saveToLocalStorage(userUUID, updatedItem, wrapper);

		closeModal();
		renderCart();
	});

	// 초기 로딩 시 옵션 없는 항목은 버튼 숨기기
	/*const userUUID = document.body.dataset.useruuid;*/
	const userUUID = document.getElementById('user-uuid')?.value;
	const wrapper = getFromLocalStorage(userUUID);
	document.querySelectorAll('.btn-change-option').forEach((button, index) => {
		const item = wrapper.items[index];
		if (!item || !item.options || item.options.length === 0) {
			button.style.display = 'none';
		}
	});
});
