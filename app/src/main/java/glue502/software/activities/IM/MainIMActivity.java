package glue502.software.activities.IM;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import glue502.software.R;

public class MainIMActivity extends AppCompatActivity {
    // 发起聊天 username 输入框
    private EditText mChatIdEdit,createGroup,joinGroup;
    // 发起聊天
    private Button mStartChatBtn,mStartChatingBtn,mCreateGroupBtn;
    // 退出登录
    private Button mSignOutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_main);

        //初始化环信，在这里实现了类似于项目中的token判断，如果没有token则跳转到登录界面
        EMOptions options = new EMOptions();
        options.setAppKey("1117240606210709#travel");
        // 其他 EMOptions 配置。
        EMClient.getInstance().init(this, options);
        if (EMClient.getInstance().isLoggedInBefore()) {
            // 用户已经登录或者SDK已经登录成功过，可以进入主界面
            initView();
        } else {
            // 用户尚未登录或者SDK尚未登录成功，跳转到登录界面
            Intent intent = new Intent(getApplicationContext(), IMLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {

        mChatIdEdit = findViewById(R.id.ec_edit_chat_id);

        mStartChatBtn = findViewById(R.id.ec_btn_start_chat);

        mStartChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // 获取我们发起聊天的者的username
                String chatId = mChatIdEdit.getText().toString().trim();
                if (!TextUtils.isEmpty(chatId)) {
                    // 获取当前登录用户的 username
                    String currUsername = EMClient.getInstance().getCurrentUser();
                    if (chatId.equals(currUsername)) {
                        Toast.makeText(MainIMActivity.this, "不能和自己聊天", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 跳转到聊天界面，开始聊天
                    Intent intent = new Intent(MainIMActivity.this, IMChatActivity.class);
                    intent.putExtra("ec_chat_id", chatId);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainIMActivity.this, "Username 不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });

        mSignOutBtn = findViewById(R.id.ec_btn_sign_out);
        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                signOut();
            }
        });

        //创建群聊社区
        mCreateGroupBtn = findViewById(R.id.ec_btn_create_chating);
        createGroup = findViewById(R.id.ec_create_chatRoom_id);
        mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String subject = "testwxc";
//                            String description = "testwxc";
//                            String welcomeMessage = "testwxc";
//                            int maxUserCount = 100;
//                            List<String> members = new ArrayList<>(); // 设置邀请加入聊天室的成员列表，如果不需要邀请成员，设置为空列表。
//
//                            EMClient.getInstance().chatroomManager().createChatRoom(subject, description, welcomeMessage, maxUserCount, members);
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(MainActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        } catch (HyphenateException e) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(MainActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                }).start();
                //TODO 现在这里写的是一个群聊，具体实现任然需要聊天室，在 后面还是要修改为聊天室
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 创建群组
                         * @param groupName 群组名称
                         * @param desc 群组简介
                         * @param allMembers 群组初始成员，如果只有自己传空数组即可（最多可以传100个成员）
                         * @param reason 邀请成员加入的reason
                         * @param option 群组类型选项，可以设置群组最大用户数(取决于所选择的版本，不同版本最大数不同)及群组类型@see {@link EMGroupStyle}
                         *               option.inviteNeedConfirm表示邀请对方进群是否需要对方同意，默认是被邀请方自动进群。
                         *               option.extField创建群时可以为群组设定扩展字段，方便个性化订制。
                         * @return 创建好的group
                         * @throws HyphenateException
                         */
                        String groupName =createGroup.getText().toString();
                        String[] members = new String[]{};
                        EMGroupOptions option = new EMGroupOptions();
                        option.maxUsers = 200;
                        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                        try {
                            EMClient.getInstance().groupManager().createGroup(groupName, "test", members, null, option);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainIMActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainIMActivity.this, GroupChatActivity.class);
                                    intent.putExtra("ec_chat_id", groupName);
                                    startActivity(intent);
                                }
                            });
                        } catch (HyphenateException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainIMActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        //加入群聊社区
        joinGroup = findViewById(R.id.ec_edit_chatRoom_id);
        mStartChatingBtn = findViewById(R.id.ec_btn_add_chating);
        mStartChatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                //TODO roomId为聊天室ID,现在写入的roomId是直接从环信服务器中获取的，目前创建聊天室技术未处理完
//                 EMClient.getInstance().chatroomManager().joinChatRoom("233085912285194", new EMValueCallBack<EMChatRoom>() {
//
//                    @Override
//                    public void onSuccess(EMChatRoom value) {
//                        //加入聊天室成功
//                        // 在主线程中显示Toast
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(MainActivity.this, GroupChatActivity.class);
//                                intent.putExtra("ec_chat_id", "233085912285194");
//                                startActivity(intent);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(final int error, String errorMsg) {
//                        //加入聊天室失败
//                        // 在主线程中显示Toast
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this, "加入失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
                //TODO 现在实现的是加入群聊，具体实现任然需要聊天室，在 后面还是要修改为聊天室
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<EMGroup> joinedGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                            boolean hasJoined = false;
                            for (EMGroup group : joinedGroups) {
                                if (group.getGroupId().equals("233185594114056")) {
                                    hasJoined = true;
                                    break;
                                }
                            }

                            if (!hasJoined) {
                                EMClient.getInstance().groupManager().joinGroup("233185594114056");
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainIMActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainIMActivity.this, GroupChatActivity.class);
                                    intent.putExtra("ec_chat_id", "233185594114056");
                                    startActivity(intent);
                                }
                            });
                        } catch (HyphenateException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainIMActivity.this, "加入失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * 退出登录，实现了sdk和用户解绑的过程,如果不进行解绑的话，用户再次登录时，任然会直接进入输入页面
     */
    private void signOut() {
        // 调用sdk的退出登录方法，第一个参数表示是否解绑推送的token，没有使用推送或者被踢都要传false
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override public void onSuccess() {
                Log.i("lzan13", "logout success");
                // 调用退出成功，结束app
                finish();
            }

            @Override public void onError(int i, String s) {
                Log.i("lzan13", "logout error " + i + " - " + s);
            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }

}