let selectedComplainId = null;

function openComplain(link) {
  const hasPassword = link.dataset.password === 'true';
  const complainId = link.dataset.id;

  if (hasPassword) {
    const form = document.getElementById('passwordForm');
    form.action = `/complains/${complainId}/checkPassword`;  // 동적으로 action 설정
    document.getElementById('passwordModal').style.display = 'block';
  } else {
    window.location.href = `/complains/${complainId}`;
  }
  return false;  // 클릭 기본 동작 차단
}

function closePasswordModal() {
  document.getElementById('passwordModal').style.display = 'none';
}

function openResolvedComplain(link) {
  const hasPassword = link.dataset.password === 'true';
  const complainId = link.dataset.id;

  if (hasPassword) {
    const form = document.getElementById('passwordForm');
    form.action = `/complains/${complainId}/checkPassword`; // 비번 검사 후 리다이렉트 필요
    document.getElementById('passwordModal').style.display = 'block';
  } else {
    window.location.href = `/complains/complainReply/${complainId}`;
  }
  return false;
}