let map;
let storeOrders = {};  // storeOrders를 전역 변수로 선언
let storeMarkers = {}; // 마커를 저장할 객체
let overlays = {}; // 오버레이를 저장할 객체

document.addEventListener("DOMContentLoaded", async function () {
    const orderItemsContainer = document.getElementById('order-items');
    if (isNaN(riderLat) || isNaN(riderLng)) {
        console.error("위도 또는 경도가 유효하지 않습니다.");
        return;
    }

    const mapContainer = document.getElementById('map');
    const mapOption = {
        center: new kakao.maps.LatLng(riderLat, riderLng),
        level: 3
    };

    map = new kakao.maps.Map(mapContainer, mapOption);

    const deliveryRangeKm = 5;
    const deliveryRangeMeter = deliveryRangeKm * 1000;

    let isFetching = false;

    async function fetchOrders() {
        if (isFetching) return;  // 요청 중이면 다시 호출하지 않음
        isFetching = true;

        try {
            const response = await fetch('/rider/api/orders');
            const newOrders = await response.json();

            const validStates = ["PENDING", "CONFIRMED"];

            // storeName 별로 묶기
            const newStoreOrdersMap = {};
            newOrders.forEach(order => {
                if (!newStoreOrdersMap[order.storeName]) {
                    newStoreOrdersMap[order.storeName] = [];
                }
                newStoreOrdersMap[order.storeName].push(order);
            });

            for (const storeName in newStoreOrdersMap) {
                const newOrdersForStore = newStoreOrdersMap[storeName]
                    .filter(order => validStates.includes(order.orderToRider))
                    .sort((a, b) => new Date(a.orderTime) - new Date(b.orderTime));

                const prevOrdersForStore = storeOrders[storeName] || [];

                const isDifferent = () => {
                    if (newOrdersForStore.length !== prevOrdersForStore.length) return true;
                    for (let i = 0; i < newOrdersForStore.length; i++) {
                        if (newOrdersForStore[i].orderId !== prevOrdersForStore[i].orderId ||
                            newOrdersForStore[i].orderToRider !== prevOrdersForStore[i].orderToRider) {
                            return true;
                        }
                    }
                    return false;
                };

                // 변경된 가게만 처리
                if (isDifferent()) {
                    const firstOrder = newOrdersForStore[0];
                    const additionalOrdersCount = newOrdersForStore.length - 1;
                    const storeLat = firstOrder.storeLatitude;
                    const storeLng = firstOrder.storeLongitude;
                    const storePosition = new kakao.maps.LatLng(storeLat, storeLng);
                    const state = firstOrder.orderToRider;

                    let bgColor, textColor;
                    if (state === "PENDING") {
                        bgColor = "#ffffff";
                        textColor = "#000000";
                    } else if (state === "CONFIRMED") {
                        bgColor = "#2ecc71";
                        textColor = "#ffffff";
                    } else {
                        continue;
                    }

                    // 기존 마커/오버레이 제거
                    if (storeMarkers[storeName]) {
                        storeMarkers[storeName].setMap(null);
                        overlays[storeName].setMap(null);
                    }

                    const marker = new kakao.maps.Marker({
                        position: storePosition,
                        image: new kakao.maps.MarkerImage(
                            "https://ifh.cc/g/HMrtaC.png",
                            new kakao.maps.Size(40, 40)
                        )
                    });
                    marker.setMap(map);
                    storeMarkers[storeName] = marker;

                    const overlayContent = `
                        <div style="padding:10px; cursor:pointer;">
                            <div style="
                                background-color: ${bgColor};
                                margin-bottom: 10px;
                                padding: 10px;
                                border-radius: 10px;
                                color: ${textColor};
                                font-weight: bold;
                            ">
                                <div style="display: flex; justify-content: space-between; align-items: center;">
                                    <a href="javascript:void(0);" style="color: ${textColor}; text-decoration: none; font-weight: bold;" 
                                       class="view-orders" data-store-name="${storeName}">
                                       ${firstOrder.storeName} <br>배달료: ${firstOrder.deliveryFee}
                                    </a>
                                    ${additionalOrdersCount > 0 ? `<div style="margin-left: 10px; color: white; font-weight: normal;">+${additionalOrdersCount}</div>` : ''}
                                </div>
                            </div>
                        </div>
                    `;

                    const overlay = new kakao.maps.CustomOverlay({
                        position: storePosition,
                        content: overlayContent,
                        xAnchor: 0.5,
                        yAnchor: 1.2
                    });
                    overlay.setMap(map);
                    overlays[storeName] = overlay;
                }

                // storeOrders 갱신
                storeOrders[storeName] = newOrdersForStore;
            }

        } catch (error) {
            console.error("주문을 가져오는 중 오류 발생:", error);
        } finally {
            isFetching = false;
        }
    }

    // 페이지 로딩 시 첫 번째 주문 데이터 로드
    await fetchOrders();

    // 30초마다 새로운 주문 데이터 가져오기
    setInterval(fetchOrders, 30000);  // 30초마다 서버에서 주문을 갱신

    // 오버레이 클릭 시 주문 목록을 하단에 표시하는 이벤트 리스너 추가
    document.addEventListener('click', function (e) {
        if (e.target && e.target.classList.contains('view-orders')) {
            if (isFetching) return;  // 요청 중이면 클릭 처리 안 함
            isFetching = true;
            const storeName = e.target.getAttribute('data-store-name');
            displayOrdersForStore(storeName);
            isFetching = false;
        }
    });
});

