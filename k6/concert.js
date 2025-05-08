import http from "k6/http";
import { sleep, check } from "k6";
// docker 내부에서 테스트하기 위해
const url = "http://host.docker.internal:8080";

export let options = {
    vus: 500,
    duration: '1m'
}

export default function () {
    let response = http.get(`${url}/concert/1/date`);
    check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });
    sleep(1);
}
