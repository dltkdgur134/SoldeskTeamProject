let map;

document.addEventListener("DOMContentLoaded", async function () {
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

    // 주문 데이터 불러오기
    const orders = await fetchOrders(riderLat, riderLng);
    displayOrdersOnMap(orders);
});

async function fetchOrders(lat, lng) {
    try {
        const response = await fetch(`/api/orders/nearby?lat=${lat}&lng=${lng}`);
        return await response.json(); // [{ id, storeName, deliveryFee, storeLatitude, storeLongitude }]
    } catch (e) {
        console.error("주문 데이터를 불러오는 데 실패했습니다", e);
        return [];
    }
}

function displayOrdersOnMap(orders) {
    orders.forEach(order => {
        const position = new kakao.maps.LatLng(order.storeLatitude, order.storeLongitude);

        let imageSrc = 'https://ifh.cc/g/HMrtaC.png',
            imageSize = new kakao.maps.Size(30, 30),
            imageOption = { offset: new kakao.maps.Point(27, 69) };

        let markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

        let marker = new kakao.maps.Marker({
            position: position,
            image: markerImage
        });
        marker.setMap(map);

        const content = `
            <div class="delivery-request" id="order-${order.id}" style="cursor:pointer;">
                <strong>${order.storeName}</strong><br>${order.deliveryFee}원
            </div>
        `;

        let customOverlay = new kakao.maps.CustomOverlay({
            map: map,
            content: content,
            position: position,
            yAnchor: 1
        });

        setTimeout(() => {
            const element = document.getElementById(`order-${order.id}`);
            if (element) {
                element.addEventListener('click', function () {
                    displayOrderInfo(order.storeName, order.deliveryFee);
                    // 여기서 상세 정보 더 넣을 수 있음
                });
            }
        }, 50);
    });
}

function displayOrderInfo(storeName, price) {
    const orderNameElement = document.getElementById("storeName").getElementsByTagName("span")[0];
    const orderPriceElement = document.getElementById("deliveryFee").getElementsByTagName("span")[0];

    orderNameElement.textContent = storeName;
    orderPriceElement.textContent = price + "원";
}
