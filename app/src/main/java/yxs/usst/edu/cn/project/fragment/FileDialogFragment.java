package yxs.usst.edu.cn.project.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.CallbackBundle;
import yxs.usst.edu.cn.project.util.OpenFileDialog;

/**
 * Created by Administrator on 2016/4/25.
 */
public class FileDialogFragment extends DialogFragment {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public interface SetExcelPath {
        void excelPath(String path, String name);
    }

    public SetExcelPath setExcelPath;

    public void setSetExcelPath(SetExcelPath setExcelPath) {
        this.setExcelPath = setExcelPath;
    }

    public boolean openFile = true;

    public void setOpenFile(boolean flag) {//true, click open file button;false, click save button
        openFile = flag;
    }

    public static List<Map<String, Object>> openResult = null;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Map<String, Integer> images = new HashMap<String, Integer>();
        images.put(OpenFileDialog.sRoot, R.mipmap.filedialog_root);	// 根目录图标
        images.put(OpenFileDialog.sParent, R.mipmap.filedialog_folder_up);	//返回上一层的图标
        images.put(OpenFileDialog.sFolder, R.mipmap.filedialog_folder);	//文件夹图标
        images.put(OpenFileDialog.sEmpty, R.mipmap.filedialog_root);
        OpenFileDialog.para = ResultContentFragment.items;//save data parameters
        Dialog dialog = null;
        if(openFile) {
            dialog = OpenFileDialog.getInstance().createDialog(context, 1, new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            String filepath = bundle.getString("path");
                            String name = bundle.getString("name");//选中的excel文件
                            setExcelPath.excelPath(filepath, name);
                        }
            }, ".xls;", images);
        } else {
            dialog = OpenFileDialog.getInstance().createDialog(context, 2, new CallbackBundle() {
                @Override
                public void callback(Bundle bundle) {
                    Toast.makeText(context, "RD4800 show save file view", Toast.LENGTH_SHORT).show();
                }
            }, ".xls;", images);
            OpenFileDialog.getInstance().resultData = openResult;
        }
        return dialog;
    }

}
