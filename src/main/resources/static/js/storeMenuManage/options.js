export const maxOption = 3;
export let addOptionCount = { value: 0 };
export let editOptionCount = { value: 0 };

const escapeHtml = str => String(str || "")
	.replace(/&/g, "&amp;")
	.replace(/</g, "&lt;")
	.replace(/>/g, "&gt;")
	.replace(/"/g, "&quot;");


export function addOptionField(containerId, btnId, countRef) {
	if (countRef.value >= maxOption) {
		alert("옵션은 최대 3개까지 추가할 수 있습니다.");
		return;
	}

	countRef.value++;

	const div = document.createElement("div");
	div.className = "option-group";

	const groupId = `optionGroup${containerId}-${countRef.value}`; // 등록/수정 구분을 위해 ID에 containerId 포함
	div.innerHTML = `
		<div class="option-group-wrapper" id="${groupId}">
			<label>옵션 그룹 ${countRef.value}</label>
			<input type="text" name="menuOptions${countRef.value}GroupName" placeholder="옵션 그룹 이름 (예: 맵기 선택)" required />
			<div class="option-items"></div>
			<button type="button" onclick="addSubOption('${groupId}', ${countRef.value})">+ 하위 옵션 추가</button>
			<button type="button" onclick="removeOptionGroup(this)">옵션 그룹 삭제</button>
		</div>
	`;

	document.getElementById(containerId).appendChild(div);

	if (countRef.value >= maxOption) {
		document.getElementById(btnId).disabled = true;
	}
}

export function fillEditOptions(menuDto) {
	const container = document.getElementById("edit-option-container");
	container.innerHTML = "";
	editOptionCount.value = 0;
	document.getElementById("edit-add-option-btn").disabled = false;

	const maxOption = 3;

	for (let i = 1; i <= maxOption; i++) {
		const groupName = menuDto[`menuOptions${i}GroupName`];
		const options = menuDto[`menuOptions${i}`];
		const prices = menuDto[`menuOptions${i}Price`];

		if (!groupName && (!options || options.length === 0)) continue;

		editOptionCount.value++;

		const div = document.createElement("div");
		div.className = "option-group";

		const groupId = `editOptionGroup${i}`;
		div.innerHTML = `
			<div class="option-group-wrapper" id="${groupId}">
				<label>옵션 그룹 ${i}</label>
				<input type="text" name="menuOptions${i}GroupName" placeholder="옵션 그룹 이름" value="${escapeHtml(groupName)}" required />
				<div class="option-items"></div>
				<button type="button" onclick="addSubOption('${groupId}', ${i})">+ 하위 옵션 추가</button>
				<button type="button" onclick="removeOptionGroup(this)">옵션 그룹 삭제</button>
			</div>
		`;

		container.appendChild(div);

		const itemsDiv = div.querySelector(".option-items");

		if (Array.isArray(options) && Array.isArray(prices)) {
			for (let j = 0; j < options.length; j++) {
				const optName = options[j];
				const optPrice = prices[j];

				const wrapper = document.createElement("div");
				wrapper.className = "sub-option";
				wrapper.innerHTML = `
					<input type="text" name="menuOptions${i}[]" value="${escapeHtml(optName)}" placeholder="하위 옵션 이름" required />
					<input type="number" name="menuOptions${i}Price[]" value="${optPrice}" placeholder="가격" required />
					<button class="remove-btn danger" type="button" onclick="this.parentElement.remove()">삭제</button>
				`;
				itemsDiv.appendChild(wrapper);
			}
		}
	}

	if (editOptionCount.value >= maxOption) {
		document.getElementById("edit-add-option-btn").disabled = true;
	}
}


// ✅ 하위 옵션 추가 (공용)
window.addSubOption = function (groupId, groupNum) {
	const container = document.querySelector(`#${groupId} .option-items`);

	const wrapper = document.createElement("div");
	wrapper.className = "sub-option";

	wrapper.innerHTML = `
		<input type="text" name="menuOptions${groupNum}[]" placeholder="하위 옵션 이름" required />
		<input type="number" name="menuOptions${groupNum}Price[]" placeholder="가격" required />
		<button class="remove-btn danger" type="button" onclick="this.parentElement.remove()">삭제</button>
	`;

	container.appendChild(wrapper);
};

// ✅ 옵션 그룹 제거 (공용)
window.removeOptionGroup = function (button) {
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
};
/*
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
*/



