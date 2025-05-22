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

    /* 새 주문 알림용 채널 */
    stompClient.subscribe(`/topic/user/${userUuid}`, msg => {
      const dto = JSON.parse(msg.body);
      showOrderNotification(dto);
      subscribeOrderChannels(dto.orderId);
    });

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
function subscribeOrderChannels(orderId) {
  if (!stompClient || currentOrderIds.has(orderId)) return;
  currentOrderIds.add(orderId);

  /* 상태 알림 */
  stompClient.subscribe(`/topic/order/${orderId}`, msg => {
    const update = JSON.parse(msg.body);
    console.log('[order-topic]', orderId, update);
    showOrderNotification(update);       // 토스트
    //updateStatusChart?.(update.stage);   // 선택 UI
    //moveRiderMarker?.(update.location?.lat, update.location?.lng);
    updateCookingProgress?.(update.stage);
    startExpectedTimeCountdown?.(
        update.expectCookingTime, update.expectDeliveryTime);
  });

  /* 채팅 메시지 */
  stompClient.subscribe(`/topic/chat/${orderId}`, msg => {
    const chat = JSON.parse(msg.body);
    showChatMessage(chat);
  });
}

/* ────────── 알림 토스트 & 채팅창 시스템 메시지 ────────── */
function showOrderNotification(dto) {
  const status = dto.orderToOwner || dto.orderStatus || 'UNKNOWN';
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
    `<strong>${chat.senderName}:</strong> ${chat.text}
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
  const orderId = Array.from(currentOrderIds).at(-1); // 최근 방
  stompClient.send(`/app/chat/${orderId}`, {}, JSON.stringify({
    orderId, senderName: userId, text: input.value.trim(),
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
