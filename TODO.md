# 해야할 일
1. ~~jwt 발급 서비스 생성~~
2. APIGW 인증 필요/불필요 페이지 분기. 증명이 필요하지 않은 페이지는 아래와 같고 그 외에는 모두 jwt token을 헤더에 포함하고있어야한다.(없으면 메인페이지로 redirect)
  - 증명 페이지
  - 회원가입 / 로그인
  - 메인페이지
4. ~~redis db 생성 및 insert / update / delete~~
5. ~~filter에서 token 인증~~ -> (NEXT) Redis 필요한가?
6. ~~filter에서 token 복호화 및 유저Id responseBody 추가~~
7. filter에서 업데이트 작업 (후처리)
8. ~~로그인 시 filter에서 후처리 + jwt 초기 발급~~
9. ~~redis -> 만료시간 setting~~
10. ~~token 만료 시간 세팅~~
11. User Manager sigup 수정 : Front에서 Access Key 전달 받는다. -> access key 기반으로 userinfo를 google에서 받아 회원가입 진행.
12. AWS에 micro service 구축
13. 추가 서비스 생각 & 구축
14. ~~API 문서 업데이트~~
15. .ConnectException 구현 
16. list JSONObject 처리


# 정한것
1. Frontend로부터 access_token만 받고 백앤드에서 access_token으로 Google Resource를 받아 회원가입을 진행한다.



