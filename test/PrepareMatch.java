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

package test;
// import java.io.PrintWriter;
import gameplay.Player;
import gameplay.PlayerAttribute;
import gameplay.PlayerAttributes;

import interactivity.EndOfMatch;
import interactivity.Signal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import utility.Config;
import utility.Tactics;
import core.Constants;
import core.Match;
import core.MatchReport;
import core.Team;

/**
 * Used for testing purposes. The 'preparation' of a match is demonstrated before its reproduction
 * 
 * @author Andreas Tasoulas
 *
 */

public class PrepareMatch {
    
    private static Random rnd = new Random();
    
    private static String DATABASE_ADDRESS;
    
    /**
     * The 'desktop' version of a match: it demonstrates the match preparation and outputs various information relevant to the match
     * @param args The database ids of the teams
     */
    public static void main(String[] args) {
        
        String homeTeamId = args[0];
        String awayTeamId = args[1];
        
        DATABASE_ADDRESS = Config.readConfig("database");
        
        // Attention! This is code is vulnerable to SQL Injection. Don't use it as is in a web app.
        String HOME_TEAM_QUERY = "select * from Team where Id=" + homeTeamId;
        String AWAY_TEAM_QUERY = "select * from Team where Id=" + awayTeamId;
        
        String HOME_SQUAD_QUERY = "select * from player, playerskills where player.id = playerskills.player and Team=" + 
        homeTeamId + " order by Position, player.id"; 
        
        String AWAY_SQUAD_QUERY = "select * from player, playerskills where player.id = playerskills.player and Team=" + 
        awayTeamId + " order by Position, player.id"; 
        
        Connection conn = null;
        Statement homeTeamStmt = null;
        Statement awayTeamStmt = null;
        
        Statement homeSquadStmt = null;
        Statement awaySquadStmt = null;
        
        try {
            
            ResultSet homeTeamRS = null;
            ResultSet awayTeamRS = null;
            
            ResultSet homeSquadRS = null;
            ResultSet awaySquadRS = null;
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            conn = DriverManager.getConnection(DATABASE_ADDRESS);
            
            homeTeamStmt = conn.createStatement();
            awayTeamStmt = conn.createStatement();
            
            homeSquadStmt = conn.createStatement();
            awaySquadStmt = conn.createStatement();
            
            if (homeTeamStmt.execute(HOME_TEAM_QUERY)) {
                homeTeamRS = homeTeamStmt.getResultSet();
            }
            
            if (awayTeamStmt.execute(AWAY_TEAM_QUERY)) {
                awayTeamRS = awayTeamStmt.getResultSet();
            }
            
            if (homeSquadStmt.execute(HOME_SQUAD_QUERY)) {
                homeSquadRS = homeSquadStmt.getResultSet();
            }
            
            if (awaySquadStmt.execute(AWAY_SQUAD_QUERY)) {
                awaySquadRS = awaySquadStmt.getResultSet();
            }
            
            // Extract match data from database
            
            homeTeamRS.next();
            awayTeamRS.next();
            
            String homeTeamName = homeTeamRS.getString("Name");
            String awayTeamName = awayTeamRS.getString("Name");
            
            Team homeTeam = new Team(homeTeamName);
            Team awayTeam = new Team(awayTeamName);
            
            while (homeSquadRS.next()) {
                
                int shirtNo = homeSquadRS.getInt("ShirtNo");
                String firstName = homeSquadRS.getString("FirstName");
                String lastName = homeSquadRS.getString("LastName");
                int position = homeSquadRS.getInt("Position");

                Player player = new Player(shirtNo, firstName, lastName, position);
                
                for (PlayerAttribute attr:PlayerAttributes.getAll()) {
                    player.addSkill(attr.getName(), homeSquadRS.getDouble(attr.getName()));
                }
                
                homeTeam.addPlayer(player, db2enum(position));
            }
            
            while (awaySquadRS.next()) {
                
                int shirtNo = awaySquadRS.getInt("ShirtNo");
                String firstName = awaySquadRS.getString("FirstName");
                String lastName = awaySquadRS.getString("LastName");
                int position = awaySquadRS.getInt("Position");
                
                Player player = new Player(shirtNo, firstName, lastName, position);
                
                for (PlayerAttribute attr:PlayerAttributes.getAll()) {
                    player.addSkill(attr.getName(), awaySquadRS.getDouble(attr.getName()));
                }
                
                awayTeam.addPlayer(player, db2enum(position));
            }
            
            // homeTeam.defineTactics();
            // awayTeam.defineTactics();
            
            homeTeam.alignPlayersDesktop();
            awayTeam.alignPlayersDesktop();
            
            System.out.println();
            
            homeTeam.displayLineup();
            System.out.println();
            awayTeam.displayLineup();
            
            System.out.println();
            
            Match match = new Match(homeTeam, awayTeam);
            
            Signal currentSignal = match.play(0); // Kick off
            // System.out.println(currentSignal);
            
            int count = 0;
            
            while (!(currentSignal instanceof EndOfMatch) && currentSignal != null) { 
                
                currentSignal = match.play(currentSignal.getTime() + 1);
                // System.out.println(currentSignal);
            }
            
            if (currentSignal instanceof EndOfMatch) 
                match.play(2 * MatchReport.halfDuration); // Final whistle
            
            // match.showProbModel();
            
            System.out.println();
            
            // match.getMatchRewind().dump();
            
        } catch (SQLException sqlex) {
            System.out.println(sqlex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            /*
             * close any jdbc instances here that weren't
             * explicitly closed during normal code path, so
             * that we don't 'leak' resources...
             */

            if (homeTeamStmt != null) {
                try {
                    homeTeamStmt.close();
                } catch (SQLException sqlex) {
                    // ignore -- as we can't do anything about it here
                }

                homeTeamStmt = null;
            }
            
            if (awayTeamStmt != null) {
                try {
                    awayTeamStmt.close();
                } catch (SQLException sqlex) {
                    // ignore -- as we can't do anything about it here
                }

                awayTeamStmt = null;
            }
            
            if (homeSquadStmt != null) {
                try {
                    homeSquadStmt.close();
                } catch (SQLException sqlex) {
                    
                }
                
                homeSquadStmt = null;
            }
            
            if (awaySquadStmt != null) {
                try {
                    awaySquadStmt.close();
                } catch (SQLException sqlex) {
                    
                }
                
                awaySquadStmt = null;
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlex) {
                    // ignore -- as we can't do anything about it here
                }
                conn = null;
            }
        }
    }
    
