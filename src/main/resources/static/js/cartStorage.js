// ✅ cartStorage.js - 사용자 UUID 기반 장바구니 저장 구조

const SECRET_KEY = "ondal-secret-key";

function getCartKey(userUUID) {
	return `cart-${userUUID}`;
}

function saveToLocalStorage(userUUID, cartItem, existingWrapper = null) {
	const key = getCartKey(userUUID);
	let cartWrapper = existingWrapper;

	if (!cartWrapper) {
		const encrypted = localStorage.getItem(key);
		cartWrapper = { cartId: crypto.randomUUID(), userUUID, items: [] };

		if (encrypted) {
			try {
				const decrypted = CryptoJS.AES.decrypt(encrypted, SECRET_KEY).toString(CryptoJS.enc.Utf8);
				cartWrapper = JSON.parse(decrypted);
			} catch (e) {
				console.warn("복호화 실패, 장바구니 초기화");
			}
		}
	}

	if (
		cartWrapper.items.length > 0 &&
		cartWrapper.items[0].storeId !== cartItem.storeId
	) {
		const confirmClear = confirm("다른 가게의 메뉴가 담겨 있습니다. 장바구니를 비울까요?");
		if (!confirmClear) return;
		cartWrapper.items = [];
	}

	// ✅ 모든 옵션에 selected 포함해서 다시 저장
	const fullOptionsWithSelected = (cartItem.options || []).map(opt => ({
		groupName: opt.groupName,
		name: opt.name,
		price: opt.price,
		selected: opt.selected ?? false
	}));

	cartItem.options = fullOptionsWithSelected;

	// ✅ 중복 판단을 위한 selected 옵션만 추출
	const itemOptions = JSON.stringify(
		fullOptionsWithSelected
			.filter(opt => opt.selected)
			.sort((a, b) => a.groupName.localeCompare(b.groupName) || a.name.localeCompare(b.name))
	);

	const existing = cartWrapper.items.find(item =>
		item.menuId === cartItem.menuId &&
		JSON.stringify(
			(item.options || [])
				.filter(opt => opt.selected)
				.sort((a, b) => a.groupName.localeCompare(b.groupName) || a.name.localeCompare(b.name))
		) === itemOptions
	);
/*
	const extractSelectedOptions = (list) =>
		list
			.filter(opt => opt.selected)
			.map(opt => ({ groupName: opt.groupName, name: opt.name, price: opt.price }))
			.sort((a, b) => a.groupName.localeCompare(b.groupName) || a.name.localeCompare(b.name));

	const itemOptions = JSON.stringify(extractSelectedOptions(cartItem.options || []));

	const existing = cartWrapper.items.find(item =>
		item.menuId === cartItem.menuId &&
		JSON.stringify(extractSelectedOptions(item.options || [])) === itemOptions
	);
*/
	if (existing) {
		existing.quantity += cartItem.quantity;
	} else {
		cartWrapper.items.push(cartItem);
	}

	const encryptedCart = CryptoJS.AES.encrypt(JSON.stringify(cartWrapper), SECRET_KEY).toString();
	localStorage.setItem(key, encryptedCart);
}

function getFromLocalStorage(userUUID) {
	const key = getCartKey(userUUID);
	const encrypted = localStorage.getItem(key);
	if (!encrypted) return { cartId: crypto.randomUUID(), userUUID, items: [] };

	try {
		const decrypted = CryptoJS.AES.decrypt(encrypted, SECRET_KEY).toString(CryptoJS.enc.Utf8);
		const parsed = JSON.parse(decrypted);
		if (!Array.isArray(parsed.items)) parsed.items = [];
		return parsed;
	} catch (e) {
		console.warn("❌ 복호화 실패:", e);
		return { cartId: crypto.randomUUID(), userUUID, items: [] };
	}
}

function clearCart(userUUID) {
	localStorage.removeItem(getCartKey(userUUID));
}

function removeFromCart(userUUID, index) {
	const wrapper = getFromLocalStorage(userUUID);
	if (index >= 0 && index < wrapper.items.length) {
		wrapper.items.splice(index, 1);
		const encrypted = CryptoJS.AES.encrypt(JSON.stringify(wrapper), SECRET_KEY).toString();
		localStorage.setItem(getCartKey(userUUID), encrypted);
	}
}
