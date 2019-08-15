package com.delong.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.ArrayMap;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.delong.common.app.MyApplication;
import com.delong.utils.StreamUtil;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
@SuppressWarnings("ALL")
public class Face {
    private static final ArrayMap<String, Bean> FACE_MAP = new ArrayMap<>();
    private static List<FaceTab> FACE_TABS = null;
    private static final ArrayMap<String,Bitmap> bimaps = new ArrayMap<>();

    private static void init(Context context) {
        if (FACE_TABS == null) {
            synchronized (Face.class) {
                ArrayList<FaceTab> faceTabs = new ArrayList<>();
                FaceTab tab = initAssetsFace(context);
                if (tab != null)
                    faceTabs.add(tab);
                tab = initResourceFace(context);
                if (tab != null)
                    faceTabs.add(tab);
                for (FaceTab faceTab : faceTabs) {
                    faceTab.copyToMap(FACE_MAP);
                }
                FACE_TABS = Collections.unmodifiableList(faceTabs);
            }
        }
    }

    //从zip包中解析我们的表情
    private static FaceTab initAssetsFace(Context context) {
        String faceAsset = "face-t.zip";
        String faceCacheDir = String.format("%s/face/tf", context.getFilesDir());
        File faceFolder = new File(faceCacheDir);
        if (!faceFolder.exists()) {
            if (faceFolder.mkdirs()) {
                try {
                    InputStream inputStream = context.getAssets().open(faceAsset);
                    File faceSource = new File(faceFolder, "source.zip");
                    StreamUtil.copy(inputStream, faceSource);
                    //解压
                    unZipFile(faceSource, faceFolder);
                    //清理文件
                    StreamUtil.delete(faceSource.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //info.json
        File infoFile = new File(faceCacheDir, "info.json");
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = gson.newJsonReader(new FileReader(infoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        FaceTab tab = gson.fromJson(reader, FaceTab.class);
        for (Bean face : tab.faces) {
            face.preview = String.format("%s/%s", faceCacheDir, face.preview);
            face.source = String.format("%s/%s", faceCacheDir, face.source);
        }
        return tab;
    }

    private static void unZipFile(File faceSource, File faceFolder) {
        final String folderPath = faceFolder.getAbsolutePath();
        try {
            ZipFile zf = new ZipFile(faceSource);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("."))
                    continue;
                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + name;

                str = new String(str.getBytes("8859_1"), "GB2312");
                File desFile = new File(str);
                StreamUtil.copy(in, desFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //从drawable中加载数据并映射到对应的key
    private static FaceTab initResourceFace(Context context) {
        final ArrayList<Bean> faces = new ArrayList<>();
        final Resources resources = context.getResources();
        String packageName = context.getApplicationInfo().packageName;
        for (int i = 1; i <= 142; i++) {
            //i=1=>001
            String key = String.format(Locale.ENGLISH, "fb%3d", i);
            String resStr = String.format(Locale.ENGLISH, "face_base_%3d", i);

            int resId = resources.getIdentifier(resStr, "drawable", packageName);
            if (resId == 0)
                continue;
            faces.add(new Bean(key, resId));
        }
        if (faces.size() == 0)
            return null;
        return new FaceTab("NAME", faces.get(0).preview, faces);
    }

    public static List<FaceTab> all(@NonNull Context context) {
        init(context);
        return FACE_TABS;
    }

    public static void inputFace(@NonNull final Context context,
                                 final Editable editable, final Face.Bean bean, final int size) {
        Glide.with(context).load(bean.preview)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(size, size) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Spannable spannable = new SpannableString(String.format("[%s]", bean.key));
                         ImageSpan imageSpan = new ImageSpan(context, resource, ImageSpan.ALIGN_BASELINE);
                         spannable.setSpan(imageSpan, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        editable.append(spannable);
                    }
                });
    }

    //从Spannable解析表情并替换显示
    public static Spannable decode(@NonNull View target,
                                   final Spannable spannable, final int size) {
        if (spannable == null)
            return null;
        String str = spannable.toString();
        if (TextUtils.isEmpty(str))
            return null;

        final Context context = target.getContext();

        Pattern pattern = Pattern.compile("(\\[[^\\[\\]:\\s\\n]+\\])");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            String key = matcher.group();
            if (TextUtils.isEmpty(key))
                continue;
            Bean bean = getBean(context, key.replace("[", "").replace("]", ""));
            if (bean == null)
                continue;

            final int start = matcher.start();
            final int end = matcher.end();
            Bitmap source;

            source = getBitmap(bean);
            ImageSpan imageSpan = new ImageSpan(context, source, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    private static Bitmap getBitmap(Bean bean) {
        Bitmap source;
        if (bimaps.containsKey(bean.key)){
            source = bimaps.get(bean.key);
        }else {
            try {
                source = BitmapFactory.decodeFile((String) bean.preview);
                bimaps.put(bean.key,source);
            }catch (Exception e){
                e.printStackTrace();
                source = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(),(int)bean.preview);
                bimaps.put(bean.key,source);
            }
        }
        return source;
    }


    private static Bean getBean(Context context, String key) {
        if (FACE_MAP.isEmpty())
            init(context);
        if (FACE_MAP.containsKey(key)) {
            return FACE_MAP.get(key);
        }
        return null;
    }



    /**
     * 每一个表情盘，含有很多表情
     */
    public static class FaceTab {
        public FaceTab(String name, Object preview, List<Bean> faces) {
            this.faces = faces;
            this.name = name;
            this.preview = preview;
        }

        public List<Bean> faces = new ArrayList<>();
        public String name;
        //预览
        public Object preview;


        public void copyToMap(ArrayMap<String, Bean> faceMap) {
            for (Bean face : faces) {
                faceMap.put(face.key, face);
            }
        }
    }

    /**
     * 每一个表情
     */
    public static class Bean {
        public Bean(String key, int preview) {
            this.key = key;
            this.source = preview;
            this.preview = preview;
        }
        public String key;
        public Object source;
        public Object preview;
        public String desc;

    }



//    public static class FaceSpan extends ImageSpan {
//        private Drawable mDrawable;
//        private View mView;
//        private int mSize;
//
//        public FaceSpan(@NonNull Context context, final View view, Object source, final int size) {
//            super(context, R.drawable.default_face, ALIGN_BOTTOM);
//            this.mView = view;
//            Glide.with(context)
//                    .load(source)
//                    .centerCrop()
//                    .into(new SimpleTarget<GlideDrawable>() {
//                        @Override
//                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                            mDrawable = resource.getCurrent();
//                            int height = mDrawable.getIntrinsicHeight();
//                            int width = mDrawable.getIntrinsicWidth();
//                            mDrawable.setBounds(0, 0, width > 0 ? width : size, height > 0 ? height : size);
//                            view.invalidate();
//                        }
//                    });
//        }
//
//        @Override
//        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
//            Rect rect = mDrawable!=null?mDrawable.getBounds():new Rect(0,0,mSize,mSize);
//            if (fm != null) {
//                fm.ascent = -rect.bottom;
//                fm.descent = 0;
//
//                fm.top = fm.ascent;
//                fm.bottom = 0;
//            }
//            return rect.right;
//        }
//
//        @Override
//        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
//            if (mDrawable!=null)
//            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
//        }
//
//        @Override
//        public Drawable getDrawable() {
//            return mDrawable;
//        }
//    }


}
