package com.easylink.cloud.control.holder;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.web.QueryList;
import com.easylink.cloud.modle.Constant;

public class PathHolder extends BindHolder {
    private TextView textView;
    private ImageView imageView;
    private ImageView ivMore;
    private Context context;
    private iQueryList callBack;

    public PathHolder(Context context, @NonNull View itemView, iQueryList callBack) {
        super(itemView);
        this.context = context;
        this.callBack = callBack;
        textView = itemView.findViewById(R.id.tv_filename);
        imageView = itemView.findViewById(R.id.iv_icon);
        ivMore = itemView.findViewById(R.id.iv_more);
        ivMore.setVisibility(View.INVISIBLE);
    }

    public void bind(Object index) {
        final CloudFile file = (CloudFile) index;

        textView.setText(file.getName());
        if (file.getState().equals(Constant.DIR)) {
            imageView.setImageResource(R.drawable.icon_folder);
        } else {
            imageView.setImageResource(R.drawable.icon_file);
        }
        textView.setText(file.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QueryList.Builder(context, callBack).setBucket(Constant.bucket).setPrefix(file.getName()).setDelimiter('/').setFlag(1).build().execute();
                callBack.updatePath(file.getName());
            }
        });
    }
}
