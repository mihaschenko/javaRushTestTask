package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import java.util.*;

@Controller
@RestController
public class PlayerController extends HttpServlet {
    @Autowired
    private PlayerService playerService;

    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> getAll(@RequestParam Map<String, String> requestBody) {
        return ResponseEntity.ok(playerService.findPlayerByFilters(requestBody));
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> getAllCount(@RequestParam Map<String, String> requestBody) {
        return ResponseEntity.ok(playerService.countPlayerByFilters(requestBody));
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> get(@PathVariable String id) {
        if(id.matches("[0-9]+") && !id.equals("0")) {
            int playerId = Integer.parseInt(id);
            Player player = playerService.get(playerId);
            if(player != null)
                return ResponseEntity.ok(player);
            else
                return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(400).build();
    }

    @PostMapping("/rest/players")
    public ResponseEntity<Player> create(@RequestBody Player player) {
        if(!checkForNull(player) || !checkForData(player))
            return ResponseEntity.status(400).build();
        setBannedStatus(player);
        setLevelAndExperienceUntilNextLevel(player);

        return ResponseEntity.ok(playerService.save(player));
    }

    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> update(@PathVariable String id, @RequestBody Player player) {
        if(id.matches("[0-9]+") && !id.equals("0")) {
            int idPlayer = Integer.parseInt(id);
            if(playerService.exists(idPlayer)) {
                Player dbPlayer = playerService.get(idPlayer);

                if(player.getName() != null)
                    dbPlayer.setName(player.getName());
                if(player.getTitle() != null)
                    dbPlayer.setTitle(player.getTitle());
                if(player.getRace() != null)
                    dbPlayer.setRace(player.getRace());
                if(player.getProfession() != null)
                    dbPlayer.setProfession(player.getProfession());
                if(player.getBirthday() != null)
                    dbPlayer.setBirthday(player.getBirthday());
                if(player.getBanned() != null)
                    dbPlayer.setBanned(player.getBanned());
                if(player.getExperience() != null) {
                    dbPlayer.setExperience(player.getExperience());
                    setLevelAndExperienceUntilNextLevel(dbPlayer);
                }

                if(!checkForData(dbPlayer))
                    return ResponseEntity.status(400).build();

                return ResponseEntity.ok(playerService.save(dbPlayer));
            }
            else
                return ResponseEntity.status(404).build();
        }
        else
            return ResponseEntity.status(400).build();
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        if(id.matches("[0-9]+") && !id.equals("0")) {
            int playerId = Integer.parseInt(id);
            if(playerService.exists(playerId)) {
                playerService.delete(playerId);
                return ResponseEntity.ok().build();
            }
            else
                return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(400).build();
    }

    private boolean checkForNull(Player player) {
        return player.getName() != null
                && player.getTitle() != null
                && player.getRace() != null
                && player.getProfession() != null
                && player.getBirthday() != null
                && player.getExperience() != null;
    }

    private boolean checkForData(Player player) {
        if(player.getName() != null && (player.getName().isEmpty() || player.getName().length() > 12))
            return false;
        if(player.getTitle() != null && player.getTitle().length() > 30)
            return false;
        if(player.getBirthday() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.set(2000, Calendar.JANUARY, 1, 0,0,0);
            Date start = calendar.getTime();
            calendar.set(3000, Calendar.DECEMBER, 31, 24, 59, 59);
            Date finish = calendar.getTime();
            if(start.after(player.getBirthday()) || finish.before(player.getBirthday()))
                return false;
        }
        if(player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10_000_000))
            return false;
        return true;
    }

    private void setBannedStatus(Player player) {
        if(player.getBanned() == null)
            player.setBanned(false);
    }

    private void setLevelAndExperienceUntilNextLevel(Player player) {
        if(player.getExperience() != null) {
            int level = countLevel(player.getExperience());
            int experienceUntilNextLevel = countExperienceUntilNextLevel(level, player.getExperience());
            player.setLevel(level);
            player.setUntilNextLevel(experienceUntilNextLevel);
        }
    }

    private int countLevel(int experience) {
        return (int) (Math.sqrt(2500 + (200 * experience)) - 50) / 100;
    }

    private int countExperienceUntilNextLevel(int level, int experience) {
        return (50 * ((level + 1) * (level + 2))) - experience;
    }
}
