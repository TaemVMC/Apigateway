~~1. jwt 발급 서비스 생성~~
2. 토큰 필요 uri / 불필요 uri 분기 
- 증명 페이지
- 회원가입 / 로그인 
- 
~~4. redis db 생성 및 insert / update / delete~~ 
5. filter에서 token 인증
6. filter에서 token 복호화 및 유저Id responseBody 추가 
7. filter에서 업데이트 작업 (후처리)
8. ~~로그인 시 filter에서 후처리 + jwt 초기 발급~~
9. ~~redis -> 만료시간 setting~~
10. ~~token 만료 시간 세팅~~
11. .ConnectException 구현 
12. list JSONObject 처리 



---
1. Front에서 Access Key 전달 받으면 access key 기반으로 userinfo get하도록 USER manager modify
2. AWS에 micro service 구축 
3. 추가 서비스 생각 & 구축 
4. API 문서 업데이트 
```
127.0.0.1:6379> set name hyunjin # set을 이용하여 key, value형식으로 저장이 가능
OK # 응답값

127.0.0.1:6379> get name # get { key } 형식으로 key에 해당하는 value을 가져 올 수 있음.
"hyunjin"

127.0.0.1:6379> expire name 10 # expire을 이용하여 해당 키의 만료시간을 설정할 수 있음.(10초)
(integer) 1 

127.0.0.1:6379> ttl name  # ttl을 이용하여 해당 키의 만료시간을 알아 낼 수 있음(7초남음)
(integer) 7 

127.0.0.1:6379> ttl name # ttl을 이용하여 해당 키의 만료시간을 알아 낼 수 있음(4남음)
(integer) 4 

127.0.0.1:6379> ttl name # 만료됨.
(integer) -2 

127.0.0.1:6379> get name # 만료된 key값 호출 
(nil) 

```