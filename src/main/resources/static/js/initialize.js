
$(function () {
  		const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
  		const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))	
		
		const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
		const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))
		
		const toastElList = document.querySelectorAll('.toast')
		const toastList = [...toastElList].map(toastEl => new bootstrap.Toast(toastEl, option))
  	});



