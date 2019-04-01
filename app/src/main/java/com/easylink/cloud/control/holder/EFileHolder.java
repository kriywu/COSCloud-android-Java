package com.easylink.cloud.control.holder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.util.QR;
import com.easylink.cloud.web.Client;
import com.easylink.cloud.web.QueryList;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

public class EFileHolder extends BindHolder {
    private static final String TAG = "EFileHolder";
    private TextView textView;
    private ImageView imageView;
    private ImageView ivMore;
    private Context context;
    private iQueryList callBack;

    public EFileHolder(Context context, @NonNull View itemView, iQueryList callBack) {
        super(itemView);
        this.context = context;
        this.callBack = callBack;
        textView = itemView.findViewById(R.id.tv_filename);
        imageView = itemView.findViewById(R.id.iv_icon);
        ivMore = itemView.findViewById(R.id.iv_more);
    }

    @Override
    public void bind(Object index) {
        final CloudFile file = (CloudFile) index;
        textView.setText(file.getName());

        if (file.getState().equals(Constant.DIR)) {
            imageView.setImageResource(R.drawable.icon_folder);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new QueryList.Builder(callBack)
                            .setBucket(Constant.bucket)
                            .setPrefix(file.getName())
                            .setDelimiter('/')
                            .build().execute();
                    callBack.updatePath(file.getKey());
                }
            });
        } else {
            imageView.setImageResource(R.drawable.icon_file);
        }
        ivMore.setOnClickListener(v -> showMore(file));
    }

    @SuppressLint("WrongConstant")
    public void showMore(final CloudFile file) {
        View view = View.inflate(context, R.layout.dialog_more_option, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(file.getName())
                .create();
        dialog.show();

        ImageView ivQr = view.findViewById(R.id.iv_qr);
        TextView tvQr = view.findViewById(R.id.tv_op_qr);
        TextView tvShare = view.findViewById(R.id.tv_op_share);
        TextView tvMove = view.findViewById(R.id.tv_op_move);
        TextView tvUrl = view.findViewById(R.id.tv_op_url);
        TextView tvDelete = view.findViewById(R.id.tv_op_remove);

        tvQr.setOnClickListener(v -> {
            String url = Client.getClient().generateUrl(Constant.bucket, file.getKey());
            if (url == null) {
                Snackbar.make(view, "生成二维码失败", LENGTH_SHORT).show();
                return;
            }
            Bitmap bitmap = QR.createBitmap(url, 200, 200);
            ivQr.setVisibility(View.VISIBLE);
            Glide.with(context).load(bitmap).into(ivQr);
            tvQr.setText("  刷新二维码");
            Log.d(TAG, "showMore: " + url);
        });

        // 删除文件
        tvDelete.setOnClickListener(v -> {
            Client.getClient().delObject(Constant.bucket, file.getKey());
            dialog.dismiss();
        });

        tvUrl.setOnClickListener(v -> {
            String url = Client.getClient().generateUrl(Constant.bucket, file.getKey());
            if (url == null) {
                Snackbar.make(view, "生成二维码失败", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else {
                ClipboardManager copy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newRawUri("URL", Uri.parse(url));
                copy.setPrimaryClip(data);
                Snackbar.make(view, "已经复制到剪切板", BaseTransientBottomBar.LENGTH_SHORT).show();
                Log.d(TAG, "showMore: " + url);
            }
        });
    }
}
