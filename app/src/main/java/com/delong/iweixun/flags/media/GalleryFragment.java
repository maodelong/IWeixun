package com.delong.iweixun.flags.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.delong.common.tools.UiTool;
import com.delong.common.widget.GalleyView;
import com.delong.iweixun.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends BottomSheetDialogFragment implements GalleyView.SelectedChangeListener {

    @BindView(R.id.galleryView)
    GalleyView galleyView;
    private OnselectedListener mListener;

    public GalleryFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusBottomSheedDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        galleyView.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChange(int count) {
        if (count > 0) {
            dismiss();
            if (mListener != null) {
                String[] paths = galleyView.getSelectPtah();
                mListener.onSelectedImage(paths[0]);
                mListener = null;
            }
        }
    }


    public GalleryFragment setListener(OnselectedListener listener) {
        mListener = listener;
        return this;
    }

    public interface OnselectedListener {
        void onSelectedImage(String path);
    }

    public static class TransStatusBottomSheedDialog extends BottomSheetDialog {

        public TransStatusBottomSheedDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheedDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheedDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Window window = getWindow();
            if (window == null) {
                return;
            }
            int screenHight = UiTool.getScreenHeight(getOwnerActivity());
            int statusHight = UiTool.getStatusBarHeight(getOwnerActivity());
            int dialogHight = screenHight - statusHight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHight);
        }
    }


}
