document.addEventListener("DOMContentLoaded", function() {
    // 결제 버튼 클릭 시
    document.getElementById("chargeButton").addEventListener("click", function() {
        // 모달 내에서 입력된 금액 가져오기
        const amountInput = document.getElementById("amount");
        const amount = amountInput.value;

        // 금액이 비어 있거나 0보다 작은 경우 처리
        if (!amount || amount <= 0) {
            alert("유효한 금액을 입력해주세요.");
            return;
        }

        // 토스 결제 API로 결제 요청
        fetch('/user/initiateTossPayment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ amount: amount })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 토스 결제 페이지로 리다이렉트
                window.location.href = data.paymentUrl;
            } else {
                alert("결제 요청에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("결제 처리 중 오류가 발생했습니다.");
        });
    });
});
