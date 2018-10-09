package zoo.hymn.views.pay;

import android.os.Bundle;
import android.view.View;

import com.bm.wb.R;

import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.views.EaseTitleBar;


/**
 * ClassName: PayResultActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/6/26
 */

public class PayResultActivity extends BaseActivity {

    @Override
    protected void initView() {

        findViewById(R.id.btn_go_order).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        ((EaseTitleBar)defaultTitleView).setTitle("支付成功");
    }

    @Override
    public void onClick(View view) {
//        startActivity(new Intent(this,OrderDetailActivity.class)
//                .putExtra("from",PayResultActivity.class.getSimpleName())
//                .putExtra("cutOrderId",getIntent().getStringExtra("cutOrderId"))
//        );
        finish();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        addChildView(R.layout.pay_result_act);
    }
}
