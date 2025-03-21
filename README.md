

`docker run --name local-postgres -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mypass -e POSTGRES_DB=mydb -p 5432:5432 -d postgres
`


docker network create  postgres-network
docker run --name my-postgres --network postgres-network -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mypass -e POSTGRES_DB=mydb -p 5432:5432 -d postgres
docker run --name pgadmin --network postgres-network -p 5050:80 -e PGADMIN_DEFAULT_EMAIL=admin@example.com -e PGADMIN_DEFAULT_PASSWORD=admin123 -d dpage/pgadmin4


host.docker.internal






















