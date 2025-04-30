const maxOption = 3;
let addOptionCount = { value: 0 };
let editOptionCount = { value: 0 };
let dynamicCategories = new Map();

//////////////////////////////////// 카테고리 /////////////////////////////////////////////////////

function openCategoryModal() {
	const modal = document.getElementById("categoryModal");
	const list = document.getElementById("categoryList");
	list.innerHTML = "";

	for (const [categoryName, categoryId] of dynamicCategories.entries()) {
		const li = document.createElement("li");
		li.style.display = "flex";
		li.style.alignItems = "center";
		li.style.gap = "10px";

		const input = document.createElement("input");
		input.type = "text";
		input.className = "category-input";
		input.readOnly = true;
		input.value = typeof categoryName === 'string' && categoryName.trim() !== "" ? categoryName : "(이름 없음)";

		const editBtn = document.createElement("button");
		editBtn.innerText = "수정";

		const deleteBtn = document.createElement("button");
		deleteBtn.innerText = "삭제";

		if (categoryName === "메인") {
			// ❌ '메인'은 수정/삭제 불가
			editBtn.disabled = true;
			deleteBtn.disabled = true;
		} else {
			
			editBtn.onclick = () => {
				input.readOnly = !input.readOnly;
				if (!input.readOnly) {
					input.focus();
					editBtn.innerText = "저장";
				} else {
					const newName = input.value.trim();
					if (!newName || newName === "전체") {
						alert("카테고리 이름이 비어있거나 전체로 수정할 수 없습니다.");
						input.value = categoryName;
						return;
					}
					fetch(`/api/category/${categoryId}`, {
						method: 'PUT',
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						body: new URLSearchParams({ name: newName })
					})
					.then(res => res.json())
					.then(updated => {
						const newMap = new Map();
						for (const [name, id] of dynamicCategories.entries()) {
							if (id !== categoryId) {
								newMap.set(name, id);
							}
						}
						newMap.set(updated.categoryName.trim(), updated.id);
						dynamicCategories = newMap;
						initCategoryButtons();
						openCategoryModal();
					})
					.catch(() => {
						alert("카테고리 수정에 실패했습니다.");
					});
				}
			};
	
			deleteBtn.onclick = () => {
				fetch(`/api/category/${categoryId}`, { method: 'DELETE' })
					.then(() => {
						dynamicCategories.delete(categoryName);
						initCategoryButtons();
						openCategoryModal();
					})
					.catch(() => {
						alert("카테고리 삭제에 실패했습니다.");
					});
			};
		}

		li.appendChild(input);
		li.appendChild(editBtn);
		li.appendChild(deleteBtn);
		list.appendChild(li);
	}

	modal.style.display = "flex";
}

function closeCategoryModal() {
	document.getElementById("categoryModal").style.display = "none";
}

function addCategory() {
	const input = document.getElementById("newCategoryInput");
	const value = input.value.trim();
	const storeId = document.body.dataset.storeId;

	if (!value || value === "전체") {
		alert("올바른 카테고리 이름을 입력하세요.");
		return;
	}

	fetch('/api/category/add', {
		method: 'POST',
		headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
		body: new URLSearchParams({
			name: value,
			storeId: storeId
		})
	})
	.then(res => res.json())
	.then(newCategory => {
		window.categoryList.push({
			categoryName: newCategory.categoryName,
			id: newCategory.id
		});
		dynamicCategories = new Map();
		window.categoryList.forEach(cat => {
			dynamicCategories.set(cat.categoryName.trim(), cat.id);
		});
		initCategoryButtons();
		openCategoryModal();
	})
	.catch(() => {
		alert("카테고리 추가 중 문제가 발생했습니다.");
	});
}

function removeCategory(category) {
	dynamicCategories.delete(category);
	initCategoryButtons();
	openCategoryModal();
}

function initCategoryButtons() {
	const uniqueCategories = new Set(["전체"]);
	window.menuList.forEach(menu => {
		if (menu.menuCategory && menu.menuCategory.trim() !== "") {
			uniqueCategories.add(menu.menuCategory.trim());
		}
	});
	dynamicCategories.forEach((id, name) => uniqueCategories.add(name));

	const categoryContainer = document.getElementById("category-buttons");
	categoryContainer.innerHTML = "";

	uniqueCategories.forEach(category => {
		const btn = document.createElement("button");
		btn.className = "tab";
		if (category === "전체") btn.classList.add("active");
		btn.innerText = category;
		btn.addEventListener("click", () => {
			document.querySelectorAll(".tab").forEach(b => b.classList.remove("active"));
			btn.classList.add("active");
			filterMenusByCategory(category);
		});
		categoryContainer.appendChild(btn);
	});

	const manageBtn = document.createElement("button");
	manageBtn.className = "tab manage-category-btn";
	manageBtn.innerText = "카테고리 관리";
	manageBtn.addEventListener("click", openCategoryModal);
	categoryContainer.appendChild(manageBtn);
}

