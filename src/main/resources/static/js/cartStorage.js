const CART_KEY = "user-cart";
const SECRET_KEY = "ondal-secret-key";

function saveToLocalStorage(cartItem) {
	const encrypted = localStorage.getItem(CART_KEY);
	let cart = [];

	if (encrypted) {
		try {
			const decrypted = CryptoJS.AES.decrypt(encrypted, SECRET_KEY).toString(CryptoJS.enc.Utf8);
			cart = JSON.parse(decrypted);
		} catch (e) {
			console.warn("복호화 실패, 초기화 진행");
			cart = [];
		}
	}

	const sameItem = cart.find(item =>
		item.menuId === cartItem.menuId &&
		JSON.stringify(item.options) === JSON.stringify(cartItem.options)
	);

	if (sameItem) {
		sameItem.quantity += cartItem.quantity;
	} else {
		cart.push(cartItem);
	}

	const encryptedCart = CryptoJS.AES.encrypt(JSON.stringify(cart), SECRET_KEY).toString();
	localStorage.setItem(CART_KEY, encryptedCart);
}

function getFromLocalStorage() {
	const encrypted = localStorage.getItem(CART_KEY);
	if (!encrypted) return [];

	try {
		const decrypted = CryptoJS.AES.decrypt(encrypted, SECRET_KEY).toString(CryptoJS.enc.Utf8);
		return JSON.parse(decrypted);
	} catch (e) {
		console.warn("복호화 실패:", e);
		return [];
	}
}

function removeFromLocalStorage(index) {
	const cart = getFromLocalStorage();
	if (index >= 0 && index < cart.length) {
		cart.splice(index, 1);
		const encryptedCart = CryptoJS.AES.encrypt(JSON.stringify(cart), SECRET_KEY).toString();
		localStorage.setItem(CART_KEY, encryptedCart);
	}
}

function clearCart() {
	localStorage.removeItem(CART_KEY);
}
