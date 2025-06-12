import http from "k6/http";
import { sleep, check } from "k6";

const BASE_URL = "http://host.docker.internal:8080";
const CONCERT_ID = 1;
const peak_scenario = [
    { duration: '1m', target: 100 },
    { duration: '30s', target: 7500 },
    { duration: '1m', target: 7500 }, // 1분간 최대 수 유지
    { duration: '30s', target: 100 },
    { duration: '7m', target: 100 },
    /*
        - vus : 10000,9000 -> k6 137 error (memory 부족(2g))
        - vus : 8000, 실패율 0.03%
        - vus : 7700, 실패율 0.03%
        - vus : 7600, 실패율 0.03%
        - vus : 7500,
    */
];

/*
    - 최고 부하 테스트
    - 시스템에 일시적으로 많은 부하가 가해졌을 때, 잘 처리하는지 평가
    - 목표치로 설정한 임계 부하를 일순간에 제공했을 때, 정상적으로 처리해내는지 파악
    - 선착순 이벤트 등을 준비하면서 정상적으로 서비스를 제공할 수 있을지 파악해 볼 수 있음
*/
export let options = {
    stages: peak_scenario,
    thresholds: {
        'http_req_failed': ['rate<0.01'],   // 1% 이하 실패율
        'http_req_duration': ['p(95)<300'], // p95 응답시간 300ms 이하
        'checks': ['rate>0.99'],            // 성공률 99% 이상
    },
};

export default function () {
    const res = http.post(`${BASE_URL}/queue/enter?concertId=${CONCERT_ID}&userId=${__VU}`);
    check(res, {
        'status is 200': (r) => r.status === 200
    });
    sleep(1);
}
