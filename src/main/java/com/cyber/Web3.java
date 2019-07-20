package com.cyber;

import com.cyber.client.AssetClient;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.omg.CORBA.BooleanSeqHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * Created on 2019/7/20-15:38
 *
 * @author lihb/l786112323@gmail.com
 */
public class Web3 extends Bootstrap {
    private static Logger logger = LoggerFactory.getLogger(Web3.class);
    private Service service;

    public Web3(Service service) {
        this.service = service;
    }

    public static void main(String[] args) throws Exception {
        try (ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml")) {
            appContext.registerShutdownHook();
            Service service = appContext.getBean(Service.class);
            Web3 web = new Web3(service);
            AssetClient client = new AssetClient();
            client.initialize();
            logger.info("{}", web.getBlockNumber());
            appContext.stop();
        }
    }

    public BigInteger getBlockNumber() throws Exception {
        service.run();
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        // 交易超时时间100秒(默认40)
        channelEthereumService.setTimeout(100000);
        //获取Web3j对象
        Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
        //通过Web3j对象调用API接口getBlockNumber
        return web3j.getBlockNumber().send().getBlockNumber();
    }
}
