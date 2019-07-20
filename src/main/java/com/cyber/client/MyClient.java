package com.cyber.client;


import com.cyber.contracts.Asset;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Properties;

/**
 * Created on 2019/7/21-2:59
 *
 * @author lihb/l786112323@gmail.com
 */

public class MyClient {

    private final ChannelEthereumService service;
    private static Logger logger = LoggerFactory.getLogger(MyClient.class);

    private Web3j web3j;
    private Credentials credentials;


    public void recordAssetAddr(String address) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.setProperty("address", address);
        final Resource contractResource = new ClassPathResource("contract.properties");
        FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
        prop.store(fileOutputStream, "contract address");
    }

    public String loadAssetAddr() throws Exception {
        // load Asset contact address from contract.properties
        Properties prop = new Properties();
        final Resource contractResource = new ClassPathResource("contract.properties");
        prop.load(contractResource.getInputStream());

        String contractAddress = prop.getProperty("address");
        if (contractAddress == null || contractAddress.trim().equals("")) {
            throw new Exception(" load Asset contract address failed, please deploy it first. ");
        }
        logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    private static BigInteger gasPrice = new BigInteger("30000000");
    private static BigInteger gasLimit = new BigInteger("30000000");

    public void deployAssetAndRecordAddr() {

        try {
            Asset asset = Asset.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();
            System.out.println(" deploy Asset success, contract address is " + asset.getContractAddress());

            recordAssetAddr(asset.getContractAddress());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
        }
    }

    public void queryAssetAmount(String assetAccount) {
        try {
            String contractAddress = loadAssetAddr();

            Asset asset = Asset.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            Tuple2<BigInteger, BigInteger> result = asset.select(assetAccount).send();
            if (result.getValue1().compareTo(new BigInteger("0")) == 0) {
                System.out.printf(" asset account %s, value %s \n", assetAccount, result.getValue2());
            } else {
                System.out.printf(" %s asset account is not exist \n", assetAccount);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
        }
    }

    public void registerAssetAccount(String assetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();

            Asset asset = Asset.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            TransactionReceipt receipt = asset.register(assetAccount, amount).send();
            List<Asset.RegisterEventEventResponse> response = asset.getRegisterEventEvents(receipt);
            if (!response.isEmpty()) {
                if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
                    System.out.printf(" register asset account success => asset: %s, value: %s \n", assetAccount,
                            amount);
                } else {
                    System.out.printf(" register asset account failed, ret code is %s \n",
                            response.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
        }
    }

    public void transferAsset(String fromAssetAccount, String toAssetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount).send();
            List<Asset.TransferEventEventResponse> response = asset.getTransferEventEvents(receipt);
            if (!response.isEmpty()) {
                if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
                    System.out.printf(" transfer success => from_asset: %s, to_asset: %s, amount: %s \n",
                            fromAssetAccount, toAssetAccount, amount);
                } else {
                    System.out.printf(" transfer asset account failed, ret code is %s \n",
                            response.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
        }
    }

    public MyClient(ChannelEthereumService service) {
        this.service = service;
    }

    public void init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        this.web3j = Web3j.build(service, 1);
        this.credentials = Credentials.create(Keys.createEcKeyPair());
    }
}
