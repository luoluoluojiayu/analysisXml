package com.example.analysisxml.analysis;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.analysisxml.util.XmlUtil.saveXml;
import static com.example.analysisxml.util.pathUtil.basePath;
import static com.example.analysisxml.util.pathUtil.comparePath;
import static com.example.analysisxml.util.pathUtil.modulePath;
import static com.example.analysisxml.util.pathUtil.newImProjectPath;

/**
 * 这个类是为了重构项目而做的，从原来的项目里面拷过来的string文件，可能顺序会被打乱，还得从原来项目一个个对应翻译弄过来比较麻烦
 * 所以现在，只要管理好中文的string文件，这个类会用中文string文件里面的值，去原来项目里面找对应翻译，自动帮你弄好对应翻译
 */

public class findSameKeyXml {
    public static void main(String[] args) throws Exception {
        //比较中文string，并且补全其他string文件
        getCompareXmlValues(comparePath, newImProjectPath, basePath);
    }

    /**
     * 比较中文string，并且补全其他string文件
     * @param comparePath
     * @param basePath
     * @param beCompareXmlBasePath
     */
    public static void getCompareXmlValues(String comparePath, String basePath, String beCompareXmlBasePath) {
        try {
            SAXReader saxReader = new SAXReader();
            //得到document
            Document document = saxReader.read(comparePath);
            //得到根节点
            Element root = document.getRootElement();
            //获取每一个module模块
            List<Element> moduleList = root.elements();
            for (int i = 0; i < moduleList.size(); i++) {
                //一个module
                Element item = moduleList.get(i);
                //一个module中所有child标签
                List<Element> childList = item.elements();
                for (int j = 0; j < childList.size(); j++) {
                    //获取这个child标签是否有设置needTranslate属性
                    String key = childList.get(j).attributeValue("needComplementTo");
                    List<String> languageXmlList = findNeedLanguageXmlPath(modulePath, beCompareXmlBasePath, key);
                    List<String> chinaXmlList = getChinaXmlList(modulePath, beCompareXmlBasePath);
                    for (int k = 0; k < languageXmlList.size(); k++) {
                        complementTranslate(basePath + item.attributeValue("name"), basePath + childList.get(j).attributeValue("name"), languageXmlList, chinaXmlList);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("获取要比对的compareXml失败，原因是：" + e);
        }
    }

    /**
     * 根据中文string的key去原来项目找，但是可能新项目会改变key，所以最好还是不用了
     * @param chinaXmlPath
     * @param needComplementXmlPath
     * @param allXmlList
     */
    public static void complementTranslate(String chinaXmlPath, String needComplementXmlPath, List<String> allXmlList) {
        try {
            SAXReader saxReader = new SAXReader();
            //得到document
            Document document = saxReader.read(chinaXmlPath);
            //得到根节点
            Element root = document.getRootElement();
            //获取每一个strings的值
            List<Element> moduleList = root.elements();
            HashMap<String, String> allString = getAllString(allXmlList);
            List<Element> newStringList = new ArrayList<>();
            for (int i = 0; i < moduleList.size(); i++) {
                String key = moduleList.get(i).attributeValue("name");
                if (allString.get(key) != null) {
                    Element e = DocumentHelper.createElement("string");
                    Attribute attribute = DocumentHelper.createAttribute(e, "name", key);
                    e.add(attribute);
                    e.setText(allString.get(key));
                    newStringList.add(e);
                } else {
                    System.out.println("找不到的key：" + key);
                }
            }
            moduleList.clear();
            moduleList.addAll(newStringList);
            saveXml(needComplementXmlPath, document);
        } catch (Exception e) {
            System.out.println("补全翻译失败，原因是：" + e);
        }
    }


    /**
     * 根据中文值去原来项目找对应翻译数据
     * @param chinaXmlPath
     * @param needComplementXmlPath
     * @param allXmlList
     * @param chinaXmlList
     */
    public static void complementTranslate(String chinaXmlPath, String needComplementXmlPath, List<String> allXmlList, List<String> chinaXmlList) {
        try {
            SAXReader saxReader = new SAXReader();
            //得到document
            Document document = saxReader.read(chinaXmlPath);
            //得到根节点
            Element root = document.getRootElement();
            //获取每一个strings的值
            List<Element> moduleList = root.elements();
            HashMap<String, String> allString = getAllString(allXmlList);
            HashMap<String, String> allChinaString = getAllChinaString(chinaXmlList);
            List<Element> newStringList = new ArrayList<>();
            for (int i = 0; i < moduleList.size(); i++) {
                String value = moduleList.get(i).getText();
                if (allChinaString.get(value) != null) {
                    String key=allChinaString.get(value);
                    if(allString.get(key)!=null){
                        Element e = DocumentHelper.createElement("string");
                        Attribute attribute = DocumentHelper.createAttribute(e, "name", moduleList.get(i).attributeValue("name"));
                        e.add(attribute);
                        e.setText(allString.get(key));
                        newStringList.add(e);
                    }
                } else {
                    System.out.println("找不到的key：" + value);
                    Element e = DocumentHelper.createElement("string");
                    Attribute attribute = DocumentHelper.createAttribute(e, "name", moduleList.get(i).attributeValue("name"));
                    e.add(attribute);
                    e.setText(moduleList.get(i).getText());
                    newStringList.add(e);
                }
            }
            moduleList.clear();
            moduleList.addAll(newStringList);
            saveXml(needComplementXmlPath, document);
        } catch (Exception e) {
            System.out.println("补全翻译失败，原因是：" + e);
        }
    }


    /**
     * 获取某种语言全部string数据
     * @param allXmlList
     * @return
     */
    public static HashMap<String, String> getAllString(List<String> allXmlList) {
        HashMap<String, String> allString = new HashMap<>();
        SAXReader saxReader = new SAXReader();
        try {
            for (int i = 0; i < allXmlList.size(); i++) {
                Document document = saxReader.read(allXmlList.get(i));
                //得到根节点
                Element root = document.getRootElement();
                //获取每一个strings的值
                List<Element> stringList = root.elements();
                for (int j = 0; j < stringList.size(); j++) {
                    allString.put(stringList.get(j).attributeValue("name"), stringList.get(j).getText());
                }
            }
        } catch (Exception e) {
            System.out.println("获取所有的xml数据失败，原因是：" + e);
        }
        return allString;
    }

    /**
     * 获取原来项目中所有的中文string，
     * @param allXmlList
     * @return
     */
    public static HashMap<String, String> getAllChinaString(List<String> allXmlList) {
        HashMap<String, String> allString = new HashMap<>();
        SAXReader saxReader = new SAXReader();
        try {
            for (int i = 0; i < allXmlList.size(); i++) {
                Document document = saxReader.read(allXmlList.get(i));
                //得到根节点
                Element root = document.getRootElement();
                //获取每一个strings的值
                List<Element> stringList = root.elements();
                for (int j = 0; j < stringList.size(); j++) {
                    allString.put(stringList.get(j).getText(), stringList.get(j).attributeValue("name"));
                }
            }
        } catch (Exception e) {
            System.out.println("获取所有的xml数据失败，原因是：" + e);
        }
        return allString;
    }

    /**
     * 在某个项目查找所有配置的特定语言的xml路径
     *
     * @param modulePath  项目的模块地址配置文件，
     * @param basePath    项目地址
     * @param languageKey 需要查找的语言
     * @return 返回所有特定语言的xml地址集合
     */
    public static List<String> findNeedLanguageXmlPath(String modulePath, String basePath, String languageKey) {
        List<String> xmlPathList = new ArrayList<>();
        try {
            SAXReader saxReader = new SAXReader();
            //得到document
            Document document = saxReader.read(modulePath);
            //得到根节点
            Element root = document.getRootElement();
            //获取每一个module模块
            List<Element> moduleList = root.elements();
            for (int i = 0; i < moduleList.size(); i++) {
                //一个module
                Element item = moduleList.get(i);
                //一个module中所有child标签
                List<Element> childList = item.elements();
                for (int j = 0; j < childList.size(); j++) {
                    //获取这个child标签是否有设置needTranslate属性
                    String key = childList.get(j).attributeValue("needTranslate");
                    if (key != null && key.equals(languageKey)) {
                        xmlPathList.add(basePath + childList.get(j).attributeValue("name"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("获取要比对的语言路径失败，原因是：" + e);
        }
        return xmlPathList;
    }

    /**
     * 获取所有中文string.xml文件的绝对路径
     * @param modulePath
     * @param basePath
     * @return
     */
    public static List<String> getChinaXmlList(String modulePath, String basePath) {
        List<String> xmlPathList = new ArrayList<>();
        try {
            SAXReader saxReader = new SAXReader();
            //得到document
            Document document = saxReader.read(modulePath);
            //得到根节点
            Element root = document.getRootElement();
            //获取每一个module模块
            List<Element> moduleList = root.elements();
            for (int i = 0; i < moduleList.size(); i++) {
                //一个module
                Element item = moduleList.get(i);
                //一个module中所有child标签
                xmlPathList.add(basePath + item.attributeValue("name"));
            }
        } catch (Exception e) {
            System.out.println("获取被比对项目所有中文数据失败，原因是：" + e);
        }
        return xmlPathList;
    }

}
