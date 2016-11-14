package com.feicuiedu.treasure.commons;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * activity工具类
 */
public class ActivityUtils {
    // 弱引用
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;
    //
    private Toast toast; //吐司
    //
    public ActivityUtils(Activity activity) { //活动
        activityWeakReference = new WeakReference<>(activity);
    }

    public ActivityUtils(Fragment fragment){ //碎片
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    private @Nullable
    Activity getActivity() { //获取活动

        if (activityWeakReference != null)
            return activityWeakReference.get();//返回获得一个所指的对象
        if (fragmentWeakReference != null) {
            Fragment fragment = fragmentWeakReference.get();  //获得一个所指的对象
            return fragment == null? null : fragment.getActivity();
        }
        return null;
    }

    /**
     * 展示吐司
     * @param msg  传入的字符序列
     */
    public void showToast(CharSequence msg){
        Activity activity = getActivity();
        if (activity != null){
            if (toast == null)
                toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
            toast.setText(msg); //设置展示的文字
            toast.show();//展示
        }
    }

    /**
     * 展示吐司
     * @param resId  传入的id
     */
    @SuppressWarnings("SameParameterValue")
    public void showToast(int resId){
        Activity activity = getActivity();
        if (activity != null) {
            String msg = activity.getString(resId); //（获得String型）
            showToast(msg);  //调上面的吐司窗口
        }
    }

    /**
     * 启动跳转Activity（活动）
     * @param clazz  要跳转的Activity
     */
    public void startActivity(Class<? extends Activity> clazz){
        Activity activity = getActivity();
        if (activity == null) return;//如果获取为空 直接 return
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
    }

    /**
     * Unfortunately Android doesn't have an official API to retrieve the height ofStatusBar.
     * This is just a way to hack around, may not work on some devices.
     * 不幸的是Android没有一个官方的API来检索状态栏的高度。
     * 这只是闲逛,可能不会在一些设备上工作。
     *
     * @return The height of StatusBar.
     *  获取状态栏的高度
     */
    public int getStatusBarHeight() {
        Activity activity = getActivity();
        if (activity == null) return 0;
        //获取资源
        Resources resources = getActivity().getResources();
        int result = 0; //结果
        //资源Id
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId); //容器像素大小
        }
        LogUtils.v("getStatusBarHeight: " + result);
        return result;
    }
//获取屏幕的宽
    public int getScreenWidth() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        DisplayMetrics metrics = new DisplayMetrics(); //显示指标
        //获得窗口默认显示指标
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels; //在像素的绝对宽度显示
    }
    //获取屏幕的高
    public int getScreenHeight() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard(){
        Activity activity = getActivity();
        if (activity == null) return;

        View view = activity.getCurrentFocus(); //获取当前焦点
        if (view != null) {
            //获取系统服务  （输入方法管理器）
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            // 参数1：请求令牌的窗口   参数2： 额外操作的标记 int型
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//隐藏软输入窗口
        }
    }
}
