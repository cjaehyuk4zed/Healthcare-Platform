
# Healthcare Web Platform

## 백엔드 기술 스택
- Java Spring
- Spring Data JPA
- Spring Security (JWT)
- Spring Reactive (메신저 어플에 사용)
- Querydsl

## 데이터베이스 기술 스택
- MySQL
- MongoDB (메신저 어플에 사용)
- Apache Kafka (메신저 어플에 사용, 미완성)

MySQL을 사용자 정보와 사이트 정보를 담당하는 메인 데이터베이스로 사용하고,
채팅 메신저에서 대화 기록 저장용으로 MongoDB를 사용한다.
(채팅 메신저에 MongoDB 대신 Apache Kafka로 이전하려고 하는 중이지만, 현재는 미완성이다)

## 백엔드 구조

해당 프로젝트는 Controller - Service - Repository 와 DTO 구조를 기반으로 설계되었다.

아래에서 패키지 및 코드 구조와, 사용된 기술 스택에 대해 아래에서 설명한다. 또한, 별도의 기술 스택은 아니지만
설명이 필요할 수 있는 외부 라이브러리 등에 대해서도 설명한다.

### DTO 구조
앞서 말했듯이 협력업체와 방향성이 틀어진 후, 데이터베이스와 API 구조를 한번 크게 뒤바꾼 적이 있었다.
바뀐 DB 구조에 맞춰 DTO 구조도 새로 개편했으나, 중간에 난잡한 DTO 구조들이 있을 수도 있다.

개편 과정에서 일부 DTO는, 하나의 DB 테이블에 관련된 요소들의 DTO를 하나의 클래스에서 한번에 관리하기 위해서 
*nested static class* 구조로 설계했다. 사용은 *ClassName.NestedClassName* 으로 사용하면 된다.
예시 :
```java
UserInfoDTO.Basic
ImageDTO.Save
```

### 글로벌 application.properties
application.properties 파일을 보면, 다음과 같은 줄이 있다 :
```properties
spring.config.additional-location=../../../config/global/
```
해당 경로에 별도로 설정된 application.properties 파일이 하나 존재한다.
여기에 messenger와 platform에서 서로 공유하는 속성들이 설정되어 있다.
특히, JWT 보안 관련 속성이 이쪽에 있는 것을 명심해야 한다.

### Spring Security - JWT
Spring Security로 JWT 토큰 인증 방식을 사용한다.

사용자는 아이디와 비번으로 로그인하고, 로그인하면  Access Token과 Refresh Token 둘 다 발급된다. 이후 사이트 내에서 이동시
HTTP Bearer Authentication 방식으로 JWT Access Token을 확인하여 로그인을 검증하고, Access Token 만료시
Refresh Token을 활용해서 새로운 Access Token을 발급받는다. 이때 Access Token은 클라이언트 HTTP 헤더에 존재하고,
Refresh Token은 Cookie에 존재한다.

### Spring Security - API
Swagger-UI OpenAPI 라이브러리를 활용하여 작성한 API 명세서 페이지가 있다.
각 API의 기능은 해당 명세서에서 확인하거나, 아니면 Controller에서 *@Operation* 어노테이션의 설명을 읽으면 된다.

Spring Security에서 다수의 filter들이 로그인 검증 및 log 기록을 한다.
각 Controller의 API 도메인에 대해서, 모든 GET요청은 로그인하지 않은 사용자도 접속이 가능하도록 설정이 되어있다.
그 외에 POST, PATCH, DELETE 등은 로그인 한 사용자만 허용하도록 설정되어 있다.
새로운 Controller 추가 또는 HTTP method 허용 범위 수정은 config 패키지의 SecurityConfig.java에서 할 수 있다.

### Spring Security - Beans
Spring Security에서 활용할 Bean들은 별도로 config 패키지에 
SecurityBeansConfig.java에서 관리한다. UserDetailsService, PasswordEncoder, AuthenticationProvider,
AuthenticationManager이 현재 각자의 구현과 함께 등록되어 있다. 
- 여기서 꼭 명심할 점이 1가지 있다. domain 패키지 아래에 User_Auth.java가 UserDetails 클래스를 구현(implement)한다.
- 이 User_Auth 객체가 사용자 로그인을 담당하는 객체다. 그리고 이 User_Auth 객체가 UserDetailsService bean에 전달되어 사용된다.

### Spring Security - User Permissions
authentication 패키지 안에 Permissions.java가 존재하지만, 아직 구현되지 않았다.

### 외부 라이브러리 - ModelMapper
build.gradle 파일에 추가된 외부 라이브러리 중 ModelMapper 라는 것이 있다.
https://mvnrepository.com/artifact/org.modelmapper/modelmapper

이는 데이터를 다른 클래스 객체로 타입 변환할 때 자동으로 mapping 및 사용자 지정 mapping을 해주는 library이다.
기본 설정으로는 변수 이름이 동일하면 자동으로 데이터를 매칭해주며, 동일한 변수 이름이 없는 데이터는 null 처리를 해준다.

config 패키지 아래에 ModelMapperConfig.java 파일을 보면, 사용자 지정 설정이 있다. 
변수 이름이 동일하지만 타입이 다른 변수로 변환을 지정 설정하거나, 이름이 다른 변수로 변환 설정 등을 할 수 있다.

ModelMapper 클래스 객체가 이미 Spring Bean으로 등록이 되어 있으며, 기본 설정도 되어 있는 상태이다.
여기에 사용자 지정 변환을 추가하려면 다음과 같은 형식으로 추가하면 된다.
```java
modelMapper.addMappings(new PropertyMap<SourceClass, DestinationClass>() {
    @Override
    protected void configure() {
        map().setDestinationData(source.getData);
        //...etc add more mappings if needed
    }
});
```


### 외부 라이브러리 - Querydsl
#### 동적 쿼리를 위한 QFile 생성
Application.properties 설정에 QFile 재생성 관련 설정이 되어있으며,
다음 명령을 순서대로 터미널에서 실행하면 된다 :
```bash
./gradlew clean
./gradlew compileJava
```

### Exception Handling
config 패키지 아래에 ExceptionHandler.java 에서 예외를 catch하고 처리한다. 
