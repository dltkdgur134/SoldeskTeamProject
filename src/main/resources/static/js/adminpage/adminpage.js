import { loadOwnerRiderApprovals } from './ownerRiderApproval.js';

function logout() {
	fetch('/admin/logout', { method: 'GET' })
		.then(() => window.location.href = '/admin/login')
		.catch(err => {
			console.error('로그아웃 실패:', err);
			alert('로그아웃에 실패했습니다.');
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
	return `${month}. ${day} (${weekday}) ${ampm} ${hours}:${minutes}`;
}

function updateTime() {
	const now = new Date();
	document.getElementById("today-date").textContent = formatDate(now);
}

export function loadSetting(url, btn) {
	document.querySelectorAll(".tab-buttons button").forEach(b => b.classList.remove("active"));
	btn.classList.add("active");

	fetch(url)
		.then(res => res.text())
		.then(html => {
			document.getElementById("setting-content").innerHTML = html;

			// ✅ 각 탭에 맞는 js 모듈 동적 import
			if (url.includes("storeapproval")) {
				import('/js/adminpage/storeapproval.js').then(module => {
					module.initStoreApproval();
				});
			} else if (url.includes("usersetting")) {
				import('/js/adminpage/usersetting.js').then(module => {
					module.initUserSetting();
				});
			} else if (url.includes("ownerRiderapproval")) {
				import('/js/adminpage/ownerRiderApproval.js').then(module => {
					module.loadOwnerRiderApprovals();
				});
			} else if (url.includes("customerService")) {
				
			}
		})
		.catch(err => {
			document.getElementById("setting-content").innerHTML = '<p>로드에 실패했습니다.</p>';
			console.error(err);
		});
}

document.addEventListener("DOMContentLoaded", () => {
	updateTime();
	setInterval(updateTime, 1000);

	document.querySelectorAll(".tab-buttons button").forEach(btn => {
		btn.addEventListener("click", () => {
			const url = btn.getAttribute("data-url");
			if (url) loadSetting(url, btn);
		});
	});

	// URL 파라미터 탭 처리
	const params = new URLSearchParams(window.location.search);
	const tab = params.get("tab");

	if (tab) {
		const buttonMap = {
			operation: "usersetting",
			printer: "storeapproval",
			alarm: "ownerRiderApproval",
			delivery: "customerService"
		};
		const urlPart = buttonMap[tab];
		if (urlPart) {
			const btn = [...document.querySelectorAll(".tab-buttons button")]
				.find(b => b.getAttribute("data-url")?.includes(urlPart));
			if (btn) btn.click();
		}
	}
	
	const currentUrl = window.location.pathname;
	
	if (currentUrl.includes('approval')) {
		loadOwnerRiderApprovals();
	}
});

window.logout = logout;

