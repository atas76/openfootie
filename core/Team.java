/*
 * Copyright 2010 Andreas Tasoulas
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *  
 */

package core;

import gameplay.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import utility.RealWorldMapping;
import utility.Tactics;
import utility.exceptions.TacticsException;


/**
 * The team object bearing 'core' data and functions 
 * 
 * @author Andreas Tasoulas
 *
 */
public class Team extends utility.Team {
    
    private HashMap<Player, Tactics.TacticLine> skilledLineup = new HashMap<Player, Tactics.TacticLine>();
    private HashMap<Player, Tactics.TacticPosition> playerLayout = new HashMap<Player, Tactics.TacticPosition>();
    
    private ArrayList<Player> flatLineup = new ArrayList<Player>();
    private ArrayList<Player> orderedLineup = new ArrayList<Player>();
    
    private HashMap<Byte, String> actionAttributes = new HashMap<Byte, String>();
    
    public Tactics.TacticPosition getPosXByPlayer(Player player) {
        return playerLayout.get(player);
    }
    
    public Tactics.TacticLine getPosYByPlayer(Player player) {
        return skilledLineup.get(player);
    }
    
    /**
     * Adds a player to the match lineup
     * 
     * @param player The player object
     * @param position The position of the player in the lineup
     */
    public void addPlayer(Player player, Tactics.TacticLine position) {
        orderedLineup.add(player);
        skilledLineup.put(player, position);
    }
    
    /**
     * Adds a player to the 'flat' lineup structure from which it will be assigned to the match lineup after further processing
     * 
     * @param player The player object
     */
    public void addPlayer(Player player) {
        flatLineup.add(player);
    }
    
    /**
     * Set the team tactics
     * @param tactics A String representation of the team tactics
     */
    public void setTactics(String tactics) {
        
        String [] tacticsFormation = tactics.split("-");
        
        try {
        
            this.tactics = new Tactics(new Integer(tacticsFormation[0]), new Integer(tacticsFormation[1]), new Integer(tacticsFormation[2]));
        
        } catch (TacticsException te) {
            System.out.println("Invalid tactics");
        }  
    }
    
    public Tactics getTactics() {
        return this.tactics;
    }
    
    /**
     * Define tactics based on the selected lineup. This is used for when the lineup is hardcoded.
     *
     */
    public void defineTactics() {
        try {
            this.tactics = new Tactics(getNumberByTL(Constants.DEFENDER), getNumberByTL(Constants.MIDFIELDER), getNumberByTL(Constants.FORWARD));
        } catch (TacticsException te) {
            System.out.println("Invalid tactics");
        }
    }
    
    /**
     * Align players in the x axis for the 'desktop' version. The difference between the 'desktop' and 'web' versions is
     * that the web version supports lineup changes throughout the match
     *
     */
    public void alignPlayersDesktop() {
        alignPlayersDesktop(Tactics.TacticLine.DEFENDER);
        alignPlayersDesktop(Tactics.TacticLine.MIDFIELDER);
        alignPlayersDesktop(Tactics.TacticLine.FORWARD);
    }
    
    /**
     * Align players in the team's formation ('web' or 'default' version)
     *
     */
    public void alignPlayers() {
        alignPlayers(Tactics.TacticLine.DEFENDER);
        alignPlayers(Tactics.TacticLine.MIDFIELDER);
        alignPlayers(Tactics.TacticLine.FORWARD);
    }
    
