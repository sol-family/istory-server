# IStory API Server

### 개발 환경
IDE: Intellij

실행 환경: AWS Ubuntu

### 빌드 및 실행방법
Project의 root directory에 다음과 같은 형식으로 .env 파일을 생성. 
```shell
  SHINHAN_API_KEY = // 신한 API 생성
  JWT_SECRET_KEY = // JWT 토큰 생성용 Secret Key
  SAVINGS_PRODUCT_CODE= //적금 상품 번호
  REDIS_HOST= // Redis Entrypoint
  REDIS_PORT=6379
  MYSQL_HOST= // DB Hostname
  MYSQL_PORT=3306
  MYSQL_DATABASE= // DB name
  MYSQL_PASSWORD= // DB userPassword
  MYSQL_ROOT_PASSWORD= // DB rootPassword
  MYSQL_USER= // DB userName
  TZ=Asia/Seoul
  FILE_DIRECTORY= // 파일 저장할 디렉토리 명시
```

apt update, apt upgrade
```shell
  sudo apt update
  sudo apt upgrade
```

docker, docker-compose 설치
```shell
  sudo apt install docker
  sudo apt install docker-compose  
```

로컬에서 실행시 compose.yaml 파일 수정
```yaml
//compose.yaml
services:
  spring-boot-application:
    working_dir: /build
    build:
      context: .
      dockerfile: Dockerfile
    volumes:
      - istory-spring:/var/lib/spring
      - type: bind
        source: .
        target: /build
    depends_on:
      - istory-db
    ports:
      - "8080:8080"
    networks:
      - istory-server-network
  istory-db:
    image: mysql:8.0.26
    restart: always
    command:
      - --lower_case_table_names=1
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    container_name: ${MYSQL_HOST}
    ports:
      - "3307:3306"
    environment:
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      TZ: ${TZ}
    volumes:
      - istory-db:/var/lib/mysql
    networks:
      - istory-server-network
  redis: # redis container 추가로 생성
    image: redis:7.4.0
    container_name: ${REDIS_HOST}
    ports:
      - "6379:6379"
    networks:
      - istory-server-network
volumes:
  istory-db:
  istory-spring:

networks:
  istory-server-network:
    driver: bridge
```

compose.yaml 실행
```shell
  sudo docker-compose up --build
```
