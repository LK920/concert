import http from "k6/http";
import { sleep, check } from "k6";

const url = "http://localhost:8080";

export let options = {
    stages : [  //부하를 생성하는 여러 스테이지를 만드는 것
        { duration: '10m', target: 6000} // 10분에 걸쳐서 가상 유저수가 6000에 도달하다록 설정하는 것 => 점진적으로 6000 유저 수 까지 올라감
    ],
}

export default function () {
  let response = http.get(`${url}/concert/list`);
  sleep(1);
}
