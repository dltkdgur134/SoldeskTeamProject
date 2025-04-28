$(function () {
  $(".nav-item > .active").css("color", "red");
  $(".nav-link").on("click", function () {
    $(".nav-link").removeClass("active").css("color", "#ffc107");
    $(this).addClass("active").css("color", "red");
  });
});

/* ───────────────────────────────  검색 로직  ─────────────────────────────── */
document.addEventListener('DOMContentLoaded', function() {
    var searchInput = document.getElementById('inp-search');
	var searchBtn = document.getElementById('btn-search');
    var listWrapper = document.getElementById('list-wrapper');
    var list = document.getElementById('list-autocomplete');
    var reenterBtn = document.getElementById('btn-address-reset');
	var borderSearchDiv = document.getElementById('search-box');
    var activeIndex = -1;

	function showHistory() {
	const history = loadHistory();
		clearList();
     	  history.forEach(word => {
	    list.appendChild(createItem(word, true)); // li 생성 후 추가
	  });

	}
	
	if(hadAddress()){
		activeIndex=-1;
		showReenterBtn();
		searchInput.setAttribute("placeholder" , "뭐 먹을까?");
		searchInput.setAttribute("value" , sessionStorage.getItem("food")+"");
		showHistory();
		
	}else{
		showAddrHistory();
	}

	
	function loadHistory() {
	  return JSON.parse(localStorage.getItem("searchHistory") || "[]");
	}
	
	function saveHistory(keyword) {
	  if (!keyword) return;
	  let history = loadHistory();
	  history = history.filter(item => item !== keyword);
	  history.unshift(keyword);
	  history = history.slice(0, 10);  // 최대 10개만 저장
	  localStorage.setItem("searchHistory", JSON.stringify(history));
	}

	searchBtn.addEventListener('click', function(e){
		e.preventDefault();
		const query = searchInput.value.trim();
		
		if(!query)return;
		
		if(!hadAddress()){
		
		sessionStorage.setItem("address" , query);
		searchInput.setAttribute("value", sessionStorage.getItem("food") || "");	
		searchInput.setAttribute("placeholder" , "뭐 먹을까?");
		showHistory();
		showReenterBtn();
		
	}else{
		
		sessionStorage.setItem("food" , query);
		const firstItem = document.querySelector('#list-autocomplete li');
		saveHistory(query);
		
		location.href("/searchStoreInRadius?orignal=" + query + "&bestMatcher="+firstItem );
		
	}
		
		clearList();		
		
	});
	
	function loadAddrHistory() {
	  return JSON.parse(localStorage.getItem('addrHistory') || '[]');
	}
	
	function showAddrHistory() {
	  clearList();
	  const hist = loadAddrHistory();
	  hist.forEach(a => list.appendChild(createItem(a, /* isHistory */ true)));
	  listWrapper.classList.remove('d-none');
	  listWrapper.classList.add('d-flex');
	}
	
	function saveAddrHistory(addr) {
	  if (!addr) return;
	  let hist = loadAddrHistory();
	  hist = hist.filter(a => a !== addr);   // 중복 제거
	  hist.unshift(addr);                    // 최신 항목 맨 앞
	  hist = hist.slice(0, 10);              // 10개 유지
	  localStorage.setItem('addrHistory', JSON.stringify(hist));
	}
	
	function clearList() {
	  list.innerHTML = '';
	  activeIndex = -1;
	}
	

	
	function createItem(text, isHistory = false) {
		const li = document.createElement('li');
		li.classList.add('autocomplete-item');
		
		// <i> 아이콘 : ⏰(fa-clock) 또는 🔍(fa-search)
		const icon = document.createElement('i');
		icon.classList.add('fa',
		                   isHistory ? 'fa-clock' : 'fa-search');

		// 텍스트
		const span = document.createElement('span');
		span.classList.add('item-text');
		span.textContent = text;

		// 조립
		li.appendChild(icon);
		li.appendChild(span);
	
	  li.addEventListener('mousedown', function(){
		searchInput.value = text;
		searchBtn.click();
	  });
	  return li;
	}

	searchInput.addEventListener('input', handleAutocomplete);

	async function handleAutocomplete() {
	  const q = searchInput.value.trim();
	  if(!hadAddress()){
		clearList();
		showAddrHistory();
		return;
	  }
	  if (!q) {
		const history = loadHistory();                // 빈 값이면 리스트 숨김
	    showHistory(history);
		return;
	  }

	  try {
	    const res = await fetch(`/autocomplete?query=${encodeURIComponent(q)}`);
	    if (!res.ok) throw new Error('Server error');
	    const data = await res.json();            // [ "감자탕", "갈비탕", ... ]

	    /* 리스트 갱신 */
	    clearList();
	    data.forEach(word => list.appendChild(createItem(word, /* isHistory */ false)));

	    listWrapper.classList.remove('d-none');
	    listWrapper.classList.add('d-flex');

		activeIndex=-1;
		refreshActiveItem();
	  } catch (e) {
	    console.error(e);
	    clearList();
	  }
	}
	searchInput.addEventListener('keydown', e => {
	  const items = list.querySelectorAll('.autocomplete-item');
	  const N = items.length;
	  if (!N) return;

	  if (e.key === 'ArrowDown') {
	    e.preventDefault();
	    activeIndex = ((activeIndex + 2) % (N+1)) -1;
	    refreshActiveItem();
	  } else if (e.key === 'ArrowUp') {
	    e.preventDefault();
	    activeIndex = ((activeIndex + (N+1)) % (N+1))-1;
	    refreshActiveItem();
	  } else if (e.key === 'Enter') {
	    e.preventDefault();
	    if (activeIndex >= 0) {
			items[activeIndex].click();
			searchBtn.click(); 
		}else{
			searchBtn.click();
		}
		
		
		// selectItem 호출
	  }
	});
	
	function refreshActiveItem() {
	  const list = document.getElementById('list-autocomplete'); // ul 태그
	  const items = list.querySelectorAll('.autocomplete-item');

	  items.forEach(item => item.classList.remove('active'));

	  if (activeIndex >= 0 && activeIndex < items.length) {
	    items[activeIndex].classList.add('active');
	  }
	}

	function hadAddress(){
		return !!sessionStorage.getItem("address");
	}
	
	function showReenterBtn (){
		reenterBtn.classList.remove("d-none");
		reenterBtn.classList.add("d-flex");
		
	}
	
	
    searchInput.addEventListener('focus', function(){
		if(!hadAddress()){
			showAddrHistory();
		}else{
			showHistory();
		}
		
		borderSearchDiv.classList.add('expand');
		listWrapper.classList.remove('d-none');
		listWrapper.classList.add('d-flex');
		
	});
	searchInput.addEventListener('blur', function(){
		borderSearchDiv.classList.remove('expand');
		listWrapper.classList.add('d-none');
		listWrapper.classList.remove('d-flex');
		
	});
          // 검색창 input

	reenterBtn.addEventListener('click', function() {
	  activeIndex = -1;
	  sessionStorage.removeItem('address');  
	  searchInput.value = '';                      // input 필드 초기화
	  searchInput.setAttribute('placeholder', '배달받을 주소를 입력하세요'); // placeholder 초기화
	  
	  reenterBtn.classList.add("d-none");
	  showAddrHistory();

	});



});