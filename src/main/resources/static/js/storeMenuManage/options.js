export const maxOption = 3;
export let addOptionCount = { value: 0 };
export let editOptionCount = { value: 0 };

export function addOptionField(containerId, btnId, countRef) {
	if (countRef.value >= maxOption) {
		alert("옵션은 최대 3개까지 추가할 수 있습니다.");
		return;
	}

	countRef.value++;

	const div = document.createElement("div");
	div.className = "option-group";
	div.innerHTML = `
		<label>옵션 ${countRef.value}</label><br>
		<input type="text" name="menuOptions${countRef.value}[]" placeholder="옵션${countRef.value} 이름" required />
		<input type="number" name="menuOptions${countRef.value}Price[]" placeholder="옵션${countRef.value} 가격" required />
		<button type="button" onclick="removeOption(this)">삭제</button>
	`;

	document.getElementById(containerId).appendChild(div);

	if (countRef.value >= maxOption) {
		document.getElementById(btnId).disabled = true;
	}
}

export function removeOption(button) {
	const group = button.closest(".option-group");
	const container = group.parentElement;
	group.remove();

	if (container.id === "option1-container") {
		addOptionCount.value--;
		document.getElementById("add-option-btn").disabled = false;
	} else if (container.id === "edit-option-container") {
		editOptionCount.value--;
		document.getElementById("edit-add-option-btn").disabled = false;
	}
}
