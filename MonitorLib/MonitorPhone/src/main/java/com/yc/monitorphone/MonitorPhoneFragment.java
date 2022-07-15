package com.yc.monitorphone;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yc.toolutils.AppDeviceUtils;
import com.yc.toolutils.AppSignUtils;
import com.yc.toolutils.AppTimeUtils;
import com.yc.toolutils.AppWindowUtils;
import com.yc.toolutils.BuildConfig;
import com.yc.toolutils.net.AppNetworkUtils;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time  : 2018/5/6
 *     desc  : 查看手机信息
 *     revise:
 * </pre>
 */
public class MonitorPhoneFragment extends Fragment {

    private Activity activity;
    private TextView tvPhoneContent;
    private TextView tvAppInfo;
    private TextView tvContentInfo;
    private TextView tvContentLang;
    private TextView tvContentProcess;
    private TextView tvContentMemory;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_all_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFindViewById(view);
        initData();
    }

    private void initFindViewById(View view) {
        tvPhoneContent = view.findViewById(R.id.tv_phone_content);
        tvAppInfo = view.findViewById(R.id.tv_app_info);
        tvContentInfo = view.findViewById(R.id.tv_content_info);
        tvContentLang = view.findViewById(R.id.tv_content_lang);
        tvContentProcess = view.findViewById(R.id.tv_content_process);
        tvContentMemory = view.findViewById(R.id.tv_content_memory);

    }

    private void initData() {
        initListData();
        //手机设备信息
        setPhoneInfo();
        //设置手机信息
        setAppInfo();
        //本机信息
        //比如mac地址，子网掩码，ip，wifi名称
        setLocationInfo();
        //设置时间和语言
        setTimeAndLang();
    }

    private void initListData() {

    }

    private void setPhoneInfo() {
        Application application = activity.getApplication();
        final StringBuilder sb = new StringBuilder();
        sb.append("是否root:").append(AppDeviceUtils.isDeviceRooted());
        sb.append("\n系统硬件商:").append(AppDeviceUtils.getManufacturer());
        sb.append("\n设备的品牌:").append(AppDeviceUtils.getBrand());
        sb.append("\n手机的型号:").append(AppDeviceUtils.getModel());
        sb.append("\n设备版本号:").append(AppDeviceUtils.getId());
        sb.append("\nCPU的类型:").append(AppDeviceUtils.getCpuType());
        sb.append("\n系统的版本:").append(AppDeviceUtils.getSDKVersionName());
        sb.append("\n系统版本值:").append(AppDeviceUtils.getSDKVersionCode());
        sb.append("\nSd卡剩余控件:").append(AppDeviceUtils.getSDCardSpace(application));
        sb.append("\n系统剩余控件:").append(AppDeviceUtils.getRomSpace(application));
        sb.append("\n手机总内存:").append(AppDeviceUtils.getTotalMemory(application));
        sb.append("\n手机可用内存:").append(AppDeviceUtils.getAvailMemory(application));
        sb.append("\n手机分辨率:").append(AppWindowUtils.getRealScreenHeight(getActivity()))
                .append("x").append(AppWindowUtils.getRealScreenWidth(getActivity()));
        sb.append("\n屏幕尺寸:").append(AppWindowUtils.getScreenInch(getActivity()));
        sb.append("\nAndroidID:").append(AppDeviceUtils.getAndroidID(application));
        tvPhoneContent.setText(sb.toString());
        tvPhoneContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppWindowUtils.copyToClipBoard(activity,sb.toString());
            }
        });
    }


    private void setAppInfo() {
        Application application = activity.getApplication();
        //版本信息
        String versionName = "";
        String versionCode = "";
        try {
            PackageManager pm = application.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(application.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = String.valueOf(pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("软件App包名:").append(application.getPackageName());
        sb.append("\n是否是DEBUG版本:").append(BuildConfig.DEBUG);
        sb.append("\nApp签名:").append(AppSignUtils.getPackageSign());
        if (versionName!=null && versionName.length()>0){
            sb.append("\n版本名称:").append(versionName);
            sb.append("\n版本号:").append(versionCode);
        }
        ApplicationInfo applicationInfo = application.getApplicationInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sb.append("\n最低系统版本号:").append(applicationInfo.minSdkVersion);
            sb.append("\n当前系统版本号:").append(applicationInfo.targetSdkVersion);
            sb.append("\n进程名称:").append(applicationInfo.processName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sb.append("\nUUID:").append(applicationInfo.storageUuid);
            }
            sb.append("\nAPK完整路径:").append(applicationInfo.sourceDir);
            sb.append("\n备份代理:").append(applicationInfo.backupAgentName);
            sb.append("\nclass名称:").append(applicationInfo.className);
            boolean profileableByShell;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                profileableByShell = applicationInfo.isProfileableByShell();
                sb.append("\n是否在分析模式:").append(profileableByShell);
            }
        }
        tvAppInfo.setText(sb.toString());
    }


    private void setLocationInfo() {
        Application application = activity.getApplication();
        StringBuilder sb = new StringBuilder();
        sb.append("wifi信号强度:").append(AppNetworkUtils.getWifiState());
        boolean wifiProxy = AppNetworkUtils.isWifiProxy(application);
        if (wifiProxy){
            sb.append("\nwifi是否代理:").append("已经链接代理");
        } else {
            sb.append("\nwifi是否代理:").append("未链接代理");
        }
        sb.append("\nMac地址:").append(AppDeviceUtils.getMacAddress(application));
        sb.append("\n运营商名称:").append(AppNetworkUtils.getNetworkOperatorName());
        sb.append("\n获取IPv4地址:").append(AppNetworkUtils.getIPAddress(true));
        sb.append("\n获取IPv6地址:").append(AppNetworkUtils.getIPAddress(false));
        sb.append("\n移动网络是否打开:").append(AppNetworkUtils.getMobileDataEnabled());
        sb.append("\n判断网络是否是4G:").append(AppNetworkUtils.is4G());
        sb.append("\nWifi是否打开:").append(AppNetworkUtils.getWifiEnabled());
        sb.append("\nWifi是否连接状态:").append(AppNetworkUtils.isWifiConnected());
        sb.append("\nWifi名称:").append(AppNetworkUtils.getWifiName(application));
        int wifiIp = AppNetworkUtils.getWifiIp(application);
        String ip = AppDeviceUtils.intToIp(wifiIp);
        sb.append("\nWifi的Ip地址:").append(ip);
        DhcpInfo dhcpInfo = AppDeviceUtils.getDhcpInfo(application);
        if (dhcpInfo!=null){
            //sb.append("\nipAddress：").append(AppDeviceUtils.intToIp(dhcpInfo.ipAddress));
            sb.append("\n子网掩码地址：").append(AppDeviceUtils.intToIp(dhcpInfo.netmask));
            sb.append("\n网关地址：").append(AppDeviceUtils.intToIp(dhcpInfo.gateway));
            sb.append("\nserverAddress：").append(AppDeviceUtils.intToIp(dhcpInfo.serverAddress));
            sb.append("\nDns1：").append(AppDeviceUtils.intToIp(dhcpInfo.dns1));
            sb.append("\nDns2：").append(AppDeviceUtils.intToIp(dhcpInfo.dns2));
        }
        tvContentInfo.setText(sb.toString());
    }

    /**
     * 设置时间和语言
     */
    private void setTimeAndLang() {
        Application application = activity.getApplication();
        StringBuilder sb = new StringBuilder();
        sb.append("当前时区:").append(AppTimeUtils.getCurrentTimeZone());
        sb.append("\nMac地址:").append(AppDeviceUtils.getMacAddress(application));
        tvContentLang.setText(sb.toString());
    }

}
