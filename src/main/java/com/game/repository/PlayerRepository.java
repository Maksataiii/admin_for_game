package com.game.repository;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p FROM Player p WHERE " +
            "(:name IS NULL OR p.name LIKE CONCAT('%', :name, '%')) " +
            "AND (:title IS NULL OR p.title LIKE CONCAT('%', :title, '%')) " +
            "AND (:race IS NULL OR p.race = :race) " +
            "AND (:profession IS NULL OR p.profession = :profession) " +
            "AND (:after IS NULL OR p.birthday >= :after) " +
            "AND (:before IS NULL OR p.birthday <= :before) " +
            "AND (:banned IS NULL OR p.banned = :banned) " +
            "AND (:minExperience IS NULL OR p.experience >= :minExperience) " +
            "AND (:maxExperience IS NULL OR p.experience <= :maxExperience) " +
            "AND (:minLevel IS NULL OR p.level >= :minLevel) " +
            "AND (:maxLevel IS NULL OR p.level <= :maxLevel) " +
            "ORDER BY " +
            "CASE WHEN :order = null THEN p.id END ASC, " +
            "CASE WHEN :order = 'name' THEN p.name END ASC, " +
            "CASE WHEN :order = 'experience' THEN p.experience END ASC, " +
            "CASE WHEN :order = 'birthday' THEN p.birthday END ASC, " +
            "CASE WHEN :order = 'level' THEN p.level END ASC")

    List<Player> findFilteredPlayers(
            @Param("name") String name, @Param("title") String title, @Param("race") Race race, @Param("profession") Profession profession,
            @Param("after") Date after, @Param("before") Date before, @Param("banned") Boolean banned,
            @Param("minExperience") Integer minExperience, @Param("maxExperience") Integer maxExperience,
            @Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel, @Param("order") PlayerOrder order,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Player p WHERE " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:title IS NULL OR p.title LIKE %:title%) AND " +
            "(:race IS NULL OR p.race = :race) AND " +
            "(:profession IS NULL OR p.profession = :profession) AND " +
            "(:after IS NULL OR p.birthday >= :after) AND " +
            "(:before IS NULL OR p.birthday <= :before) AND " +
            "(:banned IS NULL OR p.banned = :banned) AND " +
            "(:minExperience IS NULL OR p.experience >= :minExperience) AND " +
            "(:maxExperience IS NULL OR p.experience <= :maxExperience) AND " +
            "(:minLevel IS NULL OR p.level >= :minLevel) AND " +
            "(:maxLevel IS NULL OR p.level <= :maxLevel)")
    Integer countByFilters(
            @Param("name") String name,
            @Param("title") String title,
            @Param("race") Race race,
            @Param("profession") Profession profession,
            @Param("after") Date after,
            @Param("before") Date before,
            @Param("banned") Boolean banned,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience,
            @Param("minLevel") Integer minLevel,
            @Param("maxLevel") Integer maxLevel
    );

}
