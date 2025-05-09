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
	
	const brandImgInput = document.querySelector('input[name="brandImg"]');

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




