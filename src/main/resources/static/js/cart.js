window.addEventListener('DOMContentLoaded', () => {
	const container = document.getElementById('cart-items-container');
	const emptyBox = document.getElementById('empty-cart');
	const summaryBox = document.getElementById('order-summary');
	const orderPriceText = summaryBox.querySelector('p');
	const orderBtn = document.getElementById('order-btn');
	
	const userUUID = document.getElementById('user-uuid')?.value;

	fetch(`/cart/api/restore?userUuid=${userUUID}`)
		.then(res => res.json())
		.then(data => {
			if (data.restored) {
				const items = data.cartItems;
				/*items.forEach(item => {
					saveToLocalStorage(userUUID, item); // 기존 담기 로직 재활용
				});*/
				saveCart(items);
				console.log('✅ 이전 장바구니 복원 완료');
			} else {
				console.log('ℹ️ 복원할 장바구니 없음');
			}
		});

	function saveCart(cartList) {
		const userUUID = document.getElementById('user-uuid')?.value;
		if (!userUUID) {
			console.warn("❌ userUUID 없음, 저장 실패");
			return;
		}
		
		const cartWrapper = {
			cartId: crypto.randomUUID(),
			userUUID,
			items: cartList
		};
		const encrypted = CryptoJS.AES.encrypt(JSON.stringify(cartWrapper), "ondal-secret-key").toString();
		console.log("💾 localStorage 저장됨:", `cart-${userUUID}`, encrypted);
		localStorage.setItem(`cart-${userUUID}`, encrypted);
	}

	function getCart() {
		const userUUID = document.getElementById('user-uuid')?.value;
		if (!userUUID) return [];

		const encrypted = localStorage.getItem(`cart-${userUUID}`);
		if (!encrypted) return [];

		try {
			const decrypted = CryptoJS.AES.decrypt(encrypted, "ondal-secret-key").toString(CryptoJS.enc.Utf8);
			const parsed = JSON.parse(decrypted);
			return parsed.items || [];
		} catch (e) {
			console.warn("❌ 복호화 실패:", e);
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
			orderBtn?.setAttribute('disabled', true);
			return;
		} else {
			emptyBox.style.display = 'none';
			summaryBox.style.display = 'block';
			orderBtn?.removeAttribute('disabled');
		}

		cartList.forEach((item, index) => {
			const selectedOptionPrice = item.options
				.filter(opt => opt.selected)
				.reduce((sum, opt) => sum + opt.price, 0);

			const itemTotal = (item.price + selectedOptionPrice) * item.quantity;
			totalPrice += itemTotal;

			
			/*const optionsHtml = item.options.map(opt =>
				`<span>${opt.groupName} : ${opt.name} (${opt.price.toLocaleString()}원)</span><br>`
			).join('');*/
			const hasOptions = Array.isArray(item.options) && item.options.some(opt => opt.selected);
			const optionsHtml = hasOptions
				? item.options
					.filter(opt => opt.selected)
					.map(opt =>
						`<span>${opt.groupName} : ${opt.name} (${opt.price.toLocaleString()}원)</span><br>`
					).join('')
				: `<span class="no-option">옵션 없음</span>`;
				
				
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
			
			const changeOptionBtn = itemDiv.querySelector('.btn-change-option');
			if (!item.options || item.options.length === 0) {
				changeOptionBtn.style.display = 'none';
			}
			
			container.appendChild(itemDiv);
		});

		orderPriceText.textContent = `결제금액: ${totalPrice.toLocaleString()}원`;
	};

	// 이벤트 위임으로 수량 변경
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

document.getElementById('clear-cart-btn')?.addEventListener('click', () => {
	const userUUID = document.getElementById('user-uuid')?.value;
	if (!userUUID) {
		alert("사용자 정보가 없습니다.");
		return;
	}

	const confirmClear = confirm("장바구니를 모두 비우시겠습니까?");
	if (!confirmClear) return;

	localStorage.removeItem(`cart-${userUUID}`);
	renderCart();
});

function goToOrderPage() {
	const userUUID = document.getElementById('user-uuid')?.value;
	const cartData = getFromLocalStorage(userUUID);
	/*const cartData = getFromLocalStorage(); // localStorage에서 가져오기*/
	if (!cartData || !Array.isArray(cartData.items) || cartData.items.length === 0) {
		alert("장바구니가 비어있습니다.");
		return;
	}
	
	const validItems = cartData.items.map(item => ({
		menuId: item.menuId,
		storeId: item.storeId,
		quantity: item.quantity,
		options: (item.options || []).filter(opt => opt.selected).map(opt => ({
			groupName: opt.groupName,
			name: opt.name,
			price: opt.price
		}))
	}));

	fetch("/cart/api/init", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ userUUID, items:  validItems })
	})
	.then(res => {
		if (!res.ok) throw new Error("서버 오류: 장바구니 초기화 실패");
		return res.json();
	})
	.then(data => {
		localStorage.removeItem(`cart-${userUUID}`);
		localStorage.setItem("pending-cart-id", data.cartId);
		window.location.href = `/store/pay?cartId=${data.cartId}`;
		
		const form = document.createElement('form');
		form.method = 'POST';
		form.action = '/store/pay';

		const input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'cartUUID';
		input.value = data.cartId;
		form.appendChild(input);

		document.body.appendChild(form);
		form.submit();
	})
	.catch(err => {
		console.error("❌ 장바구니 저장 실패:", err);
		alert("주문 처리 중 문제가 발생했습니다.");
	});
}