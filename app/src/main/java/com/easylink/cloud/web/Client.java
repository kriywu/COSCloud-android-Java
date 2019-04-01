package com.easylink.cloud.web;

import android.content.Intent;
import android.util.Log;

import com.easylink.cloud.MyApplication;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.FetchTask;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.object.DeleteMultiObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.tag.ListBucket;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.utils.GenerateGetObjectURLUtils;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.util.LinkedList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.easylink.cloud.modle.Constant.secretKey;

public class Client {
    private static CosXmlService cosXmlService = null;

    private Client() {
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(Constant.appId, Constant.region)
                .setDebuggable(true)
                .builder();
        QCloudCredentialProvider credentialProvider = new ShortTimeCredentialProvider(Constant.secretId, secretKey, 300000);
        cosXmlService = new CosXmlService(MyApplication.getContext(), serviceConfig, credentialProvider);
    }

    public static Client getClient() {
        return SingleInstance.client;
    }

    public static class SingleInstance {
        static final Client client = new Client();
    }

    /**
     * 同步
     * 检索路径和文件
     */
    public List<CloudFile> getContentAndPath(String bucket, String prefix, Character delimiter) {
        List<CloudFile> files = new LinkedList<>();
        final GetBucketRequest getBucketRequest = new GetBucketRequest(bucket);
        getBucketRequest.setPrefix(prefix); // 文件夹或文件前缀
        getBucketRequest.setMaxKeys(1000); //单次返回的最大数量
        if (delimiter != null) getBucketRequest.setDelimiter(delimiter); //检索到下一级文件夹
        // 使用同步方法
        try {
            GetBucketResult getBucketResult = cosXmlService.getBucket(getBucketRequest);
            ListBucket listBucket = getBucketResult.listBucket;
            // 返回目录结构
            List<ListBucket.CommonPrefixes> list = listBucket.commonPrefixesList;
            for (ListBucket.CommonPrefixes contents : list) {
                files.add(new CloudFile(contents.prefix, contents.prefix, Constant.DIR));
            }

            // 目录
            List<ListBucket.Contents> list2 = listBucket.contentsList;

            for (ListBucket.Contents contents : list2) {
                if (contents.key.equals(prefix)) continue;

                CloudFile file = new CloudFile(contents.key, contents.key, Constant.FILE);
                file.setLastModify(contents.lastModified);
                file.setSize(contents.size);
                files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;

    }

    /**
     * 检索路径
     */

    public List<CloudFile> getPath(String bucket, String prefix, Character delimiter) {
        List<CloudFile> files = new LinkedList<>();
        final GetBucketRequest getBucketRequest = new GetBucketRequest(bucket);
        getBucketRequest.setPrefix(prefix); // 文件夹或文件前缀
        getBucketRequest.setMaxKeys(1000); //单次返回的最大数量
        getBucketRequest.setDelimiter(delimiter); //检索到下一级文件夹
        // 使用同步方法
        try {
            GetBucketResult getBucketResult = cosXmlService.getBucket(getBucketRequest);
            ListBucket listBucket = getBucketResult.listBucket;
            // 返回目录结构
            List<ListBucket.CommonPrefixes> list = listBucket.commonPrefixesList;
            for (ListBucket.CommonPrefixes contents : list) {
                Log.d("Client", list.size() + "");
                files.add(new CloudFile(contents.prefix, contents.prefix, Constant.DIR));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    /**
     * 异步
     */
    public void upload(final LocalBroadcastManager broadcastManager, final FetchTask task) {
        TransferConfig transferConfig = new TransferConfig.Builder().build();// 设置是否分片，分片的大小等
        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);
        String uploadId = null;//prefix + System.currentTimeMillis();//用于续传,若无,则为null

        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(task.bucket, task.prefix + task.name, task.path, uploadId); //上传文件
        //设置上传进度回调

        // 完成的任务写入文件
        // 没有完成的文件广播
        // 任务ID，任务进度
        cosxmlUploadTask.setCosXmlProgressListener((complete, target) -> {
            float progress = 1.0f * complete / target * 100;
            Log.d("CLIENT", progress + "");
            Intent intent = new Intent(Constant.BROADCAST_UPLOAD_PROGRESS);
            task.progress = progress;
            intent.putExtra(Constant.EXTRA_FETCH_TASK, task);
            broadcastManager.sendBroadcast(intent);
        });

        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Intent intent = new Intent(Constant.BROADCAST_UPLOAD_PROGRESS);
                task.isSuccess = true;
                task.progress = 100f;
                intent.putExtra(Constant.EXTRA_FETCH_TASK, task);
                broadcastManager.sendBroadcast(intent);
                Log.d("CLIENT", "Success");
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Intent intent = new Intent(Constant.BROADCAST_UPLOAD_PROGRESS);
                task.isSuccess = false;
                intent.putExtra(Constant.EXTRA_FETCH_TASK, task);
                broadcastManager.sendBroadcast(intent);
                Log.d("CLIENT", "Failed");
                exception.printStackTrace();
            }
        });

        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(state -> Log.d("TEST", "Task state:" + state.name()));
    }

    /**
     * 删除一个对象对象
     *
     * @param bucket
     * @param key
     */
    public void delObject(String bucket, String key) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, key);
        cosXmlService.deleteObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {

            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
            }
        });
    }


    /**
     * 删除多个对象
     *
     * @param bucket
     * @param lists
     */
    public void delMultiObject(String bucket, List<String> lists) {
        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest(bucket, lists);
        request.setQuiet(true);
        cosXmlService.deleteMultiObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d("Client", "del multi OK");
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d("Clent", "del multi failed");
            }
        });
    }

    /**
     * 生成url
     */
    public String generateUrl(String bucket, String key) {
        try {
            return GenerateGetObjectURLUtils.getObjectUrl(false,
                    Constant.appId,
                    bucket,
                    Constant.region,
                    key);
        } catch (CosXmlClientException e) {
            return null;
        }
    }
}
