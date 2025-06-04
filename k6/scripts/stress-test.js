import http from "k6/http";
import { sleep, check } from "k6";

const BASE_URL = "http://host.docker.internal:8080";
const CONCERT_ID = 1;

/*
    - 스트레스 테스트
    - 시스템이 지속적으로 증가하는 부하를 얼마나 잘 처리할 수 있는지 평가
    - 점진적으로 부하를 증가시켰을 때, 발생하는 문제가 있는지 파악
    - 장기적으로 Application 을 운영하기 위한 Spec 및 확장성과 장기적인 운영 계획을 파악해 볼 수 있음
*/
export let options = {
    stages: [
        { duration: '30s', target: 100 },
        { duration: '1m', target: 300 },
        { duration: '1m', target: 500 },
        { duration: '1m', target: 700 },
        { duration: '1m', target: 900 },
        { duration: '1m', target: 0 },
    ],
    thresholds: {
        'http_req_duration': ['p(95)<50'],  // 95%는 50ms 미만
        'http_req_failed': ['rate<0.01'],   // 실패율 1% 미만
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
