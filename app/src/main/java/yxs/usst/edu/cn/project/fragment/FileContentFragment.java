package yxs.usst.edu.cn.project.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.CreateDialog;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;

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
}
