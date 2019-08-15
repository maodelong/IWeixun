package com.delong.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.delong.common.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressWarnings({"ConstantConditions", "unused", "unchecked"})
public abstract class RecyclerAdapter<Data> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>> implements
        View.OnClickListener,View.OnLongClickListener,AdapterCallback<Data> {
    private final List<Data> mDataList;
    private AdapterListener mListener;

    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<Data> mListener) {
       this(new ArrayList<Data>(),mListener);
    }

    public RecyclerAdapter(List<Data> mDataList, AdapterListener<Data> mListener) {
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    /**
     *
     * @param viewGroup RecyclerView
     * @param viewType 界面的类型，约定为XML的
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //得到LayoutInflater 用于初始化XML为view
        LayoutInflater inflater =  LayoutInflater.from(viewGroup.getContext());
        //把ID为viewType 的xml文件初始化为root View
        View root = inflater.inflate(viewType,viewGroup,false);

        //通过子类必须实现的方法，得到一个ViewHolder
        ViewHolder<Data> holder = onCreateViewHolder(root,viewType);

        //设置view的Tag为holder ,进行双向绑定
        root.setTag(R.id.tag_recycler_holder,holder);
        //设置事件点击
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        //进行界面注解绑定
        holder.mUnBinder = ButterKnife.bind(holder,root);
        holder.Callback = this;
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position,mDataList.get(position));
    }

    @LayoutRes
    protected  abstract  int getItemViewType(int viewType,Data data);

    /**
     * 得到一个新的ViewHolder
     * @param root root
     * @param viewType      * @param
     * @return ViewHolder
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root ,int viewType);

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Data> holder, int position) {
        //得到需要的数据
        Data data = mDataList.get(position);
        //触发Holder的绑定方法
        holder.bind(data);
    }


    public List<Data> getItems(){
        return mDataList;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 添加一个数据，并通知插入
     * @param data data
     */
    public void add(Data data){
      mDataList.add(data);
      notifyItemChanged(mDataList.size()-1);
    }

    /**
     * 添加一堆数据，并通知集合更新
     * @param dataList dataList
     */
    public void add(Data... dataList){
        if (dataList!=null && dataList.length>0){
            int starPos = mDataList.size();
            Collections.addAll(mDataList,dataList);
            notifyItemRangeInserted(starPos,dataList.length);
        }
    }


    public void add(Collection<Data>  dataList){
        if (dataList!=null && dataList.size()>0){
            int starPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeInserted(starPos,dataList.size());
        }
    }


    public void clear(){
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换了一个新的集合，其中包括了清空
     * @param dataList dataList
     */
    public void replace(Collection<Data> dataList){
        mDataList.clear();
       if (dataList==null&&dataList.size()==0)
        return;
       mDataList.addAll(dataList);
       notifyDataSetChanged();
    }


    /**
     * 自定义的V
     * @param <Data>
     */
    public static abstract class ViewHolder<Data>  extends RecyclerView.ViewHolder{
        private Unbinder mUnBinder;
        protected Data mData;
        private AdapterCallback<Data> Callback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * 用于绑定数据的触发
         * @param data 绑定的数据
         */
        void bind(Data data){
             mData = data;
            onBind(mData);
        }

        /**
         * 当触发绑定数据时的回调，必须复写
         * @param data data
         */
         protected abstract  void onBind(Data data);

         public void upData(Data data){
             if (this.Callback!=null){
                 this.Callback.update(data,this);
             }
         }
    }

    @Override
    public void update(Data data, ViewHolder<Data> holder) {
        int pos = holder.getAdapterPosition();
        if (pos>=0){
            mDataList.remove(pos);
            mDataList.add(pos,data);
            notifyItemChanged(pos);
        }
    }

    @Override
    public void onClick(View v) {
       ViewHolder holder  = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
       if (this.mListener!=null){
           int pos  = holder.getAdapterPosition();
           //回调方法
           this.mListener.onItemClick(holder,mDataList.get(pos));

       }
    }


    @Override
    public boolean onLongClick(View v) {
        ViewHolder holder  = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListener!=null){
            int pos  = holder.getAdapterPosition();
            //回调方法
            this.mListener.onItemLongClick(holder,mDataList.get(pos));
        }
        return true;
    }

    public interface  AdapterListener<Data>{
        void onItemClick(RecyclerAdapter.ViewHolder holder,Data data);
        void onItemLongClick(RecyclerAdapter.ViewHolder holder,Data data);
    }

    public abstract static  class  AdapterListenerImpl<Data> implements AdapterListener<Data>{

        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, Data data) {

        }
    }

    /**
     * 设置监听
     * @param listener listener
     */
    public void setListener(AdapterListener<Data> listener){
        this.mListener = listener;
    }

}
