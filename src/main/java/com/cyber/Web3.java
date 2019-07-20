package com.cyber;

import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.omg.CORBA.BooleanSeqHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigInteger;

/**
 * Created on 2019/7/20-15:38
 *
 * @author lihb/l786112323@gmail.com
 */
public class Web3 extends Bootstrap {
    private static Logger logger = LoggerFactory.getLogger(Web3.class);

    @Autowired
    public Service service;
    public static void main(String[] args) throws Exception {
        //读取配置文件，SDK与区块链节点建立连接
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Service service = context.getBean(Service.class);
        service.run();
        logger.info("run");
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);

        //获取Web3j对象
        Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
        logger.info("build");
        //通过Web3j对象调用API接口getBlockNumber
        BigInteger blockNumber = web3j.getBlockNumber().send().getBlockNumber();
        logger.info("{}",blockNumber);
    }
}
