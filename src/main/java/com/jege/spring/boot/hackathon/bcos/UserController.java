package com.jege.spring.boot.hackathon.bcos;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cyber.Web3;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.EncryptType;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

import org.fisco.bcos.asset.contract.Asset;
import org.fisco.bcos.web3j.tx.gas.ContractGasProvider;
import org.fisco.bcos.web3j.abi.datatypes.Type;

@RestController
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(Web3.class);

    @RequestMapping("/user/getnew")
    public String getnew() throws Exception {
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
        logger.info("blockNum: {}",blockNumber);


        //创建普通外部账户
        EncryptType.encryptType = 0;
//创建国密外部账户，向国密区块链节点发送交易需要使用国密外部账户
// EncryptType.encryptType = 1;
        Credentials credentials = GenCredential.create();
//账户地址
        String address = credentials.getAddress();
        logger.info("address: {}",address);
//账户私钥
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        logger.info("privateKey: {}",privateKey);
//账户公钥
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        logger.info("publicKey: {}",publicKey);
        JSONObject userinfo = new JSONObject();
        userinfo.put("address", address);
        userinfo.put("private", privateKey);
        userinfo.put("public", publicKey);
        return userinfo.toJSONString();
    }

    @RequestMapping("/hello2")
    public List<String> hello2() {
        return Arrays.asList(new String[] { "A", "B", "C" });
    }

    @RequestMapping(value = "/testdeploy", method = RequestMethod.POST, consumes = "text/plain")
    public String testdeploy(@RequestBody String payload) throws Exception {
        JSONObject req = JSON.parseObject(payload);
        //读取配置文件，sdk与区块链节点建立连接，获取web3j对象
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Service service = context.getBean(Service.class);
        service.run();
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        channelEthereumService.setTimeout(10000);
        Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
        //准备部署和调用合约的参数
        BigInteger gasPrice = new BigInteger("300000000");
        BigInteger gasLimit = new BigInteger("300000000");
        String privateKey = req.getString("priv");
        //指定外部账户私钥，用于交易签名
        Credentials credentials = GenCredential.create(privateKey);
        //部署合约
        Asset contract = Asset.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();

        String address = contract.getContractAddress();

        JSONObject contractinfo = new JSONObject();
        contractinfo.put("address", address);
        return contractinfo.toJSONString();
    }

    @RequestMapping(value = "/testsend", method = RequestMethod.POST, consumes = "text/plain")
    public List<String> testsend(@RequestBody String payload) throws Exception {
        JSONObject req = JSON.parseObject(payload);
        //读取配置文件，sdk与区块链节点建立连接，获取web3j对象
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Service service = context.getBean(Service.class);
        service.run();
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        channelEthereumService.setTimeout(10000);
        Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
        //准备部署和调用合约的参数
        BigInteger gasPrice = new BigInteger("300000000");
        BigInteger gasLimit = new BigInteger("300000000");
        String privateKey = req.getString("priv");
        //指定外部账户私钥，用于交易签名
        Credentials credentials = GenCredential.create(privateKey);
        String address = req.getString("conaddress");
        //根据合约地址加载合约
        Asset contract = Asset.load(address, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
        //调用合约方法发送交易
        BigInteger value = new BigInteger("3000");
        TransactionReceipt transactionReceipt = contract.register("Alice", value).send();
        //查询合约方法查询该合约的数据状态
//        Type result = contract.getRegisterEventEvents(transactionReceipt);

        return Arrays.asList(new String[]{"A", "B", "C"});
    }
}
