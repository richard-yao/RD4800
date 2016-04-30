package yxs.usst.edu.cn.project.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.CollectData;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;
import yxs.usst.edu.cn.project.util.MyUtil;

/**
 * Created by Administrator on 2016/4/10.
 */
public class GraphContentFragment extends Fragment {
    private static String dataUrl = null;
    private LineChartView mainChart;
    private Button increaseBtn,decreaseBtn,startRecordData,stopRecordData;
    private LinearLayout holePart;
    private CheckBox famCheckbox, hexCheckbox;
    private Map<String, String> paras = new HashMap<String, String>();//设置页面所有运行参数
    private Map<String, Object> labData = null;//采集的实验数据
    private Map<String, String> showHoleChart = new HashMap<String, String>();//所有孔的显示状况
    private Map<String, String> chartType = new HashMap<String, String>();//FAM/HEX显示情况
    public static int[] colors = {Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.rgb(255,0,255),Color.rgb(128,0,128), Color.rgb(210,105,30)};

    public ListViewListener listViewListener;
    private CollectData collectData;

    public void setCollectData(CollectData collectData) {
        this.collectData = collectData;
    }
    public void setListViewListener(ListViewListener listViewListener) {
        this.listViewListener = listViewListener;
    }

    public interface ReDrawChart {
        public void reDrawChart();
    }

    public ReDrawChart reDrawChart;

