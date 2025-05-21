function logout() {
	fetch('/admin/logout', {
		method: 'GET'
	})
	.then(() => {
		window.location.href = '/admin/login';
	})
	.catch(err => {
		console.error('로그아웃 실패:', err);
		alert('로그아웃에 실패했습니다.');
	});
}

function loadSetting(url, btn) {
	document.querySelectorAll(".tab-buttons button").forEach(b => b.classList.remove("active"));
	btn.classList.add("active");

	fetch(url)
		.then(res => res.text())
		.then(html => {
			document.getElementById("setting-content").innerHTML = html;

			if (url.includes("storeapproval")) {
				refreshPendingStores();

				document.querySelector(".close")?.addEventListener("click", () => {
					document.getElementById("store-modal").style.display = "none";
				});
			}
		})
		.catch(err => {
			document.getElementById("setting-content").innerHTML = '<p>로드에 실패했습니다.</p>';
			console.error(err);
		});
}

function formatDate(date) {
    const days = ['일', '월', '화', '수', '목', '금', '토'];
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const weekday = days[date.getDay()];
    let hours = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const isAM = hours < 12;
    const ampm = isAM ? '오전' : '오후';
    if (!isAM) hours = hours > 12 ? hours - 12 : hours;
    if (hours === 0) hours = 12;
    return `${new Date().getMonth() + 1}. ${day} (${weekday}) ${ampm} ${hours}:${minutes}`;
}

function updateTime() {
    const now = new Date();
    document.getElementById("today-date").textContent = formatDate(now);
}

function renderPendingStores(stores) {
	const container = document.getElementById("pending-store-list");
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
					<button onclick="approveStore('${store.storeId}')">승인</button>
					<button onclick="rejectStore('${store.storeId}')">거부</button>
				</span>
			</div>
		`;
	}).join("");
	
	document.querySelectorAll(".store-name").forEach(el => {
		el.addEventListener("click", () => {
			const store = JSON.parse(el.closest(".store-row").dataset.store.replace(/&apos;/g, "'"));
			openStoreModal(store);
		});
	});
}

function approveStore(id) {
	if (confirm("정말 승인하시겠습니까?")) {
		fetch(`/api/stores/${id}/approve`, { method: "POST" })
			.then(() => refreshPendingStores());
	}
}

function rejectStore(id) {
	if (confirm("정말 거부하시겠습니까?")) {
		fetch(`/api/stores/${id}/reject`, { method: "POST" })
			.then(() => refreshPendingStores());
	}
}

function refreshPendingStores() {
	fetch("/api/stores/pending")
		.then(res => res.json())
		.then(stores => renderPendingStores(stores))
		.catch(err => console.error("승인대기 가게 목록 실패:", err));
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

	document.getElementById("store-modal").style.display = "block";
}



document.addEventListener("DOMContentLoaded", () => {
	updateTime();
	setInterval(updateTime, 1000);
	const params = new URLSearchParams(window.location.search);
	const tab = params.get("tab");
	
	if (tab) {
		const buttonMap = {
			operation: 0,
			printer: 1,
			alarm: 2,
			delivery: 3
		};
		const btnIndex = buttonMap[tab];
		if (btnIndex !== undefined) {
			const buttons = document.querySelectorAll(".tab-buttons button");
			buttons[btnIndex].click(); // 자동 클릭
		}
	}
	
	document.querySelector(".close").onclick = () => {
		document.getElementById("store-modal").style.display = "none";
	};
});