    /**
     * Align players in the x axis per tactics line for the desktop version
     * @param tLine The tactics line, i.e. defence, midfield, attack
     * @see #alignPlayersDesktop()
     * 
     */
    private void alignPlayersDesktop(Tactics.TacticLine tLine) {
        
        ArrayList<Player> currentLinePlayers = new ArrayList<Player>();
        
        // Set<Player> players = skilledLineup.keySet();
        
        for (Player player:orderedLineup) {
            if (skilledLineup.get(player).equals(tLine)) currentLinePlayers.add(player);
        }
        
        for (Player player:flatLineup) { // Take them from flat line up so that order is preserved
            if (skilledLineup.get(player).equals(tLine)) currentLinePlayers.add(player);
        }
        
        switch (currentLinePlayers.size()) {
        case 5:
            playerLayout.put(currentLinePlayers.get(0), Tactics.TacticPosition.LEFT);
            playerLayout.put(currentLinePlayers.get(1), Tactics.TacticPosition.LEFT_AXIS);
            playerLayout.put(currentLinePlayers.get(2), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(3), Tactics.TacticPosition.RIGHT_AXIS);
            playerLayout.put(currentLinePlayers.get(4), Tactics.TacticPosition.RIGHT);
            break;
        case 4:
            playerLayout.put(currentLinePlayers.get(0), Tactics.TacticPosition.LEFT);
            playerLayout.put(currentLinePlayers.get(1), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(2), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(3), Tactics.TacticPosition.RIGHT);
            break;
        case 3:
            playerLayout.put(currentLinePlayers.get(0), Tactics.TacticPosition.LEFT_AXIS);
            playerLayout.put(currentLinePlayers.get(1), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(2), Tactics.TacticPosition.RIGHT_AXIS);
            break;
        case 2:
        case 1:
            for (Player currentPlayer:currentLinePlayers) {
                playerLayout.put(currentPlayer, Tactics.TacticPosition.AXIS);
            }
        }
    }
    
    /**
     * Align players in the x axis for the default/web version per tactics line
     * @param tLine The tactics line, i.e. defence, midfield, attack
     */
    private void alignPlayers(Tactics.TacticLine tLine) {
        
        ArrayList<Player> currentLinePlayers = new ArrayList<Player>();
        
        for (Player player:flatLineup) { // Take them from flat line up so that order is preserved (take care because now all squad players are included in 'flat' lineup
            if (skilledLineup.get(player) != null && skilledLineup.get(player).equals(tLine)) currentLinePlayers.add(player);
        }
        
        switch (currentLinePlayers.size()) {
        case 5:
            playerLayout.put(currentLinePlayers.get(4), Tactics.TacticPosition.LEFT);
            playerLayout.put(currentLinePlayers.get(3), Tactics.TacticPosition.LEFT_AXIS);
            playerLayout.put(currentLinePlayers.get(2), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(1), Tactics.TacticPosition.RIGHT_AXIS);
            playerLayout.put(currentLinePlayers.get(0), Tactics.TacticPosition.RIGHT);
            break;
        case 4:
            playerLayout.put(currentLinePlayers.get(3), Tactics.TacticPosition.LEFT);
            playerLayout.put(currentLinePlayers.get(2), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(1), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(0), Tactics.TacticPosition.RIGHT);
            break;
        case 3:
            playerLayout.put(currentLinePlayers.get(2), Tactics.TacticPosition.LEFT_AXIS);
            playerLayout.put(currentLinePlayers.get(1), Tactics.TacticPosition.AXIS);
            playerLayout.put(currentLinePlayers.get(0), Tactics.TacticPosition.RIGHT_AXIS);
            break;
        case 2:
        case 1:
            for (Player currentPlayer:currentLinePlayers) {
                playerLayout.put(currentPlayer, Tactics.TacticPosition.AXIS);
            }
        } 
    }
    
