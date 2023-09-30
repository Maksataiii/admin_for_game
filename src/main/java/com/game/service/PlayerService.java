package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.requests.PlayerRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Transactional
    public List<Player> getFilteredPlayers(String name, String title, Race race, Profession profession,
        Long after, Long before, Boolean banned,
        Integer minExperience, Integer maxExperience,
        Integer minLevel, Integer maxLevel, PlayerOrder order,
        Integer pageNumber, Integer pageSize){
        Date afterDate = null;
        Date beforeDate = null;
        if(after!=null){
            afterDate = new Date(after);}
        if(before!=null){
            beforeDate = new Date(before);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        System.out.println(pageSize +"  "+pageNumber);
        return playerRepository.findFilteredPlayers(name, title, race, profession, afterDate, beforeDate, banned, minExperience, maxExperience, minLevel, maxLevel, order,pageable);
    }

    @Transactional
    public Player createPlayer(PlayerRequest playerRequest) {
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

    public boolean isValidPlayerRequest(PlayerRequest player) {
        // Проверка длины имени
        if (player.getName() != null && player.getName().length() > 12) {
            return false;
        }
        // Проверка длины титула
        if (player.getTitle() != null && player.getTitle().length() > 30) {
            return false;
        }
        // Проверка даты рождения (если она задана)
        if (player.getBirthday() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Long birthday = player.getBirthday();

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
        if (player.getExperience()==null || player.getExperience() < 0 || player.getExperience() >= 10000000) {
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
        Player player = playerRepository.findById(id).orElse(null);
        if(player!=null){
        if(playerRequest.getName()!=null) {
            player.setName(playerRequest.getName());
        } else playerRequest.setName(player.getName());
        if(playerRequest.getTitle()!=null) {
            player.setTitle(playerRequest.getTitle());
        } else playerRequest.setTitle(player.getTitle());
        if(playerRequest.getRace()!=null){
            player.setRace(playerRequest.getRace());
        } else playerRequest.setRace(player.getRace());
        if(playerRequest.getProfession()!=null) {
            player.setProfession(playerRequest.getProfession());
        } else playerRequest.setProfession(player.getProfession());
        if(playerRequest.getExperience()!=null) {
            int newExperience = playerRequest.getExperience();
            int newLevel = playerOperations.calculateLevel(newExperience);
            int newExpToNextLevel = playerOperations.calculateExpToNextLevel(newExperience);
            player.setExperience(newExperience);
            player.setLevel(newLevel);
            player.setUntilNextLevel(newExpToNextLevel);
        } else playerRequest.setExperience(player.getExperience());
        player.setBanned(playerRequest.getBanned() != null ? playerRequest.getBanned() : false);
        if(playerRequest.getBirthday()!=null) {
            Date date = new Date(playerRequest.getBirthday());
            player.setBirthday(date);
        } else playerRequest.setBirthday(new Date(player.getBirthday()));
        }
        if(!isValidPlayerRequest(playerRequest)){
            return null;
        }
        // Сохраняем игрока в базе данных
        return playerRepository.save(player);
    }

}
