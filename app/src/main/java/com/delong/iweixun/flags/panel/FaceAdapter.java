package com.delong.iweixun.flags.panel;

import android.view.View;

import com.delong.common.widget.recycler.RecyclerAdapter;
import com.delong.face.Face;
import com.delong.iweixun.R;

import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class FaceAdapter extends RecyclerAdapter<Face.Bean> {
    public FaceAdapter(List<Face.Bean> mDataList, AdapterListener<Face.Bean> mListener) {
        super(mDataList, mListener);
    }

    @Override
    protected int getItemViewType(int viewType, Face.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
