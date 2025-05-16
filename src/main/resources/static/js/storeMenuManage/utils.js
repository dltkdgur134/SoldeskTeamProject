export function validateImageFile(fileInput) {
	const file = fileInput.files[0];
	if (!file) return true;

	const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
	const maxSize = 1024 * 1024;

	if (!validTypes.includes(file.type)) {
		alert("이미지 파일만 업로드할 수 있습니다. (jpg, png, gif, webp)");
		return false;
	}

	if (file.size > maxSize) {
		alert("이미지 크기는 1MB 이하만 가능합니다.");
		return false;
	}

	return true;
}


