let map;
let storeOrders = {};
let storeMarkers = {};
let overlays = {};
let isFetching = false;
const validStates = ["PENDING", "CONFIRMED"];
window.addEventListener('message', (event) => {
  const routeData = event.data;
  console.log("Received Route Data:", routeData);
  sessionStorage.setItem('routeData', JSON.stringify(routeData));
  location.href = `/rider/pickupStart/${routeData.orderId}`;
});
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
    ;`
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
		// 주문 목록 갱신 후, 모든 매장 주문 목록을 다시 표시
		for (const storeName in storeOrders) {
			displayOrdersForStore(storeName);
		}
	} catch (error) {
		console.error("주문을 가져오는 중 오류 발생:", error);
	} finally {
		isFetching = false;
	}
}

document.addEventListener("DOMContentLoaded", async function() {
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

	document.addEventListener('click', function(e) {
		if (e.target && e.target.classList.contains('view-orders')) {
			if (isFetching) return;
			const storeName = e.target.getAttribute('data-store-name');
			displayOrdersForStore(storeName);
		}
	});

	window.addEventListener("focus", async function() {
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
            </div>
        `

		orderItemsContainer.appendChild(orderDiv);
	});
}

document.addEventListener("click", async (e) => {
	if (e.target.classList.contains("assign-btn")) {
		const orderId = e.target.dataset.orderId;
		console.log("클릭한 주문의 orderId:", orderId);

		try {
			const orderResponse = await fetch(`/rider/api/orders/${orderId}/navi`);
			if (!orderResponse.ok) throw new Error('주문 정보를 가져오는 데 실패했습니다.');
			const order = await orderResponse.json();
			/*aa8f8d7334d919ea0276dfc23dd37cc5*/
			/*6a82e6474b08332bbf4be73f53d5c0bb*/
			const REST_API_KEY = 'aa8f8d7334d919ea0276dfc23dd37cc5';
			const url = 'https://apis-navi.kakaomobility.com/v1/directions';


			const storeLat = order.storeLatitude;
			const storeLng = order.storeLongitude;
			const endLat = order.deliveryAddressLatitude;
			const endLng = order.deliveryAddressLongitude;



			const origin = `${riderLng},${riderLat}`;
			const destination = `${endLng},${endLat}`;
			const waypoints = `${storeLng},${storeLat}`;
			console.log("origin:", origin);
			console.log("destination:", destination);
			console.log("waypoints:", waypoints);

			const headers = {
				'Authorization': `KakaoAK ${REST_API_KEY}`,
				'Content-Type': 'application/json'
			};

			const queryParams = new URLSearchParams({
				origin,
				destination,
				waypoints,
				timestamp: Date.now().toString()
			});
			const requestUrl = `${url}?${queryParams}`;

			const routeresponse = await fetch(requestUrl, {
				method: 'GET',
				headers: headers
			});

			if (!routeresponse.ok) throw new Error('경로 정보 요청 실패');
			const routeData = await routeresponse.json();

			const path1 = []; // 출발지 → 경유지
			const path2 = []; // 경유지 → 목적지

			// 각 구간별 폴리라인 경로 추출
			routeData.routes[0].sections[0].roads.forEach(road => {
			  const vertexes = road.vertexes;
			  for (let i = 0; i < vertexes.length; i += 2) {
			    path1.push({ lat: vertexes[i + 1], lng: vertexes[i] });
			  }
			});

			routeData.routes[0].sections[1].roads.forEach(road => {
			  const vertexes = road.vertexes;
			  for (let i = 0; i < vertexes.length; i += 2) {
			    path2.push({ lat: vertexes[i + 1], lng: vertexes[i] });
			  }
			});

			console.log(routeData.routes[0].sections.length);
			console.log(riderLat, riderLng);
			const distance = routeData.routes[0].summary.distance;
			const duration = routeData.routes[0].summary.duration;
			const distanceKm = (distance / 1000).toFixed(2);
			const minutes = Math.floor(duration/60);
			const seconds = Math.floor(duration%60);

			
			
			
			
			console.log("path1 length:", path1.length);
			console.log("path2 length:", path2.length);
			console.log("map 객체:", map);
			console.log(routeData);
			// 새 창 열기
			const routeWindow = window.open('', '_blank', 'width=1500,height=1500');

			if (!routeWindow || routeWindow.closed) {
				alert('팝업이 차단되었습니다. 팝업 차단을 해제해주세요.');
				return;
			}

			// 새 창 HTML 작성
			routeWindow.document.write(`
        <html>
          <head>
            <title>길찾기 결과</title>
            <meta charset="utf-8" />
			<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
			<style>
			  body { margin:0; padding:0; font-family: 'Segoe UI', sans-serif; }
			  #map { width: 100vw; height: 100vh; }
			  .info-box {
			    position: absolute;
			    top: 1rem;
			    left: 1rem;
			    z-index: 1000;
			    background: rgba(255,255,255,0.95);
			    border-radius: 0.5rem;
			    padding: 1rem;
			    box-shadow: 0 0 10px rgba(0,0,0,0.2);
			    max-width: 300px;
			  }
			  .btn-accept {
			    width: 100%;
			    margin-top: 0.5rem;
			  }
			  .overlay-content {
			    background-color: #fff;
			    border: 1px solid #000;
			    padding: 5px;
			    font-size: 12px;
			    color: #333;
			    border-radius: 5px;
			 }
			</style>
			/*e5d3b43b5ba403cc978d5770a28e29af*/
			/*a82eb7e13124954eb1c020ac4cece497*/
            <script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=e5d3b43b5ba403cc978d5770a28e29af"></script>
          </head>
          <body>
            <div id="map"></div>
			<div class="info-box">
			  <h5 class="mb-2">배달 경로 정보</h5>
			  <p id="summary" class="mb-2 text-muted"></p>
			  <button id="accept-btn" class="btn btn-primary btn-accept">배차 수락</button>
			</div>

            <script>
			const startLat = ${riderLat};  // 라이더 현재 위치
			const startLng = ${riderLng};
			const waypointLat = ${storeLat};  // 경유지: 가게 위치
			const waypointLng = ${storeLng};
			const endLat = ${endLat};        // 목적지: 고객 주소
			const endLng = ${endLng};
			const path1 = ${JSON.stringify(path1)};
			const path2 = ${JSON.stringify(path2)};
			const summaryText = "거리: ${distanceKm} km, 시간: ${minutes}분 ${seconds}초";
			const distance = ${distance}; // 숫자 그대로
			const duration = ${duration}; // 숫자 그대로

			console.log("총 거리 (m):", distance);
			console.log("총 시간 (s):", duration);
			console.log("총 거리 (km):", ${distanceKm});
			console.log("분",${minutes});
			console.log("초",${seconds});
			
			const mapContainer = document.getElementById('map');
			const mapOption = {
                center: new kakao.maps.LatLng(startLat, startLng),
                level: 4
              };
              const map = new kakao.maps.Map(mapContainer, mapOption);

              // 출발지 마커
              new kakao.maps.Marker({
                map: map,
                position: new kakao.maps.LatLng(startLat, startLng)
              });
			  //경유지(=가게 위치) 마커 추가
			   new kakao.maps.Marker({
			     map: map,
			     position: new kakao.maps.LatLng(waypointLat, waypointLng)
			   });
              // 도착지 마커
              new kakao.maps.Marker({
                map: map,
                position: new kakao.maps.LatLng(endLat, endLng)
              });
			  // 출발지 오버레이
			  const startOverlay = new kakao.maps.CustomOverlay({
		      map: map,
		      position: new kakao.maps.LatLng(startLat, startLng),
		      content: '<div class="overlay-content">라이더 허브 주소</div>',
		      yAnchor: 1
		  	  });

			  // 경유지 오버레이
			  const waypointOverlay = new kakao.maps.CustomOverlay({
			  map: map,
			  position: new kakao.maps.LatLng(waypointLat, waypointLng),
			  content: '<div class="overlay-content">가게 위치</div>',
			  yAnchor: 1
			  });

			  // 도착지 오버레이
			  const endOverlay = new kakao.maps.CustomOverlay({
			  map: map,
			  position: new kakao.maps.LatLng(endLat, endLng),
			  content: '<div class="overlay-content">배달 장소</div>',
			  yAnchor: 1
			  });
			  
			  //경로 폴리라인
			  const polyline1 = new kakao.maps.Polyline({
			    path: path1.map(p => new kakao.maps.LatLng(p.lat, p.lng)),
			    strokeWeight: 5,
			    strokeColor: '#FF0000',
			    strokeOpacity: 1,
			    strokeStyle: 'solid'
			  });

			  const polyline2 = new kakao.maps.Polyline({
			    path: path2.map(p => new kakao.maps.LatLng(p.lat, p.lng)),
			    strokeWeight: 5,
			    strokeColor: '#0000FF',
			    strokeOpacity: 1,
			    strokeStyle: 'solid'
			  });

			  polyline1.setMap(map);
			  polyline2.setMap(map);

              document.getElementById('summary').innerText = summaryText;

              document.getElementById('accept-btn').addEventListener('click', async () => {
				const expectMinute = ${minutes};  // 예상 배달 시간(분)
				const expectSecond = ${seconds}; // 예상 배달 시간(초)
				
                try {
                  const res = await fetch('/rider/api/orders/assign', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
						 orderId: "${orderId}",
						 expectMinute: expectMinute,
						 expectSecond: expectSecond
					 })
                  });
                  if (res.ok) {
					// 예상 분,초를 서버에 전달
					const data = await res.json();
                    
					alert('배차 완료');
					
					// 경로 데이터를 부모창에 전달
					const routeData = {
					orderId: "${orderId}",
					start: { lat: startLat, lng: startLng },
					waypoint: { lat: waypointLat, lng: waypointLng },
					end: { lat: endLat, lng: endLng },
					path1: path1,
					path2: path2
					};
					
					if (window.opener && !window.opener.closed) {
						 window.opener.postMessage(routeData, '*');
					    window.opener.fetchOrders();  // 부모 창의 주문 목록 갱신
					  }
                  } else {
                    alert('배차 수락 실패');
                  }
				  window.close();
                } catch (err) {
                  console.error(err);
                  alert('오류 발생');
                }
              });
            </script>
          </body>
        </html>
      `);

			routeWindow.document.close();

		} catch (error) {
			console.error(error);
			alert('오류가 발생했습니다. 다시 시도해 주세요.');
		}
	}
});