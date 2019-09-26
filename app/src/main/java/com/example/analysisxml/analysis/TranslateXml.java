package com.example.analysisxml.analysis;

import com.example.analysisxml.util.ExcelUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;

import static com.example.analysisxml.analysis.pathUtil.basePath;
import static com.example.analysisxml.analysis.pathUtil.modulePath;
import static com.example.analysisxml.analysis.pathUtil.translateDataFilePath;
import static com.example.analysisxml.util.XmlUtil.saveXml;


/**
 * 自动给xml文件替换翻译
 */
public class TranslateXml {
    public static void main(String[] args) {
        //获取存放翻译数据的excel中的翻译数据
        HashMap<String, List<String>> translateData = ExcelUtil.readExcel(translateDataFilePath, 5);
        translate(modulePath, basePath, translateData);
    }

    /**
     * 自动替换翻译数据
     *
     * @param modulePath
     * @param basePath
     * @param translateData
     */
    public static void translate(String modulePath, String basePath, HashMap<String, List<String>> translateData) {
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
                        //假如翻译数据中有这个语言的数据
                        if (translateData.get(key) != null) {
                            List<String> chineseContents = translateData.get("中文");         //中文string集合
                            List<String> translateContents = translateData.get(key);          //需要翻译成的语言集合
                            String beTranslateXmlPath = basePath + childList.get(j).attributeValue("name");    //需要被翻译的xml的绝对路径
                            //得到待翻译的xml的document
                            Document beTranslateDocument = saxReader.read(beTranslateXmlPath);
                            //得到待翻译的xml的根节点
                            Element beTranslateRoot = beTranslateDocument.getRootElement();
                            //得到待翻译的xml的所有string标签
                            List<Element> childContents = beTranslateRoot.elements();
                            //遍历所有中文待翻译的字段，
                            for (int index = 0; index < chineseContents.size(); index++) {
                                String translateString = chineseContents.get(index);
                                //遍历该xml文件中所有字段，若有跟上面那个中文字段一样的数据，那么用对应位置的外国语言代替
                                for (int elementIndex = 0; elementIndex < childContents.size(); elementIndex++) {
                                    if (childContents.get(elementIndex).getText().equals(translateString)) {
                                        if(translateContents.get(index)!=null&&!translateContents.get(index).equals("")){
                                            childContents.get(elementIndex).setText(translateContents.get(index));
                                        }
                                    }
                                }
                            }
                            //把修改的数据存回去xml
                            saveXml(beTranslateXmlPath, beTranslateDocument);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("翻译失败，原因是：" + e);
        }
    }


}
