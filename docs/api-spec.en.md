# 📑 API Specification
This document describes the REST API of the WiseOwl backend.  
Endpoints are grouped by domain, with request/response examples provided.

---
<div align="center">
<h4><b> Domains </b></h4>
<p align="center">
  <a href="#-auth-domain">Auth</a> •
  <a href="#-user-domain">User</a> •
  <a href="#-course-domain">Course</a> •
  <a href="#-facility-domain">Facility</a>
</p>
</div>

---

## 🔐 Auth Domain

### 1. Sign Up
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

---

### 2. Login
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

---

### 3. Google Login
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

---

### 4. Change Password
**Endpoint**: `PATCH /api/auth/password`

**Request**
```json
{
  "currentPassword": "old_password",
  "newPassword": "new_password123"
}
```
**Response (204 No Content)**  
_No body returned_

---

### 5. Refresh Access Token
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

---

### 6. Logout
**Endpoint**: `POST /api/auth/logout`

**Request Header**
```
Refresh-Token: {refreshToken}
```
**Response (204 No Content)**  
_No body returned_

---

## 👤 User Domain

### 1. Update Profile
**Endpoint**: `POST /api/users/me/profile`

**Request**
```json
{
  "name": "John Doe",
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
_No body returned_

---

### 2. Register Completed Courses
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
_No body returned_

---

### 3. Get Graduation Requirements
**Endpoint**: `GET /api/users/me/graduation-requirements`

**Response (200 OK)**
```json
{
  "requirements": [
    {
      "majorName": "Computer Science",
      "majorType": "PRIMARY",
      "earnedCredits": 80,
      "requiredCredits": 120,
      "requirements": [
        {
          "userRequirementStatusId": 1,
          "name": "Thesis",
          "description": "Thesis submission completed",
          "fulfilled": true
        }
      ]
    }
  ]
}
```

---

### 4. Update Requirement Status
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
_No body returned_

---

### 5. Get Graduation Overview
**Endpoint**: `GET /api/users/me/graduation-info`

**Response (200 OK)**
```json
{
  "username": "John Doe",
  "requirementStatuses": [
    {
      "majorName": "Computer Science",
      "majorType": "PRIMARY",
      "earnedCredits": 80,
      "requiredCredits": 120,
      "requirements": [
        {
          "userRequirementStatusId": 1,
          "name": "Thesis",
          "description": "Thesis submission completed",
          "fulfilled": true
        }
      ]
    }
  ]
}
```

---

### 6. Get Required Courses Status
**Endpoint**: `GET /api/users/me/required-courses?majorType=PRIMARY`

**Response (200 OK)**
```json
{
  "majorRequiredCourses": [
    { "courseCode": "CS101", "courseName": "Data Structures", "fulfilled": true }
  ],
  "liberalRequiredCourses": [
    { "liberalCategoryName": "General Education", "fulfilled": false, "requiredCredit": 9 }
  ]
}
```

---

### 7. Get User Summary
**Endpoint**: `GET /api/users/me/summary`

**Response (200 OK)**
```json
{
  "username": "John Doe",
  "entranceYear": 2022,
  "gpa": 3.87,
  "primaryMajor": {
    "userMajorId": 1,
    "collegeId": 10,
    "collegeName": "College of Engineering",
    "majorId": 101,
    "majorName": "Computer Science",
    "majorType": "PRIMARY"
  },
  "doubleMajor": {
    "userMajorId": 2,
    "collegeId": 20,
    "collegeName": "College of Business",
    "majorId": 202,
    "majorName": "Business Administration",
    "majorType": "DOUBLE"
  },
  "track": "AI"
}
```

---

### 8. Update Major
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
_No body returned_

---

### 9. Update Major Types
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
_No body returned_

---

### 10. Get Completed Courses
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
          "courseName": "Data Structures",
          "credit": 3
        }
      ]
    }
  ]
}
```

---

### 11. Update Completed Courses
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
_No body returned_

---

### 12. Delete Completed Course
**Endpoint**: `DELETE /api/users/me/completed-courses/{userCompletedCourseId}`

**Response (204 No Content)**  
_No body returned_

---

### 13. Delete User
**Endpoint**: `DELETE /api/users/me`

**Response (204 No Content)**  
_No body returned_

---

## 📚 Course Domain

### 1. Get Course Categories
**Endpoint**: `GET /api/courses/course-categories?semesterId={semesterId}`

**Response (200 OK)**
```json
{
  "courseCategories": [
    { "id": 1, "name": "Major Required", "type": "MAJOR" },
    { "id": 2, "name": "General Education Required", "type": "LIBERAL" }
  ]
}
```

---

### 2. Get Course Offerings by Semester
**Endpoint**: `GET /api/courses/offerings?semesterId={semesterId}`

**Response (200 OK)**
```json
{
  "offerings": [
    {
      "id": 101,
      "majorId": 1,
      "liberalCategoryId": null,
      "courseName": "Data Structures",
      "professor": "John Doe",
      "classTime": "Mon 3-4",
      "courseCode": "CS101",
      "room": "Engineering Hall 101",
      "credit": 3
    },
    {
      "id": 102,
      "majorId": null,
      "liberalCategoryId": 5,
      "courseName": "English Conversation",
      "professor": "Jane Doe",
      "classTime": "Tue 2-3",
      "courseCode": "ENG201",
      "room": "Humanities Hall 202",
      "credit": 2
    }
  ]
}
```

---

### 3. Get Colleges and Majors
**Endpoint**: `GET /api/courses/colleges-with-majors`

**Response (200 OK)**
```json
{
  "colleges": [
    {
      "collegeId": 10,
      "collegeName": "College of Engineering",
      "majors": [
        { "majorId": 1, "majorName": "Computer Science" },
        { "majorId": 2, "majorName": "Electrical Engineering" }
      ]
    },
    {
      "collegeId": 20,
      "collegeName": "College of Humanities",
      "majors": [
        { "majorId": 3, "majorName": "Korean Literature" },
        { "majorId": 4, "majorName": "Philosophy" }
      ]
    }
  ]
}
```

---

## 🏢 Facility Domain

### 1. Get All Facilities
**Endpoint**: `GET /api/facilities`

**Response (200 OK)**
```json
{
  "buildings": [
    {
      "buildingNumber": 101,
      "buildingName": "Engineering Hall",
      "facilities": [
        {
          "name": "Student Cafeteria",
          "floor": 1,
          "facilityCategory": "RESTAURANT",
          "description": "Serves Korean food and snacks"
        },
        {
          "name": "Computer Lab",
          "floor": 2,
          "facilityCategory": "IT_EQUIPMENT",
          "description": "Equipped with printers and high-performance PCs"
        }
      ]
    },
    {
      "buildingNumber": 202,
      "buildingName": "Library",
      "facilities": [
        {
          "name": "Reading Room",
          "floor": 3,
          "facilityCategory": "READING_ROOM",
          "description": "Open 24 hours"
        },
        {
          "name": "Cafe",
          "floor": 1,
          "facilityCategory": "CAFE",
          "description": "Beverages and study space available"
        }
      ]
    }
  ]
}
```
