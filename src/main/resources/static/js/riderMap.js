let map;
let intervalId = null;

document.addEventListener("DOMContentLoaded", function () {
    console.log("lat from global var:", riderLat);
    console.log("lng from global var:", riderLng);

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

    startGenerating(); // 시작 시 자동 생성

    document.getElementById('stopBtn').addEventListener('click', stopGenerating);
    document.getElementById('startBtn').addEventListener('click', startGenerating);
});

function startGenerating() {
    if (intervalId === null) {
        intervalId = setInterval(() => {
            addRandomMarker(riderLat, riderLng);
        }, 5000); // 주기적으로 마커 생성
    }
}

function stopGenerating() {
    if (intervalId !== null) {
        clearInterval(intervalId);
        intervalId = null;
    }
}

function displayOrderInfo(storeName, price) {
    const orderNameElement = document.getElementById("storeName").getElementsByTagName("span")[0];
    const orderPriceElement = document.getElementById("deliveryFee").getElementsByTagName("span")[0];

    orderNameElement.textContent = storeName;
    orderPriceElement.textContent = price + "원";
}

function addRandomMarker(baseLat, baseLng) {
    let randomLat = baseLat + (Math.random() - 0.5) * 0.01;
    let randomLng = baseLng + (Math.random() - 0.5) * 0.01;

    let imageSrc = 'https://ifh.cc/g/HMrtaC.png',
        imageSize = new kakao.maps.Size(30, 30),
        imageOption = { offset: new kakao.maps.Point(27, 69) };

    let markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption),
        markerPosition = new kakao.maps.LatLng(randomLat, randomLng);

    let marker = new kakao.maps.Marker({
        position: markerPosition,
        image: markerImage
    });
    marker.setMap(map);

    const storeName = "랜덤식당";
    const price = Math.floor(Math.random() * 5000) + 1000;

    const orderId = 'order-' + crypto.randomUUID();

    let content = `
        <div class="delivery-request" id="${orderId}" 
        style="cursor:pointer;">
            <strong>${storeName}</strong><br>${price}원
        </div>
    `;

    let customOverlay = new kakao.maps.CustomOverlay({
        map: map,
        content: content,
        position: markerPosition,
        yAnchor: 1
    });

    setTimeout(() => {
        const element = document.getElementById(orderId);
        if (element) {
            element.addEventListener('click', function () {
                displayOrderInfo(storeName, price);
            });
        }
    }, 50);
}
