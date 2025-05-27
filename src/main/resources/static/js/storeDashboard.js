let stompClient = null;
let storeId = null;    // ❗ 전역 변수로 선언
let currentOrderId = null;

console.log('🧪 [window.storeId]:', window.storeId);
console.log('🧪 [document.body.dataset.storeid]:', document.body.dataset.storeid);

function notifyUserOrderUpdate(order) {
	if (!order || !order.userUuid) {
		console.warn('🔕 유저 알림 보낼 수 없음: userUuid 없음');
		return;
	}
	stompClient.send('/app/notifyUser', {}, JSON.stringify(order));
}

document.addEventListener('DOMContentLoaded', function() {
	const storeIdAttr = document.body.dataset.storeid;
	if (!storeIdAttr || storeIdAttr === 'undefined') {
		console.warn('❗ storeId가 유효하지 않음:', storeIdAttr);
		return;
	}

	storeId = storeIdAttr; // 안전하게 전역 storeId에 할당
	console.log('✅ storeId loaded:', storeId);

	// WebSocket 연결, etc.
	connect();
	// 채팅 전송 버튼
	$('#sendChatBtn').on('click', sendChat);
	$('#chatInput').on('keypress', e => {
		if (e.which === 13) sendChat();
	});

	// 1) 페이지 로드 시, 서버에서 전체 주문 목록을 불러옴
	loadOrderList();
	setupOrderButtons();  // ✅ 버튼 바인딩

});

function handleOrderAction(orderId, action, extra = {}) {
	let url = '';
	let payload = { orderId };

	const finalize = (completionTime) => {
		if (action === 'accept') {
			payload.completionTime = completionTime || 15;
		}

		console.log("🚀 접수 요청 보내기:", url, payload);

		$.ajax({
			url: url,
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(payload),
			success: function(updatedOrder) {
				alert(`주문 상태 변경 완료: ${action}`);
				renderOrderDetail(updatedOrder);
				removeOrderFromList(updatedOrder.orderId);
				addOrderToList(updatedOrder);
				notifyUserOrderUpdate(updatedOrder);
			},
			error: function(err) {
				console.error(`${action} 실패:`, err);
				alert(`${action} 실패: ` + err.responseText);
			}
		});
	};

	switch (action) {
		case 'accept':
			url = '/owner/order/accept';
			$.get(`/owner/order/get-cooking-time?orderId=${orderId}`, function(time) {
				finalize(time);
			});
			return; // ✅ 여기서 끝나야 함
		case 'reject':
			url = '/owner/order/reject'; break;
		case 'complete':
			url = '/owner/order/complete'; break;
		case 'startDelivery':
			url = '/owner/order/startDelivery'; break;
		default:
			console.warn('Unsupported action:', action); return;
	}

	finalize();
}

function getOrderId(elem) {
	// 리스트 or 상세에서 동작 가능하게 처리
	return $(elem).data('orderid') || $('#detailOrderId').text();
}

function getCompletionTime(elem) {
	const input = $(elem).closest('li').find('.time-adjust input');
	if (input.length) return input.val();

	const detailInput = $('#detailTimeInput');
	if (detailInput.length) return detailInput.val();

	return 15;
}

function setupOrderButtons() {
	// ✅ 주문 리스트 아이템 클릭 시 주문 상세 정보 로딩
	$(document).on('click', '.order-item', function() {
		const orderId = $(this).data('orderid');
		console.log('🍀 [DELEGATED CLICK] order-item clicked:', orderId);
		loadOrderDetail(orderId);
	});

	$(document).on('click', '.acceptBtn, #confirmBtn', function() {
		const orderId = getOrderId(this);
		const time = getCompletionTime(this);
		const parsedTime = parseInt(time, 10);
		handleOrderAction(orderId, 'accept', { completionTime: isNaN(parsedTime) ? 15 : parsedTime });
	});

	$(document).on('click', '.rejectBtn, #cancelBtn', function() {
		const orderId = getOrderId(this);
		handleOrderAction(orderId, 'reject');
	});

	$(document).on('click', '.completeBtn, #completeBtn', function() {
		const orderId = getOrderId(this);
		handleOrderAction(orderId, 'complete');
	});

	$(document).on('click', '.startDeliveryBtn, #startDeliveryBtn', function() {
		const orderId = getOrderId(this);
		handleOrderAction(orderId, 'startDelivery');
	});

	$(document).on('click', '.extendTimeBtn, #extendTimeBtn', function() {
		const orderId = getOrderId(this);
		extendTime(orderId, 5); // 기본 5분 추가
	});
}

