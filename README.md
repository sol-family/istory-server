# IStory API Server

### 개발 환경
IDE: Intellij

Build Tool : Gradle

실행 환경: AWS Ubuntu

### 빌드 및 실행방법

#### 1. Project의 root directory에 다음과 같은 형식으로 .env 파일을 생성. 
```shell
# 신한 API의 키를 입력합니다. 이 키는 API 호출 시 필요
SHINHAN_API_KEY=your_shinhan_api_key_here

# JWT 토큰을 생성하는 데 사용될 비밀 키를 입력
JWT_SECRET_KEY=your_jwt_secret_key_here

# 적금 상품의 번호를 입력
SAVINGS_PRODUCT_CODE=your_savings_product_code_here

# Redis의 호스트명을 입력
REDIS_HOST=your_redis_host_here

# Redis의 포트를 지정 기본값은 6379
REDIS_PORT=6379

# MySQL 데이터베이스의 호스트명을 입력
MYSQL_HOST=your_mysql_host_here

# MySQL의 포트를 지정합니다. 기본값은 3306
MYSQL_PORT=3306

# 사용할 MySQL 데이터베이스의 이름을 입력
MYSQL_DATABASE=your_mysql_database_name_here

# MySQL 데이터베이스의 사용자 비밀번호를 입력
MYSQL_PASSWORD=your_mysql_user_password_here

# MySQL 데이터베이스의 루트 비밀번호를 입력
MYSQL_ROOT_PASSWORD=your_mysql_root_password_here

# MySQL 데이터베이스의 사용자 이름을 입력
MYSQL_USER=your_mysql_user_name_here

# 애플리케이션의 시간대를 설정 한국 표준시인 'Asia/Seoul'을 설정
TZ=Asia/Seoul

# 파일을 저장할 디렉토리의 경로를 입력
FILE_DIRECTORY=your_file_directory_here
```

#### 2. apt update, apt upgrade
```shell
  sudo apt update
  sudo apt upgrade
```

#### 3. docker, docker-compose 설치
```shell
  sudo apt install docker
  sudo apt install docker-compose  
```

#### 4. compose.yaml 생성
```yaml
# docker-compose 파일은 여러 개의 컨테이너를 정의하고 실행하는 데 사용
services:
  # Spring Boot 애플리케이션을 정의하는 서비스
  spring-boot-application:
    # 컨테이너의 작업 디렉토리를 설정
    working_dir: /build
    # Dockerfile을 사용하여 이미지를 빌드
    build:
      context: .
      dockerfile: Dockerfile
    # 컨테이너에 마운트할 볼륨을 정의
    volumes:
      - istory-spring:/var/lib/spring  # 데이터 저장을 위한 볼륨
      - type: bind
        source: .
        target: /build  # 애플리케이션의 소스 코드가 위치한 디렉토리
    # 컨테이너가 의존하는 다른 서비스
    depends_on:
      - istory-db
    # 컨테이너의 포트를 호스트의 포트에 매핑
    ports:
      - "8080:8080"
    # 네트워크 설정
    networks:
      - istory-server-network
  
  # MySQL 데이터베이스를 정의하는 서비스
  istory-db:
    # MySQL 이미지 사용
    image: mysql:8.0.26
    # 컨테이너 재시작 정책
    restart: always
    # MySQL 서버의 커맨드 설정
    command:
      - --lower_case_table_names=1
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    # 컨테이너 이름 설정
    container_name: ${MYSQL_HOST}
    # 컨테이너의 포트를 호스트의 포트에 매핑
    ports:
      - "3307:3306"
    # 데이터베이스 환경 변수 설정
    environment:
      MYSQL_DATABASE: ${MYSQL_DATABASE}  # 데이터베이스 이름
      MYSQL_USER: ${MYSQL_USER}  # 사용자 이름
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}  # 사용자 비밀번호
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}  # 루트 비밀번호
      TZ: ${TZ}  # 시간대 설정
    # 컨테이너에 마운트할 볼륨 정의
    volumes:
      - istory-db:/var/lib/mysql  # MySQL 데이터 저장을 위한 볼륨
    # 네트워크 설정
    networks:
      - istory-server-network
  
  # Redis를 정의하는 서비스
  redis:
    # Redis 이미지 사용
    image: redis:7.4.0
    # 컨테이너 이름 설정
    container_name: ${REDIS_HOST}
    # 컨테이너의 포트를 호스트의 포트에 매핑
    ports:
      - "6379:6379"
    # 네트워크 설정
    networks:
      - istory-server-network

# 볼륨 설정
volumes:
  istory-db:  # MySQL 데이터 저장을 위한 볼륨
  istory-spring:  # Spring 애플리케이션 데이터 저장을 위한 볼륨

# 네트워크 설정
networks:
  istory-server-network:
    driver: bridge  # 브리지 네트워크 드라이버 사용

```

#### 5. compose.yaml 실행
```shell
  sudo docker-compose up --build
```
