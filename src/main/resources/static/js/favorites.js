document.addEventListener("DOMContentLoaded", () => {
  const list = document.querySelector(".favorite-list");

  list?.addEventListener("click", async (e) => {
    const btn = e.target.closest(".heart-btn");
    if (!btn) return;

    e.preventDefault(); // a 링크 클릭 막기
    const storeId = btn.dataset.id;

    // 1) 서버에 찜 해제 요청
    await fetch(`/favorite/${storeId}`, { method: "DELETE" });

    // 2) 화면에서 해당 li 제거
    const li = btn.closest(".favorite-item");
    li?.remove();

    // 3) 전부 지워졌으면 빈 안내 문구 표시
    if (!list.children.length) {
      document.querySelector(".favorite-empty")?.classList.remove("hidden");
    }
  });
});