function saveDynamicCategories() {
	localStorage.setItem("dynamicCategories", JSON.stringify([...dynamicCategories]));
}

///////////////////////////////////////////// 옵션 ////////////////////////////////////////////////////

function addOptionField(containerId, btnId, countRef) {
	if (countRef.value >= maxOption) {
		alert("옵션은 최대 3개까지 추가할 수 있습니다.");
		return;
	}

	countRef.value++;

	const div = document.createElement("div");
	div.className = "option-group";
	div.innerHTML = `
		<label>옵션 ${countRef.value}</label><br>
		<input type="text" name="menuOptions${countRef.value}[]" placeholder="옵션${countRef.value} 이름" required />
		<input type="number" name="menuOptions${countRef.value}Price[]" placeholder="옵션${countRef.value} 가격" required />
		<button type="button" onclick="removeOption(this)">삭제</button>
	`;

	document.getElementById(containerId).appendChild(div);

	if (countRef.value >= maxOption) {
		document.getElementById(btnId).disabled = true;
	}
}

function removeOption(button) {
	const group = button.closest(".option-group");
	const container = group.parentElement;
	group.remove();

	if (container.id === "option1-container") {
		addOptionCount.value--;
		document.getElementById("add-option-btn").disabled = false;
	} else if (container.id === "edit-option-container") {
		editOptionCount.value--;
		document.getElementById("edit-add-option-btn").disabled = false;
	}
}

////////////////////////////////////////// 메뉴 모달 //////////////////////////////////////////////////

function openMenuModal() {
	document.getElementById("menuModal").style.display = "flex";
}

function closeMenuModal() {
	document.getElementById("menuModal").style.display = "none";
}

function openEditMenuModal(menuId) {
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

function closeEditMenuModal() {
	document.getElementById('editMenuModal').style.display = 'none';
}

/////////////////////////////////////////////// 메뉴 등록, 수정, 삭제 /////////////////////////////////////////////////

function handleSubmit(event) {
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

function deleteMenu(menuId) {
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

///////////////////////////////////////////////// 카테고리 셀렉트 렌더링 //////////////////////////////////////////////////////

function validateImageFile(fileInput) {
	const file = fileInput.files[0];
	if (!file) return true;

	const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
	const maxSize = 1024 * 1024;

	if (!validTypes.includes(file.type)) {
		alert("이미지 파일만 업로드할 수 있습니다. (jpg, png, gif, webp)");
		return false;
	}

	if (file.size > maxSize) {
		alert("이미지 크기는 1MB 이하만 가능합니다.");
		return false;
	}

	return true;
}

function filterMenusByCategory(category) {
	const cards = document.querySelectorAll(".menu-card");
	cards.forEach(card => {
		const cat = card.getAttribute("data-category");
		card.style.display = (category === "전체" || cat === category) ? "" : "none";
	});
}

/////////////////////////////////////////////////// 필터 이미지 검사 //////////////////////////////////////////////////////

function renderCategorySelect() {
	const select = document.getElementById("menuCategory");
	select.innerHTML = ""; // 초기화

	for (const [name, id] of dynamicCategories.entries()) {
		const option = document.createElement("option");
		option.value = id;
		option.innerText = name;
		if (name === "메인") option.selected = true;
		select.appendChild(option);
	}
}

function renderEditCategorySelect(selectedId = "") {
	console.log("🟡 selectedId:", selectedId);
	console.log("🟢 dynamicCategories:", dynamicCategories);
	const select = document.getElementById("editMenuCategory");
	select.innerHTML = "";

	for (const [name, id] of dynamicCategories.entries()) {
			const option = document.createElement("option");
			option.value = id;
			option.innerText = name;

			if (String(id) === String(selectedId)) {
				option.selected = true;
				console.log("✅ 선택됨:", id, name);
			}

			select.appendChild(option);
		}
	select.value = String(selectedId);
}

///////////////////////////////////////////////////// 이벤트 핸들러 목록 ////////////////////////////////////////////////////

["editMenuForm"].forEach(formId => {
	document.getElementById(formId).addEventListener("submit", function(e) {
		const fileInput = this.querySelector("input[name='menuImg']");
		if (!validateImageFile(fileInput)) {
			e.preventDefault();
		}
	});
});

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

document.addEventListener("DOMContentLoaded", async () => {
	const storeId = document.body.dataset.storeId;
	
	dynamicCategories = new Map();
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

