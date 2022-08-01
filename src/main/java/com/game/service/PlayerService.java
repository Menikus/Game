package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.sqrt;


@Service
public class PlayerService {

    private static final int MAX_LENGTH_NAME = 12;
    private static final int MAX_LENGTH_TITLE = 30;
    private static final int MAX_SIZE_EXPERIENCE = 10_000_000;
    private static final long MIN_BIRTHDAY = 2000L;
    private static final long MAX_BIRTHDAY = 3000L;

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Integer calculateCurrentLevel(Integer experience) {
        return ((int) ((sqrt(2500 + 200 * experience) - 50) / 100));
    }

    private Integer calculateExperienceUntilNextLevel(Integer experience, Integer level) {
        return 50 * (level + 1) * (level + 2) - experience;
    }

//    public Page<Player> findAllPlayers() {
//        return playerRepository.findAll(specification, pageable);
//    }


    //создание спецификации


    public Player createPlayer(Player player) {
        checkName(player.getName());
        checkTitle(player.getTitle());
        checkRace(player.getRace());
        checkProfession(player.getProfession());
        checkBirthday(player.getBirthday());
        if (player.getBanned() == null) {
            player.setBanned(false);
        }
        checkExperience(player.getExperience());
        player.setLevel(calculateCurrentLevel(player.getExperience()));
        player.setUntilNextLevel(calculateExperienceUntilNextLevel(player.getExperience(), player.getLevel()));
        return playerRepository.saveAndFlush(player);
    } //createPlayer

    public Player getPlayerById(Long id) {
        checkId(id);
        return playerRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Player not found"));
    } //getPlayer

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    } //savePlayer

    public Player updatePlayer(Long id, Player player) {
        Player newPlayer = getPlayerById(id);

        if (player.getName() != null) {
            checkName(player.getName());
            newPlayer.setName(player.getName());
        }

        if (player.getTitle() != null) {
            checkTitle(player.getTitle());
            newPlayer.setTitle(player.getTitle());
        }

        if (player.getRace() != null) {
            checkRace(player.getRace());
            newPlayer.setRace(player.getRace());
        }

        if (player.getProfession() != null) {
            checkProfession(player.getProfession());
            newPlayer.setProfession(player.getProfession());
        }

        if (player.getBirthday() != null) {
            checkBirthday(player.getBirthday());
            newPlayer.setBirthday(player.getBirthday());
        }

        if (player.getBanned() != null) {
            newPlayer.setBanned(player.getBanned());
        }

        if (player.getExperience() != null) {
            checkExperience(player.getExperience());
            newPlayer.setExperience(player.getExperience());
        }

        newPlayer.setLevel(calculateCurrentLevel(newPlayer.getExperience()));
        newPlayer.setUntilNextLevel(calculateExperienceUntilNextLevel(newPlayer.getExperience(), newPlayer.getLevel()));

        return playerRepository.save(newPlayer);
    }

    public void delete(long id) {
        Player player = getPlayerById(id);
//        if (player == null && id != 0) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        } else if (id == 0) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
        playerRepository.deleteById(id);
    } //deletePlayer

    public void checkId(Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid Id");
        }
    }

    public void checkName(String name) {
        if (name == null || name.isEmpty() || name.length() > MAX_LENGTH_NAME) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid name");
        }
    }

    public void checkTitle(String title) {
        if (title == null || title.isEmpty() || title.length() > MAX_LENGTH_TITLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid title");
        }
    }

    public void checkRace(Race race) {
        if (race == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid race");
        }
    }

    public void checkProfession(Profession profession) {
        if (profession == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid profession");
        }
    }

    public void checkBirthday(Date birthday) {
        if (birthday == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid birthday");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday.getTime());
        if (calendar.get(Calendar.YEAR) < MIN_BIRTHDAY
                || calendar.get(Calendar.YEAR) > MAX_BIRTHDAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "wrong birthday");
        }
    }

    public void checkExperience(Integer experience) {
        if (experience == null || experience < 0 || experience > MAX_SIZE_EXPERIENCE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid experience");
        }
    } //checkValidInf


//    public Specification<Player> filterByRace(Race race) {
//        return (root, query, cb) -> race == null ? null : cb.equal(root.get("race"), race);
//    }


    public Specification<Player> filterByName(String name) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return name == null ? null : cb.equal(root.get("name"), name);
            }
        };
    }

    public Specification<Player> filterByTitle(String title) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return title == null ? null : cb.equal(root.get("title"), title);
            }
        };
    }

    public Specification<Player> filterByRace(Race race) {
        return (root, query, cb) -> race == null ? null : cb.equal(root.get("race"), race);
    }

    public Specification<Player> filterByProfession(Profession profession) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return profession == null ? null : cb.equal(root.get("profession"), profession);
            }
        };
    }

}


