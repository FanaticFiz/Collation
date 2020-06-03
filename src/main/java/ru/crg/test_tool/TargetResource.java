package ru.crg.test_tool;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TargetResource {

    private String schemaName;
    private String tableName;
    private String columnName;
    private String charLength;

}
