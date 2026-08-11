# lab18 — 타임코드로 이어보기 (TDD형 / 스프링 MVC / 기초)

## 배경 시나리오

동영상 플레이어의 **'이어보기'** 기능을 만든다. 클라이언트는 마지막으로 보던 위치를
`GET /lab18/resume?at=03:25`처럼 **'분:초' 타임코드** 쿼리 파라미터로 보내고,
서버는 이 위치를 **총 재생 초(seconds)** 로 변환해 응답해야 한다.

HTTP 요청 파라미터는 전부 문자열이다. 하지만 컨트롤러에서 `@RequestParam String at`으로
받아 매번 손으로 파싱하는 대신, **스프링의 타입 변환 기능에 맡겨** 컨트롤러가 곧바로
도메인 타입 `@RequestParam Timecode at`으로 값을 받도록 만든다.

스캐폴드에는 **이미 실패하는 인수 테스트 6개가 빨간불(RED)로 심어져 있다.**
프로덕션 코드를 채워 이 테스트들을 모두 초록불(GREEN)로 만드는 것이 목표다.

## 요구사항

- 요청 예시: `GET /lab18/resume?at=03:25` → 응답 `{"seconds":205}`
- 타임코드 형식: `분:초`
  - 분: 1자리 이상 정수 (0 이상, 자리수 제한 없음)
  - 초: **정확히 2자리 (00~59)**
- 총 초 = 분 × 60 + 초
- 형식에 맞지 않는 타임코드는 **`400 Bad Request`** 로 응답한다.

## 완료 조건

- `at=03:25` → `200 OK`, `{"seconds":205}`
- `at=00:00` → `{"seconds":0}`
- `at=125:30` → `{"seconds":7530}` (분은 자리수 제한 없음)
- `at=03:75` → `400` (초가 60 이상)
- `at=3:5` → `400` (초가 2자리가 아님)
- `at=abc` → `400` (숫자가 아님)
- 심어진 인수 테스트 6개(`TimecodeResumeTest`)가 **모두 통과**한다.

## 제약

- 컨트롤러 시그니처 `@RequestParam Timecode at`을 **그대로 유지**한다 —
  문자열로 받아 컨트롤러 안에서 직접 파싱하지 않는다.
- `Timecode` 클래스에 String을 받는 생성자나 정적 팩토리(`valueOf`, `of` 등)를
  추가하지 않는다.
- 테스트 코드(`TimecodeResumeTest`)는 수정하지 않는다.

## 스캐폴드 구성

| 파일 | 상태 |
|---|---|
| `Timecode.java` | 완성 — 수정 금지 |
| `StringToTimecodeConverter.java` | STUB — 구현할 것 |
| `TimecodeWebConfig.java` | TODO — 채울 것 |
| `ClipController.java` | 완성 — 수정 금지 |
| `TimecodeResumeTest.java` (test) | RED 테스트 6개 — 수정 금지 |

예상 소요 시간: 30~60분
