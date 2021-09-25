# 완료한 일
1. ~~jwt 발급 서비스 생성~~
2. ~~APIGW 인증 필요/불필요 페이지 분기. 증명이 필요하지 않은 페이지는 아래와 같고 그 외에는 모두 jwt token을 헤더에 포함하고있어야한다.~~
  - 증명 페이지
  - ~~회원가입 / 로그인~~
  - ~~메인페이지~~
4. ~~redis db 생성 및 insert / update / delete~~
5. ~~filter에서 token 인증~~
6. ~~filter에서 token 복호화 및 유저Id responseBody 추가~~
7. ~~filter에서 업데이트 작업 (후처리)~~
8. ~~로그인 시 filter에서 후처리 + jwt 초기 발급~~
9. ~~redis -> 만료시간 setting~~
10. ~~token 만료 시간 세팅~~
11. ~~User Manager sigup 수정 : Front에서 Access Key 전달 받는다. -> access key 기반으로 userinfo를 google에서 받아 회원가입 진행.~~
14. ~~API 문서 업데이트~~
17. ~~list JSONObject 처리~~


# 해야할 일 : 토요일 18시 이전까지
0. AWS 키 나오면 미리 준비해서 배포.
1. (태훈) UserManager API 응답 최신 포멧에 맞게끔 수정.
2. ~~(현주) Access Token(Bearer), Id Token(Body)을 받아야한다. -> 문서 업데이트~~
3. (태훈) AWS에 micro service 구축 -> 토요일 18시 이전까지 각 서비스 업데이트완료 예정. 토요일 18시 이후에 빌드해서 EC2에 쌩으로 배포, 일요일 오전 10시에 같이 테스트
   - (현주) RDS, S3 등 나눌수 있는 부분은 나눈다.
4. (태훈, 현주 생각) 추가 서비스 생각 & 구축
   - URL Shortener 어떻게? -> 청화님께 요청.
5. ~~(태훈) 다른 서비스로의 라우팅처리~~
6. (현주) ~~인증 에러시 body, message 세팅~~ 
7. ~~(현주) ConnectException 구현 (ex. 내부 서비스 끼리의 문제가 발생했을 때의 처리) -> Exception 잡아다가 code, message에 내용 추가해서 넘겨주기. Apigw에서 401 반환 시에 Body 넣어주기. -> 방법 없다면 이대로도 괜찮.~~
