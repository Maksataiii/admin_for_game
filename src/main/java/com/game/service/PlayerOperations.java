package com.game.service;

import org.springframework.stereotype.Service;

@Service
public class PlayerOperations {
    int calculateLevel(int experience) {
        return (int) ((Math.sqrt(2500 + 200 * experience) - 50) / 100);
    }

    int calculateExpToNextLevel(int experience) {
        int level = calculateLevel(experience);
        return 50 * (level + 1) * (level + 2) - experience;
    }
}
