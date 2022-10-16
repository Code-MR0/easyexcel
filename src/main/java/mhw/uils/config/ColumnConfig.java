package mhw.uils.config;

import lombok.Data;

import java.util.List;

/**
 * excel列配置
 *
 * @author hongweima
 */
@Data
public class ColumnConfig {
    String id;
    String chName;
    String enName;
    List<ColumRule> columRules;
}
