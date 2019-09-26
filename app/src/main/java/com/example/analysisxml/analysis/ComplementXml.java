package com.example.analysisxml.analysis;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static com.example.analysisxml.util.pathUtil.basePath;
import static com.example.analysisxml.util.pathUtil.modulePath;
import static com.example.analysisxml.util.XmlUtil.saveXml;

/**
 * 补全xml，将父xml中的所有数据都补全到子xml中，
 */
public class ComplementXml {


    public static void main(String[] args) throws Exception {
        //获取各个模块的值
        HashMap<String, ArrayList<String>> moduleList = getModuleList(modulePath, basePath);
        Set<String> keys = moduleList.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
//            anotherCompare(key, moduleList.get(key));
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

        //遍历父xml每一个元素，跟每一个子xml比对，构建出一份新的子xml
        Iterator<String> iterator = parentKeys.iterator();
        for (int i = 0; i < parentHashMap.size(); i++) {
            String key = iterator.next();
            for (int j = 0; j < childElementsHashList.size(); j++) {
                Element e = DocumentHelper.createElement("string");
                Attribute attribute = DocumentHelper.createAttribute(e, "name", key);
                e.add(attribute);
                if (childElementsHashList.get(j).get(key) == null) {
                    //假如子xml没有这个数据了，那么用子xml的值
                    e.setText(parentHashMap.get(key));
                    childElementsList.get(j).add(i , e);
                }
            }
        }

        for (int i = 0; i < childDocumentList.size(); i++) {
            saveXml(childPathList.get(i), childDocumentList.get(i));
        }
    }

    /**
     * 另一种补全
     * 重新构建一个新的xml数据，完全依照父xml的key创建的
     * 对比逻辑是，遍历父亲的key，假如子有这个key的数据，那就用子的，假如没有，就用父亲的，
     */
    public static void anotherCompare(String parentPath, List<String> childPathList) throws Exception {
        SAXReader saxReader = new SAXReader();

        //父亲的初始化
        Document parentDocument = saxReader.read(parentPath);                                     //存放document，后面存xml要用到
        List<Element> parentElements = parentDocument.getRootElement().elements();
        LinkedHashMap<String, String> parentHashMap = turnHashMapByElementsList(parentElements);
        Set<String> parentKeys = parentHashMap.keySet();

        //子xml元素的初始化
        List<Document> childDocumentList = new ArrayList<>();
        List<List<Element>> childElementsList = new ArrayList<>();
        List<LinkedHashMap<String, String>> childElementsHashList = new ArrayList<>();
        List<List<Element>> newChildElementList = new ArrayList<>();
        for (int i = 0; i < childPathList.size(); i++) {
            Document childDocument = saxReader.read(childPathList.get(i));
            childDocumentList.add(childDocument);
            List<Element> childElements = childDocument.getRootElement().elements();
            childElementsList.add(childElements);
            childElementsHashList.add(turnHashMapByElementsList(childElements));
            newChildElementList.add(new ArrayList<Element>());
        }


        //遍历父xml每一个元素，跟每一个子xml比对，构建出一份新的子xml
        Iterator<String> iterator = parentKeys.iterator();
        for (int i = 0; i < parentHashMap.size(); i++) {
            String key = iterator.next();
            for (int j = 0; j < childElementsHashList.size(); j++) {
                Element e = DocumentHelper.createElement("string");
                Attribute attribute = DocumentHelper.createAttribute(e, "name", key);
                e.add(attribute);
                if (childElementsHashList.get(j).get(key) == null) {
                    //假如子xml没有这个数据了，那么用父亲xml的值
                    e.setText(parentHashMap.get(key));
                } else {
                    //假如子xml有这个数据，那么用子xml的值
                    e.setText(childElementsHashList.get(j).get(key));
                }
                newChildElementList.get(j).add(e);
            }
        }

        //存储每一个子xml数据到原来文件中
        for (int i = 0; i < newChildElementList.size(); i++) {
            List<Element> item = childElementsList.get(i);
            //因为list跟xml的document挂钩了，只能在这个list操作，所以先清除，再把得到的新数据加进去
            item.clear();
            item.addAll(newChildElementList.get(i));
            //回写xml
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
