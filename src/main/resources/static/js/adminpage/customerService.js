// 고객센터 초기화 모듈  –  ES Module
export function initCustomerService(root) {
	alert("접근은함");
  if (!root) return;

  /* === 캐시 === */
  const roleBtns   = root.querySelectorAll('#roleFilter .nav-link');
  if (!roleBtns.length) return;          // 해당 탭 아님

  const catSel   = root.querySelector('#csCategory');
  const kwInput  = root.querySelector('#csKeyword');
  const form     = root.querySelector('#csSearchForm');
  const rows     = Array.from(root.querySelectorAll('tbody tr'));

  /* === 필터 === */
  const filter = () => {
    const role = root.querySelector('#roleFilter .nav-link.active')?.dataset.role || '';
    const cate = catSel.value;
    const kw   = kwInput.value.trim().toLowerCase();

    rows.forEach(tr => {
      const okRole = !role || tr.dataset.role === role;
      const okCat  = !cate || tr.dataset.category === cate;
      const okKw   = !kw ||
            tr.dataset.title.toLowerCase().includes(kw) ||
            tr.dataset.content.toLowerCase().includes(kw);
      tr.style.display = (okRole && okCat && okKw) ? '' : 'none';
    });
  };

  /* === 이벤트 === */
  roleBtns.forEach(b => b.addEventListener('click', () => {
    roleBtns.forEach(x => x.classList.remove('active'));
    b.classList.add('active');
    filter();
  }));
  catSel.addEventListener('change', filter);
  form.addEventListener('submit', e => { e.preventDefault(); filter(); });

  /* === 모달 === */
  const modalEl = document.getElementById('replyModal');
  const modal   = bootstrap.Modal.getOrCreateInstance(modalEl);
  const mId  = modalEl.querySelector('#modalComplainId');
  const mTit = modalEl.querySelector('#modalTitle');
  const mCon = modalEl.querySelector('#modalContent');

  root.querySelectorAll('.reply-btn').forEach(btn =>
    btn.addEventListener('click', () => {
      const tr = btn.closest('tr');
      mId.value        = tr.dataset.id;
      mTit.textContent = tr.dataset.title;
      mCon.textContent = tr.dataset.content;
      modal.show();           // 안전하게 수동 호출
    })
  );
}
