const CART_KEY = "user-cart";
const SECRET_KEY = "ondal-secret-key";

// ✅ 장바구니 항목 저장 (옵션 선택 여부 기준 병합 포함)
function saveToLocalStorage(newItem, existingCart = null) {
	const encrypted = localStorage.getItem(CART_KEY);
	let cart = [];

	if (existingCart) {
		cart = existingCart;
	} else if (encrypted) {
		try {
			const decrypted = CryptoJS.AES.decrypt(encrypted, SECRET_KEY).toString(CryptoJS.enc.Utf8);
			cart = JSON.parse(decrypted);
		} catch (e) {
			console.warn("복호화 실패, 장바구니 초기화");
		}
	}

	if (cart.length > 0 && cart[0].storeId !== newItem.storeId) {
		const confirmClear = confirm("다른 가게의 메뉴가 담겨 있습니다. 장바구니를 비울까요?");
		if (!confirmClear) return;
		cart = [];
	}

	const extractSelectedOptions = (list) =>
		list.filter(opt => opt.selected)
			.map(opt => ({ groupName: opt.groupName, name: opt.name, price: opt.price }))
			.sort((a, b) => a.groupName.localeCompare(b.groupName) || a.name.localeCompare(b.name));

	const itemOptions = JSON.stringify(extractSelectedOptions(newItem.options || []));

	const existing = cart.find(item =>
		item.menuId === newItem.menuId &&
		JSON.stringify(extractSelectedOptions(item.options || [])) === itemOptions
	);

	if (existing) {
		existing.quantity += newItem.quantity;
	} else {
		cart.push(newItem);
	}

	const encryptedCart = CryptoJS.AES.encrypt(JSON.stringify(cart), SECRET_KEY).toString();
	localStorage.setItem(CART_KEY, encryptedCart);
}

// ✅ 장바구니 가져오기
function getFromLocalStorage() {
	const encrypted = localStorage.getItem(CART_KEY);
	if (!encrypted) return [];

	try {
		const decrypted = CryptoJS.AES.decrypt(encrypted, SECRET_KEY).toString(CryptoJS.enc.Utf8);
		return JSON.parse(decrypted);
	} catch (e) {
		console.warn("❌ 복호화 실패:", e);
		return [];
	}
}

// ✅ 특정 항목 삭제
function removeFromLocalStorage(index) {
	const cart = getFromLocalStorage();
	if (index >= 0 && index < cart.length) {
		cart.splice(index, 1);
		const encryptedCart = CryptoJS.AES.encrypt(JSON.stringify(cart), SECRET_KEY).toString();
		localStorage.setItem(CART_KEY, encryptedCart);
	}
}

// ✅ 전체 비우기
function clearCart() {
	localStorage.removeItem(CART_KEY);
}
