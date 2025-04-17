# 쿼리 성능 분석 및 인덱스 적용 방안 보고서

## 1. 개요

- **목적**: 본 보고서는 콘서트 대기열 시스템의 데이터베이스 쿼리 성능을 분석하고, 사용자 수 및 데이터 증가에 대비한 성능 개선 방안을 제시합니다..

- **배경**: 콘서트 대기열 시스템의 확장성을 확보하기 위해 현재 테이블 구조와 주요 조회 쿼리의 성능을 점검하였습니다. 특히 인덱스 최적화를 통한 조회 성능 향상에 중점을 두었습니다.


## 2. 테이블별 쿼리 분석 및 개선 방안

### 2.1 콘서트 테이블 (`concert`)

| 조회 쿼리                   | 분석 및 개선 방안                                                                                                                     |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| `SELECT * FROM concert` | - 필터링 조건 없이 전체 조회  <br>- 현재는 인덱스 추가 불필요  <br>- **대용량 데이터 시 고려**: 페이징 처리, 필요한 컬럼만 선택 조회 추천<br>- 페이징 처리 방식 : offset, lastSeekId  |

---

### 2.2 콘서트 날짜 테이블 (`concert_date`)

|조회 쿼리|분석 및 개선 방안|
|---|---|
|`SELECT * FROM concert_date WHERE concert_id = ?`|- `concert_id`에 대한 조회 빈도 높음  <br>- **조치**: `concert_id` 단일 인덱스 추가 필요|

---

### 2.3 좌석 테이블 (`seat`)

| 조회 쿼리                                          | 분석 및 개선 방안                                                           |
| ---------------------------------------------- | -------------------------------------------------------------------- |
| `SELECT * FROM seat WHERE concert_date_id = ?` | - `concert_date_id` 조건 조회  <br>- **조치**: `concert_date_id` 단일 인덱스 추가 |
| `SELECT * FROM seat WHERE id = ?`              | - 기본 키(PK) 조회  <br>- **추가 조치 불필요** (이미 인덱스 존재)                       |

---

### 2.4 결제 테이블 (`payment`)

| 조회 쿼리                                     | 분석 및 개선 방안                                           |
| ----------------------------------------- | ---------------------------------------------------- |
| `SELECT * FROM payment WHERE user_id = ?` | - `user_id` 조건 조회  <br>- **조치**: `user_id` 단일 인덱스 추가 |

---

### 2.5 포인트 테이블 (`point`)

| 조회 쿼리                                   | 분석 및 개선 방안                                           |
| --------------------------------------- | ---------------------------------------------------- |
| `SELECT * FROM point WHERE user_id = ?` | - `user_id` 조건 조회  <br>- **조치**: `user_id` 단일 인덱스 추가 |

---

### 2.6 대기열 테이블 (`waiting_queue`)

| 조회 쿼리                                                                                                   | 분석 및 개선 방안                                                                           |
| ------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| `SELECT * FROM waiting_queue WHERE token = ?`<br>`SELECT created_at FROM waiting_queue WHERE token = ?` | - `token` 조건 조회  <br>- **조치**: `token` 단일 인덱스 추가                                     |
| `SELECT count(*) FROM waiting_queue WHERE status = ?`<br>`SELECT * FROM waiting_queue WHERE status = ?` | - `status` 조건 조회  <br>- **조치**: `status` 단일 인덱스 추가                                   |
| `SELECT count(*) FROM waiting_queue WHERE status = 'waiting' AND created_at <= ?`                       | - `status`, `created_at` 복합 조건 조회  <br>- **조치**: `(status, created_at)` 복합 인덱스 추가 추천 |
| `SELECT * FROM waiting_queue WHERE status = 'active' ORDER BY id ASC LIMIT 1`                           | - `status` 조건 + 정렬  <br>- **조치**: status 단일 인덱스 추가, id는 pk라서 추가 불필요                  |
| `SELECT * FROM waiting_queue WHERE status = 'waiting' ORDER BY id ASC LIMIT ?`                          | - `status` 조건 + 정렬  <br>- **조치**: status 단일 인덱스가 있어서 where문에서 인덱스를 우선적으로 탐           |

