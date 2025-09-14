package de.gamedude;

import de.gamedude.network.EchoClientHandler;
import de.gamedude.network.MessageClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientMain {

    public static void main(String[] args) {
        new ClientMain().start();
    }

    public void start() {
        try ( EventLoopGroup eventLoop = new NioEventLoopGroup(1)) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoop)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(InetAddress.getLocalHost(), 8089)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                            ch.pipeline().addLast(new MessageClientHandler());
                        }
                    });
            ChannelFuture f = bootstrap.connect().sync();

            while (f.channel().isOpen()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String line = reader.readLine();
                f.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes()));
            }

            f.channel().closeFuture().sync();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
