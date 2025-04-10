function toggleSidebar() {
	const sidebar = document.getElementById("sidebar");
	sidebar.classList.toggle("visible");
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

	return `${month}.${day} (${weekday}) ${ampm} ${hours}:${minutes}`;
}

document.addEventListener("DOMContentLoaded", () => {
	const now = new Date();
	const formatted = formatDate(now);
	document.getElementById("today-date").textContent = formatted;
});

