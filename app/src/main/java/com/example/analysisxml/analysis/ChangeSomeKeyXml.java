package com.example.analysisxml.analysis;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.List;

import static com.example.analysisxml.util.XmlUtil.saveXml;
import static com.example.analysisxml.util.pathUtil.basePath;
import static com.example.analysisxml.util.pathUtil.modulePath;


/**
 * 给指定的string item 全部替换成指定的值
 */
public class ChangeSomeKeyXml {
    public static void main(String[] args) {
        //获取存放翻译数据的excel中的翻译数据
        //翻译文件中，有几列语言，就读几列，这里默认写的是5列，中文，英语，缅甸语，泰语，台语
//        HashMap<String, List<String>> translateData = ExcelUtil.readExcel(translateDataFilePath, 5);
        changeSomeKey(modulePath, basePath, "exchange_complete_remain","完成%1$s 还需撮合交易%2$s");
    }

    /**
     * 自动替换翻译数据
     *
     * @param modulePath
     * @param basePath
     */
    public static void changeSomeKey(String modulePath, String basePath, String keyword,String word) {
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
                    //needTranslate属性不为空
                    if (key != null) {
                        String beTranslateXmlPath = basePath + childList.get(j).attributeValue("name");    //需要被翻译的xml的绝对路径
                        //得到待翻译的xml的document
                        Document beTranslateDocument = saxReader.read(beTranslateXmlPath);
                        //得到待翻译的xml的根节点
                        Element beTranslateRoot = beTranslateDocument.getRootElement();
                        //得到待翻译的xml的所有string标签
                        List<Element> childContents = beTranslateRoot.elements();
                        //遍历该xml文件中所有字段，若有跟上面那个中文字段一样的数据，那么用对应位置的外国语言代替
                        for (int elementIndex = 0; elementIndex < childContents.size(); elementIndex++) {
                            if (childContents.get(elementIndex).attributeValue("name").equals(keyword)) {
                                childContents.get(elementIndex).setText(word);
                            }
                        }
                        //把修改的数据存回去xml
                        saveXml(beTranslateXmlPath, beTranslateDocument);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("翻译失败，原因是：" + e);
        }
    }


}
