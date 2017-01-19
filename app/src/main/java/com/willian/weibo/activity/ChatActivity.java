package com.willian.weibo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.willian.weibo.R;
import com.willian.weibo.adapter.ChatAdapter;
import com.willian.weibo.bean.ChatGroup;
import com.willian.weibo.bean.ChatItem;
import com.willian.weibo.bean.Contact;
import com.willian.weibo.bean.Recorder;
import com.willian.weibo.service.ChatService;
import com.willian.weibo.sql.ChatGroupDAO;
import com.willian.weibo.sql.ChatGroupDAOImpl;
import com.willian.weibo.sql.ChatItemDAO;
import com.willian.weibo.sql.ChatItemDAOImpl;
import com.willian.weibo.utils.LoggerUtil;
import com.willian.weibo.utils.MediaManager;
import com.willian.weibo.utils.TitleBarBuilder;
import com.willian.weibo.widget.VoiceButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天界面
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";

    private static final int RECEIVE_NEW_MSG = 1;
    // 标题栏名称
    private String titleName;

    private String titleAvatar;

    private ListView mListView;

    private ChatAdapter mAdatper;

    private List<ChatItem> mChatList = new ArrayList<ChatItem>();

    private ImageView mTypeImage;

    private EditText inputText;

    private VoiceButton voiceButton;

    private ImageView addImage;

    private Button sendButton;
    // 聊天类型的默认图标为语音
    private boolean isVoice = true;

    private View mAnimView;

    private MediaManager mMediaManager;

    private int itemPosition;

    private InputMethodManager inputMethodManager;

    private Socket mClientSocket;

    private PrintWriter mWriter;

    private ChatGroup mChatGroup;

    private ChatGroupDAO mChatGroupDAO;

    private ChatItemDAO mChatItemDAO;

    private int groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mChatGroupDAO = new ChatGroupDAOImpl(this);
        mChatItemDAO = new ChatItemDAOImpl(this);

        // 获取联系人
        List<Contact> selectedContact = (List) getIntent().getSerializableExtra("selectedContact");
        if (selectedContact.size() == 1) {
            Contact mContact = selectedContact.get(0);
            // 设置标题栏名称
            titleName = mContact.getUserName();
            titleAvatar = mContact.getHeadUrl();

            mChatGroup = mChatGroupDAO.queryChatGroupByName(titleName);
        } else {
            // 设置标题栏名称
            titleName = getResources().getString(R.string.group_chat) + "(" + selectedContact.size() + ")";
        }

        mMediaManager = MediaManager.getInstance();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 根据groupName查询聊天记录
        String groupName = getIntent().getStringExtra("groupName");
        if(!TextUtils.isEmpty(groupName)){

        }

        initView();

        handleEvent();
    }

    private void initView() {
        new TitleBarBuilder(this)
                .setLeftImage(R.drawable.titlebar_back_selector)
                .setRightText(getResources().getString(R.string.setting))
                .setTitleText(titleName)
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.slide_out_from_right, R.anim.slide_in_from_left);
                    }
                }).build();

        mListView = (ListView) findViewById(R.id.lv_chat);
        mAdatper = new ChatAdapter(this, mChatList);
        // 设置语音发送者的头像
        mAdatper.setAvatarUrl(mApplication.currentUser.avatar_large);
        mListView.setAdapter(mAdatper);

        mTypeImage = (ImageView) findViewById(R.id.iv_chat_type);
        inputText = (EditText) findViewById(R.id.et_chat_content);
        voiceButton = (VoiceButton) findViewById(R.id.btn_send_voice);
        addImage = (ImageView) findViewById(R.id.iv_add);
        sendButton = (Button) findViewById(R.id.btn_send);
    }

    private void handleEvent() {
        mTypeImage.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        // 监听文本输入框
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入文本内容后，显示发送按钮
                int len = s.length();
                if (len > 0) {
                    addImage.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                } else {
                    addImage.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                }
            }
        });

        voiceButton.setOnAudioFinishedListener(new VoiceButton.OnAudioFinishedListener() {
            @Override
            public void onFinish(float audioTime, String filePath) {
                Recorder mRecorder = new Recorder(audioTime, filePath);
                ChatItem mChat = new ChatItem();
                mChat.setFrom(true); // 发送消息
                mChat.setChatType(1);
                mChat.setChatTime(getCurTime());
                mChat.setRecorder(mRecorder);
                mAdatper.addItem(mChat);
                mListView.setSelection(mAdatper.getCount() - 1);
                // 添加聊天组
                if (mChatGroup == null) {
                    ChatGroup chatGroup = new ChatGroup();
                    chatGroup.setGroupName(titleName);
                    chatGroup.setGroupAvatar(titleAvatar);
                    mChatGroupDAO.addChatGroup(chatGroup);
                }
                // 添加聊天记录
                groupId = mChatGroupDAO.queryChatGroupByName(titleName).getGroupId();
                ChatItem chatItem = new ChatItem();
                chatItem.setChatGroupId(groupId);
                chatItem.setUserName(mApplication.currentUser.name);
                chatItem.setAvatarUrl(mApplication.currentUser.avatar_large);
                chatItem.setFrom(true); // 接收
                chatItem.setChatType(1); // 语音
                chatItem.setInputContent(mRecorder.getFilePath());
                chatItem.setChatTime(getCurTime());
                mChatItemDAO.addChatItem(chatItem);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatItem mChat = mChatList.get(position);
                int chatType = mChat.getChatType();
                // 如果发送的是语音，则点击后播放
                if (chatType == 1) {
                    Recorder mRecorder = mChat.getRecorder();
                    // 如果点击的是同一个Item
                    if (itemPosition == position) {
                        // 如果已经在播放音频，则结束播放;
                        if (mAnimView != null) {
                            mAnimView.setBackgroundResource(R.mipmap.messages_audio_animation_white_from3);
                            mAnimView = null;
                            mMediaManager.releaseVoice();
                        } else {
                            // 播放语音动画
                            mAnimView = view.findViewById(R.id.iv_chat_voice);
                            mAnimView.setBackgroundResource(R.drawable.play_audio);
                            AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                            anim.start();
                            // 播放音频
                            mMediaManager.playVoice(mRecorder.getFilePath(), new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mAnimView.setBackgroundResource(R.mipmap.messages_audio_animation_white_from3);
                                }
                            });
                        }
                    } else {
                        // 点击的是不同的Item
                        if (mAnimView != null) {
                            mAnimView.setBackgroundResource(R.mipmap.messages_audio_animation_white_from3);
                            mAnimView = null;
                        }
                        // 播放语音动画
                        mAnimView = view.findViewById(R.id.iv_chat_voice);
                        mAnimView.setBackgroundResource(R.drawable.play_audio);
                        AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                        anim.start();
                        // 播放音频
                        mMediaManager.playVoice(mRecorder.getFilePath(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mAnimView.setBackgroundResource(R.mipmap.messages_audio_animation_white_from3);
                            }
                        });
                    }
                    itemPosition = position;
                }
            }
        });
        // 连接Socket服务端
        connectTcpServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_chat_type:
                if (isVoice) {
                    // 隐藏软键盘
                    inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
                    // 点击后，图标切换为键盘Icon
                    mTypeImage.setImageResource(R.drawable.msg_keyboard_selector);
                    // 隐藏输入框
                    inputText.setVisibility(View.GONE);
                    // 显示语音发送按钮
                    voiceButton.setVisibility(View.VISIBLE);
                    isVoice = false;
                } else {
                    // 显示软键盘
                    inputMethodManager.showSoftInput(inputText, 0);
                    // 点击后，图标切换为语音Icon
                    mTypeImage.setImageResource(R.drawable.msg_voice_bg_selector);
                    // 显示输入框
                    inputText.setVisibility(View.VISIBLE);
                    // 隐藏语音发送按钮
                    voiceButton.setVisibility(View.GONE);
                    isVoice = true;
                }
                break;
            case R.id.btn_send:
                String textMsg = inputText.getText().toString();
                ChatItem mChat = new ChatItem();
                mChat.setFrom(true); // 发送消息
                mChat.setChatType(0);
                mChat.setChatTime(getCurTime());
                mChat.setInputContent(textMsg);
                // 更新适配器
                mAdatper.addItem(mChat);
                // 清空文本输入内容
                inputText.setText("");
                // 将ListView定位在尾部
                mListView.setSelection(mAdatper.getCount() - 1);
                // 向服务端发送消息
                mWriter.println(textMsg);
                // 添加聊天组
                if (mChatGroup == null) {
                    ChatGroup chatGroup = new ChatGroup();
                    chatGroup.setGroupName(titleName);
                    chatGroup.setGroupAvatar(titleAvatar);
                    mChatGroupDAO.addChatGroup(chatGroup);
                }
                // 添加聊天记录
                groupId = mChatGroupDAO.queryChatGroupByName(titleName).getGroupId();
                ChatItem chatItem = new ChatItem();
                chatItem.setChatGroupId(groupId);
                chatItem.setUserName(mApplication.currentUser.name);
                chatItem.setAvatarUrl(mApplication.currentUser.avatar_large);
                chatItem.setFrom(true); // 发送
                chatItem.setChatType(0); // 文本
                chatItem.setInputContent(textMsg);
                chatItem.setChatTime(getCurTime());
                mChatItemDAO.addChatItem(chatItem);
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前聊天时间
     *
     * @return
     */
    private String getCurTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String chatTime = sdf.format(new Date());
        return chatTime;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaManager.pauseVoice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaManager.resumeVoice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaManager.releaseVoice();
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开启服务，并接收服务端消息
     */
    private void connectTcpServer() {
        Intent mIntent = new Intent(this, ChatService.class);
        startService(mIntent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                BufferedReader reader = null;
                while (socket == null) {
                    try {
                        socket = new Socket("localhost", 9898);
                        mClientSocket = socket;
                        mWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        LoggerUtil.showLog(TAG, "connect tcp server successed", 6);
                    } catch (IOException e) {
                        SystemClock.sleep(1000);
                        LoggerUtil.showLog(TAG, "connect tcp server failed", 6);
                    }
                }

                // 接收服务器端消息
                try {
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (!ChatActivity.this.isFinishing()) {
                        String serverMsg = reader.readLine();
                        LoggerUtil.showLog(TAG, "========received message :" + serverMsg, 6);
                        if (!TextUtils.isEmpty(serverMsg)) {
                            Message msg = new Message();
                            msg.what = RECEIVE_NEW_MSG;
                            msg.obj = serverMsg;
                            mHandler.sendMessage(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                        mWriter.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_NEW_MSG:
                    // 将服务端返回的消息显示在ListView中
                    String responseMsg = (String) msg.obj;
                    ChatItem mChat = new ChatItem();
                    mChat.setFrom(false);// 接收信息
                    mChat.setChatType(0);
                    mChat.setChatTime(getCurTime());
                    mChat.setInputContent(responseMsg);
                    // 更新适配器
                    mAdatper.addItem(mChat);
                    // 将ListView定位在尾部
                    mListView.setSelection(mAdatper.getCount() - 1);
                    // 添加聊天记录
                    ChatItem chatItem = new ChatItem();
                    chatItem.setChatGroupId(groupId);
                    chatItem.setUserName(mApplication.currentUser.name);
                    chatItem.setAvatarUrl(mApplication.currentUser.avatar_large);
                    chatItem.setFrom(false); // 接收
                    chatItem.setChatType(0); // 文本
                    chatItem.setInputContent(responseMsg);
                    chatItem.setChatTime(getCurTime());
                    mChatItemDAO.addChatItem(chatItem);
                    break;
                default:
                    break;
            }
        }
    };
}