function updateTemporaryCookingTime(orderId, minutes) {
	$.ajax({
		url: '/owner/order/temp-cooking-time',
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify({ orderId: orderId, completionTime: minutes }),
		success: function() {
			console.log('✅ 세션에 조리 시간 임시 저장 완료');
		},
		error: function(err) {
			console.error('❌ 시간 저장 실패', err);
		}
	});
}

// 주문 목록 불러오기
function loadOrderList() {
	const storeIdAttr = document.body.dataset.storeid;

	if (!storeIdAttr || storeIdAttr === 'undefined') {
		console.warn('❗ storeId가 유효하지 않음:', storeIdAttr);
		return;
	}

	storeId = storeIdAttr; // 안전하게 전역 storeId에 할당
	console.log('✅ storeId loaded:', storeId);
	$.ajax({
		url: `/owner/order/list?storeId=${storeId}`,
		method: 'GET',
		success: function(orderList) {
			console.log('🍀 loadOrderList 응답:', orderList);
			renderOrderList(orderList);
		},
		error: function(err) {
			console.error('Status:', err.status);
			console.error('Response body:', err.responseText);
		}

	});
}

let selectedOrderTime = 15;

function createOrderListItem(order) {
	const status = (order.orderToOwner || 'PENDING').toUpperCase();
	const orderText = $('<div>').addClass('order-text')
		.html(`주문번호: ${order.orderNumber}<br>총 금액: ${order.totalPrice}원`);
	const buttonWrap = $('<div>').addClass('order-buttons ms-auto d-flex align-items-center');
	let li;

	if (status === 'PENDING') {
		const acceptBtn = $('<button>').addClass('acceptBtn').data('orderid', order.orderId).text('접수');
		const rejectBtn = $('<button>').addClass('rejectBtn').data('orderid', order.orderId).text('거부');

		const timeAdjust = $('<div>').addClass('time-adjust d-inline-flex align-items-center ms-2');
		const minusBtn = $('<button>').addClass('btn btn-sm btn-outline-secondary px-2').text('-')
			.on('click', function() {
				const input = $(this).siblings('input');
				const newVal = Math.max(0, parseInt(input.val()) - 5);
				input.val(newVal);
				updateTemporaryCookingTime(order.orderId, newVal);
			});

		const timeInput = $('<input>').attr({ type: 'text', value: '15', size: '2', readonly: true })
			.addClass('form-control form-control-sm text-center mx-1');

		const plusBtn = $('<button>').addClass('btn btn-sm btn-outline-secondary px-2').text('+')
			.on('click', function() {
				const input = $(this).siblings('input');
				const newVal = parseInt(input.val()) + 5;
				input.val(newVal);
				updateTemporaryCookingTime(order.orderId, newVal);
			});

		timeAdjust.append(minusBtn, timeInput, plusBtn);
		buttonWrap.append(acceptBtn, rejectBtn, timeAdjust);
	}

	else if (status === 'CONFIRMED') {
		const extendTimeBtn = $('<button>').addClass('extendTimeBtn').data('orderid', order.orderId).text('시간 추가');
		const completeBtn = $('<button>').addClass('completeBtn').data('orderid', order.orderId).text('조리 완료');
		const timerContainer = $('<div>').addClass('countdown-timer').append(
			$('<div>').addClass('circle').append(
				$('<div>').addClass('inside-text').text('--')
			)
		);
		buttonWrap.append(timerContainer, extendTimeBtn, completeBtn);

		// 🕓 진행중 타이머 시작
		startCountdown(timerContainer[0], order.cookingStartTime, order.expectCookingTime);
	}


	li = $('<li>').addClass('order-item').attr('data-orderid', order.orderId)
		.append(orderText, buttonWrap);

	return { status, li };
}

// 왼쪽 주문 리스트 렌더링
function renderOrderList(orderList) {
	console.log('🍀 renderOrderList, 받았어요:', orderList.length, '건');
	$('#newOrderList, #processingOrderList, #deliveringOrderList').empty();

	orderList.forEach(order => {
		const { status, li } = createOrderListItem(order);
		console.log('– 상태', status, '에 li 생성:', li);

		if (status === 'PENDING') {
			$('#newOrderList').prepend(li);
		} else if (status === 'CONFIRMED') {
			$('#processingOrderList').prepend(li);
		} else if (status === 'IN_DELIVERY') {
			$('#deliveringOrderList').prepend(li);
		}
	});
}

