import { dynamicCategories, renderEditCategorySelect } from './category.js';
import { maxOption, fillEditOptions, editOptionCount } from './options.js';

export function openMenuModal() {
	document.getElementById("editMenuModal").style.display = "none";
	document.getElementById("menuModal").style.display = "flex";
}

export function closeMenuModal() {
	document.getElementById("menuModal").style.display = "none";
}

export function openEditMenuModal(menuId) {
	const menu = window.menuList.find(m => m.menuId === menuId);
	if (!menu) return;
	
	document.getElementById("menuModal").style.display = "none";

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
	
	fillEditOptions(menu);
	
	document.getElementById('editMenuModal').style.display = 'flex';
	document.getElementById('deleteMenuId').value = menu.menuId;
	const addBtn = document.getElementById("edit-add-option-btn");
	addBtn.disabled = editOptionCount.value >= maxOption;
	
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


