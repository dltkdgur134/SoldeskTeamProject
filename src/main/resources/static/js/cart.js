// ✅ cart.js - 장바구니 렌더링 및 수량 조정

window.addEventListener('DOMContentLoaded', () => {
	const container = document.getElementById('cart-items-container');
	const emptyBox = document.getElementById('empty-cart');
	const summaryBox = document.getElementById('order-summary');
	const orderPriceText = summaryBox.querySelector('p');

	function saveCart(cartList) {
		localStorage.setItem("user-cart", CryptoJS.AES.encrypt(JSON.stringify(cartList), "ondal-secret-key").toString());
	}

	function getCart() {
		const encrypted = localStorage.getItem("user-cart");
		if (!encrypted) return [];
		try {
			const decrypted = CryptoJS.AES.decrypt(encrypted, "ondal-secret-key").toString(CryptoJS.enc.Utf8);
			return JSON.parse(decrypted);
		} catch (e) {
			return [];
		}
	}

	window.renderCart = function () {
		const cartList = getCart();
		container.innerHTML = '';
		let totalPrice = 0;

		if (cartList.length === 0) {
			emptyBox.style.display = 'block';
			summaryBox.style.display = 'none';
			return;
		} else {
			emptyBox.style.display = 'none';
			summaryBox.style.display = 'block';
		}

		cartList.forEach((item, index) => {
			const itemTotal = (item.price + item.options.reduce((sum, opt) => sum + opt.price, 0)) * item.quantity;
			totalPrice += itemTotal;

			const optionsHtml = item.options.map(opt =>
				`<span>${opt.groupName} : ${opt.name} (${opt.price.toLocaleString()}원)</span><br>`
			).join('');

			const itemDiv = document.createElement('div');
			itemDiv.className = 'cart-item';
			itemDiv.setAttribute('data-index', index);
			itemDiv.innerHTML = `
				<div class="cart-item-body">
					<div class="cart-item-left"><img src="${item.menuImage}" alt="메뉴 이미지"></div>
					<div class="cart-item-middle">
						<h4>${item.menuName}</h4>
						<p class="menu-base-price">가격: ${item.price.toLocaleString()}원</p>
						<div class="cart-options">${optionsHtml}</div>
						<p class="itemTotalPrice">가격: ${itemTotal.toLocaleString()}원</p>
					</div>
				</div>
				<div class="cart-item-footer">
					<button class="btn-change-option" data-index="${index}">옵션 변경</button>
					<div class="quantity-controls">
						<button class="btn-decrease">${item.quantity === 1 ? '🗑' : '-'}</button>
						<span class="qty-num">${item.quantity}</span>
						<button class="btn-increase">+</button>
					</div>
				</div>
			`;
			container.appendChild(itemDiv);
		});

		orderPriceText.textContent = `결제금액: ${totalPrice.toLocaleString()}원`;
	};

	container.addEventListener('click', (e) => {
		const itemDiv = e.target.closest('.cart-item');
		if (!itemDiv) return;
		const index = parseInt(itemDiv.getAttribute('data-index'));
		if (isNaN(index)) return;

		const cartList = getCart();

		if (e.target.classList.contains('btn-increase')) {
			if (cartList[index].quantity < 99) {
				cartList[index].quantity++;
			}
		} else if (e.target.classList.contains('btn-decrease')) {
			if (cartList[index].quantity === 1) {
				cartList.splice(index, 1);
			} else {
				cartList[index].quantity--;
			}
		} else {
			return;
		}

		saveCart(cartList);
		renderCart();
	});

	renderCart();
});
