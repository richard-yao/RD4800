package yxs.usst.edu.cn.project.setting_paras;

/**
 * Created by Administrator on 2016/5/5.
 */
public class DevicePath {

    public static DevicePath devicePath = new DevicePath();

    public static String rootPath;//应用程序根目录
    public static String localPath;//存放实验结果的本地路径
    public static String ampDataPath;//存放采集到的扩增数据的路径
    public static String dissolutionPath;//存放采集到的溶解数据的路径

    public static DevicePath getInstance() {
        return devicePath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getAmpDataPath() {
        return ampDataPath;
    }

    public void setAmpDataPath(String ampDataPath) {
        this.ampDataPath = ampDataPath;
    }

    public String getDissolutionPath() {
        return dissolutionPath;
    }

    public void setDissolutionPath(String dissolutionPath) {
        this.dissolutionPath = dissolutionPath;
    }
}
