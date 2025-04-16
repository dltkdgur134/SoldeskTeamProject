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

function execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      const addr = data.roadAddress || data.jibunAddress;
      document.getElementById("address").value = addr;
    }
  }).open();
}

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
    domainInput.style.backgroundColor = '#dcdcdc';
  }

  const defaultOption = select.querySelector('option[value=""]');
  if (defaultOption) {
    select.removeChild(defaultOption);
  }
}

function getFullEmail() {
  const id = document.getElementById("email_id").value.trim();
  const domainInput = document.getElementById("email_domain_input").value.trim();

  return id + "@" + domainInput;
}

document.querySelector('[name="userPhone"]').addEventListener('input', function () {
  this.value = this.value.replace(/[^0-9\-]/g, '');
});

document.getElementById('profileImage').addEventListener('change', function () {
  const maxSize = 1024 * 1024;
  const maxWidth = 450;
  const maxHeight = 450;
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
    alert('파일 크기가 1MB를 초과했습니다. 다시 선택해주세요.');
    this.value = '';
    document.getElementById('file-name-display').textContent = '1MB 이하로 해주세요';
    return;
  }

  const img = new Image();
  const objectUrl = URL.createObjectURL(file);

  img.onload = function () {
    if (img.width > maxWidth || img.height > maxHeight) {
      alert(`이미지 크기는 ${maxWidth}x${maxHeight}px 이하여야 합니다.`);
      document.getElementById('profileImage').value = '';
      document.getElementById('file-name-display').textContent = '450x450 이하로 해주세요';
    } else {
      document.getElementById('file-name-display').textContent = file.name;
    }
    URL.revokeObjectURL(objectUrl);
  };

  img.src = objectUrl;
});

document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector("form");
  const password = document.getElementById("password");
  const confirmPassword = document.getElementById("confirmPassword");

  form.addEventListener("submit", function (e) {
    if (password.value !== confirmPassword.value) {
      e.preventDefault();
      alert("비밀번호가 일치하지 않습니다!");
      confirmPassword.focus();
      return;
    }

    const fullEmail = getFullEmail();
    if (!fullEmail.includes('@') || fullEmail.endsWith('@')) {
      e.preventDefault();
      alert("이메일 도메인을 입력해주세요.");
      return;
    }
    document.getElementById("full_email").value = fullEmail;

    const nickname = document.querySelector('[name="nickname"]').value.trim();
    if (nickname.length === 0) {
      e.preventDefault();
      alert("닉네임을 입력해주세요.");
      return;
    }

    const phoneInput = document.querySelector('[name="userPhone"]');
    phoneInput.value = phoneInput.value.replace(/-/g, '');
    if (!/^\d{10,11}$/.test(phoneInput.value)) {
      e.preventDefault();
      alert("전화번호는 10~11자리 숫자만 입력할 수 있습니다.");
      return;
    }

  });
});

// 이메일 js
let isSending = false;
function sendVerificationEmail() {
	if (isSending) return;
	isSending = true;
	
	const fullEmail = getFullEmail();
	let vaildCheck = fullEmail.split("@");
	
	if (vaildCheck.length !== 2 || vaildCheck[0] === "" || vaildCheck[1] === "") {
		alert("이메일을 올바르게 입력하세요.");
		isSending = false;
		return;
	}
	
	const sendBtn = document.querySelector("#verificationGroup button");

	startEmailCooldown(sendBtn);

	fetch(`/email/send?email=${encodeURIComponent(fullEmail)}`)
		.then(res => res.text())
		.then(msg => {
			alert(msg);
			document.getElementById('verificationGroup').style.display = 'flex';
		})
		.catch(err => {
			console.error(err);
			alert("이메일 전송 실패!");
			sendBtn.disabled = false;
			sendBtn.textContent = '인증 코드 전송';
		})
		.finally(() => {
			isSending = false; // 다시 가능하게
		});
}

let isVerifying = false;
function verifyCode() {
	if (isVerifying) return;
	isVerifying = true;

	const code = document.getElementById("emailCodeInput").value.trim();
	if (!code) {
		alert("인증 코드를 입력하세요.");
		isVerifying = false;
		return;
	}

	fetch(`/email/verify?code=${encodeURIComponent(code)}`)
		.then(res => res.text())
		.then(result => {
			if (result === "ok") {
				alert("이메일 인증 완료!");
				const group = document.getElementById('verificationGroup');
				group.style.display = 'none';
				document.getElementById('verificationSuccess').style.display = 'block';
				document.getElementById('email_id').readOnly = true;
				document.getElementById('email_id').style.backgroundColor = '#dcdcdc';
				document.getElementById('email_domain_input').readOnly = true;
				document.getElementById('email_domain_input').style.backgroundColor = '#dcdcdc';
				document.getElementById('email_domain_select').disabled = true;
				const successMsg = document.createElement('span');
				document.querySelector('.email-group').appendChild(successMsg);
			} else if (result === "expired") {
				alert("인증 코드가 만료되었습니다. 다시 시도하세요.");
			} else {
				alert("인증 코드가 틀렸습니다.");
			}
		})
		.finally(() => {
			isVerifying = false;
		});
}

function startEmailCooldown(button) {
	let seconds = 60;
	button.disabled = true;
	button.textContent = `${seconds}s`;

	const intervalId = setInterval(() => {
		seconds--;
		button.textContent = `${seconds}s`;

		if (seconds <= 0) {
			clearInterval(intervalId);
			button.disabled = false;
			button.textContent = '인증 코드 전송';
		}
	}, 1000);
}