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
    comments = "Source: fig.proto")
public class FederatedChannelGrpc {

  private FederatedChannelGrpc() {}

  public static final String SERVICE_NAME = "com.atakmap.FederatedChannel";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.FederatedEvent,
      com.atakmap.Tak.Empty> METHOD_SEND_ONE_EVENT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "SendOneEvent"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.FederatedEvent.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.BinaryBlob,
      com.atakmap.Tak.Empty> METHOD_BINARY_MESSAGE_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "BinaryMessageStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.BinaryBlob.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.BinaryBlob,
      com.atakmap.Tak.Empty> METHOD_SEND_ONE_BLOB =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "SendOneBlob"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.BinaryBlob.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.Empty,
      com.atakmap.Tak.Identity> METHOD_GET_IDENTITY =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "getIdentity"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Empty.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Identity.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.Subscription,
      com.atakmap.Tak.FederatedEvent> METHOD_CLIENT_EVENT_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "ClientEventStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Subscription.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.FederatedEvent.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.FederatedEvent,
      com.atakmap.Tak.Subscription> METHOD_SERVER_EVENT_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "ServerEventStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.FederatedEvent.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Subscription.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.ClientHealth,
      com.atakmap.Tak.ServerHealth> METHOD_HEALTH_CHECK =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "HealthCheck"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ClientHealth.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ServerHealth.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.Subscription,
      com.atakmap.Tak.ROL> METHOD_CLIENT_ROLSTREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "ClientROLStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Subscription.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ROL.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.ROL,
      com.atakmap.Tak.Subscription> METHOD_SERVER_ROLSTREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "ServerROLStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.ROL.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Subscription.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.Subscription,
      com.atakmap.Tak.FederateGroups> METHOD_SERVER_FEDERATE_GROUPS_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "ServerFederateGroupsStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Subscription.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.FederateGroups.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.atakmap.Tak.FederateGroups,
      com.atakmap.Tak.Subscription> METHOD_CLIENT_FEDERATE_GROUPS_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
          generateFullMethodName(
              "com.atakmap.FederatedChannel", "ClientFederateGroupsStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.FederateGroups.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.atakmap.Tak.Subscription.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FederatedChannelStub newStub(io.grpc.Channel channel) {
    return new FederatedChannelStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FederatedChannelBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new FederatedChannelBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static FederatedChannelFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new FederatedChannelFutureStub(channel);
  }

