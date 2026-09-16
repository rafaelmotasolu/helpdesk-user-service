### Para subir o container docker da base para testes

> **Nota:** A porta utilizada pelo container docker é diferente da padrão do postgres para evitar conflito com o postgres instalado localmente (Se houver!)

docker run --name helpdesk-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5433:5432 -d postgres:16-alpine

### Para criar a base de dados

docker exec -it helpdesk-postgres psql -U postgres -c "CREATE DATABASE user_db;"

