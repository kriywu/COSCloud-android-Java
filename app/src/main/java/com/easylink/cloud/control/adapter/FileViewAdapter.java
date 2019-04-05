package com.easylink.cloud.control.adapter;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.absolute.iShowDialog;
import com.easylink.cloud.control.fragment.FileFragment;
import com.easylink.cloud.control.fragment.ShowPhotoDialogFragment;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.util.FileTypeUtil;
import com.easylink.cloud.util.QR;
import com.easylink.cloud.web.Client;
import com.easylink.cloud.web.QueryList;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

public class FileViewAdapter extends RecyclerView.Adapter<BindHolder> {
    private Context context;
    private iQueryList callBack;
    private List data;
    private int FLAG; // viewHolder的类型 1 ,2,3,4
    private RecyclerView.Adapter adapter;

    public FileViewAdapter(Context context, iQueryList callBack, List<Object> data, int FLAG) {
        this.context = context;
        this.callBack = callBack;
        this.data = data;
        adapter = this;
        this.FLAG = FLAG;
    }

    //
    @NonNull
    @Override
    public BindHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_file, viewGroup, false);
        if (FLAG == 0) return new EFileHolder(view);
        else return new PathHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindHolder holder, int i) {
        holder.bind(data.get(i));
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    // 列表页面
    class EFileHolder extends BindHolder {
        private String url;
        private TextView tvName;
        private ImageView imageView;
        private ImageView ivMore;

        EFileHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_filename);
            imageView = itemView.findViewById(R.id.iv_icon);
            ivMore = itemView.findViewById(R.id.iv_more);
        }

        @Override
        public void bind(Object index) {
            final CloudFile file = (CloudFile) index;
            tvName.setText(file.name);
            if (!file.type.equals(Constant.PHOTO))
                imageView.setImageResource(FileTypeUtil.getIconByFileType(file.type));
            else {
                Glide.with(context).load(Client.getClient().generateUrl(Constant.bucket, file.key)).into(imageView);
            }

            url = Client.getClient().generateUrl(Constant.bucket, file.key);

            // 如果是目录
            if (file.type.equals(Constant.DIR)) {
                itemView.setOnClickListener(v -> {
                    new QueryList.Builder(callBack)
                            .setBucket(Constant.bucket)
                            .setPrefix(file.key)
                            .setDelimiter('/')
                            .build().execute();
                    callBack.updatePath(file.key);
                });
            }
            if (file.type.equals(Constant.PHOTO)) {
                imageView.setOnClickListener(v -> {
                    ((iShowDialog) callBack).show(ShowPhotoDialogFragment.newInstance(url), "PHOTO");
                });
            }

            ivMore.setOnClickListener(v -> showMore(file));
        }

        @SuppressLint("WrongConstant")
        void showMore(final CloudFile file) {
            View view = View.inflate(context, R.layout.dialog_more_option, null);
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .setTitle(file.name)
                    .create();
            dialog.show();

            ImageView ivQr = view.findViewById(R.id.iv_qr);
            TextView tvQr = view.findViewById(R.id.tv_op_qr);
            TextView tvShare = view.findViewById(R.id.tv_op_share);
            TextView tvMove = view.findViewById(R.id.tv_op_move);
            TextView tvUrl = view.findViewById(R.id.tv_op_url);
            TextView tvDelete = view.findViewById(R.id.tv_op_remove);
            TextView tvDownload = view.findViewById(R.id.tv_download);

            tvQr.setOnClickListener(v -> {
                if (url == null) {
                    Snackbar.make(view, "生成二维码失败", LENGTH_SHORT).show();
                    return;
                }
                Bitmap bitmap = QR.createBitmap(url, 200, 200);
                ivQr.setVisibility(View.VISIBLE);
                ivQr.setImageBitmap(bitmap);
                ObjectAnimator.ofFloat(ivQr, "scaleX", 0, 1).setDuration(500).start();
                ObjectAnimator.ofFloat(ivQr, "scaleY", 0, 1).setDuration(500).start();

                tvQr.setText("  刷新二维码");
            });

            // 删除文件
            tvDelete.setOnClickListener(v -> {
                Client.getClient().delObject(Constant.bucket, file.key);
                data.remove(getAdapterPosition());
                adapter.notifyItemRemoved(getAdapterPosition());

                dialog.dismiss();
            });

            tvDownload.setOnClickListener(v -> {
                ((FileFragment) callBack).binder.addTask(file.key, PreferenceManager.getDefaultSharedPreferences(context).getString("DOWNLOAD_PATH", null));
                Snackbar.make(tvDownload, "开始下载", BaseTransientBottomBar.LENGTH_SHORT).show();
            });

            // 分享URL
            tvUrl.setOnClickListener(v -> {
                if (url == null) {
                    Snackbar.make(view, "生成二维码失败", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    ClipboardManager copy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newRawUri("URL", Uri.parse(url));
                    copy.setPrimaryClip(data);
                    Snackbar.make(view, "已经复制到剪切板", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            });

            tvShare.setOnClickListener(v -> {
                Bitmap bitmap = QR.createBitmap(url, 200, 200);
                if (bitmap == null) return;
                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, file.name, file.name);
                Uri uri = Uri.parse(path);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);//设置分享行为
                intent.setType("image/*");//设置分享内容的类型
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent = Intent.createChooser(intent, "分享");
                context.startActivity(intent);
                dialog.dismiss();
            });
        }
    }

    class PathHolder extends BindHolder {
        private TextView textView;
        private ImageView imageView;
        private ImageView ivMore;

        PathHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_filename);
            imageView = itemView.findViewById(R.id.iv_icon);
            ivMore = itemView.findViewById(R.id.iv_more);
            ivMore.setVisibility(View.INVISIBLE);
        }

        public void bind(Object index) {
            final CloudFile file = (CloudFile) index;
            textView.setText(file.name);
            imageView.setImageResource(R.drawable.icon_folder);
            textView.setText(file.name);
            itemView.setOnClickListener(v -> {
                new QueryList.Builder(callBack).setBucket(Constant.bucket).setPrefix(file.key).setDelimiter('/').setFlag(1).build().execute();
                callBack.updatePath(file.key);
            });
        }
    }
}
