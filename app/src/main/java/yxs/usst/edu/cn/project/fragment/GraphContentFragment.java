package yxs.usst.edu.cn.project.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import yxs.usst.edu.cn.project.MainActivity;
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
    private Map<String, String> paras = new HashMap<String, String>();
    private Map<String, Object> labData = null;

    public ListViewListener listViewListener;
    private CollectData collectData;

    public void setCollectData(CollectData collectData) {
        this.collectData = collectData;
    }
    public void setListViewListener(ListViewListener listViewListener) {
        this.listViewListener = listViewListener;
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
        //new MyAsyncTask().execute();
    }

    private void bindButtonListener() {
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] settingParasName = MainActivity.settingParasName;
                if(paras.get(settingParasName[settingParasName.length-1]).equals("false")) {//扫描未运行
                    Toast.makeText(listViewListener.getMainContext(), "请先运行扫描程序", Toast.LENGTH_SHORT).show();
                    return;
                } else if(paras.get(settingParasName[settingParasName.length-1]).equals("true")) {
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
    }

    public void setParas(Map<String, String> paras) {
        this.paras = paras;
    }

    public void setLabData(Map<String, Object> labData) {
        this.labData = labData;
    }

    public void drawChart(int time) {//绘图
        Map<String, List<String>> famResult = (Map<String, List<String>>) labData.get("FAM");
        List<Line> lines = new ArrayList<Line>();
        for(int i=1;i<=24;i++) {
            List<String> famTemp = famResult.get(String.valueOf(i));
            List<PointValue> listVals = new ArrayList<PointValue>();
            listVals.add(new PointValue(0, 0));//起点是原点
            time = time >20?time:20;//确保每一个孔至少有20个数据
            for(int j=0;j<time;j++) {
                PointValue tempPoint = new PointValue();
                tempPoint.set(j+1, Float.parseFloat(famTemp.get(j)));
                listVals.add(tempPoint);
            }
            Line line = new Line(listVals);
            if(i < 6) {
                line.setColor(Color.RED);
            } else if(i < 12) {
                line.setColor(Color.YELLOW);
            } else if(i < 18) {
                line.setColor(Color.GREEN);
            } else if(i < 24) {
                line.setColor(R.color.aqua);
            } else if(i < 30) {
                line.setColor(Color.BLUE);
            } else if(i < 36) {
                line.setColor(R.color.magenta);
            } else if(i < 42) {
                line.setColor(R.color.purple);
            } else {
                line.setColor(R.color.godenrod);
            }
            line.setCubic(true);
            lines.add(line);
        }
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
        mainChart.setLineChartData(data);
        mainChart.setInteractive(true);
        mainChart.setZoomType(ZoomType.HORIZONTAL);
        mainChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
    }

    MyUtil mu = MyUtil.getInstance();
    public void setStopbtnOnClickable() {
        mu.setStopbtnOnClickable(startRecordData, stopRecordData);
    }

    public void setRunbtnOnClickable() {
        mu.setRunbtnOnClickable(startRecordData, stopRecordData);
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
