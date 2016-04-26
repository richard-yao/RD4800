package yxs.usst.edu.cn.project.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.CreateDialog;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;
import yxs.usst.edu.cn.project.util.MyUtil;

/**
 * Created by Administrator on 2016/4/10.
 */
public class FileContentFragment extends Fragment {

    private Button newFile,openFile,saveFile;
    private ListViewListener listViewListener;
    private CreateDialog createDialog;

    public void setListViewListener(ListViewListener lvl) {
        this.listViewListener = lvl;
    }
    public void setCreateDialog(CreateDialog createDialog) {
        this.createDialog = createDialog;
    }
    //public FileDialogFragment fileDialogFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.file_content_fragment, container,false);
        newFile = (Button) chatView.findViewById(R.id.newFile);
        openFile = (Button) chatView.findViewById(R.id.openFile);
        saveFile = (Button) chatView.findViewById(R.id.saveFile);
        //fileDialogFragment = new FileDialogFragment();
        //fileDialogFragment.setListViewListener(listViewListener);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        bindOnclickEvent();
    }

    private void bindOnclickEvent() {
        //new excel file
        newFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog();
                //Toast.makeText(listViewListener.getMainContext(), "Click on new file button", Toast.LENGTH_SHORT).show();
            }
        });
        //open excel file
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fileDialogFragment.show(getFragmentManager(), "Open file");
                createDialog.createOpenDialog();
                //Toast.makeText(listViewListener.getMainContext(), "Click on open file button", Toast.LENGTH_SHORT).show();
            }
        });
        //save result to another file
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(listViewListener.getMainContext(), "Click on save file button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void newDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(listViewListener.getMainContext());
        alertDialog.setTitle("请输入新建excel文件名");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        final View newView = LayoutInflater.from(listViewListener.getMainContext()).inflate(R.layout.new_file_dialog,null);
        /*EditText fileName = new EditText(listViewListener.getMainContext());
        fileName.setFilters(mu.validateText());*/
        alertDialog.setView(newView);
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyUtil mu = MyUtil.getInstance();
                EditText fileName = (EditText) newView.findViewById(R.id.new_file_name);
                if(fileName.getText() != null) {
                    String name = fileName.getText().toString().trim();
                    if(mu.validateText(name)) {
                        Toast.makeText(listViewListener.getMainContext(), "RD4800 show edittext success", Toast.LENGTH_SHORT).show();
                        mu.createNewExcel(null, null, fileName.getText().toString().trim(), getResources().getString(R.string.app_name));
                        dialog.dismiss();
                    } else {
                        fileName.setFocusable(true);
                        fileName.setFocusableInTouchMode(true);
                        fileName.requestFocus();
                        TextView tipText = (TextView) newView.findViewById( R.id.input_file_name_tip);
                        tipText.setText("文件名含有非法字符，请重新输入");
                        tipText.setTextColor(Color.RED);
                        return;
                    }
                }

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
}
