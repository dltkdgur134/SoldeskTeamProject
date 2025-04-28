function execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			var roadAddr = data.roadAddress;
			var zonecode = data.zonecode;
			
			document.getElementById('postcode').value = zonecode;
			document.getElementById('store_roadAddress').value = roadAddr;
			document.getElementById('storeAddress').value = roadAddr;

			document.getElementById('storeAddress').value = '';
			document.getElementById('store_detailAddress').value = '';

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

	if (detailAddressInput) {
		detailAddressInput.addEventListener('input', function() {
			const roadAddress = document.getElementById('store_roadAddress').value.trim();
			const detailAddress = detailAddressInput.value.trim();

			if (roadAddress) {
				if (detailAddress) {
					document.getElementById('storeAddress').value = roadAddress + ' ' + detailAddress;
				} else {
					document.getElementById('storeAddress').value = roadAddress;
				}
			}
		});
	}
});