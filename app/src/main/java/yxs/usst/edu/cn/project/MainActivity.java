package yxs.usst.edu.cn.project;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
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
                mViewPager.setCurrentItem(0);
                resetTextView(0);
            }
        });
        settingFeature = (Button) this.findViewById(R.id.settingFeature);
        settingFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
                resetTextView(1);
            }
        });
        graphFeature = (Button) this.findViewById(R.id.graphFeature);
        graphFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
                resetTextView(2);
            }
        });
        resultFeature = (Button) this.findViewById(R.id.resultFeature);
        resultFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(3);
                resetTextView(3);
            }
        });
        toolFeature = (Button) this.findViewById(R.id.toolFeature);
        toolFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(4);
                resetTextView(4);
            }
        });

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
                /*fileDialogFragment.setSetExcelPath(new FileDialogFragment.SetExcelPath() {
                    @Override
                    public void excelPath(String path, String name) {
                        resultFragmentData(path, name);
                    }
                });*/
                fileDialogFragment.show(getSupportFragmentManager(), "Save file");
            }
        });

        mSettingFg = new SettingContentFragment();
        mGraphFg = new GraphContentFragment();
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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView(position);
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

}
