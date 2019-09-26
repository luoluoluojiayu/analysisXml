package com.example.analysisxml.analysis;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;

import static com.example.analysisxml.util.pathUtil.basePath;
import static com.example.analysisxml.util.pathUtil.modulePath;
import static com.example.analysisxml.util.pathUtil.resultFilePath;
import static com.example.analysisxml.util.ChineseUtil.isChinese;
import static com.example.analysisxml.util.ExcelUtil.writeExcelByList;


/**
 * 解析设置好路径的所有xml，从中获取所有的中文数据，并输出到设置好的excel文件中
 */
public class GetChineseFromXml {

    public static void main(String[] args) throws Exception {

        //获取所有需要查找中文的xml文件路径
        ArrayList<String> filePath = getNeedFindXml(modulePath, basePath);

        //存放所有中文数据的集合
        ArrayList<String> allData = new ArrayList<>();
        for (int i = 0; i < filePath.size(); i++) {
            allData.addAll(analysis(filePath.get(i),getNoNeedString()));
//            allData.addAll(analysisWithStringTag(filePath.get(i)));
        }

        //去重，别说，我懒
        List<String> data = new ArrayList<>();
        for (String string : allData) {
            //如果data中没有则添加
            if (!data.contains(string)) {
                data.add(string);
            }
        }
        System.out.println("总共中文数据：" + data.size() + "条");

        //写出到excel表
//        writeExcelBySet(set, saveDataFilePath);

        writeExcelByList(data, resultFilePath);
    }

    public static List<String> getNoNeedString(){
        List<String> data=new ArrayList<>();
        data.add("简体中文");
        data.add("繁體中文");
        return data;
    }

    /**
     * 解析xml文件，获取其中的中文数据
     *
     * @param filePath 该xml的绝对路径
     * @return
     * @throws Exception
     */
    private static ArrayList<String> analysis(String filePath,List<String> noNeedString) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(filePath);
        //得到根节点
        Element root = document.getRootElement();
        //得到所有的string标签
        List<Element> list = root.elements();
        System.out.println("总共读取到" + filePath + "文件的string数据：" + list.size() + "条");
        int isChinese = 0;
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Element element = list.get(i);
            if (isChinese(element.getText())&&!noNeedString.contains(element.getText())) {
                data.add(element.getText());
                isChinese++;
                System.out.println("在文件："+filePath+"找到中文字符串："+element.getText());
            }
        }
        System.out.println("总共读取到" + isChinese + "条中文数据");
        return data;
    }

    /**
     * 解析xml文件，获取其中的中文数据,包含string标签头的
     *
     * @param filePath 该xml的绝对路径
     * @return
     * @throws Exception
     */
    private static ArrayList<String> analysisWithStringTag(String filePath) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(filePath);
        //得到根节点
        Element root = document.getRootElement();
        //得到所有的string标签
        List<Element> list = root.elements();
        System.out.println("总共读取到" + filePath + "文件的string数据：" + list.size() + "条");
        int isChinese = 0;
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Element element = list.get(i);
            if (isChinese(element.getText())) {
                data.add("<string name="+element.attributeValue("name")+">"+element.getText()+"<string>");
//                System.out.println("在文件："+filePath+"找到中文字符串："+element.getText());
                isChinese++;
            }
        }
        System.out.println("总共读取到" + filePath + "文件中" + isChinese + "条中文数据");
        return data;
    }

    /**
     * 获取各个模块中，含有getChinese属性的xml
     *
     * @param modulePath 写模块数据的xml文件绝对路径
     * @param basePath   项目路径
     * @return
     * @throws Exception
     */
    public static ArrayList<String> getNeedFindXml(String modulePath, String basePath) throws Exception {
        ArrayList<String> list = new ArrayList<>();
        SAXReader saxReader = new SAXReader();
        //得到document
        Document document = saxReader.read(modulePath);
        //得到根节点
        Element root = document.getRootElement();
        //获取每一个module模块
        List<Element> moduleList = root.elements();

        for (int i = 0; i < moduleList.size(); i++) {
            //获取某个module节点
            Element item = moduleList.get(i);
            //获取那个module节点下面所有child节点
            List<Element> childList = item.elements();
            //遍历每一个child节点，看看是否有getChinese属性
            for (int j = 0; j < childList.size(); j++) {
                if (childList.get(j).attributeValue("getChinese") != null &&
                        childList.get(j).attributeValue("getChinese").equals("true")) {
                    list.add(basePath + childList.get(j).attributeValue("name"));
                }
            }
        }
        return list;
    }
}
