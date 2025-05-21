export function initUserSetting() {
	let currentPage = 1;
	const pageSize = 10;

	const roleSelect = document.getElementById("filter-role");
	const statusSelect = document.getElementById("filter-status");

	// 초기 로드
	loadUsers(currentPage);

	// 필터 변경 시
	roleSelect.addEventListener("change", () => loadUsers(1));
	statusSelect.addEventListener("change", () => loadUsers(1));
	
	const closeBtn = document.querySelector("#user-edit-modal .close");
	if (closeBtn) {
		closeBtn.addEventListener("click", () => {
			document.getElementById("user-edit-modal").style.display = "none";
		});
	}
	
	document.addEventListener("keydown", (e) => {
		if (e.key === "Escape") {
			const modal = document.getElementById("user-edit-modal");
			if (modal.style.display === "flex") {
				modal.style.display = "none";
			}
		}
	});
	
	window.addEventListener("click", (e) => {
		const modal = document.getElementById("store-modal");
		if (e.target === modal) {
			modal.style.display = "none";
		}
	});
	
	document.getElementById("save-user-btn").addEventListener("click", saveUserEdit);
}

function loadUsers(page) {
	const role = document.getElementById("filter-role").value;
	const status = document.getElementById("filter-status").value;

	fetch(`/api/admin/users?page=${page - 1}&size=10&role=${role}&status=${status}`)
		.then(res => res.json())
		.then(data => {
			renderUsers(data.content);
			renderPagination(data.pageable.pageNumber + 1, data.totalPages);
			document.getElementById("user-count").textContent = data.totalElements;
		})
		.catch(err => console.error("유저 목록 불러오기 실패:", err));
}

function renderUsers(users) {
	const tbody = document.querySelector("#user-list");
	tbody.innerHTML = users.map(user => `
		<tr>
			<td><button class="edit-btn" data-id="${user.userUuid}">수정</button></td>
			<td>${user.createdDate?.split("T")[0] ?? "-"}</td>
			<td>${user.userId}</td>
			<td>${user.userName}</td>
			<td>${user.nickName}</td>
			<td>${user.email}</td>
			<td>${user.userPhone}</td>
			<td>${user.userRole}</td>
			<td>${user.userStatus}</td>
			<td>${user.socialLoginProvider}</td>
		</tr>
	`).join("");

	tbody.querySelectorAll(".edit-btn").forEach(btn => {
		btn.addEventListener("click", () => {
			const userId = btn.dataset.id;
			openEditModal(userId);
		});
	});
}

function renderPagination(current, total) {
	const container = document.getElementById("pagination");
	let html = "";

	if (current > 1) html += `<button onclick="loadUsers(${current - 1})">이전</button>`;

	for (let i = 1; i <= total; i++) {
		html += `<button class="${i === current ? 'active' : ''}" onclick="loadUsers(${i})">${i}</button>`;
	}

	if (current < total) html += `<button onclick="loadUsers(${current + 1})">다음</button>`;

	container.innerHTML = html;
}

function openEditModal(userId) {
	fetch(`/api/admin/users/${userId}`)
		.then(res => res.json())
		.then(user => {
			document.getElementById("edit-nickname").value = user.nickName;
			document.getElementById("edit-userRole").value = user.userRole;
			document.getElementById("edit-userStatus").value = user.userStatus;
			document.getElementById("edit-user-id").value = user.userUuid;
			document.getElementById("user-edit-modal").style.display = "flex";
		});
}

window.saveUserEdit = function () {
	const id = document.getElementById("edit-user-id").value;
	const payload = {
		nickName: document.getElementById("edit-nickname").value,
		userRole: document.getElementById("edit-userRole").value,
		userStatus: document.getElementById("edit-userStatus").value
	};

	fetch(`/api/admin/users/${id}`, {
		method: "PUT",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(payload)
	})
		.then(() => {
			// 모달 닫기
			document.getElementById("user-edit-modal").style.display = "none";

			// 갱신된 데이터 받아오기
			return fetch(`/api/admin/users/${id}`).then(res => res.json());
		})
		.then(updatedUser => {
			const row = document.querySelector(`.edit-btn[data-id="${id}"]`)?.closest("tr");
			if (row) {
				row.innerHTML = `
					<td><button class="edit-btn" data-id="${updatedUser.userUuid}">수정</button></td>
					<td>${updatedUser.createdDate?.split("T")[0] ?? "-"}</td>
					<td>${updatedUser.userId}</td>
					<td>${updatedUser.userName}</td>
					<td>${updatedUser.nickName}</td>
					<td>${updatedUser.email}</td>
					<td>${updatedUser.userPhone}</td>
					<td>${updatedUser.userRole}</td>
					<td>${updatedUser.userStatus}</td>
					<td>${updatedUser.socialLoginProvider}</td>
				`;
				// 수정 버튼 다시 이벤트 바인딩
				row.querySelector(".edit-btn").addEventListener("click", () => openEditModal(updatedUser.userUuid));
			}
		})
		.catch(err => alert("수정 실패: " + err));
};

window.loadUsers = loadUsers;


