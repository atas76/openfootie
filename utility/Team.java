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

package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * The basic team class
 * 
 * @author Andreas Tasoulas
 *
 */

public class Team {
    
    protected Tactics tactics;
    private HashMap<Player, Tactics.TacticLine> lineup = new HashMap<Player, Tactics.TacticLine>();
    protected String name;
    
    protected Stats stats = new Stats();
    protected MatchEvents events = new MatchEvents();
    
    protected double finishing;
    protected double shooting;
    
    protected static Random rnd = new Random();
    
    /**
     * The goal details in report format
     */
    
    public static class GoalDetails {
        
        private Player scorer;
        private double minute;
        private boolean penalty;
        private int timer;
        
        public GoalDetails(Player scorer, double minute, boolean isPenalty, int timer) {
            this.scorer = scorer;
            this.minute = minute;
            this.penalty = isPenalty;
            this.timer = timer;
        }
        
        public String toString() {
            String penalty = (this.penalty)?"(pen)":"";
            return new Double(this.minute).intValue() + "' " + this.scorer.getFamilyName() + penalty;
        }
        
        public Player getScorer() {
            return scorer;
        }
        
        public int getMinute() {
            return new Double(this.minute).intValue();
        }
        
        public int getTimer() {
            return this.timer;
        }
        
        public boolean isPenalty() {
            return this.penalty;
        }
    }
    
    /**
     * Details for missed penalties in report format 
     */
    
    public static class MissedPenaltyDetails {
        
        private Player taker;
        private double minute;
        private int timer;
        
        public MissedPenaltyDetails(Player taker, double minute, int timer) {
            this.taker = taker;
            this.minute = minute;
            this.timer = timer;
        }
        
        public String toString() {
            return new Double(this.minute).intValue() + "' " + this.taker.getFamilyName() + " missed penalty";
        }
        
        public Player getTaker() {
            return taker;
        }
        
        public int getMinute() {
            return new Double(this.minute).intValue();
        }
        
        public int getTimer() {
            return this.timer;
        }
    }
    
    /**
     * Aggregator class comprising goal and missed penalty details of the team
     */
    
    public class MatchEvents {
        
        private ArrayList<GoalDetails> goalDetails = new ArrayList<GoalDetails>();
        private ArrayList<MissedPenaltyDetails> penaltyDetails = new ArrayList<MissedPenaltyDetails>();
        
        public ArrayList<GoalDetails> getGoalDetails() {
            return goalDetails;
        }
        public void setGoalDetails(ArrayList<GoalDetails> goalDetails) {
            this.goalDetails = goalDetails;
        }
        
        public void resetGoalPenaltyDetails() {
            this.goalDetails = new ArrayList<GoalDetails>();
            this.penaltyDetails = new ArrayList<MissedPenaltyDetails>();
        }
        
        public ArrayList<MissedPenaltyDetails> getPenaltyDetails() {
            return penaltyDetails;
        }  
        public void setPenaltyDetails(ArrayList<MissedPenaltyDetails> penaltyDetails) {
            this.penaltyDetails = penaltyDetails;
        }
    }
    
    /**
     * The teams stats class 
     */
    
    public class Stats {
        
        private int possession = 0;
        private int goalsScored = 0;
        private int shotsOnTarget = 0;
        private int shotsOffTarget = 0;
        private int cornerKicks = 0;
        private int offsides = 0;
        private int freeKicks = 0;
        
        public void addPossession() {
            ++possession;
        }
        public int getPossessionCount() {
            return possession;
        }
        public void setPossessionCount(int possession) {
            this.possession = possession;
        }
        
        public void scoreGoal() {
            ++goalsScored;
        }
        public int getGoalsScored() {
            return goalsScored;
        }
        public void setGoalsScored(int goalsScored) {
            this.goalsScored = goalsScored;
        }
        
        public void addCornerKick() {
            ++cornerKicks;
        }
        public int getCornerKicks() {
            return cornerKicks;
        }
        public void setCornerKicks(int cornerKicks) {
            this.cornerKicks = cornerKicks;
        }
        
        public void addShotOnTarget() {
            ++shotsOnTarget;
        }
        public int getShotOnTarget() {
            return shotsOnTarget;
        }
        public void setShotOnTarget(int shotOnTarget) {
            this.shotsOnTarget = shotOnTarget;
        }
        
