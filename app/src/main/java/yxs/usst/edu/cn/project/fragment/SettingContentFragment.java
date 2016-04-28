package yxs.usst.edu.cn.project.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.CollectData;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;

/**
 * Created by Administrator on 2016/4/10.
 */
public class SettingContentFragment extends Fragment {

    //HEX通道，默认温度，溶解曲线，默认结束温度，默认度数误差
    CheckBox hexGraphChoice,defaultTemp,dissolutionGraph,stopDissolutionTemp,defaultCountTemp;
    //恒温温度值，恒温时间值，起始温度值，结束温度值，度数误差值
    EditText changeDefaultTemp,keepTempTime,dissolutionTempNum,changeDefaultStopTemp,changeDefaultCountTemp;
    //运行，停止按钮
    Button startRecordData,stopRecordData;

    private ListViewListener listViewListener;
    public void setListViewListener(ListViewListener lvl) {
        this.listViewListener = lvl;
    }
    private CollectData collectData;
    public void setCollectData(CollectData collectData) {
        this.collectData = collectData;
    }

    private Map<String, String> paras = new HashMap<String, String>();
    //map参数中所有参数的key值
    public static String[] itemsName = {"hex_graph_choice","default_temp_choice","dissolution_graph_choice","stop_dissolution_temp_choice","default_count_temp_choice",
                            "default_temp_edit","default_keeptime_edit","dissolution_tempnum_edit","change_stoptemp_edit","change_counttemp_edit"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.setting_content_fragment, container,false);
        hexGraphChoice = (CheckBox) chatView.findViewById(R.id.hexGraphChoice);
        defaultTemp = (CheckBox) chatView.findViewById(R.id.defaultTemp);
        dissolutionGraph = (CheckBox) chatView.findViewById(R.id.dissolutionGraph);
        stopDissolutionTemp = (CheckBox) chatView.findViewById(R.id.stopDissolutionTemp);
        defaultCountTemp = (CheckBox) chatView.findViewById(R.id.defaultCountTemp);

        changeDefaultTemp = (EditText) chatView.findViewById(R.id.changeDefaultTemp);
        keepTempTime = (EditText) chatView.findViewById(R.id.keepTempTime);
        dissolutionTempNum = (EditText) chatView.findViewById(R.id.dissolutionTempNum);
        changeDefaultStopTemp = (EditText) chatView.findViewById(R.id.changeDefaultStopTemp);
        changeDefaultCountTemp = (EditText) chatView.findViewById(R.id.changeDefaultCountTemp);

