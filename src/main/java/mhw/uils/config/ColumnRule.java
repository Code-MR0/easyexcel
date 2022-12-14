package mhw.uils.config;

import lombok.Data;

import java.util.List;

/**
 * excel列规则配置
 *
 * @author hongweima
 */
@Data
public class ColumnRule {
    String type;
    String errorMsg;
    List<ColumnRuleDetail> ruleDetails;
}
