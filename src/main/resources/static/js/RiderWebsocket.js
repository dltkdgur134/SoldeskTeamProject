 function updateProgress(step) {
		const progressBar = document.getElementById('progressBar');
		const steps = document.querySelectorAll('.step');
		const labels = document.querySelectorAll('.step-label');

		// 상태바 넓이 계산
		const progressPercent = ((step - 1) / (steps.length - 1)) * 100;
		progressBar.style.width = `${progressPercent}%`;

		// 현재 단계 및 레이블 스타일링
		steps.forEach(el => {
		     const currentStep = parseInt(el.getAttribute('data-step'), 10);
		     if (currentStep <= step) {
		         el.classList.add('active');
		     } else {
		         el.classList.remove('active');
		     }
		 });

		 labels.forEach(el => {
		     const currentStep = parseInt(el.getAttribute('data-step'), 10);
		     if (currentStep <= step) {
		         el.classList.add('active');
		     } else {
		         el.classList.remove('active');
		     }
		 });
		 if (step == 4) {
			window.location.href = "/orderHistory"
		}
	} 
 
  
	function incrementProgress() {
	 	  if (count < 5) { // 4단계 까지만
	 	    updateProgress(count);
	 	    document.getElementById('slider-step').value = count;
	 	    count++;
	 	    setTimeout(incrementProgress, 5000); // 5초마다 호출
	 	  }
	 }
	// 처음 화면 진입 시 단계는 1로 고정
	let count = document.getElementById('slider-step').value;
	//let status = document.getElementById('stepSlider').value;
	updateProgress(count);
			 	 
	//incrementProgress();
	
	const targetNode = document.getElementById("chatMessages");

	const config = { attributes: true, childList: true, subtree: true };
	const callback = (mutationList, observer) => {
		  for (const mutation of mutationList) {
		    if (mutation.type === "childList") {
		    	const msg = document.getElementById("chatMessages").innerText;
		      //alert("자식 노드가 추가되거나 제거됐습니다.");
		      //alert(msg);
		      //updateProgress(2);
		      console.log(msg);
		    } else if (mutation.type === "attributes") {
		      alert(`${mutation.attributeName} 특성이 변경됐습니다.`);
		    }
		  }
		};
	const observer = new MutationObserver(callback);
	observer.observe(targetNode, config);
	
	
	//observer.disconnect();
	//incrementProgress(); // 반복 시작	 
