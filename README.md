# CAU26_DesignPattern

중앙대학교 2026년 2학기 **설계패턴** 텀프로젝트 저장소입니다.

Allen Holub의 Game of Life 구현을 바탕으로, 수업에서 다룬 설계 패턴과 객체지향 설계 원칙을 적용해 **기능을 확장하고 설계를 개선**합니다. 원본이 지원하던 기능은 그대로 유지하며, 변경 사항은 JUnit 테스트로 검증합니다.

---

## 원본 프로젝트

| 항목 | 내용 |
|---|---|
| 저자 | Allen I. Holub |
| 출처 | *Holub on Patterns* (Apress, 2004) 수록 구현 |
| 배포 페이지 | https://holub.com/software/life/index.html |
| 버전 | 배포 페이지 기준 **1.03** (2005-07-16) · 동봉 README에는 1.02로 표기 |
| 원본 README | [`docs/ORIGINAL_README.TXT`](docs/ORIGINAL_README.TXT) |

원본 소스는 커밋 `91016c9` (*Import HolubLife*)에 **수정 없이** 보존되어 있습니다. 기존 코드와 비교할 때는 이 커밋을 기준으로 삼습니다.

---

## 개발 환경

| 항목 | 버전 |
|---|---|
| JDK | **21 이상** 필요 (컴파일 기준 Java 21, `options.release = 21`) |
| 동작 확인 | JDK 23 |
| 빌드 도구 | Gradle 9.8.2 — **Wrapper 포함, 별도 설치 불필요** |
| 테스트 | JUnit 6 (Jupiter) |

> JDK 21 이상이면 어떤 버전이든 빌드할 수 있습니다. 단, 코드는 Java 21 문법·API 범위 안에서만 작성해야 하며, 이를 벗어나면 컴파일러가 에러로 막아줍니다.

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

- 처음 실행할 때 Gradle 본체와 의존성을 내려받느라 시간이 조금 걸립니다.
- 실행 가능한 jar는 `build/libs/CAU26_DesignPattern.jar`에 생성되며, `java -jar build/libs/CAU26_DesignPattern.jar`로 실행할 수 있습니다.
- 테스트 결과 리포트는 `build/reports/tests/test/index.html`에서 확인할 수 있습니다.

---

## 폴더 구조

```
CAU26_DesignPattern/
├── src/
│   ├── main/java/com/holub/
│   │   ├── life/     게임 로직 (Life, Universe, Neighborhood, Cell, Clock 등)
│   │   ├── ui/       메뉴·색상 UI (MenuSite, Colors)
│   │   ├── tools/    공용 도구 (Publisher, Log)
│   │   ├── io/       파일 선택 유틸 (Files)
│   │   └── asynch/   동기화 유틸 (ConditionVariable)
│   └── test/java/    JUnit 테스트 (추가 예정)
├── docs/             문서 (원본 README 등)
├── gradle/wrapper/   Gradle Wrapper
├── build.gradle      빌드 설정
├── settings.gradle
├── gradlew           Wrapper 실행 스크립트 (macOS / Linux)
└── gradlew.bat       Wrapper 실행 스크립트 (Windows)
```
