package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.requests.PlayerRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerOperations playerOperations;

    public PlayerService(PlayerRepository playerRepository, PlayerOperations playerOperations) {
        this.playerRepository = playerRepository;
        this.playerOperations = playerOperations;
    }
    @Transactional(readOnly = true)
    public List<Player> getFilteredPlayers(String name, String title, Race race, Profession profession,
        Long after, Long before, Boolean banned,
        Integer minExperience, Integer maxExperience,
        Integer minLevel, Integer maxLevel, PlayerOrder order,
        Integer pageNumber, Integer pageSize){
        return  playerRepository.findFilteredPlayers(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);
    }

    @Transactional
    public Player createPlayer(PlayerRequest playerRequest) {
        // Проверка на соответствие условиям валидации
        if (!isValidPlayerRequest(playerRequest)) {
            throw new IllegalArgumentException("Invalid player data");
        }

        // Создание нового игрока и расчет уровня и опыта
        Player player = new Player();
        player.setName(playerRequest.getName());
        player.setTitle(playerRequest.getTitle());
        player.setRace(playerRequest.getRace());
        player.setProfession(playerRequest.getProfession());
        player.setBanned(playerRequest.getBanned() != null ? playerRequest.getBanned() : false);
        Date date = new Date(playerRequest.getBirthday());
        player.setBirthday(date);

        // Рассчитываем уровень и опыт
        int newExperience = playerRequest.getExperience();
        int newLevel = playerOperations.calculateLevel(newExperience);
        int newExpToNextLevel = playerOperations.calculateExpToNextLevel(newExperience);

        player.setExperience(newExperience);
        player.setLevel(newLevel);
        player.setUntilNextLevel(newExpToNextLevel);

        // Сохраняем игрока в базе данных
        return playerRepository.save(player);
    }

    public boolean isValidPlayerRequest(PlayerRequest playerRequest) {
        // Проверка длины имени
        if (playerRequest.getName() != null && playerRequest.getName().length() > 12) {
            return false;
        }

        // Проверка длины титула
        if (playerRequest.getTitle() != null && playerRequest.getTitle().length() > 30) {
            return false;
        }

        // Проверка даты рождения (если она задана)
        if (playerRequest.getBirthday() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Long birthday = playerRequest.getBirthday();

                // Проверка, что дата находится в допустимом диапазоне (2000-01-01..3000-12-31)
                long minAllowedDate = dateFormat.parse("2000-01-01").getTime();
                long maxAllowedDate = dateFormat.parse("3000-12-31").getTime();
                if (birthday <= minAllowedDate || birthday >= maxAllowedDate) {
                    return false;
                }
            } catch (ParseException e) {
                // Если дата не может быть распарсена, считаем ее невалидной
                return false;
            }
        }

        // Проверка опыта
        if (playerRequest.getExperience()==null || playerRequest.getExperience() < 0 || playerRequest.getExperience() >= 10_000_000) {
            return false;
        }
        return true;
    }


    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public boolean deletePlayerById(Long id) {
        Player player = playerRepository.findById(id).orElse(null);
        if(player==null) return false;
        playerRepository.delete(player);
        return true;
    }

    public Integer countPlayersWithFilters(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        Date afterDate = null;
        Date beforeDate = null;
        if(after!=null){
            afterDate = new Date(after);}
        if(before!=null){
            beforeDate = new Date(before);
        }
        return playerRepository.countByFilters(name, title, race, profession, afterDate, beforeDate, banned, minExperience, maxExperience, minLevel, maxLevel);
    }

    @Transactional
    public Player updatePlayer(Long id, PlayerRequest playerRequest) {
        Player player = getPlayerById(id);
        if(player!=null){
        if(playerRequest.getName()!=null) {
            player.setName(playerRequest.getName());
        }
        if(playerRequest.getTitle()!=null) {
            player.setTitle(playerRequest.getTitle());
        }
        if(playerRequest.getRace()!=null){
            player.setRace(playerRequest.getRace());
        }
        if(playerRequest.getProfession()!=null) {
            player.setProfession(playerRequest.getProfession());
        }
        if(playerRequest.getExperience()!=null) {
            int newExperience = playerRequest.getExperience();
            int newLevel = playerOperations.calculateLevel(newExperience);
            int newExpToNextLevel = playerOperations.calculateExpToNextLevel(newExperience);

            player.setExperience(newExperience);
            player.setLevel(newLevel);
            player.setUntilNextLevel(newExpToNextLevel);
        }
        player.setBanned(playerRequest.getBanned() != null ? playerRequest.getBanned() : false);
        if(playerRequest.getBirthday()!=null) {
            Date date = new Date(playerRequest.getBirthday());
            player.setBirthday(date);
        }
        }
        // Сохраняем игрока в базе данных
        return playerRepository.save(player);
    }

}
