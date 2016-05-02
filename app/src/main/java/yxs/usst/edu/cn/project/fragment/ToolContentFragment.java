package yxs.usst.edu.cn.project.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.media.TransportPerformer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.ListViewListener;
import yxs.usst.edu.cn.project.util.FileItem;
import yxs.usst.edu.cn.project.util.FileItemAdapter;
import yxs.usst.edu.cn.project.util.MyUtil;

/**
 * Created by Administrator on 2016/4/10.
 */
public class ToolContentFragment extends Fragment {

    public String appName;
    Button exportExcel, deleteExcel, clearExcel;
    ListView localFiles, outFiles;
    Map<String, String> selectedFiles = new HashMap<String, String>();

    private ListViewListener listViewListener;
    public void setListViewListener(ListViewListener lvl) {
        this.listViewListener = lvl;
    }
    MyUtil mu = MyUtil.getInstance();
    String localPath = "";
    String sdCardPath = "";
    Integer image = R.mipmap.filedialog_root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.tool_content_fragment, container,false);
        exportExcel = (Button) chatView.findViewById(R.id.exportExcel);
        deleteExcel = (Button) chatView.findViewById(R.id.deleteExcel);
        clearExcel = (Button) chatView.findViewById(R.id.clearExcel);
        localFiles = (ListView) chatView.findViewById(R.id.localFiles);
        outFiles = (ListView) chatView.findViewById(R.id.outFiles);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        appName = listViewListener.getMainContext().getResources().getString(R.string.app_name);
        localPath = Environment.getExternalStorageDirectory().getPath()+"/" + appName;
        sdCardPath = mu.getExtSdCardPath();
        //sdCardPath = mu.getExtSdCardPath() + "/" + appName;
        bindControlBtnListener();
        showFilesList();
    }

    private void bindControlBtnListener() {
        exportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportExcelOut();
            }
        });

        deleteExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocalExcel();
            }
        });

        clearExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLocalExcel();
            }
        });
    }

    private void showFilesList() {//初始化显示文件列表
        //String sdCardPath = "/storage/sdcard1/" + appName;
        showLocalFiles();
        showExtFiles();
    }

    private List<FileItem> directoryFiles(String path, Integer img) {
        List<FileItem> fileItems = new ArrayList<FileItem>();
        File[] files = null;
        try {
            files = new File(path).listFiles();
        } catch (Exception e) {
            Toast.makeText(listViewListener.getMainContext(), e.toString(), Toast.LENGTH_SHORT).show();
            files = null;
        }
        if (files == null) {
            Toast.makeText(listViewListener.getMainContext(), "Get file wrong!", Toast.LENGTH_SHORT).show();
            return null;
        }
        for(File temp:files) {
            if(temp.isDirectory() || temp.listFiles() != null) {
                continue;
            } else if(temp.isFile() && temp.getName().toLowerCase().endsWith(".xls")) {
                FileItem tempFile = new FileItem();
                tempFile.setImages(img);
                tempFile.setFileName(temp.getName());
                fileItems.add(tempFile);
            }
        }
        return fileItems;
    }

    private void showLocalFiles() {
        if(localPath != null) {
            List<FileItem> localList = directoryFiles(localPath, image);
            if(localList != null && localList.size() > 0) {
                FileItemAdapter localAdapter = new FileItemAdapter(listViewListener.getMainContext(), R.layout.file_directory, localList);
                localFiles.setAdapter(localAdapter);
                localFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView fileName = (TextView) view.findViewById(R.id.filedialogitem_name);
                        LinearLayout item = (LinearLayout) view.findViewById(R.id.file_item_id);
                        if(item.getTag() == null || item.getTag().equals("")) {
                            item.setTag("selected");
                            item.setBackgroundColor(Color.CYAN);
                            selectedFiles.put(String.valueOf(position), fileName.getText().toString().trim());
                        } else if(item.getTag().equals("selected")) {
                            item.setTag("");
                            item.setBackgroundColor(Color.TRANSPARENT);
                            selectedFiles.remove(String.valueOf(position));
                        }
                    }
                });
            } else {
                localList = new ArrayList<FileItem>();
                FileItemAdapter localAdapter = new FileItemAdapter(listViewListener.getMainContext(), R.layout.file_directory, localList);
                localFiles.setAdapter(localAdapter);
                TextView tip = new TextView(listViewListener.getMainContext(), null);
                tip.setText("再怎么看也没有了");
                localFiles.setEmptyView(tip);
            }
        }
    }

    private void showExtFiles() {
        if(sdCardPath != null) {
            List<FileItem> extList = directoryFiles(sdCardPath, image);
            if(extList == null || extList.size() == 0) {
                extList = new ArrayList<FileItem>();
                FileItemAdapter localAdapter = new FileItemAdapter(listViewListener.getMainContext(), R.layout.file_directory, extList);
                localFiles.setAdapter(localAdapter);
                TextView tip = new TextView(listViewListener.getMainContext(), null);
                tip.setText("再怎么看也没有了");
                outFiles.setEmptyView(tip);
                return;
            }
            if(extList != null && extList.size() > 0) {
                FileItemAdapter extAdapter = new FileItemAdapter(listViewListener.getMainContext(), R.layout.file_directory, extList);
                outFiles.setAdapter(extAdapter);
            }
        }
    }


    private void exportExcelOut() {
        if(selectedFiles.size() == 0) {
            Toast.makeText(listViewListener.getMainContext(), "Please select files to export out", Toast.LENGTH_SHORT).show();
        } else {
            if(mu.getExtSdCardPath() == null) {
                Toast.makeText(listViewListener.getMainContext(), "Please sure the sdcard is mounted", Toast.LENGTH_SHORT).show();
                return;
            }
            for(String temp:selectedFiles.keySet()) {
                String result = mu.copySdcardFile(localPath+"/"+selectedFiles.get(temp), sdCardPath+"/"+selectedFiles.get(temp));
                Toast.makeText(listViewListener.getMainContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
        showExtFiles();
    }

    private void deleteLocalExcel() {
        if(selectedFiles.size() == 0) {
            Toast.makeText(listViewListener.getMainContext(), "Please select files to export out", Toast.LENGTH_SHORT).show();
        } else {
            for(String temp:selectedFiles.keySet()) {
                File file = new File(localPath+"/"+selectedFiles.get(temp));
                mu.deleteFile(file);
            }
        }
        showLocalFiles();
    }

    private void clearLocalExcel() {
        File file = new File(localPath);
        mu.deleteFile(file);
        showLocalFiles();
    }
}
