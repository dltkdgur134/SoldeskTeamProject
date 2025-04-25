let map;
let storeOrders = {};
let storeMarkers = {};
let overlays = {};
let isFetching = false;
const validStates = ["PENDING", "CONFIRMED"];

// 주문 업데이트 비교 함수
const isDifferent = (newOrdersForStore, prevOrdersForStore) => {
    if (newOrdersForStore.length !== prevOrdersForStore.length) return true;
    for (let i = 0; i < newOrdersForStore.length; i++) {
        if (newOrdersForStore[i].orderId !== prevOrdersForStore[i].orderId ||
            newOrdersForStore[i].orderToRider !== prevOrdersForStore[i].orderToRider ||
            newOrdersForStore[i].deliveryFee !== prevOrdersForStore[i].deliveryFee) {
            return true;
        }
    }
    return false;
};

// 마커와 오버레이 업데이트 함수
const updateStoreMarkerAndOverlay = (storeName, firstOrder, additionalOrdersCount) => {
    const storePosition = new kakao.maps.LatLng(firstOrder.storeLatitude, firstOrder.storeLongitude);
    const state = firstOrder.orderToRider;

    let bgColor = "#ffffff", textColor = "#000000";
    if (state === "CONFIRMED") {
        bgColor = "#2ecc71";
        textColor = "#ffffff";
    }

    // 기존 마커와 오버레이 삭제
    if (storeMarkers[storeName]) {
        storeMarkers[storeName].setMap(null);
        delete storeMarkers[storeName];
    }

    if (overlays[storeName]) {
        overlays[storeName].setMap(null);
        delete overlays[storeName];
    }

    // 새 마커 추가
    const marker = new kakao.maps.Marker({
        position: storePosition,
        image: new kakao.maps.MarkerImage("https://ifh.cc/g/HMrtaC.png", new kakao.maps.Size(40, 40))
    });
    marker.setMap(map);
    storeMarkers[storeName] = marker;

    // 새 오버레이 추가
    const overlayContent = `
        <div style="padding:10px; cursor:pointer;" data-uniq="${storeName}-${Date.now()}">
            <div style="background-color: ${bgColor}; margin-bottom: 10px; padding: 10px; border-radius: 10px; color: ${textColor}; font-weight: bold;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <a href="javascript:void(0);" style="color: ${textColor}; text-decoration: none;" class="view-orders" data-store-name="${storeName}">
                        ${firstOrder.storeName} <br>배달료: ${firstOrder.deliveryFee}
                    </a>
                    ${additionalOrdersCount > 0 ? `<div style="margin-left: 10px; color: ${textColor};">+${additionalOrdersCount}</div>` : ''}
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
};

// **전역에서 fetchOrders 함수 정의**
async function fetchOrders() {
    if (isFetching) return;
    isFetching = true;
    console.log('📦 fetchOrders 호출됨');
    try {
        const response = await fetch('/rider/api/orders');
        const newOrders = await response.json();

        const newStoreOrdersMap = {};
        newOrders.forEach(order => {
            if (!newStoreOrdersMap[order.storeName]) {
                newStoreOrdersMap[order.storeName] = [];
            }
            newStoreOrdersMap[order.storeName].push(order);
        });

        // 새로운 주문과 기존 주문을 비교하여 갱신할 부분만 처리
        for (const storeName in newStoreOrdersMap) {
            const newOrdersForStore = newStoreOrdersMap[storeName]
                .filter(order => validStates.includes(order.orderToRider))  // 상태 필터링
                .sort((a, b) => new Date(a.orderTime) - new Date(b.orderTime));
            const prevOrdersForStore = storeOrders[storeName] || [];

            // 주문 내용이 다를 경우에만 처리
            if (isDifferent(newOrdersForStore, prevOrdersForStore)) {
                storeOrders[storeName] = newOrdersForStore;

                const firstOrder = newOrdersForStore[0];
                const additionalOrdersCount = newOrdersForStore.length - 1;

                updateStoreMarkerAndOverlay(storeName, firstOrder, additionalOrdersCount);
            }
        }

        // 배차된 주문 처리 (마커 및 오버레이 제거)
        for (const storeName in storeOrders) {
            if (!(storeName in newStoreOrdersMap)) {
                console.log(`🧼 ${storeName} - 주문이 모두 배차되어 마커와 오버레이 제거`);
				
                if (storeMarkers[storeName]) {
                    storeMarkers[storeName].setMap(null);
                    delete storeMarkers[storeName];
                }

                if (overlays[storeName]) {
                    overlays[storeName].setMap(null);
                    delete overlays[storeName];
                }

                delete storeOrders[storeName];
            }
        }
    } catch (error) {
        console.error("주문을 가져오는 중 오류 발생:", error);
    } finally {
        isFetching = false;
    }
}

document.addEventListener("DOMContentLoaded", async function () {
    if (isNaN(riderLat) || isNaN(riderLng)) {
        console.error("위도 또는 경도가 유효하지 않습니다.");
        return;
    }

    map = new kakao.maps.Map(document.getElementById('map'), {
        center: new kakao.maps.LatLng(riderLat, riderLng),
        level: 3
    });

    await fetchOrders();
    setInterval(fetchOrders, 30000); // 30초마다 주문 새로고침

    document.addEventListener('click', function (e) {
        if (e.target && e.target.classList.contains('view-orders')) {
            if (isFetching) return;
            const storeName = e.target.getAttribute('data-store-name');
            displayOrdersForStore(storeName);
        }
    });

    window.addEventListener("focus", async function () {
        console.log("페이지 포커스 돌아옴, 새로고침 시작");
        isFetching = false;
        await fetchOrders();
    });
});

// 매장별 주문 표시
function displayOrdersForStore(storeName) {
    const orderItemsContainer = document.getElementById('order-items');
    orderItemsContainer.innerHTML = '';

    const filteredOrders = (storeOrders[storeName] || []).filter(order =>
        validStates.includes(order.orderToRider)
    );

    filteredOrders.forEach(order => {
        const orderDiv = document.createElement('div');
        orderDiv.classList.add('order-item');

        let borderColor = '#ccc';
        if (order.orderToRider === 'PENDING') borderColor = '#bdc3c7';
        else if (order.orderToRider === 'CONFIRMED') borderColor = '#2ecc71';
        else if (order.orderToRider === 'DISPATCHED') borderColor = '#3498db';

        orderDiv.innerHTML = `
            <div class="order-card" style="border: 3px solid ${borderColor}; border-radius: 10px; padding: 10px; margin-bottom: 10px;">
                <h5>${order.storeName}</h5>
                <p>주문번호: <span>${order.orderId}</span></p>
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
}

