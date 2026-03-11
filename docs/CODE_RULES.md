# Spring Boot Kotlin 프로젝트 코드 규칙 (CODE_RULES.md)

## 1. 네이밍 규칙 (Naming Convention)

- **패키지**: 소문자로만 구성하며 가급적 한 단어를 사용한다. (예: `com.perflog.config.kafka`)
- **클래스/인터페이스**: `PascalCase`를 사용한다.
- **함수/변수**: `camelCase`를 사용한다.

## 2. Kotlin Idioms

- **Null Safety**: `!!` (Not-null assertion) 연산자 사용을 지양하고, 안전 호출(`?.`)이나 엘비스 연산자(`?:`)를 사용한다.
- **Data Class**: 데이터를 전달하는 용도의 객체는 `data class`를 적극 활용한다.
- **Scope Functions**: `let`, `apply`, `run`, `with`, `also`를 적절히 활용하여 가독성을 높인다.

## 3. 계층형 아키텍처 (Layered Architecture)

- **Controller**: 외부 요청을 받고 응답을 반환하는 역할에 집중한다.
- **Service**: 핵심 비즈니스 로직을 처리하며 트랜잭션의 경계를 정의한다.
- **Repository**: 데이터베이스 접근을 담당한다.
- **DTO**: 계층 간 데이터 전달을 위해 사용하며, 엔티티를 직접 노출하지 않는다.

## 4. Spring Boot 관련

- **의존성 주입**: 생성자 주입 방식을 사용한다 (Kotlin의 클래스 선언 시 생성자 활용).
- **트랜잭션**: `@Transactional` 어노테이션을 명시하여 데이터 무결성을 보장한다.
- **예외 처리**: `GlobalExceptionHandler`를 통해 통합 예외 응답을 제공한다.

## 5. 주석 (Documentation)

- **KDoc**: 공용 API, 복잡한 로직의 함수나 클래스 상단에 KDoc(`/** ... */`) 스타일의 주석을 작성한다.
