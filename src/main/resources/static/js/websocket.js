// websocket.js

let stompClient;

function initGlobalWebsocket() {
  if (!userId) return;  // 치환 실패 시 구독 안 함
  const socket = new SockJS('/stomp');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, frame => {
    console.log('Global WS connected:', frame);

    // /topic/user/{userId} 구독 → 모든 페이지에서 주문 변경 알림 받음
    stompClient.subscribe('/topic/user/' + userId, message => {
      const data = JSON.parse(message.body);
      showOrderNotification(data);
    });
  }, err => {
    console.error('Global WS error', err);
  });
}

function showOrderNotification(orderDto) {
  // 간단히 alert or 원하는 토스트 라이브러리로 띄우기
  alert(`[주문 #${orderDto.orderId}] 상태가 "${orderDto.orderStatus}" 로 변경되었습니다.`);
}

// DOM 로드 완료 후 바로 실행
document.addEventListener('DOMContentLoaded', initGlobalWebsocket);
