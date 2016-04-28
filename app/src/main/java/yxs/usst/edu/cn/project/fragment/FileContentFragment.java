package yxs.usst.edu.cn.project.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
    private EditText fileName;
    private Button sureCreateBtn, cancelCreateBtn;
    private TextView inputTip;


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
            }
        });
        //open excel file
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog.createOpenDialog();
            }
        });
        //save result to another file
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog.createSaveDialog();
            }
        });
    }

    private void initNewFileDialog(View view, final AlertDialog alertDialog) {
        fileName = (EditText) view.findViewById(R.id.new_file_name);
        inputTip = (TextView) view.findViewById(R.id.input_file_name_tip);
        sureCreateBtn = (Button) view.findViewById(R.id.sureCreateExcel);
        cancelCreateBtn = (Button) view.findViewById(R.id.cancelCreateExcel);

        TextWatcher textWatcher = new TextWatcher() {
            String tempText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                MyUtil mu = MyUtil.getInstance();
                if(tempText.toString().trim().equals("")) {
                    return;
                }
                if(!mu.validateText(tempText)) {
                    inputTip.setText("文件名称含有非法字符!");
                    inputTip.setTextColor(Color.RED);
                    inputTip.setFocusable(true);
                    inputTip.setFocusableInTouchMode(true);
                    inputTip.requestFocus();
                    sureCreateBtn.setClickable(false);
                } else {
                    sureCreateBtn.setClickable(true);
                    inputTip.setText("");
                }
            }
        };
        fileName.addTextChangedListener(textWatcher);
        sureCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtil mu = MyUtil.getInstance();
                String newFile = fileName.getText().toString().trim();
                if(newFile.equals("")) {
                    return;
                }
                mu.createNewExcel(null, null, newFile, getResources().getString(R.string.app_name));
                Toast.makeText(listViewListener.getMainContext(), "new excel file successfully!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        cancelCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void newDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(listViewListener.getMainContext()).create();
        alertDialog.setTitle("请输入新建excel文件名");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        final View newView = LayoutInflater.from(listViewListener.getMainContext()).inflate(R.layout.new_file_dialog,null);
        alertDialog.setView(newView);
        initNewFileDialog(newView, alertDialog);
        alertDialog.show();

    }
}
