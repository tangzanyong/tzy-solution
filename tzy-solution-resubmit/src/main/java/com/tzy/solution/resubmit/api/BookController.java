package com.tzy.solution.resubmit.api;

import com.tzy.solution.resubmit.annotation.CacheLock;
import com.tzy.solution.resubmit.annotation.CacheParam;
import com.tzy.solution.resubmit.annotation.LocalLock;
import com.tzy.solution.resubmit.annotation.Token;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tangzanyong
 * @description @TODO
 * @date 2020/5/22
 **/
@RestController
@RequestMapping("/books")
public class BookController {

    //打开新增页面请求
    @RequestMapping("/add")
    @Token(save=true)
    public String add() {
        return "index";
    }

    //提交保存数据请求
    @RequestMapping("/save")
    @Token(remove=true)
    public String save(@RequestParam String token) {
        return "success - " + token;
    }

    //提交保存2
    @RequestMapping("/save2")
    @LocalLock(key = "book:arg[0]")
    public String save2(@RequestParam String token) {
        return "success - " + token;
    }

    @CacheLock(prefix = "books")
    @GetMapping("/query")
    public String query(@CacheParam(name = "token") @RequestParam String token) {
        return "success - " + token;
    }
}
