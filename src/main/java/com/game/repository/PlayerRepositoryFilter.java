package com.game.repository;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayerRepositoryFilter {
    List<Player> findPlayerByFilters(Map<String, String> filter);
    int countPlayerByFilters(Map<String, String> filter);
}
