package ru.crg.reglaments;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TargetResource {

    private String schemaName;
    private String tableName;

}
