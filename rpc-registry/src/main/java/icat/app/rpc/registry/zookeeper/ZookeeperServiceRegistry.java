package icat.app.rpc.registry.zookeeper;

import icat.app.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import static icat.app.rpc.registry.zookeeper.ZookeeperConstant.*;

@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private final ZkClient zkClient;

    public ZookeeperServiceRegistry(String zkServersAddress) {
        zkClient = new ZkClient(zkServersAddress, ZK_SESSION_TIMEOUT, ZK_CONNECTION_TIMEOUT);
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        if (!zkClient.exists(ZK_REGISTRY_PATH)) {
            zkClient.createPersistent(ZK_REGISTRY_PATH);
            log.info("create registry node: {}", ZK_REGISTRY_PATH);
        }
        String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.info("create service node: {}", servicePath);
        }
        String serviceAddressPath = servicePath + "/address-" + serviceAddress;
        String serviceNode = zkClient.createEphemeralSequential(serviceAddressPath, serviceAddress);
        log.info("create service node: {}", serviceNode);
    }

}
