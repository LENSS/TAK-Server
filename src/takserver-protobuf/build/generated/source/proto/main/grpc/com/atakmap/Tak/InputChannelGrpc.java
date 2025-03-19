package com.atakmap.Tak;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.2)",
    comments = "Source: streaminginput.proto")
public class InputChannelGrpc {

  private InputChannelGrpc() {}

  public static final String SERVICE_NAME = "com.atakmap.InputChannel";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.ClientSubscription,
      atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage> METHOD_CLIENT_TAK_MESSAGE_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "com.atakmap.InputChannel", "ClientTakMessageStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ClientSubscription.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage,
      com.atakmap.Tak.ClientSubscription> METHOD_SERVER_TAK_MESSAGE_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
          generateFullMethodName(
              "com.atakmap.InputChannel", "ServerTakMessageStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ClientSubscription.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.ClientVersion,
      com.atakmap.Tak.ServerVersion> METHOD_VERSION_CHECK =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.atakmap.InputChannel", "VersionCheck"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ClientVersion.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ServerVersion.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InputChannelStub newStub(io.grpc.Channel channel) {
    return new InputChannelStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InputChannelBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new InputChannelBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static InputChannelFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new InputChannelFutureStub(channel);
  }

  /**
   */
  public static abstract class InputChannelImplBase implements io.grpc.BindableService {

    /**
     */
    public void clientTakMessageStream(com.atakmap.Tak.ClientSubscription request,
        io.grpc.stub.StreamObserver<atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CLIENT_TAK_MESSAGE_STREAM, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage> serverTakMessageStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ClientSubscription> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_SERVER_TAK_MESSAGE_STREAM, responseObserver);
    }

    /**
     */
    public void versionCheck(com.atakmap.Tak.ClientVersion request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ServerVersion> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_VERSION_CHECK, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_CLIENT_TAK_MESSAGE_STREAM,
            asyncServerStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.ClientSubscription,
                atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage>(
                  this, METHODID_CLIENT_TAK_MESSAGE_STREAM)))
          .addMethod(
            METHOD_SERVER_TAK_MESSAGE_STREAM,
            asyncClientStreamingCall(
              new MethodHandlers<
                atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage,
                com.atakmap.Tak.ClientSubscription>(
                  this, METHODID_SERVER_TAK_MESSAGE_STREAM)))
          .addMethod(
            METHOD_VERSION_CHECK,
            asyncUnaryCall(
              new MethodHandlers<
                com.atakmap.Tak.ClientVersion,
                com.atakmap.Tak.ServerVersion>(
                  this, METHODID_VERSION_CHECK)))
          .build();
    }
  }

  /**
   */
  public static final class InputChannelStub extends io.grpc.stub.AbstractStub<InputChannelStub> {
    private InputChannelStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InputChannelStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InputChannelStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InputChannelStub(channel, callOptions);
    }

    /**
     */
    public void clientTakMessageStream(com.atakmap.Tak.ClientSubscription request,
        io.grpc.stub.StreamObserver<atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_CLIENT_TAK_MESSAGE_STREAM, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage> serverTakMessageStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ClientSubscription> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_SERVER_TAK_MESSAGE_STREAM, getCallOptions()), responseObserver);
    }

    /**
     */
    public void versionCheck(com.atakmap.Tak.ClientVersion request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ServerVersion> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_VERSION_CHECK, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InputChannelBlockingStub extends io.grpc.stub.AbstractStub<InputChannelBlockingStub> {
    private InputChannelBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InputChannelBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InputChannelBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InputChannelBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage> clientTakMessageStream(
        com.atakmap.Tak.ClientSubscription request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_CLIENT_TAK_MESSAGE_STREAM, getCallOptions(), request);
    }

    /**
     */
    public com.atakmap.Tak.ServerVersion versionCheck(com.atakmap.Tak.ClientVersion request) {
      return blockingUnaryCall(
          getChannel(), METHOD_VERSION_CHECK, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InputChannelFutureStub extends io.grpc.stub.AbstractStub<InputChannelFutureStub> {
    private InputChannelFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InputChannelFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InputChannelFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InputChannelFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.atakmap.Tak.ServerVersion> versionCheck(
        com.atakmap.Tak.ClientVersion request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_VERSION_CHECK, getCallOptions()), request);
    }
  }

  private static final int METHODID_CLIENT_TAK_MESSAGE_STREAM = 0;
  private static final int METHODID_VERSION_CHECK = 1;
  private static final int METHODID_SERVER_TAK_MESSAGE_STREAM = 2;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InputChannelImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(InputChannelImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CLIENT_TAK_MESSAGE_STREAM:
          serviceImpl.clientTakMessageStream((com.atakmap.Tak.ClientSubscription) request,
              (io.grpc.stub.StreamObserver<atakmap.commoncommo.protobuf.v1.Takmessage.TakMessage>) responseObserver);
          break;
        case METHODID_VERSION_CHECK:
          serviceImpl.versionCheck((com.atakmap.Tak.ClientVersion) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.ServerVersion>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SERVER_TAK_MESSAGE_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.serverTakMessageStream(
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.ClientSubscription>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_CLIENT_TAK_MESSAGE_STREAM,
        METHOD_SERVER_TAK_MESSAGE_STREAM,
        METHOD_VERSION_CHECK);
  }

}
