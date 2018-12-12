package com.example.nettydemo.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Copyright (C), 2018-2019, 深圳市xxx科技有限公司
 *
 * @author: chaoshibin
 * Date:     2018/12/12 10:48
 * Description:
 */
public class TcpClient {
    private String host;

    private int port;

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        TcpClient tcpServer = new TcpClient("127.0.0.1", 9002);
        tcpServer.connect();
    }

    public void connect() {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new TcpClientHandler());
                    }
                });

        // 绑定端口，并启动server，同时设置启动方式为同步
        try {
            // 发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放线程池资源
            group.shutdownGracefully();
        }
    }
}
