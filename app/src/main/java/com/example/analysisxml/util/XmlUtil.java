package com.example.analysisxml.util;


import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class XmlUtil {
    /**
     * 存储xml
     *
     * @param xmlPath
     * @param xmlDocument
     */
    public static void saveXml(String xmlPath, Document xmlDocument) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(xmlPath), format);
            xmlWriter.write(xmlDocument);
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("存储" + xmlPath + "xml文件失败，原因是：" + e);
        }
    }
}
