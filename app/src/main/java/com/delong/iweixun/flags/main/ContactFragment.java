package com.delong.iweixun.flags.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.delong.common.app.PresenterFragment;
import com.delong.factory.presenter.contact.ContactContract;
import com.delong.factory.presenter.contact.ContactPresenter;
import com.delong.common.widget.EmptyView;
import com.delong.common.widget.PortraitView;
import com.delong.common.widget.recycler.RecyclerAdapter;
import com.delong.factory.model.db.User;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.MessageActivity;
import com.delong.iweixun.activities.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressWarnings("FieldCanBeLocal")
public class ContactFragment extends PresenterFragment<ContactContract.Presenter> implements ContactContract.View{
    @BindView(R.id.main_contact_empty_view)
    EmptyView mEmptyView;
    @BindView(R.id.main_contact_recycler_view)
    RecyclerView mRecyclerView;
    public static RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter<User>() {
            @Override
            protected int getItemViewType(int viewType, User userCard) {
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                 return new ContactFragment.ViewHolder(root);
            }
        };
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                MessageActivity.show(getContext(),user);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        //初始化占位布局
        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
          mPresenter.star();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChange() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<User>{
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView tvName;
        @BindView(R.id.txt_desc)
        TextView tvDesc;

        @SuppressWarnings("WeakerAccess")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @OnClick(R.id.im_portrait)
        void onClickPortrait(){
            PersonalActivity.show(getContext(),mData.getId());
        }

        @Override
        protected void onBind(User user) {
            Glide.with(getContext()).load(user.getPortrait()).centerCrop().into(mPortraitView);
            tvName.setText(user.getName());
            tvDesc.setText(user.getDescription());
        }
    }

}
