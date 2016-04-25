package yxs.usst.edu.cn.project.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.util.MyUtil;

/**
 * Created by Administrator on 2016/4/10.
 */
public class GraphContentFragment extends Fragment {
    private static String dataUrl = null;
    private LineChartView mainChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.graph_content_fragment, container,false);
        if(dataUrl == null) {
            dataUrl = chatView.getResources().getString(R.string.web_http);
            dataUrl = dataUrl + "/GetDataServlet";
        }
        mainChart = (LineChartView) chatView.findViewById(R.id.mainChart);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        new MyAsyncTask().execute();
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {

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
                    mainChart.setLineChartData(data);
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
