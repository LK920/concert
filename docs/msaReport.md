# MSA 확장을 고려한 트랜잭션 및 도메인 분리 설계 보고서

## 1. MSA란?

**MSA(Microservice Architecture, 마이크로서비스 아키텍처)**는 단일 어플리케이션을 비즈니스 기능 단위로 나누어 각각 독립적으로 배포 및 운영 가능한 **작은 서비스들의 집합**으로 구성하는 아키텍처 스타일입니다.

### 주요 특징
- 서비스는 각자 독립된 DB를 가짐
- 각 도메인은 서로 HTTP/gRPC/Kafka 등의 메시징으로 통신
- 서비스별로 개별 개발·배포가 가능
- 확장성과 장애 격리성이 뛰어남

---

## 2. 도메인 단위로 배포 분리

### 2.1 어떤 도메인으로 배포 단위를 설계할지

현재 프로젝트 구조를 기반으로 다음과 같이 **도메인 단위로 마이크로서비스를 분리**할 수 있습니다:

| 마이크로서비스        | 포함 도메인                   | 설명                         |
|---------------------|------------------------------|------------------------------|
| Reservation Service | `reservation`, `seat`, `queue` | 예약 및 좌석 상태 관리, 대기열 처리 |
| Point Service       | `point`, `ranking`          | 사용자 포인트 관리 및 랭킹 점수 처리 |
| Payment Service     | `payment`                   | 결제 내역 처리 및 트랜잭션 기록 |
| Concert Service     | `concert`, `concertDate` | 공연 정보 및 일정 |
| User Service | `concertUser` | 고객 정보|
| Event Service       | `events`, 외부 메시지 전파      | 이벤트 발행 및 알림 처리      |

---

### 2.2 그 분리에 따른 트랜잭션 처리의 한계와 해결방안

단일 애플리케이션에서는 Spring의 `@Transactional`로 DB 트랜잭션을 손쉽게 처리할 수 있으나, **MSA에서는 트랜잭션이 서비스 간에 걸쳐질 수 없음**에 주의해야 합니다.

#### 🚫 문제: 분산 트랜잭션의 한계

예: 예약 시  
`reservation → point → payment → seat`  
서비스들이 연쇄적으로 호출되며 각각 별도의 DB를 사용하는 경우, 하나의 트랜잭션으로 묶기 어려움.

#### ✅ 해결방안: 비동기 이벤트 + 보상 트랜잭션 (SAGA 패턴)

| 전략                    | 설명                                                                  | 적용 예시 |
|-----------------------|---------------------------------------------------------------------|-----------|
| **SAGA 패턴**         | 각 서비스가 자신의 작업을 커밋하고 다음 서비스에 이벤트를 전달하며, 실패 시 보상 동작 수행 | `ReservationCreatedEvent → PointUsedEvent → PaymentCreatedEvent → ReservationCompletedEvent` |
| **도메인 이벤트 발행** | 트랜잭션 이후 이벤트 발행 (`@TransactionalEventListener(phase = AFTER_COMMIT)`) | 도메인 간 메시징 연결 |
| **@Async 이벤트 핸들러** | 각 이벤트를 비동기 처리하여 서비스 간 독립성 확보                              | 결제 생성 후 예약 상태 변경 |
| **보상 트랜잭션 구현** | 실패 시 이전 상태로 복원하는 로직 수행                                     | 포인트 차감 실패 → 좌석 상태 복원 |

---

#### 예시: Reservation Flow
```aiignore
[Client 요청]
↓
Reservation Service → 예약 생성 + ReservationCreatedEvent 발행
↓
Point Service → 포인트 차감 + PointUsedEvent 발행
↓
Payment Service → 결제 생성 + PaymentCreatedEvent 발행
↓
Reservation Service → 예약 상태 COMPLETE로 변경

```

**실패 시 보상 이벤트:**
- 포인트 차감 실패 → `PointUsingFailedEvent` → 좌석 해제
- 결제 실패 → `PaymentFailedEvent` → 포인트 롤백 또는 예약 취소

---

## 3. 마치며

서비스가 확장되며 도메인 단위로 마이크로서비스로 분리될 경우, 가장 큰 이슈는 **분산 트랜잭션 처리**입니다. 
이를 해결하기 위해:
- **SAGA 패턴과 보상 트랜잭션**을 활용하고,
- **도메인 이벤트와 메시징 기반의 비동기 처리 전략**을 적용하여
- 각 도메인의 독립성과 신뢰성을 유지하는 것이 중요합니다.