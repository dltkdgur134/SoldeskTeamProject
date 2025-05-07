
$(function() {
	var flashDurationInSeconds = 5;
	 var flashContainerId = 'flash-messages';

	 function removeFlashMessages() {
	    $('#' + flashContainerId).remove();
	 }
	 setTimeout(removeFlashMessages, flashDurationInSeconds * 500);
	
	/*document.getElementById('address-search-bar').addEventListener('change', event => {
		var results = [];
		let address = {
			place_name : "",
			address_name : "",
			road_address_name : "",
			xCoordinate : "",
			yCoordinate: ""
		}
		
		const keyword = document.getElementById('address-search-bar').value;
		//console.log(keyword)
		$.ajax({
			url: "https://dapi.kakao.com/v2/local/search/keyword.json",
			data: {
				query: keyword,
				size : 10
			},
			headers: {
				Authorization: "KakaoAK c7dd39e36776f90fb259bbd8ac3fcdc6" 
			},
			success: function(response) {
				const places = response.documents;
				//const resultList = $('#resultList');
				console.log(response.meta.total_count);
				
				places.forEach(place => {
					//resultList.append(`<li>${place.place_name} - ${place.address_name}</li>`);
					//results.push(place.place_name + "-" + place.address_name);
					address.place_name = place.place_name;
					address.address_name = place.address_name;
					address.road_address_name = place.road_address_name;
					address.xCoordinate = place.x;
					address.yCoordinate = place.y;
					results.push(address);
				});
				
				
				const query = keyword;
				
				const dropdown = document.getElementById('dropdown-results');
					  
				if (!query) {
					dropdown.style.display = 'none';
					dropdown.innerHTML = '';
					return;
				}
				
				if (places.length === 0) {
					resultList.append("<li>검색 결과가 없습니다.</li>");
					results.push("검색결과가 없습니다.");
				}
					  
				// Simulating a search API call with sample data
				//const filteredData = results.filter(item => item.toLowerCase().includes(query.toLowerCase()));

				// Show results in the dropdown
				//dropdown.innerHTML = filteredData.map(item => `<li onclick="handleSelection('${item}')">${item}</li>`).join('');

				//dropdown.style.display = filteredData.length > 0 ? 'block' : 'none';
				const filteredData = results.filter(item =>
				               item.place_name.toLowerCase().includes(keyword.toLowerCase())
				           );

				           // Show filtered results in the dropdown
				           dropdown.innerHTML = filteredData.map(item =>
				               `<li onclick="handleSelection('${item.place_name}')">${item.place_name}</li>`
				           ).join('');

				           dropdown.style.display = filteredData.length > 0 ? 'block' : 'none';
			}
		})
		
	});*/

	/*document.getElementById('좌표 받기').addEventListener('click', event => {
			var results = [];
			let address = {
				place_name : "",
				address_name : "",
				road_address_name : "",
				xCoordinate : "",
				yCoordinate: ""
			}
			
			const keyword = document.getElementById('sample2_address').value;
			//console.log(keyword)
			$.ajax({
				url: "https://dapi.kakao.com/v2/local/search/keyword.json",
				data: {
					query: keyword,
					size : 10
				},
				headers: {
					Authorization: "KakaoAK 004973e06271b159ad20691c7671ad88" 
				},
				success: function(response) {
					const places = response.documents;
					//const resultList = $('#resultList');
					console.log(response.meta.total_count);
					
					places.forEach(place => {
						//resultList.append(`<li>${place.place_name} - ${place.address_name}</li>`);
						//results.push(place.place_name + "-" + place.address_name);
						const x = place.x;
						const y = place.y;
						$('#longitude').val(x);
						$('#latitude').val(y);
					});
					
				}
			})
			
		});*/

});

function fetchSearchResults(query) {
	const dropdown = document.getElementById('dropdown-results');

	if (!query) {
		dropdown.style.display = 'none';
		dropdown.innerHTML = '';
		return;
	}

	// Simulating a search API call with sample data
	const sampleData = ['Apple', 'Banana', 'Cherry', 'Date', 'Grape', 'Kiwi', 'Mango', '서초구', '강남구', '관악구'];
	const filteredData = sampleData.filter(item => item.toLowerCase().includes(query.toLowerCase()));

	// Show results in the dropdown
	dropdown.innerHTML = filteredData.map(item => `<li onclick="handleSelection('${item}')">${item}</li>`).join('');

	dropdown.style.display = filteredData.length > 0 ? 'block' : 'none';
}

function handleSelection(selectedValue) {
	const searchBar = document.getElementById('address-search-bar');
	const dropdown = document.getElementById('dropdown-results');
	searchBar.value = selectedValue;
	dropdown.style.display = 'none';
}

// 주소 삭제
function deleteAddress(count) {
	const form = document.getElementById('address-form' + count);
	const regAddressId = form.regAddressId;
	// 버튼 innerHTML을 로딩 스피너로 변경
	const btn = document.getElementById('delete-address-btn' + count);
	btn.innerHTML =
	"<span class='spinner-border spinner-border-sm' aria-hidden='true'></span>";
	fetch('/content/deleteAddress/' + regAddressId.value, {
		method: 'delete',
		headers: {
			'Content-Type': 'application/json'
		}
	})
		.then(response => {
			// 응답 상태 확인 (200)
			if (!response.ok) {
				throw new Error('주소 삭제 실패 응답 상태: ' + response.status);
			}
			return response.json(); // 응답 상태 OK 시 JSON 파싱 진행
		})
		.then(data => {
			if (data.result === 0) {
				//alert(data.resultMsg);
				const container = document.getElementById('result-msg-container');
				const statusContainer = document.getElementById('result-status');
				container.innerHTML = 
				"<div class='alert alert-success' id='flash-messages' role='alert'>" +
				"<i class='fa-solid fa-circle-check'></i>" +
				"<strong>" + data.resultMsg + "</strong>" +
				"</div>";
				statusContainer.innerHTML = 
				"<div class='spinner-border text-dark'' role='status'><span class='visually-hidden'>로딩중...</span></div>";
				var flashDurationInSeconds = 2;
				var flashContainerId = 'result-msg-container';

					 function removeFlashMessages() {
					    $('#' + flashContainerId).remove();
					 }
					 setTimeout(removeFlashMessages, flashDurationInSeconds * 500); // 요청 응답 메시지 표시
			} else {
				alert(data.resultMsg); // 에러 메시지
			}
			setTimeout(function () { location.reload(); }, 1000); // 페이지 새로고침			
			//location.reload(); // 삭제 후 페이지 새로고침
			//window.location.href = "/myAddress?deleted=true";
		})
		.catch(error => {
			console.error('오류 발생:', error);
			alert('삭제 중 오류가 발생했습니다.');
		});
}




