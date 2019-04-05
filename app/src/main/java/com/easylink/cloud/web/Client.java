package com.easylink.cloud.web;

import android.content.Intent;
import android.util.Log;

import com.easylink.cloud.MyApplication;
import com.easylink.cloud.absolute.iUploadListener;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.Task;
import com.easylink.cloud.util.FileTypeUtil;
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
            // 文件夹
            List<ListBucket.CommonPrefixes> list = listBucket.commonPrefixesList;
            for (ListBucket.CommonPrefixes contents : list) {
                String s = contents.prefix.substring(0, contents.prefix.length() - 1);
                String name = s.contains("/") ? s.substring(s.indexOf('/') + 1) : s;
                files.add(new CloudFile(contents.prefix, name, Constant.DIR));
            }

            // 文件
            List<ListBucket.Contents> list2 = listBucket.contentsList;

            for (ListBucket.Contents contents : list2) {
                if (contents.key.equals(prefix)) continue;

                CloudFile file = new CloudFile(contents.key,
                        FileTypeUtil.recognizeName(contents.key),
                        FileTypeUtil.recognizeType(contents.key));
                file.lastModify = contents.lastModified;
                file.size = contents.size;
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
                String s = contents.prefix.substring(0, contents.prefix.length() - 1);
                String name = s.contains("/") ? s.substring(s.indexOf('/') + 1) : s;
                files.add(new CloudFile(contents.prefix, name, Constant.DIR));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public List<CloudFile> queryAllFile(String bucket) {
        List<CloudFile> files = new LinkedList<>();
        final GetBucketRequest getBucketRequest = new GetBucketRequest(bucket);
        getBucketRequest.setMaxKeys(1000); //单次返回的最大数量
        // 使用同步方法
        try {
            GetBucketResult getBucketResult = cosXmlService.getBucket(getBucketRequest);
            ListBucket listBucket = getBucketResult.listBucket;

            // 目录
            List<ListBucket.Contents> list2 = listBucket.contentsList;

            for (ListBucket.Contents contents : list2) {
                CloudFile file = new CloudFile(contents.key,
                        FileTypeUtil.recognizeName(contents.key),
                        FileTypeUtil.recognizeType(contents.key));
                file.lastModify = contents.lastModified;
                file.size = contents.size;
                files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public void upload(iUploadListener listener, Task task) {
        TransferConfig transferConfig = new TransferConfig.Builder().build();// 设置是否分片，分片的大小等
        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);
        String uploadId = null;//prefix + System.currentTimeMillis();//用于续传,若无,则为null
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(Constant.bucket, task.key, task.path, uploadId); //上传文件
        //设置返回结果回调

        cosxmlUploadTask.setCosXmlProgressListener((complete, target) -> {
            int progress = (int) (1.0 * complete / target * 100);
            listener.onProgress(task.key, progress);
        });

        new Thread(() -> {
            while (true) {
                if (task.isPause) cosxmlUploadTask.pause();
                if (task.isCanceled) {
                    cosxmlUploadTask.cancel();
                    return;
                }
                if (task.isResume) cosxmlUploadTask.resume();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Intent intent = new Intent(Constant.BROADCAST_UPLOAD_PROGRESS);
                listener.onSuccess(task.key);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Intent intent = new Intent(Constant.BROADCAST_UPLOAD_PROGRESS);
                listener.onFailed(task.key);
                exception.printStackTrace();
            }
        });

        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(state -> Log.d("TEST", "Task type:" + state.name()));
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
