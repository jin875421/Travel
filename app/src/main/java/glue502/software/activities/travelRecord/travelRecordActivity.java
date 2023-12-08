package glue502.software.activities.travelRecord;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.LinkedList;

import glue502.software.R;

public class travelRecordActivity extends Activity {
    // 在 PostActivity 中定义一个 SharedPreferences 的实例变量,持久化保存
    private SharedPreferences sharedPreferences;
    // 外围的LinearLayout容器
    private LinearLayout llContentView;
    //添加点击按钮
    private ImageView imageInput;
    private EditText etContent1;
    private Button btnReturn,btnSubmit;
    // “+”按钮控件List
    private LinkedList<ImageButton> listIBTNAdd;
    // “+”按钮ID索引
    private int btnIDIndex = 1000;
    // “-”按钮控件List
    private LinkedList<ImageButton> listIBTNDel;

    private int iETContentHeight = 0;   // EditText控件高度
    private float fDimRatio = 1.0f; // 尺寸比例（实际尺寸/xml文件里尺寸）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Log.d("PostActivity", "onCreate() called");
        initCtrl();
        // 获取 SharedPreferences 实例
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int numberOfControls = sharedPreferences.getInt("numberOfControls", 0);
        // 如果之前有保存的控件数量，则重新创建控件
        if (numberOfControls > 0) {

            for (int i = 1; i < numberOfControls; i++) {
                addContentWithTag(i);
            }
        }
        // 加载保存的用户输入内容
        loadSavedContent();
        setListener();
    }

    private void setListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("11111111");
            }
        });
        imageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户选择的图片
                addPicture();
            }
        });

    }

        private void addPicture(){


        }
    private void addContentWithTag(int i) {
        // 1.创建外围LinearLayout控件
        LinearLayout layout = new LinearLayout(travelRecordActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#A9A9A9"));
        layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
        layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));

//TODO 以下是图片的新加

// 1. 创建外围 HorizontalScrollView 控件
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(150)); // 150dp高度
        scrollView.setLayoutParams(scrollParams);
        scrollView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));

// 创建内部 LinearLayout
        LinearLayout innerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        innerLayout.setLayoutParams(innerParams);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8)); // 设置内边距

// 创建 GridView
        GridView gridView = new GridView(this);
        LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        gridView.setLayoutParams(gridParams);
        gridView.setNumColumns(3);

// 将 GridView 添加到内部 LinearLayout
        innerLayout.addView(gridView);

// 创建 ImageView
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(imageParams);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        imageView.setVisibility(View.VISIBLE); // 设置为可见
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 执行点击操作
                addPicture();
            }
        });
// 将 ImageView 添加到内部 LinearLayout
        innerLayout.addView(imageView);

// 将内部 LinearLayout 添加到 HorizontalScrollView
        scrollView.addView(innerLayout);

// 添加到您的布局容器中（假设容器是 llContentView）
        layout.addView(scrollView);
//TODO 以上是图片的新加


// 创建 EditText1
        EditText etContent1 = new EditText(this);
        LinearLayout.LayoutParams etParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(25));
        etContent1.setLayoutParams(etParams1);
        etContent1.setId(View.generateViewId());
        etContent1.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        etContent1.setGravity(Gravity.LEFT);
        etContent1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etContent1.setPadding(dpToPx(5), 0, 0, 0);
        etContent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        etContent1.setHint("标题");
        etContent1.setTag(i);
        layout.addView(etContent1);

