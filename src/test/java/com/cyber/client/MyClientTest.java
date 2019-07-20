package com.cyber.client;

import com.cyber.Bootstrap;
import com.cyber.contracts.Asset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MyClientTest extends AbstractJUnit4SpringContextTests {
    static {new Bootstrap(){};}
    private static Logger logger = LoggerFactory.getLogger(MyClientTest.class);
    @Autowired
    private MyClient client;


    @Test
    public void testLoadAssetAddr() throws Exception {
        logger.info(client.loadAssetAddr());
    }

    @Test
    public void recordAssetAddr() {
    }

    @Test
    public void deployAssetAndRecordAddr() {
        client.deployAssetAndRecordAddr();
    }

    @Test
    public void queryAssetAmount() {
    }

    @Test
    public void registerAssetAccount() {
    }

    @Test
    public void transferAsset() {
    }
}