function execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function (data) {
			const roadAddr = data.roadAddress;
			const zonecode = data.zonecode;

			document.getElementById('postcode').value = zonecode;
			document.getElementById('store_roadAddress').value = roadAddr;
			document.getElementById('store_detailAddress').value = '';
			document.getElementById('store_detailAddress').focus();

			const geocoder = new kakao.maps.services.Geocoder();
			geocoder.addressSearch(roadAddr, function (result, status) {
				if (status === kakao.maps.services.Status.OK) {
					document.getElementById('latitude').value = result[0].y;
					document.getElementById('longitude').value = result[0].x;
				} else {
					console.error('좌표를 가져오지 못했습니다.');
				}
			});
		}
	}).open();
}

function setupAddressAutoFill() {
	const detailAddressInput = document.getElementById('store_detailAddress');
	const roadAddressInput = document.getElementById('store_roadAddress');
	const storeInfoForm = document.getElementById('store-info-form');

	if (detailAddressInput && roadAddressInput) {
		detailAddressInput.addEventListener('input', () => {
			const roadAddress = roadAddressInput.value.trim();
			const detailAddress = detailAddressInput.value.trim();
			if (roadAddress) {
				document.getElementById('storeAddress').value = `${roadAddress} ${detailAddress}`.trim();
			}
		});
		roadAddressInput.addEventListener('input', () => {
			const roadAddress = roadAddressInput.value.trim();
			if (roadAddress) {
				const geocoder = new kakao.maps.services.Geocoder();
				geocoder.addressSearch(roadAddress, (result, status) => {
					if (status === kakao.maps.services.Status.OK) {
						document.getElementById('latitude').value = result[0].y;
						document.getElementById('longitude').value = result[0].x;
					}
				});
			}
		});
	}

	if (storeInfoForm) {
		storeInfoForm.addEventListener('submit', function (event) {
			const roadAddress = roadAddressInput.value.trim();
			const detailAddress = detailAddressInput.value.trim();
			const existingAddress = document.getElementById('current-address').value.trim();

			document.getElementById('storeAddress').value = roadAddress
				? `${roadAddress} ${detailAddress}`.trim()
				: existingAddress;
		});
	}
}

function setupFileNameDisplay(inputEl, labelElId) {
	const labelEl = document.getElementById(labelElId);
	if (!inputEl || !labelEl) return;

	inputEl.addEventListener('change', function () {
		if (this.files.length > 0) {
			const names = Array.from(this.files).map(file => file.name);
			labelEl.textContent = names.length === 1 ? names[0] : `${names[0]} 외 ${names.length - 1}개`;
		} else {
			labelEl.textContent = '선택된 파일 없음';
		}
	});
}

function setupUpdatedImgPreview(inputEl) {
	const idSuffix = inputEl.id.replace("storeImgInput_", "");
	const nameSpan = document.getElementById("storeImgName_" + idSuffix);
	const changeBtn = document.getElementById("changeBtn_" + idSuffix);

	inputEl.addEventListener('change', function () {
		if (this.files.length > 0) {
			nameSpan.textContent = this.files[0].name;
			changeBtn.disabled = false;
			changeBtn.style.backgroundColor = "#FFC107";
			changeBtn.style.cursor = "pointer";
		} else {
			nameSpan.textContent = "선택된 파일 없음";
			changeBtn.disabled = true;
			changeBtn.style.backgroundColor = "gray";
			changeBtn.style.cursor = "not-allowed";
		}
	});
}

function setupBrandImgPreview() {
	const brandImgInput = document.getElementById('brandImgInput');
	const brandImgName = document.getElementById('brandImgName');

	if (!brandImgInput || !brandImgName) return;

	brandImgInput.addEventListener('change', function (event) {
		const file = event.target.files[0];
		if (!file) {
			brandImgName.textContent = '선택된 파일 없음';
			return;
		}
		brandImgName.textContent = file.name;

		const previewUrl = URL.createObjectURL(file);
		let previewImg = document.querySelector('.store-img-section .preview-item img');

		if (!previewImg) {
			const previewItemDiv = document.createElement('div');
			previewItemDiv.classList.add('preview-item');

			const img = document.createElement('img');
			img.alt = '브랜드 이미지 미리보기';
			img.src = previewUrl;

			previewItemDiv.appendChild(img);

			const brandImgForm = document.getElementById('brand-img-form');
			brandImgForm.insertAdjacentElement('afterend', previewItemDiv);
		} else {
			previewImg.src = previewUrl;
		}
	});
}

document.addEventListener('DOMContentLoaded', function () {
	setupAddressAutoFill();
	setupFileNameDisplay(document.getElementById('storeImgInput'), 'storeImgName');
	setupFileNameDisplay(document.getElementById('newStoreImgInput'), 'newStoreImgName');
	document.querySelectorAll('input[name="updatedImg"]').forEach(setupUpdatedImgPreview);
	setupBrandImgPreview();
});


