package com.example.analysisxml.analysis;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static com.example.analysisxml.util.XmlUtil.saveXml;
import static com.example.analysisxml.util.pathUtil.basePath;
import static com.example.analysisxml.util.pathUtil.modulePath;

/**
 * 同步xml，以中文string为标准，将其他string中，只要是中文string文件中没有的字段统统删除
 */
public class ComplementXmlByDelete {


    public static void main(String[] args) throws Exception {
        //获取各个模块的值
        HashMap<String, ArrayList<String>> moduleList = getModuleList(modulePath, basePath);
        Set<String> keys = moduleList.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            compareXml(key, moduleList.get(key));
        }
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


    /**
     * 比较补全
     *
     * @param parentPath
     * @param childPathList
     * @throws Exception
     */
    public static void compareXml(String parentPath, List<String> childPathList) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document parentDocument = saxReader.read(parentPath);
        List<Element> parentElements = parentDocument.getRootElement().elements();
        LinkedHashMap<String, String> parentHashMap = turnHashMapByElementsList(parentElements);
        Set<String> parentKeys = parentHashMap.keySet();
        List<Document> childDocumentList = new ArrayList<>();
        List<List<Element>> childElementsList = new ArrayList<>();
        List<LinkedHashMap<String, String>> childElementsHashList = new ArrayList<>();
        for (int i = 0; i < childPathList.size(); i++) {
            Document childDocument = saxReader.read(childPathList.get(i));
            childDocumentList.add(childDocument);
            List<Element> childElements = childDocument.getRootElement().elements();
            childElementsList.add(childElements);
            childElementsHashList.add(turnHashMapByElementsList(childElements));
        }


        for (int j = 0; j < childElementsList.size(); j++) {
            List<Element> list = childElementsList.get(j);
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i).attributeValue("name");
                if (parentHashMap.get(key) == null) {
                    list.remove(i);
                }
            }
            list.size();
        }
        childElementsList.size();

        for (int i = 0; i < childDocumentList.size(); i++) {
            saveXml(childPathList.get(i), childDocumentList.get(i));
        }
    }


    /**
     * 把一个list转成LinkedHashMap
     *
     * @param list
     * @return
     */
    private static LinkedHashMap<String, String> turnHashMapByElementsList(List<Element> list) {
        LinkedHashMap<String, String> item = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            item.put(list.get(i).attributeValue("name"), list.get(i).getText());
        }
        return item;
    }

}
