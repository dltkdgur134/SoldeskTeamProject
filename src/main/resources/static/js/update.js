
$(function(){
	'use strict'

	 document.getElementById('update-nickname-btn').addEventListener('click', event => {
		if (!checkNicknameValidity()) {
			event.preventDefault();
			event.stopPropagation();
		}
	 });
	 
	 document.getElementById('profileImage').addEventListener('change', function () {
	   const maxSize = 1024 * 1024;
	   const maxWidth = 450;
	   const maxHeight = 450;
	   const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif'];
	   const file = this.files[0];

	   if (!file) {
	     document.getElementById('file-name-display').textContent = '선택된 파일 없음';
	     return;
	   }

	   const fileExtension = file.name.split('.').pop().toLowerCase();
	   if (!allowedExtensions.includes(fileExtension)) {
	     alert('jpg, jpeg, png, gif 형식의 이미지만 업로드 가능합니다.');
	     this.value = '';
	     document.getElementById('file-name-display').textContent = '이미지를 선택해주세요';
	     return;
	   }

	   if (file.size > maxSize) {
	     alert('파일 크기가 1MB를 초과했습니다. 다시 선택해주세요.');
	     this.value = '';
	     document.getElementById('file-name-display').textContent = '1MB 이하로 해주세요';
	     return;
	   }

	   const img = new Image();
	   const objectUrl = URL.createObjectURL(file);

	   img.onload = function () {
	     if (img.width > maxWidth || img.height > maxHeight) {
	       alert(`이미지 크기는 ${maxWidth}x${maxHeight}px 이하여야 합니다.`);
	       document.getElementById('profileImage').value = '';
	       document.getElementById('file-name-display').textContent = '450x450 이하로 해주세요';
	     } else {
	       document.getElementById('file-name-display').textContent = file.name;
	     }
	     URL.revokeObjectURL(objectUrl);
	   };

	   img.src = objectUrl;
	 });
	 
});

function checkNicknameValidity() {
	'use strict'
	const oldNickname = document.getElementById('profile-nickName');
	const newNickname = document.upnickform.nickname;
	if (oldNickname.textContent === newNickname.value || newNickname.value.trim() === '') {
		newNickname.classList.add("is-invalid");
		newNickname.classList.remove("is-valid");
		return false;
	} else {
		newNickname.classList.add("is-valid");
		newNickname.classList.remove("is-invalid");
		return true;
	}
}

function dropHandler(ev) {
  console.log("File(s) dropped");

  // Prevent default behavior (Prevent file from being opened)
  ev.preventDefault();

  if (ev.dataTransfer.items) {
    // Use DataTransferItemList interface to access the file(s)
    [...ev.dataTransfer.items].forEach((item, i) => {
      // If dropped items aren't files, reject them
      if (item.kind === "file") {
        const file = item.getAsFile();
        console.log(`… file[${i}].name = ${file.name}`);
      }
    });
  } else {
    // Use DataTransfer interface to access the file(s)
    [...ev.dataTransfer.files].forEach((file, i) => {
      console.log(`… file[${i}].name = ${file.name}`);
    });
  }
}

function dragOverHandler(ev) {
  console.log("File(s) in drop zone");

  // Prevent default behavior (Prevent file from being opened)
  ev.preventDefault();
}

