package mhw.uils;

import mhw.uils.config.ExcelConfig;
import mhw.uils.parser.ExcelReader;
import mhw.uils.parser.ExcelXmlParser;

import java.util.List;
import java.util.Map;

/**
 * @author hongweima
 */
public class Main {
    public static void main(String[] args) {
        String fileName = "/Users/hongweima/IdeaProjects/project/easyexcel/easyexcel/src/main/resources/demo.xlsx";
        ExcelConfig xmlConfig = ExcelXmlParser.getConfig("/Users/hongweima/IdeaProjects/project/easyexcel/easyexcel/src/main/resources/xml/demo.xml");
        xmlConfig.setFileName(fileName);
        List<Map<String, String>> mapList = ExcelReader.read(xmlConfig);
        System.out.println(mapList);
    }
}