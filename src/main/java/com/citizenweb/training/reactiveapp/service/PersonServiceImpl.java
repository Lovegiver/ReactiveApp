package com.citizenweb.training.reactiveapp.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class PersonServiceImpl implements PersonService {

    @Override
    public String buildName() throws InterruptedException {
        Thread.sleep(200);
        final String VOWEL = "V";
        final String CONSONANT = "C";
        StringBuilder stringBuilder = new StringBuilder();
        char[] vowels = new char[]{'A','E','I','O','U','Y'};
        char[] consonants = new char[]{'B','C','D','F','G','H','J','K','L','M','N','P','Q','R','S','T','V','W','X','Z'};
        String namePattern = "CVCCVCV";
        for (int i = 0; i < namePattern.length(); i++) {
            String letterType = namePattern.substring(i,i+1);
            int seed;
            char c = 0;
            switch (letterType) {
                case CONSONANT:
                    seed = (int) (Math.random() * consonants.length);
                    c = consonants[seed];
                    break;
                case VOWEL:
                    seed = (int) (Math.random() * vowels.length);
                    c = vowels[seed];
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Override
    public int computeAge() {
        return (int) (Math.random()*100);
    }

    @Override
    public long countCharOccurrencesInWord(String word, char c) {
        return word.chars().filter(value -> c == value).count();
    }

    @Override
    public double computeMean(List<String> words, char c) {
        return words.stream().mapToLong(word -> countCharOccurrencesInWord(word,c)).average().orElse(0d);
    }

}