// 创建 EditText2
        EditText etContent2 = new EditText(this);
        LinearLayout.LayoutParams etParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(80));
        etContent2.setLayoutParams(etParams2);
        etContent2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        etContent2.setGravity(Gravity.LEFT);
        etContent2.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etContent2.setPadding(dpToPx(5), 0, 0, 0);
        etContent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        etContent2.setHint("输入你的内容");
        etContent2.setTag(i);
        layout.addView(etContent2);

        // 3.创建“+”和“-”按钮外围控件RelativeLayout
        RelativeLayout rlBtn = new RelativeLayout(travelRecordActivity.this);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//          rlParam.setMargins(0, (int) (fDimRatio * 5), 0, 0);
        rlBtn.setPadding(0, (int) (fDimRatio * 5), 0, 0);
        rlBtn.setLayoutParams(rlParam);

        // 4.创建“+”按钮
        ImageButton btnAdd = new ImageButton(travelRecordActivity.this);
        RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 靠右放置
        btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnAdd.setLayoutParams(btnAddParam);
        // 设置属性
        btnAdd.setBackgroundResource(R.drawable.ic_add);
        btnAdd.setId(btnIDIndex);
        // 设置点击操作
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addContent(v);
            }
        });
        // 将“+”按钮放到RelativeLayout里
        rlBtn.addView(btnAdd);
        listIBTNAdd.add(1, btnAdd);

        // 5.创建“-”按钮
        ImageButton btnDelete = new ImageButton(travelRecordActivity.this);
        btnDelete.setBackgroundResource(R.drawable.ic_delete);
        RelativeLayout.LayoutParams btnDeleteAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDeleteAddParam.setMargins(0, 0, (int) (fDimRatio * 5), 0);
        // “-”按钮放在“+”按钮左侧
        btnDeleteAddParam.addRule(RelativeLayout.LEFT_OF, btnIDIndex);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteContent(v);
            }
        });
        // 将“-”按钮放到RelativeLayout里
        rlBtn.addView(btnDelete, btnDeleteAddParam);
        listIBTNDel.add(1, btnDelete);

        // 6.将RelativeLayout放到LinearLayout里
        layout.addView(rlBtn);

        // 7.将layout同它内部的所有控件加到最外围的llContentView容器里
        llContentView.addView(layout, 1);

        btnIDIndex++;


    }

    // Method to convert dp to pixels
    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    /**
     * 初始化控件
     */
    private void initCtrl() {
        llContentView = (LinearLayout) this.findViewById(R.id.content_view);
        etContent1 = (EditText) this.findViewById(R.id.et_content1);
        btnReturn =findViewById(R.id.btn_return);
        btnSubmit =findViewById(R.id.btn_submit);
        imageInput = findViewById(R.id.input_image);
        listIBTNAdd = new LinkedList<ImageButton>();
        listIBTNDel = new LinkedList<ImageButton>();

        // “+”按钮（第一个）
        ImageButton ibtnAdd1 = (ImageButton) this.findViewById(R.id.ibn_add1);
        ibtnAdd1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 获取尺寸变化比例
                iETContentHeight = etContent1.getHeight();
                fDimRatio = iETContentHeight / 80;
                // 检查输入框内容是否为空
                if (etContent1.getText().toString().isEmpty()) {
                    // 如果输入框内容为空，显示提示或者打印消息
                    Toast.makeText(travelRecordActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    // 或者打印消息到控制台
                    Log.d("PostActivity", "输入框内容为空");
                } else {
                    // 如果输入框不为空，则执行添加控件的操作
                    addContent(v);
                }
            }
        });

        listIBTNAdd.add(ibtnAdd1);
        listIBTNDel.add(null);  // 第一组隐藏了“-”按钮，所以为null
    }

    /**
     * 添加一组新控件
     *
     * @param v 事件触发控件，其实就是触发添加事件对应的“+”按钮
     */
    private void addContent(View v) {
        if (v == null) {
            return;
        }

        // 判断第几个“+”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNAdd.size(); i++) {
            if (listIBTNAdd.get(i) == v) {
                iIndex = i;
                break;
            }
        }

        if (iIndex >= 0) {
// 控件实际添加位置为当前触发位置点下一位
            iIndex += 1;
// 1.创建外围LinearLayout控件
            LinearLayout layout = new LinearLayout(travelRecordActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setBackgroundColor(Color.parseColor("#A9A9A9"));
            layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
            layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
//TODO 以下是图片的新加

// 1. 创建外围 HorizontalScrollView 控件
            HorizontalScrollView scrollView = new HorizontalScrollView(this);
            LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(150)); // 150dp高度
            scrollView.setLayoutParams(scrollParams);
            scrollView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));

