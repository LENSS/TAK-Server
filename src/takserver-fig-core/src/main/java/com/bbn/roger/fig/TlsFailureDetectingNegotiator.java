package com.bbn.roger.fig;


import io.grpc.netty.GrpcHttp2ConnectionHandler;
import io.grpc.netty.InternalProtocolNegotiator;
import io.grpc.netty.InternalProtocolNegotiators;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.AsciiString;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import tak.server.federation.oidf.TrustChainHandler;

public class TlsFailureDetectingNegotiator implements InternalProtocolNegotiator.ProtocolNegotiator {

    private static final Logger logger = LoggerFactory.getLogger(TlsFailureDetectingNegotiator.class);

    private final InternalProtocolNegotiator.ProtocolNegotiator base;

    public TlsFailureDetectingNegotiator(SslContext sslContext) {
        this.base = InternalProtocolNegotiators.serverTls(sslContext);
    }

    @Override
    public ChannelHandler newHandler(GrpcHttp2ConnectionHandler grpcHandler) {
        ChannelHandler baseHandler = base.newHandler(grpcHandler);

        return new ChannelInboundHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                // Add original TLS handler
                ctx.pipeline().addLast(baseHandler);

                // Add your TLS monitoring handler *after* SslHandler is in place
                ctx.pipeline().addLast(new TlsHandshakeLoggingHandler());
                super.handlerAdded(ctx);
            }
        };
    }
    /*
    @Override
    public ChannelHandler newHandler(GrpcHttp2ConnectionHandler grpcHandler) {
        ChannelHandler originalHandler = base.newHandler(grpcHandler);
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                ctx.pipeline().addLast(originalHandler);
            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof SslHandshakeCompletionEvent event && !event.isSuccess()) {
                    InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                    logger.warn("TLS handshake failed from remote address: {}", remoteAddress, event.cause());

                    handshakeFailureHandler.accept(event.cause());
                    //TrustChainHandler.tryResolveFromRemote("http://" + remoteAddress.getAddress().getHostAddress() + ":8181");

                }
                super.userEventTriggered(ctx, evt);
            }
        };
    }   */

    @Override
    public AsciiString scheme() {
        return base.scheme();
    }

    @Override
    public void close() {
        base.close();
    }

}
