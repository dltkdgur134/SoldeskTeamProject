// /js/adminpage/customerService.js – ES Module
export function initCustomerService(root) {
  if (!root) return;

  const roleBtns = root.querySelectorAll('#roleFilter .nav-link');
  if (!roleBtns.length) return;

  const catSel  = root.querySelector('#csCategory');
  const kwInput = root.querySelector('#csKeyword');
  const form    = root.querySelector('#csSearchForm');
  const tbody   = root.querySelector('tbody');
  const pager   = root.querySelector('#csPagination');

  /* ===== 상수 ===== */
  let page = 0, size = 20;
  const ROLE = {USER:'사용자',RIDER:'라이더',OWNER:'업주',GUEST:'비회원'};
  const CAT  = {FOOD_ISSUE:'음식 문제',DELAY:'배달 지연',PAY:'정산/결제',BUSINESS:'사업자/매장'};
  const STAT = {PENDING:['bg-light text-dark','대기'],
                IN_PROGRESS:['bg-warning text-dark','진행중'],
                RESOLVED:['bg-success','완료']};

  /* ===== 데이터 로드 ===== */
  async function loadData(goPage=0){
    page=goPage;
    const q=new URLSearchParams({
      page,size,
      role: root.querySelector('#roleFilter .active')?.dataset.role||'',
      keyword: kwInput.value.trim()
    });
    const res=await fetch(`/api/complains?${q}`);
    if(!res.ok) return alert('목록 로드 실패');
    const data=await res.json();

    tbody.innerHTML=data.content.map(rowHtml).join('');
    renderPager(data);
  }

  /* ===== <tr> ===== */
  function rowHtml(c, i) {
    const esc = s => String(s).replace(/[&<>"']/g, m => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[m]));

    const map = { WAIT: 'PENDING', IN_PROGRESS: 'IN_PROGRESS', RESOLVED: 'RESOLVED' };
    const [cls, st] = STAT[map[c.status] ?? c.complainStatus] ?? STAT.PENDING;
    const imgIcon = c.hasImg ? '<i class="bi bi-image text-primary me-1"></i>' : ''; // ✅ 수정된 부분

    return `
      <tr data-id="${esc(c.id)}">
        <td>${i + 1 + page * size}</td>
        <td class="text-start">${imgIcon}${esc(c.title)}</td>
        <td>${esc(c.userId)}</td>
        <td><span class="badge bg-secondary">${ROLE[c.role] ?? c.role}</span></td>
        <td>${dayjs(c.createdAt).format('YYYY-MM-DD HH:mm')}</td>
        <td><span class="badge ${cls}">${st}</span></td>
        <td><button class="btn btn-sm btn-outline-primary reply-btn">답변</button></td>
      </tr>`;
  }

  /* ===== pagination ===== */
  function renderPager(p){
    const cur=p.number,total=p.totalPages;
    if(total<=1){pager.innerHTML='';return;}

    let h='<ul class="pagination pagination-sm mb-0">';
    const item=(n,txt,d=false,a=false)=>`
      <li class="page-item ${d?'disabled':''} ${a?'active':''}">
        <a class="page-link" data-page="${n}" href="#">${txt}</a></li>`;
    h+=item(cur-1,'«',cur===0);
    for(let i=0;i<total;i++){
      if(i===0||i===total-1||Math.abs(i-cur)<=2){
        h+=item(i,i+1,false,i===cur);
      }else if(i===1&&cur>3||i===total-2&&cur<total-4){
        h+='<li class="page-item disabled"><span class="page-link">…</span></li>';
        i=i<cur?cur-2:total-2;
      }
    }
    h+=item(cur+1,'»',cur===total-1);
    h+='</ul>';
    pager.innerHTML=h;
  }

  /* ===== 이벤트 ===== */
  roleBtns.forEach(b=>b.addEventListener('click',()=>{
    roleBtns.forEach(x=>x.classList.remove('active'));
    b.classList.add('active'); loadData(0);
  }));
  catSel.addEventListener('change',()=>loadData(0));
  form.addEventListener('submit',e=>{e.preventDefault();loadData(0);});
  pager.addEventListener('click',e=>{
    const a=e.target.closest('a[data-page]'); if(!a) return;
    e.preventDefault(); loadData(+a.dataset.page);
  });

  /* ===== 모달 ===== */
  const modalEl  = document.getElementById('replyModal');
  const modal    = bootstrap.Modal.getOrCreateInstance(modalEl);
  const mId      = modalEl.querySelector('#modalComplainId');
  const mTit     = modalEl.querySelector('#modalTitle');
  const mCon     = modalEl.querySelector('#modalContent');
  const mImgs    = modalEl.querySelector('#complainImages');
  const rText    = modalEl.querySelector('#replyText');
  const rInput   = modalEl.querySelector('#replyImages');
  const rPrev    = modalEl.querySelector('#replyPreview');
  const rForm    = modalEl.querySelector('#replyForm');

  /* ── 답변 버튼 클릭 시 모달 오픈 + Ajax로 상세 조회 ── */
  tbody.addEventListener('click', async e => {
    const btn = e.target.closest('.reply-btn');
    if (!btn) return;

    const tr = btn.closest('tr');
    const complainId = tr.dataset.id;

    try {
      const res = await fetch(`/api/admin/complains/${complainId}`);
      if (!res.ok) throw new Error('서버 응답 오류');
      const data = await res.json();

      mId.value = data.complainId;
      mTit.textContent = data.complainTitle;
      mCon.textContent = data.complainContent;

      mImgs.innerHTML = data.images?.length
        ? data.images.map(src => `<img src="/img/complain/${src}" class="w-25 rounded me-2 mb-2">`).join('')
        : '<p class="text-muted mb-0">첨부 이미지 없음</p>';

      rText.value = '';
      rInput.value = '';
      rPrev.innerHTML = '';
      modal.show();

    } catch (err) {
      console.error(err);
      alert('문의 상세를 불러오지 못했습니다.');
    }
  });

  /* ── 답변 이미지 프리뷰 ── */
  rInput.addEventListener('change',()=>{
    rPrev.innerHTML='';
    [...rInput.files].forEach(f=>{
      const url=URL.createObjectURL(f);
      rPrev.insertAdjacentHTML('beforeend',
        `<img src="${url}" class="w-25 rounded me-2 mb-2">`);
    });
  });

  /* ── 답변 저장 ── */
  rForm.addEventListener('submit',async e=>{
    e.preventDefault();
    const id = mId.value;
    const fd = new FormData();
    fd.append('content', rText.value.trim());
    [...rInput.files].forEach(f=>fd.append('images',f));

    const res = await fetch(`/api/complains/${id}/reply`,{method:'POST',body:fd});
    if(res.ok){
      alert('답변이 저장되었습니다.');
      modal.hide(); loadData(page);
    }else{
      alert('저장 실패'); console.error(await res.text());
    }
  });

  /* ===== 초기 ===== */
  loadData(0);
}
