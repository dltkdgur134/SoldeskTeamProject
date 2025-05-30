
$(function(){
	// 결과 상태창 표시
	'use strict'
	 var flashDurationInSeconds = 5;
	 var flashContainerId = 'flash-messages';

	 function removeFlashMessages() {
	    $('#' + flashContainerId).remove();
	 }
	 setTimeout(removeFlashMessages, flashDurationInSeconds * 500);
	
	 // 닉네임 변경 버튼 클릭
	 document.getElementById('update-nickname-btn').addEventListener('click', event => {
		if (!checkNicknameValidity() || document.getElementById('nickname-input').dataset.status === "no") {
			event.preventDefault();
			event.stopPropagation();
		}
	 });
	 
	 // 닉네임 중복 확인 버튼 클릭
	 document.getElementById('nickname-exists').addEventListener('click', event => {
		const nickname = document.upNickForm.nickname;
		$('.invalid-feedback').empty();

		if (nickname.value.trim() === '') {
			nickname.classList.add("is-invalid");
			nickname.classList.remove("is-valid");
			document.getElementById('nickname-input').dataset.status = "no";
			$('.invalid-feedback').html("닉네임이 비어있습니다.");
			return;
		} 	
		
		$.ajax({
			url: "checkNickname",
			type: "POST",
			async: true,
			data: {
				nickname : nickname.value
			},
			success: function(data) {
				// 기존 닉네임 있음 (사용 불가능)
				if (data.count > 0) {
					document.getElementById('nickname-input').dataset.status = "no";
					console.log($('#nickname-input').attr('data-status')); // 중복 유무 로그
					nickname.classList.add("is-invalid");
					nickname.classList.remove("is-valid");
					$('.invalid-feedback').html('닉네임이 이미 존재합니다.');
				// 기존 닉네임 없음 (사용 가능)
				} else {
					document.getElementById('nickname-input').dataset.status = "yes";
					console.log($('#nickname-input').attr('data-status')); // 중복 유무 로그
					nickname.classList.add("is-valid");
					nickname.classList.remove("is-invalid");
				}
			},
			error: function(e) {
				alert("error : " + e);
			}
		});
	})
	 
	// 프로필 이미지 변경 버튼 클릭
	 document.getElementById('profileImage').addEventListener('change', function () {
	   const maxSize = 1024 * 1024;
	   const maxWidth = 450;
	   const maxHeight = 450;
	   const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif'];
	   const file = this.files[0];

	   if (!file) {
		 document.getElementById('file-name-display').style.color = '#e40024';
	     document.getElementById('file-name-display').textContent = '선택된 파일 없음';
	     return;
	   }

	   const fileExtension = file.name.split('.').pop().toLowerCase();
	   if (!allowedExtensions.includes(fileExtension)) {
	     alert('jpg, jpeg, png, gif 형식의 이미지만 업로드 가능합니다.');
	     this.value = '';
		 document.getElementById('file-name-display').style.color = '#e40024';
	     document.getElementById('file-name-display').textContent = '이미지를 선택해주세요';
	     return;
	   }

	   if (file.size > maxSize) {
	     alert('파일 크기가 1MB를 초과했습니다. 다시 선택해주세요.');
	     this.value = '';
		 document.getElementById('file-name-display').style.color = '#e40024';
	     document.getElementById('file-name-display').textContent = '1MB 이하로 해주세요';
	     return;
	   }

	   const img = new Image();
	   const objectUrl = URL.createObjectURL(file);

	   img.onload = function () {
	     if (img.width > maxWidth || img.height > maxHeight) {
	       alert(`이미지 크기는 ${maxWidth}x${maxHeight}px 이하여야 합니다.`);
	       document.getElementById('profileImage').value = '';
		   document.getElementById('file-name-display').style.color = '#e40024';
	       document.getElementById('file-name-display').textContent = '450x450 이하로 해주세요';
	     } else {
	       /*document.getElementById('file-name-display').textContent = file.name;*/
		   document.getElementById('file-name-display').style.color = 'green';
		   document.getElementById('file-name-display').textContent = '프로필 이미지 등록이 가능합니다';
	     }
	     URL.revokeObjectURL(objectUrl);
	   };
	   img.src = objectUrl;
	 });
	 
	 // 전화번호 변경 버튼 클릭
	 document.getElementById('update-phone-btn').addEventListener('click', event => {
	 		if (!checkPhonenumValidity() || document.getElementById('phone-input').dataset.status === "no") {
	 			event.preventDefault();
	 			event.stopPropagation();
	 		}
	 });
	 
	// 전화번호 중복 확인 버튼 클릭
	 document.getElementById('phonenum-exists').addEventListener('click', event => {
			const phoneNum = document.querySelector('form[name="upPhoneForm"] input[name="userPhone"]');
			phoneNum.value = phoneNum.value.replace(/[^0-9\-]/g, '');
			phoneNum.value = phoneNum.value.replace(/-/g, '');
	 		$('.invalid-feedback').empty();

	 		if (phoneNum.value.trim() === '') {
	 			phoneNum.classList.add("is-invalid");
	 			phoneNum.classList.remove("is-valid");
	 			document.getElementById('phone-input').dataset.status = "no";
	 			$('.invalid-feedback').html("전화번호가 비어있습니다.");
	 			return;
	 		} 	
	 		
	 		$.ajax({
	 			url: "checkPhoneNum",
	 			type: "POST",
	 			async: true,
	 			data: {
	 				userPhone: phoneNum.value
	 			},
	 			success: function(data) {
	 				// 기존 전화번호 없음
	 				if (data.count > 0) {
	 					document.getElementById('phone-input').dataset.status = "no";
	 					console.log($('#phone-input').attr('data-status')); // 중복 유무 로그
	 					phoneNum.classList.add("is-invalid");
	 					phoneNum.classList.remove("is-valid");
	 					$('.invalid-feedback').html('전화번호가 이미 존재합니다.');
	 				// 기존 전화번호 있음
	 				} else {
	 					document.getElementById('phone-input').dataset.status = "yes";
	 					console.log($('#phone-input').attr('data-status')); // 중복 유무 로그
	 					phoneNum.classList.add("is-valid");
	 					phoneNum.classList.remove("is-invalid");
	 				}
	 			},
	 			error: function(e) {
	 				alert("error : " + e);
	 			}
	 		});
	 	})
	
});

