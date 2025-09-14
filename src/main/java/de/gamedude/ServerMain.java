package de.gamedude;

import de.gamedude.network.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

// https://livebook.manning.com/book/netty-in-action/part-1/
public class ServerMain {

    public static void main(String[] args) {
        new ServerMain().start();
    }

    public void start() {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        var bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        var workerGroup = new MultiThreadIoEventLoopGroup(0, NioIoHandler.newFactory());


        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(8089)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("Server started and listening on " + future.channel().localAddress());
            future.channel().closeFuture().sync();
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
