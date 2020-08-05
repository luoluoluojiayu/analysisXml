package com.example.analysisxml.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static jxl.biff.BaseCellFeatures.logger;

public class ExcelUtil {
    /**
     * 写数据到excel中
     *
     * @param data
     * @param savePath
     */
    public static void writeExcelBySet(Set<String> data, String savePath) {
        //开始写入excel,创建模型文件头
        String[] titleA = {"中文string"};
        //创建Excel文件，B库CD表文件
        File fileA = new File(savePath);
        if (fileA.exists()) {
            //如果文件存在就删除
            fileA.delete();
        }
        try {
            fileA.createNewFile();
            //创建工作簿
            WritableWorkbook workbookA = Workbook.createWorkbook(fileA);
            //创建sheet
            WritableSheet sheetA = workbookA.createSheet("sheet1", 0);
            Label labelA = null;
            //设置列名
            for (int i = 0; i < titleA.length; i++) {
                labelA = new Label(i, 0, titleA[i]);
                sheetA.addCell(labelA);
            }
            Iterator<String> iterator=data.iterator();
            int i=0;
            while (iterator.hasNext()){
                labelA = new Label(0, i + 1, iterator.next());
                sheetA.addCell(labelA);
                i++;
            }
//            //获取数据源
//            for (int i = 0; i < data.size(); i++) {
//                labelA = new Label(0, i + 1, data.get(i));
//                sheetA.addCell(labelA);
//            }
            workbookA.write();
            //写入数据
            workbookA.close();
            //关闭连接
            logger.info("成功写入文件，请前往" + savePath + "查看文件！");
        } catch (Exception e) {
            logger.info("文件写入失败，报异常..." + e);
        }
    }

    /**
     * 写数据到excel中
     *
     * @param data
     * @param savePath
     */
    public static void writeExcelByList(List<String> data, String savePath) {
        //开始写入excel,创建模型文件头
        String[] titleA = {"中文"};
        //创建Excel文件，B库CD表文件
        File fileA = new File(savePath);
        if (fileA.exists()) {
            //如果文件存在就删除
            fileA.delete();
        }
        try {
            fileA.createNewFile();
            //创建工作簿
            WritableWorkbook workbookA = Workbook.createWorkbook(fileA);
            //创建sheet
            WritableSheet sheetA = workbookA.createSheet("sheet1", 0);
            Label labelA = null;
            //设置列名
            for (int i = 0; i < titleA.length; i++) {
                labelA = new Label(i, 0, titleA[i]);
                sheetA.addCell(labelA);
            }
            //获取数据源
            for (int i = 0; i < data.size(); i++) {
                labelA = new Label(0, i + 1, data.get(i));
                sheetA.addCell(labelA);
            }
            workbookA.write();
            //写入数据
            workbookA.close();
            //关闭连接
            logger.info("成功写入文件，请前往" + savePath + "查看文件！");
        } catch (Exception e) {
            logger.info("文件写入失败，报异常..." + e);
        }
    }

    /**
     * 读取excel数据，将每一列的数据弄成一个list，然后再存进去一个list<list<string></>></>中
     * @param filePath  该excel的绝对路径
     * @param columnNum 需要读取该excel前几列
     * @throws FileNotFoundException
     */
    public static HashMap<String, List<String>> readExcel(String filePath, int columnNum){
        HashMap<String, List<String>> data=new HashMap<>();
        try {
            Workbook book = Workbook.getWorkbook(new File(filePath));
            //读取excel表的第一个sheet
            Sheet sheet = book.getSheet(0);
            //构建所有读取到的列的列表
            for(int i=0;i<columnNum;i++){
                Cell[] cells = sheet.getColumn(i);
                //每列行数起码要大于一个元素
                if(cells.length>1){
                    List<String> contents=new ArrayList<>();
                    for(int j=1;j<cells.length;j++){
                        contents.add(cells[j].getContents());
                    }
                    data.put(cells[0].getContents(),contents);
                }
            }

        } catch (Exception e) {
            logger.info("文件读取失败，报异常..." + e);
        }
        return data;
    }

    /**
     * 读取指定excel某一列的数据
     * @param filePath  该excel的绝对路径
     * @param columnNum 需要读取该excel前几列
     * @throws FileNotFoundException
     */
    public static List<String> readExcelByOneColumn(String filePath, int columnNum){
        List<String> data=new ArrayList<>();
        try {
            Workbook book = Workbook.getWorkbook(new File(filePath));
            //读取excel表的第一个sheet
            Sheet sheet = book.getSheet(0);
            //构建所有读取到的列的列表
            Cell[] cells = sheet.getColumn(columnNum);
            for(int j=1;j<cells.length;j++){
                data.add(cells[j].getContents());
            }
        } catch (Exception e) {
            logger.info("文件读取失败，报异常..." + e);
        }
        return data;
    }
}
