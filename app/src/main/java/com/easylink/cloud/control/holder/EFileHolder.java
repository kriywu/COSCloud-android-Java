package com.easylink.cloud.control.holder;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.web.Client;
import com.easylink.cloud.web.QueryList;
import com.easylink.cloud.modle.Constant;

public class EFileHolder extends BindHolder {
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
                    new QueryList.Builder(context, callBack)
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
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore(file);
            }
        });
    }

    public void showMore(final CloudFile file) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_more_option, null);
        builder.setView(view).setTitle(file.getName()).create();

        final AlertDialog alertDialog = builder.show();

        LinearLayout llRemove = view.findViewById(R.id.op_remove);
        /**
         * 删除文件
         */
        llRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.getClient(context).delObject(Constant.bucket,file.getKey());
                alertDialog.dismiss();
            }
        });
        /**
         *  删除文件夹
         */

    }
}
