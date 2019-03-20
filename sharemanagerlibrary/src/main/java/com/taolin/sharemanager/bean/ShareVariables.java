package com.taolin.sharemanager.bean;

/**
 * 项目名 : ShareManager
 * 包名   : com.pt.mobileapp.bean.sharebean
 * 作者   ：taolin
 * 时间   : 2018/12/21
 * 描述   :  存储分享功能相关变量
 */

public class ShareVariables {

    //分享类型
    public static final int SINGLE_IMAGE    = 0;//单张图片
    public static final int SINGLE_PDF      = 1;//单个（PDF）
    public static final int IMAGES           = 2;//多张图片
    public static final int FILES            = 3;//多个文件（多图除外）
    //微信等应用外分享相关变量
    public class ShareFromApps{
        //解析文件类型key值 ，value类型为string 取值值为 image,document
        public static final String EXTRAS_KEY_FOR_FILE_TYPE          = "type";
        //APP启动场景key值  ，value类型为 enum取值为 app_running_on_other_ui,outside_single_ui,app_running_on_same_ui
        public static final String EXTRAS_KEY_FOR_LAUNCH_SITUATION  = "situation";
    }
    //APP启动场景value值
    public enum ShareFromAppsExtraLaunchSituation{
        APP_RUNNING_ON_OTHER_UI//app启动，且不在文档预览界面
        ,OUTSIED_SINGLE_UI//APP未启动
        ,APP_RUNNING_ON_SAME_UI//APP启动，且在文档预览界面
    }
    //包名相关 分享应用途径
    //eMail
    public static final String EMAIL         ="";
    //QQ
    public static final String QQ_PACKAGE    = "com.tencent.mobileqq";
    //WeChat
    public static final String WECHAT        = "com.tencent.mm";
    //tim
    public static final String TIM           = "com.tencent.tim";
    //sina
    public static final String SINA          = "com.sina.weibo";
    //dingding
    public static final String DINGDING      = "com.alibaba.android.rimet";


    public class QQ{
        //发送给朋友
        public static final String QQ_TO_FRIEND           = "com.tencent.mobileqq.activity.JumpActivity";
        //发送到我的电脑
        public static final String QQ_TO_MY_COMPUTER     = "com.tencent.mobileqq.activity.qfileJumpActivity";
        //面对面快传
        public static final String QQ_FAST_SHATE         = "cooperation.qlink.QlinkShareJumpActivity";
        //添加到QQ收藏
        public static final String QQ_ADD_FAVORITE       = "cooperation.qqfav.widget.QfavJumpActivity";
    }
    public class WeChat{
        //发送给朋友
        public static final String WECHAT_TO_FRIEND      = "com.tencent.mm.ui.tools.ShareImgUI";
        //分享到朋友圈
        public static final String WECHAT_MOMENT         = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
        //添加到微信收藏
        public static final String WECHAT_ADD_FAVORITE   = "com.tencent.mm.ui.tools.AddFavoriteUI";
    }
    public class Tim{
        //发送给朋友
        public static final String TIM_TO_FRIEND          = "com.tencent.mobileqq.activity.JumpActivity";
        //添加到TIM收藏
        public static final String TIM_ADD_FAVORITE       = "cooperation.qqfav.widget.QfavJumpActivity";
    }
    public class DingDing{
        //钉钉
        public static final String DINGDING                = "com.alibaba.android.rimet.biz.BokuiActivity";
    }
}
