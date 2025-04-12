package com.qpa.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qpa.entity.FAQ;
import com.qpa.repository.FAQRepository;

@Service
public class FAQService {

    @Autowired
    private FAQRepository faqRepository;

    public String getAnswer(String userQuestion) {
        List<FAQ> faqs = faqRepository.findAll();
        if (faqs.isEmpty()) {
            return "No FAQs available.";
        }

        FAQ bestMatch = null;
        double maxSimilarity = 0.0;
        Set<String> userWords = getWords(userQuestion);

        for (FAQ faq : faqs) {
            Set<String> faqWords = getWords(faq.getQuestion());

            Set<String> intersection = new HashSet<>(userWords);
            intersection.retainAll(faqWords);

            double similarityPercent = (faqWords.size() > 0)
                ? (intersection.size() * 100.0 / faqWords.size())
                : 0.0;

            if (similarityPercent > maxSimilarity) {
                maxSimilarity = similarityPercent;
                bestMatch = faq;
            }
        }

        if (bestMatch != null && maxSimilarity >= 30.0) {
            return String.format(
                "Answer (Match: %.1f%%): %s",
                maxSimilarity,
                bestMatch.getAnswer()
            );
        } else {
            return "Sorry, I couldn't find an answer.";
        }
    }

    private Set<String> getWords(String sentence) {
        return new HashSet<>(Arrays.asList(sentence.toLowerCase().split("\\W+")));
    }
}

