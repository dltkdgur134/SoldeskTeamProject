// websocket.js

let stompClient;

function initGlobalWebsocket() {
  if (!userId) return;  // 치환 실패 시 구독 안 함
  const socket = new SockJS('/stomp'); // SockJS를 통해 /stomp 엔드포인트로 연결
  stompClient = Stomp.over(socket); // STOMP 프로토콜을 통해 메시지 처리
  stompClient.connect({}, frame => {
    console.log('Global WS connected:', frame); // 연결 성공 시 로그

    // /topic/user/{userId} 구독 → 모든 페이지에서 주문 변경 알림 받음
    stompClient.subscribe('/topic/user/' + userId, message => {
      const data = JSON.parse(message.body); // 서버에서 보낸 메시지를 JSON으로 파싱
	  console.log("수신된 메시지:", data); // 수신된 데이터 로그
	 
	   // 수신된 데이터로 알림 표시
      showOrderNotification(data);
    });
  }, err => {
    console.error('Global WS error', err);  // 연결 실패 시 오류 로그
  });
}

function showOrderNotification(orderDto) {
  // 간단히 alert or 원하는 토스트 라이브러리로 띄우기
  alert(`[주문 #${orderDto.orderId}] 상태가 "${orderDto.orderStatus}" 로 변경되었습니다.`);
}

// DOM 로드 완료 후 바로 실행
document.addEventListener('DOMContentLoaded', initGlobalWebsocket);
