### Консольная тула для collate

Собрать `mvn package`

Запустить с настройками по умолчанию: `java -jar collate.jar`

#### Переопределение настроек

##### Указать новый файл  
`java -jar collate.jar --spring.config.location=classpath:/another-location/some-name.yml`

##### Заменить одно/несколько из свойств, например:  
укажем иной префикс схемы: workspace_271   
и collation: en_US  

`java -jar collate.jar --spring.datasource.url=jdbc:postgresql://127.0.0.1:5434/database_28 --crg-options.collation=en_US`
