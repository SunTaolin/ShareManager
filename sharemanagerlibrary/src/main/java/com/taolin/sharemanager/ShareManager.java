package com.taolin.sharemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.widget.Toast;


import com.taolin.sharemanager.bean.ShareVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 项目名 : ShareManager
 * 包名   : com.pt.mobileapp.presenter.sharepresenter
 * 作者   ：taolin
 * 时间   : 2018/12/20
 * 描述   : 应用内分享管理器(单例)
 * 作用   ：分享文件到第三方平台，相关信息{@link ShareVariables}。目前支持文件格式 PDF ，JPG（扫描文件保存格式），分享平台 QQ，微信
 */

public class ShareManager {
    private static volatile ShareManager shareManager;

    private ShareManager(){}

    public static ShareManager getInstance(){
        if (shareManager == null){
            synchronized (ShareManager.class){
                if (shareManager == null){
                    shareManager = new ShareManager();
                }
            }
        }
        return shareManager;
    }

    /**
     * 分享文件（触发场景：多PDF，PDF图片混选）
     */
    public void shareFiles(Context context,ArrayList<String> filePaths) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        //下面三行分享到邮件是需要，分享到其他社交平台不需要（推测）
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        //目前尝试使用uri有效，直接传文件路径会出现获取资源失败
        //路径转换为uri
        ArrayList<Uri> fileUris = new ArrayList<Uri>();
        //安卓7.0以需要使用fileprovider获取uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //路径转换为uri
            for(int i=0; i<filePaths.size(); i++)
            {
                fileUris.add(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider",new File(filePaths.get(i).toString())));
//                PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"imageUri--->" + fileUris.get(i));
            }
        }else{
            //路径转换为uri
            for(int i=0; i<filePaths.size(); i++)
            {
                fileUris.add(Uri.parse("file://" + filePaths.get(i).toString()));
//                PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"imageUri--->" + fileUris.get(i));
            }
        }
//        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
        intent.setType("application/octet-stream");
        intent.setType("message/rfc882");
        //查询所有可以分享的activity
        //进行筛选
        List<LabeledIntent> targetIntents = filterIntent(context, ShareVariables.FILES,intent,null,fileUris,new String[]{});
        Intent chooseIntent = Intent.createChooser(targetIntents.remove(0),null);
        if (chooseIntent == null) {
            return;
        }
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,targetIntents.toArray(new Parcelable[]{}));
        try {
            context.startActivity(chooseIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "找不到该分享应用组件", Toast.LENGTH_SHORT).show();
        }
        //startActivityForResult(Intent.createChooser(intent, "选择应用"), 1001);;
//        context.startActivity(Intent.createChooser(intent, null));
    }

    /**
     * 分享单个PDF文档（触发场景）
     */
    public void shareSinglePDF(Context context,String pdfPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        //目前尝试使用uri有效，直接传文件路径会出现获取资源失败
        Uri pdfUri;
        //安卓7.0以需要使用fileprovider获取uri，否则会抛出FileUriExposedException异常
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //路径转换为uri
            pdfUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider",new File(pdfPath.toString()));
        }else{
            //路径转换为uri
            pdfUri = Uri.parse("file://" + pdfPath.toString());
        }
//        PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"pdfUri--->" + pdfUri);
//        intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        intent.setType("application/pdf");//设置mime格式为PDF
        //查询所有可以分享的activity
        //进行筛选
        List<LabeledIntent> targetIntents = filterIntent(context,ShareVariables.SINGLE_PDF,intent,pdfUri,null,new String[]{});
