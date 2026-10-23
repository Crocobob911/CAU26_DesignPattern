# CAU26_DesignPattern

중앙대학교 2026년 2학기 **설계패턴**

Allen Holub의 Game of Life 구현을 바탕으로, 수업에서 다룬 설계 패턴과 객체지향 설계 원칙을 적용해 **기능을 확장하고 설계를 개선**

---

## 개발 환경

| 항목 | 버전 |
|---|---|
| JDK | **21 이상** 필요 (컴파일 기준 Java 21, `options.release = 21`) |
| 동작 확인 | JDK 23 |
| 빌드 도구 | Gradle 9.8.2 |
| 테스트 | JUnit 6 (Jupiter) |

> JDK 21 이상이면 어떤 버전이든 빌드 가능

---

## 시작하기

### 1. 저장소 받기

```bash
git clone https://github.com/Crocobob911/CAU26_DesignPattern.git
cd CAU26_DesignPattern
```

### 2. IDE에서 열기

Gradle 프로젝트이므로 IDE가 `build.gradle`을 읽어 자동으로 설정합니다.

| IDE | 방법 |
|---|---|
| IntelliJ IDEA | `Open` → 프로젝트 폴더 선택 |
| Eclipse | `File` → `Import` → `Gradle` → `Existing Gradle Project` |
| VS Code | *Extension Pack for Java* 설치 후 폴더 열기 |

IDE의 Gradle JVM / Project SDK가 **JDK 21 이상**으로 설정되어 있는지 확인하세요.

### 3. 빌드 · 실행 · 테스트

| 작업 | Windows (PowerShell) | macOS / Linux |
|---|---|---|
| 빌드 (컴파일 + 테스트) | `.\gradlew.bat build` | `./gradlew build` |
| 실행 | `.\gradlew.bat run` | `./gradlew run` |
| 테스트만 | `.\gradlew.bat test` | `./gradlew test` |
| 빌드 결과 삭제 | `.\gradlew.bat clean` | `./gradlew clean` |