function startCountdown(timerElem, cookingStartTimeArr, expectCookingTimeArr) {
	const circle = timerElem.querySelector('.circle');
	const text = timerElem.querySelector('.inside-text');

	if (!Array.isArray(cookingStartTimeArr) || !Array.isArray(expectCookingTimeArr)) {
		text.textContent = '--';
		return;
	}

	// ✅ Java LocalDateTime => [YYYY, MM, DD, HH, mm, ss, nano]
	const start = new Date(
		cookingStartTimeArr[0],           // year
		cookingStartTimeArr[1] - 1,       // month (0-based)
		cookingStartTimeArr[2],           // day
		cookingStartTimeArr[3],           // hour
		cookingStartTimeArr[4],           // minute
		cookingStartTimeArr[5] || 0       // second
	);

	// ✅ Java LocalTime => [HH, mm]
	const cookingMinutes = (expectCookingTimeArr[0] || 0) * 60 + (expectCookingTimeArr[1] || 0);
	const endTime = new Date(start.getTime() + cookingMinutes * 60000);

	if (timerElem._intervalId) {
		clearInterval(timerElem._intervalId);
	}

	function update() {
		const now = new Date();
		const remainingMs = endTime.getTime() - now.getTime();
		const remainingMinutes = Math.floor(remainingMs / 60000);
		const elapsedPercent = Math.max((1 - (remainingMs / (cookingMinutes * 60000))) * 100, 0);

		if (remainingMinutes < 0) {
			circle.classList.add('overdue');
			text.textContent = '+' + Math.abs(remainingMinutes) + '분';
		} else {
			circle.classList.remove('overdue');
			text.textContent = remainingMinutes + '분';
		}

		circle.style.setProperty('--progress', 100 - elapsedPercent);
	}

	update();
	timerElem._intervalId = setInterval(update, 1000);
}


function startTimer(elemId, minutes) {
	const circleLength = 125.66;
	let timeLeft = minutes * 60;
	const elem = $('#' + elemId);

	function updateCircle() {
		const percent = timeLeft / (minutes * 60);
		elem.text(Math.ceil(timeLeft / 60));
		elem.css('background', `conic-gradient(#007bff ${percent * 360}deg, #eee 0deg)`);
		timeLeft--;
		if (timeLeft >= 0) setTimeout(updateCircle, 1000);
	}

	updateCircle();
}
let currentChatSubscription = null;
// 특정 주문 상세 불러오기
function loadOrderDetail(orderId) {
	console.log('▶▶▶ loadOrderDetail 시작:', orderId);
	currentOrderId = orderId;
	$('#chatMessages').empty();

	if (currentChatSubscription) {
		currentChatSubscription.unsubscribe();
		console.log('✖ 이전 구독 해제됨');
	}

	// — 기존에 있던 채팅 재구독 로직
	if (stompClient) {
		// (선택) 이전 구독 토픽 해제 로직이 필요하면 여기서 처리
		//stompClient.subscribe('/user/queue/chat', onChatMessage);
		//stompClient.subscribe('/topic/chat/' + orderId, onChatMessage);
		currentChatSubscription = stompClient.subscribe('/topic/chat/' + orderId, onChatMessage);
	}

	// — AJAX 로 상세 정보 가져오기
	$.ajax({
		url: '/owner/order/detail?orderId=' + orderId,
		method: 'GET',
		success: function(order) {
			console.log('▶▶▶ 주문 상세 응답:', order);
			renderOrderDetail(order);
		},
		error: function(err) {
			console.error('✖ 주문 상세 로드 실패:', err);
		}
	});

	fetch('/chat/getPrevMsgs/' + orderId)
		.then(r => {
			if (!r.ok) throw new Error(`HTTP ${r.status}`);
			return r.json();
		})
		.then(msgs => {
			console.log("가져온 메시지 내역 : ", msgs);
			msgs.forEach(showPrevChatMessage);
		})
		.catch(e => console.error('previous messages 가져오기 실패:', e));

}


