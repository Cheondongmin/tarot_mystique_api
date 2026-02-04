# Tarot Mystique API

타로 활동 저장을 위한 백엔드 API

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)

## 기술 스택

### Backend
- Kotlin 1.9.25
- Spring Boot 3.4.5
- Spring Data MongoDB
- Spring Web
- Spring Validation

### Database
- MongoDB Atlas

### Tools
- Jackson
- Logback

## 시작하기

### 요구사항
- JDK 21+
- MongoDB Atlas 계정

### 실행

```bash
git clone https://github.com/Cheondongmin/tarot_mystique_api.git
cd tarot_mystique_api
./gradlew bootRun
```

## 프로젝트 구조

```
src/main/kotlin/com/hangtudy/
├── app/
│   ├── domain/                    # 도메인 로직
│   │   └── Tarot/
│   │       ├── Activity.kt           # 타로 활동 엔티티
│   │       ├── ActivityRepository.kt # 데이터 저장소
│   │       └── TarotService.kt       # 비즈니스 로직
│   ├── infrastructure/            # 인프라 계층
│   │   └── repository/tarot/
│   └── interfaces/               # API 계층
│       └── api/v1/
│           ├── common/               # 공통 응답
│           │   ├── CommonRes.kt
│           │   └── ResultType.kt
│           ├── exception/            # 예외 처리
│           │   ├── ApiException.kt
│           │   ├── ApiExceptionHandler.kt
│           │   ├── ExceptionCode.kt
│           │   └── ExceptionMessage.kt
│           ├── health/              # 헬스체크
│           │   └── HealthController.kt
│           └── tarot/               # 타로 API
│               ├── TarotController.kt
│               └── req/
│                   └── AddTarotReq.kt
├── config/                       # 설정
│   ├── CorsConfig.kt
│   ├── PerformanceConfig.kt
│   ├── RequestCachingFilter.kt
│   ├── RequestLoggingInterceptor.kt
│   └── WebConfig.kt
└── HangtudyApplication.kt        # 메인 애플리케이션
```

## API 엔드포인트

### 타로 활동 저장
```http
POST /api/v1/tarot/add
Content-Type: application/json

{
  "category": "연애운",
  "userContent": "올해 연애운이 궁금합니다.",
  "resultContent": "{\"cards\":[{\"name\":\"The Star\",\"nameKr\":\"별\",\"reversed\":false,\"interpretation\":\"희망적인 미래\"}],\"interpretation\":\"좋은 소식이 있을 것입니다.\",\"timestamp\":\"2025-05-26T10:30:00Z\"}",
  "userIp": "127.0.0.1"
}
```

응답:
```json
{
  "resultType": "SUCCESS",
  "data": "타로 데이터가 성공적으로 저장되었습니다."
}
```

### 헬스체크
```http
GET /api/v1/health/check
```

응답:
```json
{
  "status": "UP",
  "timestamp": "2025-05-26T10:30:15"
}
```

## 데이터 모델

### Activity
```kotlin
@Document(collection = "Activity")
data class Activity(
    @Id val id: String? = null,
    val category: String,           // 타로 카테고리 (연애운, 직업운 등)
    val ipAddress: String,          // 사용자 IP 주소
    val userContent: String,        // 사용자 질문
    val resultContent: String,      // 타로 결과 (JSON 문자열)
    val resultData: TarotResult?,   // 파싱된 타로 결과 데이터
    val createdAt: LocalDateTime,   // 생성 시간 (UTC)
    val updatedAt: LocalDateTime,   // 수정 시간 (UTC)
    val createdAtKst: LocalDateTime,// 생성 시간 (KST)
    val updatedAtKst: LocalDateTime // 수정 시간 (KST)
)
```

### TarotResult
```kotlin
data class TarotResult(
    val cards: List<TarotCard>,     // 뽑힌 카드들
    val interpretation: String,     // 전체 해석
    val timestamp: String           // 타로 시행 시간
)

data class TarotCard(
    val name: String,               // 카드 영문명
    val nameKr: String,             // 카드 한글명
    val reversed: Boolean,          // 역방향 여부
    val interpretation: String      // 카드 해석
)
```

## 요청 검증

```kotlin
data class AddTarotReq(
    @field:NotBlank(message = "카테고리는 필수입니다.")
    @field:Size(max = 50, message = "카테고리는 50자 이하로 입력해주세요.")
    val category: String,
    
    @field:NotBlank(message = "사용자 내용은 필수입니다.")
    @field:Size(max = 200, message = "질문은 200자 이하로 입력해주세요.")
    val userContent: String,
    
    @field:NotBlank(message = "결과 내용은 필수입니다.")
    @field:Size(max = 5000, message = "결과 내용은 5000자 이하로 입력해주세요.")
    val resultContent: String,
    
    @field:NotBlank(message = "IP 주소는 필수입니다.")
    @field:Size(max = 45, message = "IP 주소는 45자 이하로 입력해주세요.")
    val userIp: String
)
```

### 에러 응답
```json
{
  "resultType": "FAIL",
  "data": {},
  "exception": {
    "code": "MISSING_REQUIRED_FIELD",
    "message": "필수 필드 'userContent'이(가) 누락되었습니다.",
    "data": null
  }
}
```

## 로깅

### 로그 파일
```
log/
├── logger.log              # 애플리케이션 로그
├── api_request.log         # API 요청 로그  
└── logger-2025-05-26.log.gz  # 일별 압축 보관
```

### 로그 형식
```
2025-05-26 10:30:15 [INFO] [tx-12345] [POST] [/api/v1/tarot/add] [127.0.0.1] 타로 데이터 저장
```

### 설정
- 환경별 로그 레벨: local(DEBUG), dev/prod(INFO)
- 일별 자동 롤링 및 압축 보관
- MDC: Transaction ID, HTTP Method, URL, Client IP

## 관련 링크

- [Frontend Repository](https://github.com/mcp-space/tarot_mystique_web)