// 특정 가게의 주문 목록을 하단에 표시하는 함수
function displayOrdersForStore(storeName) {
    const orderItemsContainer = document.getElementById('order-items');
    orderItemsContainer.innerHTML = ''; // 기존 목록 초기화

    // 해당 가게의 주문 목록 가져오기
    const filteredOrders = storeOrders[storeName];
    console.log("해당 가게의 주문 목록:", filteredOrders);  // 해당 가게의 주문 목록 로그로 확인

    filteredOrders.forEach(order => {
        // PENDING과 CONFIRMED 상태만 표시
        if (order.orderToRider !== "PENDING" && order.orderToRider !== "CONFIRMED") {
            return; // 해당 주문은 목록에 추가하지 않음
        }

        const orderDiv = document.createElement('div');
        orderDiv.classList.add('order-item');

        // 상태에 따른 테두리 색상 지정
        let borderColor;
        switch (order.orderToRider) {
            case 'PENDING':
                borderColor = '#bdc3c7';
                break;
            case 'CONFIRMED':
                borderColor = '#2ecc71';
                break;
            case 'DISPATCHED':  // 'DISPATCHED' 상태 추가
                borderColor = '#3498db';  // blue color for dispatched
                break;
            default:
                borderColor = '#ccc'; // 기본 색
        }

        orderDiv.innerHTML = ` 
            <div class="order-card" style="border: 3px solid ${borderColor}; border-radius: 10px; padding: 10px; margin-bottom: 10px;">
                <h5>${order.storeName}</h5>
                <p>배달 번호: <span>${order.orderId}</span></p>
                <p>주문일자: <span>${order.orderTimeFormatted}</span></p>
                <p>배달 요청 사항: <span>${order.deliveryRequest}</span></p>
                <p>배달료: <span>${order.deliveryFee}</span></p>
                <p>조리 완료 예상 시간: <span>${order.expectCookingTimeFormatted}</span></p>
                <p>배달 상태: <span>${order.orderToRider}</span></p>
                <p>배달 주소: <span>${order.deliveryAddress}</span></p>
                <button class="assign-btn" data-order-id="${order.orderId}">배차</button>
                <button class="cancel-btn" data-order-id="${order.orderId}">취소</button>
            </div>
        `;

        orderItemsContainer.appendChild(orderDiv);
    });

    document.addEventListener("click", async (e) => {
        if (e.target.classList.contains("assign-btn")) {
            const orderId = e.target.dataset.orderId;
            const response = await fetch(`/rider/api/orders/assign`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ orderId })
            });

            if (response.ok) {
                alert("배차가 완료되었습니다.");
                // 배차 후 주문 상태를 DISPATCHED로 갱신하고, 지도에서 해당 마커 및 오버레이 삭제
                const storeName = e.target.closest('.order-item').querySelector('.view-orders').getAttribute('data-store-name');
                const order = storeOrders[storeName].find(o => o.orderId === orderId);
                order.orderToRider = "DISPATCHED";
                
                // 마커와 오버레이 삭제
                storeMarkers[storeName].setMap(null);
                overlays[storeName].setMap(null);
                storeOrders[storeName] = storeOrders[storeName].filter(o => o.orderId !== orderId); // DOM에서 주문 삭제
                
                fetchOrders(); // 배차 후 주문 목록 갱신
            } else {
                alert("배차 실패");
            }
        }
    });
}
