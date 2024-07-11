package com.nsteuerberg.Bot.de.NSteuerberg.repository;

import com.nsteuerberg.Bot.de.NSteuerberg.model.UserScore;
import com.nsteuerberg.Bot.de.NSteuerberg.model.UserScoreKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface UserScoreRepository extends CrudRepository<UserScore, UserScoreKey> {
    /*@Query("SELECT u FROM UserScore u WHERE u.userScoreKey.serverId = :serverId ORDER BY u.score DESC")
    List<UserScore> findTopByServerId(@Param("serverId") String serverId);
     */
}
