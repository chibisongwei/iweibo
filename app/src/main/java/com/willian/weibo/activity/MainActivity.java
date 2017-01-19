package com.willian.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.willian.weibo.R;
import com.willian.weibo.fragment.FragmentController;

public class MainActivity extends FragmentActivity {

    private RadioGroup mRadioGroup;
    private FragmentController mController;

    private ImageView addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        // 默认显示首页选项卡
        mController.showFragment(0);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        mController.showFragment(0);
                        break;
                    case R.id.rb_message:
                        mController.showFragment(1);
                        break;
                    case R.id.rb_search:
                        mController.showFragment(2);
                        break;
                    case R.id.rb_user:
                        mController.showFragment(3);
                        break;
                    default:
                        break;
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, WriteStatusActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
    }

    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mController = FragmentController.getInstance(MainActivity.this, R.id.fl_fragment);

        addImage = (ImageView) findViewById(R.id.iv_add);
    }

    /**
     * 点击Back健不销毁当前Activity，与Home健效果一样
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // true对任何Activity都适用
            // false只对栈底Activity有效
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
