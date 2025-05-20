let statusChart, map, marker;
const userId = /*[[${#authentication.principal.user.userId}]]*/ 'guest';
const stages = ['접수됨','조리중','배달중','배송완료'];
// HTML 인라인 스크립트로부터 넘어온 전역 orderId 사용
// => 이제 에디터에도 빨간줄 안 뜹니다!
console.log('Order ID:', orderId);

function initChart(currentStage) {
  const ctx = document.getElementById('statusChart').getContext('2d');
  const data = stages.map((s, i) => currentStage >= i ? 1 : 0);
  statusChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: stages,
      datasets: [{
        data,
        backgroundColor: stages.map((_, i) =>
          i === currentStage ? '#2D7121':'#ddd'
        ),
      }]
    },
    options: {
      indexAxis: 'y',
      scales: {
        x: { display: false },
        y: {
          ticks: { font: { size: 14 } }
        }
      },
      plugins: { legend: { display: false } }
    }
  });
}

function initMap() {
  const container = document.getElementById('map');
  const center = new kakao.maps.LatLng( /* 위도,경도 */ );
  const options = { center, level: 3 };
  new kakao.maps.Map(container, options);
}

/**---------------------------------------------------
 * 1) WebSocket 연결 & 채팅/상태 업데이트 구독
 *--------------------------------------------------*/
function connectChat() {
  const socket = new SockJS('/stomp');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, frame => {
    console.log('WebSocket 연결됨:', frame);

    // 1-1) 채팅 메시지 구독
    stompClient.subscribe('/topic/chat/' + orderId, onChatMessage);

    // 1-2) (예시) 주문 상태/위치 업데이트 구독
    stompClient.subscribe('/topic/order/' + orderId, msg => {
      const payload = JSON.parse(msg.body);
      updateStatusChart(payload.stage);
      moveRiderMarker(payload.location.lat, payload.location.lng);
    });
  }, error => {
    console.error('WebSocket 연결 실패:', error);
  });
}

/**---------------------------------------------------
 * 2) 채팅 수신 핸들러
 *--------------------------------------------------*/
function onChatMessage(message) {
  const { sender, text, timestamp } = JSON.parse(message.body);
  const container = document.getElementById('chatMessages');
  const el = document.createElement('div');
  el.className = 'chat-message';
  el.innerHTML = `
    <strong class="sender">${sender}:</strong>
    <span class="text">${text}</span>
    <div class="timestamp text-muted small">
      ${new Date(timestamp).toLocaleTimeString()}
    </div>
  `;
  container.append(el);
  container.scrollTop = container.scrollHeight;
}

/**---------------------------------------------------
 * 3) 채팅 전송 함수
 *--------------------------------------------------*/
function sendChat() {
  const input = document.getElementById('chatInput');
  const text = input.value.trim();
  if (!text || !stompClient) return;

  const payload = {
    orderId,
    sender: '사용자',            // 또는 사용자 이름/ID
    text,
    timestamp: new Date().toISOString()
  };
  stompClient.send('/app/chat/' + orderId, {}, JSON.stringify(payload));
  input.value = '';
}

/**---------------------------------------------------
 * 3.5) 채팅 읽었는지 확인하는 함수 "1"
 *--------------------------------------------------*/
function receiveMessage(message) {
  const isFocused = $('#chatPanel').is(':visible'); // 또는 채팅방 상태 확인
  if (!isFocused) {
    $('#unreadBadge').text('1').show(); // 누적도 가능
  }

  $('#chatMessages').append(`<div>${message}</div>`);
}

function markMessagesAsRead() {
  $('#unreadBadge').hide(); // 뱃지 숨기기
  // 필요하다면 서버에 "읽음" 처리 요청도 추가
  // $.post('/chat/read', { userId: ... });
}

$('.chat-button').click(function () {
  $('#chatPanel').show();
  markMessagesAsRead();
});


/**---------------------------------------------------
 * 4) 페이지 로드 시 초기화
 *--------------------------------------------------*/
window.addEventListener('load', () => {
  connectChat();

  // 전송 버튼 & 엔터키 바인딩
  document.getElementById('sendChatBtn')
    .addEventListener('click', sendChat);
  document.getElementById('chatInput')
    .addEventListener('keypress', e => {
      if (e.key === 'Enter') sendChat();
    });


  // 1) 초기 상태(예: 서버에서 내려주는 currentStage 변수로)
  const currentStage = /*[[${currentStage}]]*/ 1;
  initChart(currentStage);
  kakao.maps.load(initMap);
});



