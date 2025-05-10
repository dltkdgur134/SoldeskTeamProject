function execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			var roadAddr = data.roadAddress;
			var zonecode = data.zonecode;
			
			document.getElementById('postcode').value = zonecode;
			document.getElementById('store_roadAddress').value = roadAddr;

			document.getElementById('store_detailAddress').value = '';
			document.getElementById('store_detailAddress').focus();

			var geocoder = new kakao.maps.services.Geocoder();
			geocoder.addressSearch(roadAddr, function(result, status) {
				if (status === kakao.maps.services.Status.OK) {
					var lat = result[0].y;
					var lng = result[0].x;
					document.getElementById('latitude').value = lat;
					document.getElementById('longitude').value = lng;
				} else {
					console.error('좌표를 가져오지 못했습니다.');
				}
			});
		}
	}).open();
}

document.addEventListener('DOMContentLoaded', function() {
	const detailAddressInput = document.getElementById('store_detailAddress');
	const roadAddressInput = document.getElementById('store_roadAddress');
	const storeInfoForm = document.getElementById('store-info-form');
	

	if (detailAddressInput) {
		detailAddressInput.addEventListener('input', function() {
			const roadAddress = roadAddressInput.value.trim();
			const detailAddress = detailAddressInput.value.trim();
			if (roadAddress) {
				document.getElementById('storeAddress').value = (roadAddress + ' ' + detailAddress).trim();
			}
		});
	}

	if (roadAddressInput) {
		roadAddressInput.addEventListener('input', function() {
			const roadAddress = roadAddressInput.value.trim();
			if (roadAddress) {
				const geocoder = new kakao.maps.services.Geocoder();
				geocoder.addressSearch(roadAddress, function(result, status) {
					if (status === kakao.maps.services.Status.OK) {
						var lat = result[0].y;
						var lng = result[0].x;
						document.getElementById('latitude').value = lat;
						document.getElementById('longitude').value = lng;
					} else {
						console.error('도로명 주소로 좌표를 가져오지 못했습니다.');
					}
				});
			}
		});
	}

	if (storeInfoForm) {
		storeInfoForm.addEventListener('submit', function(event) {
			const roadAddress = roadAddressInput.value.trim();
			const detailAddress = detailAddressInput.value.trim();
			const existingAddress = document.getElementById('current-address').value.trim();

			let finalAddress = '';
			if (!roadAddress) {
				finalAddress = existingAddress;
			} else {
				finalAddress = (roadAddress + ' ' + detailAddress).trim();
			}
			document.getElementById('storeAddress').value = finalAddress;
		});
	}
	
	const brandImgInput = document.querySelector('#brandImgInput');
	const brandImgName = document.querySelector('#brandImgName');

	if (brandImgInput) {
		brandImgInput.addEventListener('change', function () {
			if (this.files.length > 0) {
				brandImgName.textContent = this.files[0].name;
			} else {
				brandImgName.textContent = '선택된 파일 없음';
			}
		});
	}
	
	const storeImgInput = document.getElementById('storeImgInput');
	const storeImgName = document.getElementById('storeImgName');

	if (storeImgInput) {
		storeImgInput.addEventListener('change', function () {
			if (this.files.length > 0) {
				// 여러 파일이 있을 경우 첫 번째 이름 + 외 몇 개
				const names = Array.from(this.files).map(file => file.name);
				storeImgName.textContent = names.length === 1
					? names[0]
					: `${names[0]} 외 ${names.length - 1}개`;
			} else {
				storeImgName.textContent = '선택된 파일 없음';
			}
		});
	}
	
	const newStoreImgInput = document.getElementById('newStoreImgInput');
	const newStoreImgName = document.getElementById('newStoreImgName');

	if (newStoreImgInput) {
		newStoreImgInput.addEventListener('change', function () {
			const names = Array.from(this.files).map(file => file.name);
			newStoreImgName.textContent = names.length === 1
				? names[0]
				: `${names[0]} 외 ${names.length - 1}개`;
		});
	}
	
	document.querySelectorAll('input[name="updatedImg"]').forEach(input => {
		input.addEventListener('change', function () {
			const idSuffix = input.id.replace("storeImgInput_", "");
			const nameSpan = document.getElementById("storeImgName_" + idSuffix);
			const changeBtn = document.getElementById("changeBtn_" + idSuffix);
			
			if (this.files.length > 0) {
				nameSpan.textContent = this.files[0].name;
				
				if (changeBtn) {
					changeBtn.disabled = false;
					changeBtn.style.backgroundColor = "#FFC107";
					changeBtn.style.cursor = "pointer";
				}
			} else {
				nameSpan.textContent = "선택된 파일 없음";
				
				if (changeBtn) {
					changeBtn.disabled = true;
					changeBtn.style.backgroundColor = "gray";
					changeBtn.style.cursor = "not-allowed";
				}
			}
		});
	});

	if (brandImgInput) {
		brandImgInput.addEventListener('change', function(event) {
			const file = event.target.files[0];
			if (file) {
				const previewUrl = URL.createObjectURL(file);

				let previewContainer = document.querySelector('.store-img-section .preview-item img');

				// 미리보기가 없으면 새로 생성
				if (!previewContainer) {
					const previewItemDiv = document.createElement('div');
					previewItemDiv.classList.add('preview-item');

					const img = document.createElement('img');
					img.alt = '브랜드 이미지 미리보기';
					img.src = previewUrl;

					previewItemDiv.appendChild(img);

					// 브랜드 이미지 업로드 폼 아래에 삽입
					const brandImgForm = document.getElementById('brand-img-form');
					brandImgForm.insertAdjacentElement('afterend', previewItemDiv);
				} else {
					// 미리보기가 있으면 src만 바꾼다
					previewContainer.src = previewUrl;
				}
			}
		});
	}
});




