package com.game.controller;


import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rest/players")
public class MyController {

    private final PlayerService playerService;

    public MyController(PlayerService playerService) {
        this.playerService = playerService;
    }

//    @GetMapping("/players")
//    public List<Player> getAllPlayers(@RequestParam int id) {
//        List<Player> allPlayers = (List<Player>) playerService.findAllPlayers();
//        return allPlayers;
//    }

//    @GetMapping("/players/count")
//    public long count(){
//        long count = playerService.count();
//        return count();
//    }

    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);
        return player;
    }

    @PostMapping("/")
    public Player createPlayer(@RequestBody Player player) {
        playerService.createPlayer(player);
        return player;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        return ResponseEntity.ok(playerService.updatePlayer(id, player));
    }


    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playerService.delete(id);
    }
}
