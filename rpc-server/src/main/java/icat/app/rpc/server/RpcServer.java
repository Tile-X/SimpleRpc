package icat.app.rpc.server;

import icat.app.rpc.common.codec.RpcDecoder;
import icat.app.rpc.common.codec.RpcEncoder;
import icat.app.rpc.common.entity.RpcRequest;
import icat.app.rpc.common.entity.RpcResponse;
import icat.app.rpc.common.serializer.ProtostuffSerializer;
import icat.app.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ProtostuffSerializer serializer = new ProtostuffSerializer();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new RpcDecoder(RpcRequest.class, serializer));
                            pipeline.addLast(new RpcEncoder(RpcResponse.class, serializer));
                            pipeline.addLast(new RpcServerHandler());
                        }
                    });
            String[] addresses = serviceAddress.split(":");
            String host = addresses[0];
            int port = Integer.parseInt(addresses[1]);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    log.info("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            log.info("Server started on {}:{}", host, port);
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.interfaceName().getName();
                String serviceVersion = rpcService.serviceVersion();
                if (serviceVersion != null) {
                    serviceVersion = serviceVersion.trim();
                    if (!serviceVersion.isEmpty()) {
                        serviceName = serviceName + "-" + serviceVersion;
                    }
                }
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }

}