// 주문 상세 표시
function renderOrderDetail(order) {
	$('#detailOrderNumber').text(
		order.orderNumber ? ` [${order.orderNumber.toString().padStart(3, '0')}]` : ''
	);
	$('#detailOrderId').text(order.orderId);
	$('#detailAddress').text(order.deliveryAddress);
	$('#detailStatus').text(order.orderStatus);
	$('#detailStoreRequest').text(order.storeRequest);
	$('#detailDeliveryRequest').text(order.deliveryRequest);
	$('#detailTotalPrice').text(order.totalPrice);
	$('#detailDeliveryFee').text(order.deliveryFee);
	$('#TotalPrice').text(order.deliveryFee + order.totalPrice);



	// 🧼 기존 메뉴 리스트 영역 비우기
	$('#detailMenuList').empty();


	const $detailArea = $('<div>');

	order.orderDetails.forEach(detail => {
		const $menu = $('<div>').text(`${detail.menuName}  x ${detail.quantity}`).css({ fontWeight: 'bold' });
		$detailArea.append($menu);

		if (detail.optionNames && detail.optionNames.length > 0) {
			detail.optionNames.forEach((opt, idx) => {
				const optText = `└ ${opt}`;
				$detailArea.append($('<div>').text(optText).css({ marginLeft: '10px', fontSize: '0.9em' }));
			});
		}
	});

	$('#detailMenuList').append($detailArea);

	$('#confirmBtn, #cancelBtn, #completeBtn, #startDeliveryBtn')
		.data('orderid', order.orderId)
		.hide();

	if (order.orderStatus === 'PENDING') {
		$('#confirmBtn, #cancelBtn').show();
	} else if (order.orderStatus === 'CONFIRMED') {
		$('#completeBtn, #cancelBtn').show();
	} else if (order.orderStatus === 'IN_DELIVERY') {
		$('#startDeliveryBtn').show();
	}
}

// 주문 상태 업데이트
function updateOrderStatus(orderId, url) {
	$.ajax({
		url: url,
		type: 'POST',  // ✅ method → type으로 바꿔도 안정적
		contentType: 'application/json',
		data: JSON.stringify({ orderId: orderId }),
		success: function(updatedOrder) {
			console.log('🔄 updatedOrder 응답:', updatedOrder);
			renderOrderDetail(updatedOrder);
			removeOrderFromList(updatedOrder.orderId); // ✅ 삭제 후
			addOrderToList(updatedOrder);         // ✅ 갱신
			notifyUserOrderUpdate(updatedOrder);
		},
		error: function(err) {
			console.error('상태 변경 실패:', err);
			alert('상태 변경 실패: ' + err.responseText);
		}
	});
}
<<<<<<< HEAD

=======
>>>>>>> 03d6d967cfc3b6a30349ad61624e4f1291e9d9c8
//Web Socket Connect
function connect() {
	const socket = new SockJS('/stomp');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		// /topic/store/{storeId} 구독
		stompClient.subscribe('/topic/store/' + storeId, function(message) {
			const orderData = JSON.parse(message.body);
			showNewOrderPopup(orderData);
		});
		stompClient.subscribe('/topic/store/removeOrder/' + storeId, function(message) {
			const orderData = JSON.parse(message.body);
			console.log(orderData.orderToUser);
			removeDeliveringOrderList(orderData.orderId);
			alert("배달이 완료되었습니다. 주문번호 :")
		});
		if (currentOrderId) {
			alert('오더아이디 어디선가');
			//stompClient.subscribe('/topic/chat/' + currentOrderId, onChatMessage);
			stompClient.subscribe('/topic/chat/' + currentOrderId, message => {
				console.log('채팅 메시지 도착:', message);
				onChatMessage(message);
			})
		}
	});
}
// 채팅 메시지 핸들러
function onChatMessage(message) {
	console.log('🥡 onChatMessage 호출됨:', message);
	const payload = JSON.parse(message.body);
	console.log('🥡 메시지 페이로드:', payload);
	const { senderName, senderType, text, timestamp } = payload;
	const container = document.getElementById('chatMessages');
	if (!container) {
		console.error("chatMessages element not found!");
		return;
	}
	const el = document.createElement('div');
	el.className = 'chat-message';
	el.innerHTML = `
>>>>>>> ba1e3148f2132b47e62d966867a6e5dacb52a612
	      <strong class="sender">${senderType}:</strong>
	      <span class="text">${text}</span>
	      <div class="timestamp text-muted small">
	        ${new Date(timestamp).toLocaleTimeString()}
	      </div>
	    `;
	container.append(el);
	container.scrollTop = container.scrollHeight;
}

