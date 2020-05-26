package com.tzy.solution.resubmit;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.tzy.solution.resubmit.annotation.CacheLock

/**
 * @author tangzanyong
 * @description @TODO
 * @date 2020/5/26
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {
    @org.junit.Test
    public void test1(){
        System.out.println(CacheLock);
    }
}