    public void setReDrawChart(ReDrawChart reDrawChart) {
        this.reDrawChart = reDrawChart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.graph_content_fragment, container,false);
        if(dataUrl == null) {
            dataUrl = chatView.getResources().getString(R.string.web_http);
            dataUrl = dataUrl + "/GetDataServlet";
        }
        mainChart = (LineChartView) chatView.findViewById(R.id.mainChart);
        increaseBtn = (Button) chatView.findViewById(R.id.increaseBtn);
        decreaseBtn = (Button) chatView.findViewById(R.id.decreaseBtn);
        startRecordData = (Button) chatView.findViewById(R.id.startRecordData);
        stopRecordData = (Button) chatView.findViewById(R.id.stopRecordData);
        holePart = (LinearLayout) chatView.findViewById(R.id.holePart);
        famCheckbox = (CheckBox) chatView.findViewById(R.id.fam_checkbox);
        hexCheckbox = (CheckBox) chatView.findViewById(R.id.hex_checkbox);
        chartType.put("fam_checkbox", "true");
        chartType.put("hex_checkbox", "false");
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        bindButtonListener();
        if(paras != null && paras.get("run") != null) {
            if(paras.get("run").equals("true")) {
                collectData.getDataFromDb(paras);
                setStopbtnOnClickable();
            } else if(paras.get("run").equals("false")){
                collectData.stopGetData(paras);
                setRunbtnOnClickable();
            }
        }
        bindHolePartListener();
        //new MyAsyncTask().execute();
    }

    private void bindButtonListener() {
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paras.get("run").equals("false")) {//扫描未运行
                    Toast.makeText(listViewListener.getMainContext(), "请先运行扫描程序", Toast.LENGTH_SHORT).show();
                    return;
                } else if(paras.get("run").equals("true")) {
                    //drawChart();
                    mainChart.setVisibility(View.VISIBLE);
                }
            }
        });
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(listViewListener.getMainContext(), "溶解曲线", Toast.LENGTH_SHORT).show();
            }
        });
        startRecordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paras.put("run", "true");
                collectData.getDataFromDb(paras);
                setStopbtnOnClickable();
            }
        });
        stopRecordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paras.put("run", "false");
                collectData.stopGetData(paras);
                setRunbtnOnClickable();
            }
        });
        famCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    chartType.put("fam_checkbox", "true");
                } else {
                    chartType.put("fam_checkbox", "false");
                }
                reDrawChart.reDrawChart();
            }
        });
        hexCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    chartType.put("hex_checkbox", "true");
                } else {
                    chartType.put("hex_checkbox", "false");
                }
                reDrawChart.reDrawChart();
            }
        });
    }

    private void bindHolePartListener() {
        Resources res = listViewListener.getMainContext().getResources();
        Drawable draw=res.getDrawable(R.mipmap.circle_hole, null);
        int number = 1;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        for(int i=0;i<8;i++) {//总共8行数据
            LinearLayout tempLayout = new LinearLayout(listViewListener.getMainContext());
            tempLayout.setOrientation(LinearLayout.HORIZONTAL);
            tempLayout.setBackgroundColor(colors[i]);
            tempLayout.setLayoutParams(lp);
            for(int j=0;j<6;j++) {//每行孔数
                final ImageButton tempBtn = new ImageButton(listViewListener.getMainContext());
                tempBtn.setLayoutParams(lp);
                tempBtn.setBackground(draw);
                tempBtn.setTag(String.valueOf(number));
                tempBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = (String) tempBtn.getTag();
                        if(showHoleChart.get(tag).equals("true")) {
                            showHoleChart.put(tag, "false");
                            tempBtn.setAlpha(0.5f);//不透明
                        } else {
                            showHoleChart.put(tag, "true");
                            tempBtn.setAlpha(1.0f);
                        }
                        reDrawChart.reDrawChart();
                    }
                });
                tempLayout.addView(tempBtn, j);
                number++;//孔的标识符
            }
            holePart.addView(tempLayout, i);
        }

    }

    public void setParas(Map<String, String> paras) {
        this.paras = paras;
    }

    public void setLabData(Map<String, Object> labData) {
        this.labData = labData;
    }

    public void setShowHoleChart() {//初始化
        if(showHoleChart.size() == 0) {
            for(int i=1;i<=48;i++) {
                showHoleChart.put(String.valueOf(i), "true");//默认显示所有孔曲线
            }
        }
    }

    public void drawChart(int time) {//绘图
        Map<String, List<String>> famResult = null;
        Map<String, List<String>> hexResult = null;
        if(chartType.get("fam_checkbox").equals("true")) {
            famResult = (Map<String, List<String>>) labData.get("FAM");
        }
        if(hexCheckbox.isClickable()) {//可点击，双通道采集数据
            if(chartType.get("hex_checkbox").equals("true")) {//显示hex
                hexResult = (Map<String, List<String>>) labData.get("HEX");
            }
        }
        List<Line> lines = new ArrayList<Line>();
        for(int i=1;i<=48;i++) {//从1到48个孔
            int num = (i-1)/6;
            if(showHoleChart.get(String.valueOf(i)).equals("true")) {//该孔曲线显示
                if(famResult != null) {
                    Line line = new Line(getListVals(famResult, i, time));
                    line.setColor(colors[num]);
                    line.setCubic(true);
                    lines.add(line);
                }
                if(hexResult != null) {
                    Line line = new Line(getListVals(hexResult, i, time));
                    line.setColor(colors[num]);
                    line.setCubic(true);
                    lines.add(line);
                }
            } else {
                continue;
            }

        }
        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis(); //X轴
        //axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.BLACK);
        //axisX.setName("扩增效率");
        //axisX.setMaxLabelChars(10);
        data.setAxisXBottom(axisX);

        Axis axisY = new Axis();  //Y轴
        //axisY.setMaxLabelChars(10);
        //axisY.setHasTiltedLabels(true);
        axisY.setTextColor(Color.BLACK);
        //axisY.setName("扩增倍数");
        data.setAxisYLeft(axisY);
        mainChart.setLineChartData(data);
        mainChart.setInteractive(true);
        mainChart.setZoomType(ZoomType.HORIZONTAL);
        mainChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
    }

    private List<PointValue> getListVals(Map<String, List<String>> result, int hole, int time) {
        List<String> famTemp = result.get(String.valueOf(hole));
        List<PointValue> listVals = new ArrayList<PointValue>();
        listVals.add(new PointValue(0, 0));//起点是原点
        if(time >= result.size()) {//运行时间最大不能超过采集到的数据总数
            time = result.size();
        } else {
            time = time>20?time:20;//确保每一个孔至少有20个数据,测试数据
        }
        for(int j=0;j<time;j++) {
            PointValue tempPoint = new PointValue();
            tempPoint.set(j+1, Float.parseFloat(famTemp.get(j)));
            listVals.add(tempPoint);
        }
        return listVals;
    }

    MyUtil mu = MyUtil.getInstance();
    public void setStopbtnOnClickable() {
        mu.setStopbtnOnClickable(startRecordData, stopRecordData);
    }

    public void setRunbtnOnClickable() {
        mu.setRunbtnOnClickable(startRecordData, stopRecordData);
    }

    public void setHexCheckboxTrue() {
        hexCheckbox.setClickable(true);
        hexCheckbox.setEnabled(true);
    }

    public void setHexCheckboxFalse() {
        hexCheckbox.setClickable(false);
        hexCheckbox.setEnabled(false);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {//用来处理网络数据，获取网络数据

        @Override
        protected String doInBackground(Void... params) {
            MyUtil mu = MyUtil.getInstance();
            try {
                String html = mu.gainMessageJSON(dataUrl);
                return html;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            List<PointValue> listVals = new ArrayList<PointValue>();
            List<AxisValue> listAxis = new ArrayList<AxisValue>();
            //String[] xLabel = {"0","0.07","0.14","0.21","0.28","0.35","0.42","0.49","0.56","0.63","0.70","0.77","0.84","0.91","0.98"};
            try {
                if(result != null) {
                    JSONArray dataArr = new JSONArray(result);
                    for(int i=0;i<dataArr.length();i++) {
                        JSONObject tempObj = dataArr.getJSONObject(i);
                        PointValue tempPoint = new PointValue();
                        tempPoint.set(Float.parseFloat(tempObj.getString("increase_efficiency")), Float.parseFloat(tempObj.getString("increase_multiple")));
                        listVals.add(tempPoint);
                    }
                    /*for(int i=0;i<xLabel.length;i++) {
                        listAxis.add(new AxisValue(Float.parseFloat(xLabel[i])));
                    }*/
                    Line line = new Line(listVals).setColor(Color.BLUE).setCubic(true);
                    List<Line> lines = new ArrayList<Line>();
                    lines.add(line);
                    LineChartData data = new LineChartData();
                    data.setLines(lines);

                    Axis axisX = new Axis(); //X轴
                    axisX.setHasTiltedLabels(true);
                    axisX.setTextColor(Color.BLACK);
                    axisX.setName("扩增效率");
                    axisX.setMaxLabelChars(10);
                    data.setAxisXBottom(axisX);

                    Axis axisY = new Axis();  //Y轴
                    axisY.setMaxLabelChars(10);
                    axisY.setHasTiltedLabels(true);
                    axisY.setTextColor(Color.BLACK);
                    axisY.setName("扩增倍数");
                    data.setAxisYLeft(axisY);

                    //设置行为属性，支持缩放、滑动以及平移
                    mainChart.setLineChartData(data);
                    mainChart.setInteractive(true);
                    mainChart.setZoomType(ZoomType.HORIZONTAL);
                    mainChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
                    mainChart.setVisibility(View.VISIBLE);

                } else {
                    Log.e("RD4800", "Get data from service null");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
