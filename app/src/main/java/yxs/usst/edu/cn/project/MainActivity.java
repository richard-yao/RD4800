package yxs.usst.edu.cn.project;


import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yxs.usst.edu.cn.project.fragment.FileContentFragment;
import yxs.usst.edu.cn.project.custom_class.FileDialogFragment;
import yxs.usst.edu.cn.project.fragment.GraphContentFragment;
import yxs.usst.edu.cn.project.fragment.ResultContentFragment;
import yxs.usst.edu.cn.project.fragment.SettingContentFragment;
import yxs.usst.edu.cn.project.fragment.ToolContentFragment;
import yxs.usst.edu.cn.project.interface_class.CollectData;
import yxs.usst.edu.cn.project.interface_class.CreateDialog;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;
import yxs.usst.edu.cn.project.custom_class.DetailViewPager;
import yxs.usst.edu.cn.project.custom_class.FragmentAdapter;
import yxs.usst.edu.cn.project.setting_paras.DevicePath;
import yxs.usst.edu.cn.project.util.MyUtil;
import yxs.usst.edu.cn.project.setting_paras.RequestPermission;

public class MainActivity extends FragmentActivity {

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();//all fragment
    private FragmentAdapter mFragmentAdapter;
    private DetailViewPager mViewPager;//id_fragment_content, show changed fragment content
    Button fileFeature, settingFeature, graphFeature, resultFeature, toolFeature;
    private FileContentFragment mFileFg;
    private SettingContentFragment mSettingFg;
    private GraphContentFragment mGraphFg;
    private ResultContentFragment mResultFg;
    private ToolContentFragment mToolRg;
    private FileDialogFragment fileDialogFragment;

    public List<Map<String, Object>> excelData = null;
    public Map<String, String> settingParas = null;
    //map参数中所有参数的key值
    public static String[] settingParasName = {"hex_graph_choice", "default_temp_choice", "dissolution_graph_choice", "stop_dissolution_temp_choice", "default_count_temp_choice",
            "default_temp_edit", "default_keeptime_edit", "dissolution_tempnum_edit", "change_stoptemp_edit", "change_counttemp_edit", "run"};
    private Map<String, Object> labData;//扩增曲线数据
    private Map<String, Object> dissolutionData;//溶解曲线数据
    private static int refreshTime = 60 * 1000;//设定扩增扫描时间1分钟
    private static boolean ampFlag = false;
    private static int temperatureTime = 80 * 1000;//设定溶解温度变化一度时间是80s
    private static boolean disFlag = false;
    private int runTime = 0;
    private int disTimes = 0;
    private String newLabName = "";//实验名称，新建时生成

