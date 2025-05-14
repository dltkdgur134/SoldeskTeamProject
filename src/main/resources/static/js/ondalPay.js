document.addEventListener("DOMContentLoaded", function () {
    const userUUID = /*[[${userUUID}]]*/ 0;
	 // Thymeleaf를 사용하여 서버에서 전달된 값을 JavaScript로 삽입

    const chargeButton = document.getElementById("chargeButton");

    chargeButton.addEventListener("click", function () {
        const amountInput = document.getElementById("amount");
        const amount = parseInt(amountInput.value, 10);

		if (isNaN(amount) || amount <= 0) {
		    alert("충전할 금액을 입력해주세요.");
		    return;
		}

        // TossPayments 초기화
        const tossPayments = TossPayments("test_ck_Z61JOxRQVEEOgm2alYRRVW0X9bAq");

        // 고유한 orderId 생성 (UUID 사용)
        const orderId = crypto.randomUUID();

		const successUrl = `https://localhost:8443/walletCharge/success?userUUID=${userUUID}`;
		const failUrl = `https://localhost:8443/walletCharge/fail?userUUID=${userUUID}`;
		const authorization = "Basic test_ck_Z61JOxRQVEEOgm2alYRRVW0X9bAq";
        // 결제 요청
        tossPayments.requestPayment({
            amount: amount,
            orderId: orderId,
            orderName: "온달 지갑 충전",
            successUrl: successUrl,
            failUrl: failUrl,
			headers: {
			        Authorization: authorization
			    }
        });
    });
});
