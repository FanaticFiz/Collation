### Консольная тула для тестирования линков на регламенты

Запуск с настройками по умолчанию: `java -jar testLinks.jar`

#### Переопределение настроек

##### Указать новый файл  
`java -jar testLinks.jar --spring.config.location=classpath:/another-location/some-name.yml`

##### Заменить одно/несколько из свойств, например:  
укажем иную базу данных: database_28  
укажем иной префикс схемы: workspace_271   
и название колонки: link  

`java -jar testLinks.jar --spring.datasource.url=jdbc:postgresql://127.0.0.1:5434/database_28 testLinks --crg-options.schemaPrefix=workspace_271 --crg-options.reglamentColumnName=link`