    private TextView showTempText, showRunType;
    MyUtil mu = MyUtil.getInstance();
    AmpTaskThread ampTaskThread = new AmpTaskThread();
    DisTaskThread disTaskThread = new DisTaskThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        findAllControlById();
        initFragmentView();
        RequestPermission.verifyStoragePermissions(this);
    }

    /**
     * 初始化应用需要用到的文件路径
     */
    private void initialize() {
        DevicePath.getInstance().setRootPath(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name));
        DevicePath.getInstance().setLocalPath(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name) + "/" + getResources().getString(R.string.amplification));
        DevicePath.getInstance().setAmpDataPath(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name) + "/" + getResources().getString(R.string.ampData));
        DevicePath.getInstance().setDissolutionPath(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name) + "/" + getResources().getString(R.string.disData));
        DevicePath.getInstance().setProjectSdPath(getExternalFilesDir(null).getAbsolutePath());
        mu.createInitializeFolds();
    }

    private void findAllControlById() {
        mViewPager = (DetailViewPager) this.findViewById(R.id.id_fragment_content);

        fileFeature = (Button) this.findViewById(R.id.fileFeature);
        fileFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 1) {
                    settingParas = mSettingFg.getParas();
                }
                if (mViewPager.getCurrentItem() == 2) {
                    mGraphFg.setParas(settingParas);
                }
                mViewPager.setCurrentItem(0);
                resetTextView(0);
            }
        });
        settingFeature = (Button) this.findViewById(R.id.settingFeature);
        settingFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 2) {
                    mGraphFg.setParas(settingParas);
                }
                mViewPager.setCurrentItem(1);
                resetTextView(1);
            }
        });
        graphFeature = (Button) this.findViewById(R.id.graphFeature);
        graphFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 1) {
                    settingParas = mSettingFg.getParas();
                }
                mGraphFg.setParas(settingParas);
                mViewPager.setCurrentItem(2);
                resetTextView(2);
            }
        });
        resultFeature = (Button) this.findViewById(R.id.resultFeature);
        resultFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 1) {
                    settingParas = mSettingFg.getParas();
                }
                if (mViewPager.getCurrentItem() == 2) {
                    mGraphFg.setParas(settingParas);
                }
                mViewPager.setCurrentItem(3);
                resetTextView(3);
            }
        });
        toolFeature = (Button) this.findViewById(R.id.toolFeature);
        toolFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 1) {
                    settingParas = mSettingFg.getParas();
                }
                if (mViewPager.getCurrentItem() == 2) {
                    mGraphFg.setParas(settingParas);
                }
                mViewPager.setCurrentItem(4);
                resetTextView(4);
            }
        });
        showTempText = (TextView) this.findViewById(R.id.showTempText);
        showRunType = (TextView) this.findViewById(R.id.showRunType);
    }

    private void initFragmentView() {
        mFileFg = new FileContentFragment();
        fileDialogFragment = new FileDialogFragment();
        fileDialogFragment.setContext(getInstance());
        mFileFg.setListViewListener(new ListViewListener() {
            @Override
            public Context getMainContext() {
                return getInstance();
            }
        });
        mFileFg.setCreateDialog(new CreateDialog() {
            @Override
            public void createOpenDialog() {
                fileDialogFragment.setOpenFile(true);
                fileDialogFragment.setSetExcelPath(new FileDialogFragment.SetExcelPath() {
                    @Override
                    public void excelPath(String path, String name) {
                        resultFragmentData(path, name);
                    }
                });
                fileDialogFragment.show(getSupportFragmentManager(), "Open file");
            }

            @Override
            public void createNewDialog() {

            }

            @Override
            public void createSaveDialog() {
                fileDialogFragment.setOpenFile(false);
                FileDialogFragment.openResult = excelData;
                fileDialogFragment.show(getSupportFragmentManager(), "Save file");
            }
        });
        mFileFg.setLabNameSetting(new FileContentFragment.LabNameSetting() {
            @Override
            public void setLabName(String labName) {
                newLabName = labName;
            }
        });

        mSettingFg = new SettingContentFragment();
        mSettingFg.setListViewListener(new ListViewListener() {
            @Override
            public Context getMainContext() {
                return getInstance();
            }
        });
        mSettingFg.setCollectData(new CollectData() {
            @Override
            public void useInstancePara(Map<String, String> paras) {
                settingParas = paras;
            }

            @Override
            public void getDataFromDb(Map<String, String> paras) {
                settingParas = paras;
                //mGraphFg.setStopbtnOnClickable();
                mGraphFg.setRunRecordBtn();
                mSettingFg.setAllContentReadOnly();
                startGetDataFromDb();
                //Toast.makeText(getInstance(), "get data from db", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void stopGetData(Map<String, String> paras) {
                settingParas = paras;
                //mGraphFg.setRunbtnOnClickable();
                mGraphFg.setStopRecordBtn();
                mSettingFg.setAllContentClickable();
                if (settingParas.get("dissolution_graph_choice").equals("true")) {//强制停止运行实验
                    ampFlag = true;
                    disFlag = true;
                } else if (settingParas.get("dissolution_graph_choice").equals("false")) {
                    ampFlag = true;
                }
                stopGetDataFromDb();
                //Toast.makeText(getInstance(), "stop get data", Toast.LENGTH_SHORT).show();
            }
        });
        mGraphFg = new GraphContentFragment();
        mGraphFg.setShowHoleChart();
        mGraphFg.setListViewListener(new ListViewListener() {
            @Override
            public Context getMainContext() {
                return getInstance();
            }
        });
        mGraphFg.setCollectData(new CollectData() {
            @Override
            public void useInstancePara(Map<String, String> paras) {
                settingParas = paras;
            }

            @Override
            public void getDataFromDb(Map<String, String> paras) {
                settingParas = paras;
                mSettingFg.setStopbtnOnClickable();
                mSettingFg.setAllContentReadOnly();
                startGetDataFromDb();
                //Toast.makeText(getInstance(), "get data from db", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void stopGetData(Map<String, String> paras) {
                settingParas = paras;
                mSettingFg.setRunbtnOnClickable();
                mSettingFg.setAllContentClickable();
                if (settingParas.get("dissolution_graph_choice").equals("true")) {//强制停止运行实验
                    ampFlag = true;
                    disFlag = true;
                } else if (settingParas.get("dissolution_graph_choice").equals("false")) {
                    ampFlag = true;
                }
                stopGetDataFromDb();
                //Toast.makeText(getInstance(), "stop get data", Toast.LENGTH_SHORT).show();
            }
        });
        mGraphFg.setReDrawChart(new GraphContentFragment.ReDrawChart() {
            @Override
            public void reDrawChart(int type) {
                if (type == 1) {
                    mGraphFg.drawChart(runTime);//重新绘制扩增图形
                } else if (type == 2) {
                    mGraphFg.drawChart(disTimes);//重新绘制溶解图形
                }

            }
        });
        mResultFg = new ResultContentFragment();
        mResultFg.setListViewListener(new ListViewListener() {
            @Override
            public Context getMainContext() {
                return getInstance();
            }
        });
        mResultFg.setShowResultListData(new ResultContentFragment.ShowResultListData() {
            @Override
            public List<Map<String, Object>> showResultData() {
                return excelData;
            }
        });
        mToolRg = new ToolContentFragment();
        mToolRg.setAppName(getResources().getString(R.string.app_name));
        mToolRg.setListViewListener(new ListViewListener() {
            @Override
            public Context getMainContext() {
                return getInstance();
            }
        });
        mToolRg.setContentResolver(new ToolContentFragment.CustomContentResolver() {
            @Override
            public ContentResolver getCustomContentResolver() {
                return getContentResolver();
            }
        });
        mFragmentList.add(mFileFg);
        mFragmentList.add(mSettingFg);
        mFragmentList.add(mGraphFg);
        mFragmentList.add(mResultFg);
        mFragmentList.add(mToolRg);

        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setCurrentItem(0);
        resetTextView(0);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    settingParas = mSettingFg.getParas();//当setting页面滑动时，main获得设置的参数
                    mGraphFg.setParas(settingParas);
                }
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView(position);
                if (position == 3) {
                    mResultFg.showResultData();
                }
                if (position == 4) {
                    mToolRg.showFilesList();//重新加载显示文件目录
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //重置颜色
    private void resetTextView(int position) {
        switch (position) {
            case 0:
                fileFeature.setBackgroundColor(getResources().getColor(R.color.btnColor));
                changeTitleBtnColor(fileFeature.getId());
                break;
            case 1:
                settingFeature.setBackgroundColor(getResources().getColor(R.color.btnColor));
                changeTitleBtnColor(settingFeature.getId());
                break;
            case 2:
                graphFeature.setBackgroundColor(getResources().getColor(R.color.btnColor));
                changeTitleBtnColor(graphFeature.getId());
                break;
            case 3:
                resultFeature.setBackgroundColor(getResources().getColor(R.color.btnColor));
                changeTitleBtnColor(resultFeature.getId());
                break;
            case 4:
                toolFeature.setBackgroundColor(getResources().getColor(R.color.btnColor));
                changeTitleBtnColor(toolFeature.getId());
                break;
        }
    }

    //change title button background color
    private void changeTitleBtnColor(int btnId) {
        Integer[] allTitle = {R.id.fileFeature, R.id.settingFeature, R.id.graphFeature, R.id.resultFeature, R.id.toolFeature};
        for (int temp : allTitle) {
            if (btnId == temp) {
                continue;
            }
            if (findViewById(temp) != null) {
                findViewById(temp).setBackgroundColor(getResources().getColor(R.color.titleBackColor));
            }
        }
    }

    //get context
    private MainActivity getInstance() {
        return this;
    }

    public void resultFragmentData(String filePath, String fileName) {
        MyUtil mu = MyUtil.getInstance();
        excelData = mu.readExcel(ResultContentFragment.items, fileName, filePath);
        Toast.makeText(this, "RD4800 get excel data successfully", Toast.LENGTH_SHORT).show();
        if (excelData == null) {
            excelData = mu.creatTestData(ResultContentFragment.items);
        }
    }


    private Handler handler = new Handler() {
        protected void handlerMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    Toast.makeText(getInstance(), "Get data from dis excel", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getInstance(), "Get data from amp excel", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public class AmpTaskThread implements Runnable {
        public volatile boolean stop = false;

        @Override
        public void run() {
            Looper.prepare();
            if (settingParas == null) {
                return;
            }
            Integer temp = Integer.parseInt(settingParas.get("default_keeptime_edit").toString().trim());
            while (!stop && runTime < temp) {
                runTime++;
                if (settingParas.get("hex_graph_choice").equals("true")) {
                    labData = mu.getLabDataFromPhone(2, 1);
                } else {
                    labData = mu.getLabDataFromPhone(1, 1);
                }
                mGraphFg.setLabData(labData);
                mGraphFg.drawChart(runTime);
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.sendToTarget();
                //handler.postDelayed(this, refreshTime);
                try {
                    Thread.sleep(refreshTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ampFlag = true;
            stopGetDataFromDb();
            Message msg = handler.obtainMessage();
            msg.sendToTarget();
            Looper.loop();
        }
    }

    public class DisTaskThread implements Runnable {
        public volatile boolean stop = false;

        @Override
        public void run() {
            Looper.prepare();
            if (settingParas == null) {
                return;
            }
            Integer tempCount = Integer.parseInt(settingParas.get("change_counttemp_edit"));//度数误差
            Double tempGap = Double.parseDouble(settingParas.get("change_stoptemp_edit")) - Double.parseDouble(settingParas.get("dissolution_tempnum_edit"));
            while (!stop && disTimes < mu.divideValue(tempGap, tempCount, 0)) {
                disTimes++;
                if (settingParas.get("dissolution_graph_choice").equals("true")) {//采集溶解曲线数据
                    if (settingParas.get("hex_graph_choice").equals("true")) {
                        dissolutionData = mu.getLabDataFromPhone(2, 2);
                    } else {
                        dissolutionData = mu.getLabDataFromPhone(1, 2);
                    }
                    mGraphFg.setDissolutionData(dissolutionData);
                    mGraphFg.drawChart(disTimes);
                    if (settingParas != null && settingParas.size() > 0) {
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        msg.sendToTarget();
                    }
                    try {
                        Thread.sleep(temperatureTime * tempCount);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            disFlag = true;
            stopGetDataFromDb();
            Message msg = handler.obtainMessage();
            msg.sendToTarget();
            Looper.loop();
        }
    }

    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {//采集恒温扩增数据
            runTime++;
            if(settingParas.get("hex_graph_choice").equals("true")) {
                labData = mu.getLabDataFromPhone(2, 1);
            } else {
                labData = mu.getLabDataFromPhone(1, 1);
            }
            mGraphFg.setLabData(labData);
            mGraphFg.drawChart(runTime);
            handler.postDelayed(this, refreshTime);
            Toast.makeText(getInstance(), "Get data from amp excel", Toast.LENGTH_SHORT).show();
            if(settingParas != null) {
                Integer temp = Integer.parseInt(settingParas.get("default_keeptime_edit").toString().trim());
                if(runTime >= temp) {
                    ampFlag = true;
                    stopGetDataFromDb();
                }
            }
        }
    };*/
    /*Runnable disRunnale = new Runnable() {//采集溶解曲线数据
        @Override
        public void run() {
            disTimes++;
            if(settingParas.get("dissolution_graph_choice").equals("true")) {//采集溶解曲线数据
                if(settingParas.get("hex_graph_choice").equals("true")) {
                    dissolutionData = mu.getLabDataFromPhone(2, 2);
                } else {
                    dissolutionData = mu.getLabDataFromPhone(1, 2);
                }
                mGraphFg.setDissolutionData(dissolutionData);
                mGraphFg.drawChart(disTimes);
                if(settingParas != null && settingParas.size() > 0) {
                    Toast.makeText(getInstance(), "Get data from dis excel", Toast.LENGTH_SHORT).show();
                    Integer tempCount = Integer.parseInt(settingParas.get("change_counttemp_edit"));//度数误差
                    handler.postDelayed(this, temperatureTime * tempCount);//执行一次采集数据的时间
                    Double tempGap = Double.parseDouble(settingParas.get("change_stoptemp_edit"))- Double.parseDouble(settingParas.get("dissolution_tempnum_edit"));
                    if(disTimes >= mu.divideValue(tempGap, tempCount, 0)) {
                        disFlag = true;
                        stopGetDataFromDb();
                    }
                }
            }
        }
    };*/

    public void startGetDataFromDb() {//运行
        if (excelData != null && excelData.size() > 0) {
            excelData = new ArrayList<Map<String, Object>>();
            mResultFg.clearResultData();//清除掉旧的实验结果
        }
        showRunType.setText("运行");
        if (settingParas.get("default_temp_edit") != null) {
            showTempText.setText(settingParas.get("default_temp_edit") + "℃");
        }
        if (settingParas.get("hex_graph_choice").equals("false")) {//如果设置页面没有勾选采集hex通道数据，则这里无法点击选择
            mGraphFg.setHexCheckboxFalse();
        } else if (settingParas.get("hex_graph_choice").equals("true")) {
            mGraphFg.setHexCheckboxTrue();
        }
        //handler.post(runnable);
        new Thread(ampTaskThread).start();
        if (settingParas.get("dissolution_graph_choice").equals("true")) {
            //handler.post(disRunnale);
            new Thread(disTaskThread).start();
        }
    }

    public void stopGetDataFromDb() {//停止
        if (runTime == 0) {
            return;
        }
        boolean flag = false;
        if (settingParas.get("dissolution_graph_choice").equals("true")) {
            if (ampFlag && disFlag) {
                flag = true;
            }
        } else if (settingParas.get("dissolution_graph_choice").equals("false")) {
            if (ampFlag) {
                flag = true;
            }
        }
        if (flag) {
            mSettingFg.setRunbtnOnClickable();//后台采集停止运行，运行按钮可以点击
            mGraphFg.setRunbtnOnClickable();
            showRunType.setText("停止");
            ampTaskThread.stop = true;
            //handler.removeCallbacks(runnable);
            ampFlag = false;
            if (settingParas.get("dissolution_graph_choice").equals("true")) {
                //handler.removeCallbacks(disRunnale);
                disTaskThread.stop = true;
                disTimes = 0;
                disFlag = false;
            }
            printOutResult(runTime);
            mResultFg.showResultData();//显示实验结果
            if (excelData != null) {
                String fileName = "";
                if (newLabName.equals("")) {//没有新建excel
                    fileName = mu.formatDate();
                } else {//新建excel作为实验结果文件名
                    fileName = newLabName;
                }
                mu.createNewExcel(excelData, ResultContentFragment.items, fileName, DevicePath.getInstance().getLocalPath());//停止采集数据后自动保存result
            }
            runTime = 0;
        }
    }

    private void printOutResult(int runTime) {//显示扩增实验结果
        if (labData == null) {
            return;
        } else {
            Map<String, List<String>> famResult = (Map<String, List<String>>) labData.get("FAM");
            List<Map<String, Object>> recordResult = new ArrayList<Map<String, Object>>();
            String[] items = ResultContentFragment.items;
            for (int i = 0; i < famResult.size(); i++) {
                Map<String, Object> temp = new HashMap<String, Object>();
                temp.put(items[0], i + 1);//第一个是孔序号
                Map<String, String> tempResult = mu.getChartResult(famResult.get(String.valueOf(i + 1)), 1, runTime);
                for (int j = 1; j < items.length; j++) {
                    if (j == 6) {//dt值
                        temp.put(items[j], tempResult.get("dt"));
                    } else if (j == 7) {//结果
                        if (tempResult.get("result").equals("1")) {
                            temp.put(items[j], getResources().getString(R.string.positiveVal));
                        } else if (tempResult.get("result").equals("0")) {
                            temp.put(items[j], getResources().getString(R.string.negativeVal));
                        } else if (tempResult.get("result").equals("2")) {
                            temp.put(items[j], "wrong!");
                        }
                    } else {
                        temp.put(items[j], "null");
                    }
                }
                recordResult.add(temp);
            }
            excelData = recordResult;
        }
    }

}
