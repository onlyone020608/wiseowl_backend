# 📑 API 명세서
본 문서는 WiseOwl 백엔드의 REST API 명세서입니다.  
도메인별로 구분하여 주요 엔드포인트와 요청/응답 예시를 정리합니다.

---
<div align="center">
<h4><b> 도메인 </b></h4>
<p align="center">
  <a href="#-auth-도메인">Auth</a> •
  <a href="#-user-도메인">User</a> •
  <a href="#-course-도메인">Course</a> •
  <a href="#-facility-도메인">Facility</a>
</p>
</div>

---

## 🔐 Auth 도메인

### 1. 회원가입
**Endpoint**: `POST /api/auth`

**Request**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
**Response (201 Created)**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "newUser": true
}
```

### 2. 로그인
**Endpoint**: `POST /api/auth/login`

**Request**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
**Response (200 OK)**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "newUser": false
}
```

### 3. 구글 로그인
**Endpoint**: `POST /api/auth/oauth/google`

**Request**
```json
{
  "authCode": "authorization_code_from_google"
}
```
**Response (200 OK)**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "newUser": true
}
```

### 4. 비밀번호 변경
**Endpoint**: `PATCH /api/auth/password`

**Request**
```json
{
  "currentPassword": "old_password",
  "newPassword": "new_password123"
}
```
**Response (204 No Content)**
- 본문 없음

### 5. 액세스 토큰 재발급
**Endpoint**: `POST /api/auth/refresh`

**Request Header**
```
Refresh-Token: {refreshToken}
```
**Response (200 OK)**
```json
{
  "accessToken": "new_access_token",
  "refreshToken": "existing_refresh_token",
  "newUser": false
}
```

### 6. 로그아웃
**Endpoint**: `POST /api/auth/logout`

**Request Header**
```
Refresh-Token: {refreshToken}
```
**Response (204 No Content)**
- 본문 없음
---


## 👤 User 도메인

### 1. 프로필 수정
**Endpoint**: `POST /api/users/me/profile`

**Request**
```json
{
  "name": "홍길동",
  "entranceYear": 2022,
  "majors": [
    {
      "majorId": 1,
      "majorType": "PRIMARY"
    }
  ],
  "track": "AI"
}
```

**Response (200 OK)**
- 본문 없음

---

### 2. 이수 과목 등록
**Endpoint**: `POST /api/users/me/completed-courses`

**Request**
```json
{
  "courses": [
    {
      "courseOfferingId": 101,
      "grade": "A+",
      "retake": false
    }
  ]
}
```

**Response (201 Created)**
- 본문 없음

---

### 3. 졸업 요건 조회
**Endpoint**: `GET /api/users/me/graduation-requirements`

**Response (200 OK)**
```json
{
  "requirements": [
    {
      "majorName": "컴퓨터공학",
      "majorType": "PRIMARY",
      "earnedCredits": 80,
      "requiredCredits": 120,
      "requirements": [
        {
          "userRequirementStatusId": 1,
          "name": "졸업논문",
          "description": "졸업논문 제출 완료",
          "fulfilled": true
        }
      ]
    }
  ]
}
```

---

### 4. 졸업 요건 충족 상태 수정
**Endpoint**: `PUT /api/users/me/graduation-requirements`

**Request**
```json
[
  {
    "userRequirementStatusId": 1,
    "fulfilled": true
  }
]
```

**Response (204 No Content)**
- 본문 없음

---

### 5. 메인 졸업 현황 조회
**Endpoint**: `GET /api/users/me/graduation-info`

**Response (200 OK)**
```json
{
  "username": "홍길동",
  "requirementStatuses": [
    {
      "majorName": "컴퓨터공학",
      "majorType": "PRIMARY",
      "earnedCredits": 80,
      "requiredCredits": 120,
      "requirements": [
        {
          "userRequirementStatusId": 1,
          "name": "졸업논문",
          "description": "졸업논문 제출 완료",
          "fulfilled": true
        }
      ]
    }
  ]
}
```

---

### 6. 필수 과목 이수 상태 조회
**Endpoint**: `GET /api/users/me/required-courses?majorType=PRIMARY`

**Response (200 OK)**
```json
{
  "majorRequiredCourses": [
    { "courseCode": "CS101", "courseName": "자료구조", "fulfilled": true }
  ],
  "liberalRequiredCourses": [
    { "liberalCategoryName": "교양필수", "fulfilled": false, "requiredCredit": 9 }
  ]
}
```

---

### 7. 요약 정보 조회
**Endpoint**: `GET /api/users/me/summary`

**Response (200 OK)**
```json
{
  "username": "홍길동",
  "entranceYear": 2022,
  "gpa": 3.87,
  "primaryMajor": {
    "userMajorId": 1,
    "collegeId": 10,
    "collegeName": "공과대학",
    "majorId": 101,
    "majorName": "컴퓨터공학",
    "majorType": "PRIMARY"
  },
  "doubleMajor": {
    "userMajorId": 2,
    "collegeId": 20,
    "collegeName": "경영대학",
    "majorId": 202,
    "majorName": "경영학",
    "majorType": "DOUBLE"
  },
  "track": "AI"
}
```

---

### 8. 전공 변경
**Endpoint**: `PATCH /api/users/me/majors`

**Request**
```json
[
  {
    "majorType": "PRIMARY",
    "oldMajorId": 1,
    "newMajorId": 2
  }
]
```

**Response (204 No Content)**
- 본문 없음

---

### 9. 전공 유형 변경
**Endpoint**: `PATCH /api/users/me/majors/type`

**Request**
```json
{
  "userMajorTypeUpdateItems": [
    {
      "majorType": "DOUBLE",
      "oldMajorId": 2,
      "newMajorId": 3
    }
  ],
  "track": "AI"
}
```

**Response (204 No Content)**
- 본문 없음

---

### 10. 이수 과목 조회
**Endpoint**: `GET /api/users/me/completed-courses`

**Response (200 OK)**
```json
{
  "semesters": [
    {
      "semesterId": 1,
      "year": 2023,
      "term": "FIRST",
      "completedCourses": [
        {
          "userCompletedCourseId": 1,
          "grade": "A+",
          "retake": false,
          "courseName": "자료구조",
          "credit": 3
        }
      ]
    }
  ]
}
```

---

### 11. 이수 과목 수정
**Endpoint**: `PATCH /api/users/me/completed-courses`

**Request**
```json
[
  {
    "userCompletedCourseId": 1,
    "grade": "A0",
    "retake": false
  }
]
```

**Response (204 No Content)**
- 본문 없음

---

### 12. 이수 과목 삭제
**Endpoint**: `DELETE /api/users/me/completed-courses/{userCompletedCourseId}`

**Response (204 No Content)**
- 본문 없음

---

### 13. 회원 탈퇴
**Endpoint**: `DELETE /api/users/me`

**Response (204 No Content)**
- 본문 없음
---

## 📚 Course 도메인

### 1. 수강 과목 카테고리 조회
**Endpoint**: `GET /api/courses/course-categories?semesterId={semesterId}`

**Response (200 OK)**
```json
{
  "courseCategories": [
    { "id": 1, "name": "전공필수", "type": "MAJOR" },
    { "id": 2, "name": "교양필수", "type": "LIBERAL" }
  ]
}
```

---

### 2. 학기별 개설 과목 조회
**Endpoint**: `GET /api/courses/offerings?semesterId={semesterId}`

**Response (200 OK)**
```json
{
  "offerings": [
    {
      "id": 101,
      "majorId": 1,
      "liberalCategoryId": null,
      "courseName": "자료구조",
      "professor": "홍길동",
      "classTime": "월 3-4",
      "courseCode": "CS101",
      "room": "공학관 101호",
      "credit": 3
    },
    {
      "id": 102,
      "majorId": null,
      "liberalCategoryId": 5,
      "courseName": "영어회화",
      "professor": "Jane Doe",
      "classTime": "화 2-3",
      "courseCode": "ENG201",
      "room": "인문관 202호",
      "credit": 2
    }
  ]
}
```

---

### 3. 단과대학 및 전공 목록 조회
**Endpoint**: `GET /api/courses/colleges-with-majors`

**Response (200 OK)**
```json
{
  "colleges": [
    {
      "collegeId": 10,
      "collegeName": "공과대학",
      "majors": [
        { "majorId": 1, "majorName": "컴퓨터공학" },
        { "majorId": 2, "majorName": "전기전자공학" }
      ]
    },
    {
      "collegeId": 20,
      "collegeName": "인문대학",
      "majors": [
        { "majorId": 3, "majorName": "국어국문학" },
        { "majorId": 4, "majorName": "철학" }
      ]
    }
  ]
}
```
---

## 🏢 Facility 도메인

### 1. 캠퍼스 시설 전체 조회
**Endpoint**: `GET /api/facilities`

**Response (200 OK)**
```json
{
  "buildings": [
    {
      "buildingNumber": 101,
      "buildingName": "공학관",
      "facilities": [
        {
          "name": "학생식당",
          "floor": 1,
          "facilityCategory": "RESTAURANT",
          "description": "한식, 분식 제공"
        },
        {
          "name": "컴퓨터실",
          "floor": 2,
          "facilityCategory": "IT_EQUIPMENT",
          "description": "프린터 및 고성능 PC 구비"
        }
      ]
    },
    {
      "buildingNumber": 202,
      "buildingName": "도서관",
      "facilities": [
        {
          "name": "열람실",
          "floor": 3,
          "facilityCategory": "READING_ROOM",
          "description": "24시간 개방"
        },
        {
          "name": "카페",
          "floor": 1,
          "facilityCategory": "CAFE",
          "description": "스터디 공간과 음료 제공"
        }
      ]
    }
  ]
}
```

