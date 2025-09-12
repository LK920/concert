import http from "k6/http";
import { sleep, check } from "k6";
// docker 내부에서 테스트하기 위해
const url = "http://concert-service:8080";

export let options = {
    vus: 500,
    duration: '1m'
}

export default function () {
    // Test concert list endpoint
    let response = http.get(`${url}/concert/list`);
    check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
        'has concert data': (r) => r.body.includes('Concert_'),
    });
    sleep(1);
}
