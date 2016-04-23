package yxs.usst.edu.cn.project.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;
import yxs.usst.edu.cn.project.util.MyUtil;

/**
 * Created by Administrator on 2016/4/10.
 */
public class ResultContentFragment extends Fragment{

    private ListView resultLv;

    private ListViewListener listViewListener;

    public void setListViewListener(ListViewListener lvl) {
        this.listViewListener = lvl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.result_content_fragment, container,false);
        resultLv = (ListView) chatView.findViewById(R.id.resultViewShow);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(listViewListener != null) {
            String[] items = {"hole_position", "code", "name", "sex", "age", "project", "dt", "result", "remark"};
            int[] itemId = {R.id.result_holePosition, R.id.result_code, R.id.result_name, R.id.result_sex, R.id.result_age,
                    R.id.result_project, R.id.result_dt, R.id.result_result, R.id.result_remark};
            Toast.makeText(listViewListener.getMainContext(), Environment.getExternalStorageDirectory().getPath()+"/RD4800/", Toast.LENGTH_SHORT).show();
            List<Map<String, Object>> result = getResultData(items);
            SimpleAdapter simpleAdapter = new SimpleAdapter(listViewListener.getMainContext(),result,
                    R.layout.result_list_item, items, itemId);
            resultLv.setAdapter(simpleAdapter);

        }
    }

    private List<Map<String, Object>> getResultData(String[] items) {
        MyUtil mu = MyUtil.getInstance();
        List<Map<String, Object>> result = mu.parseExcel(items, "0.91.xls");
        /*List<Map<String, Object>> result =new ArrayList<Map<String, Object>>();
        int number = 1;
        for(int j=0;j<20;j++) {
            Map<String, Object> temp = new HashMap<String, Object>();
            for(int i=0;i<items.length;i++) {
                if(i == 0) {
                    temp.put(items[i], mu.getNumber(number));
                } else {
                    temp.put(items[i], mu.getTwoPointData(Math.random()));
                }
            }
            result.add(temp);
            number++;
        }
        mu.createNewExcel(result, mu.getTwoPointData(Math.random()), items);*/
        return result;

    }
}
