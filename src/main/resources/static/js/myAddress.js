
$(function() {
	
	document.getElementById('search-bar').addEventListener('change', event => {
		const keyword = document.getElementById('search-bar').value;
		console.log(keyword);
		
	});
	
	
});




function fetchSearchResults(query) {
	  const dropdown = document.getElementById('dropdown-results');
	  
	  if (!query) {
	    dropdown.style.display = 'none';
	    dropdown.innerHTML = '';
	    return;
	  }
		
	  // Simulating a search API call with sample data
	  const sampleData = ['Apple', 'Banana', 'Cherry', 'Date', 'Grape', 'Kiwi', 'Mango', "서초구", "강남구", "관악구"];
	  const filteredData = sampleData.filter(item => item.toLowerCase().includes(query.toLowerCase()));

	  // Show results in the dropdown
	  dropdown.innerHTML = filteredData.map(item => `<li onclick="handleSelection('${item}')">${item}</li>`).join('');

	  dropdown.style.display = filteredData.length > 0 ? 'block' : 'none';
	}

	function handleSelection(selectedValue) {
	  const searchBar = document.getElementById('search-bar');
	  const dropdown = document.getElementById('dropdown-results');
	  searchBar.value = selectedValue;
	  dropdown.style.display = 'none';
	}