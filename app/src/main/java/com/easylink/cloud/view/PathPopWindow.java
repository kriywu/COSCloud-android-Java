package com.easylink.cloud.view;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.EditText;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.absolute.iSetUploadPath;
import com.easylink.cloud.adapter.FileViewAdapter;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.absolute.CustomPopupWindow;
import com.easylink.cloud.web.QueryList;

import java.util.ArrayList;
import java.util.List;

public class PathPopWindow extends CustomPopupWindow implements iQueryList {
    private List<CloudFile> files = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText etPath;
    private Button btnAck;

    public PathPopWindow(Builder builder) {
        super(builder);
        recyclerView = contentView.findViewById(R.id.rv_paths);
        etPath = contentView.findViewById(R.id.tv_path);
        btnAck = contentView.findViewById(R.id.btn_ack);

        recyclerView.setAdapter(new FileViewAdapter(mContext, this, (List) files, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        new QueryList.Builder(this).setFlag(1).build().execute();

        btnAck.setOnClickListener(v -> {
            ((iSetUploadPath) mContext).setPath(etPath.getText().toString());
            mPopupWindow.dismiss();
        });
    }

    @Override
    public void updateList(List<CloudFile> fs) {
        files.clear();
        files.addAll(fs);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updatePath(String path) {
        etPath.setText(path);
    }
}
