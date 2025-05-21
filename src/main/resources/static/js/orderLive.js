// HTML data- 속성에서 변수 읽기
let orderStompClient = null;
console.log('orderId check:', orderId);
function connectOrderWebSocket() {
  if (!orderId) {
    console.warn('❗ orderId 없음, 주문 채팅 및 상태 업데이트 생략');
    return;
  }

  const socket = new SockJS('/stomp');
  orderStompClient = Stomp.over(socket);

  orderStompClient.connect({}, frame => {
    console.log('🌐 주문별 WebSocket 연결 성공:', frame);

	orderStompClient.subscribe('/topic/chat/' + orderId, message => {
	  console.log('채팅 메시지 도착:', message);
	  onChatMessage(message);
	});
    orderStompClient.subscribe('/topic/order/' + orderId, message => {
	  console.log('주문 상태 메시지 도착:', message);
      const payload = JSON.parse(message.body);
      //updateStatusChart(payload.stage);
      updateCookingProgress(payload.stage);
      startExpectedTimeCountdown(payload.expectCookingTime, payload.expectDeliveryTime);
    });
	console.log('현재 orderId:', orderId);
	console.log('현재 userUuid:', userUuid);
    if (userUuid) {
      orderStompClient.subscribe('/topic/user/' + userUuid, message => {
        const payload = JSON.parse(message.body);
        if (payload.orderToOwner === 'CANCELED') {
          updateCookingProgress('REJECTED');
        }
      });
    }
  }, error => {
    console.error('❌ 주문별 WebSocket 연결 실패:', error);
  });
}

// ✅ 채팅 수신
function onChatMessage(message) {
  const { sender, text, timestamp } = JSON.parse(message.body);
  const container = document.getElementById('chatMessages');
  if (!container) return;

  const el = document.createElement('div');
  el.className = 'chat-message';
  el.innerHTML = `
    <strong>${sender}:</strong>
    <span>${text}</span>
    <div class="timestamp text-muted small">${new Date(timestamp).toLocaleTimeString()}</div>
  `;
  container.appendChild(el);
  container.scrollTop = container.scrollHeight;
}

// ✅ 채팅 전송
function sendChatMessage() {
  const input = document.getElementById('chatInput');
  const text = input?.value.trim();
  if (!text || !orderStompClient) return;

  const payload = {
    orderId: orderId,
    sender: '사용자',
    text: text,
    timestamp: new Date().toISOString()
  };
  orderStompClient.send('/app/chat/' + orderId, {}, JSON.stringify(payload));
  input.value = '';
}

// ✅ 조리/배달 ProgressBar
window.updateCookingProgress = function(stage) {
	console.log('현재 단계:', stage);
  const bar = document.getElementById('cookingProgressBar');
  if (!bar) return;

  switch (stage) {
    case 'PENDING':
      setProgress(bar, 0, '접수 대기', 'bg-secondary');
      break;
    case 'COOKING':
      setProgress(bar, 50, '조리중', 'bg-info');
      break;
    case 'COOKING_COMPLETED':
      setProgress(bar, 75, '조리완료', 'bg-success');
      break;
    case 'IN_DELIVERY':
      setProgress(bar, 90, '배달중', 'bg-warning');
      break;
    case 'DELIVERED':
    case 'COMPLETED':
      setProgress(bar, 100, '배달완료', 'bg-primary');
      break;
	case 'REJECTED': 
	  setProgress(bar, 100, '주문 거부됨', 'bg-danger'); 
	  alert('해당 주문은 가게에서 거부되었습니다.');
	  disableOrderUI();
	  break;  
    default:
      console.warn('❓ Unknown stage:', stage);
  }
}
//주문 거부시 활성 함수
function disableOrderUI() {
  // 채팅 입력창, 버튼 비활성화
  const input = document.getElementById('chatInput');
  const sendBtn = document.getElementById('sendChatBtn');
  if (input) input.disabled = true;
  if (sendBtn) sendBtn.disabled = true;

  // 예상 시간 제거
  const display = document.getElementById('expectedTimeDisplay');
  if (display) display.textContent = '주문이 거부되었습니다';

  // 타이머 정지
  if (expectedTimerInterval) {
    clearInterval(expectedTimerInterval);
    expectedTimerInterval = null;
  }
}
function setProgress(elem, percent, text, colorClass) {
  elem.style.width = percent + '%';
  elem.className = 'progress-bar progress-bar-striped progress-bar-animated ' + colorClass;
  elem.textContent = text;
}

// ✅ 예상 남은 시간
let totalExpectedSeconds = 0;
let expectedTimerInterval = null;

function startExpectedTimeCountdown(expectCookingTime, expectDeliveryTime) {
  const display = document.getElementById('expectedTimeDisplay');
  if (!expectCookingTime || !expectDeliveryTime || !display) {
    display.textContent = '--분';
    return;
  }

  const cookingMinutes = (expectCookingTime[0] || 0) * 60 + (expectCookingTime[1] || 0);
  const deliveryMinutes = (expectDeliveryTime[0] || 0) * 60 + (expectDeliveryTime[1] || 0);
  totalExpectedSeconds = (cookingMinutes + deliveryMinutes) * 60;

  if (expectedTimerInterval) clearInterval(expectedTimerInterval);
  updateExpectedTimeUI();

  expectedTimerInterval = setInterval(() => {
    totalExpectedSeconds -= 60;
    if (totalExpectedSeconds <= 0) {
      clearInterval(expectedTimerInterval);
      display.textContent = '도착 임박!';
      return;
    }
    updateExpectedTimeUI();
  }, 60000);
}

function updateExpectedTimeUI() {
  const remainingMinutes = Math.ceil(totalExpectedSeconds / 60);
  const display = document.getElementById('expectedTimeDisplay');
  if (!display) return;

  display.textContent = remainingMinutes <= 1 ? '도착 임박!' : `남은 시간: ${remainingMinutes}분`;
}

// ✅ 초기화
window.addEventListener('load', () => {
  connectOrderWebSocket();

  const sendBtn = document.getElementById('sendChatBtn');
  const chatInput = document.getElementById('chatInput');

  if (sendBtn && chatInput) {
    sendBtn.addEventListener('click', sendChatMessage);
    chatInput.addEventListener('keypress', e => {
      if (e.key === 'Enter') sendChatMessage();
    });
  }


  updateCookingProgress(currentStage);
  startExpectedTimeCountdown(expectCookingTime, expectDeliveryTime);
});
