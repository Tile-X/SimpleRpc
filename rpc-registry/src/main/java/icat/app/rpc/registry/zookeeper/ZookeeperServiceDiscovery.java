package icat.app.rpc.registry.zookeeper;

import icat.app.rpc.registry.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static icat.app.rpc.registry.zookeeper.ZookeeperConstant.*;

public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);
    private final String zkServiceAddress;

    public ZookeeperServiceDiscovery(String zkServiceAddress) {
        this.zkServiceAddress = zkServiceAddress;
    }

    @Override
    public String discovery(String serviceName) {
        ZkClient zkClient = new ZkClient(zkServiceAddress, ZK_SESSION_TIMEOUT, ZK_CONNECTION_TIMEOUT);
        log.info("Connect to Zookeeper");
        try {
            String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s/", servicePath));
            }
            List<String> addresses = zkClient.getChildren(servicePath);
            if (CollectionUtils.isEmpty(addresses)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            String address = addresses.get(0);
            if (addresses.size() > 1) {
                address = addresses.get(ThreadLocalRandom.current().nextInt(addresses.size()));
            }
            log.info("Discovery service [{}] address [{}]", serviceName, address);
            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }
    }

}
