$('.rating span').click(function() {
	const rating = parseInt($(this).attr('id')); // 누른 별의 id 구하기
	$('#rating-stars').val(rating); // 숨겨진 input 값 설정

	// 나머지 별 selected 클래스 삭제
	$('.rating span').removeClass('selected');

	// 누른 별 및 그 전 별들 모두 selected 클래스 추가
	$('.rating span').each(function() {
		if (parseInt($(this).attr('id')) <= rating) {
			$(this).addClass('selected');
		}
	});
});

// 리뷰 작성 업로드 이미지 프리뷰
document.getElementById('review-img').addEventListener('change', function(event) {
	const files = event.target.files; // 선택된 파일들
	const previewContainer = document.getElementById('image-preview-container');
	previewContainer.innerHTML = ''; // 이전 프리뷰 삭제
	
	// 이미지는 3장 까지만 업로드 가능
	if (files.length > 3) {
		previewContainer.innerHTML = '<p><i class="fa-solid fa-circle-exclamation"></i>  리뷰 이미지는 최대 3개까지 등록 가능합니다.</p>';
	} else if (files.length > 0 && files.length < 4) {
		Array.from(files).forEach(file => {
			const reader = new FileReader();

			reader.onload = function(e) {
				// 이미지 엘레먼트 생성
				const img = document.createElement('img');
				img.src = e.target.result; // 이미지 소스 설정
				img.alt = file.name;
				img.style = 'max-width: 100px; height: auto; margin: 5px; border: 3px solid  #667EFF; padding: 5px; border-radius: 5px;';

				// 프리뷰 컨테이너 안에 이미지 넣기
				previewContainer.appendChild(img);
			};

			reader.readAsDataURL(file); //파일을 data url로 읽어오기
		});
	} else {
		previewContainer.innerHTML = '<p>선택된 이미지가 없습니다.</p>';
	}
});

function checkReview() {
	const ratingStars = document.getElementById('rating-stars');
	if (ratingStars.value < 1) {
		ratingStars.classList.add("is-invalid");
		ratingStars.classList.remove("is-valid")
		return false;
	} else {
		ratingStars.classList.add("is-valid");
		ratingStars.classList.remove("is-invalid");
		return true;
	}
	
}



