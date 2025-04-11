
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
		   document.getElementById('file-name-display').style.color = 'black';
		   document.getElementById('file-name-display').textContent = '프로필 이미지 등록이 가능합니다';
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

function dragOverHandler(ev) {
  console.log("File(s) in drop zone");
  // 디폴트 행동 방지
  ev.preventDefault();
}

function dragEnterHandler(ev) {
	console.log("File(s) entered drop zone")
	ev.dataTransfer.clearData();
	ev.dataTransfer.setData("img", ev.target.id);
	ev.target.style.backgroundColor = "#99A9FF";
}

function dragLeaveHandler(ev) {
	console.log("File(s) left drop zone")
	ev.target.style.backgroundColor = "white";
}