  /**
   */
  public static abstract class FederatedChannelImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendOneEvent(com.atakmap.Tak.FederatedEvent request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SEND_ONE_EVENT, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.BinaryBlob> binaryMessageStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_BINARY_MESSAGE_STREAM, responseObserver);
    }

    /**
     */
    public void sendOneBlob(com.atakmap.Tak.BinaryBlob request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SEND_ONE_BLOB, responseObserver);
    }

    /**
     */
    public void getIdentity(com.atakmap.Tak.Empty request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Identity> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_IDENTITY, responseObserver);
    }

    /**
     */
    public void clientEventStream(com.atakmap.Tak.Subscription request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.FederatedEvent> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CLIENT_EVENT_STREAM, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.FederatedEvent> serverEventStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_SERVER_EVENT_STREAM, responseObserver);
    }

    /**
     */
    public void healthCheck(com.atakmap.Tak.ClientHealth request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ServerHealth> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HEALTH_CHECK, responseObserver);
    }

    /**
     */
    public void clientROLStream(com.atakmap.Tak.Subscription request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ROL> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CLIENT_ROLSTREAM, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.ROL> serverROLStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_SERVER_ROLSTREAM, responseObserver);
    }

    /**
     * <pre>
     *client federate requests the Federate Server's groups, serverStreamingCall
     * </pre>
     */
    public void serverFederateGroupsStream(com.atakmap.Tak.Subscription request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.FederateGroups> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SERVER_FEDERATE_GROUPS_STREAM, responseObserver);
    }

    /**
     * <pre>
     * the client federate sends its groups to the Federate Server, clientStreamingCall
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.FederateGroups> clientFederateGroupsStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_CLIENT_FEDERATE_GROUPS_STREAM, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_SEND_ONE_EVENT,
            asyncUnaryCall(
              new MethodHandlers<
                com.atakmap.Tak.FederatedEvent,
                com.atakmap.Tak.Empty>(
                  this, METHODID_SEND_ONE_EVENT)))
          .addMethod(
            METHOD_BINARY_MESSAGE_STREAM,
            asyncClientStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.BinaryBlob,
                com.atakmap.Tak.Empty>(
                  this, METHODID_BINARY_MESSAGE_STREAM)))
          .addMethod(
            METHOD_SEND_ONE_BLOB,
            asyncUnaryCall(
              new MethodHandlers<
                com.atakmap.Tak.BinaryBlob,
                com.atakmap.Tak.Empty>(
                  this, METHODID_SEND_ONE_BLOB)))
          .addMethod(
            METHOD_GET_IDENTITY,
            asyncUnaryCall(
              new MethodHandlers<
                com.atakmap.Tak.Empty,
                com.atakmap.Tak.Identity>(
                  this, METHODID_GET_IDENTITY)))
          .addMethod(
            METHOD_CLIENT_EVENT_STREAM,
            asyncServerStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.Subscription,
                com.atakmap.Tak.FederatedEvent>(
                  this, METHODID_CLIENT_EVENT_STREAM)))
          .addMethod(
            METHOD_SERVER_EVENT_STREAM,
            asyncClientStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.FederatedEvent,
                com.atakmap.Tak.Subscription>(
                  this, METHODID_SERVER_EVENT_STREAM)))
          .addMethod(
            METHOD_HEALTH_CHECK,
            asyncUnaryCall(
              new MethodHandlers<
                com.atakmap.Tak.ClientHealth,
                com.atakmap.Tak.ServerHealth>(
                  this, METHODID_HEALTH_CHECK)))
          .addMethod(
            METHOD_CLIENT_ROLSTREAM,
            asyncServerStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.Subscription,
                com.atakmap.Tak.ROL>(
                  this, METHODID_CLIENT_ROLSTREAM)))
          .addMethod(
            METHOD_SERVER_ROLSTREAM,
            asyncClientStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.ROL,
                com.atakmap.Tak.Subscription>(
                  this, METHODID_SERVER_ROLSTREAM)))
          .addMethod(
            METHOD_SERVER_FEDERATE_GROUPS_STREAM,
            asyncServerStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.Subscription,
                com.atakmap.Tak.FederateGroups>(
                  this, METHODID_SERVER_FEDERATE_GROUPS_STREAM)))
          .addMethod(
            METHOD_CLIENT_FEDERATE_GROUPS_STREAM,
            asyncClientStreamingCall(
              new MethodHandlers<
                com.atakmap.Tak.FederateGroups,
                com.atakmap.Tak.Subscription>(
                  this, METHODID_CLIENT_FEDERATE_GROUPS_STREAM)))
          .build();
    }
  }

  /**
   */
  public static final class FederatedChannelStub extends io.grpc.stub.AbstractStub<FederatedChannelStub> {
    private FederatedChannelStub(io.grpc.Channel channel) {
      super(channel);
    }

    private FederatedChannelStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FederatedChannelStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FederatedChannelStub(channel, callOptions);
    }

    /**
     */
    public void sendOneEvent(com.atakmap.Tak.FederatedEvent request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEND_ONE_EVENT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.BinaryBlob> binaryMessageStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_BINARY_MESSAGE_STREAM, getCallOptions()), responseObserver);
    }

    /**
     */
    public void sendOneBlob(com.atakmap.Tak.BinaryBlob request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEND_ONE_BLOB, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getIdentity(com.atakmap.Tak.Empty request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Identity> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_IDENTITY, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clientEventStream(com.atakmap.Tak.Subscription request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.FederatedEvent> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_CLIENT_EVENT_STREAM, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.FederatedEvent> serverEventStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_SERVER_EVENT_STREAM, getCallOptions()), responseObserver);
    }

    /**
     */
    public void healthCheck(com.atakmap.Tak.ClientHealth request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ServerHealth> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HEALTH_CHECK, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clientROLStream(com.atakmap.Tak.Subscription request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.ROL> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_CLIENT_ROLSTREAM, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.ROL> serverROLStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_SERVER_ROLSTREAM, getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     *client federate requests the Federate Server's groups, serverStreamingCall
     * </pre>
     */
    public void serverFederateGroupsStream(com.atakmap.Tak.Subscription request,
        io.grpc.stub.StreamObserver<com.atakmap.Tak.FederateGroups> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_SERVER_FEDERATE_GROUPS_STREAM, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * the client federate sends its groups to the Federate Server, clientStreamingCall
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.atakmap.Tak.FederateGroups> clientFederateGroupsStream(
        io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_CLIENT_FEDERATE_GROUPS_STREAM, getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class FederatedChannelBlockingStub extends io.grpc.stub.AbstractStub<FederatedChannelBlockingStub> {
    private FederatedChannelBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private FederatedChannelBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FederatedChannelBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FederatedChannelBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.atakmap.Tak.Empty sendOneEvent(com.atakmap.Tak.FederatedEvent request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SEND_ONE_EVENT, getCallOptions(), request);
    }

    /**
     */
    public com.atakmap.Tak.Empty sendOneBlob(com.atakmap.Tak.BinaryBlob request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SEND_ONE_BLOB, getCallOptions(), request);
    }

    /**
     */
    public com.atakmap.Tak.Identity getIdentity(com.atakmap.Tak.Empty request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_IDENTITY, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<com.atakmap.Tak.FederatedEvent> clientEventStream(
        com.atakmap.Tak.Subscription request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_CLIENT_EVENT_STREAM, getCallOptions(), request);
    }

    /**
     */
    public com.atakmap.Tak.ServerHealth healthCheck(com.atakmap.Tak.ClientHealth request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HEALTH_CHECK, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<com.atakmap.Tak.ROL> clientROLStream(
        com.atakmap.Tak.Subscription request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_CLIENT_ROLSTREAM, getCallOptions(), request);
    }

    /**
     * <pre>
     *client federate requests the Federate Server's groups, serverStreamingCall
     * </pre>
     */
    public java.util.Iterator<com.atakmap.Tak.FederateGroups> serverFederateGroupsStream(
        com.atakmap.Tak.Subscription request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_SERVER_FEDERATE_GROUPS_STREAM, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class FederatedChannelFutureStub extends io.grpc.stub.AbstractStub<FederatedChannelFutureStub> {
    private FederatedChannelFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private FederatedChannelFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FederatedChannelFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FederatedChannelFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.atakmap.Tak.Empty> sendOneEvent(
        com.atakmap.Tak.FederatedEvent request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEND_ONE_EVENT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.atakmap.Tak.Empty> sendOneBlob(
        com.atakmap.Tak.BinaryBlob request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEND_ONE_BLOB, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.atakmap.Tak.Identity> getIdentity(
        com.atakmap.Tak.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_IDENTITY, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.atakmap.Tak.ServerHealth> healthCheck(
        com.atakmap.Tak.ClientHealth request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HEALTH_CHECK, getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_ONE_EVENT = 0;
  private static final int METHODID_SEND_ONE_BLOB = 1;
  private static final int METHODID_GET_IDENTITY = 2;
  private static final int METHODID_CLIENT_EVENT_STREAM = 3;
  private static final int METHODID_HEALTH_CHECK = 4;
  private static final int METHODID_CLIENT_ROLSTREAM = 5;
  private static final int METHODID_SERVER_FEDERATE_GROUPS_STREAM = 6;
  private static final int METHODID_BINARY_MESSAGE_STREAM = 7;
  private static final int METHODID_SERVER_EVENT_STREAM = 8;
  private static final int METHODID_SERVER_ROLSTREAM = 9;
  private static final int METHODID_CLIENT_FEDERATE_GROUPS_STREAM = 10;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FederatedChannelImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(FederatedChannelImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_ONE_EVENT:
          serviceImpl.sendOneEvent((com.atakmap.Tak.FederatedEvent) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty>) responseObserver);
          break;
        case METHODID_SEND_ONE_BLOB:
          serviceImpl.sendOneBlob((com.atakmap.Tak.BinaryBlob) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty>) responseObserver);
          break;
        case METHODID_GET_IDENTITY:
          serviceImpl.getIdentity((com.atakmap.Tak.Empty) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.Identity>) responseObserver);
          break;
        case METHODID_CLIENT_EVENT_STREAM:
          serviceImpl.clientEventStream((com.atakmap.Tak.Subscription) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.FederatedEvent>) responseObserver);
          break;
        case METHODID_HEALTH_CHECK:
          serviceImpl.healthCheck((com.atakmap.Tak.ClientHealth) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.ServerHealth>) responseObserver);
          break;
        case METHODID_CLIENT_ROLSTREAM:
          serviceImpl.clientROLStream((com.atakmap.Tak.Subscription) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.ROL>) responseObserver);
          break;
        case METHODID_SERVER_FEDERATE_GROUPS_STREAM:
          serviceImpl.serverFederateGroupsStream((com.atakmap.Tak.Subscription) request,
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.FederateGroups>) responseObserver);
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
        case METHODID_BINARY_MESSAGE_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.binaryMessageStream(
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.Empty>) responseObserver);
        case METHODID_SERVER_EVENT_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.serverEventStream(
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription>) responseObserver);
        case METHODID_SERVER_ROLSTREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.serverROLStream(
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription>) responseObserver);
        case METHODID_CLIENT_FEDERATE_GROUPS_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.clientFederateGroupsStream(
              (io.grpc.stub.StreamObserver<com.atakmap.Tak.Subscription>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_SEND_ONE_EVENT,
        METHOD_BINARY_MESSAGE_STREAM,
        METHOD_SEND_ONE_BLOB,
        METHOD_GET_IDENTITY,
        METHOD_CLIENT_EVENT_STREAM,
        METHOD_SERVER_EVENT_STREAM,
        METHOD_HEALTH_CHECK,
        METHOD_CLIENT_ROLSTREAM,
        METHOD_SERVER_ROLSTREAM,
        METHOD_SERVER_FEDERATE_GROUPS_STREAM,
        METHOD_CLIENT_FEDERATE_GROUPS_STREAM);
  }

}