        public void addShotOffTarget() {
            ++shotsOffTarget;
        }
        public int getShotsOffTarget() {
            return shotsOffTarget;
        }
        public void setShotsOffTarget(int shotsOffTarget) {
            this.shotsOffTarget = shotsOffTarget;
        }
        
        public void addOffside() {
            ++offsides;
        }
        public int getOffsides() {
            return offsides;
        }
        public void setOffsides(int offsides) {
            this.offsides = offsides;
        }
        
        public void addFreeKick() {
            ++freeKicks;
        }
        public int getFreeKicks() {
            return freeKicks;
        }
        public void setFreeKicks(int freeKicks) {
            this.freeKicks = freeKicks;
        }
    }
    
    /**
     * Add a goal event in the list of team events
     * @param goal The goal representation
     */
    public void registerGoalEvent(GoalDetails goal) {
        events.goalDetails.add(goal);
    }
    
    /**
     * Add a missed penalty event in the list of team events
     * @param missedPenalty The missed penalty representation
     */
    public void registerMissedPenalty(MissedPenaltyDetails missedPenalty) {
        events.penaltyDetails.add(missedPenalty);
    }
    
    /**
     * Team initialization
     * @param name The team name
     */
    public Team(String name) {
        
        this.name = name;
        
        // Same values for every team to keep things simple
        this.finishing = RealWorldMapping.avgFinishing;
        this.shooting = RealWorldMapping.avgShooting;
    }
    
    /**
     * Add a player straight to the starting lineup
     * @param player The player object
     * @param position The player's position in the formation
     */
    public void addPlayer(Player player, Tactics.TacticLine position) {
        lineup.put(player, position);
    }
    
    public HashMap<Player, Tactics.TacticLine> getLineup() {
        return lineup;
    }
    
    public String getName() {
        return name;
    }
    
    public Stats getStats() {
        return stats;
    }
    
    public MatchEvents getMatchEvents() {
        return this.events;
    }
    
    public double getFinishing() {
        return finishing;
    }
    
    public double getShooting() {
        return shooting;
    }
    
    /**
     * Getter
     * @return The team goalkeeper
     */
    public Player getGK() {
        
        Set<Player> players = lineup.keySet();
        
        for (Player player:players) {
            if (lineup.get(player).equals(Tactics.TacticLine.GK)) return player;
        }
        
        return null;
    }
    
    /**
     * Find a defending player getting the rebound from an opponent's attacking effort
     * @return A random player who qualifies for the defending rebound (only a defender for the time being)
     */
    public Player getDefensiveRebounder() {
        
        Set<Player> players = lineup.keySet();
        ArrayList<Player> candidateRebounders = new ArrayList<Player>();
        
        for (Player player:players) {
            if (lineup.get(player).equals(Tactics.TacticLine.DEFENDER)) candidateRebounders.add(player);
        }
        
        int rebounderIndex = rnd.nextInt(candidateRebounders.size());
        
        return candidateRebounders.get(rebounderIndex);   
    }
    
    /**
     * Find an attacking player getting the rebound from its team's attacking effort
     * @return A weighted random player qualifying for the attacking rebound
     */
    public Player getAttackingRebounder() {
        
        Set<Player> players = lineup.keySet();
        HashMap<Player, Double> candidateRebounders = new HashMap<Player, Double>();
        double totalReboundingWeight = 0;
        for (Player player:players) {
            double reboundingWeight = RealWorldMapping.getReboundingWeight(lineup.get(player));
            totalReboundingWeight += reboundingWeight;
            candidateRebounders.put(player, reboundingWeight);
        }
        
        return getWeightedRandom(totalReboundingWeight, candidateRebounders);
    }

    /**
     * Calculate the weighted random of a number of players
     * @param total The sum of the players' weights
     * @param weightedPlayers The association of players to their weights
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
     * Calculates the scorer of a goal "after the fact"
     * @return The goal scorer according to some weighted distribution among players
     */ 
    public Player getGoalScorer() {
        
        HashMap<Player, Tactics.TacticLine> lineup = this.lineup;
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
        return null; // default
    }
}
