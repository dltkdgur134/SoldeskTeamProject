export function initStoreApproval() {
	refreshPendingStores();

	document.querySelector(".close")?.addEventListener("click", () => {
		document.getElementById("store-modal").style.display = "none";
	});
	
	document.addEventListener("keydown", (e) => {
		if (e.key === "Escape") {
			document.getElementById("store-modal").style.display = "none";
		}
	});
	
	window.addEventListener("click", (e) => {
		const modal = document.getElementById("store-modal");
		if (e.target === modal) {
			modal.style.display = "none";
		}
	});
}

function renderPendingStores(stores) {
	const container = document.getElementById("pending-store-list");
	const countSpan = document.getElementById("store-count");
	countSpan.textContent = `(${stores.length}개)`;
	container.innerHTML = stores.map(store => {
		const dateStr = store.registrationDate?.split("T")[0] || "날짜 없음";

		return `
			<div class="store-row"
				style="display: flex; justify-content: space-between; align-items: center; padding: 10px; border-bottom: 1px solid #ddd;"
				data-store='${JSON.stringify(store).replace(/'/g, "&apos;")}'>
				
				<span class="store-name" style="cursor:pointer; color: blue;">
					📍 ${dateStr} - <strong>${store.storeName}</strong>
				</span>

				<span>
					<button class="approve-btn" data-id="${store.storeId}">승인</button>
					<button class="reject-btn" data-id="${store.storeId}">거부</button>
				</span>
			</div>
		`;
	}).join("");

	container.querySelectorAll(".approve-btn").forEach(btn => {
		btn.addEventListener("click", () => {
			const id = btn.dataset.id;
			approveStore(id);
		});
	});

	container.querySelectorAll(".reject-btn").forEach(btn => {
		btn.addEventListener("click", () => {
			const id = btn.dataset.id;
			rejectStore(id);
		});
	});

	container.querySelectorAll(".store-name").forEach(el => {
		el.addEventListener("click", () => {
			const store = JSON.parse(el.closest(".store-row").dataset.store.replace(/&apos;/g, "'"));
			openStoreModal(store);
		});
	});
}

function refreshPendingStores() {
	fetch("/api/admin/stores/pending")
		.then(res => res.json())
		.then(stores => renderPendingStores(stores))
		.catch(err => console.error("승인대기 가게 목록 실패:", err));
}

function approveStore(id) {
	if (confirm("정말 승인하시겠습니까?")) {
		fetch(`/api/admin/stores/${id}/approve`, { method: "POST" })
			.then(() => refreshPendingStores());
	}
}

function rejectStore(id) {
	if (confirm("정말 거부하시겠습니까?")) {
		fetch(`/api/admin/stores/${id}/reject`, { method: "POST" })
			.then(() => refreshPendingStores());
	}
}

function openStoreModal(store) {
	const parsed = typeof store === "string" ? JSON.parse(decodeURIComponent(store)) : store;
	document.getElementById("modal-store-name").textContent = parsed.storeName;
	document.getElementById("modal-biz-num").textContent = parsed.businessNum;
	document.getElementById("modal-phone").textContent = parsed.storePhone;
	document.getElementById("modal-owner-id").textContent = parsed.ownerId;
	document.getElementById("modal-category").textContent = parsed.category;
	document.getElementById("modal-address").textContent = parsed.storeAddress;
	document.getElementById("modal-img").src = parsed.brandImg || "/img/store/default.png";

	document.getElementById("store-modal").style.display = "flex";
}


