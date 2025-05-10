
// 스페이스 입력을 막을 input에만 "user-input" 클래스 부여해서 구분
document.addEventListener('DOMContentLoaded', function () {
  const userInputs = document.querySelectorAll('input');

  userInputs.forEach(input => {
    input.addEventListener('keydown', function(event) {
      if (event.code === 'Space' || event.keyCode === 32) {
        event.preventDefault();
      }
    });
  });
});
