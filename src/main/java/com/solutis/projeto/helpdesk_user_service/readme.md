### Para subir o container docker da base para testes

> **Nota:** Usamos a porta mapeada no host como `5433` (`-p 5433:5432`) para evitar conflito com instâncias locais do PostgreSQL rodando na porta padrão `5432` do Windows.

```bash
docker run --name helpdesk-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5433:5432 -d postgres:16-alpine
```

### Para criar a base de dados

```bash
docker exec -it helpdesk-postgres psql -U postgres -c "CREATE DATABASE user_db;"
```

