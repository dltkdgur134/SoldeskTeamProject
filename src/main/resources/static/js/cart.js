document.addEventListener('DOMContentLoaded', () => {
	const updateQty = async (uuid, newQty, container) => {
		try {
			const res = await fetch(`/cart/api/cart/update-quantity`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ cartItemUuid: uuid, quantity: newQty })
			});

			if (res.ok) {
				const data = await res.json(); // { totalPrice: ..., cartTotalPrice: ... }

				// 수량 업데이트
				container.querySelector('.qty-num').textContent = newQty;

				// 가격 반영
				const totalPriceEl = container.closest('.cart-item').querySelector('.itemTotalPrice');
				if (totalPriceEl) {
					totalPriceEl.textContent = `가격: ${data.totalPrice}원`;
				}

				// 전체 결제 금액 반영
				const orderTotalEl = document.getElementById('order-total-price');
				if (orderTotalEl) {
					orderTotalEl.textContent = `결제금액: ${data.cartTotalPrice}원`;
				}
				const orderSummaryText = document.querySelector('.order-summary p');
				if (orderSummaryText) {
					orderSummaryText.textContent = `결제금액: ${data.cartTotalPrice}원`;
				}

				// 버튼 표시 전환
				const decreaseBtn = container.querySelector('.btn-decrease');
				if (decreaseBtn) {
					decreaseBtn.innerHTML = newQty === 1 ? '🗑' : '-';
				}
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
				fetch(`/cart/api/cart/delete`, {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ cartItemUuid: uuid })
				}).then(() => {
					cartItem.remove();

					// 전체 결제 금액도 새로고침
					fetch('/cart/api/cart/total-price')
						.then(res => res.json())
						.then(data => {
							const totalEl = document.getElementById('order-total-price');
							if (totalEl) {
								totalEl.textContent = `결제금액: ${data.cartTotalPrice}원`;
							}
						});

					// 장바구니가 완전히 비었는지 확인
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


