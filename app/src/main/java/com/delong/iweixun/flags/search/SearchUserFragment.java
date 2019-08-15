package com.delong.iweixun.flags.search;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delong.common.app.MyApplication;
import com.delong.common.app.PresenterFragment;
import com.delong.common.widget.EmptyView;
import com.delong.common.widget.PortraitView;
import com.delong.common.widget.recycler.RecyclerAdapter;
import com.delong.factory.model.card.UserCard;
import com.delong.factory.presenter.contact.FollowContract;
import com.delong.factory.presenter.contact.FollowPresenter;
import com.delong.factory.presenter.search.SearchContract;
import com.delong.factory.presenter.search.SearchUserPresenter;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.PersonalActivity;
import com.delong.iweixun.activities.SearchActivity;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment,
        SearchContract.UserView {

    @BindView(R.id.search_user_empty_view)
    EmptyView mEmptyView;

    @BindView(R.id.search_user_recycler_view)
    RecyclerView mRecyclerView;

    private ViewHolder mHolder;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initData() {
      mPresenter.search("");
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected int getItemViewType(int viewType, UserCard userCard) {
                return R.layout.cell_search;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                mHolder = new SearchUserFragment.ViewHolder(root);
                return mHolder;
            }
        };

        mRecyclerView.setAdapter(mAdapter);
        //初始化占位布局
        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchUserPresenter(this);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        mAdapter.replace(userCards);
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    public void refresh(UserCard userCard) {
        if (userCard.isFollow()) {
            mAdapter.update(userCard, mHolder);

        }
    }


    @SuppressWarnings("unused")
class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard> implements FollowContract.View {
        @BindView(R.id.im_portrait)
        PortraitView portraitView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.im_follow)
        ImageView mFollow;
        private FollowContract.Presenter mPresenter;

        @OnClick(R.id.im_portrait)
        void onClickPortrait(){
            PersonalActivity.show(getContext(),mData.getId());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick() {
            mPresenter.followUser(mData.getId());
        }

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            Glide.with(getContext()).load(userCard.getPortrait())
                    .centerCrop()
                    .into(portraitView);
            tvName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

        @Override
        public void onFollowSuccess(UserCard userCard) {
            if(mFollow.getDrawable() instanceof LoadingDrawable ){
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
           upData(userCard);
        }

        @Override
        public void showError(int strId) {
            MyApplication.showToast(strId);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);

            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);
            int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
            drawable.setForegroundColor(color);
            mFollow.setImageDrawable(drawable);
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }
    }
}
