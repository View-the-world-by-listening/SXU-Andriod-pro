package com.zsh.sight.feature;

import static android.content.Context.MODE_PRIVATE;
import static com.zsh.sight.Utils.pxUtil.dip2px;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsh.sight.MyApplication;
import com.zsh.sight.R;
import com.zsh.sight.Utils.CornerTransform;
import com.zsh.sight.Utils.HttpUtils;
import com.zsh.sight.Utils.ScreenShotUtil;
import com.zsh.sight.Utils.ScreenSizeUtils;
import com.zsh.sight.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Blind_mine_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Blind_mine_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView mine_img_blind;
    private View view;
    private ImageView edit_head, edit_pass, edit_trace, edit_face, exit_button;
    private String username;
    private TrunkActivity trunkActivity;
    private MyApplication myApplication;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton floatingActionButton;

    public Blind_mine_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Blind_mine_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Blind_mine_Fragment newInstance(String param1, String param2) {
        Blind_mine_Fragment fragment = new Blind_mine_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        trunkActivity = (TrunkActivity) getActivity();
        myApplication = (MyApplication) trunkActivity.getApplication();
        username = myApplication.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blind_mine_, container, false);
        mine_img_blind = view.findViewById(R.id.mine_img_blind);
        CornerTransform transformation = new CornerTransform(getContext(),dip2px(getContext(), 20));
        transformation.setExceptCorner(false, true, true, false);
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            String p = HttpUtils.getJsonData(json, "http://121.5.169.147:8000/getHead");
            json = new JSONObject(p);
            Glide.with(getActivity().getApplicationContext()).asBitmap().load(json.getString("path"))
                    .transform(transformation)
                    .into(mine_img_blind);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        edit_head = view.findViewById(R.id.edit_head_blind);
        edit_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customBindDialog();
            }
        });
        edit_pass = view.findViewById(R.id.edit_pass_blind);
        edit_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customBindDialog();
            }
        });
        edit_face = view.findViewById(R.id.edit_face_blind);
        edit_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customBindDialog();
            }
        });
        edit_trace = view.findViewById(R.id.edit_trace_blind);
        edit_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username);
                    String url = "http://121.5.169.147:8000/train";
                    String data = HttpUtils.getJsonData(jsonObject, url);
                    JSONObject jsonObject1 = new JSONObject(data);
                    if(jsonObject1.getString("res").equals("true")){
                        Toast.makeText(getActivity(), "更新完成", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        exit_button = view.findViewById(R.id.exit_login_blind);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_message_box();
            }
        });
        floatingActionButton = view.findViewById(R.id.help_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(trunkActivity, HelpActivity.class);
                Bitmap bitmap = ScreenShotUtil.screenShot(trunkActivity);
                try {
                    String path = ScreenShotUtil.save(bitmap, trunkActivity, Bitmap.CompressFormat.JPEG, true);
                    intent.putExtra("path", path);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        return view;
    }
    private void customBindDialog() {
        final Dialog dialog = new Dialog(trunkActivity, R.style.NormalDialogStyle);
        View view = View.inflate(trunkActivity, R.layout.bind_pro_dialog, null);
        MaterialButton materialButton = (MaterialButton) view.findViewById(R.id.help_button);
        TextInputLayout help_input = (TextInputLayout) view.findViewById(R.id.help_input);
        TextInputEditText help_input_edit = (TextInputEditText) view.findViewById(R.id.help_edit_text);
        ImageView imageView = (ImageView) view.findViewById(R.id.path_back);

        materialButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.e("###", "in click " + help_input_edit.getText().toString());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("helpname", help_input_edit.getText());
                    String data = HttpUtils.getJsonData(jsonObject, "http://121.5.169.147:8000/uploadCare");
                    if(data.equals("100\n")){
                        help_input.setError("请输入正确的被监护人用户名");
                    }
                    else{
                        help_input.setError(null);
                        dialog.dismiss();
                    }
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 设置自定义的布局
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(trunkActivity).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(trunkActivity).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    // 确认弹窗
    private void confirm_message_box(){
        AlertDialog alertDialog1 = new AlertDialog.Builder(trunkActivity)
                .setTitle("注销登录")//标题
                .setMessage("是否确认注销登录？")//内容
                .setIcon(R.drawable.dog)//图标
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences loginInfo = trunkActivity.getSharedPreferences("login_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = loginInfo.edit();
                        editor.remove("last_account");
                        editor.remove("last_password");
                        editor.apply();
                        Intent intent = new Intent(trunkActivity, LoginActivity.class);
                        startActivity(intent);
                        trunkActivity.finish();
                        Toast.makeText(trunkActivity,"注销登录", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog1.show();
    }
}