// /js/adminpage/adminpage.js  (ES Module)

// ❖ 다른 탭 모듈에서 직접 export 필요
import { loadOwnerRiderApprovals } from './ownerRiderApproval.js';

/* ───── 공통 유틸 ───── */
function logout() {
  fetch('/admin/logout', { method: 'GET' })
    .then(() => (window.location.href = '/admin/login'))
    .catch(err => {
      console.error('로그아웃 실패:', err);
      alert('로그아웃에 실패했습니다.');
    });
}
window.logout = logout;

function formatDate(d) {
  const days = ['일', '월', '화', '수', '목', '금', '토'];
  const m = d.getMonth() + 1;
  const day = d.getDate();
  const wd = days[d.getDay()];
  let h = d.getHours();
  const mm = d.getMinutes().toString().padStart(2, '0');
  const am = h < 12 ? '오전' : '오후';
  if (h === 0) h = 12;
  if (h > 12) h -= 12;
  return `${m}. ${day} (${wd}) ${am} ${h}:${mm}`;
}
function updateTime() {
  document.getElementById('today-date').textContent = formatDate(new Date());
}

/* ───── 탭 로드 함수 ───── */
export function loadSetting(url, btn) {
  // 탭 active
  document.querySelectorAll('.tab-buttons button').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');

  fetch(url)
    .then(res => res.text())
    .then(html => {
      const contentEl = document.getElementById('setting-content'); // root div
      contentEl.innerHTML = html;

      /* === 탭별 전용 모듈 === */
      if (url.includes('customerService')) {
        import('/js/adminpage/customerService.js')
          .then(mod => mod.initCustomerService(contentEl))
          .catch(console.error);

      } else if (url.includes('storeapproval')) {
        import('/js/adminpage/storeapproval.js')
          .then(mod => mod.initStoreApproval(contentEl))
          .catch(console.error);

      } else if (url.includes('usersetting')) {
        import('/js/adminpage/usersetting.js')
          .then(mod => mod.initUserSetting(contentEl))
          .catch(console.error);

      } else if (url.includes('ownerRiderapproval')) {
        import('/js/adminpage/ownerRiderApproval.js')
          .then(mod => mod.loadOwnerRiderApprovals(contentEl))
          .catch(console.error);
      }
    })
    .catch(err => {
      document.getElementById('setting-content').innerHTML =
        '<p class="text-danger p-3">로드에 실패했습니다.</p>';
      console.error(err);
    });
}

/* ───── 초기 이벤트 바인딩 ───── */
document.addEventListener('DOMContentLoaded', () => {
  updateTime();
  setInterval(updateTime, 1000);

  // 탭 클릭
  document.querySelectorAll('.tab-buttons button').forEach(btn => {
    btn.addEventListener('click', () => {
      const url = btn.getAttribute('data-url');
      if (url) loadSetting(url, btn);
    });
  });

  // URL 파라미터(tab) 첫 진입 처리
  const params = new URLSearchParams(window.location.search);
  const tab = params.get('tab');
  if (tab) {
    const map = {
      operation: 'usersetting',
      printer: 'storeapproval',
      alarm: 'ownerRiderapproval',
      delivery: 'customerService',
    };
    const urlPart = map[tab];
    if (urlPart) {
      const btn = [...document.querySelectorAll('.tab-buttons button')]
        .find(b => b.getAttribute('data-url')?.includes(urlPart));
      btn?.click();
    }
  }

  // 단독 접근 시 오너/라이더 승인 자동 로드
  if (window.location.pathname.includes('approval')) {
    loadOwnerRiderApprovals();
  }
});
