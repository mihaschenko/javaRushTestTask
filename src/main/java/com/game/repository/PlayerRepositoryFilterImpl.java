package com.game.repository;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.*;

public class PlayerRepositoryFilterImpl implements PlayerRepositoryFilter {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Player> findPlayerByFilters(Map<String, String> filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> query = cb.createQuery(Player.class);
        Root<Player> root = query.from(Player.class);

        List<Predicate> predicates = getPredicates(cb, root, filter);

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        if(filter.containsKey("order"))
            query.orderBy(cb.asc(root.get(PlayerOrder.valueOf(filter.get("order")).getFieldName())));
        else
            query.orderBy(cb.asc(root.get(PlayerOrder.ID.getFieldName())));

        int pageSize = Integer.parseInt(filter.getOrDefault("pageSize", "3"));
        int pageNumber = Integer.parseInt(filter.getOrDefault("pageNumber", "0"));

        return entityManager.createQuery(query.select(root))
                .setFirstResult(pageSize * pageNumber)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public int countPlayerByFilters(Map<String, String> filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> query = cb.createQuery(Player.class);
        Root<Player> root = query.from(Player.class);

        List<Predicate> predicates = getPredicates(cb, root, filter);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query.select(root))
                .getResultList().size();
    }

    private List<Predicate> getPredicates(CriteriaBuilder cb, Root<Player> root, Map<String, String> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if(filter.containsKey("name"))
            predicates.add(cb.like(root.get("name"), "%" + filter.get("name") + "%"));
        if(filter.containsKey("title"))
            predicates.add(cb.like(root.get("title"), "%" + filter.get("title") + "%"));
        if(filter.containsKey("race"))
            predicates.add(cb.equal(root.get("race"), filter.get("race")));
        if(filter.containsKey("profession"))
            predicates.add(cb.equal(root.get("profession"), filter.get("profession")));
        if(filter.containsKey("after"))
            predicates.add(cb.greaterThan(root.get("birthday"), new Date(Long.parseLong(filter.get("after")))));
        if(filter.containsKey("before"))
            predicates.add(cb.lessThan(root.get("birthday"), new Date(Long.parseLong(filter.get("before")))));
        if(filter.containsKey("banned")) {
            boolean banned = Boolean.parseBoolean(filter.get("banned"));
            predicates.add(banned ? cb.isTrue(root.get("banned")) : cb.isFalse(root.get("banned")));
        }
        if(filter.containsKey("minExperience"))
            predicates.add(cb.greaterThanOrEqualTo(root.get("experience"), filter.get("minExperience")));
        if(filter.containsKey("maxExperience"))
            predicates.add(cb.lessThanOrEqualTo(root.get("experience"), filter.get("maxExperience")));
        if(filter.containsKey("minLevel"))
            predicates.add(cb.greaterThanOrEqualTo(root.get("level"), filter.get("minLevel")));
        if(filter.containsKey("maxLevel"))
            predicates.add(cb.lessThanOrEqualTo(root.get("level"), filter.get("maxLevel")));
        return predicates;
    }
}
