document.addEventListener('DOMContentLoaded', () => {
	const updateQty = async (uuid, newQty, container) => {
		try {
			const res = await fetch(`/api/cart/update-quantity`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ cartItemUuid: uuid, quantity: newQty })
			});
			if (res.ok) {
				const data = await res.json(); // { totalPrice: 52000, cartTotalPrice: 74100 }

				// 수량 텍스트 변경
				container.querySelector('.qty-num').textContent = newQty;

				// 항목 총 가격 변경
				container.closest('.cart-item').querySelector('.itemTotalPrice').textContent = `가격: ${data.totalPrice}원`;

				// 전체 금액 변경
				document.getElementById('order-total-price').textContent = `결제금액: ${data.cartTotalPrice}원`;

				// - 또는 🗑 표시 전환
				const decreaseBtn = container.querySelector('.btn-decrease');
				decreaseBtn.innerHTML = newQty === 1 ? '🗑' : '-';
			}
		} catch (err) {
			console.error('수량 변경 실패', err);
		}
	};

	document.querySelectorAll('.quantity-controls').forEach(container => {
		const uuid = container.getAttribute('data-id');
		const cartItem = container.closest('.cart-item');
		let qtySpan = container.querySelector('.qty-num');

		container.querySelector('.btn-increase').addEventListener('click', () => {
			let qty = parseInt(qtySpan.textContent);
			updateQty(uuid, qty + 1, container);
		});

		container.querySelector('.btn-decrease').addEventListener('click', () => {
			let qty = parseInt(qtySpan.textContent);
			if (qty === 1) {
				fetch(`/api/cart/delete`, {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ cartItemUuid: uuid })
				}).then(async () => {
					cartItem.remove();

					const res = await fetch('/api/cart/total-price');
					if (res.ok) {
						const data = await res.json();
						document.getElementById('order-total-price').textContent = `결제금액: ${data.cartTotalPrice}원`;
					}

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


