package com.cyber.actions;

import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created on 2019/7/21-3:55
 *
 * @author lihb/l786112323@gmail.com
 */
public class Deployer {
    private final ChannelEthereumService service;
    private Web3j web3j;
    private Credentials credentials;

    public Deployer(ChannelEthereumService service) {
        this.service = service;
    }

    public void init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        this.web3j = Web3j.build(service, 1);
        this.credentials = Credentials.create(Keys.createEcKeyPair());
    }


}
