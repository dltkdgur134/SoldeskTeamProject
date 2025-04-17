
/*$(function () {
	$(".nav-item > .active").css("color", "red");
	
	$(".nav-link").click(function (){
		$(".nav-item > .active").css("color", "#ffc107;");
		$(".nav-link").removeClass('active');
		
		$(this).addClass('active');
		$(".nav-item > .active").css("color", "red");
	});
});*/

document.addEventListener('DOMContentLoaded', function () {
	var flashDurationInSeconds = 5;
	var flashContainerId = 'flash-messages';

	 function removeFlashMessages() {
	    $('#' + flashContainerId).remove();
	 }
	 setTimeout(removeFlashMessages, flashDurationInSeconds * 500);
	
  const input = document.getElementById('main-input');
  const btn = document.getElementById('main-btn');
  const reenterBtn = document.getElementById('reenter-address-btn');

  const savedAddress = sessionStorage.getItem('address');
  const savedFood = sessionStorage.getItem('food');

  // 초기 세팅: 주소 있으면 음식 검색 모드로
  if (savedAddress) {
    reenterBtn.style.display = 'inline-block';
    input.placeholder = '뭐 먹을까?';
    if (savedFood) input.value = savedFood;
  }

  // 버튼 클릭 처리
  btn.addEventListener('click', function (e) {
	e.preventDefault();
    const value = input.value.trim();
    if (!value) return;

    if (!sessionStorage.getItem('address')) {
      // 주소 입력 모드
      sessionStorage.setItem('address', value);
      input.value = sessionStorage.getItem('food') || '';
      input.placeholder = '뭐 먹을까?';
      reenterBtn.style.display = 'inline-block';
    } else {
      // 음식 검색 모드	
      sessionStorage.setItem('food', value);
      alert('"' + value + '" 검색됨 (검색 로직 연결 가능)');
    }
  });

  // 주소 다시 입력
  reenterBtn.addEventListener('click', function () {
    input.value = '';
    input.placeholder = '배달받을 주소를 입력하세요';
    sessionStorage.removeItem('address');
    reenterBtn.style.display = 'none';
  });
});


