document.addEventListener('DOMContentLoaded', () => {
    console.log('currentStatus:', currentStatus);
    console.log('currentDays:', currentDays);
    console.log('currentUsage:', currentUsage);

    updateExcelDownloadUrl();
});

function filterByStatus(status) {
	const rows = document.querySelectorAll("#payHistoryTable tbody tr");
	rows.forEach(row => {
		const rowStatus = row.dataset.status;
		row.style.display = (status === 'ALL' || rowStatus === status) ? '' : 'none';
	});
}

function filterByDays(days) {
	const rows = document.querySelectorAll("#payHistoryTable tbody tr");
	const now = new Date();
	rows.forEach(row => {
		const requestedAt = new Date(row.dataset.requested);
		const diffDays = (now - requestedAt) / (1000 * 60 * 60 * 24);
		row.style.display = (diffDays <= days) ? '' : 'none';
	});
}

function filterByUsage(usageType) {
	const rows = document.querySelectorAll("#payHistoryTable tbody tr");
	rows.forEach(row => {
		const rowUsage = row.dataset.usage;
		row.style.display = (rowUsage === usageType) ? '' : 'none';
	});
}


// 필터 버튼 함수들
function filterByStatus(status) {
	currentStatus = status;
	currentDays = null;
	currentUsage = null;
	loadFilteredData();
}

function filterByDays(days) {
	currentDays = days;
	currentStatus = 'ALL';
	currentUsage = null;
	loadFilteredData();
}

function filterByUsage(usage) {
	currentUsage = usage;
	currentStatus = 'ALL';
	currentDays = null;
	loadFilteredData();
}

// 필터된 데이터를 로딩 (페이지 이동 또는 Ajax)
function loadFilteredData() {
	let url = `/userPayHistory?status=${currentStatus}`;

	if (currentDays !== null) {
		url += `&days=${currentDays}`;
	}
	if (currentUsage !== null) {
		url += `&usage=${currentUsage}`;
	}

	// 페이지 이동 방식 (또는 Ajax로 대체 가능)
	window.location.href = url;

	// 다운로드 버튼 링크 업데이트
	updateExcelDownloadUrl();
}

// 엑셀 다운로드 링크 업데이트
function updateExcelDownloadUrl() {
	const excelBtn = document.getElementById('excelDownloadBtn');
	if (!excelBtn) return;

	let url = `/userPayHistory/download?status=${currentStatus}`;

	if (currentDays !== null) {
		url += `&days=${currentDays}`;
	}
	if (currentUsage !== null) {
		url += `&usage=${currentUsage}`;
	}

	excelBtn.href = url;
}
