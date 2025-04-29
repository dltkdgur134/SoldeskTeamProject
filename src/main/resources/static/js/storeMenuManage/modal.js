import { dynamicCategories, renderEditCategorySelect } from './category.js';
import { maxOption, editOptionCount } from './options.js';

export function openMenuModal() {
	document.getElementById("menuModal").style.display = "flex";
}

export function closeMenuModal() {
	document.getElementById("menuModal").style.display = "none";
}

export function openEditMenuModal(menuId) {
	const menu = window.menuList.find(m => m.menuId === menuId);
	if (!menu) return;

	console.log("👉 선택할 카테고리 ID:", menu.menuCategoryId);
	console.log("🧭 전체 카테고리 목록:", [...dynamicCategories.entries()]);
	console.log("editMenuModal: 선택된 메뉴", menu);

	document.getElementById('editMenuId').value = menu.menuId;
	document.getElementById('editMenuName').value = menu.menuName;
	document.getElementById('editPrice').value = menu.price;
	document.getElementById('editDescription').value = menu.description || "";
	document.getElementById('editMenuStatus').value = menu.menuStatus;
	document.getElementById('editMenuCategory').value = menu.menuCategory || "";
	
	renderEditCategorySelect(menu.menuCategoryId);

	const container = document.getElementById("edit-option-container");
	container.innerHTML = "";
	editOptionCount.value = 0;

	for (let i = 1; i <= 3; i++) {
		const nameKey = `menuOptions${i}`;
		const priceKey = `menuOptions${i}Price`;

		if (menu[nameKey] && menu[priceKey]) {
			const names = Array.isArray(menu[nameKey]) ? menu[nameKey] : menu[nameKey].split("온달");
			const prices = Array.isArray(menu[priceKey]) ? menu[priceKey] : menu[priceKey].split("온달");

			for (let j = 0; j < names.length; j++) {
				if (names[j].trim() === "") continue;

				editOptionCount.value++;

				const div = document.createElement("div");
				div.className = "option-group";
				div.innerHTML = `
					<label>옵션 ${editOptionCount.value}</label><br>
					<input type="text" name="menuOptions${editOptionCount.value}[]" value="${names[j].trim()}" required />
					<input type="number" name="menuOptions${editOptionCount.value}Price[]" value="${String(prices[j]).trim() || 0}" required />
					<button type="button" onclick="removeOption(this)">삭제</button>
				`;

				container.appendChild(div);
			}
		}
	}

	document.getElementById('editMenuModal').style.display = 'flex';
	const addBtn = document.getElementById("edit-add-option-btn");
	addBtn.disabled = editOptionCount.value >= maxOption;
	
	document.getElementById('deleteMenuId').value = menu.menuId;
}

export function closeEditMenuModal() {
	document.getElementById('editMenuModal').style.display = 'none';
}

export function setupGlobalModalEvents() {
	document.addEventListener("mousedown", function(event) {
		const modals = [
			{ id: "menuModal", close: closeMenuModal },
			{ id: "editMenuModal", close: closeEditMenuModal }
		];

		for (const { id, close } of modals) {
			const modal = document.getElementById(id);
			if (!modal || modal.style.display !== "flex") continue;

			const content = modal.querySelector(".modal-content");
			if (event.target === modal && !content.contains(event.target)) {
				close();
			}
		}
	});

	document.addEventListener("keydown", e => {
		if (e.key === "Escape") {
			if (document.getElementById("menuModal").style.display === "flex") closeMenuModal();
			if (document.getElementById("editMenuModal").style.display === "flex") closeEditMenuModal();
		}
	});
}


