package com.netty.customer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 功能描述：页面控制
 * 作者：唐泽齐
 */
@Controller
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
public class HtmlController {

    @GetMapping("")
    public String index(Model model) {
        return "index";
    }

}
