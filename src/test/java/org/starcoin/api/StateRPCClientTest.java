/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starcoin.api;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.starcoin.bean.ListResource;
import org.starcoin.bean.Resource;
import org.starcoin.bean.TableInfo;
import org.starcoin.bean.TokenInfo;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class StateRPCClientTest {
    private StateRPCClient stateRPCClient;

    @Before
    public void setUp() throws Exception {
        //stateRPCClient = new StateRPCClient(new URL("http://localhost:9850"));
        stateRPCClient = new StateRPCClient(new URL("https://barnard-seed.starcoin.org"));
    }

    @Test
    public void testGetState() throws MalformedURLException {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
            System.out.println(format.format(new Date()));
            stateRPCClient.getState("0x5c04bb88984f933151bf69504692074d");
            long amount = stateRPCClient.getAddressAmount("0x5c04bb88984f933151bf69504692074d", "0x9350502a3af6c617e9a42fa9e306a385::BX_USDT::BX_USDT");
            System.out.println("amount: " + amount);
            amount = stateRPCClient.getAddressAmount("0x5c04bb88984f933151bf69504692074d", "0x00000000000000000000000000000001::STC::STC");
            System.out.println("amount: " + amount);
            TokenInfo tokenInfo = stateRPCClient.getTokenInfo("0x44366bba9bc9ed51bc8f564ecb18b12a", "0x44366bba9bc9ed51bc8f564ecb18b12a::DummyToken::BBB");
            System.out.println(tokenInfo);

            String token = "0x8c109349c6bd91411d6bc962e080c4a3::STAR::STAR";
            System.out.println(token.substring(0, 34));
            tokenInfo = stateRPCClient.getTokenInfo("0x8c109349c6bd91411d6bc962e080c4a3", "0x8c109349c6bd91411d6bc962e080c4a3::STAR::STAR");
            System.out.println(tokenInfo);
        } catch (JSONRPC2SessionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStateWithRoot() throws JSONRPC2SessionException {
        ListResource resource = stateRPCClient.getState("0x86fDDFFbBB603C428e5c74442CE1e966", true, "0x5ee196ac92839743e79db7e6a7d75acdd4afe02b3c89c036e498f66df996c0cf");
        System.out.println(resource);
        for (String key : resource.getResources().keySet()) {
            if (key.contains("TokenSwapPair")) {
                String tokenPair = key.substring(key.indexOf("<") + 1, key.length() - 1);
                String[] tokens = tokenPair.split(",");
                if (tokens.length != 2) {
                    continue;
                }
                System.out.println("x: " + tokens[0] + ", y: " + tokens[1]);
                long xReserve = resource.getResources().get(key).getJson().get("token_x_reserve").get("value").longValue();
                long yReserve = resource.getResources().get(key).getJson().get("token_y_reserve").get("value").longValue();
                System.out.println("x: " + xReserve + ", y: " + yReserve);
            }
        }
    }

    @Test
    public void testGetAddressAmountValue() {
        BigInteger amount = stateRPCClient.getAddressAmountValue("0x86fDDFFbBB603C428e5c74442CE1e966", "0x49142e24bf3b34b323b3bd339e2434e3::AWW::AWW");
        System.out.println(amount);
        amount = stateRPCClient.getAddressAmountValue("0x86fDDFFbBB603C428e5c74442CE1e966", "0x00000000000000000000000000000001::STC::STC");
        System.out.println(amount);
    }

    @Test
    public void testGetAllResourcesByAddress() throws JSONRPC2SessionException {
        //查询所有
        ListResource listResource = stateRPCClient.getAllResourcesByAddress("0x00000000000000000000000000000001");
        Map<String, Resource> resources = listResource.getResources();
        System.out.println(resources.size());
    }

    @Test
    public void testGetResourceListByPage() throws JSONRPC2SessionException {
        //分页查询 10 个
        ListResource listResource = stateRPCClient.getResourceListByPage("0x00000000000000000000000000000001", 1, 10);
        System.out.println(listResource.getResources().size());
    }

    @Test
    public void testGetAllResourcesByResourceTypes() throws JSONRPC2SessionException {
        //指定资源类型查询
        String address = "0x86fDDFFbBB603C428e5c74442CE1e966";
        List<String> resTypes1 = Arrays.asList("0x00000000000000000000000000000001::Account::Balance<0x00000000000000000000000000000001::STC::STC>");
        ListResource listResource = stateRPCClient.getAllResourcesByResourceTypes(address, resTypes1);
        System.out.println(listResource);
        System.out.println(listResource.getResources().size());

        List<String> resTypes2 = Arrays.asList("0x00000000000000000000000000000001::Account::Balance<0x00000000000000000000000000000001::STC::STC>"
                , "0x00000000000000000000000000000001::Account::Balance<0x8c109349c6bd91411d6bc962e080c4a3::STAR::STAR>");
        ListResource listResource2 = stateRPCClient.getAllResourcesByResourceTypes(address, resTypes2);
        System.out.println(listResource2);
        System.out.println(listResource2.getResources().size());
    }

    @Test
    public void testGetAllResourcesByTypeTags() throws JSONRPC2SessionException {
        //指定 TypeTags 查询
        String address = "0x86fDDFFbBB603C428e5c74442CE1e966";
        List<String> typeTags1 = Arrays.asList("0x00000000000000000000000000000001::Account::Balance");
        ListResource listResource = stateRPCClient.getAllResourcesByTypeTags(address, typeTags1);
        System.out.println(listResource);
        System.out.println(listResource.getResources().size());

        List<String> typeTags2 = Arrays.asList("0x00000000000000000000000000000001::Account::Balance", "0x00000000000000000000000000000001::Account::Account");
        ListResource listResource2 = stateRPCClient.getAllResourcesByTypeTags(address, typeTags2);
        System.out.println(listResource2);
        System.out.println(listResource2.getResources().size());
    }

    @Test
    public void testGetTableInfo() throws JSONRPC2SessionException, JsonProcessingException {
        String address = "0x86fDDFFbBB603C428e5c74442CE1e966";
        TableInfo tableInfo = stateRPCClient.getTableInfo(address);
        if (tableInfo != null) {
            System.out.println(tableInfo);
        }
    }

}