//  보내기
function sendChat() {
	const text = $('#chatInput').val().trim();
	if (!text || !currentOrderId) return;
	const payload = {
		storeId,
		orderId: currentOrderId,
		senderName: '사장님',
		senderType: '사장님',
		text,
		timestamp: new Date().toISOString()
	};
	stompClient.send('/app/chat/' + currentOrderId, {}, JSON.stringify(payload));
	$('#chatInput').val('');
}

// 새 주문 팝업 표시
function showNewOrderPopup(order) {
	// 주문 정보 표시
	$('#orderNumber').text(order.orderNumber?.toString().padStart(3, '0') || '-');
	$('#orderInfo').text(order.menuNameList);
	$('#orderOptions').text(order.options);
	$('#orderCount').text(order.totalCount);
	$('#orderPrice').text(order.totalPrice);

	$('#storeRequest').text(order.storeRequest);
	$('#deliveryRequest').text(order.deliveryRequest);
	$('#needSpoon').text(order.needSpoon ? '예' : '아니오');

	$('#orderId').text(order.orderId);
	$('#contactNumber').text(order.contactNumber);
	$('#address').text(order.deliveryAddress);

	loadOrderList();

	// 모달 열기
	newOrderModal.show();

	// 2) 리스트에도 추가 (신규 주문이므로 PENDING 상태 가정)
	addOrderToList(order);

	// 버튼 이벤트 등록
	$('#acceptBtn').off('click').on('click', function() {
		acceptOrder(order.orderId, $('#completionTime').val());
	});
	$('#rejectBtn').off('click').on('click', function() {
		rejectOrder(order.orderId);
	});
}

function addOrderToList(order) {
	const { status, li } = createOrderListItem(order);

	if (status === 'PENDING') {
		$('#newOrderList').prepend(li);
	} else if (status === 'CONFIRMED') {
		$('#processingOrderList').prepend(li);
	} else if (status === 'IN_DELIVERY') {
		$('#deliveringOrderList').prepend(li);
	}
}

function closeNewOrderPopup() {
	// 모달 닫기
	if (newOrderModal) {
		newOrderModal.hide();
	}
}

// 배달 완료시간 + - 버튼 조절 (5분 단위)
function changeTime(minute) {
	let current = parseInt($('#completionTime').val(), 10);
	if (isNaN(current)) current = 0;
	$('#completionTime').val(current + minute);
}

// ✅ 주문 접수
function acceptOrder(orderId, completionTime) {
	const parsedTime = parseInt(completionTime);
	console.log("보내는 데이터", JSON.stringify({ orderId, completionTime: parsedTime }));

	$.ajax({
		url: '/owner/order/accept',
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify({ orderId: orderId, completionTime: parsedTime }),
		success: function(updatedOrder) {
			alert(`주문이 접수 처리되었습니다.`);
			closeNewOrderPopup();
			renderOrderDetail(updatedOrder);  // ✅ 추가
			removeOrderFromList(orderId);
			addOrderToList(updatedOrder);
			notifyUserOrderUpdate(updatedOrder);
		},
		error: function(err) {
			console.error('접수 실패:', err);
			alert('접수 실패: ' + err.responseText);
		}
	});
}




function removeOrderFromList(orderId) {
	$('#newOrderList li, #processingOrderList li').each(function() {
		const currentId = $(this).data('orderid');
		if (currentId === orderId) {
			$(this).remove();
		}
	});
}
function removeDeliveringOrderList(orderId) {
	$('#deliveringOrderList li').each(function() {
		const currentId = $(this).data('orderid');
		if (currentId === orderId) {
			$(this).remove();
		}
	});
}

// 주문 거부
function rejectOrder(orderId) {
	$.ajax({
		url: '/owner/order/reject',
		type: 'POST',
		contentType: 'application/json', // ✅ 추가
		data: JSON.stringify({ orderId: orderId }), // ✅ JSON 문자열로
		success: function(response) {
			alert(`주문이 거부 처리되었습니다.`);
			closeNewOrderPopup();
			removeOrderFromList(orderId);
		},
		error: function(err) {
			console.error('거부 실패:', err);
			alert('거부 실패: ' + err.responseText);
		}
	});
}

