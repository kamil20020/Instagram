# Instagram backend 

Liquibase generate changelog: 
```console
mvn liquibase:generateChangeLog -Dliquibase.outputChangeLogFile=src/main/resources/db/changelog/generated.yaml
```

Liquibase generate diff changelog:
```console
mvn liquibase:diff
```

Docker login:
```console
docker login
```

Build image:
```console
docker build -t instagram-backend .
```

Tag image:
```console
docker tag instagram-backend kamil20/instagram-backend
```

Push image:
```console
docker push kamil20/instagram-backend
```