    /**
     * Change the team lineup. Used for lineup changes in the duration of a match
     * @param lineupShirts A list of the player shirt numbers of the new lineup. This is a valid and real-world identification as shirt numbers
     * cannot be duplicated between two players of the same team. A database id might be more 'technically appropriate', but it may not always
     * be available to the calling application, and it certainly is not in the reference calling application
     */
    public void changeLineup(ArrayList<Integer> lineupShirts) {
        
        ArrayList<Player> squad = new ArrayList<Player>();
        ArrayList<Player> lineup = new ArrayList<Player>();
        
        /*
        for (Integer shirtNo:lineupShirts) {
            System.out.println("Checking shirt number: " + shirtNo);
        }
        */
        
        for (Integer shirtNo:lineupShirts) {
        
            boolean playerFound = false;
            
            for (Player player:this.flatLineup) {       
                if (shirtNo == player.getShirtNo()) {
                    lineup.add(player);
                }
            }
            
        }
        
        for (Player player:this.flatLineup) {
            if (!lineup.contains(player)) { // Make the rest follow
                squad.add(player);
            }
        }
        
        this.flatLineup = new ArrayList<Player>();
        
        for (Player player:lineup) {
            this.flatLineup.add(player);
        }
        
        for (Player player:squad) {
            this.flatLineup.add(player);
        }
        
        /*
        for (Player player:this.flatLineup) {
            System.out.println("Player: " + player.getFamilyName());
        }
        */
    }
    
    /**
     * Align the players in the team's formation
     *
     */
    public void alignFormationPlayers() {
        
        // First align players in skilled lineup (y axis)
        int defenders = tactics.getDefenders();
        int midfielders = tactics.getMidfielders();
        int forwards = tactics.getForwards();
        
        // reset skilled line up
        skilledLineup = new HashMap<Player, Tactics.TacticLine>();
        
        skilledLineup.put(flatLineup.get(0), Tactics.TacticLine.GK);
        
        // System.out.println("Defenders to align: " + defenders);
        // System.out.println("Midfielders to align: " + midfielders);
        // System.out.println("Forwards to align: " + forwards);
        
        for (int i = 1; i <= tactics.getDefenders(); i++) {
            skilledLineup.put(flatLineup.get(i), Tactics.TacticLine.DEFENDER);
        }
        
        for (int i = tactics.getDefenders() + 1; i <= tactics.getMidfielders() + tactics.getDefenders(); i++) {
            skilledLineup.put(flatLineup.get(i), Tactics.TacticLine.MIDFIELDER);
        }
        
        for (int i = tactics.getMidfielders() + tactics.getDefenders() + 1; i < 11; i++) {
            skilledLineup.put(flatLineup.get(i), Tactics.TacticLine.FORWARD);
        }
        
        // Now align them in the x axis ('player layout')
        alignPlayers();
    }
    
    /**
     * Just display the lineup (for debugging purposes and also very crude)
     *
     */
    public void displayLineup() {
        
        Set<Player> players = skilledLineup.keySet();
        
        for (Player currentPlayer:players) {
            System.out.println(currentPlayer.getShirtNo() + " " + currentPlayer + " " + skilledLineup.get(currentPlayer) + " " + 
                    playerLayout.get(currentPlayer));
        }
    }
    
    /**
     * Getter
     * @return The family names of the players
     */
    public ArrayList<String> getPlayerNames() {
        
        Set<Player> players = skilledLineup.keySet();
        ArrayList<String> playerNames = new ArrayList<String>();
        
        for (Player currentPlayer:players) {
            playerNames.add(currentPlayer.getFamilyName());
        }
        
        return playerNames; 
    }
    
    public Set<Player> getPlayers() {
        return skilledLineup.keySet();
    }
    
    public ArrayList<Player> getSquadPlayers() {
        return flatLineup;
    }
    
    /**
     * For every player potentially entering the lineup throughout a match, adjust its stats up to a certain time
     * @param time The virtual time
     */
    public void adjustPlayerStatsToTime(int time) {
        for (Player player:flatLineup) {
            player.getStats().adjustToTime(time);
        }
    }
    
