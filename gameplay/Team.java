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

package gameplay;

import java.util.ArrayList;
import java.util.Random;

/**
 * The team's object in terms of the 'gameplay'
 * 
 * @author Andreas Tasoulas
 * @see gameplay.Player
 *
 */
public class Team extends utility.Team {
    
    private int reputation;
    private ArrayList<Player> squad;
    private static Random rnd = new Random(); 

    /**
     * The initialization of the team 
     * @param name The team name
     * @param reputation The team reputation; in other words the team's strength
     * @param squad The list of the team players in the squad
     */
    public Team(String name, int reputation, ArrayList<Player> squad) {
        super(name);
        this.reputation = reputation;
        this.squad = squad;
    }
    
    public int getReputation() {
        return this.reputation;
    }
    
    public void setReputation(int reputation) {
        this.reputation = reputation;
    }
    
    /**
     * Assign the player attributes rates. This function is used in the generation of an 'environment'.
     * An environment is a set of teams, their squads and the attributes of the players. It is based on an 'environment template'.
     * An environment template is a set of data which serves as a guideline for the generation of the environment. Each team in an 
     * environment template is assigned a reputation, indicating its strength, and based on this reputation a 'basic rate' for each player
     * attribute is produced. For the time being, each player's strength is based only on the strength of its team. We could provide separate
     * 'template strengths' for each player but this is not a priority issue if not useless, because had we used real players we could just stick
     * the 'real' values for their 'attributes' to them. Since we plan to use only 'real' teams, we let just the strength of the team decide 
     * the rest. So, what this method does is calculating a rate for each player's attribute based on the strength of its team and its 
     * defined position (in the environment template) 
     * @return
     */
    public ArrayList<Player> assignAttributes() {
        for (Player currentPlayer:squad) {
            for (PlayerAttribute attribute:PlayerAttributes.getAll()) {
                String name = attribute.getName();
                Double deviation = rnd.nextGaussian();
                // System.out.println("Deviation: " + deviation);
                double rate = (double) reputation + deviation;
                // System.out.println("Initial rate: " + rate);
                rate = GkFilter(PosFilter(rate, attribute, currentPlayer), attribute, currentPlayer);
                if (rate < 0) rate = 0;
                currentPlayer.addSkill(attribute.getName(), rate);
            }
        }
        return squad;
    }
    
    /**
     * Checks whether one of the attribute's optimal positions matches the player's position, for the attribute's rate to be enhanced
     * @return The player's position is among the attribute's optimal positions
     */
    private boolean matchOptimalPos(PlayerAttribute attribute, Player player) {        
        if (player.isAttrPrivileged(attribute)) 
            return true; 
        else
            return false;
    }
    
    /**
     * Promote a player's rate (Utility function)
     * @param rate The player's rate 
     * @return The 'promoted' rate
     */
    private double promoteRate(double rate) {
        
        double count = 0;
        double groundCovered = 0;
        double ratePromotion = rnd.nextDouble();
        
        // System.out.println("Rate promotion: " + ratePromotion);
        
        for (double currentDivisor = 2; ratePromotion > groundCovered + 1d/currentDivisor; count++) {
            groundCovered += 1d/currentDivisor;
            currentDivisor *= 2;
        }
        
        return rate + (double) count;
    }
    
    /**
     * Utility function: apply a 'filter' to a player's attribute rate according to the player's position
     * @param rate The initial ('basic') player's rate as output from the randomizer
     * @param attribute The object of the attribute to apply the filter to
     * @param player The player object
     * @return The rate of the player after applying the position filter
     */
    private double PosFilter(double rate, PlayerAttribute attribute, Player player) {
        if (matchOptimalPos(attribute, player)) {
            return promoteRate(rate);
        } else {
            return rate;
        }
    }
    
    /**
     * Utility function: apply a 'filter' to a player's attribute rate according to whether the player is a goalie or an outfield player.
     * Essentially, this filter reduces the goalie attributes for the outfield players and the non-goalie attributes for the goalkeepers
     * @param rate The initial ('basic') player's rate as output from the randomizer
     * @param attribute The object of the attribute to apply the filter to
     * @param player The player object
     * @return The rate of the player after applying the goalkeeper filter
     */
    private double GkFilter(double rate, PlayerAttribute attribute, Player player) {
        
        // two cases of applying this filter:
        // 1. The player is not a goalkeeper while it is a goalkeeper attribute
        // 2. The player is a goalkeeper while it is not a goalkeeper attribute
        
        if (player.getPosition() == Constants.GK) {
            if (attribute.isOutfield()) 
                return rate / 2d;
        } else if (!attribute.isOutfield())
            return rate / 2d;
        
        return rate;
    }
}
