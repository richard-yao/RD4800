package yxs.usst.edu.cn.project;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/4/4.
 */
public class TitleFragment extends Fragment{



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.title_fragment, container, false);
    }
}
