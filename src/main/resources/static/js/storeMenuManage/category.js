import { filterMenusByCategory } from './menuForm.js';

export const dynamicCategories = new Map();

export function openCategoryModal() {
	const modal = document.getElementById("categoryModal");
	const list = document.getElementById("categoryList");
	list.innerHTML = "";

	for (const [categoryName, categoryId] of dynamicCategories.entries()) {
		const li = document.createElement("li");		
		li.setAttribute('data-category-id', categoryId);
		
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
						const entries = Array.from(dynamicCategories.entries()).filter(([_, id]) => id !== categoryId);
						dynamicCategories.clear();
						entries.forEach(([name, id]) => dynamicCategories.set(name, id));
						dynamicCategories.set(updated.categoryName.trim(), updated.id);
						
						initCategoryButtons();
						openCategoryModal();
					})
					.catch(() => {
						alert("카테고리 수정에 실패했습니다.");
					});
				}
			};
	
			deleteBtn.onclick = () => {
				fetch(`/api/category/${categoryId}/has-menu`)
					.then(res => res.json())
					.then(hasMenu => {
						if (hasMenu) {
							alert("❌ 해당 카테고리에 등록된 메뉴가 있어 삭제할 수 없습니다.");
							return;
						}

						if (!confirm(`정말로 카테고리 "${categoryName}"을 삭제하시겠습니까?`)) return;

						fetch(`/api/category/${categoryId}`, { method: 'DELETE' })
							.then(() => {
								dynamicCategories.delete(categoryName);
								initCategoryButtons();
								openCategoryModal();
							})
							.catch(() => {
								alert("카테고리 삭제에 실패했습니다.");
							});
					})
					.catch(() => {
						alert("카테고리 메뉴 조회에 실패했습니다.");
					});
			};
		}

		li.appendChild(input);
		li.appendChild(editBtn);
		li.appendChild(deleteBtn);
		list.appendChild(li);
	}

	modal.style.display = "flex";
	enableCategoryDrag();
}

export function closeCategoryModal() {
	document.getElementById("categoryModal").style.display = "none";
}

export function addCategory() {
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
		dynamicCategories.clear();
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

export function removeCategory(category) {
	dynamicCategories.delete(category);
	initCategoryButtons();
	openCategoryModal();
}

/*export function initCategoryButtons() {
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
}*/

export function initCategoryButtons() {
	const categoryContainer = document.getElementById("category-buttons");
	categoryContainer.innerHTML = "";

	const 전체버튼 = document.createElement("button");
	전체버튼.className = "tab active";
	전체버튼.innerText = "전체";
	전체버튼.addEventListener("click", () => {
		document.querySelectorAll(".tab").forEach(b => b.classList.remove("active"));
		전체버튼.classList.add("active");
		filterMenusByCategory("전체");
	});
	categoryContainer.appendChild(전체버튼);

	window.categoryList.forEach(cat => {
		const btn = document.createElement("button");
		btn.className = "tab";
		btn.innerText = cat.categoryName;
		btn.addEventListener("click", () => {
			document.querySelectorAll(".tab").forEach(b => b.classList.remove("active"));
			btn.classList.add("active");
			filterMenusByCategory(cat.categoryName);
		});
		categoryContainer.appendChild(btn);
	});

	const manageBtn = document.createElement("button");
	manageBtn.className = "tab manage-category-btn";
	manageBtn.innerText = "카테고리 관리";
	manageBtn.addEventListener("click", openCategoryModal);
	categoryContainer.appendChild(manageBtn);
}

export function saveDynamicCategories() {
	localStorage.setItem("dynamicCategories", JSON.stringify([...dynamicCategories]));
}

export function renderCategorySelect() {
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

export function renderEditCategorySelect(selectedId = "") {
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

export function enableCategoryDrag() {
	const list = document.getElementById("categoryList");

	Sortable.create(list, {
		animation: 150,
		onEnd: () => {
			const newOrder = [...list.children].map((li, index) => {
				const categoryId = li.getAttribute('data-category-id');
				
				if (!categoryId) {
					console.warn(`❗ 카테고리 ID가 비어 있습니다.`);
					return;
				}
				
				return { id: categoryId, order: index };
			}).filter(item => item !== null);

			console.log("🔁 새로운 카테고리 순서:", newOrder);

			fetch('/api/category/reorder', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(newOrder)
			});
		}
	});
}

export function initCategoryData() {
	const storeId = document.body.dataset.storeId;

	fetch(`/api/category/list?storeId=${storeId}`)
		.then(res => res.json())
		.then(data => {
			window.categoryList = data;
			dynamicCategories.clear();
			data.forEach(cat => {
				dynamicCategories.set(cat.categoryName.trim(), cat.id);
			});
			initCategoryButtons(); // ✅ 버튼도 여기서 생성
		})
		.catch(() => {
			alert("카테고리 목록을 불러오는 데 실패했습니다.");
		});
}



