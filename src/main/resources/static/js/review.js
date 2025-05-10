// 달점 클릭 시 누른 별 좌측 별 색 변경 및 값 입력
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

// 페이지 로딩 시 달점 표기
$(function() {
	const reviewForms = document.querySelectorAll("form[id^='review-form']");

	reviewForms.forEach((form) => {
		// 각 form 마다 달점 input과 값 구하기
		const ratingInput = form.querySelector("input[id^='rating-stars']");
		const ratingValue = parseInt(ratingInput.value, 10);

		// 달점에 따라 달 색 변경 (회색 -> 노란색)
		if (!isNaN(ratingValue)) {
			const stars = form.querySelectorAll(".star");
			stars.forEach((star) => {
				const starId = parseInt(star.id, 10);
				if (starId <= ratingValue) {
					star.classList.add("selected"); // 선택된 달 개수 만큼 selected 클래스 추가
				} else {
					star.classList.remove("selected"); // selected 클래스 삭제 (4 달점일 경우 4개까지만 노란색)
				}
			});
		}
	});

});

// 리뷰 작성 업로드 이미지 프리뷰
document.getElementById('review-img').addEventListener('change', function(event) {
	const files = event.target.files; // 선택된 파일들
	const previewContainer = document.getElementById('image-preview-container');
	previewContainer.innerHTML = ''; // 이전 프리뷰 삭제

	// 이미지는 1장 까지만 업로드 가능
	if (files.length > 3) {
		previewContainer.innerHTML = '<p><i class="fa-solid fa-circle-exclamation"></i>  리뷰 이미지는 최대 1개까지 등록 가능합니다.</p>';
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

// 리뷰 내용 작성 시 글자수 표기
document.getElementById('review-content').addEventListener("keyup", function(event) {
	let text = document.getElementById('review-content').value;

	let numWords = text.length;

	let wordCount = document.getElementById('show-word-count');

	wordCount.innerHTML = "(" + numWords + " / 255)";

	if (numWords == 255) {
		wordCount.style.color = "red";
	} else if (numWords == 0) {
		wordCount.style.color = "black";
	} else {
		wordCount.style.color = "#667EFF";
	}
});

// 리뷰 유효성 검사
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

// 리뷰 삭제
function deleteReview(count) {
	const form = document.getElementById('review-form' + count);
	const reviewId = form.reviewId;
	
	const btn = document.getElementById('delete-review-btn' + count);
	btn.innerHTML = "<span class='spinner-border spinner-border-sm' aria-hidden='true'></span>";
	fetch('/content/deleteReview/' + reviewId.value, {
		method: "delete",
		headers: {
			'Content-Type' : 'application/json'
		}
	})	
	.then(response => {
		if (!response.ok) {
			throw new Error('리뷰 삭제 실패 응답 상태: ' + response.status);
		}
		return response.json();
	})
	.then(data => {
		if (data.result === 0) {
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
			} setTimeout(removeFlashMessages, flashDurationInSeconds * 500); // 요청 응답 메시지 표시
			
		} else {
			alert(data.resultMsg); // 에러 메시지
		}
		setTimeout(function() {location.reload(); }, 1000); // 페이지 새로고침
	})
	.catch(error => {
		console.error('오류 발생', error);
		alert('삭제 중 오류가 발생했습니다.')
	});
}
