package tak.server.federation;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.marti.config.Configuration;
import com.bbn.marti.config.Federation;
import com.bbn.marti.config.Tls;
import com.bbn.roger.fig.model.FigServerConfig;
import com.bbn.marti.remote.config.CoreConfigFacade;

import java.net.SocketAddress;

public class TlsHandshakeProxy {

    private static final int PROXY_PORT = 9001;
    private static final String GRPC_HOST = "127.0.0.1";
    private static final int GRPC_PORT = 9101;

    private SslContext sslContext;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    private static final Logger logger = LoggerFactory.getLogger(TlsHandshakeProxy.class);

    private FigServerConfig config = null;
    private SSLConfig sslConfig = null;

    private Configuration coreConfig() {
        return CoreConfigFacade.getInstance().getRemoteConfiguration();
    }

    private Federation fedConfig() {
        return coreConfig().getFederation();
    }

    public void initSsl() {
        try {
            // if the federation truststore is pointing to the root truststore - undo it and set to fed truststore.
            if (fedConfig() != null) {

                com.bbn.marti.config.Federation.FederationServer fedServerConfig = fedConfig().getFederationServer();

                if (fedServerConfig != null) {
                    Tls fedTls = fedServerConfig.getTls();

                    if (fedTls != null) {
                        String fedTruststore = fedTls.getTruststoreFile();

                        if (fedTruststore != null && fedTruststore.equals("certs/files/truststore-root.jks")) {
                            fedTls.setTruststoreFile(CoreConfigFacade.DEFAULT_TRUSTSTORE);
                            CoreConfigFacade.getInstance().saveChanges();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error checking federation truststore configuration.", e);
        }

        com.bbn.marti.config.Federation.FederationServer fedServerConfig = fedConfig().getFederationServer();

        FigServerConfig serverConfig = new FigServerConfig();

        serverConfig.setPort(fedServerConfig.getV2Port());
        serverConfig.setKeystoreFile(fedServerConfig.getTls().getKeystoreFile());
        serverConfig.setKeystorePassword(fedServerConfig.getTls().getKeystorePass());
        serverConfig.setTruststoreFile(fedServerConfig.getTls().getTruststoreFile());
        serverConfig.setTruststorePass(fedServerConfig.getTls().getTruststorePass());
        serverConfig.setContext(fedServerConfig.getTls().getContext());
        serverConfig.setCiphers(fedServerConfig.getTls().getCiphers());
        serverConfig.setSkipGateway(true); // eliminate this
        serverConfig.setMaxMessageSizeBytes(fedConfig().getFederationServer().getMaxMessageSizeBytes()); // put in coreconfig
        serverConfig.setMetricsLogIntervalSeconds(60); // put in coreconfig
        serverConfig.setClientTimeoutTime(15); // put in coreconfig
        serverConfig.setClientRefreshTime(5); // put in coreconfig

        setConfig(serverConfig);

        sslConfig = new SSLConfig();
        this.sslContext = sslConfig.initSslContext(config);
    }

    public void start() throws InterruptedException {
        initSsl();
        if (sslContext == null) throw new IllegalStateException("SSLContext not initialized!");

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                        sslHandler.handshakeFuture().addListener((GenericFutureListener<? extends Future<? super Channel>>) future -> {
                            if (!future.isSuccess()) {
                                onHandshakeFailure(ch.remoteAddress(), future.cause());
                                ch.close();
                            } else {
                                startProxying(ch);
                            }
                        });
                        ch.pipeline().addLast(sslHandler);
                    }
                });

        serverChannel = b.bind(PROXY_PORT).sync().channel();
        logger.info("TlsHandshakeProxy started on port " + PROXY_PORT);
    }

    public void stop() {
        if (serverChannel != null) serverChannel.close();
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
        logger.info("TlsHandshakeProxy shut down.");
    }

    protected void onHandshakeFailure(SocketAddress remote, Throwable cause) {
        // Your callback logic here
        logger.info("TLS handshake failed from " + remote + ": " + cause);
    }

    /*
    public void start() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            SslHandler sslHandler = sslContext.newHandler(ch.alloc());

                            // Listen for handshake result
                            sslHandler.handshakeFuture().addListener((GenericFutureListener<? extends Future<? super Channel>>) future -> {
                                if (!future.isSuccess()) {
                                    // 🔔 HANDSHAKE FAILURE CALLBACK HERE
                                    Throwable cause = future.cause();
                                    logger.info("TLS handshake failed: " + cause);
                                    // Call your method here:
                                    // onHandshakeFailure(ch.remoteAddress(), cause);
                                    ch.close();
                                } else {
                                    // Handshake succeeded: pipe traffic to real gRPC server
                                    startProxying(ch);
                                }
                            });

                            ch.pipeline().addLast(sslHandler);
                            // No need to add more handlers yet; proxy is set up after handshake
                        }
                    });

            ChannelFuture f = b.bind(PROXY_PORT).sync();
            logger.info("TLS proxy listening on " + PROXY_PORT + ", forwarding to " + REAL_GRPC_PORT);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }*/

    // Called only if handshake succeeded
    static void startProxying(Channel clientChannel) {
        Bootstrap b = new Bootstrap();
        b.group(clientChannel.eventLoop()) // Use same event loop!
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel serverChannel) {
                        // Pipe data between clientChannel <-> serverChannel
                        serverChannel.pipeline().addLast(new RelayHandler(clientChannel));
                    }
                });

        b.connect(GRPC_HOST, GRPC_PORT).addListener((ChannelFutureListener) serverFuture -> {
            if (serverFuture.isSuccess()) {
                // Link both channels for bidirectional proxying
                Channel serverChannel = serverFuture.channel();
                clientChannel.pipeline().addLast(new RelayHandler(serverChannel));
            } else {
                clientChannel.close();
            }
        });
    }

    // Handles relaying traffic from one channel to another
    static class RelayHandler extends ChannelInboundHandlerAdapter {
        private final Channel relayTo;
        RelayHandler(Channel relayTo) { this.relayTo = relayTo; }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (relayTo.isActive()) {
                relayTo.writeAndFlush(msg);
            } else {
                ReferenceCountUtil.release(msg);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            if (relayTo.isActive()) {
                relayTo.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    }

    public void setConfig(FigServerConfig config) {
        this.config = config;
    }

}
