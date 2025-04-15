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
        oncomplete: function(data) {
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('roadAddress').value = data.roadAddress || data.jibunAddress;
            document.getElementById('detailAddress').focus();

            getLatLngFromAddress(data.roadAddress || data.jibunAddress);
        }
    }).open();
}

function getLatLngFromAddress(address) {
    const geocoder = new kakao.maps.services.Geocoder();

    geocoder.addressSearch(address, function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            const lat = result[0].y;
            const lng = result[0].x;

            document.getElementById('latitude').value = lat;
            document.getElementById('longitude').value = lng;

            console.log("위도:", lat, "경도:", lng);
        } else {
            alert("좌표를 찾을 수 없습니다.");
        }
    });
}

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
    if (!/^\d{9,10}$/.test(phoneInput.value)) {
      e.preventDefault();
      alert("전화번호는 9~10자리 숫자만 입력할 수 있습니다.");
      return;
    }

  });
});


