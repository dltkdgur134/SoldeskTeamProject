$(function () {
  $(".nav-item > .active").css("color", "red");
  $(".nav-link").on("click", function () {
    $(".nav-link").removeClass("active").css("color", "#ffc107");
    $(this).addClass("active").css("color", "red");
  });
});

/* ───────────────────────────────  검색 로직  ─────────────────────────────── */
$(document).ready(function() {
  const $input      = $("#inp-search");              // 메인 검색 input
  const $searchBtn  = $("#btn-search");                // 검색 버튼 (돋보기 아이콘)
  const $reAddrBtn  = $("#btn-address-reset");     // '주소 다시 입력' 버튼
  const $wrapper    = $("#search-box");          // 검색창+리스트 래퍼 (포커스 스타일용)
  const $list       = $("#list-autocomplete");       // 자동완성/기록 리스트 UL
	
  // --- 상태 관리: 주소 입력 여부 확인 (주소가 있으면 true) ---
  function hasAddress() {
    return !!sessionStorage.getItem("address");
  }

  // 페이지 로드 시, 이전에 주소를 입력한 적이 있으면 음식 검색 모드로 전환
  if (hasAddress()) {
    $reAddrBtn.show();                              // 주소 다시 입력 버튼 표시
    $input.attr("placeholder", "뭐 먹을까?");       // placeholder 문구를 음식 검색용으로 변경
    $input.val(sessionStorage.getItem("food") || ""); // 이전에 검색한 음식이 있으면 채워놓기
    showHistory();  // 포커스 없이도 최근 검색어를 미리 보여줄 수 있음 (선택 사항)
  }

  // --- 검색 버튼 클릭 이벤트 ---
  $searchBtn.on("click", function(e) {
    e.preventDefault();  // 앵커 기본 동작 (#로의 이동) 막기
    const query = $input.val().trim();
    if (!query) return;  // 입력이 비어있으면 아무 행동 하지 않음

    if (!hasAddress()) {
      // [1] 주소 입력 단계 처리
      sessionStorage.setItem("address", query);            // 주소를 세션스토리지에 저장
      $input.val(sessionStorage.getItem("food") || "");    // input을 비우거나 기존 food 검색어 로드
      $input.attr("placeholder", "뭐 먹을까?");            // placeholder를 음식 검색 문구로 변경
      $reAddrBtn.show();                                   // '주소 다시 입력' 버튼 표시
      hideList();                                          // 혹시 열려있을 리스트 닫기
    } else {
      // [2] 음식 검색 실행 단계 처리
      sessionStorage.setItem("food", query);   // 현재 검색어를 저장 (다음 방문 시 복원용)
      saveHistory(query);                     // 로컬스토리지에 검색 기록 저장
      hideList();                             // 리스트 닫기
      // 실제 검색 동작 수행: 아래는 예시로 alert을 사용하고, 실제로는 결과 페이지로 이동 등 처리
      alert(`"${query}" 검색을 실행합니다!`);
      // 예: window.location.href = `/search?query=${encodeURIComponent(query)}`;
    }
  });

  // --- 주소 다시 입력 버튼 클릭 이벤트 ---
  $reAddrBtn.on("click", function() {
    sessionStorage.removeItem("address");                         // 저장된 주소 삭제
    $input.val("").attr("placeholder", "배달받을 주소를 입력하세요");  // 입력 필드 초기화
    $reAddrBtn.hide();
	$("#search-box-container").removeClass("flex-grow-1").addClass("w-100 justify-content-center");
    hideList();
  });

  // --- 입력창 포커스 이벤트 ---
  $input.on("focus", function() {
		if (!hasAddress()){ return;  // 주소 입력 전이라면 아무 작업도 안 함
	}
    // 음식 검색 모드에서는 포커스 시 최근 검색기록 표시 (입력값이 없을 때)
    if ($input.val().trim() === "") {
      showHistory();
    }
    // (입력값이 있는 경우 focus 이벤트 후 이어질 input 이벤트에서 자동완성 처리)
  });

  // --- 입력값 변경 이벤트 (자동완성 & 검색기록 표시) ---
  let debounceId;
  $input.on("input", function() {
    const term = $input.val().trim();
    activeIndex = -1;                       // 새로운 입력 시 선택 인덱스 초기화
    if (!hasAddress()) {
      // 주소 입력 단계에서는 자동완성 기능 동작하지 않음
      hideList();
      return;
    }
    if (term === "") {
      // 입력이 없는 경우: 검색 기록 표시
      showHistory();
    } else {
      // 입력이 있는 경우: 자동완성 결과를 (디바운스하여) 가져오기
      clearTimeout(debounceId);
      debounceId = setTimeout(() => fetchSuggestions(term), 100);  // 100ms 디바운스
    }
  });

  // --- 키보드 방향키 및 Enter 키 처리 ---
  let activeIndex = -1;
  $input.on("keydown", function(e) {
    const items = $list.children(".autocomplete-item");
    if (!items.length) return;  // 리스트가 비어있으면 처리하지 않음

    if (e.key === "ArrowDown") {
      e.preventDefault();
      activeIndex = (activeIndex + 1) % items.length;      // 인덱스 증가 (순환)
      refreshActiveItem();
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      activeIndex = (activeIndex - 1 + items.length) % items.length;  // 인덱스 감소 (순환)
      refreshActiveItem();
    } else if (e.key === "Enter") {
      e.preventDefault();
      if (activeIndex >= 0) {
        // 현재 선택된 리스트 아이템 클릭 처리 (검색 실행)
        const selectedText = $(items[activeIndex]).text().trim();
        selectItem(selectedText);
      } else {
        // 선택된 항목이 없고 Enter를 누르면, 현재 입력값으로 검색 실행
        $searchBtn.trigger("click");
      }
    } 
  });

  // --- 문서 전체 클릭 이벤트 (외부 클릭 감지) ---
  $(document).on("click", function(e) {
    // 검색 영역 밖을 클릭하면 리스트 닫기
    if ($(e.target).closest("#search-box").length === 0) {
      hideList();
    }
  });

  // ====== 자동완성/검색기록 표시를 위한 헬퍼 함수들 ======


  // 검색 결과 항목을 클릭 또는 선택했을 때 처리
  function selectItem(text) {
    $input.val(text);
    saveHistory(text);
    hideList();
    // 음식 검색 모드인 경우 바로 검색 실행
    if (hasAddress()) {
      sessionStorage.setItem("food", text);
      // 실제 검색 실행 로직 (필요에 따라 변경)
      alert(`"${text}" 검색을 실행합니다!`);
      // 예: window.location.href = `/search?query=${encodeURIComponent(text)}`;
    }
  }

  // 자동완성/기록 리스트 아이템 생성 함수
  function createItem(text, isHistory = false) {
    // 아이템 구성: 아이콘 span + 텍스트
    const iconClass = isHistory ? "history" : "suggest";
    const $item = $(`
      <li class="autocomplete-item">
        <span class="icon-span ${iconClass}"></span>
        <span class="item-text">${text}</span>
      </li>
    `);
    // 항목 클릭 시: 해당 텍스트로 검색 실행
    $item.on("click", () => selectItem(text));
    return $item;
  }

  // 현재 activeIndex에 따라 리스트 아이템의 활성화 상태 업데이트
  function refreshActiveItem() {
    $list.children(".autocomplete-item").removeClass("active");
    if (activeIndex >= 0) {
      $list.children(".autocomplete-item").eq(activeIndex).addClass("active");
    }
  }

  // --- 검색 기록 저장/불러오기 ---
  function loadHistory() {
    return JSON.parse(localStorage.getItem("searchHistory") || "[]");
  }
  function saveHistory(keyword) {
    if (!keyword) return;
    let history = loadHistory();
    // 중복 항목 삭제하고 최신 검색어를 배열 앞에 추가
    history = history.filter(item => item !== keyword);
    history.unshift(keyword);
    history = history.slice(0, 10);  // 최대 10개만 저장
    localStorage.setItem("searchHistory", JSON.stringify(history));
  }
  function showHistory() {
    const history = loadHistory();
    $list.empty();
    if (history.length === 0) {
      hideList();
      return;
    }
    history.forEach(word => {
      $list.append(createItem(word, true));  // isHistory = true (아이콘 구분 용도)
    });
    showList();
  }
  

  // --- 자동완성 제안 결과 불러오기 (AJAX) ---
  function fetchSuggestions(query) {
    // AJAX 요청으로 자동완성 데이터 가져오기 (예: /autocomplete API)
    $.getJSON(`/autocomplete?query=${encodeURIComponent(query)}`, function(results) {
      $list.empty();
      if (!results || results.length === 0) {
        hideList();
        return;
      }
	  
      results.forEach(word => {
		$list.append(createItem(word, false));  // isHistory = false
      });
      showList();
      refreshActiveItem();  // activeIndex 초기값 -1 유지 (첫 항목 선택 없음)
    });
  }
  function showList() {
    $wrapper.addClass("show-list");
	$list.removeClass("d-none");
  }
  function hideList() {
    $list.empty();
    $wrapper.removeClass("show-list");
    activeIndex = -1;
  }
});