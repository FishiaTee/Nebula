package emu.nebula.game.character;

import java.util.Collection;

import emu.nebula.Nebula;
import emu.nebula.data.GameData;
import emu.nebula.data.resources.CharacterDef;
import emu.nebula.data.resources.DiscDef;
import emu.nebula.game.player.PlayerManager;
import emu.nebula.game.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public class CharacterStorage extends PlayerManager {
    private final Int2ObjectMap<Character> characters;
    private final Int2ObjectMap<GameDisc> discs;
    
    public CharacterStorage(Player player) {
        super(player);
        
        this.characters = new Int2ObjectOpenHashMap<>();
        this.discs = new Int2ObjectOpenHashMap<>();
    }
    
    // Characters

    public Character getCharacterById(int id) {
        if (id <= 0) {
            return null;
        }
        
        return this.characters.get(id);
    }
    
    public boolean hasCharacter(int id) {
        return this.characters.containsKey(id);
    }
    
    public Character addCharacter(int charId) {
        // Sanity check to make sure we dont have this character already
        if (this.hasCharacter(charId)) {
            return null;
        }
        
        return this.addCharacter(GameData.getCharacterDataTable().get(charId));
    }

    private Character addCharacter(CharacterDef data) {
        // Sanity check to make sure we dont have this character already
        if (this.hasCharacter(data.getId())) {
            return null;
        }
        
        // Create character
        var character = new Character(this.getPlayer(), data);
        
        // Save to database
        character.save();
        
        // Add to characters
        this.characters.put(character.getCharId(), character);
        return character;
    }
    
    public Collection<Character> getCharacterCollection() {
        return this.getCharacters().values();
    }
    
    // Discs

    public GameDisc getDiscById(int id) {
        if (id <= 0) {
            return null;
        }
        
        return this.discs.get(id);
    }
    
    public boolean hasDisc(int id) {
        return this.discs.containsKey(id);
    }
    
    public GameDisc addDisc(int discId) {
        // Sanity check to make sure we dont have this character already
        if (this.hasDisc(discId)) {
            return null;
        }
        
        return this.addDisc(GameData.getDiscDataTable().get(discId));
    }

    private GameDisc addDisc(DiscDef data) {
        // Sanity check to make sure we dont have this character already
        if (this.hasDisc(data.getId())) {
            return null;
        }
        
        // Create disc
        var disc = new GameDisc(this.getPlayer(), data);
        
        // Save to database
        disc.save();
        
        // Add to discs
        this.discs.put(disc.getDiscId(), disc);
        return disc;
    }
    
    public Collection<GameDisc> getDiscCollection() {
        return this.getDiscs().values();
    }
    
    
    // Database
    
    public void loadFromDatabase() {
        var db = Nebula.getGameDatabase();
        
        db.getObjects(Character.class, "playerUid", getPlayerUid()).forEach(character -> {
            // Get data
            var data = GameData.getCharacterDataTable().get(character.getCharId());
            
            // Validate
            if (data == null) {
                return;
            }
            
            character.setPlayer(this.getPlayer());
            character.setData(data);
            
            // Add to characters
            this.characters.put(character.getCharId(), character);
        });
        
        db.getObjects(GameDisc.class, "playerUid", getPlayerUid()).forEach(disc -> {
            // Get data
            var data = GameData.getDiscDataTable().get(disc.getDiscId());
            if (data == null) return;
            
            disc.setPlayer(this.getPlayer());
            disc.setData(data);
            
            // Add
            this.discs.put(disc.getDiscId(), disc);
        });
    }
}
