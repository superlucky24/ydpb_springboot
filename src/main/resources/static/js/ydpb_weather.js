function checkWeather() {
    const url = 'http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst'; /*URL*/
    const nx = 58;  // 예보지점 X
    const ny = 126; // 예보지점 Y
    let today = new Date();
    let year = today.getFullYear();
    let month = today.getMonth() + 1;
    if(month < 10) {
        month = '0' + month;
    }
    let day = today.getDate();
    if(day < 10) {
        day = '0' + day;
    }
    let dateStr = '' + year + month + day;
    let hour = today.getHours();
    if(hour < 10) {
        hour = '0' + hour;
    }
    hour += '00';
    var queryParams = '?' + encodeURIComponent('serviceKey') + '='+'WGScCCXJB6AS59TVUi23GrXGznEckWHIz9OC26JUsKNPV4IQ3%2BXRNGJfiX%2BzfOwvUGXGNiyQyYU7fbw4iIauyw%3D%3D'; /*Service Key*/
    queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1');
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('1000');
    queryParams += '&' + encodeURIComponent('dataType') + '=' + encodeURIComponent('JSON');
    queryParams += '&' + encodeURIComponent('base_date') + '=' + encodeURIComponent(dateStr);
    queryParams += '&' + encodeURIComponent('base_time') + '=' + encodeURIComponent(hour);
    queryParams += '&' + encodeURIComponent('nx') + '=' + encodeURIComponent(nx);
    queryParams += '&' + encodeURIComponent('ny') + '=' + encodeURIComponent(ny);
    
    fetch(queryParams)
    // 서버로 부터 받은 객체 데이터 응답 처리 => json 객체로 변환
    .then(response => response.json())
    // 변환된 데이터를 문자열로 변환 => 브라우저에 출력
    .then((data) => {
        let result = JSON.stringify(data,null,2); //stringify(data,null,공백갯수) | null=모든속성, 공백갯수=들여쓰기 2칸
        console.log(result);
    })
    // 오류 발생시 예외 처리 => 네트워크 오류, API가 문제 발생시
    .catch(error => console.log("GET 요청 오류 : ", error));
}
