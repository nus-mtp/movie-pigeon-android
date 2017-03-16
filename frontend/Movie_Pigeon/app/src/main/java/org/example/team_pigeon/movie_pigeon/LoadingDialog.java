package org.example.team_pigeon.movie_pigeon;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by SHENGX on 2017/3/15.
 */

public class LoadingDialog extends ProgressDialog {
    private Context mContext;
    public LoadingDialog(Context context){
        super(context);
        mContext = context;
    }
    public LoadingDialog(Context context, int theme){
        super(context,theme);
        mContext = context;
    }
    @Override
    protected void onCreate(Bundle save){
        super.onCreate(save);
        init(mContext);
    }
    private void init (Context context){
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

}
