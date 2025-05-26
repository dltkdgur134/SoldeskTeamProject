document.addEventListener('DOMContentLoaded', () => {
	const modal = document.getElementById('optionModal');
	const modalOverlay = document.getElementById('option-modal-overlay');
	const closeBtn = document.querySelector('.close-btn');
	let currentCartItemUuid = null;

	function closeModal() {
		modal.style.display = 'none';
		document.body.style.overflow = 'auto';
	}
	closeBtn?.addEventListener('click', closeModal);
	modalOverlay?.addEventListener('click', closeModal);

	document.querySelectorAll('.btn-change-option').forEach(button => {
		button.addEventListener('click', async () => {
			const cartItemUuid = button.dataset.id;
			currentCartItemUuid = cartItemUuid;
			document.getElementById('modal-cart-item-id').value = cartItemUuid;

			try {
				const res = await fetch(`/cart/api/cart-item/options?uuid=${cartItemUuid}`);
				if (!res.ok) throw new Error('옵션 불러오기 실패');
				const optionData = await res.json();

				const modalContent = document.getElementById('optionList');
				modalContent.innerHTML = '';

				optionData.forEach(opt => {
					const className = `group-${opt.groupName.replace(/\s+/g, '-').replace(/[^a-zA-Z0-9-_]/g, '')}`;
					let groupContainer = modalContent.querySelector(`.${className}`);

					if (!groupContainer) {
						groupContainer = document.createElement('div');
						groupContainer.classList.add(className, 'modal-option-group');
						groupContainer.innerHTML = `<div class="group-title"><strong>${opt.groupName}</strong></div>`;
						modalContent.appendChild(groupContainer);
					}

					groupContainer.innerHTML += `
						<label class="modal-option-row">
							<input type="checkbox" name="options" value="${opt.name}" data-price="${opt.price}" ${opt.selected ? 'checked' : ''}>
							<span class="option-name">${opt.name}</span>
							<span class="price">(${opt.price.toLocaleString()}원)</span>
						</label>
					`;
				});

				modal.style.display = 'flex';
				document.body.style.overflow = 'hidden';
			} catch (err) {
				console.error('❌ 옵션 로딩 실패:', err);
			}
		});
	});

	const saveBtn = document.getElementById('save-options-btn');
	if (saveBtn) {
		saveBtn.addEventListener('click', async () => {
			if (!currentCartItemUuid) return;

			const selectedOptions = Array.from(document.querySelectorAll('input[name="options"]:checked')).map(input => ({
				name: input.value,
				price: parseInt(input.dataset.price, 10),
				groupName: input.closest('.modal-option-group').querySelector('.group-title').textContent.trim()
			}));

			try {
				const res = await fetch('/cart/api/cart-item/save-options', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ cartItemUuid: currentCartItemUuid, options: selectedOptions })
				});
				if (!res.ok) throw new Error(`서버 오류 (${res.status})`);

				/*const contentType = res.headers.get('content-type') || '';*/
				/*const data = contentType.includes('application/json') ? await res.json() : { message: '옵션 저장 완료' };*/

				/*alert(data.message);*/
				setTimeout(() => location.reload(), 300);
			} catch (err) {
				console.error('❌ 옵션 저장 에러:', err);
				alert('옵션 저장 중 오류 발생\n' + err.message);
			}
		});
	}
});