//        context.startActivity(Intent.createChooser(intent, null));
        Intent chooseIntent = Intent.createChooser(targetIntents.remove(0),null);
        if (chooseIntent == null) {
            return;
        }
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,targetIntents.toArray(new Parcelable[]{}));
        try {
            context.startActivity(chooseIntent);
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(context, "找不到该分享应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享图片（触发场景单个）
     * @param context 上下文
     */
    public void shareSingleImage(Context context,String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri imageUri;
        //安卓7.0以需要使用fileprovider获取uri，否则会抛出FileUriExposedException异常
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //路径转换为uri
            imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider",new File(imgPath.toString()));
        }else{
            //路径转换为uri
            imageUri = Uri.parse("file://" + imgPath.toString());
        }
//        PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"imageUri--->" + imageUri);
        intent.setType("image/*");//设置mime格式为图片
        //查询所有可以分享的activity
        //进行筛选
        List<LabeledIntent> targetIntents = filterIntent(context,ShareVariables.SINGLE_IMAGE,intent,imageUri,null,new String[]{});

        Intent chooseIntent = Intent.createChooser(targetIntents.remove(0),null);
        if (chooseIntent == null) {
            return;
        }
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,targetIntents.toArray(new Parcelable[]{}));
        //startActivityForResult(Intent.createChooser(intent, "选择应用"), 1001);;

        try {
            context.startActivity(chooseIntent);
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(context, "找不到该分享应用", Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * 分享图片（触发场景多个图片）
     */
    public void shareImages(Context context,ArrayList<String> imgPaths) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");//设置mime格式为图片
//		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
//		intent.putExtra(Intent.EXTRA_SUBJECT, "");
//		intent.putExtra(Intent.EXTRA_TEXT, "");
        //目前尝试使用uri有效，直接传文件路径会出现获取资源失败
        //路径转换为uri
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        //安卓7.0以需要使用fileprovider获取uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //路径转换为uri
            for(int i=0; i<imgPaths.size(); i++)
            {
                imageUris.add(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider",new File(imgPaths.get(i).toString())));
//                PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"imageUri--->" + imageUris.get(i));
            }
        }else{
            //路径转换为uri
            for(int i=0; i<imgPaths.size(); i++)
            {
                imageUris.add(Uri.parse("file://" + imgPaths.get(i).toString()));
//                PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"imageUri--->" + imageUris.get(i));
            }
        }
        //进行筛选
        List<LabeledIntent> targetIntents = filterIntent(context,ShareVariables.IMAGES,intent,null,imageUris,new String[]{});
//        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//        intent.setType("image/*");//设置mime格式为图片
        //startActivityForResult(Intent.createChooser(intent, "选择应用"), 1001);;
//        context.startActivity(Intent.createChooser(intent, null));
        Intent chooseIntent = Intent.createChooser(targetIntents.remove(0),null);
        if (chooseIntent == null) {
            return;
        }
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,targetIntents.toArray(new Parcelable[]{}));

        try {
            context.startActivity(chooseIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "找不到该分享应用组件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 筛选符合条件的分享渠道
     * @param context     上下文
     * @param shareType   分享类型 {@link ShareVariables#SINGLE_IMAGE}单个图片，
     *                             {@link ShareVariables#SINGLE_PDF}单个PDF，
     *                             {@link ShareVariables#IMAGES}多个图片，
     *                             {@link ShareVariables#FILES}多个文件（不包含多个图片）
     * @param intent      需要筛选的intent
     * @param data        单个图片，PDF时传递的数据。分享多个时请置空 null
     * @param datas       多个图片，文件时传递的数据。分享单个时请置空 null
     * @param filters     分享渠道展示（填写支持分享的第三方平台{@link ShareVariables}）目前为空
     * @return
     */
    public List<LabeledIntent> filterIntent(Context context, int shareType, Intent intent, Uri data, ArrayList<Uri> datas, String[] filters){
        //查询所有可以分享的activity
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        List<LabeledIntent> targetShareIntents = new ArrayList<>();
        //分离主流应用包名
        boolean isWeChatInstall   = false;
        boolean isQQInstall       = false;
        boolean isDingdingInstall = false;
        //微信
        List<LabeledIntent> tempWechatShareIntents   = new ArrayList<>();
        //QQ
        List<LabeledIntent> tempQQShareIntents       = new ArrayList<>();
        //钉钉
        List<LabeledIntent> tempDingDingShareIntents = new ArrayList<>();
        //其他应用
        List<LabeledIntent> tempOtherAppShareIntents = new ArrayList<>();
        int targetPos = 0;//记录微信是否安装，指定显示位置
        if (!resolveInfos.isEmpty()){
            //判断微信是否安装
            for (int i=0;i<resolveInfos.size();i++){
                //打印所有符合条件的APP
//                PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"packageName--->" + resolveInfos.get(i).activityInfo.packageName + "  name--->" + resolveInfos.get(i).activityInfo.name);
                if (resolveInfos.get(i).activityInfo.packageName.equals(ShareVariables.WECHAT)){
                    isWeChatInstall = true;
                }
                if (resolveInfos.get(i).activityInfo.packageName.equals(ShareVariables.QQ_PACKAGE)){
                    isQQInstall     = true;
                }
                if (resolveInfos.get(i).activityInfo.packageName.equals(ShareVariables.DINGDING)){
                    isDingdingInstall = true;
                }
            }
            //过滤选择非邮件应用
            for (ResolveInfo resolveInfo : resolveInfos){
                Intent target = new Intent();//设置分享行为 单个文件、图片
                /**
                 * 添加分享内容
                 * 注意:单个文件ACTION_SEND调用{@link Intent#putExtra}方法，
                 * 多个ACTION_SEND_MULTIPLE调用{@link Intent#putParcelableArrayListExtra}方法
                 */
                switch (shareType){
                    case ShareVariables.SINGLE_IMAGE://单个图片
                        target.setAction(Intent.ACTION_SEND);
                        target.putExtra(Intent.EXTRA_STREAM,data);
                        target.setType("image/*");//设置分享内容mime类型
                        break;
                    case ShareVariables.SINGLE_PDF://单个文件
                        target.setAction(Intent.ACTION_SEND);
                        target.putExtra(Intent.EXTRA_STREAM,data);
                        target.setType("application/pdf");//设置分享内容mime类型
                        break;
                    case ShareVariables.IMAGES://多个图片
                        target.setAction(Intent.ACTION_SEND_MULTIPLE);
                        target.putParcelableArrayListExtra(Intent.EXTRA_STREAM,datas);
                        target.setType("image/*");//设置分享内容mime类型
                        break;
                    case ShareVariables.FILES://多个文件(多图除外)
                        target.setAction(Intent.ACTION_SEND_MULTIPLE);
                        target.putParcelableArrayListExtra(Intent.EXTRA_STREAM,datas);
                        target.setType("application/octet-stream");//设置分享内容mime类型
                        target.setType("message/rfc882");
                        break;
                }

                ActivityInfo activityInfo = resolveInfo.activityInfo;
                PackageManager pm = ((Activity)context).getApplication().getPackageManager();
                 //打印所有符合条件的APP
//                 PrintLogCat.printLogCat(CommonFunction.getClassNameAndMethodNameAndLineNumberInfo()+"packageName--->" + activityInfo.packageName + "  name--->" + activityInfo.name+" label--->"+activityInfo.applicationInfo.loadLabel(pm).toString()+" activity label--->"+activityInfo.loadLabel(pm).toString()+" unknow label--->"+resolveInfo.loadLabel(pm).toString());
                /**
                 * 获取activityinfo类中的基本信息 如 包名 activity名称，应用label activity label等
                 *  {@link activityInfo.packageName}                     应用包名,对应{@link applicationId 属性}
                 *  {@link activityInfo.name}                            activity名称，对应{@link <activity>}中的{@linkplain android:name 属性}
                 *  {@link activityInfo.applicationInfo.loadLabel(pm)};  应用名称,对应{@link <application>}中的{@linkplain android:label 属性}
                 *  {@link activityInfo.loadLabel(pm)};                  activity名称（未设置默认为应用名称），对应{@link <activity>}中的{@linkplain android.label属性}
                 *  {@link resolveInfo.loadLabel(pm)};                   intent名称(未设置默认为前两个中优先级高的),对应{@link <intent-filter>}中的{@linkplain android:label属性}
                 */
                //微信
                if (activityInfo.packageName.contains(ShareVariables.WECHAT)){
//                    if (resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString().equals("微信")&&!resolveInfo.loadLabel(pm).toString().contains("添加到微信收藏")) {
                    target.setPackage(activityInfo.packageName);
                    target.setClassName(activityInfo.packageName, activityInfo.name);
                    /**
                     * 由于直接传递intent 会导致微信QQ等部分（为intent-filter设置标签）的APP标签，在分享途径中丢失
                     * （很多应用是为activity设置label，并没有为intent-filter设置标签），
                     * 所以需要为传递intent的添加筛选出的标签。
                     */
                    LabeledIntent targeted = new LabeledIntent(target,activityInfo.packageName,resolveInfo.loadLabel(pm),resolveInfo.icon);
//                    if (activityInfo.name.equals(ShareVariables.WeChat.WECHAT_TO_FRIEND)){
//                        targetShareIntents.add(targetPos,targeted);
//                    }
//                    if (activityInfo.name.equals(ShareVariables.WeChat.WECHAT_MOMENT)){
//                        if (shareType == ShareVariables.SINGLE_IMAGE) {
//                            targetShareIntents.add(targetPos+1, targeted);
//                        }
//                    }
                    if (shareType == ShareVariables.SINGLE_IMAGE) {
                        if (activityInfo.name.equals(ShareVariables.WeChat.WECHAT_TO_FRIEND)) {
                            tempWechatShareIntents.add(0, targeted);
                        }
                        if (activityInfo.name.equals(ShareVariables.WeChat.WECHAT_MOMENT)) {
                            tempWechatShareIntents.add(targeted);
                        }
                    }else{
                        if (activityInfo.name.equals(ShareVariables.WeChat.WECHAT_TO_FRIEND)) {
                            tempWechatShareIntents.add(targeted);
                        }
                    }
                }
//                //qq
                if (activityInfo.packageName.contains(ShareVariables.QQ_PACKAGE)){
//                    if (resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString().equals("微信")&&!resolveInfo.loadLabel(pm).toString().contains("添加到微信收藏")) {
                    target.setPackage(activityInfo.packageName);
                    target.setClassName(activityInfo.packageName, activityInfo.name);
                    /**
                     * 由于直接传递intent 会导致微信QQ等部分（为intent-filter设置标签）的APP标签，在分享途径中丢失
                     * （很多应用是为activity设置label，并没有为intent-filter设置标签），
                     * 所以需要为传递intent的添加筛选出的标签。
                     */
                    LabeledIntent targeted = new LabeledIntent(target,activityInfo.packageName,resolveInfo.loadLabel(pm),resolveInfo.icon);
                    if (activityInfo.name.equals(ShareVariables.QQ.QQ_TO_FRIEND)){
//                        if (targetPos == 0) {
//                            targetShareIntents.add(1, targeted);
//                        } else {
//                            if (shareType == ShareVariables.SINGLE_IMAGE) {
//                                targetShareIntents.add(targetPos + 2, targeted);
//                            }else {
//                                targetShareIntents.add(targetPos + 1, targeted);
//                            }
//                        }
                        tempQQShareIntents.add(targeted);
                    }
//                        targetShareIntents.add(targeted);
//                    }
                }
//                //钉钉
                if (activityInfo.packageName.contains(ShareVariables.DINGDING)){
//                    if (resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString().equals("微信")&&!resolveInfo.loadLabel(pm).toString().contains("添加到微信收藏")) {
                    target.setPackage(activityInfo.packageName);
                    target.setClassName(activityInfo.packageName, activityInfo.name);
                    /**
                     * 由于直接传递intent 会导致微信QQ等部分（为intent-filter设置标签）的APP标签，在分享途径中丢失
                     * （很多应用是为activity设置label，并没有为intent-filter设置标签），
                     * 所以需要为传递intent的添加筛选出的标签。
                     */
                    LabeledIntent targeted = new LabeledIntent(target,activityInfo.packageName,resolveInfo.loadLabel(pm),resolveInfo.icon);
                    if (activityInfo.name.equals(ShareVariables.DingDing.DINGDING)){
                        tempDingDingShareIntents.add(targeted);
                    }
//                        targetShareIntents.add(targeted);
//                    }
                }
                if (activityInfo.packageName.contains("mail") || activityInfo.packageName.contains("com.android.")
                        ||activityInfo.packageName.contains("share")){
                    target.setPackage(activityInfo.packageName);
                    target.setClassName(activityInfo.packageName, activityInfo.name);
                    /**
                     * 由于直接传递intent 会导致微信QQ等部分（为intent-filter设置标签）的APP标签，在分享途径中丢失
                     * （很多应用是为activity设置label，并没有为intent-filter设置标签），
                     * 所以需要为传递intent的添加筛选出的标签。
                     */
                    LabeledIntent targeted = new LabeledIntent(target,activityInfo.packageName,resolveInfo.loadLabel(pm),resolveInfo.icon);
                    tempOtherAppShareIntents.add(targeted);
                }

                /**
                 * 由于直接传递intent 会导致微信QQ等部分（为intent-filter设置标签）的APP标签，在分享途径中丢失
                 * （很多应用是为activity设置label，并没有为intent-filter设置标签），
                 * 所以需要为传递intent的添加筛选出的标签。
                 */
//                LabeledIntent targeted = new LabeledIntent(target,activityInfo.packageName,resolveInfo.loadLabel(pm),resolveInfo.icon);
//                targetShareIntents.add(targeted);
            }
            //chooseActivity 会移除第一个应用选择并在末尾添加，这里将首个应用渠道随机添加，在末尾相应少添加该应用渠道保证
            targetShareIntents.add(tempOtherAppShareIntents.size()==0? new ArrayList<LabeledIntent>(1).get(0) :tempOtherAppShareIntents.get(0));
            targetShareIntents.addAll(tempWechatShareIntents);
            targetShareIntents.addAll(tempQQShareIntents);
            targetShareIntents.addAll(tempDingDingShareIntents);
            targetShareIntents.addAll(tempOtherAppShareIntents.size()==0 ? new ArrayList<LabeledIntent>() : tempOtherAppShareIntents.subList(1,tempOtherAppShareIntents.size()));
        }
        //返回筛选结果
        return  targetShareIntents;
    }
}