    /**
     * Testing only: displays the averages of the pass attribute strength per team and position in random basis
     * @param homeTeam The home team object
     * @param awayTeam The away team object
     */
    private static void avgTest(Team homeTeam, Team awayTeam) {
        
        ArrayList<Byte> positions = new ArrayList<Byte>();
        ArrayList<Boolean> isHomeTeam = new ArrayList<Boolean>();
        
        int testSize = rnd.nextInt(8) + 1;
        
        for (int i = 0; i < testSize; i++) {
            positions.add((byte) (rnd.nextInt(4) + 1));
            isHomeTeam.add(rnd.nextBoolean());
        }
        
        for (int i = 0; i < testSize; i++) {
            System.out.println("Position: " + positions.get(i));
            System.out.println("Team: " + (isHomeTeam.get(i)?homeTeam.getName():awayTeam.getName()));
            System.out.println("Average passing: " + (isHomeTeam.get(i)?homeTeam.getAverageFromAction(positions.get(i), Constants.Pass):awayTeam.getAverageFromAction(positions.get(i), Constants.Pass)));
            System.out.println();
        }
        
    }
    
    /**
     * Testing only: displayes the averages of random attributes strength per team and position in random basis
     * @param homeTeam The home team object
     * @param awayTeam The away team object
     */
    private static void avgAttributeTest(Team homeTeam, Team awayTeam) {
        
        ArrayList<String> allAttributes = new ArrayList<String>();
        
        int testSize = rnd.nextInt(10) + 1;
        
        for (PlayerAttribute attr:PlayerAttributes.getAll()) {
            allAttributes.add(attr.getName());
        }
        
        for (int i = 0; i < testSize; i++) {
            int currentPosition = rnd.nextInt(4) + 1;
            Team currentTeam = rnd.nextBoolean()?homeTeam:awayTeam;
            String attribute = allAttributes.get(rnd.nextInt(allAttributes.size()));
            
            System.out.println("Position: " + currentPosition);
            System.out.println("Team: " + currentTeam.getName());
            System.out.println("Attribute: " + attribute);
            System.out.println("Average value: " + currentTeam.getAverageFromAttribute(currentPosition, attribute));
            
            System.out.println();
            
        }
        
    }
    
    /**
     * Testing only: Display random attributes strengths of random players
     * @param homeTeam The home team object
     * @param awayTeam The away team object
     */
    private static void initTest(Team homeTeam, Team awayTeam) {
        
        ArrayList<String> allAttributes = new ArrayList<String>();
        
        for (PlayerAttribute attr:PlayerAttributes.getAll()) {
            allAttributes.add(attr.getName());
        }
        
        int numPlayers = rnd.nextInt(11) + 1;
        
        for (int i = 0; i < numPlayers; i++) {
            
            int testedAttribute = rnd.nextInt(allAttributes.size());
            int testedPlayer = rnd.nextInt(11);
            
            String attribute = allAttributes.get(testedAttribute);
            
            Player homePlayer = homeTeam.getAnyPlayer(testedPlayer);
            Player awayPlayer = awayTeam.getAnyPlayer(testedPlayer);
            
            System.out.println(homePlayer.getFamilyName() + " has attribute " + attribute + " of value " + homePlayer.getSkill(attribute));
            System.out.println(awayPlayer.getFamilyName() + " has attribute " + attribute + " of value " + awayPlayer.getSkill(attribute));
            
            System.out.println();
        }
        
    }
    
    /**
     * Utility: Transform player position as read from database to the corresponding enum value
     * @param pos
     * @return
     */
    private static Tactics.TacticLine db2enum(int pos) {
        
        switch (pos) {
        case 1:
            return Tactics.TacticLine.GK;
        case 2:
            return Tactics.TacticLine.DEFENDER;
        case 3:
            return Tactics.TacticLine.MIDFIELDER;
        case 4:
            return Tactics.TacticLine.FORWARD;
        }
        
        return null;
        
    }

}
