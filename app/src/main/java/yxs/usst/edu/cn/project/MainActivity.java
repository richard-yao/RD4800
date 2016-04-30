package yxs.usst.edu.cn.project;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yxs.usst.edu.cn.project.fragment.FileContentFragment;
import yxs.usst.edu.cn.project.fragment.FileDialogFragment;
import yxs.usst.edu.cn.project.fragment.GraphContentFragment;
import yxs.usst.edu.cn.project.fragment.ResultContentFragment;
import yxs.usst.edu.cn.project.fragment.SettingContentFragment;
import yxs.usst.edu.cn.project.fragment.ToolContentFragment;
import yxs.usst.edu.cn.project.interface_class.CollectData;
import yxs.usst.edu.cn.project.interface_class.CreateDialog;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;
import yxs.usst.edu.cn.project.util.FragmentAdapter;
import yxs.usst.edu.cn.project.util.MyUtil;

public class MainActivity extends FragmentActivity {

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();//all fragment
    private FragmentAdapter mFragmentAdapter;
    private ViewPager mViewPager;//id_fragment_content, show changed fragment content
    Button fileFeature,settingFeature,graphFeature,resultFeature,toolFeature;
    private FileContentFragment mFileFg;
    private SettingContentFragment mSettingFg;
    private GraphContentFragment mGraphFg;
    private ResultContentFragment mResultFg;
    private ToolContentFragment mToolRg;
    private FileDialogFragment fileDialogFragment;

    public List<Map<String, Object>> excelData = null;
    public Map<String, String> settingParas = null;
    //map参数中所有参数的key值
    public static String[] settingParasName = {"hex_graph_choice","default_temp_choice","dissolution_graph_choice","stop_dissolution_temp_choice","default_count_temp_choice",
            "default_temp_edit","default_keeptime_edit","dissolution_tempnum_edit","change_stoptemp_edit","change_counttemp_edit","run"};
    private Map<String, Object> labData;//扩增曲线数据
    private static int refreshTime = 60*1000;
    private int runTime = 0;

    private TextView showTempText,showRunType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findAllControlById();
        initFragmentView();
    }


    private void findAllControlById() {
        mViewPager = (ViewPager) this.findViewById(R.id.id_fragment_content);

        fileFeature = (Button) this.findViewById(R.id.fileFeature);
        fileFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager.getCurrentItem() == 1) {
                    settingParas = mSettingFg.getParas();
                }
                if(mViewPager.getCurrentItem() == 2) {
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
                if(mViewPager.getCurrentItem() == 2) {
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
                if(mViewPager.getCurrentItem() == 1) {
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
                if(mViewPager.getCurrentItem() == 1) {
                    settingParas = mSettingFg.getParas();
                }
                if(mViewPager.getCurrentItem() == 2) {
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
                if(mViewPager.getCurrentItem() == 1) {
                    settingParas = mSettingFg.getParas();
                }
                if(mViewPager.getCurrentItem() == 2) {
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
                mGraphFg.setStopbtnOnClickable();
                startGetDataFromDb();
                Toast.makeText(getInstance(), "get data from db", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void stopGetData(Map<String, String> paras) {
                settingParas = paras;
                mGraphFg.setRunbtnOnClickable();
                stopGetDataFromDb();
                Toast.makeText(getInstance(), "stop get data", Toast.LENGTH_SHORT).show();
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
                startGetDataFromDb();
                Toast.makeText(getInstance(), "get data from db", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void stopGetData(Map<String, String> paras) {
                settingParas = paras;
                mSettingFg.setRunbtnOnClickable();
                stopGetDataFromDb();
                Toast.makeText(getInstance(), "stop get data", Toast.LENGTH_SHORT).show();
            }
        });
        mGraphFg.setReDrawChart(new GraphContentFragment.ReDrawChart() {
            @Override
            public void reDrawChart() {
                mGraphFg.drawChart(runTime);//重新绘制图形
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
                if(position == 1) {
                    settingParas = mSettingFg.getParas();//当setting页面滑动时，main获得设置的参数
                    mGraphFg.setParas(settingParas);
                }
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView(position);
                if(position == 3) {
                    mResultFg.showResultData();
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
        for(int temp:allTitle) {
            if(btnId == temp) {
                continue;
            }
            if(findViewById(temp) != null) {
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
        if(excelData == null) {
            excelData = mu.creatTestData(ResultContentFragment.items);
        }
    }

    MyUtil mu = MyUtil.getInstance();
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            runTime++;
            if(settingParas.get("hex_graph_choice").equals("true")) {
                labData = mu.getLabDataFromPhone(2);
            } else {
                labData = mu.getLabDataFromPhone(1);
            }
            mGraphFg.setLabData(labData);
            mGraphFg.drawChart(runTime);
            handler.postDelayed(this, refreshTime);
            Toast.makeText(getInstance(), "Get data from excel", Toast.LENGTH_SHORT).show();
            if(settingParas != null) {
                Integer temp = Integer.parseInt(settingParas.get("default_keeptime_edit").toString().trim());
                if(runTime >= temp) {
                    stopGetDataFromDb();
                }
            }
        }
    };

    public void startGetDataFromDb() {//运行
        showRunType.setText("运行");
        if(settingParas.get("default_temp_edit") != null) {
            showTempText.setText(settingParas.get("default_temp_edit")+"℃");
        }
        if(settingParas.get("hex_graph_choice").equals("false")) {//如果设置页面没有勾选采集hex通道数据，则这里无法点击选择
            mGraphFg.setHexCheckboxFalse();
        } else if(settingParas.get("hex_graph_choice").equals("true")) {
            mGraphFg.setHexCheckboxTrue();
        }
        handler.post(runnable);
    }

    public void stopGetDataFromDb() {//停止
        showRunType.setText("停止");
        handler.removeCallbacks(runnable);
        runTime = 0;
    }

}
