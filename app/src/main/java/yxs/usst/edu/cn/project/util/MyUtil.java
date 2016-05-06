package yxs.usst.edu.cn.project.util;

import android.graphics.Color;
import android.os.Environment;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lecho.lib.hellocharts.model.PointValue;
import yxs.usst.edu.cn.project.setting_paras.DevicePath;

/**
 * Created by Administrator on 2016/4/17.
 */
public class MyUtil {

    public static double dtTypeVal = 50.0;//根据线性拟合的斜率来区分阴阳性
    public static double hourTime = 60.0;//采集点x时间转换为小时

    public static MyUtil myUtil = new MyUtil();

    public static MyUtil getInstance() {
        return myUtil;
    }

    public String gainMessageJSON(String url) throws Exception {
        URL u = new URL(url);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestProperty("Content-type", "text/plain;charset=utf-8");
        con.setRequestMethod("GET");//
        con.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String line;
        String sum = "";
        while ((line = reader.readLine()) != null) {
            sum += line;
        }
        return sum;
    }

    public String getNumber(int input) {
        if (input <= 9) {
            return "0" + input;
        } else {
            return String.valueOf(input);
        }
    }

    public boolean isNullEmpty(String str) {
        if (str == null) {
            return false;
        } else if (str.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public String getTwoPointData(double input) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(input);
    }

    public String formatDate() {
        Date date = new Date();
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormater.format(date);
    }

    public void createInitializeFolds() {
        File file = new File(DevicePath.getInstance().getRootPath());
        if (!file.exists()) {
            file.mkdir();//创建应用程序根目录
        }
        file = new File(DevicePath.getInstance().getLocalPath());
        if (!file.exists()) {
            file.mkdir();//创建存放实验结果的目录
        }
        file = new File(DevicePath.getInstance().getDissolutionPath());
        if (!file.exists()) {
            file.mkdir();//创建存放溶解数据的目录
        }
        file = new File(DevicePath.getInstance().getAmpDataPath());
        if (!file.exists()) {
            file.mkdir();//创建存放扩增数据的目录;
        }
        file = new File(DevicePath.getInstance().getProjectSdPath());
        if (!file.exists()) {
            file.mkdir();//在外置sd卡中创建项目目录;
        }
    }

    public List<Map<String, Object>> readExcel(String[] para, String fileName, String directory) {
        try {
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//存储卡正常挂载
                InputStream is = new FileInputStream(directory);
                WorkbookSettings workbookSettings = new WorkbookSettings();
                workbookSettings.setGCDisabled(true);
                Workbook book = Workbook.getWorkbook(is, workbookSettings);
                if (book.getNumberOfSheets() > 0) {
                    Sheet sheet = book.getSheet(0);
                    int rows = sheet.getRows();
                    int cols = sheet.getColumns();
                    if (rows > 1) {//contain lab data
                        for (int i = 1; i < rows; i++) {
                            Map<String, Object> temp = new HashMap<String, Object>();
                            for (int j = 0; j < cols; j++) {
                                if (sheet.getCell(j, i).getContents() != null) {
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
            if (directory.startsWith("/")) {//directory but not file
                book = Workbook.createWorkbook(new File(directory + "/" + fileName + ".xls"));
            } else {
                book = Workbook.createWorkbook(new File(Environment.getExternalStorageDirectory().getPath() + "/" + directory + "/" + fileName + ".xls"));
            }
            WritableSheet sheet = book.createSheet("实验结果", 0);
            for (int i = 0; i < itemsName.length; i++) {
                sheet.addCell(new Label(i, 0, itemsName[i]));
                sheet.setColumnView(i, 20);
            }
            if (data != null && para != null) {
                int row = 1;
                if (data.size() > 0) {
                    for (Map<String, Object> temp : data) {
                        sheet.addCell(new Label(0, row, getNumber(row)));
                        for (int i = 1; i < para.length; i++) {
                            String value = (String) temp.get(para[i]);
                            value = value == null ? "" : value;
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
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        int number = 1;
        for (int j = 0; j < 20; j++) {
            Map<String, Object> temp = new HashMap<String, Object>();
            for (int i = 0; i < items.length; i++) {
                if (i == 0) {
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
        for (int i = 0; i < name_arr.length; i++) {
            if (input.charAt(i) == ' ' || input.charAt(i) == '*' || input.charAt(i) == '\\' || input.charAt(i) == '/' || input.charAt(i) == '|' || input.charAt(i) == '?' || input.charAt(i) == '>' || input.charAt(i) == '<') {
                return false;
            }
        }
        return true;
    }

    /**
     * @param type      1是单通道，2是双通道
     * @param chartType 1是恒温扩增曲线，2是溶解曲线
     * @return 读取到的扩增曲线的数据
     */
    public Map<String, Object> getLabDataFromPhone(int type, int chartType) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, List<String>> famResult = new HashMap<String, List<String>>();
        Map<String, List<String>> hexResult = new HashMap<String, List<String>>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//存储卡正常挂载
            try {
                String directory = "";
                if (chartType == 1) {
                    directory = DevicePath.getInstance().getAmpDataPath();//扩增数据目录
                } else if (chartType == 2) {
                    directory = DevicePath.getInstance().getDissolutionPath();//溶解数据目录
                }
                File files = new File(directory);
                File[] listFiles = files.listFiles();
                if (listFiles.length == 1 && listFiles[0].getName().endsWith(".xls")) {//该目录下唯一的一个excel就是采集到的数据点
                    String dataFile = directory + "/" + listFiles[0].getName();
                    InputStream is = new FileInputStream(dataFile);
                    WorkbookSettings workbookSettings = new WorkbookSettings();
                    workbookSettings.setGCDisabled(true);
                    Workbook book = Workbook.getWorkbook(is, workbookSettings);
                    if (book.getNumberOfSheets() == 2) {//测试数据，共有两个，一为FAM，一为HEX
                        if (type == 2) {//双通道
                            Sheet famSheet = book.getSheet(0);
                            Sheet hexSheet = book.getSheet(1);
                            famResult = getSheetData(famSheet);
                            hexResult = getSheetData(hexSheet);
                            result.put("FAM", famResult);
                            result.put("HEX", hexResult);
                        } else if (type == 1) {//单通道
                            Sheet famSheet = book.getSheet(0);
                            famResult = getSheetData(famSheet);
                            result.put("FAM", famResult);
                        }
                        book.close();
                        is.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    /**
     * @param sheet 输入excel的工作表
     * @return 以孔为key，每行所有数据为value的map
     */
    public Map<String, List<String>> getSheetData(Sheet sheet) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        int rows = sheet.getRows();
        int cols = sheet.getColumns();
        if (rows > 1) {//contain lab data
            for (int i = 1; i < rows; i++) {
                String hole = sheet.getCell(0, i).getContents();//excel中第一列的值，也就是孔的序号
                for (int j = 2; j < cols; j++) {
                    if (result.get(hole) != null) {
                        result.get(hole).add(sheet.getCell(j, i).getContents());
                    } else {
                        List<String> tempList = new ArrayList<String>();
                        tempList.add(sheet.getCell(j, i).getContents());
                        result.put(hole, tempList);
                    }
                }
            }
        }
        return result;
    }

    public void setRunbtnOnClickable(Button startBtn, Button stopBtn) {
        if (startBtn == null || stopBtn == null) {//程序最开始，按钮还未实例化
            return;
        }
        startBtn.setClickable(true);
        startBtn.setTextColor(Color.BLACK);
        stopBtn.setClickable(false);
        stopBtn.setTextColor(Color.GRAY);
    }

    public void setStopbtnOnClickable(Button startBtn, Button stopBtn) {
        if (startBtn == null || stopBtn == null) {//程序最开始，按钮还未实例化
            return;
        }
        startBtn.setClickable(false);
        startBtn.setTextColor(Color.GRAY);
        stopBtn.setClickable(true);
        stopBtn.setTextColor(Color.BLACK);
    }

    public String getExtSdCardPath() {
        String sdcard_path = null;
        String sd_default = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("cat /proc/mounts");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/storage/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                }
            }
            br.close();
            isr.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdcard_path;
    }

    public String copySdcardFile(String fromFile, String toFile) {
        int fileMax = 1024 * 1024;
        try {
            File inputFile = new File(fromFile);
            if (!inputFile.exists()) {
                return "error";
            }
            File outFile = new File(toFile);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            InputStream fosfrom = new FileInputStream(fromFile);
            /*RandomAccessFile raf = new RandomAccessFile(toFile, "rw");*/
            OutputStream raf = new FileOutputStream(toFile);
            byte bt[] = new byte[fileMax];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                raf.write(bt, 0, c);
            }
            fosfrom.close();
            raf.close();
            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }

    }

    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete(); // 否则如果它是一个目录
            } else if (file.isDirectory()) {
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            //file.delete();这个目录不用删除了
        }
    }

    /**
     * @param val1   被除数
     * @param val2   除数
     * @param valNum 小数点后保留位数
     * @return
     */
    public double divideValue(double val1, double val2, int valNum) {
        BigDecimal v1 = new BigDecimal(Double.valueOf(val1));
        BigDecimal v2 = new BigDecimal(Double.valueOf(val2));
        return v1.divide(v2, valNum, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * @param axisVal 输入y点的值
     * @return 返回均值
     */
    public double getAverageValue(List<String> axisVal) {
        double sum = 0.0;
        for (int i = 0; i < axisVal.size(); i++) {
            sum += Double.parseDouble(axisVal.get(i));
        }
        return divideValue(sum, axisVal.size(), 2);
    }


    /**
     * @param value 采集到的每个孔的连续数据
     * @param type  数据类型，1是扩增，2是溶解
     * @return 斜率dt以及结果：0阴性 or 1阳性
     */
    public Map<String, String> getChartResult(List<String> value, int type, int time) {
        if (time < 20) {//确保每一个孔至少有20个数据,测试数据
            time = 20;
        }
        if (time >= value.size()) {//运行时间最大不能超过采集到的数据总数
            time = value.size();
        }
        value = value.subList(0, time);
        Map<String, String> result = new HashMap<String, String>();
        if (value != null && value.size() > 0) {
            if (type == 1) {
                int size = value.size();
                double aveX = divideValue(Double.valueOf(size + 1), 2.0, 2);//x轴连续整数相加求平均(n+1)/2
                double aveY = getAverageValue(value);
                /*double r = getCorrelationValue(value, size, aveX, aveY);
                if(r < 0.5) {//线性不相关，是有波峰的曲线
                    result.put("result", "1");
                    result.put("dt", String.valueOf(getDtValue(value, size, aveX, aveY)));//预先模拟dt值为1
                } else {//线性相关，阳性
                    result.put("result", "0");
                    result.put("dt", String.valueOf(getDtValue(value, size, aveX, aveY)));
                }*/
                double dt = 0;
                try {
                    dt = getDtValue(value, size, aveX, aveY);
                } catch (Exception e) {//除数为0
                    e.printStackTrace();
                    result.put("result", "2");
                    result.put("dt", "wrong!");
                }
                if (dt < dtTypeVal * hourTime) {//如果是阴性的，基于实验数据，dt值不可能大于20，这里默认最大为50,乘以时间60
                    result.put("result", "0");
                    result.put("dt", String.valueOf(dt));
                } else {//阳性，有波峰
                    result.put("result", "1");
                    double temp = countAmplificationDt(value, divideValue(dt, hourTime, 2));
                    if (temp != -1.0) {
                        result.put("dt", String.valueOf(temp));
                    } else {
                        result.put("dt", String.valueOf(dt));
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param value 扩增采集到的数据,默认从1开始计算
     * @return 线性相关系数，0到1之间,,这个线性相关系数似乎没用了，即便趋势是直线，然而如果误差很大还是不准
     */
    public Double getCorrelationValue(List<String> value, int size, double aveX, double aveY) {
        double numerator = 0.0;//分子
        double xDeno = 0.0;
        double yDeno = 0.0;
        for (int i = 0; i < size; i++) {
            numerator += ((i + 1) - aveX) * (Double.parseDouble(value.get(i)) - aveY);
            xDeno += Math.pow((i + 1) - aveX, 2);
            yDeno += Math.pow((Double.parseDouble(value.get(i)) - aveY), 2);
        }
        double denominator = Math.sqrt(xDeno) * Math.sqrt(yDeno);//分母
        return Math.abs(divideValue(numerator, denominator, 2));
    }


    /**
     * @param value 扩增采集到的数据
     * @return 线性系数dt
     */
    public Double getDtValue(List<String> value, int size, double aveX, double aveY) {//注意这里需要将x轴的值转为小时值，所以换算后需要将结果乘以60
        double numerator = 0.0;
        double xDeno = 0.0;
        for (int i = 0; i < size; i++) {
            numerator += (i + 1) * Double.parseDouble(value.get(i));
            xDeno += Math.pow((i + 1), 2);
        }
        numerator = numerator - size * aveX * aveY;
        double denominator = xDeno - size * Math.pow(aveX, 2);
        numerator = numerator * hourTime;
        return divideValue(numerator, denominator, 2);
    }

    /**
     * @param value 扩增采集到的数据点
     * @return 真正扩增曲线的dt值
     */
    public Double countAmplificationDt(List<String> value, double curveDt) {
        List<Double> dyList = new ArrayList<Double>();
        List<Double> resultList = new ArrayList<Double>();
        double temp = 0.0;
        for (int i = 0; i < value.size() - 1; i++) {
            temp = Double.parseDouble(value.get(i + 1)) - Double.parseDouble(value.get(i));//两点之间的差值
            dyList.add(temp);
        }
        for (int i = 0; i < dyList.size() - 1; i++) {//根据实验结果，前五分钟一般不会有明显变化
            if (dyList.get(i) > curveDt && dyList.get(i + 1) > curveDt) {//连续两个点斜率大于拟合拟合直线的斜率
                resultList.add(dyList.get(i));
                continue;
            }
            if (dyList.get(i) > curveDt && dyList.get(i + 1) < curveDt) {//曲线开始平稳乃至下降
                if (resultList.size() >= 1) {
                    resultList.add(dyList.get(i));
                    break;
                } else {
                    continue;
                }
            }
        }
        if (resultList.size() == 0) {//未找到比拟合直线斜率大的
            return -1.0;
        } else {
            return maxValue(resultList);
        }
    }

    public Double maxValue(List<Double> inputList) {
        double temp = 0;
        if (inputList != null && inputList.size() > 1) {
            temp = inputList.get(0);
            for (int i = 1; i < inputList.size(); i++) {
                temp = temp > inputList.get(i) ? temp : inputList.get(i);
            }
        }
        return temp;
    }

    public List<PointValue> getDisDtValue(List<String> result,int time, double initVal, double dis) {
        List<PointValue> listVals = new ArrayList<PointValue>();
        if(result == null || result.size() == 0) {
            return listVals;
        }
        double dt = 0.0;
        double xVal = 0.0;
        if (time < 20) {//确保每一个孔至少有20个数据,测试数据
            time = 20;
        }
        if (time >= result.size()) {//运行时间最大不能超过采集到的数据总数
            time = result.size();
        }
        listVals.add(new PointValue((float)initVal, 0));
        for(int i=0;i<time-1;i++) {
            PointValue pointValue = new PointValue();
            dt = Double.parseDouble(result.get(i+1)) - Double.parseDouble(result.get(i));
            dt = divideValue(dt, dis, 2);
            xVal = initVal + (i+1)*dis;
            pointValue.set((float)xVal, (float)dt);
            listVals.add(pointValue);
        }
        return listVals;
    }
}