// 创建内部 LinearLayout
            LinearLayout innerLayout = new LinearLayout(this);
            LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            innerLayout.setLayoutParams(innerParams);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8)); // 设置内边距
// 创建 GridView
            GridView gridView = new GridView(this);
            LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            gridView.setLayoutParams(gridParams);
            gridView.setNumColumns(3);

// 将 GridView 添加到内部 LinearLayout
            innerLayout.addView(gridView);

// 创建 ImageView
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(imageParams);
            imageView.setImageResource(R.mipmap.ic_launcher_round);

// 将 ImageView 添加到内部 LinearLayout
            innerLayout.addView(imageView);

// 将内部 LinearLayout 添加到 HorizontalScrollView
            scrollView.addView(innerLayout);

// 添加到您的布局容器中（假设容器是 llContentView）
            layout.addView(scrollView);
//TODO 以上是图片的新加

// 创建 EditText1
            EditText etContent1 = new EditText(this);
            LinearLayout.LayoutParams etParams1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(25));
            etContent1.setLayoutParams(etParams1);
            etContent1.setId(View.generateViewId());
            etContent1.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            etContent1.setGravity(Gravity.LEFT);
            etContent1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            etContent1.setPadding(dpToPx(5), 0, 0, 0);
            etContent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            etContent1.setHint("标题");
            int newIndex = llContentView.getChildCount(); // 索引值从已有子控件的数量开始
            etContent1.setTag(newIndex);
            layout.addView(etContent1);

// 创建 EditText2
            EditText etContent2 = new EditText(this);
            LinearLayout.LayoutParams etParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(80));
            etContent2.setLayoutParams(etParams2);
            etContent2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            etContent2.setGravity(Gravity.LEFT);
            etContent2.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            etContent2.setPadding(dpToPx(5), 0, 0, 0);
            etContent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            etContent2.setHint("输入你的内容");
            int a = llContentView.getChildCount(); // 索引值从已有子控件的数量开始
            etContent2.setTag(a);
            layout.addView(etContent2);

// 将动态生成的 LinearLayout 添加到 llContentView 容器中
            llContentView.addView(layout);

            // 3.创建“+”和“-”按钮外围控件RelativeLayout
            RelativeLayout rlBtn = new RelativeLayout(travelRecordActivity.this);
            RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
//          rlParam.setMargins(0, (int) (fDimRatio * 5), 0, 0);
            rlBtn.setPadding(0, (int) (fDimRatio * 5), 0, 0);
            rlBtn.setLayoutParams(rlParam);

            // 4.创建“+”按钮
            ImageButton btnAdd = new ImageButton(travelRecordActivity.this);
            RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // 靠右放置
            btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            btnAdd.setLayoutParams(btnAddParam);
            // 设置属性
            btnAdd.setBackgroundResource(R.drawable.ic_add);
            btnAdd.setId(btnIDIndex);
            // 设置点击操作
            btnAdd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    addContent(v);
                }
            });
            // 将“+”按钮放到RelativeLayout里
            rlBtn.addView(btnAdd);
            listIBTNAdd.add(iIndex, btnAdd);

            // 5.创建“-”按钮
            ImageButton btnDelete = new ImageButton(travelRecordActivity.this);
            btnDelete.setBackgroundResource(R.drawable.ic_delete);
            RelativeLayout.LayoutParams btnDeleteAddParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            btnDeleteAddParam.setMargins(0, 0, (int) (fDimRatio * 5), 0);
            // “-”按钮放在“+”按钮左侧
            btnDeleteAddParam.addRule(RelativeLayout.LEFT_OF, btnIDIndex);
            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    deleteContent(v);
                }
            });
            // 将“-”按钮放到RelativeLayout里
            rlBtn.addView(btnDelete, btnDeleteAddParam);
            listIBTNDel.add(iIndex, btnDelete);

            // 6.将RelativeLayout放到LinearLayout里
            layout.addView(rlBtn);

            // 7.将layout同它内部的所有控件加到最外围的llContentView容器里
