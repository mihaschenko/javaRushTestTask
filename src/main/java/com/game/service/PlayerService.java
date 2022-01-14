package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> findPlayerByFilters(Map<String, String> filter) {
        return playerRepository.findPlayerByFilters(filter);
    }

    public int countPlayerByFilters(Map<String, String> filter) {
        return playerRepository.countPlayerByFilters(filter);
    }

    public void delete(int id) {
        playerRepository.deleteById(id);
    }

    public boolean exists(int id) {
        return playerRepository.existsById(id);
    }

    public Player get(int id) {
        return playerRepository.findById(id).orElse(null);
    }

    public Player save(Player player) { return playerRepository.save(player); }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }
}
