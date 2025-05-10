const now = new Date();
   const currentYear = now.getFullYear();
   const currentMonth = now.getMonth() + 1;
   const defaultStartMonth = currentMonth - 4 > 0 ? currentMonth - 4 : 1;
   const defaultEndMonth = currentMonth;

   $(document).ready(function() {
       // 기본 월 범위 설정
       $('#startMonth').val(defaultStartMonth);
       $('#endMonth').val(defaultEndMonth);

       // 초기 자동 호출
       fetchAndDisplaySalesData(defaultStartMonth, defaultEndMonth);

       // 사용자 지정 범위 요청
       $('#loadDataButton').on('click', function () {
           const startMonth = parseInt($('#startMonth').val());
           const endMonth = parseInt($('#endMonth').val());

           if (startMonth > endMonth) {
               alert("시작 월은 끝 월보다 작거나 같아야 합니다.");
               return;
           }

           fetchAndDisplaySalesData(startMonth, endMonth);
       });
   });

   function fetchAndDisplaySalesData(startMonth, endMonth) {
       $.get(`/rider/monthly-sales-summary?year=${currentYear}&startMonth=${startMonth}&endMonth=${endMonth}`, function (data) {
           $('#statistics').empty();

           let chartLabels = [];
           let chartData = [];
           let chartPercentiles = [];

           for (let i = startMonth; i <= endMonth; i++) {
               const key = `${currentYear}-${i.toString().padStart(2, '0')}`;
               const averageSales = data[`${key}_averageSales`] || 0;
               const completedOrderCount = data[`${key}_completedOrderCount`] || 0;
               const percentile = data[`${key}_percentile`] || 0;

               // 현재 월에 대한 통계만 표시
               if (i === currentMonth) {
                   $('#statistics').append(`<p><strong>${i}월 평균 매출액:</strong> ${averageSales} 원</p>`);
                   $('#statistics').append(`<p><strong>${i}월 배달 횟수:</strong> ${completedOrderCount} 회</p>`);
                   $('#statistics').append(`<p><strong>${i}월 매출액 백분위:</strong> ${percentile} %</p>`);
               }

               chartLabels.push(i + "월");
               chartData.push(averageSales);
               chartPercentiles.push(percentile);
           }

           drawChart(chartLabels, chartData, chartPercentiles);
       });
   }

   let salesChartInstance = null;

   function drawChart(labels, salesData, percentiles) {
       const ctx = document.getElementById('salesChart').getContext('2d');

       if (salesChartInstance) {
           salesChartInstance.destroy(); // 기존 차트 제거
       }

       salesChartInstance = new Chart(ctx, {
           type: 'line',
           data: {
               labels: labels,
               datasets: [
                   {
                       label: '매출액',
                       data: salesData,
                       borderColor: 'rgb(75, 192, 192)',
                       backgroundColor: 'rgba(75, 192, 192, 0.2)',
                       fill: true
                   }
               ]
           },
           options: {
               responsive: true,
               plugins: {
                   legend: {
                       position: 'top',
                   },
                   tooltip: {
                       mode: 'index',
                       intersect: false,
                   },
               },
               interaction: {
                   mode: 'nearest',
                   axis: 'x',
                   intersect: false,
               },
               scales: {
                   x: {
                       title: {
                           display: true,
                           text: '월',
                       }
                   },
                   y: {
                       title: {
                           display: true,
                           text: '매출액(원)',
                       }
                   }
               }
           }
       });
   }