        startRecordData = (Button) chatView.findViewById(R.id.startRecordData);
        stopRecordData = (Button) chatView.findViewById(R.id.stopRecordData);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        bindCheckBoxListener();
        bindButtonListener();
    }

    private void bindCheckBoxListener() {
        paras.put("hex_graph_choice", "true");
        hexGraphChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    paras.put("hex_graph_choice", "true");
                } else {
                    paras.put("hex_graph_choice", "false");
                }
            }
        });
        paras.put("default_temp_choice", "true");
        setEditTextReadOnly(changeDefaultTemp);//默认温度选中，则编辑框无法更改
        defaultTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    paras.put("default_temp", "true");
                    changeDefaultTemp.setText("42.0");
                    setEditTextReadOnly(changeDefaultTemp);//默认温度选中，则编辑框无法更改
                } else {
                    paras.put("default_temp", "false");
                    setEditTextEditable(changeDefaultTemp);//未选中默认温度，则可以编辑
                }
            }
        });
        paras.put("dissolution_graph_choice", "false");
        setEditTextReadOnly(dissolutionTempNum);
        setEditTextReadOnly(changeDefaultStopTemp);
        setEditTextReadOnly(changeDefaultCountTemp);
        dissolutionGraph.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    paras.put("dissolution_graph_choice", "true");
                    setEditTextEditable(dissolutionTempNum);//选中后，起始温度可编辑
                    stopDissolutionTemp.setChecked(true);//选中后，结束温度默认自动勾选
                    defaultCountTemp.setChecked(true);//选中后，度数误差默认自动勾选
                } else {
                    paras.put("dissolution_graph_choice", "false");
                    setEditTextReadOnly(dissolutionTempNum);//未选中，起始温度不可编辑
                    stopDissolutionTemp.setChecked(false);//未选中，结束温度无法勾选
                    defaultCountTemp.setChecked(false);//未选中，度数误差无法勾选
                }
            }
        });
       /* stopDissolutionTemp.setOnClickListener(new View.OnClickListener() {//根据溶解曲线选中情况判断是否选中结束温度
            @Override
            public void onClick(View v) {
                if(!dissolutionGraph.isChecked()) {
                    stopDissolutionTemp.setChecked(false);
                } else {//溶解曲线选中
                    if(stopDissolutionTemp.isChecked()) {
                        stopDissolutionTemp.setChecked(false);
                    } else {
                        stopDissolutionTemp.setChecked(true);
                    }
                }
            }
        });
        defaultCountTemp.setOnClickListener(new View.OnClickListener() {//根据溶解曲线选中情况判断是否选中度数温差
            @Override
            public void onClick(View v) {
                if(!dissolutionGraph.isChecked()) {
                    defaultCountTemp.setChecked(false);
                } else {//溶解曲线选中
                    if(defaultCountTemp.isChecked()) {
                        defaultCountTemp.setChecked(false);
                    } else {
                        defaultCountTemp.setChecked(true);
                    }
                }
            }
        });*/
        paras.put("stop_dissolution_temp_choice", "false");
        stopDissolutionTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(dissolutionGraph.isChecked()) {
                        paras.put("stop_dissolution_temp_choice", "true");
                        changeDefaultStopTemp.setText("80.0");
                        setEditTextReadOnly(changeDefaultStopTemp);//选中后，结束温度无法修改
                    } else {
                        paras.put("stop_dissolution_temp_choice", "false");
                        stopDissolutionTemp.setChecked(false);
                        setEditTextReadOnly(changeDefaultStopTemp);
                    }
                } else {
                    paras.put("stop_dissolution_temp_choice", "false");
                    setEditTextEditable(changeDefaultStopTemp);//未选中，结束温度可修改
                }
            }
        });
        paras.put("default_count_temp_choice", "false");
        defaultCountTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(dissolutionGraph.isChecked()) {
                        paras.put("default_count_temp_choice", "true");
                        changeDefaultCountTemp.setText("1");
                        setEditTextReadOnly(changeDefaultCountTemp);//选中后，度数误差无法修改
                    } else {
                        paras.put("default_count_temp_choice", "false");
                        defaultCountTemp.setChecked(false);
                        setEditTextReadOnly(changeDefaultCountTemp);
                    }

                } else {
                    paras.put("default_count_temp_choice", "false");
                    setEditTextEditable(changeDefaultCountTemp);//未选中，度数误差可修改
                }
            }
        });
    }

    private void bindButtonListener() {
        startRecordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeDefaultTemp.getText().toString().trim().equals("")) {
                    getEditFocus(changeDefaultTemp);
                    return;
                } else {
                    paras.put("default_temp_edit", changeDefaultTemp.getText().toString().trim());
                }
                if(keepTempTime.getText().toString().trim().equals("")) {
                    getEditFocus(keepTempTime);
                    return;
                } else {
                    paras.put("default_keeptime_edit", keepTempTime.getText().toString().trim());
                }
                if(dissolutionTempNum.getText().toString().trim().equals("")) {
                    getEditFocus(dissolutionTempNum);
                    return;
                } else {
                    paras.put("dissolution_tempnum_edit", dissolutionTempNum.getText().toString().trim());
                }
                if(changeDefaultStopTemp.getText().toString().trim().equals("")) {
                    getEditFocus(changeDefaultStopTemp);
                    return;
                } else {
                    paras.put("change_stoptemp_edit", changeDefaultStopTemp.getText().toString().trim());
                }
                if(changeDefaultCountTemp.getText().toString().trim().equals("")) {
                    getEditFocus(changeDefaultCountTemp);
                    return;
                } else {
                    paras.put("change_counttemp_edit", changeDefaultCountTemp.getText().toString().trim());
                }
                collectData.getDataFromDb(paras);
            }
        });

        stopRecordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData.stopGetData();
            }
        });


    }


    private static void setEditTextReadOnly(TextView view){
        view.setTextColor(Color.GRAY);   //设置只读时的文字颜色
        if (view instanceof android.widget.EditText){
            view.setCursorVisible(false);      //设置输入框中的光标不可见
            view.setFocusable(false);           //无焦点
            view.setFocusableInTouchMode(false);     //触摸时也得不到焦点
        }
    }

    private static void setEditTextEditable(TextView view) {
        view.setTextColor(Color.BLACK);
        if (view instanceof android.widget.EditText){
            view.setCursorVisible(true);      //设置输入框中的光标不可见
            view.setFocusableInTouchMode(true);     //触摸时也得不到焦点
        }
    }

    private static void getEditFocus(TextView view) {
        if(view instanceof android.widget.EditText) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }
}
