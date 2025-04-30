import { closeMenuModal } from './modal.js';
import { validateImageFile } from './utils.js';

export function handleSubmit(event) {
	const fileInput = document.querySelector("#menuForm input[type='file']");
	const isValid = validateImageFile(fileInput);

	if (!isValid) {
		event.preventDefault();
		console.log("❌ 이미지 유효성 검사 실패");
		return false;
	}

	closeMenuModal();
	return true;
}

export function deleteMenu(menuId) {
	if (!confirm("정말로 삭제하시겠습니까?")) return;

	const storeId = document.body.dataset.storeId;
	fetch(`/owner/storeManagement/${storeId}/menu-delete`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: new URLSearchParams({ menuId })
	})
	.then(() => {
		alert("메뉴가 삭제되었습니다.");
		location.reload();
	})
	.catch(() => {
		alert("메뉴 삭제에 실패했습니다.");
	});
}

export function setupEditFormValidation() {
	const form = document.getElementById("editMenuForm");
	if (form) {
		form.addEventListener("submit", function(e) {
			const fileInput = this.querySelector("input[name='menuImg']");
			if (!validateImageFile(fileInput)) {
				e.preventDefault();
			}
		});
	}
}

export function filterMenusByCategory(category) {
	const cards = document.querySelectorAll(".menu-card");
	cards.forEach(card => {
		const cat = card.getAttribute("data-category");
		card.style.display = (category === "전체" || cat === category) ? "" : "none";
	});
}



