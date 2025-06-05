import http from "k6/http";
import { sleep, check } from "k6";

const BASE_URL = "http://host.docker.internal:8080";
const CONCERT_ID = 1;

/*
    - 내구성 테스트
    - 시스템이 장기간 동안 안정적으로 운영될 수 있는지 평가
    - 특정한 부하를 장기간 동안 제공했을 때, 발생하는 문제가 있는지 파악
    - 장기적으로 Application 을 운영할 때 발생할 수 있는 숨겨진 문제를 파악해 볼 수 있음 ( feat. Memory Leak, Slow Query 등 )
    - 50
*/
export let options = {
    stages:[
        { duration: '10m', target: 50 },     // 예열 (Warm-up)
        { duration: '10m', target: 100 },    // 점진적 부하 증가 (Ramping up)
        { duration: '30m', target: 100 },    // 고정 부하 유지 (Soaking)
        { duration: '5m', target: 0 },      // 종료 (Cool-down)
    ],
    thresholds: {
        'http_req_duration': ['p(95)<300'],  // 95%는 50ms 미만
        'http_req_failed': ['rate<0.01'],   // 실패율 1% 미만
        'checks': ['rate>0.99'],            // 성공률 99% 이상
    },
}

export default function () {
    let response = http.post(`${BASE_URL}/queue/enter?concertId=${CONCERT_ID}&userId=${__VU}`);
    check(response,{
        'status is 200': (r) => r.status === 200
    });
    sleep(1);
}