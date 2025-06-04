import http from "k6/http";
import { sleep, check } from "k6";

const BASE_URL = "http://host.docker.internal:8080";
const CONCERT_ID = 1;

/*
    - 최고 부하 테스트
    - 시스템에 일시적으로 많은 부하가 가해졌을 때, 잘 처리하는지 평가
    - 목표치로 설정한 임계 부하를 일순간에 제공했을 때, 정상적으로 처리해내는지 파악
    - 선착순 이벤트 등을 준비하면서 정상적으로 서비스를 제공할 수 있을지 파악해 볼 수 있음
*/
export let options = {
    stages: [
        { duration: '10s', target: 0 },
        { duration: '5s', target: 7600 },
        /*
            - vus : 10000,9000 -> k6 137 error (memory 부족(2g))
            - vus : 8000 -> 실패율이 높음 (0.03%)
                checks_total.......................: 63353  2496.251876/s
                checks_succeeded...................: 99.96% 63328 out of 63353
                checks_failed......................: 0.03%  25 out of 63353
                ✗ status is 200
                  ↳  99% — ✓ 63328 / ✗ 25
            - vus : 7700 에서 실패율 발생 -> 임계점은 7600으로 생각
        */
        { duration: '10s', target: 0 },
    ],
};

export default function () {
    const res = http.post(`${BASE_URL}/queue/enter?concertId=${CONCERT_ID}&userId=${__VU}`);
    check(res, {
        'status is 200': (r) => r.status === 200
    });
    sleep(1);
}
