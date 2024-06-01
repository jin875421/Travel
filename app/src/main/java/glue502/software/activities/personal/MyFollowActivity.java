package glue502.software.activities.personal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import glue502.software.R;

public class MyFollowActivity extends AppCompatActivity {

    private RelativeLayout title;
    private ImageView back;
    private TextView tvTitle;
    private SmartRefreshLayout refreshLayout;
    private ListView travelReview;
//    private YourAdapter adapter; // 用你自己的Adapter替换

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);

        // 初始化布局中的组件
        initViews();

        // 设置标题和返回按钮的点击事件
        setClickListeners();

        // 设置ListView的Adapter
//        setListAdapter();

        // 设置SmartRefreshLayout的刷新和加载更多监听
        setRefreshLayoutListener();
    }

    private void initViews() {
        ConstraintLayout rootLayout = findViewById(R.id.root_layout); // 如果有ConstraintLayout的id
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        tvTitle = findViewById(R.id.title_text);
        refreshLayout = findViewById(R.id.refreshLayout);
        travelReview = findViewById(R.id.travel_review);
    }

    private void setClickListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回上一个Activity
            }
        });
    }

//    private void setListAdapter() {
//        // 创建并设置Adapter，此处需要你自定义的Adapter实例
//        adapter = new YourAdapter(this, yourDataList); // 替换yourDataList为实际的数据列表
//        travelReview.setAdapter(adapter);
//    }

    private void setRefreshLayoutListener() {
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    private void initData() {

    }
}