//            llContentView.addView(layout, iIndex);

            btnIDIndex++;
            saveContentToSharedPreferences();
        }

    }


    /**
     * 删除一组控件
     *
     * @param v 事件触发控件，其实就是触发删除事件对应的“-”按钮
     */
    private void deleteContent(View v) {
        if (v == null) {
            return;
        }

        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNDel.size(); i++) {
            if (listIBTNDel.get(i) == v) {
                iIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            listIBTNAdd.remove(iIndex);
            listIBTNDel.remove(iIndex);
            // 从外围llContentView容器里删除第iIndex控件
            llContentView.removeViewAt(iIndex);
        }
    }
    // 保存内容到 SharedPreferences
    private void saveContentToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < llContentView.getChildCount(); i++) {
            View view = llContentView.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                int editTextCount = 0;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View childView = linearLayout.getChildAt(j);

                    // Ensure childView is an instance of EditText
                    if (childView instanceof EditText) {
                        EditText editText = (EditText) childView;

                        int index;
                        if (editText.getTag() != null && editText.getTag() instanceof Integer) {
                            index = (int) editText.getTag();
                        } else {
                            index = 0; // Use default index if tag is not an Integer or null
                        }
                        String title = editText.getText().toString();
                        String content = editText.getText().toString();

                        if (editTextCount == 0) {
                            // First EditText - Assume it as title EditText
                            editor.putString("userTitle" + index, title);
                        } else if (editTextCount == 1) {
                            // Second EditText - Assume it as content EditText
                            editor.putString("userContent" + index, content);
                        }

                        editTextCount++; // Increment EditText counter
                    }
                }
            }
        }
        editor.apply();
        Log.d("PostActivity", "saveContentToSharedPreferences() called");
    }

    private void loadSavedContent() {
        for (int i = 0; i < llContentView.getChildCount(); i++) {
            View view = llContentView.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                EditText etTitle = null;
                EditText etContent = null;

                // 遍历 LinearLayout 中的子视图
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View childView = linearLayout.getChildAt(j);
                    if (childView instanceof EditText) {
                        EditText editText = (EditText) childView;

                        if (etTitle == null) {
                            // 在 LinearLayout 中找到第一个 EditText，假设为标题 EditText
                            etTitle = editText;
                        } else {
                            // 在 LinearLayout 中找到第二个 EditText，假设为内容 EditText
                            etContent = editText;
                            break;
                        }
                    }
                }

                if (etTitle != null && etContent != null) {
                    Object tagObject = etTitle.getTag();
                    int savedIndex;
                    if (tagObject instanceof Integer) {
                        savedIndex = (Integer) tagObject;
                    } else {
                        savedIndex = 0; // 如果标签不是 Integer 类型或为空，则使用默认索引
                    }

                    String savedTitle = sharedPreferences.getString("userTitle" + savedIndex, "");
                    String savedContent = sharedPreferences.getString("userContent" + savedIndex, "");

                    etTitle.setText(savedTitle);
                    etContent.setText(savedContent);
                }
            }
        }
        Log.d("PostActivity", "loadSavedContent() called");
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

//关闭时执行的代码
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PostActivity", "onPause() called");
        saveContentToSharedPreferences();
        // 保存已添加的控件数量到 SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("numberOfControls", 0);
        editor.putInt("numberOfControls", llContentView.getChildCount());
        editor.apply();
    }

    //下面是按钮删除的操作在储存里也删除了
    private void removeFromSharedPreferences(int index) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userContent" + index);
        editor.apply();
    }
}