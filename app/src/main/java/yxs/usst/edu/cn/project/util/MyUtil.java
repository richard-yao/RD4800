package yxs.usst.edu.cn.project.util;

import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import yxs.usst.edu.cn.project.R;

/**
 * Created by Administrator on 2016/4/17.
 */
public class MyUtil {

    public static MyUtil myUtil = new MyUtil();

    public static MyUtil getInstance() {
        return  myUtil;
    }

    public String gainMessageJSON(String url) throws Exception{
        URL u=new URL(url);
        HttpURLConnection con=(HttpURLConnection) u.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestProperty("Content-type","text/plain;charset=utf-8");
        con.setRequestMethod("GET");//
        con.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String line;
        String sum="";
        while ((line = reader.readLine()) != null) {
            sum+=line;
        }
        return sum;
    }

    public String getNumber(int input) {
        if(input <= 9) {
            return "0"+input;
        } else {
            return String.valueOf(input);
        }
    }

    public boolean isNullEmpty(String str) {
        if(str == null) {
            return false;
        } else if(str.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public String getTwoPointData(double input) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(input);
    }

    public List<Map<String, Object>> readExcel(String[] para, String fileName, String directory) {
        try {
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//存储卡正常挂载
                //InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/"+directory+"/"+fileName);
                InputStream is = new FileInputStream(directory);
                WorkbookSettings workbookSettings = new WorkbookSettings();
                workbookSettings.setGCDisabled(true);
                Workbook book = Workbook.getWorkbook(is, workbookSettings);
                if(book.getNumberOfSheets() > 0) {
                    Sheet sheet = book.getSheet(0);
                    int rows = sheet.getRows();
                    int cols = sheet.getColumns();
                    if(rows > 1) {//contain lab data
                        for (int i = 1; i < rows ; i++) {
                            Map<String, Object> temp = new HashMap<String, Object>();
                            for (int j = 0; j < cols ; j++) {
                                if(sheet.getCell(j, i).getContents() != null) {
                                    temp.put(para[j], sheet.getCell(j, i).getContents());
                                } else {
                                    temp.put(para[j], "");
                                }
                            }
                            result.add(temp);
                        }
                    }
                    book.close();
                    is.close();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createNewExcel(List<Map<String, Object>> data, String[] para, String fileName, String directory) {
        try {
            String[] itemsName = {"孔位", "条码", "姓名", "性别", "年龄", "项目", "dt值", "结果", "备注"};
            WritableWorkbook book = null;
            if(directory.startsWith("/")) {//directory but not file
                book = Workbook.createWorkbook(new File(directory+"/"+fileName+".xls"));
            } else {
                book = Workbook.createWorkbook(new File(Environment.getExternalStorageDirectory().getPath()+"/"+directory+"/"+fileName+".xls"));
            }
            //WritableWorkbook book = Workbook.createWorkbook(new File(Environment.getExternalStorageDirectory().getPath()+"/"+directory+"/"+fileName+".xls"));
            WritableSheet sheet = book.createSheet("Lab_result", 0);
            for(int i=0;i<itemsName.length;i++) {
                sheet.addCell(new Label(i, 0, itemsName[i]));
                sheet.setColumnView(i, 20);
            }
            if(data != null && para != null) {
                int row = 1;
                if(data.size() > 0) {
                    for(Map<String, Object> temp:data) {
                        sheet.addCell(new Label(0, row, getNumber(row)));
                        for(int i=1;i<para.length;i++) {
                            String value = (String) temp.get(para[i]);
                            value = value == null?"":value;
                            sheet.addCell(new Label(i, row, value));
                        }
                        row++;
                    }
                }
            }
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> creatTestData(String[] items) {
        List<Map<String, Object>> result =new ArrayList<Map<String, Object>>();
        int number = 1;
        for(int j=0;j<20;j++) {
            Map<String, Object> temp = new HashMap<String, Object>();
            for(int i=0;i<items.length;i++) {
                if(i == 0) {
                    temp.put(items[i], getNumber(number));
                } else {
                    temp.put(items[i], getTwoPointData(Math.random()));
                }
            }
            result.add(temp);
            number++;
        }
        return result;
    }

    public boolean validateText(String input) {//验证输入字符是否有效
        input = input.toString().trim();
        char[] name_arr = input.toCharArray();
        for(int i=0;i<name_arr.length;i++) {
            if(input.charAt(i) == ' ' || input.charAt(i) == '*' || input.charAt(i) == '\\' || input.charAt(i) == '/' || input.charAt(i) == '|' || input.charAt(i) == '?' || input.charAt(i) == '>' || input.charAt(i) == '<') {
                return false;
            }
        }
        return true;
    }
}
