/**
 * 
 */

$(function () {
	$(".nav-item > .active").css("color", "red");
	
	$(".nav-link").click(function (){
		alert('test');
		$(".nav-item > .active").css("color", "#ffc107;");
		$(".nav-link").removeClass('active');
		
		$(this).addClass('active');
		$(".nav-item > .active").css("color", "red");
	});
});

