# lab05 — 커넥션을 삼키는 조회수 API (디버깅형)

- 분야: 스프링 DB / 난이도: 기초 / 예상 소요: 30~60분
- 패키지: `com.vryez.backendlab.lab05.videoview`

## 배경 시나리오

동영상 스트리밍 서비스에서 영상 재생 시 조회수를 1 올리는 내부 기능 `increaseViewCount(videoId)`를 순수 JDBC(+ 커넥션 풀)로 구현해 배포했다. 로컬에서 한두 번 눌러볼 땐 멀쩡했는데, QA가 같은 영상 상세 페이지를 빠르게 여러 번 새로고침하자 아래 장애가 재현됐다.

- 처음 **3번 정도**의 조회수 증가 요청은 정상 처리된다.
- 그 직후부터는 요청이 **약 1초간 멈췄다가** 예외를 던지며 실패한다.
- 애플리케이션 로그에 이런 에러가 찍힌다:
  - `HikariPool-1 - Connection is not available, request timed out after 1000ms`
  - `java.sql.SQLTransientConnectionException`
- 애플리케이션을 **재시작하면 잠깐 정상**으로 돌아왔다가, 몇 번만 호출하면 똑같이 멈춘다.

DB 서버는 멀쩡하고, 실행되는 SQL(`update video_view set view_count = view_count + 1 where video_id = ?`) 자체도 정상이다. 쿼리는 맞는데, 호출이 조금만 쌓이면 **커넥션을 얻지 못해** 죽는다. 원인을 찾아 고쳐라.

## 요구사항

- `increaseViewCount`를 여러 번 연속 호출해도 위 장애가 재발하지 않도록 원인을 찾아 고친다.
- 조회수 증가/조회 결과의 정확성은 그대로 유지한다.

## 완료 조건

- `increaseViewCount("v1")`을 **10회 연속** 호출해도 예외 없이 모두 성공한다.
- 10회 호출 후 `getViewCount("v1")`이 정확히 **10**을 반환한다(누적 정확성 유지).
- 존재하지 않는 videoId 조회처럼 **예외가 발생하는 경로를 반복 호출**한 뒤에도 이후 정상 요청이 계속 성공한다(예외 경로에서도 커넥션이 새지 않는다).

## 제약

- 커넥션 풀 최대 크기(현재 `3`)와 `connection-timeout`(현재 `1000ms`)은 **건드리지 않는다**. 설정으로 증상을 미루지 말고 원인을 제거한다.
- `VideoViewLabConfig`의 풀 설정(`maximum-pool-size`, `connection-timeout`)을 변경하지 않고 완료 조건을 통과해야 한다.

## 실행 방법

```bash
./gradlew bootRun   # 앱 기동 (스키마는 기동 시 자동 생성, 'v1' 시드 포함)
./gradlew test --tests 'com.vryez.backendlab.lab05.*'   # 채점 시 테스트가 추가될 위치
```
