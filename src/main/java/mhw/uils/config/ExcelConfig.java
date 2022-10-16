package mhw.uils.config;

import lombok.Data;

import java.util.List;

/**
 * Excel配置
 *
 * @author hongweima
 */
@Data
public class ExcelConfig {
    String fileName;
    String fileParser;
    String fileTemplate;
    int dataStartLine;
    int maxLine;
    List<ColumnConfig> columnConfigs;
}
