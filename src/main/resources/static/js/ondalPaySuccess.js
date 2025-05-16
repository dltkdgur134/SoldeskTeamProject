// ondalPaySuccess.js
document.addEventListener("DOMContentLoaded", async function () {
    const urlParams = new URLSearchParams(window.location.search);
    const paymentKey = urlParams.get("paymentKey");
    const orderId = urlParams.get("orderId");
    const amount = parseInt(urlParams.get("amount"));

    if (paymentKey && orderId && amount) {
        try {
            const response = await fetch("/api/ondal-wallet/charge", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams({
                    paymentKey,
                    orderId,
                    amount
                })
            });

            if (response.ok) {
                alert("온달페이 충전이 완료되었습니다.");
            } else {
                alert("충전 처리에 실패했습니다.");
            }
        } catch (e) {
            alert("서버와의 통신 중 오류가 발생했습니다.");
        } finally {
            window.location.href = "/ondalPay"; // 다시 페이지 새로고침
        }
    } else {
        alert("필수 결제 정보가 누락되었습니다.");
        window.location.href = "/ondalPay";
    }
});
