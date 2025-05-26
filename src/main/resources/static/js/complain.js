let selectedComplainId = null;

  function openComplain(link) {
    const hasPassword = link.dataset.password === 'true';
    const complainId = link.dataset.id;

    if (hasPassword) {
      selectedComplainId = complainId;
      document.getElementById('passwordModal').style.display = 'block';
    } else {
      window.location.href = `/complains/${complainId}`;
    }
  }

  function submitPassword() {
    const inputPassword = document.getElementById('complainPasswordInput').value;
    if (!inputPassword) {
      alert('비밀번호를 입력해주세요');
      return;
    }
    document.getElementById('passwordModal').style.display = 'none';
    window.location.href = `/complains/${selectedComplainId}?password=${inputPassword}`;
  }