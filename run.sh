
# Run databases
#     MySQL (port 3037)
#     Cassandra (port 9043)
#     Cassandra (port 9044)

docker compose up -d

# Install Java Classes
mvn clean install

# Run Eureka Server (port 8761)
nohup java -jar eureka-server/target/eureka-server-1.0-SNAPSHOT.jar &

# Run API Gateway (port 8080)
nohup java -jar apigw/target/apigw-1.0-SNAPSHOT.jar &

sleep 30

# Wait until all databases are up and running
# Run Token service (port 8084)
nohup java -jar token/target/token-1.0-SNAPSHOT.jar &

# Run Long URL service (port 8082)
nohup java -jar longurl/target/longurl-1.0-SNAPSHOT.jar &


sleep 10

# Wait until Token service and Long URL service are up and running
# Run Short URL service (port 8083)
nohup java -jar shorturl/target/shorturl-1.0-SNAPSHOT.jar &
