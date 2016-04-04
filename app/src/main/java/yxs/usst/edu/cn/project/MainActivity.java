package yxs.usst.edu.cn.project;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FileContentFragment fileFrag;
    private SettingContentFragment settingFrag;
    private FileContentFragment graphFrag;
    private FileContentFragment resultFrag;
    private FileContentFragment toolFrag;

    private Button fileBtn;
    private Button settingBtn;
    private Button graphBtn;
    private Button resultBtn;
    private Button toolBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//keep screen light
        setContentView(R.layout.activity_main);

        fileBtn = (Button) findViewById(R.id.fileFeature);
        settingBtn = (Button) findViewById(R.id.settingFeature);
        graphBtn = (Button) findViewById(R.id.graphFeature);
        resultBtn = (Button) findViewById(R.id.resultFeature);
        toolBtn = (Button) findViewById(R.id.toolFeature);

        fileBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

        setDefaultFragment();
    }

    private void setDefaultFragment(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        fileFrag = new FileContentFragment();
        fileBtn.setBackgroundColor(getResources().getColor(R.color.btnColor));
        transaction.replace(R.id.id_content, fileFrag);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.fileFeature:
                if (fileFrag == null) {
                    fileFrag = new FileContentFragment();
                }
                fileBtn.setBackgroundColor(getResources().getColor(R.color.btnColor));
                transaction.replace(R.id.id_content, fileFrag);
                changeTitleBtnColor(R.id.fileFeature);
                break;
            case R.id.settingFeature:
                if(settingFrag == null) {
                    settingFrag = new SettingContentFragment();
                }
                settingBtn.setBackgroundColor(getResources().getColor(R.color.btnColor));
                transaction.replace(R.id.id_content, settingFrag);
                changeTitleBtnColor(R.id.settingFeature);
                break;

        }
        // transaction.addToBackStack();
        // 事务提交
        transaction.commit();
    }
    //change title back button color
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

}
