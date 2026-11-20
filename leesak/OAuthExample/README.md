# OAuthExample 실습 시작용

1. 압축을 풀고 IntelliJ에서 `OAuthExample` 폴더를 Gradle 프로젝트로 엽니다. Gradle JVM은 Java 17을 선택합니다.
2. `build.gradle`에는 IntelliJ에서 선택한 기본 스타터 6개가 들어 있습니다. 강의자료의 **17. build.gradle에 의존성 직접 추가**를 따라 Validation·JJWT·Gson 선언을 넣고 Gradle을 동기화한 뒤 빌드합니다.
3. 강의자료의 **18. application.yml 설정**을 따라 실습용 MySQL과 본인의 Google Cloud 클라이언트 값 등을 IntelliJ의 `OAuthExampleApplication` 실행 환경 변수에 입력합니다.
4. `OAuthExampleApplication`을 실행합니다. 아래 URL에서 `YOUR_CLIENT_ID`를 본인 값으로 바꾸고, 리디렉션 URI를 Google Cloud에 등록한 값과 맞춘 뒤 브라우저 주소창에서 엽니다.

```text
http://localhost:8080/api/oauth2/login/google
```

5. 구글 로그인 후 콜백 응답의 `accessToken`을 Postman의 `GET /users/me` 요청에 Bearer Token으로 넣습니다.

이 예제는 로컬 실습용입니다. 운영 서비스에서는 로그인 요청과 콜백을 연결하는 `state` 검증을 적용합니다.
