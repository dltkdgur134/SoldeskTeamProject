// websocket.js
let stompClient = null;
let currentOrderIds = new Set();

function connectGlobalWebSocket() {
  const userId = document.body.dataset.userid;
  if (!userId) {
    console.warn('❗ userId 없음, WebSocket 연결 생략');
    return;
  }

  const socket = new SockJS('/stomp');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, frame => {
    console.log('🌐 WebSocket connected:', frame);

    stompClient.subscribe('/topic/user/' + userId, message => {
      const data = JSON.parse(message.body);
      showOrderNotification(data);

      if (data.orderId && !currentOrderIds.has(data.orderId)) {
        subscribeOrderTopics(data.orderId);
        currentOrderIds.add(data.orderId);
      }
    });
  }, error => {
    console.error('❌ WebSocket 연결 실패:', error);
  });
}

// ✅ 특정 주문 채팅/상태 구독
function subscribeOrderTopics(orderId) {
  if (!stompClient) return;

  stompClient.subscribe('/topic/chat/' + orderId, message => {
    const chat = JSON.parse(message.body);
    showChatMessage(chat);
  });

  stompClient.subscribe('/topic/order/' + orderId, msg => {
    const payload = JSON.parse(msg.body);
    updateStatusChart(payload.stage);
    moveRiderMarker(payload.location.lat, payload.location.lng);
    updateCookingProgress(payload.stage);
    startExpectedTimeCountdown(payload.expectCookingTime, payload.expectDeliveryTime);
  });
}

// ✅ 알림 표시
function showOrderNotification(orderDto) {
  const status = orderDto.orderToOwner || orderDto.orderStatus || 'UNKNOWN';
  if (!orderDto.orderId) {
    console.warn('알림 데이터 누락:', orderDto);
    return;
  }
  showToast(`📦 주문 #${orderDto.orderId} 상태: "${status}"`);
}

// ✅ 채팅 표시
function showChatMessage(chat) {
  const container = document.getElementById('chatMessages');
  if (!container) return;

  const el = document.createElement('div');
  el.className = 'chat-message';
  el.innerHTML = `
    <strong>${chat.sender}:</strong> ${chat.text}
    <div class="timestamp small text-muted">${new Date(chat.timestamp).toLocaleTimeString()}</div>
  `;
  container.appendChild(el);
  container.scrollTop = container.scrollHeight;

  if (!$('#chatPanel').is(':visible')) {
    $('#unreadBadge').text('1').show();
  }
}

// ✅ 토스트 메세지
function showToast(message) {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.style.position = 'fixed';
    container.style.top = '10px';
    container.style.right = '10px';
    container.style.zIndex = 10000;
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = 'toast-message';
  toast.textContent = message;
  Object.assign(toast.style, {
    background: '#333', color: '#fff', padding: '10px 20px',
    marginBottom: '10px', borderRadius: '5px', opacity: '0.9',
    boxShadow: '0 2px 5px rgba(0,0,0,0.3)', transition: 'opacity 0.5s ease'
  });

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => container.removeChild(toast), 500);
  }, 3000);
}

// ✅ 채팅 전송
function sendChat() {
  const input = document.getElementById('chatInput');
  if (!input || !stompClient) return;

  const text = input.value.trim();
  if (!text) return;

  const payload = {
    orderId: Array.from(currentOrderIds)[0],  // 가장 최근 주문에 보내기
    sender: '사용자',
    text: text,
    timestamp: new Date().toISOString()
  };
  stompClient.send('/app/chat/' + payload.orderId, {}, JSON.stringify(payload));
  input.value = '';
}

// ✅ 메시지 읽음 처리
function markMessagesAsRead() {
  $('#unreadBadge').hide();
}

$('.chat-button').click(function () {
  $('#chatPanel').show();
  markMessagesAsRead();
});

// ✅ 초기화
document.addEventListener('DOMContentLoaded', () => {
  connectGlobalWebSocket();

  const sendBtn = document.getElementById('sendChatBtn');
  const chatInput = document.getElementById('chatInput');

  if (sendBtn && chatInput) {
    sendBtn.addEventListener('click', sendChat);
    chatInput.addEventListener('keypress', e => {
      if (e.key === 'Enter') sendChat();
    });
  }
});