function extendTime(orderId, minutes) {
	$.ajax({
		url: '/owner/order/extendTime',
		method: 'POST',
		contentType: 'application/json',
		data: JSON.stringify({ orderId: orderId, minutes: minutes }),
		success: function(updatedOrder) {
			alert('시간 추가 완료.');

			// ✅ 1. 리스트에서 해당 주문 아이템 찾기
			const $li = $(`li[data-orderid='${orderId}']`);
			const timerElem = $li.find('.countdown-timer')[0];

			if (timerElem) {
				// ✅ 2. 기존 타이머 중지
				if (timerElem._intervalId) {
					clearInterval(timerElem._intervalId);
					timerElem._intervalId = null;
				}

				// ✅ 3. 타이머 UI 초기화
				timerElem.querySelector('.circle')?.classList.remove('overdue');
				timerElem.querySelector('.inside-text').textContent = '--';

				// ✅ 4. 새로 받은 시간으로 타이머 재시작
				startCountdown(timerElem, updatedOrder.cookingStartTime, updatedOrder.expectCookingTime);
			}
			notifyUserOrderUpdate(updatedOrder);
		},
		error: function(err) {
			console.error("시간 추가 실패:", err);
			alert("시간 추가 실패: " + err.responseText);
		}
	});
}

function getRemainingMinutes(cookingStartTime, expectCookingTime) {
	if (!cookingStartTime || !expectCookingTime) {
		console.warn('⚠️ cookingStartTime or expectCookingTime is missing.');
		return 0;
	}

	const start = new Date(cookingStartTime);
	if (isNaN(start.getTime())) {
		console.warn('⚠️ Invalid cookingStartTime format:', cookingStartTime);
		return 0;
	}

	const timeParts = expectCookingTime.split(':');
	if (timeParts.length < 2) {
		console.warn('⚠️ Invalid expectCookingTime format:', expectCookingTime);
		return 0;
	}

	const [h, m, s] = timeParts.map(part => parseInt(part, 10) || 0);
	const durationMs = ((h * 60 + m) * 60000) + (s * 1000);

	const endTime = start.getTime() + durationMs;
	const now = Date.now();

	const remainingMs = endTime - now;
	const remainingMinutes = Math.floor(remainingMs / 60000); // ✅ 음수도 유지

	return remainingMinutes;
}

function completeOrder(orderId) {
	$.ajax({
		url: '/owner/order/complete',
		method: 'POST',
		contentType: 'application/json',
		data: JSON.stringify({ orderId: orderId }),
		success: function(updatedOrder) {
			alert(`주문이 조리완료 처리되었습니다.`);
			renderOrderDetail(updatedOrder);
			removeOrderFromList(orderId);
			addOrderToList(updatedOrder);
			loadOrderList();
			notifyUserOrderUpdate(updatedOrder);
		},
		error: function(err) {
			console.error('조리 완료 실패:', err);
		}
	});
}

function showToast(message) {
	const toastBody = document.getElementById('orderToastBody');
	toastBody.textContent = message;

	const toastElement = document.getElementById('orderToast');
	const toast = new bootstrap.Toast(toastElement);
	toast.show();
}

function showPrevChatMessage(chat) {
	const box = document.getElementById('chatMessages');
	if (!box) return;

	const senderType = chat.senderType || "Unknown";
	let senderTypeText = '';
	switch (senderType) {
		case 'USER':
			senderTypeText = '손님';
			break;
		case 'OWNER':
			senderTypeText = '사장님';
			break;
		case 'RIDER':
			senderTypeText = '라이더';
			break;
	}
	const message = chat.message || "No message";
	const timestamp = chat.timestamp
		? new Date(chat.timestamp).toLocaleTimeString()
		: "Invalid Date";

	const el = document.createElement('div');
	el.className = 'chat-message';
	el.innerHTML = `
			<strong class="sender-type">${senderTypeText}:</strong> 
			<span class="text">${message}</span>
			<div class="timestamp small text-muted">${timestamp}</div>
		`;
	box.appendChild(el);
	box.scrollTop = box.scrollHeight;
}


// 페이지 로드 시 WebSocket 연결
$(document).ready(function() {
	// Bootstrap Modal 객체 생성
	const modalElem = document.getElementById('newOrderPopup');
	newOrderModal = new bootstrap.Modal(modalElem, {
		backdrop: 'static', // 모달 밖 클릭해도 닫히지 않도록
		keyboard: false     // ESC로 닫히지 않도록
	});

});