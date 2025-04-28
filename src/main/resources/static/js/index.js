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
    var listWrapper = document.getElementById('list-wrapper');
    var list = document.getElementById('list-autocomplete');
    var reenterBtn = document.getElementById('btn-address-reset');
	var borderSearchDiv = document.getElementById('search-box');
    var activeIndex = -1;

    function openList() {
        listWrapper.classList.add('open');
        listWrapper.classList.remove('closed');
    }
    function closeList() {
        listWrapper.classList.remove('open');
        listWrapper.classList.add('closed');
        if (activeIndex >= 0 && activeIndex < listItems.length) {
            listItems[activeIndex].classList.remove('active');
        }
        activeIndex = -1;
    }

    searchInput.addEventListener('focus', function(e){
		borderSearchDiv.classList.add('expand');
		listWrapper.classList.remove('d-none');
		listWrapper.classList.add('d-flex');
		
	});
	searchInput.addEventListener('blur', function(e){
		borderSearchDiv.classList.remove('expand');
		listWrapper.classList.add('d-none');
		listWrapper.classList.remove('d-flex');
		
	});
    searchInput.addEventListener('click', openList);

	
    searchInput.addEventListener('keydown', function(e) {
        var key = e.key;
        if (key === 'ArrowDown') {
            e.preventDefault();
            if (!listWrapper.classList.contains('open')) {
                openList();
            }
            if (activeIndex < listItems.length - 1) {
                if (activeIndex >= 0) {
                    listItems[activeIndex].classList.remove('active');
                }
                activeIndex++;
                listItems[activeIndex].classList.add('active');
                listItems[activeIndex].scrollIntoView({ block: 'nearest' });
            }
        } else if (key === 'ArrowUp') {
            e.preventDefault();
            if (!listWrapper.classList.contains('open')) {
                openList();
            }
            if (activeIndex > 0) {
                listItems[activeIndex].classList.remove('active');
                activeIndex--;
                listItems[activeIndex].classList.add('active');
                listItems[activeIndex].scrollIntoView({ block: 'nearest' });
            }
        } else if (key === 'Enter') {
            if (listWrapper.classList.contains('open') && activeIndex >= 0) {
                e.preventDefault();
                var selectedText = listItems[activeIndex].textContent;
                searchInput.value = selectedText;
                closeList();
                reenterBtn.style.display = 'inline-block';
                outer.classList.add('has-btn');
                outer.classList.remove('no-btn');
                searchInput.blur();
            }
        } else if (key === 'Escape') {
            closeList();
        }
    });

    for (let i = 0; i < listItems.length; i++) {
        listItems[i].addEventListener('mousedown', function(e) {
            e.preventDefault();
        });
        listItems[i].addEventListener('click', function() {
            searchInput.value = this.textContent;
            closeList();
            reenterBtn.style.display = 'inline-block';
            outer.classList.add('has-btn');
            outer.classList.remove('no-btn');
            searchInput.blur();
        });
    }

    reenterBtn.addEventListener('click', function() {
        reenterBtn.style.display = 'none';
        outer.classList.remove('has-btn');
        outer.classList.add('no-btn');
        searchInput.value = '';
        searchInput.focus();
    });

    searchInput.addEventListener('blur', function() {
        setTimeout(function() {
            if (document.activeElement !== searchInput) {
                closeList();
            }
        }, 100);
    });
});