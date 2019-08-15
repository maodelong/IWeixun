package com.delong.iweixun.flags.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delong.common.app.PresenterFragment;
import com.delong.face.Face;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.Session;
import com.delong.factory.presenter.message.SessionContract;
import com.delong.factory.presenter.message.SessionPresenter;
import com.delong.common.widget.EmptyView;
import com.delong.common.widget.PortraitView;
import com.delong.common.widget.recycler.RecyclerAdapter;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.MessageActivity;
import com.delong.iweixun.activities.PersonalActivity;
import com.delong.utils.DateTimeUtils;

import net.qiujuer.genius.ui.Ui;

import butterknife.BindView;
import butterknife.OnClick;


public class ActiveFragment extends PresenterFragment<SessionContract.Presenter> implements SessionContract.View {
    @BindView(R.id.main_contact_empty_view)
    EmptyView mEmptyView;
    @BindView(R.id.main_contact_recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerAdapter<Session> mAdapter;

    public ActiveFragment() {
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.star();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter<Session>() {
            @Override
            protected int getItemViewType(int viewType, Session session) {
                return R.layout.cell_session_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        };
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                MessageActivity.show(getContext(), session);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        //初始化占位布局
        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChange() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItems().size() > 0);
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView tvName;
        @BindView(R.id.txt_desc)
        TextView tvContent;
        @BindView(R.id.txt_time)
        TextView tvTime;

        @SuppressWarnings("WeakerAccess")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @OnClick(R.id.im_portrait)
        void onClickPortrait() {
            PersonalActivity.show(getContext(), mData.getId());
        }

        @Override
        protected void onBind(Session session) {
            Log.e("delong","头像》》》》"+session.getPicture());
            Glide.with(getContext()).load(session.getPicture()).centerCrop().into(mPortraitView);
            tvName.setText(session.getTitle());
            tvTime.setText(DateTimeUtils.getSampleDate(session.getModifyAt()));
            String str = TextUtils.isEmpty(session.getContent()) ? "" : session.getContent();
          switch (session.getMessage().getType()){
              case Message.TYPE_STR:
                  Spannable spannable = new SpannableString(str);
                  Face.decode(tvContent, spannable, (int) Ui.dipToPx(getResources(), 20));
                  tvContent.setText(spannable);
                  break;
              case Message.TYPE_AUDIO:
                  tvContent.setText("[语音]");
              break;
              case  Message.TYPE_PIC:
                  tvContent.setText("[图片]");
                  break;
              default:
                  tvContent.setText(str);
          }
        }
    }
}
