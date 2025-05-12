document.addEventListener('DOMContentLoaded', () => {
	const updateQty = async (uuid, newQty, container) => {
		try {
			const res = await fetch('/cart/api/cart/update-quantity', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ cartItemUuid: uuid, quantity: newQty })
			});

			if (!res.ok) return;

			const data = await res.json();

			container.querySelector('.qty-num').textContent = newQty;

			const totalPriceEl = container.closest('.cart-item').querySelector('.itemTotalPrice');
			if (totalPriceEl) {
				totalPriceEl.textContent = `가격: ${data.totalPrice}원`;
			}

			const orderTotalEl = document.getElementById('order-total-price');
			if (orderTotalEl) {
				orderTotalEl.textContent = `결제금액: ${data.cartTotalPrice}원`;
			}

			const summaryText = document.querySelector('.order-summary p');
			if (summaryText) {
				summaryText.textContent = `결제금액: ${data.cartTotalPrice}원`;
			}

			const decreaseBtn = container.querySelector('.btn-decrease');
			if (decreaseBtn) {
				decreaseBtn.innerHTML = newQty === 1 ? '🗑' : '-';
			}
		} catch (err) {
			console.error('❌ 수량 변경 실패:', err);
		}
	};

	document.querySelectorAll('.quantity-controls').forEach(container => {
		const uuid = container.getAttribute('data-id');
		const cartItem = container.closest('.cart-item');
		const qtySpan = container.querySelector('.qty-num');

		container.querySelector('.btn-increase').addEventListener('click', () => {
			const qty = parseInt(qtySpan.textContent);
			updateQty(uuid, qty + 1, container);
		});

		container.querySelector('.btn-decrease').addEventListener('click', () => {
			const qty = parseInt(qtySpan.textContent);

			if (qty === 1) {
				fetch('/cart/api/cart/delete', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ cartItemUuid: uuid })
				}).then(() => {
					cartItem.remove();

					fetch('/cart/api/cart/total-price')
						.then(res => res.json())
						.then(data => {
							const totalEl = document.getElementById('order-total-price');
							if (totalEl) {
								totalEl.textContent = `결제금액: ${data.cartTotalPrice}원`;
							}
						});

					if (document.querySelectorAll('.cart-item').length === 0) {
						document.querySelector('.cart-empty').style.display = 'block';
						document.querySelector('.order-summary').style.display = 'none';
					}
				});
			} else {
				updateQty(uuid, qty - 1, container);
			}
		});
	});
});
