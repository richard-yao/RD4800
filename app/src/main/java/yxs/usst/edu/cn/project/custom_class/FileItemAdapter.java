package yxs.usst.edu.cn.project.custom_class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import yxs.usst.edu.cn.project.R;
import yxs.usst.edu.cn.project.util.FileItem;

/**
 * Created by Administrator on 2016/5/2.
 */
public class FileItemAdapter extends ArrayAdapter<FileItem> {

    private int resourceId;

    public FileItemAdapter(Context context, int resource, List<FileItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        FileItem fileItem = getItem(position);
        LinearLayout linearLayout = new LinearLayout(getContext());
        String inflater = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
        vi.inflate(resourceId, linearLayout, true);
        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.filedialogitem_img);
        TextView fileName = (TextView) linearLayout.findViewById(R.id.filedialogitem_name);
        imageView.setBackgroundResource(fileItem.getImages());
        fileName.setText(fileItem.getFileName());
        return linearLayout;
    }
}
