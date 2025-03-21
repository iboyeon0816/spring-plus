# SPRING PLUS
본 프로젝트는 기존 일정 관리 시스템을 개선하여 JPA 심층 학습 및 성능 최적화를 목표로 한다. 또한, AWS 및 CI/CD를 활용한 자동화된 배포 환경 구축을 통해 안정적인 클라우드 기반 서비스를 운영하는 경험을 쌓고자 한다.

이 프로젝트는 [f-api/spring-plus](https://github.com/f-api/spring-plus)에서 fork하여 가져온 것으로, 원본 프로젝트를 기반으로 기능을 확장하고, 성능 최적화를 진행하였다.

<br>

## 기술 스택
| 분야      | 기술 |
|-----------|----------------------------------------|
| **Backend** | Spring Boot, Spring Security, JPA, QueryDSL, Java, Kotlin |
| **Database** | MySQL |
| **Authentication** | JWT |
| **Cloud** | AWS EC2, RDS, S3 |
| **CI/CD** | GitHub Actions |

<br>

## 주요 개선 사항
### 1. JPA 성능 최적화
- N+1 문제 해결을 통한 불필요한 쿼리 감소
- 대용량 데이터 조회 성능 최적화
- 트랜잭션 전파 옵션 설정을 통한 안정적인 데이터 관리

### 2. QueryDSL 활용
- 동적 쿼리를 효율적으로 작성하여 복잡한 검색 기능 구현

### 3. Spring Security 적용
- JWT의 stateless 특성을 활용한 인증 및 인가 구현

### 4. AWS 기반 서비스 배포
- EC2, RDS, S3를 활용한 클라우드 환경 구축
- GitHub Actions 기반 CI/CD 적용을 통한 자동화된 배포

### 5. Kotlin 전환
- Java 코드를 Kotlin으로 변환하여 코드 간결화 및 유지보수성 향상

<br>

## Health Check API
서비스의 상태를 모니터링할 수 있는 API이다. [Health Check API 호출하기](http://3.37.64.211:8080/health)

### Health Check API 엔드포인트
```sh
GET /health
```
### 실행 결과 예시
```json
{
  "status": "UP"
}
```

<br>

## AWS 설정 캡쳐 화면
### 1. EC2 인스턴스 설정
<img width="600" alt="스크린샷 2025-03-21 오전 10 41 12" src="https://github.com/user-attachments/assets/b43886aa-c618-4165-b47f-99e44448e19e" />

### 2. RDS 설정
<img width="600" alt="스크린샷 2025-03-21 오전 10 44 39" src="https://github.com/user-attachments/assets/386e2c0f-cadd-49d9-a67a-3ad02bd27b74" />

### 3. S3 버킷 설정
<img width="600" alt="스크린샷 2025-03-21 오전 10 47 00" src="https://github.com/user-attachments/assets/6c95c472-2039-481c-bc69-6ec7c7f957a7" />

<br>
<br>

## 대용량 데이터 조회 성능 비교
본 테스트에서는 사용자 데이터 100만 건을 DB에 저장한 후, 닉네임을 기준으로 조회 성능을 비교한다. 닉네임은 중복이 거의 없는 고유 값이며, 두 가지 방법을 사용해 성능을 비교했다:
1. 인덱스가 없는 상태에서 조회
2. 닉네임에 인덱스 설정 후 조회

<br>

결론: 인덱스를 적용한 후 조회 성능이 약 4배 정도 향상되었다. 자주 조회되는 컬럼에 인덱스를 추가하는 것이 성능 최적화에 매우 효과적이라는 것을 알게 되었다.

<br>

### 1. 인덱스가 없는 상태에서 조회
Spring Data JPA를 활용하여 닉네임을 기준으로 조회할 때 인덱스 없이 기본 조회를 수행한다. 이 경우 DB는 테이블의 모든 레코드를 순차적으로 검색한다.

#### 구현 코드
```java
@Test
void 닉네임_인덱스_설정_없음() {
    long startTime = System.currentTimeMillis();

    userRepository.findByNickname(nickname);

    long endTime = System.currentTimeMillis();
    System.out.println("인덱스 없을 때 시간: " + (endTime - startTime) + " ms");
}
```

#### 실행 결과
```
Hibernate: select u1_0.id,u1_0.created_at,u1_0.email,u1_0.image_url,u1_0.modified_at,u1_0.nickname,u1_0.password,u1_0.user_role from users u1_0 where u1_0.nickname=?
인덱스 없을 때 시간: 257 ms
```

### 2. 닉네임에 인덱스 설정 후 조회
nickname 컬럼에 인덱스를 추가하여 조회 성능을 최적화한다. 인덱스를 사용하면 DB는 데이터 전체를 순차적으로 검색하는 대신 인덱스를 통해 빠르게 조회한다.

#### 구현 코드
```java
@Test
void 닉네임_인덱스_설정_있음() {
    jdbcTemplate.execute("CREATE INDEX idx_user_nickname ON users (nickname)");

    long startTime = System.currentTimeMillis();

    userRepository.findByNickname(nickname);

    long endTime = System.currentTimeMillis();
    System.out.println("인덱스 있을 때 시간: " + (endTime - startTime) + " ms");

    jdbcTemplate.execute("DROP INDEX idx_user_nickname ON users");
}
```

#### 실행 결과
```
Hibernate: select u1_0.id,u1_0.created_at,u1_0.email,u1_0.image_url,u1_0.modified_at,u1_0.nickname,u1_0.password,u1_0.user_role from users u1_0 where u1_0.nickname=?
인덱스 있을 때 시간: 61 ms
```
