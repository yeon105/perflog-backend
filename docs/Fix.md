# 프로젝트 문제점 및 기술 부채 분석 (Fix.md)

## 1. 아키텍처 (Architecture)

- **분산 트랜잭션 데이터 불일치 위험**: `PerfumeServiceImpl`에서 MySQL 저장 후 Kafka로 이벤트를 발행할 때, Kafka 발행에 실패하면 MySQL
  데이터는 저장되었으나 Elasticsearch에는 반영되지 않는 데이터 불일치가 발생할 가능성이 큼.
    - **해결 방안**: Transactional Outbox Pattern을 도입하거나, `spring-kafka`의 트랜잭션 지원 기능을 활용하여 MySQL과 Kafka
      발행을 원자적으로 처리해야 함.
- **하드코딩된 설정값**: `KafkaConsumerConfig`, `KafkaProducerConfig` 내에 `localhost:9092` 등의 접속 정보가 하드코딩되어
  있음.
    - **해결 방안**: `application.yml`의 `spring.kafka.bootstrap-servers` 등을 활용하여 설정 정보를 관리해야 함.

## 2. 코드 품질 (Code Quality)

- **빈약한 도메인 모델 (Anemic Domain Model)**: `Perfume`, `Member` 엔티티가 상태(데이터) 위주로 구성되어 있고, 비즈니스 로직은 서비스
  계층에 파편화되어 있음.
    - **해결 방안**: 엔티티 내부에 객체 스스로 상태를 변경하는 비즈니스 메서드를 추가하여 캡슐화를 강화해야 함.
- **DTO와 엔티티의 혼용**: 일부 계층에서 엔티티를 직접 노출하거나, 엔티티를 통해 다른 계층의 데이터를 조회하는 구조가 남아 있음.

## 3. 안정성 (Stability)

- **대용량 마이그레이션 타임아웃 위험**: `PerfumeServiceImpl.migrate()`에서 모든 향수 정보를 한 번에 마이그레이션할 때, 데이터가 많아지면 HTTP
  요청 타임아웃이나 서버 부하가 발생할 수 있음.
    - **해결 방안**: Spring Batch를 도입하여 청크 단위로 안전하게 처리하거나, 비동기로 마이그레이션을 실행하도록 개선 필요.
- **Kafka 설정 오류**: `KafkaConsumerConfig`에서 `ENABLE_AUTO_COMMIT_CONFIG`는 `false`로 설정했으나, 실제 Ack 처리는
  `MANUAL`이 아닌 `RECORD`로 자동 처리되는 구조임.

## 4. 보안 (Security)

- **토큰 관리 로직 파편화**: 토큰을 생성하고 만료를 확인하는 로직이 `JwtUtil`, `TokenService`, `LoginFilter` 등에 흩어져 있어 관리가
  어려움.
    - **해결 방안**: 토큰 수명 주기(Lifecycle)를 전문적으로 관리하는 객체를 정의하여 책임을 집중시켜야 함.

## 5. 향후 우선순위 로드맵

1. **P1 (Critical)**: 분산 트랜잭션 데이터 불일치 문제 해결 (Kafka 트랜잭션 또는 Outbox Pattern).
2. **P2 (Major)**: 설정 정보 외부 주입 및 패키지 구조 정비.
3. **P3 (Normal)**: 도메인 엔티티 비즈니스 로직 강화 및 리팩토링.
