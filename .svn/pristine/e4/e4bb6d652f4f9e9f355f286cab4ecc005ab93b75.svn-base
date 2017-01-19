package com.willian.weibo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.willian.weibo.utils.LoggerUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket服务端
 */
public class ChatService extends Service {

    private static final String TAG = "ChatService";
    // Service是否关闭，默认为开启状态
    private boolean mIsShutdown = false;

    /**
     * 创建Service的时候调用
     *
     * @param intent
     * @param startId
     */
    @Override
    public void onStart(Intent intent, int startId) {
        new Thread(new TcpServer()).start();
        super.onStart(intent, startId);
        LoggerUtil.showLog(TAG, "=====ChatService onStart========", 6);
    }

    /**
     * 每次启动Service的时候调用
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsShutdown = true;
        super.onDestroy();
        LoggerUtil.showLog(TAG, "=====ChatService onDestory========", 6);
    }

    /**
     * 开启一个端口为9898的Socket服务端
     */
    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;

            try {
                serverSocket = new ServerSocket(9898);
            } catch (IOException e) {
                LoggerUtil.showLog(TAG, "establish tcp server failed", 6);
                e.printStackTrace();
            }

            while (!mIsShutdown) {
                try {
                    // 等待客户端接入，如果没有客户端接入，则程序会一直阻塞在这里
                    final Socket client = serverSocket.accept();
                    LoggerUtil.showLog(TAG, "========client name :" + client.hashCode(), 6);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 接收来自客户端的消息，并回复消息给客户端
     *
     * @param client
     * @throws IOException
     */
    private void responseClient(Socket client) throws IOException {
        // 接收客户端消息
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        // 向客户端发送消息
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        while (!mIsShutdown) {
            String receivedMsg = reader.readLine();
            LoggerUtil.showLog(TAG, "========server accept message:" + receivedMsg, 6);
            if (TextUtils.isEmpty(receivedMsg)) {
                break;
            }
            String sendMsg = "Nice to meet you.";
            writer.println(sendMsg);
            LoggerUtil.showLog(TAG, "========server send message:" + sendMsg, 6);
        }

        reader.close();
        writer.close();
        client.close();
    }
}
