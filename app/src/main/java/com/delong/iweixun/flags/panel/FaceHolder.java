package com.delong.iweixun.flags.panel;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.delong.common.widget.recycler.RecyclerAdapter;
import com.delong.face.Face;
import com.delong.iweixun.R;

import butterknife.BindView;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
@SuppressWarnings("ALL")
public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean> {
    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if (bean!=null &&
                  ((bean.preview instanceof Integer)
                    || bean.preview instanceof String)){
              Glide.with(mFace.getContext())
                      .load(bean.preview)
                      .asBitmap().format(DecodeFormat.PREFER_ARGB_8888)
                      .into(mFace);

          }
    }


}
