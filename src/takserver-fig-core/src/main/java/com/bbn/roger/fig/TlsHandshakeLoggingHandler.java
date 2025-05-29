package com.bbn.roger.fig;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TlsHandshakeLoggingHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TlsHandshakeLoggingHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof SslHandshakeCompletionEvent handshakeEvent && !handshakeEvent.isSuccess()) {
            Throwable cause = handshakeEvent.cause();
            logger.warn("TLS handshake failed from {}: {}", ctx.channel().remoteAddress(), cause.getMessage(), cause);

            // Add trust chain resolution or alert logic here
        }
        super.userEventTriggered(ctx, evt);
    }
}

