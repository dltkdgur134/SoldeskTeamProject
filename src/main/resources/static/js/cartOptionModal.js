document.addEventListener('DOMContentLoaded', () => {
	const modal = document.getElementById('optionModal');
	const modalOverlay = document.getElementById('option-modal-overlay');
	const closeBtn = document.querySelector('.close-btn');
	let currentCartItemUuid = null;

	document.querySelectorAll('.btn-change-option').forEach(button => {
		button.addEventListener('click', async () => {
			const cartItemUuid = button.dataset.id;
			currentCartItemUuid = cartItemUuid;

			try {
				const res = await fetch(`/cart/api/cart-item/options?uuid=${cartItemUuid}`);
				if (!res.ok) throw new Error('옵션 불러오기 실패');

				const optionData = await res.json(); // 예: [{groupName: "소스", name: "매운맛", price: 1000}, ...]

				const modalContent = document.getElementById('optionList');
				modalContent.innerHTML = '';

				optionData.forEach((opt, idx) => {
					const safeClassName = `group-${opt.groupName.replace(/\s+/g, '-').replace(/[^a-zA-Z0-9-_]/g, '')}`;
					const existingGroup = modalContent.querySelector(`.${safeClassName}`);
					let groupContainer = existingGroup;

					if (!groupContainer) {
						groupContainer = document.createElement('div');
						groupContainer.classList.add(safeClassName, 'modal-option-group');
						groupContainer.innerHTML = `<div class="group-title"><strong>${opt.groupName}</strong></div>`;
						modalContent.appendChild(groupContainer);
					}

					const row = document.createElement('label');
					row.classList.add('modal-option-row');
					row.innerHTML = `
						<input type="checkbox" name="options" value="${opt.name}" data-price="${opt.price}" checked>
						<span class="option-name">${opt.name}</span>
						<span class="price">(${opt.price.toLocaleString()}원)</span>
					`;
					groupContainer.appendChild(row);
				});

				modal.style.display = 'flex';
				document.body.style.overflow = 'hidden';
			} catch (err) {
				console.error('❌ 옵션 로딩 실패:', err);
			}
		});
	});
	
	if (closeBtn) {
		closeBtn.addEventListener('click', () => {
			modal.style.display = 'none';
			document.body.style.overflow = 'auto';
		});
	}

	if (modalOverlay) {
		modalOverlay.addEventListener('click', () => {
			modal.style.display = 'none';
			document.body.style.overflow = 'auto';
		});
	}
	
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
					body: JSON.stringify({
						cartItemUuid: currentCartItemUuid,
						options: selectedOptions
					})
				});
				if (!res.ok) throw new Error('옵션 저장 실패');
				const data = await res.json();
				console.log('🧪 저장 응답:', data);
				
				alert('옵션이 저장되었습니다.');
				modal.style.display = 'none';
				document.body.style.overflow = 'auto';
				
				const card = document.querySelector(`.cart-item-card[data-id="${currentCartItemUuid}"]`);
				if (card) {
					const optionText = selectedOptions.map(opt => `${opt.groupName}:${opt.name} (${opt.price.toLocaleString()}원)`).join(', ');
					card.querySelector('.cart-item-options').textContent = optionText;

					const basePrice = parseInt(card.dataset.basePrice, 10); // 원래 메뉴 가격
					const quantity = parseInt(card.querySelector('.quantity-display').textContent, 10);
					const optionTotal = selectedOptions.reduce((sum, opt) => sum + opt.price, 0);
					const total = (basePrice + optionTotal) * quantity;

					card.querySelector('.cart-item-total-price').textContent = `${total.toLocaleString()}원`;
				}
			} catch (err) {
				console.error('❌ 옵션 저장 에러:', err);
				alert('옵션 저장 중 오류 발생');
			}
		});
	}
});


