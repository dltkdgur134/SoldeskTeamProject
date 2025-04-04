function togglePassword(inputId, iconElement) {
  const input = document.getElementById(inputId);
  if (input.type === "password") {
    input.type = "text";
    iconElement.textContent = "🙈";
  } else {
    input.type = "password";
    iconElement.textContent = "👁️";
  }
}

// 🏠 카카오 주소 검색
function execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      const addr = data.roadAddress || data.jibunAddress;
      document.getElementById("address").value = addr;
    }
  }).open();
}

// 📧 이메일 도메인 처리
function handleEmailDomainChange(select) {
  const domainInput = document.getElementById('email_domain_input');

  if (select.value === 'direct') {
    domainInput.value = '';
    domainInput.readOnly = false;
    domainInput.style.backgroundColor = '#fff';
    domainInput.focus();
  } else {
    domainInput.value = select.value;
    domainInput.readOnly = true;
    domainInput.style.backgroundColor = '#e9ecef';
  }

  const defaultOption = select.querySelector('option[value=""]');
  if (defaultOption) {
    select.removeChild(defaultOption);
  }
}

// 📧 이메일 완성
function getFullEmail() {
  const id = document.getElementById("email_id").value.trim();
  const select = document.getElementById("email_domain");
  let domain = "";

  if (select.value === "direct") {
    domain = document.getElementById("email_domain_input").value.trim();
  } else {
    domain = select.value;
  }

  return id + "@" + domain;
}

// 📞 전화번호 입력 제한 (숫자/하이픈만)
document.querySelector('[name="userPhone"]').addEventListener('input', function () {
  this.value = this.value.replace(/[^0-9\-]/g, '');
});

// 🖼️ 파일 업로드 검사
document.getElementById('profileImage').addEventListener('change', function () {
  const maxSize = 700 * 1024;
  const maxWidth = 300;
  const maxHeight = 300;
  const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif'];
  const file = this.files[0];

  if (!file) {
    document.getElementById('file-name-display').textContent = '선택된 파일 없음';
    return;
  }

  const fileExtension = file.name.split('.').pop().toLowerCase();
  if (!allowedExtensions.includes(fileExtension)) {
    alert('jpg, jpeg, png, gif 형식의 이미지만 업로드 가능합니다.');
    this.value = '';
    document.getElementById('file-name-display').textContent = '이미지를 선택해주세요';
    return;
  }

  if (file.size > maxSize) {
    alert('파일 크기가 700KB를 초과했습니다. 다시 선택해주세요.');
    this.value = '';
    document.getElementById('file-name-display').textContent = '700KB 이하로 해주세요';
    return;
  }

  const img = new Image();
  const objectUrl = URL.createObjectURL(file);

  img.onload = function () {
    if (img.width > maxWidth || img.height > maxHeight) {
      alert(`이미지 크기는 ${maxWidth}x${maxHeight}px 이하여야 합니다.`);
      document.getElementById('profileImage').value = '';
      document.getElementById('file-name-display').textContent = '300x300 이하로 해주세요';
    } else {
      document.getElementById('file-name-display').textContent = file.name;
    }
    URL.revokeObjectURL(objectUrl);
  };

  img.src = objectUrl;
});

// ✅ 전체 유효성 검사 처리
document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector("form");
  const password = document.getElementById("password");
  const confirmPassword = document.getElementById("confirmPassword");

  form.addEventListener("submit", function (e) {
    // 1. 비밀번호 확인
    if (password.value !== confirmPassword.value) {
      e.preventDefault();
      alert("비밀번호가 일치하지 않습니다!");
      confirmPassword.focus();
      return;
    }

    // 2. 이메일 검사
    const fullEmail = getFullEmail();
    if (!fullEmail.includes('@') || fullEmail.endsWith('@')) {
      e.preventDefault();
      alert("이메일 도메인을 입력해주세요.");
      return;
    }
    document.getElementById("full_email").value = fullEmail;

    // 3. 닉네임 공백 방지
    const nickname = document.querySelector('[name="nickname"]').value.trim();
    if (nickname.length === 0) {
      e.preventDefault();
      alert("닉네임을 입력해주세요.");
      return;
    }

    // 4. 전화번호 하이픈 제거 및 검사
    const phoneInput = document.querySelector('[name="userPhone"]');
    phoneInput.value = phoneInput.value.replace(/-/g, '');
    if (!/^\d{10,11}$/.test(phoneInput.value)) {
      e.preventDefault();
      alert("전화번호는 10~11자리 숫자만 입력할 수 있습니다.");
      return;
    }

    // 5. 주소 필수 확인 (readonly 라서 required 작동 안함)
    const address = document.getElementById("address").value.trim();
    if (address === "") {
      e.preventDefault();
      alert("주소를 입력해주세요.");
      return;
    }
  });
});