// websocket.js  (Order-centric 구독 전환)
let stompClient = null;
const currentOrderIds = new Set();

/* ─────────────────────────────── WebSocket 연결 ────────────────────────────── */
function connectGlobalWebSocket() {
	const userUuid = document.body.dataset.userid;
	if (!userUuid) return console.warn('userId 없음, WS 미연결');

	stompClient = Stomp.over(new SockJS('/stomp'));

	// ① 성공 콜백
	const onConnected = frame => {
		console.log('🌐 connected:', frame.headers);


		/* 로그인 직후 서버에 “진행 중 주문 목록” 요청 */
		fetch('/user/order/active-ids')
			.then(r => {
				if (!r.ok) throw new Error(`HTTP ${r.status}`); // 500 방어
				return r.json();
			})
			.then(ids => ids.forEach(subscribeOrderChannels))
			.catch(e => console.error('active-ids 가져오기 실패:', e));
	};

	// ② 실패 콜백
	const onError = error => {
		console.error('❌ WS 연결 실패:', error);
	};

	stompClient.connect({}, onConnected, onError);
}

/* ────────── 주문별 채팅·상태 토픽 구독  ────────── */
function subscribeOrderChannels(paramOrderId) {
	if (!stompClient || currentOrderIds.has(paramOrderId)) return;
	currentOrderIds.add(paramOrderId);
	/* 상태 알림 */
	stompClient.subscribe(`/topic/order/${paramOrderId}`, msg => {
		const update = JSON.parse(msg.body);
		if (typeof orderId === 'undefined') {
			let statusMsg = "";
			switch(update.orderToUser) {
				case 'COOKING':
					if (update.orderToOwner === 'IN_DELIVERY') {
						statusMsg = "조리 완료"
						break;
					}
					statusMsg = "조리 중";
					break;
				case 'DELIVERING':
					statusMsg = "배달 중";
					break;
				case 'COMPLETED':
					statusMsg = "배달 완료";
					break;
				case 'CANCELED':
					statusMsg = "주문 취소";
					break;
				default:
					break;
			}
			showToast(`📦 주문 #${update.orderId} 상태: "${statusMsg}"`);
		}
		if (update.orderId === orderId) {
			console.log("subscribeOrderChannels 살아있음");
			console.log('[order-topic]', paramOrderId, update);
			showOrderNotification(update);       // 토스트
			if (typeof update.currentStatus === 'number') {
				if (typeof window.updateProgress === 'function') {
					window.updateProgress(update.currentStatus);
				}
			}
			//moveRiderMarker?.(update.location?.lat, update.location?.lng);
			if (window.hasOwnProperty('updateCookingProgress') &&
				typeof window.updateCookingProgress === 'function') {
				window.updateCookingProgress(update.stage);
			}
			if (typeof window.startExpectedTimeCountdown === 'function') {
				window.startExpectedTimeCountdown(update.expectCookingTime, update.expectDeliveryTime);
			}
			switch (update.orderToUser) {
				case 'CONFIRMED':
					updateProgress(2);
					break;
				case 'COOKING':
					updateProgress(3);
					break;
				case 'CANCELED':
					window.location.href = "/orderHistory";
				default:
					break;
			}
		} else {
			console.log("걸러진 주문");
		}
		
	});

	/* 채팅 메시지 */
	stompClient.subscribe(`/topic/chat/${paramOrderId}`, msg => {
		const chat = JSON.parse(msg.body);
		//showToast(msg);
		if (chat.orderId === orderId) {
			showChatMessage(chat);
		} else {
			console.log("테스트:걸러진 주문");
		}
	});
}

/* ────────── 알림 토스트 & 채팅창 시스템 메시지 ────────── */
function showOrderNotification(dto) {
	//const status = dto.orderToOwner || dto.orderStatus || 'UNKNOWN';
	const status = dto.orderToUser || 'UNKNOWN';
	showToast(`📦 주문 #${dto.orderId} 상태: "${status}"`);

	// (선택) 시스템 메시지를 채팅창에도 넣기
	if (document.getElementById('chatMessages')) {
		const div = document.createElement('div');
		div.className = 'chat-message system';
		div.innerHTML = `<em>시스템:</em> 주문 상태가 <b>${status}</b> 로 변경되었습니다.`;
		chatMessages.append(div);
		chatMessages.scrollTop = chatMessages.scrollHeight;
	}
}

/* ────────── 채팅 표시 ────────── */
function showChatMessage(chat) {
	const box = document.getElementById('chatMessages');
	if (!box) return;
	const el = document.createElement('div');
	el.className = 'chat-message';
	el.innerHTML =
		`<strong class="sender-type">${chat.senderType}:</strong> 
		<span class="text">${chat.text}</span>
     <div class="timestamp small text-muted">
       ${new Date(chat.timestamp).toLocaleTimeString()}
     </div>`;
	box.appendChild(el);
	box.scrollTop = box.scrollHeight;
}

/* ────────── 토스트 UI ────────── */
function showToast(msg) {
	let wrap = document.getElementById('toast-container');
	if (!wrap) {
		wrap = Object.assign(document.createElement('div'), {
			id: 'toast-container',
			style: 'position:fixed;top:10px;right:10px;z-index:10000'
		});
		document.body.append(wrap);
	}
	const toast = Object.assign(document.createElement('div'), {
		className: 'toast-message',
		textContent: msg,
		style: 'background:#333;color:#fff;padding:10px 20px;margin-bottom:10px;' +
			'border-radius:5px;opacity:0.9;box-shadow:0 2px 5px rgba(0,0,0,0.3);' +
			'transition:opacity .5s'
	});
	wrap.append(toast);
	setTimeout(() => { toast.style.opacity = 0; setTimeout(() => toast.remove(), 500); }, 3000);
}

/* ────────── 채팅 전송 ────────── */
function sendChat() {
	const input = document.getElementById('chatInput');
	if (!input.value.trim() || !stompClient) return;
	//const orderId = Array.from(currentOrderIds).at(-1); // 최근 방
	//const orderId = document.getElementById('orderIdInput').value;
	stompClient.send(`/app/chat/${orderId}`, {}, JSON.stringify({
		orderId, senderType: '손님', senderName: userId, text: input.value.trim(),
		timestamp: new Date().toISOString()
	}));
	input.value = '';
}

/* ────────── 초기화 ────────── */
document.addEventListener('DOMContentLoaded', () => {
	connectGlobalWebSocket();
	document.getElementById('sendChatBtn')?.addEventListener('click', sendChat);
	document.getElementById('chatInput')?.addEventListener('keypress',
		e => e.key === 'Enter' && sendChat());
});