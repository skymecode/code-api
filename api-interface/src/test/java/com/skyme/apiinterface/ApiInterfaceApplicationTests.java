package com.skyme.apiinterface;


import com.skyme.apiclientsdk.client.ApiClient;
import com.skyme.apiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApiInterfaceApplicationTests {
    @Resource
    private ApiClient apiClient;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUsername("skyme");
        String res = apiClient.getUserNameByPost(user);
        System.out.println(res);
    }

}
