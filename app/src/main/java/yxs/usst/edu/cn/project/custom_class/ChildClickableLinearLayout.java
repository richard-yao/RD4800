package yxs.usst.edu.cn.project.custom_class;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/5/5.
 */
public class ChildClickableLinearLayout extends LinearLayout{

    //子控件是否可以接受点击事件
    private boolean childClickable = true;

    public ChildClickableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildClickableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChildClickableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ChildClickableLinearLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //返回true则拦截子控件所有点击事件
        return !childClickable;
    }

    public void setChildClickable(boolean clickable) {
        childClickable = clickable;
    }
}
