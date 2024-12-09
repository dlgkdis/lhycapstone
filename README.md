# HARUNIKI - 공동사용자 다이어리 어플

HARUNIKI는 다수의 사용자가 함께 사용할 수 있는 다이어리 애플리케이션으로, 일정 관리, 그림일기, 커스텀 마이룸 등 다양한 기능을 제공합니다. Android Studio와 Firebase를 기반으로 개발되었습니다.

---

## 주요 기능

### 1. **공동 사용자 관리 기능**
여러 사용자가 함께 다이어리를 작성하고 관리할 수 있습니다.
- **LoginActivity**: 사용자 로그인 기능을 담당합니다.
- **InviteActivity**: 다른 사용자를 다이어리에 초대합니다.
- **JoinActivity**: 초대받은 사용자가 다이어리에 참여할 수 있도록 합니다.
- **LeaveActivity**: 사용자가 다이어리를 떠날 수 있도록 합니다.

### 2. **일정 관리 기능**
사용자들의 일정을 체계적으로 관리합니다.
- **ScheduleAddFragment**: 새로운 일정을 추가하고 관리할 수 있는 화면을 제공합니다.

### 3. **그림일기 기능**
그림과 텍스트를 함께 기록할 수 있는 특별한 일기 기능입니다.
- **DiaryWriteFragment**: 사용자가 그림과 일기를 작성할 수 있는 화면을 제공합니다.

### 4. **커스텀 마이룸 기능**
사용자가 자신의 방을 커스터마이즈할 수 있는 기능입니다.
- **MainFragment**: 커스텀 마이룸의 설정과 디자인 기능을 제공합니다.

### 5. **기타 기능**
다양한 부가 기능으로 사용자 경험을 풍부하게 합니다.
- **Bell**: 알림 관련 기능을 처리합니다.
- **Reward**: 보상 시스템을 제공합니다.
- **Store**: 사용자 커스텀 아이템을 구매할 수 있는 스토어 기능을 제공합니다.
- **CoinManager**: 앱 내에서 사용되는 코인 관리 기능을 처리합니다.
- **LockScreenActivity**: 잠금 화면 설정 및 동작을 담당합니다.

---

## 기술 스택
- **Android Studio**: 애플리케이션 UI와 로직 개발
- **Firebase**: 데이터베이스, 인증, 실시간 동기화 등 백엔드 서비스

---

## Firebase 연동
Firebase와의 연결은 **FirebaseHelper.java** 파일에서 처리됩니다.
이 파일은 Firebase Authentication, Realtime Database, Firestore 등 다양한 Firebase 기능과의 인터페이스 역할을 합니다.

---

## 설치 및 실행 방법
1. Android Studio에서 프로젝트를 열기
2. Firebase 프로젝트와 연결하기
3. `build.gradle` 파일에서 필요한 종속성을 설치
4. 앱을 빌드하여 Android 디바이스 또는 에뮬레이터에서 실행

---

## 프로젝트 구조
```
HARUNIKI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── FirebaseHelper.java
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── InviteActivity.java
│   │   │   │   ├── JoinActivity.java
│   │   │   │   ├── LeaveActivity.java
│   │   │   │   ├── ScheduleAddFragment.java
│   │   │   │   ├── DiaryWriteFragment.java
│   │   │   │   ├── MainFragment.java
│   │   │   │   ├── Bell.java
│   │   │   │   ├── Reward.java
│   │   │   │   ├── Store.java
│   │   │   │   ├── CoinManager.java
│   │   │   │   └── LockScreenActivity.java
```

---

## 개발자 정보
- **개발자**: [여기에 이름을 작성하세요]
- **연락처**: [이메일 또는 기타 연락처 작성]

---

HARUNIKI는 협업과 창의성을 결합하여 사용자가 함께 더 나은 일상을 만들어갈 수 있도록 돕습니다. 많은 이용 바랍니다!

