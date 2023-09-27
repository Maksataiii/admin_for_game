package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.requests.PlayerRequest;
import com.game.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<Player> getPlayers(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "race", required = false) Race race,
            @RequestParam(name = "profession", required = false) Profession profession,
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "banned", required = false) Boolean banned,
            @RequestParam(name = "minExperience", required = false) Integer minExperience,
            @RequestParam(name = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(name = "minLevel", required = false) Integer minLevel,
            @RequestParam(name = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(name = "order", required = false) PlayerOrder order,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        // Вызов сервиса для получения отфильтрованного и пагинированного списка игроков
        List<Player> filteredPlayers = playerService.getFilteredPlayers(
                name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel, order,
                pageNumber, pageSize);
        return filteredPlayers;
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getPlayerCount(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "race", required = false) Race race,
            @RequestParam(name = "profession", required = false) Profession profession,
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "banned", required = false) Boolean banned,
            @RequestParam(name = "minExperience", required = false) Integer minExperience,
            @RequestParam(name = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(name = "minLevel", required = false) Integer minLevel,
            @RequestParam(name = "maxLevel", required = false) Integer maxLevel
    ) {
        Integer playerCount = playerService.countPlayersWithFilters(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        return ResponseEntity.ok(playerCount);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody PlayerRequest playerRequest) {
        if (!playerService.isValidPlayerRequest(playerRequest)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Player player = playerService.createPlayer(playerRequest);
        return ResponseEntity.ok(player);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Player player = playerService.getPlayerById(id);
        if (player==null) {
            // Возвращаем ошибку 404 Not Found, если игрок не найден
            return ResponseEntity.notFound().build();
        }
        // Возвращаем игрока с кодом 200 OK
        return ResponseEntity.ok(player);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") Long id) {
        // Проверьте валидность ID
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        // Попробуйте удалить игрока
        boolean deleted = playerService.deletePlayerById(id);
        if (deleted) {
            return ResponseEntity.ok().build(); // Возвращаем 200 OK в случае успешного удаления
        } else {
            return ResponseEntity.notFound().build(); // Возвращаем 404 Not Found, если игрок не найден
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody PlayerRequest playerRequest) {
        Player player = playerService.getPlayerById(id);
        if (playerRequest.getBirthday() == null && playerRequest.getBanned()==null && playerRequest.getName()==null&&playerRequest.getExperience()==null&&playerRequest.getTitle()==null&&playerRequest.getProfession()==null&&playerRequest.getRace()==null) {
            return ResponseEntity.ok(player);
        }
        if (id==0 || !playerService.isValidPlayerRequest(playerRequest)) {
            return ResponseEntity.badRequest().build();
        }
        if(player==null){
            return ResponseEntity.notFound().build();
        }
        Player updatePlayer = playerService.updatePlayer(id, playerRequest);
        return ResponseEntity.ok(updatePlayer);
    }
}
