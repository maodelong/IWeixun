package com.delong.iweixun.issist;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delong.common.app.MyApplication;
import com.delong.iweixun.R;
import com.delong.iweixun.flags.media.GalleryFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {
    private static final int RC = 0x0100;
    private static OnSubmit mCallback;
    public PermissionFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new GalleryFragment.TransStatusBottomSheedDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_permission, container, false);
        ButterKnife.bind(this, root);
        refreshState(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (mCallback!=null){
            mCallback.onClickSubmit(requestPerm());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallback = null;
    }

    public interface OnSubmit{
        void onClickSubmit(boolean requestPermIsSucceed);
    }

    private void refreshState(View view) {
        Context context = getContext();

        view.findViewById(R.id.im_sate_permission_network)
                .setVisibility(haveNetworkperm(context) ? View.VISIBLE : View.GONE);

        view.findViewById(R.id.im_sate_permission_write)
                .setVisibility(haveWriteperm(context) ? View.VISIBLE : View.GONE);

        view.findViewById(R.id.im_sate_permission_read)
                .setVisibility(haveReadperm(context) ? View.VISIBLE : View.GONE);

        view.findViewById(R.id.im_sate_permission_record_audio)
                .setVisibility(haveReordAudioperm(context) ? View.VISIBLE : View.GONE);
    }


    private static boolean haveReordAudioperm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveReadperm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveWriteperm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveNetworkperm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE

        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static void show(FragmentManager manager) {
        new PermissionFragment()
                .show(manager, PermissionFragment.class.getName());
    }

    public static boolean hasAll(Context context, FragmentManager manager,OnSubmit callback) {
        mCallback = callback;
        boolean hasAll = haveNetworkperm(context)
                && haveReadperm(context)
                && haveWriteperm(context)
                && haveReordAudioperm(context);

        if (!hasAll) {
            show(manager);
        }
        return hasAll;
    }

    private boolean requestPerm() {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            MyApplication.showToast(R.string.label_permission_ok);
            refreshState(getView());
            return true;
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions), RC, perms);
            return false;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .build()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
