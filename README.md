# Instagram backend 

Liquibase generate changelog: 
```console
mvn liquibase:generateChangeLog -Dliquibase.outputChangeLogFile=src/main/resources/db/changelog/generated.yaml
```

Liquibase generate diff changelog:
```console
mvn liquibase:diff
```