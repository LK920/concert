import http from "k6/http";
import { sleep, check } from "k6";

const BASE_URL = "http://host.docker.internal:8080";
const CONCERT_ID = 1;

/*
    - 부하테스트
    - 시스템이 예상되는 부하를 정상적으로 처리할 수 있는지 평가
    - 특정한 부하를 제한된 시간 동안 제공해 이상이 없는지 파악
*/
export let options = {
    stages: [
            { duration: '1m', target: 100 },
            { duration: '2m', target: 500 },
            { duration: '2m', target: 500 },
            { duration: '1m', target: 0 },
        ],
}

export default function () {
    let response = http.post(`${BASE_URL}/queue/enter?concertId=${CONCERT_ID}&userId=${__VU}`);
    check(response,{
        'status is 200': (r) => r.status === 200
    });
    sleep(1);
}