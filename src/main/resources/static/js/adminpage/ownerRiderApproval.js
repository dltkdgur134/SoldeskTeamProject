export function loadOwnerRiderApprovals() {
	fetch('/api/admin/requests/owner')
		.then(res => res.json())
		.then(data => renderRequestList('owner-request-list', data, 'owner'));

	fetch('/api/admin/requests/rider')
		.then(res => res.json())
		.then(data => renderRequestList('rider-request-list', data, 'rider'));
}

function renderRequestList(containerId, users, type) {
	const container = document.getElementById(containerId);
	container.innerHTML = users.map(user => `
		<tr>
			<td>${user.createdDate || '-'}</td>
			<td>${user.userId}</td>
			<td>${user.userName}</td>
			<td>${user.nickName}</td>
			<td>${user.userRole}</td>
			<td>${user.userStatus}</td>
			<td>
				<button onclick="approveUser('${user.userUuid}', '${type}')">승인</button>
				<button onclick="rejectUser('${user.userUuid}', '${type}')">거부</button>
			</td>
		</tr>
	`).join('');
}

window.approveUser = function(uuid, type) {
	fetch(`/api/admin/requests/approve/${uuid}/${type}`, { method: 'POST' })
		.then(() => loadOwnerRiderApprovals());
};

window.rejectUser = function(uuid, type) {
	fetch(`/api/admin/requests/reject/${uuid}/${type}`, { method: 'POST' })
		.then(() => loadOwnerRiderApprovals());
};