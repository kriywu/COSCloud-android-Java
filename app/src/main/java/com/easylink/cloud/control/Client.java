package com.easylink.cloud.control;

import android.content.Context;
import android.util.Log;


import com.easylink.cloud.modle.EFile;
import com.easylink.cloud.util.Constant;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.tag.ListBucket;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.easylink.cloud.util.Constant.secretKey;

public class Client {
    private static Client client = null;
    private static CosXmlService cosXmlService = null;

    private Client(Context context) {
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(Constant.appId,Constant.region)
                .setDebuggable(true)
                .builder();
        QCloudCredentialProvider credentialProvider = new ShortTimeCredentialProvider(Constant.secretId, secretKey, 3000);
        cosXmlService = new CosXmlService(context, serviceConfig, credentialProvider);
    }

    public static Client getClient(Context context) {
        if (client == null) {
            client = new Client(context);
        }
        return client;
    }

    /**
     *
     * @param bucket
     * @param prefix 检索内容的而前缀，KEY的前缀
     * @param delimiter 定界符 如果定界符为null，则查询所有KEY包含prefix的内容
     */
    public List<EFile> getCurrentFiles(String bucket, String prefix, Character delimiter) {
        List<EFile> files = new LinkedList<>();
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
            for(ListBucket.CommonPrefixes contents : list){
                Log.d("Client",list.size()+"");
                files.add(new EFile(contents.prefix,contents.prefix,Constant.DIR));
            }

            // 文件夹
            List<ListBucket.Contents> list2 = listBucket.contentsList;
            for(ListBucket.Contents contents : list2){
                EFile file = new EFile(contents.key,contents.key,Constant.FILE);
                file.setLastModify(contents.lastModified);
                file.setSize(contents.size);
                files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return files;
        }

    }

    public void upload(File file) {
        TransferConfig transferConfig = new TransferConfig.Builder().build();// 设置是否分片，分片的大小等
        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);
        String cosPath = file.getName(); //KEY
        String srcPath = "本地文件的绝对路径"; // 如 srcPath=Environment.getExternalStorageDirectory().getPath() + "/test.txt";
        String uploadId = "分片上传的UploadId";//用于续传,若无,则为null.
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(Constant.bucket, cosPath, srcPath, uploadId); //上传文件
        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d("TEST", String.format("progress = %d%%", (int) progress));
            }
        });
        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d("TEST", "Success: " + result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d("TEST", "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));
            }
        });
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d("TEST", "Task state:" + state.name());
            }
        });
    }
}
