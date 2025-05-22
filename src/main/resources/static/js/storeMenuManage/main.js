import { openMenuModal, closeMenuModal, openEditMenuModal, closeEditMenuModal, setupGlobalModalEvents } from './modal.js';
import { dynamicCategories, openCategoryModal, closeCategoryModal, 
	addCategory, removeCategory, initCategoryButtons, 
	saveDynamicCategories, renderCategorySelect, renderEditCategorySelect, initCategoryData } from './category.js';
import { addOptionField, fillEditOptions, addOptionCount, editOptionCount/*, removeOption*/ } from './options.js';
import { handleSubmit, deleteMenu, setupEditFormValidation, filterMenusByCategory } from './menuForm.js';


console.log("✅ main.js loaded");
document.addEventListener("DOMContentLoaded", async () => {
	setupGlobalModalEvents();
	initCategoryData();
	
	const storeId = document.body.dataset.storeId;
	dynamicCategories.clear();
	window.categoryList.forEach(cat => {
		dynamicCategories.set(cat.categoryName.trim(), cat.id);
	});
	
	if (!dynamicCategories.has("메인")) {
		try {
			const res = await fetch("/api/category/add", {
				method: "POST",
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
				body: new URLSearchParams({ name: "메인", storeId })
			});
			const mainCategory = await res.json();
			window.categoryList.push({
				categoryName: mainCategory.categoryName,
				id: mainCategory.id
			});
			dynamicCategories.set(mainCategory.categoryName, mainCategory.id);
		} catch (err) {
			alert("기본 카테고리 '메인' 생성 실패");
		}
	}
	
	initCategoryButtons();
	renderCategorySelect();
	setupEditFormValidation();

	document.getElementById("add-option-btn").addEventListener("click", () => {
		addOptionField("option1-container", "add-option-btn", addOptionCount);
	});
	document.getElementById("edit-add-option-btn").addEventListener("click", () => {
		addOptionField("edit-option-container", "edit-add-option-btn", editOptionCount);
	});
	document.querySelectorAll(".menu-card").forEach(card => {
		card.addEventListener("click", () => {
			openEditMenuModal(card.getAttribute("data-menu-id"));
		});
		const status = card.getAttribute("data-status");
		if (status === 'SOLD_OUT') {
			card.classList.add("sold-out");
		}
	});
	const menuForm = document.querySelector("#menuForm");
	if (menuForm) {
		menuForm.addEventListener("submit", event => {
			const formData = new FormData(menuForm);
			for (let pair of formData.entries()) {
				console.log(`${pair[0]} = ${pair[1]}`);
			}
		});
		menuForm.addEventListener("submit", handleSubmit);
	}
	const el = document.getElementById('menu-list-container');
	if (el) {
		Sortable.create(el, {
			animation: 150,
			onEnd: function (evt) {
				const newOrder = [...el.children].map((card, idx) => ({
					menuId: card.dataset.menuId,
					order: idx
				}));
				console.log("🔃 변경된 순서", newOrder);

				fetch('/owner/menu-reorder', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(newOrder)
				});
			}
		});
	}
});

window.openMenuModal = openMenuModal;
window.closeMenuModal = closeMenuModal;
window.openEditMenuModal = openEditMenuModal;
window.closeEditMenuModal = closeEditMenuModal;

window.openCategoryModal = openCategoryModal;
window.closeCategoryModal = closeCategoryModal;
window.addCategory = addCategory;
window.removeCategory = removeCategory;

window.addOptionField = addOptionField;
window.removeOption = removeOption;