---

### 2.7 예약 테이블 (`reservation`)

| 조회 쿼리                                                 | 분석 및 개선 방안                                                           |
| ----------------------------------------------------- | -------------------------------------------------------------------- |
| `SELECT * FROM reservation WHERE id = ?`              | - 기본 키(PK) 조회  <br>- **추가 조치 불필요**                                   |
| `SELECT * FROM reservation WHERE user_id = ?`         | - `user_id` 조건 조회  <br>- **조치**: `user_id` 단일 인덱스 추가                 |
| `SELECT * FROM reservation WHERE concert_seat_id = ?` | - `concert_seat_id` 조건 조회  <br>- **조치**: `concert_seat_id` 단일 인덱스 추가 |

---

## 3. 인덱스 추가 제안 요약 및 적용 여부

| 테이블           | 컬럼                   | 인덱스 타입 | 프로젝트 적용 여부 |
| ------------- | -------------------- | ------ |------------|
| concert_date  | concert_id           | 단일 인덱스 | 적용         |
| seat          | concert_date_id      | 단일 인덱스 | 적용         |
| payment       | user_id              | 단일 인덱스 | 적용         |
| point         | user_id              | 단일 인덱스 | 적용         |
| waiting_queue | token                | 단일 인덱스 | 적용         |
| waiting_queue | status               | 단일 인덱스 | 적용         |
| waiting_queue | (status, created_at) | 복합 인덱스 | 미적용 - 검토필요 |
| waiting_queue | (status, id)         | 복합 인덱스 | 미적용 - 검토필요 |
| reservation   | user_id              | 단일 인덱스 | 적용         |
| reservation   | concert_seat_id      | 단일 인덱스 | 적용         |

---

## 4. 결론 및 권장사항
### 인덱스 전략
- 단일 인덱스: 대부분의 조회 쿼리에서 단일 컬럼 조건(WHERE 컬럼 = ?)이 사용되므로, 단일 인덱스를 우선적으로 적용합니다.
- 복합 인덱스 검토: 복합 조건을 사용하는 쿼리의 경우, 아래 고려사항을 바탕으로 복합 인덱스 적용을 신중히 검토해야 합니다.
---
### 복합 인덱스에 대한 고려사항
1. 순서 의존성
    - 복합 인덱스는 순서에 매우 민감합니다.
    - 예: (status, created_at) 인덱스는 status 단독 조회 시 효과적이나, created_at 단독 조회 시 효과가 없습니다.
복합 인덱스는 선행 컬럼부터 순차적으로 사용할 때 최대 효율을 발휘합니다.

1. 인덱스 병합 가능성
   - RDBMS는 단일 인덱스 간 병합(Intersection) 기능을 제공합니다.
   - 예: status와 id 각각에 단일 인덱스가 있을 경우, status = ? AND id > ? 쿼리는 두 인덱스를 병합하여 최적화될 수 있습니다.

1. 관리 비용

    - 인덱스가 많아질수록 데이터 변경 시 관리 비용이 증가합니다.
    - 이는 쓰기 성능(write performance)에 영향을 줄 수 있으므로 적절한 균형이 필요합니다.
---
### 후속 조치

- 대용량 테이블 관리: waiting_queue와 seat 테이블은 데이터 증가가 예상되므로 인덱스 성능을 주기적으로 모니터링해야 합니다.
- 확장성 고려: 테이블 구조가 복잡해지고 연관관계가 늘어날 경우, 다음과 같은 추가 방안을 검토해야 합니다:
    
    - 테이블 파티셔닝
    - 데이터 캐싱 전략
    - 데이터베이스 뷰(view) 활용
    - 읽기/쓰기 분리 아키텍처 구성

- 성능 테스트: 인덱스 적용 전후 성능 테스트를 통해 개선 효과를 측정하고 문서화하는 것이 중요합니다.

본 보고서의 권장사항을 바탕으로 인덱스를 적용함으로써, 콘서트 대기열 시스템의 안정적인 확장과 성능 개선을 기대할 수 있을 것입니다.