    /**
     * The 'core' team object is based on a 'utility' team object from the previous incarnation of this project. The parent contructor is called
     * as well. For this object, we map the available to a player actions to the relevant skill description
     * @param teamName The name of the team
     */
    public Team(String teamName) {
        super(teamName);
        actionAttributes.put(Constants.GkLongPass, "Passing");
        actionAttributes.put(Constants.Pass, "Passing");
        actionAttributes.put(Constants.HighPass, "Passing");
        actionAttributes.put(Constants.ForwardPass, "Passing");
        actionAttributes.put(Constants.Combination, "Teamwork");
        actionAttributes.put(Constants.FlankPass, "Passing");
        actionAttributes.put(Constants.BallControl, "BallControl");
        actionAttributes.put(Constants.LongThrowIn, "ThrowIn");
        actionAttributes.put(Constants.Dribbling, "Dribbling");
        actionAttributes.put(Constants.LongFlankPass, "Passing");
        actionAttributes.put(Constants.Cross, "Crossing");
        actionAttributes.put(Constants.LowCross, "Crossing");
        actionAttributes.put(Constants.LongPass, "Passing");
        actionAttributes.put(Constants.AreaPass, "Passing");
        actionAttributes.put(Constants.RunBall, "BallControl");
    }
    
    public HashMap<Player, Tactics.TacticLine> getSkilledLineup() {
        return skilledLineup;
    }
    
