package mhw.uils.parser;

import mhw.uils.config.ColumnConfig;
import mhw.uils.config.ColumnRule;
import mhw.uils.config.ColumnRuleDetail;
import mhw.uils.config.ExcelConfig;
import mhw.uils.constant.ExcelConstant;
import mhw.uils.exception.ExcelReadException;
import mhw.uils.exception.XmlParserException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel读取
 *
 * @author hongweima
 */
public class ExcelReader {
    public static List<Map<String, String>> read(ExcelConfig config) {
        // 获取文件名
        String fileName = config.getFileName();
        // 获取workbook
        Workbook workbook = getWorkbook(fileName);
        // 获取sheet页
        Sheet sheet = workbook.getSheetAt(config.getSheetNumber());

        List<Map<String, String>> data = new ArrayList<>();
        int maxLine = config.getMaxLine();
        int startLine = config.getDataStartLine();
        int rowNum = sheet.getLastRowNum();
        short columnSize = sheet.getRow(startLine).getLastCellNum();
        List<ColumnConfig> columnConfigs = config.getColumnConfigs();

        // 检查文件条数
        if (rowNum > maxLine) {
            throw new ExcelReadException("The number of excel files exceeds " + maxLine);
        }

        // 根据配置逐行读取
        for (int i = startLine; i <= rowNum; i++) {
            HashMap<String, String> rowData = new HashMap<>(columnSize);
            Row row = sheet.getRow(i);
            for (int j = 0; j < columnSize; j++) {
                Cell cell = row.getCell(j);
                ColumnConfig columnConfig = columnConfigs.get(j);
                List<ColumnRule> rules = columnConfig.getColumnRules();
                String key = columnConfig.getEnName();
                String value = getCellValue(cell, rules);
                rowData.put(key, value);
            }
            data.add(rowData);
        }
        return data;

    }

    private static String getCellValue(Cell cell, List<ColumnRule> rules) {
        CellType cellType = cell.getCellType();
        String value;

        // 获取cell值
        switch (cellType) {
            case BLANK:
                value = null;
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                boolean b = cell.getBooleanCellValue();
                value = b ? "1" : "0";
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    value = df.format(date);
                } else {
                    double cellValue = cell.getNumericCellValue();
                    value = String.valueOf(cellValue);
                }
                break;
            default:
                throw new ExcelReadException("excel cell format error! only BLANK STRING BOOLEAN NUMERIC are allowed!");
        }

        // cell值根据配置转化
        if (rules == null) {
            return value;
        }
        for (ColumnRule rule : rules) {
            String type = rule.getType();
            List<ColumnRuleDetail> ruleDetails = rule.getRuleDetails();
            String errorMsg = rule.getErrorMsg();
            switch (type) {
                case "subString":
                    int beginIndex = Integer.parseInt(ruleDetails.get(0).getValue());
                    int endIndex = Integer.parseInt(ruleDetails.get(1).getValue());
                    value = value.substring(beginIndex, endIndex);
                    break;
                case "length":
                    int length = Integer.parseInt(ruleDetails.get(0).getValue());
                    if (value.length() != length) {
                        throw new ExcelReadException(errorMsg);
                    }
                    break;
                case "range":
                    int minLength = Integer.parseInt(ruleDetails.get(0).getValue());
                    int maxLength = Integer.parseInt(ruleDetails.get(1).getValue());
                    if (value.length() > maxLength || value.length() < minLength) {
                        throw new ExcelReadException(errorMsg);
                    }
                    break;
                default:
                    throw new XmlParserException("unknown parsing rule:" + type);
            }
        }
        return value;
    }

    private static Workbook getWorkbook(String fileFullName) {
        if (!fileFullName.endsWith(ExcelConstant.EXCEL_XLS) && !fileFullName.endsWith(ExcelConstant.EXCEL_XLSX)) {
            throw new ExcelReadException("excel format error! only xls and xlsx formats are allowed!");
        }
        File file = new File(fileFullName);
        FileInputStream fis;
        Workbook workbook;
        try {
            fis = new FileInputStream(file);
            if (file.getName().endsWith(ExcelConstant.EXCEL_XLS)) {
                workbook = new HSSFWorkbook(fis);
            } else {
                workbook = new XSSFWorkbook(fis);
            }
        } catch (Exception e) {
            throw new ExcelReadException(e.getMessage());
        }
        return workbook;
    }

    public static void main(String[] args) {
        String fileName = "/Users/hongweima/IdeaProjects/project/easyexcel/easyexcel/src/main/resources/demo.xlsx";
        ExcelConfig xmlConfig = ExcelXmlParser.getConfig("/Users/hongweima/IdeaProjects/project/easyexcel/easyexcel/src/main/resources/xml/demo.xml");
        xmlConfig.setFileName(fileName);
        List<Map<String, String>> mapList = read(xmlConfig);
        System.out.println(mapList);
    }
}
