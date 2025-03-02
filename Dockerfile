# Java 11이 설치된 경량 이미지 사용
FROM openjdk:11-jre-slim

# 임시 파일 생성을 위한 디렉토리 설정 (필요에 따라 사용)
VOLUME /tmp

# 빌드 시 jar 파일의 경로 지정 (default 값)
ARG JAR_FILE=build/libs/project_shoppingmall-v1.1.jar

# 로컬의 jar 파일을 이미지 내 /app.jar 로 복사
COPY ${JAR_FILE} app.jar

# 이미지 파일들을 컨테이너의 /images 폴더에 복사
COPY src/main/resources/static/images/item/sample_detail/ /images/item/sample_detail/
COPY src/main/resources/static/images/item/sample_img/ /images/item/sample_img/
COPY src/main/resources/static/images/item/sample_thumb/ /images/item/sample_thumb/

# 컨테이너 내에서 애플리케이션이 사용하는 포트를 EXPOSE
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app.jar"]