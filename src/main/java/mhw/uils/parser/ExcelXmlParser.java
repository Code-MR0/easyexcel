package mhw.uils.parser;

import mhw.uils.config.ColumRule;
import mhw.uils.config.ColumRuleDetail;
import mhw.uils.config.ColumnConfig;
import mhw.uils.config.ExcelConfig;
import mhw.uils.constant.ExcelConstant;
import mhw.uils.exception.XmlParserException;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * excel xml解析器
 *
 * @author hongweima
 */
public class ExcelXmlParser {

    // 读取配置文件并把配置全部解析到ExcelConfig里面

    public static ExcelConfig getConfig(String fileFullName) {
        if (fileFullName == null || "".equals(fileFullName)) {
            return null;
        }
        ExcelConfig excelConfig = new ExcelConfig();
        File file = new File(fileFullName);
        if (file.exists()) {
            SAXReader saxReader = new SAXReader();
            // 解析excel配置
            Element root;
            try {
                root = saxReader.read(file).getRootElement();
            } catch (DocumentException e) {
                throw new XmlParserException(e.getMessage());
            }
            excelConfig.setDataStartLine(Integer.parseInt(root.attributeValue("data-start-line")));
            excelConfig.setMaxLine(Integer.parseInt(root.attributeValue("max-line")));
            excelConfig.setFileName(fileFullName);
            excelConfig.setFileParser(root.attributeValue("file-parser"));

            // 解析列配置
            List<Element> fieldList = root.elements("field");
            ArrayList<ColumnConfig> columnConfigs = new ArrayList<>(fieldList.size());
            fieldList.forEach(e -> columnConfigs.add(getColumnConfig(e)));
            excelConfig.setColumnConfigs(columnConfigs);
        } else {
            throw new XmlParserException("file is not exists");
        }
        return excelConfig;
    }

    private static ColumnConfig getColumnConfig(Element element) {
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setId(element.attributeValue("id"));
        columnConfig.setChName(element.attributeValue("chName"));
        columnConfig.setEnName(element.attributeValue("enName"));

        // 解析列配置规则
        if (element.element(ExcelConstant.RULES) == null) {
            return columnConfig;
        }
        Element rules = element.element("rules");
        List<Element> ruleList = rules.elements("rule");
        ArrayList<ColumRule> columRules = new ArrayList<>(ruleList.size());
        ruleList.forEach(e -> columRules.add(getColumRule(e)));
        columnConfig.setColumRules(columRules);

        return columnConfig;
    }

    private static ColumRule getColumRule(Element element) {
        ColumRule columRule = new ColumRule();
        columRule.setType(element.attributeValue("type"));
        columRule.setErrorMsg(element.attributeValue("error-msg"));

        // 解析列规则详情
        List<Element> paramList = element.elements("param");
        ArrayList<ColumRuleDetail> ruleDetails = new ArrayList<>(paramList.size());
        paramList.forEach(e -> ruleDetails.add(getColumRuleDetails(e)));
        columRule.setRuleDetails(ruleDetails);
        return columRule;
    }

    private static ColumRuleDetail getColumRuleDetails(Element element) {
        ColumRuleDetail ruleDetail = new ColumRuleDetail();
        ruleDetail.setName(element.attributeValue("name"));
        ruleDetail.setValue(element.attributeValue("value"));
        return ruleDetail;
    }

    public static void main(String[] args) {
        ExcelConfig xmlConfig = getConfig("/Users/hongweima/IdeaProjects/project/easyexcel/easyexcel/src/main/resources/xml/demo.xml");
        System.out.println(xmlConfig.toString());
    }
}
