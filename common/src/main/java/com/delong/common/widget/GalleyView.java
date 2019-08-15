package com.delong.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.delong.common.R;
import com.delong.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class GalleyView extends RecyclerView {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Image> mSelectImages = new LinkedList<>();
    private static  final int MAX_IMAGE_COUNT =3;
    private static  final  int LOADER_ID= 0X0100;
    private static  final  int MIN_IMAGE_FILE_SIZE= 10*1024;
    private Adapter mAdapter = new Adapter();
    private LoaderCallback mLoaderCallback = new LoaderCallback();
    private SelectedChangeListener mListener;

    public GalleyView(Context context) {
        super(context);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
           setLayoutManager(new GridLayoutManager(getContext(),4));
           setAdapter(mAdapter);
           mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
               @Override
               public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                   if (onItemSelectClick(image)){
                       //noinspection unchecked
                       holder.upData(image);
                           }
               }
           });
    }

    public void setup(LoaderManager loaderManager, SelectedChangeListener mListener){
        loaderManager.initLoader(LOADER_ID,null,mLoaderCallback);
        this.mListener = mListener;
    }


    private boolean onItemSelectClick(Image image){
        boolean notifyRefresh;
        if (mSelectImages.contains(image)){
            mSelectImages.remove(image);
            image.isSelect =false;
            notifyRefresh = true;
        }else{
            if (mSelectImages.size()>=MAX_IMAGE_COUNT){
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                str = String.format(str,MAX_IMAGE_COUNT);
                Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
                notifyRefresh =false;
            }else{
                mSelectImages.add(image);
                image.isSelect=true;
                notifyRefresh= true;
            }
        }
        if (notifyRefresh){
            notifySelectChanged();
        }
        return true;
    }


    private void notifySelectChanged(){
        SelectedChangeListener listener = mListener;
        if (listener!=null){
            listener.onSelectedCountChange(mSelectImages.size());
        }
    }


    private static class Image{
        int id;
        String path;
        long date;
        boolean isSelect;

        @SuppressWarnings("WeakerAccess")
        public Image(int id, String path, long date) {
            this.id = id;
            this.path = path;
            this.date = date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Image image = (Image) o;
            return Objects.equals(path, image.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }

    private  class Adapter extends RecyclerAdapter<Image>{
        @Override
        protected int getItemViewType(int viewtype, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new VieHolder(root);
        }

    }

    private  class VieHolder extends RecyclerAdapter.ViewHolder<Image>{
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;
        VieHolder(@NonNull View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(mPic);
            mShade.setVisibility(image.isSelect?VISIBLE:GONE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }


    @SuppressWarnings({"SingleStatementInBlock", "unused"})
    public String[] getSelectPtah(){
        String[] paths = new String[mSelectImages.size()];
        int index = 0;
        for (Image image : mSelectImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    public void clear(){
        for (Image image : mSelectImages) {
            image.isSelect = false;
        }
        mSelectImages.clear();
        mAdapter.notifyDataSetChanged();
    }


    private void updateSource(List<Image> images){
        mAdapter.replace(images);
    }

    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>{
        private  final  String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {

            if (id == LOADER_ID){
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2]+" DESC");
            }
            //noinspection ConstantConditions
            return null;
        }

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            List<Image> images = new ArrayList<>();
            if (data!=null){
                int count = data.getCount();
                if(count>0){
                    data.moveToFirst();
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate =data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do{
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        Long date = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists()||file.length()<MIN_IMAGE_FILE_SIZE){
                            continue;
                        }
                        Image image = new Image(id,path,date);
                        images.add(image);
                    }while(data.moveToNext());
                }

            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
               updateSource(null);
        }
    }

    public interface  SelectedChangeListener{
        void onSelectedCountChange(int count);
    }
}