    /**
     * Get number of players per tactics line
     * @param tacticsLine The tactics line
     * @return The number of players per tactics line
     */
    public int getNumberByTL(int tacticsLine) {
        
        Tactics.TacticLine posLine = null;
        
        switch (tacticsLine) {
        case Constants.GK:
            posLine = Tactics.TacticLine.GK;
            break;
        case Constants.DEFENDER:
            posLine = Tactics.TacticLine.DEFENDER;
            break;
        case Constants.MIDFIELDER:
            posLine = Tactics.TacticLine.MIDFIELDER;
            break;
        case Constants.FORWARD:
            posLine = Tactics.TacticLine.FORWARD;
            break;
        }
        
        if (posLine.equals(Tactics.TacticLine.GK)) {
            return 1;
        }
        
        Set<Player> players = skilledLineup.keySet();
        
        int counter = 0;
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(posLine)) ++counter;
        }
        
        return counter;
    }
    
    /**
     * Calculate the 'collective' strength of a tactics line based on its number of players 
     * @param tacticsLine The tactics line
     * @return The 'strength' of the tactics line as function of its number of players
     */
    public double getTargetStrength(int tacticsLine) {
        
        Tactics.TacticLine posLine = null;
        
        switch (tacticsLine) {
        case Constants.GK:
            posLine = Tactics.TacticLine.GK;
            break;
        case Constants.DEFENDER:
            posLine = Tactics.TacticLine.DEFENDER;
            break;
        case Constants.MIDFIELDER:
            posLine = Tactics.TacticLine.MIDFIELDER;
            break;
        case Constants.FORWARD:
            posLine = Tactics.TacticLine.FORWARD;
            break;
        }
        
        if (posLine.equals(Tactics.TacticLine.GK)) {
            return 1;
        }
        
        Set<Player> players = skilledLineup.keySet();
        
        double counter = 0;
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(posLine)) ++counter;
        }
        
        return counter / RealWorldMapping.defaultTLCardinality;        
    }
    
    /**
     * Get the goalkeeper's player object
     */
    public Player getGK() {
        
        Set<Player> players = skilledLineup.keySet();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(Tactics.TacticLine.GK)) return player;
        }
        
        return null;
    }
    
    /**
     * Get a lineup player by index (used for debugging purposes)
     * @param index The index of the player in the lineup structure
     * @return The player object
     */
    public Player getAnyPlayer(int index) {
        
        ArrayList<Player> allPlayers = new ArrayList<Player>();
        
        for (Player player: skilledLineup.keySet()) {
            allPlayers.add(player);
        }
        
        return allPlayers.get(index);
    }
    
    /**
     * Gets a player who has a specific position
     * @param position The player's position in the field (in the Y axis)
     * @return A random player who has a specific position
     */
    public Player getPlayerByPosition(int position) {
        
        Tactics.TacticLine posLine = null;
        
        switch (position) {
        case Constants.GK:
            posLine = Tactics.TacticLine.GK;
            break;
        case Constants.DEFENDER:
            posLine = Tactics.TacticLine.DEFENDER;
            break;
        case Constants.MIDFIELDER:
            posLine = Tactics.TacticLine.MIDFIELDER;
            break;
        case Constants.FORWARD:
            posLine = Tactics.TacticLine.FORWARD;
            break;
        }
        
        Set<Player> players = skilledLineup.keySet();
        
        double counter = 0;
        
        ArrayList<Player> qualifiedPlayers = new ArrayList<Player>();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(posLine)) qualifiedPlayers.add(player);
        }
        
        return qualifiedPlayers.get(rnd.nextInt(qualifiedPlayers.size()));
    }
    
    /**
     * Gets a player by a specific position, excluding a specific player
     * @param position The Y axis position
     * @param xPos The X axis position
     * @param excludedPlayer The player to be excluded from the candidates for the result. This is because, for example, we don't want a player to 
     * pass the ball to itself 
     * @return A random player who has a specific position and is not specified as excluded
     */
    public Player getPlayerByPosition(int position, Tactics.TacticPosition xPos, Player excludedPlayer) {
        
        // System.out.println("Position: " + position);
        // System.out.println("Getting player by position: " + xPos);
        
        Tactics.TacticLine posLine = null;
        
        switch (position) {
        case Constants.GK:
            posLine = Tactics.TacticLine.GK;
            break;
        case Constants.DEFENDER:
            posLine = Tactics.TacticLine.DEFENDER;
            break;
        case Constants.MIDFIELDER:
            posLine = Tactics.TacticLine.MIDFIELDER;
            break;
        case Constants.FORWARD:
            posLine = Tactics.TacticLine.FORWARD;
            break;
        }
        
        Set<Player> players = skilledLineup.keySet();
        
        ArrayList<Player> qualifiedPlayers = new ArrayList<Player>();
        ArrayList<Player> matchingPlayers = new ArrayList<Player>();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(posLine)) qualifiedPlayers.add(player);
        }
        
        // System.out.println("Qualified players: " + qualifiedPlayers.size());
        
        for (Player currentPlayer:qualifiedPlayers) {
            
            if (currentPlayer == excludedPlayer) continue;
            
            if (xPos.equals(Tactics.TacticPosition.LEFT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT) ||
                   (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            } else if (xPos.equals(Tactics.TacticPosition.RIGHT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            
            } else if (xPos.equals(Tactics.TacticPosition.AXIS)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS))  ||
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.AXIS))) {
                    matchingPlayers.add(currentPlayer);
                }
            } 
        }
        
        // System.out.println("Matching players: " + matchingPlayers.size());
        
        // Accommodate for when a qualified player is not found: use the more defensive tactic line as a new pool of players
        if (matchingPlayers.size() == 0) {
            if (position == Constants.FORWARD) {
                // System.out.println("Forward not found");
                return getPlayerByPosition(Constants.MIDFIELDER, xPos, excludedPlayer);
            } else if (position == Constants.MIDFIELDER) {
                // System.out.println("Midfielder not found");
                // System.out.println("Excluded player: " + excludedPlayer.getFamilyName());
                return getPlayerByPosition(Constants.DEFENDER, xPos, excludedPlayer);
            } else {
                // System.out.println("Defender not found");
                return getPlayerByPosition(Constants.MIDFIELDER, Tactics.TacticPosition.AXIS);
            }
        } else if (matchingPlayers.size() == 1) {
            if (matchingPlayers.get(0) == excludedPlayer) {
                if (position == Constants.FORWARD) {
                    // System.out.println("Forward not found");
                    return getPlayerByPosition(Constants.MIDFIELDER, xPos);
                } else if (position == Constants.MIDFIELDER) {
                    // System.out.println("Midfielder not found (excluded)");
                    return getPlayerByPosition(Constants.DEFENDER, xPos);
                } else {
                    // System.out.println("Defender excluded");
                    return getPlayerByPosition(Constants.MIDFIELDER, Tactics.TacticPosition.AXIS); // Search for midfield
                }
            } else {
                // System.out.println("Returning only candidate");
                return matchingPlayers.get(0);
            }
        }
        
        // System.out.println("Position found");
        return matchingPlayers.get(rnd.nextInt(matchingPlayers.size()));
    }
    
    /**
     * Get a player by a specific position 
     * @param position The Y axis position
     * @param xPos The X axis position
     * @return A random player who has a specific position
     */
    public Player getPlayerByPosition(int position, Tactics.TacticPosition xPos) {
        
        // System.out.println("Position: " + position);
        // System.out.println("Getting player by position: " + xPos);
        
        Tactics.TacticLine posLine = null;
        
        switch (position) {
        case Constants.GK:
            posLine = Tactics.TacticLine.GK;
            break;
        case Constants.DEFENDER:
            posLine = Tactics.TacticLine.DEFENDER;
            break;
        case Constants.MIDFIELDER:
            posLine = Tactics.TacticLine.MIDFIELDER;
            break;
        case Constants.FORWARD:
            posLine = Tactics.TacticLine.FORWARD;
            break;
        }
        
        Set<Player> players = skilledLineup.keySet();
        
        ArrayList<Player> qualifiedPlayers = new ArrayList<Player>();
        ArrayList<Player> matchingPlayers = new ArrayList<Player>();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(posLine)) qualifiedPlayers.add(player);
        }
        
        for (Player currentPlayer:qualifiedPlayers) {
            if (xPos.equals(Tactics.TacticPosition.LEFT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT) ||
                   (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            } else if (xPos.equals(Tactics.TacticPosition.RIGHT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            
            } else if (xPos.equals(Tactics.TacticPosition.AXIS)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS))  ||
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.AXIS))) {
                    matchingPlayers.add(currentPlayer);
                }
            } 
        }
        
        // Accommodate for when a qualified player is not found: use the more defensive tactic line as a new pool of players
        if (matchingPlayers.size() == 0) {
            if (position == Constants.FORWARD) {
                // System.out.println("Forward not found");
                return getPlayerByPosition(Constants.MIDFIELDER, xPos);
            } else if (position == Constants.MIDFIELDER) {
                // System.out.println("Midfielder not found");
                return getPlayerByPosition(Constants.DEFENDER, xPos);
            } else {
                return null;
            }
        }
        
        // System.out.println("Position found");
        return matchingPlayers.get(rnd.nextInt(matchingPlayers.size()));
    }
    
    /**
     * Get a random player who can be characterized as 'defensive'
     * @return A 'defensive player' object
     */
    public Player getAnyDefensivePlayer() {
        
        Set<Player> players = skilledLineup.keySet();
        
        ArrayList<Player> qualifiedPlayers = new ArrayList<Player>();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(Tactics.TacticLine.DEFENDER) || 
                skilledLineup.get(player).equals(Tactics.TacticLine.MIDFIELDER)) 
                    qualifiedPlayers.add(player);
        }
        
        return qualifiedPlayers.get(rnd.nextInt(qualifiedPlayers.size()));
    }
    
    /**
     * Get a random player who can be characterized as 'defensive' having a specific position
     * @param xPos The X axis position
     * @return A 'defensive player' object
     */
    public Player getAnyDefensivePlayer(Tactics.TacticPosition xPos) {
        
        Set<Player> players = skilledLineup.keySet();
        
        ArrayList<Player> qualifiedPlayers = new ArrayList<Player>();
        ArrayList<Player> matchingPlayers = new ArrayList<Player>();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(Tactics.TacticLine.DEFENDER) || 
                skilledLineup.get(player).equals(Tactics.TacticLine.MIDFIELDER)) 
                    qualifiedPlayers.add(player);
        }
        
        for (Player currentPlayer:qualifiedPlayers) {
            if (xPos.equals(Tactics.TacticPosition.LEFT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT) ||
                   (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            } else if (xPos.equals(Tactics.TacticPosition.RIGHT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            
            } else if (xPos.equals(Tactics.TacticPosition.AXIS) || matchingPlayers.size() == 0) { // By default get central players if no flank players are found
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS))  ||
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.AXIS))) {
                    matchingPlayers.add(currentPlayer);
                }
            } 
        }
        
        // Accommodate for when a qualified player is not found: use the more defensive tactic line as a new pool of players
        if (matchingPlayers.size() == 0) {
            return null;
        }
        
        return matchingPlayers.get(rnd.nextInt(matchingPlayers.size()));
    }
    
    /**
     * Get a random player who can be characterized as 'defensive' having a specific position and it is not specified as 'excluded'
     * @param xPos The X axis position
     * @param excludedPlayer The player to be excluded from the candidates for the result
     * @return A 'defensive player' object
     * @see #getPlayerByPosition(int, Tactics.TacticPosition, Player)
     */
    public Player getAnyDefensivePlayer(Tactics.TacticPosition xPos, Player excludedPlayer) {
        
        Set<Player> players = skilledLineup.keySet();
        
        ArrayList<Player> qualifiedPlayers = new ArrayList<Player>();
        ArrayList<Player> matchingPlayers = new ArrayList<Player>();
        
        // System.out.println("Excluded player: " + excludedPlayer.getFamilyName());
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(Tactics.TacticLine.DEFENDER) || 
                skilledLineup.get(player).equals(Tactics.TacticLine.MIDFIELDER)) 
                    qualifiedPlayers.add(player);
        }
        
        for (Player currentPlayer:qualifiedPlayers) {
            
            if (xPos.equals(Tactics.TacticPosition.LEFT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT) ||
                   (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            } else if (xPos.equals(Tactics.TacticPosition.RIGHT)) {
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)))) {
                    matchingPlayers.add(currentPlayer);
                }
            
            } 
            
            if (xPos.equals(Tactics.TacticPosition.AXIS) || matchingPlayers.size() == 0 || 
                    (matchingPlayers.size() == 1 && matchingPlayers.get(0) == excludedPlayer)) { // By default get central players if no flank players are found
                if ((playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.RIGHT_AXIS)) || 
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.LEFT_AXIS))  ||
                    (playerLayout.get(currentPlayer).equals(Tactics.TacticPosition.AXIS))) {
                    matchingPlayers.add(currentPlayer);
                }
            } 
        }
        
        // Accommodate for when a qualified player is not found: use the more defensive tactic line as a new pool of players
        if (matchingPlayers.size() == 0) {
            // System.out.println("Can't find any defensive player");
            return null;
        }
        
        // System.out.println("Matching players size: " + matchingPlayers.size());
        
        return matchingPlayers.get(rnd.nextInt(matchingPlayers.size()));
    }
    
    /**
     * Utility function which calculates the average skill of players for a specific attribute grouped by their position
     * @param position The Y axis position
     * @param attribute The attribute name
     * @return The average of an attribute's skills for players of a specific position
     */
    public double getAverageFromAttribute(int position, String attribute) {
    
        // decode the mappings
        
        Tactics.TacticLine posLine = null;
        
        switch (position) {
        case Constants.GK:
            posLine = Tactics.TacticLine.GK;
            break;
        case Constants.DEFENDER:
            posLine = Tactics.TacticLine.DEFENDER;
            break;
        case Constants.MIDFIELDER:
            posLine = Tactics.TacticLine.MIDFIELDER;
            break;
        case Constants.FORWARD:
            posLine = Tactics.TacticLine.FORWARD;
            break;
        }
        
        if (posLine.equals(Tactics.TacticLine.GK)) {
            return getGK().getSkill(attribute);
        }
        
        Set<Player> players = skilledLineup.keySet();
        
        ArrayList<Player> posPlayers = new ArrayList<Player>();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(posLine)) posPlayers.add(player);
        }
        
        double totalSkill = 0;
        
        for (int i = 0; i < posPlayers.size(); i++) {
            totalSkill += posPlayers.get(i).getSkill(attribute);
        }
        
        return totalSkill / (double) posPlayers.size();
    }
    
    /**
     * Get the average skill of attributes relating to a specific action for players of a specific position
     * @param position The Y axis position
     * @param action The action constant
     * @return The average skill per player position per action
     */
    public double getAverageFromAction(int position, byte action) {
        return getAverageFromAttribute(position, actionAttributes.get(action));
    }
    
    /**
     * Utility function which calculates a defender who gets a rebound from an attacking effort of the opponent
     * @return A rebounder's player object
     */
    public Player getDefensiveRebounder() {
        
        Set<Player> players = skilledLineup.keySet();
        ArrayList<Player> candidateRebounders = new ArrayList<Player>();
        
        for (Player player:players) {
            if (skilledLineup.get(player).equals(Tactics.TacticLine.DEFENDER)) candidateRebounders.add(player);
        }
        
        int rebounderIndex = rnd.nextInt(candidateRebounders.size());
        
        return candidateRebounders.get(rebounderIndex);   
    }
    
    /**
     * Utility function: Get a random player from a group of players, with weights attributed to each player for their selection
     * @param total The sum of the players' weights
     * @param weightedPlayers A map of the players to their weights
     * @return A weighted random player
     */
    private Player getWeightedRandom(double total, HashMap<Player, Double> weightedPlayers) {
        double reboundIndex = rnd.nextDouble() * total;
        double checkedWeight = 0;
        for (Player player:weightedPlayers.keySet()) {
            checkedWeight += weightedPlayers.get(player);
            if (reboundIndex <= checkedWeight) {
                return player;
            }
        }
        return null;
    }
    
    /**
     * Calculate a rebounder of the attacking team from an attacking effort
     * @return A weighted random player object of the attacking rebounder 
     */
    public Player getAttackingRebounder() {
        
        Set<Player> players = skilledLineup.keySet();
        HashMap<Player, Double> candidateRebounders = new HashMap<Player, Double>();
        double totalReboundingWeight = 0;
        for (Player player:players) {
            double reboundingWeight = RealWorldMapping.getReboundingWeight(skilledLineup.get(player));
            totalReboundingWeight += reboundingWeight;
            candidateRebounders.put(player, reboundingWeight);
        }
        
        return getWeightedRandom(totalReboundingWeight, candidateRebounders);
    }
    
    /**
     * Finds the scorer of a goal "after the fact". Please note that the only reason to do this is when the goal scoring is 'decided' independently
     * of the current state; i.e. a cross or penalty kick. In these example cases we need to 'pick' a player to take the role of the goal scorer
     * (or the role of the player making an attempt on goal, for that matter)
     * @return The goal scorer according to some weighted ditribution among players
     */
    public Player getGoalScorer() {
        
        HashMap<Player, Tactics.TacticLine> lineup = this.skilledLineup;
        HashMap<Player, Double> scoringLineup = new HashMap<Player, Double>();
        
        Set<Player> players = lineup.keySet();
        
        double totalScoringWeight = 0;
        for (Player currentPlayer:players) {
            double currentScoringWeight = RealWorldMapping.getScoringWeight(lineup.get(currentPlayer));
            totalScoringWeight += currentScoringWeight;
            scoringLineup.put(currentPlayer, currentScoringWeight);
        }
        
        double scorerIndex = rnd.nextDouble() * totalScoringWeight;
        
        double checkedScoringWeight = 0;
        for (Player currentPlayer:players) {
            checkedScoringWeight += scoringLineup.get(currentPlayer);
            if (scorerIndex <= checkedScoringWeight) {
                return currentPlayer;
            }
        }
        return null; // default: no player is 'eligible'
    }
}
