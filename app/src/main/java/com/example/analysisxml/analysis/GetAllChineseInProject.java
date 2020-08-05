package com.example.analysisxml.analysis;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;

import static com.example.analysisxml.analysis.GetChineseFromXml.analysis;
import static com.example.analysisxml.analysis.GetChineseFromXml.getNoNeedString;
import static com.example.analysisxml.util.ExcelUtil.writeExcelByList;
import static com.example.analysisxml.util.pathUtil.basePath;
import static com.example.analysisxml.util.pathUtil.modulePath;
import static com.example.analysisxml.util.pathUtil.resultFilePath;

/**
 * @author jiayu
 * @since 2020/3/20
 * 获取项目中所有中文字符串，不管有没有翻译过的
 */
public class GetAllChineseInProject {
    public static void main(String[] args) throws Exception {

        //获取所有需要查找中文的xml文件路径
        ArrayList<String> filePath = getNeedFindXml(modulePath, basePath);

        //存放所有中文数据的集合
        ArrayList<String> allData = new ArrayList<>();
        for (int i = 0; i < filePath.size(); i++) {
            allData.addAll(analysis(filePath.get(i), getNoNeedString()));
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

        writeExcelByList(data, resultFilePath);
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
            list.add(basePath + item.attributeValue("name"));
        }
        return list;
    }
}
