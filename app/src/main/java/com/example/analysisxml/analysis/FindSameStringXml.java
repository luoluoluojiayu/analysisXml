package com.example.analysisxml.analysis;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.analysisxml.util.ExcelUtil.writeExcelByList;
import static com.example.analysisxml.util.pathUtil.basePath;
import static com.example.analysisxml.util.pathUtil.modulePath;
import static com.example.analysisxml.util.pathUtil.resultFilePath;

/**
 * @author jiayu
 * @since 2020/3/12
 * 找到中文string中是否有相同的string
 */
public class FindSameStringXml {
    public static void main(String[] args) throws Exception {
        //获取各个模块的值
        HashMap<String, ArrayList<String>> moduleList = getModuleList(modulePath, basePath);
        Set<String> keys = moduleList.keySet();
        Iterator<String> iterator = keys.iterator();
        ArrayList<String> data = new ArrayList<>();
        while (iterator.hasNext()) {
            String key = iterator.next();
//            anotherCompare(key, moduleList.get(key));
            data.addAll(CheckSameString(key, moduleList.get(key)));
        }
        writeExcelByList(data, resultFilePath);
    }


    public static ArrayList<String> CheckSameString(String parentPath, List<String> childPathList) throws DocumentException {
        ArrayList<String> resultList = new ArrayList<>();
        resultList.add(parentPath);
        SAXReader saxReader = new SAXReader();
        Document parentDocument = saxReader.read(parentPath);
        List<Element> parentElements = parentDocument.getRootElement().elements();
        for (int i = 0; i < parentElements.size(); i++) {
            for (int j = 0; j < parentElements.size(); j++) {
                if (parentElements.get(i).getText().equals(parentElements.get(j).getText()) &&
                        !parentElements.get(i).attributeValue("name").equals(parentElements.get(j).attributeValue("name"))) {
                    if (!hasSameItem(parentElements.get(i).getText(), resultList)) {
                        System.out.println(parentElements.get(i).getText());
                        resultList.add(parentElements.get(i).getText());
                    }
                    if (!hasSameItem(parentElements.get(i).attributeValue("name"), resultList)) {
                        System.out.println(parentElements.get(i).attributeValue("name"));
                        resultList.add(parentElements.get(i).attributeValue("name"));
                    }
                    if (!hasSameItem(parentElements.get(j).attributeValue("name"), resultList)) {
                        System.out.println(parentElements.get(j).attributeValue("name"));
                        resultList.add(parentElements.get(j).attributeValue("name"));
                    }
                }
            }
        }
        return resultList;
    }

    private static boolean hasSameItem(String key, ArrayList<String> list) {
        boolean hasSameKey = false;
        for (int k = 0; k < list.size(); k++) {
            if (key.equals(list.get(k))) {
                hasSameKey = true;
            }
        }
        return hasSameKey;
    }

    /**
     * 获取各个模块的值
     *
     * @param modulePath 写模块数据的xml文件绝对路径
     * @param basePath   项目路径
     * @return
     * @throws Exception
     */
    public static HashMap<String, ArrayList<String>> getModuleList(String modulePath, String basePath) throws Exception {
        HashMap<String, ArrayList<String>> list = new HashMap<>();
        SAXReader saxReader = new SAXReader();
        //得到document
        Document document = saxReader.read(modulePath);
        //得到根节点
        Element root = document.getRootElement();

        List<Element> moduleList = root.elements();
        for (int i = 0; i < moduleList.size(); i++) {
            ArrayList<String> child = new ArrayList<>();
            Element item = moduleList.get(i);
            if (item.attributeValue("needComplement") != null && item.attributeValue("needComplement").equals("true")) {
                List<Element> childList = item.elements();
                for (int j = 0; j < childList.size(); j++) {
                    child.add(basePath + childList.get(j).attributeValue("name"));
                }
                list.put(basePath + moduleList.get(i).attributeValue("name"), child);
            }
        }
        return list;
    }

}
