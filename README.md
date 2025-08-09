# 🍼 Premium Baby Shopping Mall

> **프리미엄 영유아 쇼핑몰** - 개인 프로젝트로 시작해 엔터프라이즈급까지 성장한 풀스택 이커머스

## 🚀 Quick Access

| 버전 | 상태 | 주요 기술 | 상세 보기 |
|------|------|-----------|-----------|
| **v1.0** | ✅ 완료 | Spring Boot + Thymeleaf | [📖 v1.0 상세보기](docs/v1-README.md) |
| **v2.0** | 🚧 진행중 | React  | [📖 v2.0 상세보기](docs/v2-README.md) |

### 🎮 Live Demo
- **현재 버전**: http://spring-mysql.ap-northeast-2.elasticbeanstalk.com/
- **Admin**: `admin@example.com` / `test1234`
- **Customer**: `user@example.com` / `test1234`

---

## 🎯 프로젝트 진화 스토리

### v1.0 → v2.0 핵심 개선점


| 영역     | v1.0                  | v2.0                         | 비고                     |
|----------|-----------------------|------------------------------|--------------------------|
| 아키텍처 | Layered               | 구조 개선 (서비스 분리)      | 코드 가독성 향상         |
| 테스트   | 수동 테스트           | 단위 테스트 + 자동 실행     | 누락 방지                |
| API 문서 | 없음                  | OpenAPI + Swagger UI         | 명세 자동화              |
| 모바일   | 반응형 CSS            | React 도입 + PWA 적용 중     | UX 개선                  |
| 배포     | 수동 배포             | GitHub Actions 자동화        | 반복 작업 제거           |

---

## 💡 주요 문제 해결 사례

### 🎨 UI/UX 개선 (v1.0)
- **문제**: 초기 디자인이 투박하고 비일관적이었으며 사용자 경험이 떨어졌음
- **해결**: 커스텀 CSS 적용 및 레이아웃 개선, Bootstrap 사용 지양 후 직접 스타일링
- **결과**: 사용자 인터페이스 정돈, 페이지 가독성 및 접근성 향상

### 🧪 테스트 자동화 기반 구축 (v2.0)
- **문제**: 코드 변경 시 수동 테스트에만 의존, 오류 누락 가능성
- **해결**: 핵심 서비스 로직에 단위 테스트 작성, GitHub Actions 연동으로 자동 실행
- **결과**: 변경 사항에 대한 빠른 피드백 확보, 디버깅 시간 절감

### ⚛️ React 점진적 도입 (v2.0)
- **문제**: Thymeleaf만으로는 복잡한 동적 UI 구현에 한계
- **해결**: 상품 상세, 장바구니 등 주요 화면부터 React로 점진적 전환
- **결과**: UI 반응성 및 유지보수성 개선, 코드 재사용성 향상

---

## 🛠️ 기술 스택 진화

### Backend
- **Framework**: Spring Boot 2.x
- **Architecture**: Layered Architecture (서비스/책임 분리 중심으로 개선)
- **Database**: MySQL + Spring Data JPA + QueryDSL
- **Security**: Spring Security + JWT

### Frontend
- **v1.0**: Thymeleaf + jQuery
- **v2.0**: React 18 + Redux Toolkit

### DevOps
- **v1.0**: AWS Elastic Beanstalk
- **v2.0**: GitHub Actions CI/CD

---

&nbsp;
* 메인페이지  
![개선전개선후](https://github.com/user-attachments/assets/fde8a04a-fa5f-4053-91e3-dc4eb2a79912)

### v1.0 달성 성과
- ✅ 실제 결제 연동 완료 (포트원 API)
- ✅ AWS 클라우드 배포 성공
- ✅ 다중 이미지 처리 최적화
- ✅ QueryDSL 동적 쿼리 구현

### v2.0 목표 성과
- 🎯 핵심 로직 테스트 코드 작성 및 자동 실행
- 🎯 API 성능 개선 (쿼리 최적화, 페이징 적용)
- 🎯 모바일 UI 개선 및 반응형 대응
- 🎯 GitHub Actions로 배포 자동화
  
---

## 🎥 데모 & 문서

### 📹 시연 영상
- [전체 프로젝트 데모](https://www.youtube.com/watch?v=dvROwZE9FsQ) - v1.0 전체 기능 시연
- [관리자 기능](https://github.com/user-attachments/assets/95caac63-e7f6-4e9f-9c52-923dd1454c94) - 상품/브랜드 관리 시스템
- [실제 결제 과정](https://github.com/user-attachments/assets/6a7e6692-5eb6-439f-8775-c94944cd1f26) - 롯데카드 실결제 완료

### 📚 상세 문서
- [📖 v1.0 개발 과정](docs/v1-README.md) - 기능별 상세 구현 내용
- [📖 v2.0 개발 계획](docs/v2-README.md) - 리팩토링 및 신규 기능
- [📋 API 문서](docs/api-docs.md) - REST API 명세서 (v2.0)

---

## 🏆 프로젝트 하이라이트

> **"단순한 토이 프로젝트에서 시작해 실제 운영 가능한 엔터프라이즈급 시스템까지"**

### 개발자 성장 스토리
1. **v1.0**: "일단 동작하는 것" → 기본 CRUD + 실제 결제
2. **v2.0**: "제대로 동작하는 것" → 클린 코드 + 테스트 + 문서화

### 기술적 도전과 학습
- 💳 **결제 기능 구현**: 포트원 API 연동으로 카드 결제 처리
- 🎨 **UI 개선 작업**: 커스텀 CSS로 디자인 정비 및 가독성 향상
- 🧱 **서비스 구조 정리**: 비대한 서비스 로직을 메서드 단위로 분리
- 📱 **모바일 웹 대응**: 반응형 구성 + PWA 적용으로 설치 가능 처리
---

## 🚀 프로젝트 실행 안내

- 👉 [데모 페이지 바로가기](https://spring-mysql.ap-northeast-2.elasticbeanstalk.com)
- 🧪 테스트 계정
    - 관리자: `admin@example.com` / `test1234`
    - 사용자: `user@example.com` / `test1234`

---

**Contact**: dydgh1095@naver.com  | [Blog](https://immediately-act.tistory.com/)

*"지속적으로 발전하는 개발자가 되겠습니다."*