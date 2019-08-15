package com.delong.iweixun.flags.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.delong.common.Common;
import com.delong.common.app.MyApplication;
import com.delong.common.app.PresenterFragment;
import com.delong.common.widget.PortraitView;
import com.delong.factory.presenter.user.UpdateInfoContract;
import com.delong.factory.presenter.user.UpdateInfoPresenter;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.MainActivity;
import com.delong.iweixun.flags.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;
import java.io.File;
import java.util.Objects;
import butterknife.BindView;
import butterknife.OnClick;

@SuppressWarnings("unused")
public class UpdateFragment extends PresenterFragment<UpdateInfoContract.presenter>
        implements UpdateInfoContract.view, Common.Constance {
    private String portraitPath;
    @BindView(R.id.im_portrait)
    PortraitView portraitView;

    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    @BindView(R.id.loading)
    Loading loading;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean isMan = true;

    public UpdateFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_updata;
    }

    @OnClick
    void onPortraitClick() {
        new GalleryFragment().setListener(new GalleryFragment.OnselectedListener() {
            @Override
            public void onSelectedImage(String path) {
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setCompressionQuality(96);
                File dPath = MyApplication.getPortraitTmpFile();
                UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(520, 520)
                        .withOptions(options)
                        .start(Objects.requireNonNull(getActivity()), REQUEST_PORTRAIT);
            }
        }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PORTRAIT) {
            final Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            loadPortrait(resultUri);
        } else {
           showError(R.string.data_rsp_error_unknown);
        }
    }

    private void loadPortrait(Uri resultUri) {
        portraitPath = resultUri.getPath();
        Glide.with(getActivity())
                .load(resultUri)
                .asBitmap()
                .centerCrop()
                .into(portraitView);
    }

    @Override
    public void showError(int strId) {
        super.showError(strId);
        loading.stop();
        portraitView.setEnabled(true);
        mDesc.setEnabled(true);
        mSubmit.setEnabled(true);
        mSex.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        loading.start();
        portraitView.setEnabled(false);
        mDesc.setEnabled(false);
        mSubmit.setEnabled(false);
        mSex.setEnabled(false);
    }

    @OnClick(R.id.im_sex)
    void onSexClick(){
        isMan = !isMan;
        Drawable drawable = getResources().getDrawable(isMan?R.drawable.ic_sex_man
                   :R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        mSex.getBackground().setLevel(isMan?0:1);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String desc = mDesc.getText().toString().trim();
        mPresenter.update(portraitPath,desc,isMan);
    }

    @Override
    protected UpdateInfoContract.presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }

    @Override
    public void updateSuccess() {
        MainActivity.show(Objects.requireNonNull(getContext()));
        Objects.requireNonNull(getActivity()).finish();
    }



}