function checkPassword() {
	const oldPassword = document.checkPasswordForm.oldPassword;
	const password = document.checkPasswordForm.password;
	const passwordCheck = document.checkPasswordForm.passwordCheck;
	
	oldPassword.classList.remove("is-invalid");
	oldPassword.classList.remove("is-valid");
	password.classList.remove("is-valid");
	password.classList.remove("is-invalid");
	passwordCheck.classList.remove("is-valid");
	passwordCheck.classList.remove("is-invalid");
	
	if (oldPassword.value.trim() === '') {
		oldPassword.classList.add("is-invalid");
		oldPassword.classList.remove("is-valid");
		oldPassword.focus();
		return false;
	}
	if (password.value.trim() === '') {
		password.classList.add("is-invalid");
		password.classList.remove("is-valid")
		password.focus();
		return false;
	}
	if (password.value !== passwordCheck.value) {
		passwordCheck.classList.add("is-invalid");
		passwordCheck.classList.remove("is-valid");
		passwordCheck.focus();
		return false;
 	}
	else {
		return true;
	}
}

// 닉네임 유효성 검사
function checkNicknameValidity() {
	'use strict'
	const newNickname = document.upNickForm.nickname;
	
	if (newNickname.dataset.status === "no") {
		newNickname.classList.add("is-invalid");
		newNickname.classList.remove("is-valid");
		$('.invalid-feedback').html("중복확인이 필요합니다.");
		return false;
	}
	else {
		newNickname.classList.add("is-valid");
		newNickname.classList.remove("is-invalid");
		return true;
	}
}

