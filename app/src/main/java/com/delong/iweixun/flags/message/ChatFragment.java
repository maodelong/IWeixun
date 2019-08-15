package com.delong.iweixun.flags.message;


import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delong.common.app.Fragment;
import com.delong.common.app.MyApplication;
import com.delong.common.app.PresenterFragment;
import com.delong.common.tools.AudioPlayHelper;
import com.delong.face.Face;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.User;
import com.delong.factory.persistence.Account;
import com.delong.factory.presenter.message.ChatContract;
import com.delong.common.widget.EditTextWatcher;
import com.delong.common.widget.PortraitView;
import com.delong.common.widget.recycler.RecyclerAdapter;
import com.delong.factory.utils.FileCache;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.MessageActivity;
import com.delong.iweixun.flags.panel.PanelFragment;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel>, PanelFragment.PanelCallback {
    protected String mReceiverId;
    protected Adapter mAdapter;
    private PanelFragment mPanelFragment;
    private LinearLayoutManager manager;
    private boolean isFist = true;
    private FileCache<AudioHolder> mAudioFileCache;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            smoothScrollToPosition();
        }
    };
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.edit_content)
    EditText edit_content;

    @BindView(R.id.btn_submit)
    ImageView btn_submit;
    private AirPanel.Boss mBoos;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AudioPlayHelper<AudioHolder> mAudioPlayer;

    public ChatFragment() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioPlayer.destroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAudioPlayer = new AudioPlayHelper<>(new AudioPlayHelper.RecordPlayListener<AudioHolder>() {
            @Override
            public void onPlayStart(AudioHolder audioHolder) {
                audioHolder.onPlayStart();
            }

            @Override
            public void onPlayStop(AudioHolder audioHolder) {
                audioHolder.onPlayStop();
            }

            @Override
            public void onPlayError(AudioHolder audioHolder) {
                MyApplication.showToast("播放异常！");
            }
        });


        mAudioFileCache = new FileCache<AudioHolder>(new FileCache.CacheListener<AudioHolder>() {

            @Override
            public void onDownloadSucceed(final AudioHolder audioHolder, final File file) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        mAudioPlayer.trigger(audioHolder, file.getAbsolutePath());
                    }
                });
            }

            @Override
            public void onDownloadFailed(AudioHolder audioHolder) {
                MyApplication.showToast("失败");
            }
        }, "audio/cache", ".mp3");
    }

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initToolbar();
        initAppbar();
        initEditContent();
        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.panel_fragment);
        mPanelFragment.setPanelCallback(this);
        //初始化面板
        mBoos = root.findViewById(R.id.panel_container);
        mBoos.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                Util.hideKeyboard(mRecyclerView);
            }
        });
        mBoos.setOnStateChangedListener(new AirPanel.OnStateChangedListener() {
            @Override
            public void onPanelStateChanged(boolean isOpen) {
                mAppBarLayout.setExpanded(false, true);
            }

            @Override
            public void onSoftKeyboardStateChanged(boolean isOpen) {
                mAppBarLayout.setExpanded(false, true);
                if (isOpen) {
                    smoothScrollToPosition();
                }
            }
        });

        manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Message>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Message message) {
                Util.hideKeyboard(edit_content);
                if (mBoos.isOpen()) {
                    mBoos.closePanel();
                }
            }
        });
    }

    private void smoothScrollToPosition() {
            if (mAdapter.getItemCount() > 0) {
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
    }

    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    private void initEditContent() {
        edit_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessageDelayed(3, 100);
            }
        });

        edit_content.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSend = !TextUtils.isEmpty(content);
                if (needSend) {
                    btn_submit.setActivated(true);
                } else {
                    btn_submit.setActivated(false);
                }
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }


    /**
     * 模拟用户滑动操作
     *
     * @param view 要触发操作的view
     * @param type 模拟操作类型：均匀滑动、快速滑动
     * @param p1x  滑动的起始点x坐标
     * @param p1y  滑动的起始点y坐标
     * @param p2x  滑动的终点x坐标
     * @param p2y  滑动的终点y坐标
     */
    private static void analogUserScroll(View view, final float p1x, final float p1y, final float p2x, final float p2y) {
        Log.e("analogUserScroll>>", "正在模拟滑屏操作：p1->" + p1x + "," + p1y + ";p2->" + p2x + "," + p2y);
        if (view == null) {
            return;
        }
        long downTime = SystemClock.uptimeMillis();//模拟按下去的时间

        long eventTime = downTime;

        float pX = p1x;
        float pY = p1y;
        int speed = 0;//快速滑动
        float touchTime = 116;//模拟滑动时发生的触摸事件次数

        //平均每次事件要移动的距离
        float perX = (p2x - p1x) / touchTime;
        float perY = (p2y - p1y) / touchTime;

        boolean isReversal = perX < 0 || perY < 0;//判断是否反向：手指从下往上滑动，或者手指从右往左滑动
        boolean isHandY = Math.abs(perY) > Math.abs(perX);//判断是左右滑动还是上下滑动

        // if (type == USER_TOUCH_TYPE_1) {//加速滑动
        touchTime = 10;//如果是快速滑动，则发生的触摸事件比均匀滑动更少
        speed = isReversal ? -100 : 100;//反向移动则坐标每次递减
        //  }

        //模拟用户按下
        MotionEvent downEvent = MotionEvent.obtain(downTime, eventTime,
                ACTION_DOWN, pX, pY, 0);
        view.onTouchEvent(downEvent);

        //模拟移动过程中的事件
        List<MotionEvent> moveEvents = new ArrayList<>();
        boolean isSkip = false;
        for (int i = 0; i < touchTime; i++) {

            pX += (perX + speed);
            pY += (perY + speed);
            if ((isReversal && pX < p2x) || (!isReversal && pX > p2x)) {
                pX = p2x;
                isSkip = !isHandY;
            }

            if ((isReversal && pY < p2y) || (!isReversal && pY > p2y)) {
                pY = p2y;
                isSkip = isHandY;
            }
            eventTime += 20.0f;//事件发生的时间要不断递增
            MotionEvent moveEvent = MotionEvent.obtain(downTime, eventTime, ACTION_MOVE, pX, pY, 0);
            moveEvents.add(moveEvent);
            view.onTouchEvent(moveEvent);
            //  if (type == USER_TOUCH_TYPE_1) {//加速滑动
            speed += (isReversal ? -100 : 100);
            //  }
            if (isSkip) {
                break;
            }
        }

        //模拟手指离开屏幕
        MotionEvent upEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_UP, pX, pY, 0);
        view.onTouchEvent(upEvent);

        //回收触摸事件
        downEvent.recycle();
        for (int i = 0; i < moveEvents.size(); i++) {
            moveEvents.get(i).recycle();
        }
        upEvent.recycle();
    }

    /**
     * 模拟用户点击
     *
     * @param view 要触发操作的view
     * @param x    相对于要操作view的左上角x轴偏移量
     * @param y    相对于要操作view的左上角y轴偏移量
     */
    private static void analogUserClick(View view, float x, float y) {
        if (view == null) {
            return;
        }

        long downTime = SystemClock.uptimeMillis();//模拟按下去的时间

        long eventTime = downTime;//事件发生时间

        MotionEvent downEvent = MotionEvent.obtain(downTime, eventTime,
                ACTION_DOWN, x, y, 0);
        view.onTouchEvent(downEvent);

        eventTime = eventTime + 90;//离开屏幕时间

        MotionEvent upEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(upEvent);

        //回收事件
        downEvent.recycle();
        upEvent.recycle();
    }


    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (btn_submit.isActivated()) {
            //发送消息
            String content = edit_content.getText().toString();
            edit_content.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.star();
    }


    @OnClick(R.id.btn_face)
    void onFaceClick() {
        mPanelFragment.showFace();
        airPanelDone();
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        mPanelFragment.showRecord();
        airPanelDone();
    }

    private void onMoreClick() {
        mPanelFragment.showGalley();
        airPanelDone();
    }

    private void airPanelDone() {
        mBoos.openPanel();
        mHandler.sendEmptyMessageDelayed(2, 100);
    }

    @Override
    public RecyclerAdapter<Message> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChange() {
        if (mAdapter.getItemCount() > 0) {
            analogUserScroll(mRecyclerView, 300, 300, 300, 100);
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mBoos.isOpen()) {
            mBoos.closePanel();
            return true;
        }
        return false;
    }

    @Override
    public EditText getInputEditText() {
        return edit_content;
    }

    @Override
    public void onGalleySendClick(String[] selectPtah) {
        mPresenter.pushImages(selectPtah);
    }

    @Override
    public void onRecordDone(File file, long time) {
        mPresenter.pushAudio(file.getAbsolutePath(), time);
    }


    private class Adapter extends RecyclerAdapter<Message> {
        @Override
        protected int getItemViewType(int viewType, Message message) {
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                case Message.TYPE_STR:
                    return !isRight ? R.layout.cell_text_left : R.layout.cell_text_right;
                case Message.TYPE_PIC:
                    return !isRight ? R.layout.cell_pic_left : R.layout.cell_pic_right;
                case Message.TYPE_AUDIO:
                    return !isRight ? R.layout.cell_audio_left : R.layout.cell_audio_right;
                default:
                    return 0;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                case R.layout.cell_text_left:
                case R.layout.cell_text_right:
                    return new TextHolder(root);
                case R.layout.cell_pic_left:
                case R.layout.cell_pic_right:
                    return new PicHolder(root);
                case R.layout.cell_audio_left:
                case R.layout.cell_audio_right:
                    return new AudioHolder(root);
                default:
                    return new TextHolder(root);
            }
        }
    }

    @SuppressWarnings({"NullableProblems", "WeakerAccess"})
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            sender.load();
            Glide.with(ChatFragment.this).load(sender.getPortrait()).centerCrop().into(mPortrait);

            if (mLoading != null) {//说明是右边
                switch (message.getStatus()) {
                    case Message.STATUS_DONE://正常消息
                        mLoading.setVisibility(View.INVISIBLE);
                        mLoading.stop();
                        break;
                    case Message.STATUS_CREATED://正在发送
                        mLoading.setProgress(0);
                        mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                        mLoading.start();
                        break;
                    case Message.STATUS_FAILED://发送失败
                        mLoading.setProgress(1);
                        mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                        mLoading.stop();
                        break;
                }
            }
            mPortrait.setEnabled(message.getStatus() == Message.STATUS_FAILED);

        }

        @OnClick(R.id.im_portrait)
        void onRePushClick() {

        }
    }

    class TextHolder extends BaseHolder {
        @BindView(R.id.tex_content)
        TextView mContent;
        @Nullable
        @BindView(R.id.test)
        FrameLayout test;
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public TextHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            Spannable spannable = new SpannableString(message.getContent());
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));
            mContent.setText(spannable);
        }
    }

    @SuppressWarnings("WeakerAccess")
    class PicHolder extends BaseHolder {
        @BindView(R.id.im_img)
        ImageView mContent;

        public PicHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            String content = message.getContent();
            Glide.with(ChatFragment.this)
                    .load(content)
                    .centerCrop().
                    into(mContent);
        }
    }

    class AudioHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.im_audio_track)
        ImageView mAudioTrack;
        @BindView(R.id.ll_play)
        LinearLayout mLinearLayout;

        public AudioHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            int temp = formatTime(message.getAttach());
            int time = temp == 0 ? 1 : temp;
            mContent.setText(String.format("%s″", time));
            mContent.setWidth((int) Ui.dipToPx(getResources(),20 + 10 * (time - 1)));
        }

        @OnClick(R.id.ll_play)
        void onClickPlay() {
            mAudioFileCache.download(this, mData.getContent());
        }

        void onPlayStart() {
            mAudioTrack.setVisibility(View.VISIBLE);
        }

        void onPlayStop() {
            mAudioTrack.setVisibility(View.INVISIBLE);
        }

        private int formatTime(String attach) {
            int time;
            try {
                time = (int) (Long.valueOf(attach) / 1000);
            } catch (Exception e) {
                e.printStackTrace();
                time = 0;
            }
            return time;
        }
    }
}
