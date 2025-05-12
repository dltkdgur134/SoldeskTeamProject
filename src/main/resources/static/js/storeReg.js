function updateFullAddress() {
	const road = document.getElementById("store_roadAddress").value.trim();
	const detail = document.getElementById("store_detailAddress").value.trim();
	document.getElementById("storeAddress").value = road + " " + detail;
}

function execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			document.getElementById('postcode').value = data.zonecode;
			document.getElementById('store_roadAddress').value = data.roadAddress || data.jibunAddress;
			document.getElementById('store_detailAddress').focus();

			getLatLngFromAddress(data.roadAddress || data.jibunAddress);
			updateFullAddress();
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
document.addEventListener("DOMContentLoaded", function () {
	document.getElementById("brandImg").addEventListener("change", function(e) {
		const file = e.target.files[0];
		const preview = document.getElementById("brandImgPreview");
		const label = document.getElementById("file-name-display");

		const maxSize = 1024 * 1024;
		const maxWidth = 750;
		const maxHeight = 750;
		const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif'];

		if (!file) {
			label.textContent = '선택된 파일 없음';
			return;
		}

		const ext = file.name.split('.').pop().toLowerCase();
		if (!allowedExtensions.includes(ext)) {
			alert('jpg, jpeg, png, gif 형식의 이미지만 업로드 가능합니다.');
			e.target.value = '';
			label.textContent = '이미지를 선택해주세요';
			preview.style.display = "none";
			return;
		}

		if (file.size > maxSize) {
			alert('파일 크기가 1MB를 초과했습니다. 다시 선택해주세요.');
			e.target.value = '';
			label.textContent = '1MB 이하로 해주세요';
			preview.style.display = "none";
			return;
		}

		const img = new Image();
		
		const objectUrl = URL.createObjectURL(file);
		img.onload = function () {
			if (img.width > maxWidth || img.height > maxHeight) {
				alert(`이미지 크기는 ${maxWidth}x${maxHeight}px 이하여야 합니다.`);
				e.target.value = '';
				label.textContent = '750x750 이하로 해주세요';
				preview.style.display = "none";
			} else {
				label.textContent = file.name;
				preview.src = objectUrl;
				preview.style.display = "block";
			}
			URL.revokeObjectURL(objectUrl);
		};
		img.src = objectUrl;
	});
	
	document.getElementById("store_detailAddress").addEventListener("input", updateFullAddress);

	const form = document.querySelector("form");

	form.addEventListener("submit", function (e) {
		updateFullAddress();
		const phoneInput = document.querySelector('[name="storePhone"]');
		phoneInput.value = phoneInput.value.replace(/-/g, '');
		if (!/^\d{9,11}$/.test(phoneInput.value)) {
			e.preventDefault();
			alert("전화번호는 9~11자리 숫자만 입력할 수 있습니다.");
			return;
		}

 	});
});




