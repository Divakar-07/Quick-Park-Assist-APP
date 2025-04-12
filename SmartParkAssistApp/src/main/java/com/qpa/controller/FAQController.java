package com.qpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.dto.QuestionRequest;
import com.qpa.service.FAQService;

@CrossOrigin(origins = "http://localhost:7213")
@RestController
@RequestMapping("/faq")
public class FAQController {
    @Autowired
    private FAQService faqService;

    @PostMapping
    public String getFAQResponse(@RequestBody QuestionRequest request) {
        return faqService.getAnswer(request.getQuestion());
    }
}