document.addEventListener("click", async (e) => {
    if (e.target.classList.contains("assign-btn")) {
        const orderId = e.target.dataset.orderId;
        console.log("클릭한 주문의 orderId:", orderId);

        try {
            const response = await fetch(`/rider/api/orders/assign`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ orderId: orderId })
            });

            if (!response.ok) {
                throw new Error('배차 요청 실패');
            }

            const result = await response.json();
            alert(result.message || '배차 요청 성공');

            // storeOrders에서 해당 주문 제거
            for (const storeName in storeOrders) {
                const updatedOrders = storeOrders[storeName].filter(order => order.orderId !== orderId);
                storeOrders[storeName] = updatedOrders;
            }

            // 화면에 보여지는 주문 리스트 다시 렌더링
            const clickedStoreName = e.target.closest('.order-item')?.querySelector('h5')?.innerText;
            if (clickedStoreName) {
                displayOrdersForStore(clickedStoreName);
				const ordersForStore = storeOrders[clickedStoreName];
				   if (ordersForStore && ordersForStore.length > 0) {
				       const firstOrder = ordersForStore[0];
				       const additionalOrdersCount = ordersForStore.length - 1;
				       updateStoreMarkerAndOverlay(clickedStoreName, firstOrder, additionalOrdersCount);
				   }
            }

            // 오버레이 반영을 위해 fetchOrders 호출
            await fetchOrders();
        } catch (error) {
            alert("배차 요청 중 오류 발생: " + error.message);
        }
    }
});
