package com.delong.iweixun.flags.panel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.delong.common.app.Fragment;
import com.delong.common.app.MyApplication;
import com.delong.common.tools.AudioPlayHelper;
import com.delong.common.tools.AudioRecordHelper;
import com.delong.common.tools.UiTool;
import com.delong.common.widget.AudioRecordView;
import com.delong.common.widget.GalleyView;
import com.delong.common.widget.recycler.RecyclerAdapter;
import com.delong.face.Face;
import com.delong.iweixun.R;

import net.qiujuer.genius.ui.Ui;

import java.io.File;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class PanelFragment extends Fragment {
    private View mFacePanel, mGalleryPanel, mRecordPanel;
    private PanelCallback mPanelCallback;


    public PanelFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_panel;
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initFace(root);
        initGallery(root);
        initRecord(root);
    }

    public void setPanelCallback(PanelCallback callback) {
        this.mPanelCallback = callback;
    }


    private void initFace(View root) {
        final View facePanel = mFacePanel = root.findViewById(R.id.lay_panel_face);
        ImageView backspace = facePanel.findViewById(R.id.im_back);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PanelCallback callback = mPanelCallback;
                if (callback == null)
                    return;
                //模拟一个键盘点击
                KeyEvent event = new KeyEvent(0, 0, 0,
                        KeyEvent.KEYCODE_DEL, 0, 0, 0,
                        0, KeyEvent.KEYCODE_ENDCALL);
                callback.getInputEditText().dispatchKeyEvent(event);
            }
        });

        TabLayout tabLayout = facePanel.findViewById(R.id.tab);
        ViewPager viewPager = facePanel.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //每一个表情显示48 dp
        final int minFaceSize = (int) Ui.dipToPx(getResources(), 48);
        final int totalScreen = UiTool.getScreenWidth(Objects.requireNonNull(getActivity()));
        final int spanCount = totalScreen / minFaceSize;

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Face.all(Objects.requireNonNull(getContext())).size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.lay_face_content, container, false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
                //设置adapter
                List<Face.Bean> faces = Face.all(getContext()).get(position).faces;
                FaceAdapter adapter = new FaceAdapter(faces, new RecyclerAdapter.AdapterListenerImpl<Face.Bean>() {
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, Face.Bean bean) {
                        super.onItemClick(holder, bean);
                        if (mPanelCallback == null)
                            return;
                        EditText editText = mPanelCallback.getInputEditText();
                        Face.inputFace(getContext(), editText.getEditableText(), bean,
                                (int) (editText.getTextSize() + (int) Ui.dipToPx(getResources(), 2)));

                    }
                });
                recyclerView.setAdapter(adapter);
                container.addView(recyclerView);
                return recyclerView;

            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                //移除的
                container.removeView((View) object);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return Face.all(Objects.requireNonNull(getContext())).get(position).name;
            }
        });

    }

    private void initRecord(View root) {
        View recordPanel = mRecordPanel = root.findViewById(R.id.lay_panel_record);
        final AudioRecordView audioRecordView = recordPanel.findViewById(R.id.view_audio_record);

        File tempFile = MyApplication.getPortraitTmpFile();
        final AudioRecordHelper helper = new AudioRecordHelper(tempFile, new AudioRecordHelper.RecordCallback() {
            @Override
            public void onRecordStart() {

            }

            @Override
            public void onProgress(long time) {

            }
            @Override
            public void onRecordDone(File file, long time) {
                if (time < 1000)
                    return;
                File audioFile = MyApplication.getAudioTmpFile(false);
                if (file.renameTo(audioFile)) {
                    PanelCallback callback = mPanelCallback;
                    if (callback != null) {
                        callback.onRecordDone(audioFile, time);
                    }
                }
            }
        });
        //初始化
            audioRecordView.setup(new AudioRecordView.Callback() {
                @Override
                public void requestStartRecord() {
                    helper.recordAsync();
                }

                @Override
                public void requestStopRecord(int type) {
                    switch (type) {
                        case AudioRecordView.END_TYPE_CANCEL:
                        case AudioRecordView.END_TYPE_DELETE:
                            helper.stop(true);
                            break;
                        case AudioRecordView.END_TYPE_NONE:
                        case AudioRecordView.END_TYPE_PLAY:
                            helper.stop(false);
                            break;
                    }
                }
            });

    }

    private void initGallery(View root) {
        final View galleryPanel = mGalleryPanel = root.findViewById(R.id.lay_gallery_panel);
        final GalleyView galleyView = galleryPanel.findViewById(R.id.view_gallery);
        final TextView selectSize = galleryPanel.findViewById(R.id.txt_gallery_select_count);
        galleyView.setup(getLoaderManager(), new GalleyView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChange(int count) {
                selectSize.setText(String.format(getText(R.string.label_gallery_selected_size).toString(), count));
            }
        });

        galleryPanel.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleySendClick(galleyView, galleyView.getSelectPtah());
            }
        });
    }

    private void onGalleySendClick(GalleyView galleyView, String[] selectPtah) {
        galleyView.clear();
        PanelCallback callback = mPanelCallback;
        if (callback == null)
            return;
        callback.onGalleySendClick(selectPtah);

    }


    public void showFace() {
        mRecordPanel.setVisibility(View.GONE);
        mFacePanel.setVisibility(View.VISIBLE);
        mGalleryPanel.setVisibility(View.GONE);
    }

    public void showRecord() {
        mRecordPanel.setVisibility(View.VISIBLE);
        mFacePanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.GONE);
    }

    public void showGalley() {
        mFacePanel.setVisibility(View.GONE);
        mRecordPanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.VISIBLE);
    }

    //回调聊天界面callback
    public interface PanelCallback {
        EditText getInputEditText();

        void onGalleySendClick(String[] selectPtah);

        void onRecordDone(File file, long time);
    }
}
