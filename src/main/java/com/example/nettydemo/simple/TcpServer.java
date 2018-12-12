package com.example.nettydemo.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Copyright (C), 2018-2019, 深圳市xxx科技有限公司
 *
 * @author: chaoshibin
 * Date:     2018/12/12 10:48
 * Description:
 */
public class TcpServer {
    private int port;

    private TcpServer(int port) {
        this.port = port;
    }

    public void run() {

        // 主线程组, 用于接受客户端的连接，但是不做任何具体业务处理，像老板一样，负责接待客户，不具体服务客户
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 工作线程组, 老板线程组会把任务丢给他，让手下线程组去做任务，服务客户
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("handler", new TcpServerHandler());
                    }
                });

                 // 绑定端口，并启动server，同时设置启动方式为同步
        try {
            ChannelFuture f = b.bind(port).sync();
            System.out.println(" 启动成功，在地址[" + f.channel().localAddress() + "]上等待客户请求......");
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        TcpServer tcpServer = new TcpServer(9002);
        tcpServer.run();
    }
}
