package yxs.usst.edu.cn.project.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.interface_class.CallbackBundle;
import yxs.usst.edu.cn.project.setting_paras.DevicePath;

/**
 * Created by Administrator on 2016/4/25.
 */
public class OpenFileDialog {

    static final public String sRoot = "/";
    static final public String sParent = "..";
    static final public String sFolder = ".";
    static final public String sEmpty = "";
    static final private String sOnErrorMsg = "No rights to access!";

    public static Dialog staticDialog = null;

    public static List<Map<String, Object>> resultData = null;
    public static String[] para = null;

    private EditText fileName;
    private Button sureCreateBtn, cancelCreateBtn;
    private TextView inputTip;

    public static OpenFileDialog openFileDialog = new OpenFileDialog();

    public static OpenFileDialog getInstance() {
        return openFileDialog;
    }

    // 参数说明
    // context:上下文
    // dialogid:对话框ID
    // title:对话框标题
    // callback:一个传递Bundle参数的回调接口
    // suffix:需要选择的文件后缀，比如需要选择wav、mp3文件的时候设置为".wav;.mp3;"，注意最后需要一个分号(;)
    // images:用来根据后缀显示的图标资源ID。
    //	根目录图标的索引为sRoot;
    //	父目录的索引为sParent;
    //	文件夹的索引为sFolder;
    //	默认图标的索引为sEmpty;
    //	其他的直接根据后缀进行索引，比如.wav文件图标的索引为"wav"
    public Dialog createDialog(final Context context, int type, CallbackBundle callback, String suffix, Map<String, Integer> images) {
        AlertDialog builder = new AlertDialog.Builder(context).create();
        RelativeLayout showView = new RelativeLayout(context);
        final FileSelectView fileSelectView = new FileSelectView(context, callback, suffix, images, type);
        showView.addView(fileSelectView, 0);
        if (type == 2) {//save file
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fileSelectView.getLayoutParams();
            lp.setMargins(0, 0, 0, 200);
            fileSelectView.setLayoutParams(lp);
            final LinearLayout childView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.new_file_dialog, showView, false);//file name when save file button click
            lp = (RelativeLayout.LayoutParams) childView.getLayoutParams();
            lp.setMargins(0, -20, 0, 0);
            showView.addView(childView, 1);
            initNewFileDialog(childView, builder, fileSelectView, context);
        }
        builder.setView(showView);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        staticDialog = builder;
        //dialog.setTitle(title);
        return builder;
    }

    public void initNewFileDialog(final View view, final AlertDialog alertDialog, final FileSelectView fileSelectView, final Context context) {
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
                if (tempText.toString().trim().equals("")) {
                    return;
                }
                if (!mu.validateText(tempText)) {
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
                if (newFile.equals("")) {
                    Toast.makeText(context, "Please input excel file name.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultData == null) {
                    Toast.makeText(context, "You cannot save as another excel when there is none data", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle directroy = fileSelectView.getFileParaBack();
                String filepath = "";
                if (directroy == null) {//没有点击进入,就在程序目录
                    filepath = fileSelectView.getPath();//default directory
                } else {
                    filepath = directroy.getString("path");
                }
                if (filepath != null && !filepath.equals("")) {
                    if (filepath.endsWith(".xls")) {
                        filepath = filepath.substring(0, filepath.lastIndexOf("/"));
                    }
                    mu.createNewExcel(resultData, para, newFile, filepath);
                    resultData = null;
                    alertDialog.dismiss();
                    Toast.makeText(context, "Save as another excel file successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("RD4800", "Cannot get directory");
                }

            }
        });
        cancelCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    static class FileSelectView extends ListView implements OnItemClickListener {
        private CallbackBundle callback = null;
        private String path = DevicePath.getInstance().getLocalPath();
        private List<Map<String, Object>> list = null;
        private String suffix = null;

        private Map<String, Integer> imagemap = null;

        private int type = 0;

        private Bundle fileParas = null;

        public FileSelectView(Context context, CallbackBundle callback, String suffix, Map<String, Integer> images, int type) {
            super(context);
            this.imagemap = images;
            this.suffix = suffix == null ? "" : suffix.toLowerCase();
            this.callback = callback;
            this.type = type;
            this.setOnItemClickListener(this);
            refreshFileList();
        }

        private String getSuffix(String filename) {
            int dix = filename.lastIndexOf('.');
            if (dix < 0) {
                return "";
            } else {
                return filename.substring(dix + 1);
            }
        }

        private int getImageId(String s) {
            if (imagemap == null) {
                return 0;
            } else if (imagemap.containsKey(s)) {
                return imagemap.get(s);
            } else if (imagemap.containsKey(sEmpty)) {
                return imagemap.get(sEmpty);
            } else {
                return 0;
            }
        }

        private int refreshFileList() {
            // 刷新文件列表
            File[] files = null;
            try {
                files = new File(path).listFiles();
            } catch (Exception e) {
                files = null;
            }
            if (files == null) {
                // 访问出错
                Toast.makeText(getContext(), sOnErrorMsg, Toast.LENGTH_SHORT).show();
                return -1;
            }
            if (list != null) {
                list.clear();
            } else {
                list = new ArrayList<Map<String, Object>>(files.length);
            }

            // 用来先保存文件夹和文件夹的两个列表
            ArrayList<Map<String, Object>> lfolders = new ArrayList<Map<String, Object>>();
            ArrayList<Map<String, Object>> lfiles = new ArrayList<Map<String, Object>>();

            if (!this.path.equals(sRoot)) {
                // 添加根目录 和 上一层目录
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", sRoot);
                map.put("path", sRoot);
                map.put("img", getImageId(sRoot));
                list.add(map);

                map = new HashMap<String, Object>();
                map.put("name", sParent);
                map.put("path", path);
                map.put("img", getImageId(sParent));
                list.add(map);
            }

            for (File file : files) {
                if (file.isDirectory() && file.listFiles() != null) {
                    // 添加文件夹
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", file.getName());
                    map.put("path", file.getPath());
                    map.put("img", getImageId(sFolder));
                    lfolders.add(map);
                } else if (file.isFile()) {
                    // 添加文件
                    String sf = getSuffix(file.getName()).toLowerCase();
                    if (suffix == null || suffix.length() == 0 || (sf.length() > 0 && suffix.indexOf("." + sf + ";") >= 0)) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("name", file.getName());
                        map.put("path", file.getPath());
                        map.put("img", getImageId(sf));
                        lfiles.add(map);
                    }
                }
            }

            list.addAll(lfolders); // 先添加文件夹，确保文件夹显示在上面
            list.addAll(lfiles);    //再添加文件


            SimpleAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.file_directory, new String[]{"img", "name", "path"}, new int[]{R.id.filedialogitem_img, R.id.filedialogitem_name, R.id.filedialogitem_path});
            this.setAdapter(adapter);
            return files.length;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // 条目选择
            String pt = (String) list.get(position).get("path");
            String fn = (String) list.get(position).get("name");
            if (fn.equals(sRoot) || fn.equals(sParent)) {
                // 如果是更目录或者上一层
                File fl = new File(pt);
                String ppt = fl.getParent();
                if (ppt != null) {
                    // 返回上一层
                    path = ppt;
                } else {
                    // 返回根目录
                    path = sRoot;
                }
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                bundle.putString("name", fn);
                fileParas = bundle;
            } else {
                File fl = new File(pt);
                if (fl.isFile()) {
                    if (type == 1) {
                        // 如果是文件
                        staticDialog.dismiss();
                    } else if (type == 2) {
                        return;
                    }
                    // 设置回调的返回值
                    Bundle bundle = new Bundle();
                    bundle.putString("path", pt);
                    bundle.putString("name", fn);
                    fileParas = bundle;
                    // 调用事先设置的回调函数
                    this.callback.callback(bundle);
                    return;
                } else if (fl.isDirectory()) {
                    // 如果是文件夹
                    // 那么进入选中的文件夹
                    path = pt;
                }
            }
            this.refreshFileList();
        }

        public Bundle getFileParaBack() {
            return fileParas;
        }

        public String getPath() {
            return path;
        }

    }
}
