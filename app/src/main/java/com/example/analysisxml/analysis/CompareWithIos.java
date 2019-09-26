package com.example.analysisxml.analysis;

import com.example.analysisxml.util.ExcelUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.analysisxml.analysis.pathUtil.beCompareFilePath;
import static com.example.analysisxml.analysis.pathUtil.resultFilePath;

/**
 * 跟苹果数据源做对比
 */
public class CompareWithIos {
    public static void main(String[] args) throws Exception {
        //对比两个list
        List<String> result = compareTwoList(ExcelUtil.readExcelByOneColumn(beCompareFilePath, 0),
                ExcelUtil.readExcelByOneColumn(beCompareFilePath, 1));
        //导入结果到excel
        ExcelUtil.writeExcelByList(result, resultFilePath);
    }

    /**
     * 比对两个list，然后合为一个新的list
     *
     * @param targetList
     * @param list
     */
    public static List<String> compareTwoList(List<String> targetList, List<String> list) {
        List<String> resultList = new ArrayList<>();
        resultList.addAll(targetList);
        for (int i = 0; i < list.size(); i++) {
            if (!targetList.contains(list.get(i))) {
                resultList.add(list.get(i));
            }
        }
        return resultList;
    }
}
