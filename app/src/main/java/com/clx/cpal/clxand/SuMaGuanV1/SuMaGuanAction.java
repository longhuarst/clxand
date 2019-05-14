package com.clx.cpal.clxand.SuMaGuanV1;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clx.cpal.clxand.R;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;


public class SuMaGuanAction extends AppCompatActivity {

    String uuid = "";

    SuMaGuanTcp socket_ = null;

    TextView tvStatus = null;
    Button btnOn = null;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Log.e("clx","handlermessage");

            switch (msg.what){
                case 0x100:
                    tvStatus.setText("打开");
                    btnOn.setText("关闭");
                    break;
                case 0x200:
                    tvStatus.setText("关闭");
                    btnOn.setText("打开");
                    break;
                case 0x300:
                    tvStatus.setText("执行中...");
                    btnOn.setText("正在打开...");
                    break;
                case 0x400:
                    tvStatus.setText("执行中...");
                    btnOn.setText("正在关闭...");
                    break;
            }


//            if (msg.what == 0x100){
//                Log.e("clx",msg.obj.toString());
//                Toast.makeText(SuMaGuanAction.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
//            }
        }
    };


    public Handler getmHandler(){
        return mHandler;
    }



























    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su_ma_guan_action);


        uuid = SuMaGuanUuid.getInstance().getUUID();


        tvStatus = findViewById(R.id.status);
        btnOn = findViewById(R.id.btnOn);



        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btnOn.getText().toString()){
                    case "等待设备连接...":
                        Toast.makeText(SuMaGuanAction.this,"请等待服务器响应",Toast.LENGTH_LONG).show();
                    break;
                    case "打开":
                        socketChannel.writeAndFlush("\npub/"+uuid+"-device"+"/"+
                                "VER=1.0&TYPE=sumaguan&PAYLOAD=c/on$\r\n");
                    break;
                    case "关闭":
                        socketChannel.writeAndFlush("\npub/"+uuid+"-device"+"/"+
                                "VER=1.0&TYPE=sumaguan&PAYLOAD=c/off$\r\n");
                    break;
                    default:
                        Toast.makeText(SuMaGuanAction.this,"忙碌中...",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        });



        Log.e("clx",SuMaGuanUuid.getInstance().getUUID());

        if (uuid.equals("")){
            Toast.makeText(this,"无法正确识别设备",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,uuid,Toast.LENGTH_LONG).show();

//            socket_ = new SuMaGuanTcp(mHandler){
//
//
//                void SendMessage(Message msg){
//
//                }
//            };
//            socket_.start();




            nettyThread.start();;

//            SocketSingleton.getInstance().getService().connect(new SocketService.Callback<Void>() {
//                @Override
//                public void onEvent(int code, String msg, Void aVoid) {
//                    if (code == 200){
//                        Log.e("clx","connected!");
//                    }else if (code ==400){
//
//                        Log.e("clx","connected failed!");
//                    }
//                }
//            });

        }








    }




    Thread nettyThread = new Thread(new Runnable() {
        @Override
        public void run() {
            connect(new Callback<Void>(){

                @Override
                public void onEvent(int code, String msg, Void aVoid) {
                    if (code == 200){
                        Log.e("clx","connected!");
                    }else if (code ==400){

                        Log.e("clx","connected failed!");
                    }
                }
            });




            while(true){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Log.e("clx","nettythread exit");
        }
    });



    enum Status{
        CONNECTING,
        CONNECTED,
        UNCONNECTED
    };


    public interface Callback<T> {
        void onEvent(int code, String msg, T t);
    }



    Status status = Status.UNCONNECTED;


    private Handler handler;
    private SocketChannel socketChannel;

    public void connect(@NonNull Callback<Void> callback){

        if (status == Status.CONNECTING){
            return;//正在连接了
        }

        status = Status.CONNECTING;

        NioEventLoopGroup group = new NioEventLoopGroup();

        new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(group)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0,10,0));
//                        ch.pipeline().addLast(new ObjectEncoder());
//                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelHandler());
                    }
                })
                .connect(new InetSocketAddress("www.hongyiweichuang.com",20000))
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()){
                        socketChannel = (SocketChannel) future.channel();

                        String message = "\r\n" +
                                "sub/"+uuid+"\r\n";
                        socketChannel.writeAndFlush(message);

                        callback.onEvent(200, "success", null);
                    }else {
                        Log.e("clx", "connect failed");
                        close();
                        // 这里一定要关闭，不然一直重试会引发OOM
                        future.channel().close();
                        group.shutdownGracefully();
                        callback.onEvent(400, "connect failed", null);
                    }
                });


    }






    private void close() {
        if (socketChannel != null) {
            socketChannel.close();
            socketChannel = null;
        }
    }




    private class ChannelHandler extends SimpleChannelInboundHandler<String> {


        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
            Log.e("clx","channelRegistered");
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            Log.e("clx","channelUnregistered");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            Log.e("clx","channelActive");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            Log.e("clx","channelInactive");
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);

        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);


            if (evt instanceof IdleStateEvent){

                IdleStateEvent e = (IdleStateEvent) evt;

                switch (e.state()){
                    case WRITER_IDLE:
                    {
                        String meg = "heartbeat\r\n";
                        socketChannel.writeAndFlush(meg);
                    }
                        break;

                }
            }

        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            super.channelWritabilityChanged(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

            Log.e("clx","recv tcp :"+msg);

            if (msg.endsWith("$")){
                msg = msg.substring(0,msg.length()-1);
                String segment[] = msg.split("&");
                if (segment.length == 3){
                    if (segment[0].equals("VER=1.0") && segment[1].equals("TYPE=sumaguan")){
                        String payload[] = segment[2].split("=");
                        if (payload.length == 2){
                                String body[] = payload[1].split("/");
                                if (body.length == 2){
                                    switch (body[0]){
                                        case "s":
                                            Log.e("clx",body[1]);
                                        {
                                            Message msgBody = new Message();
                                            switch (body[1]){
                                                case "on":

                                                    msgBody.what = 0x100;
                                                    break;
                                                case "off":
                                                    msgBody.what = 0x200;
                                                    break;
                                                case "oning":
                                                    msgBody.what = 0x300;
                                                    break;
                                                case "offing":
                                                    msgBody.what = 0x400;
                                                    break;
                                                default:
                                                    break;
                                            }

                                            mHandler.sendMessage(msgBody);


                                        }
                                            break;
                                        case "c":
                                            Log.e("clx",body[1]);

                                            break;
                                        default:
                                            break;
                                    }
                                }

                        }
                    }
                }
            }


        }
    }

    //TCP
//    Thread nettyThread = new Thread(new Runnable() {
//
//
//
//
//        @Override
//        public void run() {
//
//
//
//
//
//
//
//        }
//    });













}
