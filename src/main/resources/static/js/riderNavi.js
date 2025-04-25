const axios = require('axios');
const express = require('express');
const app = express();

const KAKAO_API_KEY = 'e55542fa93e461f8923c2c474499e925';
const KAKAO_URL = 'https://dapi.kakao.com/v2/maps/directions.json';

app.use(express.json());

// 클라이언트에서 보내는 길찾기 요청 처리
app.post('/rider/api/get-route', async (req, res) => {
    const { startLat, startLng, endLat, endLng } = req.body;

    try {
        // 카카오맵 길찾기 API 호출
        const response = await axios.post(KAKAO_URL, {
            start: [startLng, startLat],
            end: [endLng, endLat],
        }, {
            headers: {
                Authorization: `KakaoAK ${KAKAO_API_KEY}`,
            },
        });

        if (response.data.routes && response.data.routes.length > 0) {
            const route = response.data.routes[0];
            const duration = route.legs[0].duration.text; // 예상 시간
            const distance = route.legs[0].distance.text; // 예상 거리

            // 경로 데이터 반환
            res.json({
                duration,
                distance,
                route, // 경로 정보 포함
            });
        } else {
            res.status(400).json({ error: '경로를 찾을 수 없습니다.' });
        }
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: '길찾기 API 호출 실패' });
    }
});

// 서버 실행
app.listen(3000, () => {
    console.log('서버가 3000번 포트에서 실행 중');
});