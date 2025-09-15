<p align="center">
  <img src="../assets/wiseowl-logo.png" alt="WiseOwl Logo" width="400"/>
</p>

<h1 align="center" style="font-weight: bold;">WiseOwl Backend</h1>

<p align="center">
  <a href="#1-프로젝트-개요">개요</a> •
  <a href="#2-문제-정의">문제</a> •
  <a href="#3-주요-기능">기능</a> •
  <a href="#4-기술-스택">스택</a> •
  <a href="#5-아키텍처">아키텍처</a> •
  <a href="#6-api-문서">API</a> •
  <a href="#7-테스트">테스트</a> •
  <a href="#8-성능-테스트">성능</a> •
  <a href="#9-배포">배포</a> •
  <a href="#10-데모">데모</a>
</p>

---

## 1. 프로젝트 개요
WiseOwl은 대학 신입생을 위한 **졸업 요건 관리 서비스**입니다.  
학과별/전공별/트랙별 규칙을 기반으로 졸업 요건 충족 여부를 자동 검증하고,  
학생이 졸업 계획을 체계적으로 관리할 수 있도록 지원합니다.

---

## 2. 문제 정의
- 학과별·전공별로  상이한 졸업 요건(논문, 프로젝트, 인증 등) 관리의 복잡성
- 신입생 입장에서 학사 규정을 직관적으로 이해하기 어려움
- 이를 자동화/시각화하여 효율적으로 확인할 수 있는 시스템 부재

---

## 3. 주요 기능
- 회원가입/로그인 (JWT, 구글 OAuth2 지원)
- 학번·전공·트랙 조건에 맞는 졸업 요건을 조회해 제공
- 전공/부전공/트랙별 규칙 관리
- 졸업 필수과목 이수여부 자동계산
- 캠퍼스 지도 기능 (건물·층 단위 편의시설 조회, 카테고리별 모아보기)

---

## 4. 기술 스택
- **Backend**: Spring Boot, Spring Security, JPA (QueryDSL), Gradle
- **Database**: MySQL, Flyway, Testcontainers
- **Infra**: AWS EC2, Docker, Redis
- **Test**: JUnit5, MockMvc, Testcontainers
- **Frontend**: React

---

## 5. 아키텍처
- **Layered Architecture** 기반 설계 (Controller → Service → Repository → Domain)
- 데이터 모델은 ERD(Entity Relationship Diagram)로 관리
- ERD 전체 구조는 복잡하므로, README에서는 축소 버전을 제공하고 세부 사항은 원본에서 확인 가능

<p align="center">
  <img src="../assets/wiseowl_backend_erd.png" alt="WiseOwl ERD" width="600"/>
</p>

 [ERD 원본 전체 보기](../assets/wiseowl_backend_erd.png)

---

## 6. API 문서
주요 엔드포인트 예시:

| Method | Endpoint                                | Description                  |
|--------|-----------------------------------------|------------------------------|
| POST   | `/api/auth/signup`                      | 회원가입 및 JWT 발급         |
| POST   | `/api/auth/login`                       | 로그인 및 JWT 발급           |
| POST   | `/api/auth/oauth/google`                | 구글 소셜 로그인             |
| GET    | `/api/users/me/graduation-requirements` | 사용자별 졸업 요건 조회       |
| GET    | `/api/users/me/required-courses`        | 전공/교양 필수과목 이수 현황 |
| GET    | `/api/users/me/summary`                 | 사용자 학사 요약 정보 조회   |
| GET    | `/api/courses/offerings`                | 학기별 개설 교과목 조회      |
| GET    | `/api/facilities`                       | 캠퍼스 건물·편의시설 조회     |


 상세 요청/응답 예시는 [API 명세서](api-spec.ko.md)에서 확인 가능합니다.

---

## 7. 테스트
- 단위 테스트: JUnit5 + Mockito
- 통합 테스트: Spring Boot + MockMvc
- Flyway 기반 DB 마이그레이션을 포함한 Testcontainers MySQL 환경 → 운영과 유사한 격리된 환경 제공

```bash
./gradlew test
```

---

## 8. 성능 테스트

### 목적
- 주요 API 동시 접속 처리 성능 검증
- HikariCP 커넥션 풀 및 Redis 캐싱 적용 효과 측정

### 환경
- **Server**: AWS EC2 (Spring Boot, MySQL, Redis, Docker)
- **DB Connection Pool**: HikariCP (default 설정)
- **Tool**: k6

### 시나리오
- Virtual Users (VUs): 20 → 50 → 100 단계별 부하 테스트
- Target Endpoints: `/grad_info`, `/courses`, `/facilities`, `/user_summary`
- Success Criteria: P95 응답 시간 < 400ms, 실패율 < 2%

### 결과
| VUs                | Avg (ms) | P95 (ms) | Max (ms) | Error Rate |
|---------------------|----------|----------|----------|------------|
| 20                  | ~47      | ~113     | 848      | 0%         |
| 50                  | ~65      | ~177     | 883      | 0%         |
| 100 (before cache)  | ~1318    | ~2542    | 11898    | 21.2%      |
| 100 (with Redis)    | ~95      | ~210     | 1842     | 0%         |

### 인사이트
- 20~50 VU 구간: 안정적으로 <200ms 응답 유지
- 100 VU (캐시 미적용): DB 부하로 평균 1.3s 지연, 실패율 21.2% 발생
- Redis 캐시 적용 후: 평균 응답 속도 약 14배 개선(1318ms → 95ms), 실패율 0%로 안정화
- HikariCP 풀 사이즈는 기본값(default)이 가장 안정적임을 확인

### 결론
적절한 캐시 전략과 커넥션 풀 관리가 대규모 트래픽 대응의 핵심임을 검증.  
본 프로젝트는 Redis 캐싱 적용을 통해 100 VU 환경에서도 안정적인 성능을 확보.

---

## 9. 배포

이 서비스는 Docker 기반으로 컨테이너화하여 AWS EC2에 배포되었습니다.  
각 구성 요소는 개별 컨테이너로 실행되며, 실행 시 `-e` 옵션을 통해 환경 변수를 주입합니다.

###  백엔드 API
- Spring Boot 애플리케이션을 Docker 이미지로 빌드 후 EC2에 배포
- 내부 네트워크를 통해 데이터베이스와 캐시 서버와 연결

###  데이터베이스
- MySQL 8.0 컨테이너 실행
- 주요 데이터(사용자, 졸업 요건, 과목, 학과, 캠퍼스 시설 등) 저장

###  캐싱 서버
- Redis 컨테이너 실행
- 빈번한 조회 요청을 캐싱하여 응답 속도 최적화

### ️ 인프라
- AWS EC2(Ubuntu)에서 각 컨테이너를 직접 관리 (`docker run …` 방식)
- 실행 시 `-e` 옵션으로 DB 비밀번호, JWT 시크릿 등 민감 정보를 환경 변수로 주입

---

## 10. 데모

🔗 [Live Demo](http://wiseowl1.vercel.app)

> ⚠️ 주의: 현재 데모에 포함된 수업 데이터는 **2021학년도 1학기(한국외국어대학교 기준)** 만 제공됩니다.  
> 반복적인 데이터 입력 작업을 모두 진행하기보다는, 서비스 동작을 검증할 수 있도록  
> 일부 학기 데이터만 예시로 삽입하였습니다.  
> 다른 학기 데이터도 동일한 방식으로 추가 삽입하면 그대로 동작하도록 설계되어 있습니다.