// 전화번호 유효성 검사
function checkPhonenumValidity() {
	'use strict'
	const phoneNum = document.upPhoneForm.userPhone;
	if (phoneNum.dataset.status === "no") {
		phoneNum.classList.add("is-invalid");
		phoneNum.classList.remove("is-valid");
		$('.invalid-feedback').html("중복확인이 필요합니다.");
		return false;
	} else {
		phoneNum.classList.add("is-valid");
		phoneNum.classList.add("is-invalid");
		return true;
	}
}

// 드래그앤드롭 기능 드롭핸들러
function dropHandler(ev) {
  console.log("파일 드롭 완료");
  ev.preventDefault();

  const dropZone = ev.target;
  const validTypes = ["image/jpeg", "image/png", "image/gif"];
  const maxSize = 5 * 1024 * 1024; // 5 MB

  if (ev.dataTransfer.items) {
    [...ev.dataTransfer.items].forEach((item, i) => {
      if (item.kind === "file") {
        const file = item.getAsFile();
        if (!validTypes.includes(file.type)) {
          dropZone.style.backgroundColor = "#ff99a9"; // 에러 발생 알림용 색상
          alert("jpg, jpeg, png, gif 형식의 이미지만 업로드 가능합니다.");
          return;
        }
        if (file.size > maxSize) {
          dropZone.style.backgroundColor = "#ff99a9"; // 에러 발생 알림용 색상
          alert("파일 크기가 5MB를 초과했습니다. 다시 선택해주세요.");
          return;
        }
        console.log(`… file[${i}].name = ${file.name}`);
        document.getElementById("file-name-display").textContent = file.name;

        const dTrans = new DataTransfer();
        dTrans.items.add(file);
        const profileImage = document.getElementById("profileImage");
        profileImage.files = dTrans.files;

        // change 이벤트 수동 트리거
        profileImage.dispatchEvent(new Event("change"));
        dropZone.style.backgroundColor = ""; // 유효성 검사 통과 시 스타일 리셋
      }
    });
  }
}

// 드래그앤드롭 기능 드래그오버핸들러
function dragOverHandler(ev) {
  console.log("드랍존에 파일 들어옴");
  // 디폴트 행동 방지
  ev.preventDefault();
}

function checkRiderNicknameValidity() {
  const oldNickname = document.getElementById("profile-nickName");
  const newNickname = document.getElementById("rider-nickname-input");

  if (
    oldNickname.textContent.trim() === newNickname.value.trim() ||
    newNickname.value.trim() === ""
  ) {
    newNickname.classList.add("is-invalid");
    newNickname.classList.remove("is-valid");
    return false;
  } else {
    newNickname.classList.add("is-valid");
    newNickname.classList.remove("is-invalid");
    return true;
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const nicknameInput = document.getElementById("rider-nickname-input");

  if (nicknameInput) {
    nicknameInput.addEventListener("input", checkRiderNicknameValidity);
  }
});

// 드래그앤드롭 기능 드래그엔터핸들러 (드롭존 안에 들어올 때)
function dragEnterHandler(ev) {
	console.log("File(s) entered drop zone")
	ev.dataTransfer.clearData();
	ev.dataTransfer.setData("img", ev.target.id);
	ev.target.style.backgroundColor = "#99A9FF";
}

// 드래그앤드롭 기능 드래그리브핸들러 (드롭존 나갈 때)
function dragLeaveHandler(ev) {
	console.log("File(s) left drop zone")
	ev.target.style.backgroundColor = "white";
}


// 회원 탈퇴 
function checkDeleteCheckbox() {
	const agreeToTerms = document.deleteAccountForm.agree;
	if (agreeToTerms.checked == true) {
		agreeToTerms.classList.add("is-valid");
		agreeToTerms.classList.remove("is-invalid")
		return true;
	} else {
		agreeToTerms.classList.add("is-invalid");
		agreeToTerms.classList.remove("is-valid");
		return false
	}
}
