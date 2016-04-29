package yxs.usst.edu.cn.project.interface_class;

import java.util.Map;

/**
 * Created by Administrator on 2016/4/28.
 */
public interface CollectData {

    public void useInstancePara(Map<String, String> paras);

    public void getDataFromDb(Map<String, String> paras);

    public void stopGetData(Map<String, String> paras);
}
