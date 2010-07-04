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

import interactivity.BallPossessionUpdate;
import interactivity.CornerKick;
import interactivity.CrossShot;
import interactivity.EndOfHalf;
import interactivity.EndOfMatch;
import interactivity.FreeKick;
import interactivity.Offside;
import interactivity.PenaltyKick;
import interactivity.Shot;
import interactivity.Signal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

import report.Report;
import report.ReportObject;
import representation.Challenge;
import representation.Instant;
import representation.Opponent;
import representation.ResultState;
import representation.Success;
import utility.Config;
import utility.Player;
import utility.RealWorldMapping;
import utility.Tactics;

/**
 * 
 * @author Andreas Tasoulas
 *
 */

public class Match {
    
    private String probModelFilename;
    private String matchReportFilename;
    private String playerStatsFilename;
    private String playerStatsSummFilename;
    
    private final static int halfDuration = MatchReport.halfDuration;
    
    private final String PROB_MODEL_KEY = "probmodel";
    private final String MATCH_REPORT_KEY = "matchreport";
    private final String PLAYER_STATS_KEY = "playerstats";
    private final String STATS_SUMMARY_KEY = "statssummary";
    
    private final static int reportUpdateTime = 25;
    private int tempUpdateTime = 25;
    private int updateWindow = 5;
    
    private ArrayList<WrapperObject> probModel = new ArrayList<WrapperObject>();
    private ArrayList<Instant> matchRepresentation = new ArrayList<Instant>();
    private static Random rnd = new Random();
    
    private Team homeTeam;
    private Team awayTeam;
    
    private MatchReport matchReport = new MatchReport();
    private MatchRewind matchRewind = new MatchRewind();
    
    public MatchReport getMatchReport() {
        return this.matchReport;
    }
    
    public MatchRewind getMatchRewind() {
        return this.matchRewind;
    }
    
    public Team getHomeTeam() {
        return homeTeam;
    }
    
    public void setHomeTeam(Team team) {
        this.homeTeam = team;
    }
    
    public Team getAwayTeam() {
        return awayTeam;
    }
    
    public void setAwayTeam(Team team) {
        this.awayTeam = team;
    }
    
    /**
     * Match constructor. The teams are initialized along with the relevant files.
     * These are:
     * Probability model: This contains the virtual match in binary format on which all matches will be based.
     * Match report: Contains a codified match report for debugging purposes
     * Player stats: Analytic player stats
     * Player stats summary: More 'human-readable' player stats summary
     * @param homeTeam
     * @param awayTeam
     */
    public Match(Team homeTeam, Team awayTeam) {
        
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.probModelFilename = Config.readConfig(this.PROB_MODEL_KEY);
        this.matchReportFilename = Config.readConfig(this.MATCH_REPORT_KEY);
        this.playerStatsFilename = Config.readConfig(this.PLAYER_STATS_KEY);
        this.playerStatsSummFilename = Config.readConfig(this.STATS_SUMMARY_KEY);
    }
    
    /**
     * Match constructor. Used for external invocation.
     * @param homeTeam Home team object
     * @param awayTeam Away team object
     * @param probModelFilename Probability model filename
     * @param matchReportFilename Match report filename
     * @see #Match(Team, Team)
     */
    public Match(Team homeTeam, Team awayTeam, String probModelFilename, String matchReportFilename) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.probModelFilename = probModelFilename;
        this.matchReportFilename = matchReportFilename;
    }
    
    /**
     * Match constructor. Used for external invocation.
     * @param homeTeam Home team object
     * @param awayTeam Away team object
     * @param probModelFilename Probability model filename
     * @param matchReportFilename Match report filename
     * @param playerStatsFilename Player stats filename
     * @param statsSummaryFilename Stats summary filename
     * @see #Match(Team, Team)
     */
    public Match(Team homeTeam, Team awayTeam, String probModelFilename, String matchReportFilename, String playerStatsFilename, 
            String statsSummaryFilename) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.probModelFilename = probModelFilename;
        this.matchReportFilename = matchReportFilename;
        this.playerStatsFilename = playerStatsFilename;
        this.playerStatsSummFilename = statsSummaryFilename;
    }
    
    /**
     * Plays the match loop starting from a certain time.
     * 
     * @param startTime The virtual time from which the match is resumed
     * @return The outcome of the match at the time of its interruption
     */
    public Signal play(int startTime) {
        
        if (startTime == 0) { // Kick-off
        
            loadProbModel();
        
            // Make probability model manipulation easier
            transformProbModel();
        
            Team kickOffTeam = decideKickOff();
            matchReport.setFirstHalfKickOff(kickOffTeam);
            matchReport.setCurrentState(new State(kickOffTeam, State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE));
            matchReport.getCurrentState().setPlayer(matchReport.getCurrentState().getTeam().getAnyDefensivePlayer());
            matchReport.getCurrentEvent().setActionState(matchReport.getCurrentState());
        }
        
        int probModelPool = probModel.size();
        
        boolean currentStateFound = false;
        State nextState = null;
        
        // First half
        if (startTime < halfDuration) {
            
            return playTimeFrame(probModelPool, startTime, halfDuration);
            
        } else if (startTime == halfDuration) {
        
            logHighlight(-1, "End of first half");
        
            logHighlight(-1, matchReport.getScoreLine(this.homeTeam, this.awayTeam));
            logHighlight(-1, "");
        
            // Second half
            matchReport.setCurrentState(new State(toggleTeam(matchReport.getFirstHalfKickOff()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE));
            matchReport.getCurrentState().setPlayer(matchReport.getCurrentState().getTeam().getAnyDefensivePlayer());
            matchReport.getCurrentEvent().setActionState(matchReport.getCurrentState());
        
            return playTimeFrame(probModelPool, halfDuration, 2 * halfDuration);
        
        } else if (startTime == 2 * halfDuration) { // end of match
        
            // Construct the match report and save it to a file
            
            ArrayList<ReportObject> matchReport = this.matchReport.getReport();
        
            try {
            
                PrintWriter outputStream = new PrintWriter(new FileWriter(matchReportFilename));
        
                for (ReportObject currentEvent:matchReport) {
                
                    String currentLine = "";
                
                    State actionState = currentEvent.getActionState();
                    currentLine += actionState.toString() + "," + Constants.actionDescription[currentEvent.getAction()];
                
                    if (currentEvent.isSpecial()) {
                        currentLine += "-> " + Report.resultDescription[currentEvent.getSpecial()];
                    }
                
                    outputStream.println(currentEvent.getActionState().getPlayer().getFamilyName());
                    outputStream.println(currentLine);
                }
            
                outputStream.close();
            
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            
            Collection<gameplay.Player> homeTeamLineup;
            Collection<gameplay.Player> awayTeamLineup;
            
            if (homeTeam.getSquadPlayers() != null && !homeTeam.getSquadPlayers().isEmpty()) {
                homeTeamLineup = (ArrayList<gameplay.Player>) homeTeam.getSquadPlayers();
            } else {
                homeTeamLineup = (Set<gameplay.Player>) homeTeam.getPlayers();
            }
            
            if (awayTeam.getSquadPlayers() != null && !awayTeam.getSquadPlayers().isEmpty()) {
                awayTeamLineup = (ArrayList<gameplay.Player>) awayTeam.getSquadPlayers();
            } else {
                awayTeamLineup = (Set<gameplay.Player>) awayTeam.getPlayers();
            }
        
            try {
            
                PrintWriter outputStream = new PrintWriter(new FileWriter(playerStatsFilename));
            
                for (gameplay.Player currentPlayer:homeTeamLineup) {
                
                    outputStream.println(currentPlayer.getFamilyName());
                
                    outputStream.println("Gk Long Pass: " + currentPlayer.getStats().gkLongPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().gkLongPass.getTotalAttempts());
                
                    outputStream.println("Long Pass: " + currentPlayer.getStats().longPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longPass.getTotalAttempts());
                
                    outputStream.println("Forward Pass: " + currentPlayer.getStats().forwardPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().forwardPass.getTotalAttempts());
                
                    outputStream.println("Flank Pass: " + currentPlayer.getStats().flankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().flankPass.getTotalAttempts());
                
                    outputStream.println("Ball Control: " + currentPlayer.getStats().ballControl.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().ballControl.getTotalAttempts());
                
                    outputStream.println("Dribbling: " + currentPlayer.getStats().dribbling.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().dribbling.getTotalAttempts());
                
                    outputStream.println("Long Flank Pass: " + currentPlayer.getStats().longFlankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longFlankPass.getTotalAttempts());
                
                    outputStream.println("Pass: " + currentPlayer.getStats().pass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().pass.getTotalAttempts());
                
                    outputStream.println("Area Pass: " + currentPlayer.getStats().areaPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().areaPass.getTotalAttempts());
                
                    outputStream.println("Run Ball: " + currentPlayer.getStats().runBall.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().runBall.getTotalAttempts());
                
                    outputStream.println("Low Cross: " + currentPlayer.getStats().lowCross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().lowCross.getTotalAttempts());
                
                    outputStream.println("Cross: " + currentPlayer.getStats().cross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().cross.getTotalAttempts());
                
                    outputStream.println("Shots: " + currentPlayer.getStats().shots.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().shots.getTotalAttempts());
                
                    outputStream.println("Headings: " + currentPlayer.getStats().headingsOnTarget.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().headingsOnTarget.getTotalAttempts());
                
                    outputStream.println("Individual challenges: " + currentPlayer.getStats().personalChallenges.getOccurences());
                    outputStream.println("Interceptions: " + currentPlayer.getStats().interceptions.getOccurences());
                    outputStream.println("Saves: " + currentPlayer.getStats().saves.getOccurences());
                    outputStream.println("Concedings: " + currentPlayer.getStats().concedings.getOccurences());
                    outputStream.println();
                
                }
            
                for (gameplay.Player currentPlayer:awayTeamLineup) {
                
                    outputStream.println(currentPlayer.getFamilyName());
                
                    outputStream.println("Gk Long Pass: " + currentPlayer.getStats().gkLongPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().gkLongPass.getTotalAttempts());
                
                    outputStream.println("Long Pass: " + currentPlayer.getStats().longPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longPass.getTotalAttempts());
                
                    outputStream.println("Forward Pass: " + currentPlayer.getStats().forwardPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().forwardPass.getTotalAttempts());
                
                    outputStream.println("Flank Pass: " + currentPlayer.getStats().flankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().flankPass.getTotalAttempts());
                
                    outputStream.println("Ball Control: " + currentPlayer.getStats().ballControl.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().ballControl.getTotalAttempts());
                
                    outputStream.println("Dribbling: " + currentPlayer.getStats().dribbling.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().dribbling.getTotalAttempts());
                
                    outputStream.println("Long Flank Pass: " + currentPlayer.getStats().longFlankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longFlankPass.getTotalAttempts());
                
                    outputStream.println("Pass: " + currentPlayer.getStats().pass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().pass.getTotalAttempts());
                
                    outputStream.println("Area Pass: " + currentPlayer.getStats().areaPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().areaPass.getTotalAttempts());
                
                    outputStream.println("Run Ball: " + currentPlayer.getStats().runBall.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().runBall.getTotalAttempts());
                
                    outputStream.println("Low Cross: " + currentPlayer.getStats().lowCross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().lowCross.getTotalAttempts());
                
                    outputStream.println("Cross: " + currentPlayer.getStats().cross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().cross.getTotalAttempts());
                
                    outputStream.println("Shots: " + currentPlayer.getStats().shots.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().shots.getTotalAttempts());
                
                    outputStream.println("Headings: " + currentPlayer.getStats().headingsOnTarget.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().headingsOnTarget.getTotalAttempts());
                
                    outputStream.println("Individual challenges: " + currentPlayer.getStats().personalChallenges.getOccurences());
                    outputStream.println("Interceptions: " + currentPlayer.getStats().interceptions.getOccurences());
                    outputStream.println("Saves: " + currentPlayer.getStats().saves.getOccurences());
                    outputStream.println("Concedings: " + currentPlayer.getStats().concedings.getOccurences());
                    outputStream.println();
                
                }
            
                outputStream.close();
            
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        
            // Player stats summary
        
            try {
            
                PrintWriter outputStream = new PrintWriter(new FileWriter(playerStatsSummFilename));
            
                // Set<gameplay.Player> homeTeamLineup = homeTeam.getPlayers();
                // Set<gameplay.Player> awayTeamLineup = awayTeam.getPlayers();
            
                for (gameplay.Player currentPlayer:homeTeamLineup) {
                
                    outputStream.println(currentPlayer.getFamilyName());
                
                    outputStream.println("Passes: " + currentPlayer.getStats().getAllPassesSucc() + "/" + 
                        currentPlayer.getStats().getAllPassesTotal());
                
                    outputStream.println("Personal challenges (attacking): " + currentPlayer.getStats().getAttPersonalChallengesSucc() + "/" +
                        currentPlayer.getStats().getAttPersonalChallengesTotal());
                
                    outputStream.println("Crosses: " + currentPlayer.getStats().getCrossesSucc() + "/" + 
                        currentPlayer.getStats().getCrossesTotal());
                
                    outputStream.println("Player runs: " + currentPlayer.getStats().getRunsSucc() + "/" +
                        currentPlayer.getStats().getRunsTotal());
                
                    outputStream.println("Personal challenges won (defending): " + currentPlayer.getStats().getDefChallengesWon());
                
                    outputStream.println("Interceptions: " + currentPlayer.getStats().getInterceptions());
                
                    outputStream.println("Saves: " + currentPlayer.getStats().getSaves());
                
                    outputStream.println("Attempts at goal: " + currentPlayer.getStats().getAllShotsSucc() + "/" +
                        currentPlayer.getStats().getAllShotsTotal());
                
                    outputStream.println("Goals scored: " + currentPlayer.getStats().getGoals());
                
                    outputStream.println("Penalties missed: " + currentPlayer.getStats().getPenaltiesMissed());
                
                    outputStream.println("Penalties saved: " + currentPlayer.getStats().getPenaltiesSaved());
                
                    outputStream.println();
                
                }
            
                for (gameplay.Player currentPlayer:awayTeamLineup) {
                
                    outputStream.println(currentPlayer.getFamilyName());
                
                    outputStream.println("Passes: " + currentPlayer.getStats().getAllPassesSucc() + "/" + 
                        currentPlayer.getStats().getAllPassesTotal());
                
                    outputStream.println("Personal challenges (attacking): " + currentPlayer.getStats().getAttPersonalChallengesSucc() + "/" +
                        currentPlayer.getStats().getAttPersonalChallengesTotal());
                
                    outputStream.println("Crosses: " + currentPlayer.getStats().getCrossesSucc() + "/" + 
                        currentPlayer.getStats().getCrossesTotal());
                
                    outputStream.println("Player runs: " + currentPlayer.getStats().getRunsSucc() + "/" +
                        currentPlayer.getStats().getRunsTotal());
                
                    outputStream.println("Personal challenges won (defending): " + currentPlayer.getStats().getDefChallengesWon());
                
                    outputStream.println("Interceptions: " + currentPlayer.getStats().getInterceptions());
                
                    outputStream.println("Saves: " + currentPlayer.getStats().getSaves());
                
                    outputStream.println("Attempts at goal: " + currentPlayer.getStats().getAllShotsSucc() + "/" +
                        currentPlayer.getStats().getAllShotsTotal());
                
                    outputStream.println("Goals scored: " + currentPlayer.getStats().getGoals());
                
                    outputStream.println("Penalties missed: " + currentPlayer.getStats().getPenaltiesMissed());
                
                    outputStream.println("Penalties saved: " + currentPlayer.getStats().getPenaltiesSaved());
                
                    outputStream.println();
                
                }
            
                outputStream.close();
            
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        
            // Match summary
            logHighlight(-1, "End of match");
            logHighlight(-1, this.matchReport.getScoreLine(this.homeTeam, this.awayTeam));
            logHighlight(-1, "");
        
            System.out.println("Match events");
            System.out.println("-------------");
            System.out.println();
        
            System.out.println(homeTeam.getName());
            printTeamSummary(homeTeam);
        
            System.out.println();
        
            System.out.println(awayTeam.getName());
            printTeamSummary(awayTeam);
        
            System.out.println();
        
            System.out.println("Stats");
            System.out.println("------");
            System.out.println();
        
            int totalBallPossession = homeTeam.getStats().getPossessionCount() + awayTeam.getStats().getPossessionCount();
        
            System.out.println(homeTeam.getName());
            printTeamStats(homeTeam, totalBallPossession);
        
            System.out.println();
        
            System.out.println(awayTeam.getName());
            printTeamStats(awayTeam, totalBallPossession);
        
            // Print individual player ratings
        
            System.out.println();
            System.out.println("Player ratings");
            System.out.println();
        
            // End: Player stats summary
            
            // ArrayList<gameplay.Player> homeTeamLineup = homeTeam.getSquadPlayers();
            // ArrayList<gameplay.Player> awayTeamLineup = awayTeam.getSquadPlayers();
        
            System.out.println(homeTeam.getName());
            System.out.println();
        
            for (gameplay.Player currentPlayer:homeTeamLineup) {
                System.out.println(currentPlayer.getFamilyName() + " : " + currentPlayer.getStats().getRating(homeTeam, currentPlayer));
                System.out.println();
            }
        
            System.out.println();
        
            System.out.println(awayTeam.getName());
            System.out.println();
        
            for (gameplay.Player currentPlayer:awayTeamLineup) {
                System.out.println(currentPlayer.getFamilyName() + " : " + currentPlayer.getStats().getRating(awayTeam, currentPlayer));
                System.out.println();
            }
        
        } else if (startTime > halfDuration) {
            
            return playTimeFrame(probModelPool, startTime, 2 * halfDuration);
            
        }
        
        return null;
        
    }
    
    /**
     * 
     * Starts and executes the match loop. During the match and in the end it outputs the relevant statistics and events. 
     * This is used for simulating a match from start to end without any interruptions. 
     * 
     */
    public void start() {
        
        loadProbModel();
        
        // Make probability model manipulation easier
        transformProbModel();
        
        Team kickOffTeam = decideKickOff();
        matchReport.setFirstHalfKickOff(kickOffTeam);
        matchReport.setCurrentState(new State(kickOffTeam, State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE));
        matchReport.getCurrentState().setPlayer(matchReport.getCurrentState().getTeam().getAnyDefensivePlayer());
        matchReport.getCurrentEvent().setActionState(matchReport.getCurrentState());
        
        int probModelPool = probModel.size();
        
        boolean currentStateFound = false;
        State nextState = null;
        
        // First half
        playTimeFrame(probModelPool, 0, halfDuration);
        
        logHighlight(-1, "End of first half");
        
        logHighlight(-1, matchReport.getScoreLine(this.homeTeam, this.awayTeam));
        logHighlight(-1, "");
        
        // Second half
        matchReport.setCurrentState(new State(toggleTeam(kickOffTeam), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE));
        matchReport.getCurrentState().setPlayer(matchReport.getCurrentState().getTeam().getAnyDefensivePlayer());
        matchReport.getCurrentEvent().setActionState(matchReport.getCurrentState());
        
        playTimeFrame(probModelPool, halfDuration, 2 * halfDuration);
        
        // Construct the match report and save it to a file
        
        ArrayList<ReportObject> matchReport = this.matchReport.getReport();
        
        try {
            
            PrintWriter outputStream = new PrintWriter(new FileWriter(matchReportFilename));
        
            for (ReportObject currentEvent:matchReport) {
                
                String currentLine = "";
                
                State actionState = currentEvent.getActionState();
                currentLine += actionState.toString() + "," + Constants.actionDescription[currentEvent.getAction()];
                
                if (currentEvent.isSpecial()) {
                    currentLine += "-> " + Report.resultDescription[currentEvent.getSpecial()];
                }
                
                outputStream.println(currentEvent.getActionState().getPlayer().getFamilyName());
                outputStream.println(currentLine);
            }
            
            outputStream.close();
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        try {
            
            PrintWriter outputStream = new PrintWriter(new FileWriter(playerStatsFilename));
            
            Set<gameplay.Player> homeTeamLineup = homeTeam.getPlayers();
            Set<gameplay.Player> awayTeamLineup = awayTeam.getPlayers();
            
            for (gameplay.Player currentPlayer:homeTeamLineup) {
                
                outputStream.println(currentPlayer.getFamilyName());
                
                outputStream.println("Gk Long Pass: " + currentPlayer.getStats().gkLongPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().gkLongPass.getTotalAttempts());
                
                outputStream.println("Long Pass: " + currentPlayer.getStats().longPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longPass.getTotalAttempts());
                
                outputStream.println("Forward Pass: " + currentPlayer.getStats().forwardPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().forwardPass.getTotalAttempts());
                
                outputStream.println("Flank Pass: " + currentPlayer.getStats().flankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().flankPass.getTotalAttempts());
                
                outputStream.println("Ball Control: " + currentPlayer.getStats().ballControl.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().ballControl.getTotalAttempts());
                
                outputStream.println("Dribbling: " + currentPlayer.getStats().dribbling.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().dribbling.getTotalAttempts());
                
                outputStream.println("Long Flank Pass: " + currentPlayer.getStats().longFlankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longFlankPass.getTotalAttempts());
                
                outputStream.println("Pass: " + currentPlayer.getStats().pass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().pass.getTotalAttempts());
                
                outputStream.println("Area Pass: " + currentPlayer.getStats().areaPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().areaPass.getTotalAttempts());
                
                outputStream.println("Run Ball: " + currentPlayer.getStats().runBall.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().runBall.getTotalAttempts());
                
                outputStream.println("Low Cross: " + currentPlayer.getStats().lowCross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().lowCross.getTotalAttempts());
                
                outputStream.println("Cross: " + currentPlayer.getStats().cross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().cross.getTotalAttempts());
                
                outputStream.println("Shots: " + currentPlayer.getStats().shots.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().shots.getTotalAttempts());
                
                outputStream.println("Headings: " + currentPlayer.getStats().headingsOnTarget.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().headingsOnTarget.getTotalAttempts());
                
                outputStream.println("Individual challenges: " + currentPlayer.getStats().personalChallenges.getOccurences());
                outputStream.println("Interceptions: " + currentPlayer.getStats().interceptions.getOccurences());
                outputStream.println("Saves: " + currentPlayer.getStats().saves.getOccurences());
                outputStream.println("Concedings: " + currentPlayer.getStats().concedings.getOccurences());
                outputStream.println();
                
             }
            
            for (gameplay.Player currentPlayer:awayTeamLineup) {
                
                outputStream.println(currentPlayer.getFamilyName());
                
                outputStream.println("Gk Long Pass: " + currentPlayer.getStats().gkLongPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().gkLongPass.getTotalAttempts());
                
                outputStream.println("Long Pass: " + currentPlayer.getStats().longPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longPass.getTotalAttempts());
                
                outputStream.println("Forward Pass: " + currentPlayer.getStats().forwardPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().forwardPass.getTotalAttempts());
                
                outputStream.println("Flank Pass: " + currentPlayer.getStats().flankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().flankPass.getTotalAttempts());
                
                outputStream.println("Ball Control: " + currentPlayer.getStats().ballControl.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().ballControl.getTotalAttempts());
                
                outputStream.println("Dribbling: " + currentPlayer.getStats().dribbling.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().dribbling.getTotalAttempts());
                
                outputStream.println("Long Flank Pass: " + currentPlayer.getStats().longFlankPass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().longFlankPass.getTotalAttempts());
                
                outputStream.println("Pass: " + currentPlayer.getStats().pass.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().pass.getTotalAttempts());
                
                outputStream.println("Area Pass: " + currentPlayer.getStats().areaPass.getSuccessfulAttempts() + "/" + 
                        currentPlayer.getStats().areaPass.getTotalAttempts());
                
                outputStream.println("Run Ball: " + currentPlayer.getStats().runBall.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().runBall.getTotalAttempts());
                
                outputStream.println("Low Cross: " + currentPlayer.getStats().lowCross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().lowCross.getTotalAttempts());
                
                outputStream.println("Cross: " + currentPlayer.getStats().cross.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().cross.getTotalAttempts());
                
                outputStream.println("Shots: " + currentPlayer.getStats().shots.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().shots.getTotalAttempts());
                
                outputStream.println("Headings: " + currentPlayer.getStats().headingsOnTarget.getSuccessfulAttempts() + "/" +
                        currentPlayer.getStats().headingsOnTarget.getTotalAttempts());
                
                outputStream.println("Individual challenges: " + currentPlayer.getStats().personalChallenges.getOccurences());
                outputStream.println("Interceptions: " + currentPlayer.getStats().interceptions.getOccurences());
                outputStream.println("Saves: " + currentPlayer.getStats().saves.getOccurences());
                outputStream.println("Concedings: " + currentPlayer.getStats().concedings.getOccurences());
                outputStream.println();
                
            }
            
            outputStream.close();
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        // Player stats summary
        
        try {
            
            PrintWriter outputStream = new PrintWriter(new FileWriter(playerStatsSummFilename));
            
            Set<gameplay.Player> homeTeamLineup = homeTeam.getPlayers();
            Set<gameplay.Player> awayTeamLineup = awayTeam.getPlayers();
            
            for (gameplay.Player currentPlayer:homeTeamLineup) {
                
                outputStream.println(currentPlayer.getFamilyName());
                
                outputStream.println("Passes: " + currentPlayer.getStats().getAllPassesSucc() + "/" + 
                        currentPlayer.getStats().getAllPassesTotal());
                
                outputStream.println("Personal challenges (attacking): " + currentPlayer.getStats().getAttPersonalChallengesSucc() + "/" +
                        currentPlayer.getStats().getAttPersonalChallengesTotal());
                
                outputStream.println("Crosses: " + currentPlayer.getStats().getCrossesSucc() + "/" + 
                        currentPlayer.getStats().getCrossesTotal());
                
                outputStream.println("Player runs: " + currentPlayer.getStats().getRunsSucc() + "/" +
                        currentPlayer.getStats().getRunsTotal());
                
                outputStream.println("Personal challenges won (defending): " + currentPlayer.getStats().getDefChallengesWon());
                
                outputStream.println("Interceptions: " + currentPlayer.getStats().getInterceptions());
                
                outputStream.println("Saves: " + currentPlayer.getStats().getSaves());
                
                outputStream.println("Attempts at goal: " + currentPlayer.getStats().getAllShotsSucc() + "/" +
                        currentPlayer.getStats().getAllShotsTotal());
                
                outputStream.println("Goals scored: " + currentPlayer.getStats().getGoals());
                
                outputStream.println("Penalties missed: " + currentPlayer.getStats().getPenaltiesMissed());
                
                outputStream.println("Penalties saved: " + currentPlayer.getStats().getPenaltiesSaved());
                
                outputStream.println();
                
             }
            
            for (gameplay.Player currentPlayer:awayTeamLineup) {
                
                outputStream.println(currentPlayer.getFamilyName());
                
                outputStream.println("Passes: " + currentPlayer.getStats().getAllPassesSucc() + "/" + 
                        currentPlayer.getStats().getAllPassesTotal());
                
                outputStream.println("Personal challenges (attacking): " + currentPlayer.getStats().getAttPersonalChallengesSucc() + "/" +
                        currentPlayer.getStats().getAttPersonalChallengesTotal());
                
                outputStream.println("Crosses: " + currentPlayer.getStats().getCrossesSucc() + "/" + 
                        currentPlayer.getStats().getCrossesTotal());
                
                outputStream.println("Player runs: " + currentPlayer.getStats().getRunsSucc() + "/" +
                        currentPlayer.getStats().getRunsTotal());
                
                outputStream.println("Personal challenges won (defending): " + currentPlayer.getStats().getDefChallengesWon());
                
                outputStream.println("Interceptions: " + currentPlayer.getStats().getInterceptions());
                
                outputStream.println("Saves: " + currentPlayer.getStats().getSaves());
                
                outputStream.println("Attempts at goal: " + currentPlayer.getStats().getAllShotsSucc() + "/" +
                        currentPlayer.getStats().getAllShotsTotal());
                
                outputStream.println("Goals scored: " + currentPlayer.getStats().getGoals());
                
                outputStream.println("Penalties missed: " + currentPlayer.getStats().getPenaltiesMissed());
                
                outputStream.println("Penalties saved: " + currentPlayer.getStats().getPenaltiesSaved());
                
                outputStream.println();
                
            }
            
            outputStream.close();
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        // Match summary
        logHighlight(-1, "End of game");
        logHighlight(-1, this.matchReport.getScoreLine(this.homeTeam, this.awayTeam));
        logHighlight(-1, "");
        
        System.out.println("Match events");
        System.out.println("-------------");
        System.out.println();
        
        System.out.println(homeTeam.getName());
        printTeamSummary(homeTeam);
        
        System.out.println();
        
        System.out.println(awayTeam.getName());
        printTeamSummary(awayTeam);
        
        System.out.println();
        
        System.out.println("Stats");
        System.out.println("------");
        System.out.println();
        
        int totalBallPossession = homeTeam.getStats().getPossessionCount() + awayTeam.getStats().getPossessionCount();
        
        System.out.println(homeTeam.getName());
        printTeamStats(homeTeam, totalBallPossession);
        
        System.out.println();
        
        System.out.println(awayTeam.getName());
        printTeamStats(awayTeam, totalBallPossession);
        
        // Print individual player ratings
        
        System.out.println();
        System.out.println("Player ratings");
        System.out.println();
        
        Set<gameplay.Player> homeTeamLineup = homeTeam.getPlayers();
        Set<gameplay.Player> awayTeamLineup = awayTeam.getPlayers();
        
        System.out.println(homeTeam.getName());
        System.out.println();
        
        for (gameplay.Player currentPlayer:homeTeamLineup) {
            System.out.println(currentPlayer.getFamilyName() + " : " + currentPlayer.getStats().getRating(homeTeam, currentPlayer));
            System.out.println();
        }
        
        System.out.println();
        
        System.out.println(awayTeam.getName());
        System.out.println();
        
        for (gameplay.Player currentPlayer:awayTeamLineup) {
            System.out.println(currentPlayer.getFamilyName() + " : " + currentPlayer.getStats().getRating(awayTeam, currentPlayer));
            System.out.println();
        }
        
    }

    /**
     * Play match within a specific time frame
     * @param probModelPool The number of "events" within the virtual match
     * @param timerStart The time frame start in virtual time
     * @param timerEnd The time frame end in virtual time
     * @return The outcome of the match when the time frame has ended or interrupted
     */
    private Signal playTimeFrame(int probModelPool, int timerStart, int timerEnd) {
        
        State nextState;
        
        int localCount = 0;
        matchRewind.reset();
        
        for (int timer = timerStart; timer < timerEnd; timer++) {
            
            matchReport.setTimer(timer);
            
            // Process current signal
            if (matchRewind.getCurrentSignal() != null) {
                
                if (timer % tempUpdateTime == 0) 
                    tempUpdateTime -= updateWindow; // Reduce update window so that we match a ball possession update eventually
                
                if (tempUpdateTime <= 0) tempUpdateTime = 1;
                
                if (timer != Match.halfDuration && timer != 2 * Match.halfDuration) {
                
                    return matchRewind.getCurrentSignal();
                    
                } else if (timer == Match.halfDuration) {
                    
                    Signal currentSignal = matchRewind.getCurrentSignal();
                    
                    Signal endOfHalf = new EndOfHalf(timer);
                    matchRewind.addSignal(endOfHalf);
                    
                    return currentSignal;
                    
                } else { // timer has reached the end of match
                    
                    Signal currentSignal = matchRewind.getCurrentSignal();
                    
                    // System.out.println("Match ended due to timer reaching end");
                    
                    Signal endOfMatch = new EndOfMatch(timer);
                    matchRewind.addSignal(endOfMatch);
                    
                    return currentSignal;
                    
                }
            }
            
            // First half has ended
            if (timer >= Match.halfDuration && !matchRewind.isSecondHalf()) {
                
                Signal endOfHalf = new EndOfHalf(timer);
                
                matchRewind.addSignal(endOfHalf);
                
                return endOfHalf;
                
            }
            
            // End of match
            if (timer == 2 * Match.halfDuration) {
                
                // System.out.println("No signal and timer is on the end of the match");
                
                Signal endOfMatch = new EndOfMatch(timer);
                matchRewind.addSignal(endOfMatch);
                
                return endOfMatch;
                
            }
            
            ++localCount;
            
            if ((localCount + timerStart) % tempUpdateTime == 0) {
                
                tempUpdateTime = reportUpdateTime; // Get back to default value
                
                int totalBallInPlay = homeTeam.getStats().getPossessionCount() + awayTeam.getStats().getPossessionCount();
                
                int homeTeamPoss = getBallPossession(homeTeam, totalBallInPlay);
                int awayTeamPoss = getBallPossession(awayTeam, totalBallInPlay);
                
                BallPossessionUpdate ballPossessionUpdate = 
                    new BallPossessionUpdate(timerStart + localCount + 1, homeTeamPoss, awayTeamPoss, homeTeam.getName(), awayTeam.getName());
                
                matchRewind.addSignal(ballPossessionUpdate);
                
                return ballPossessionUpdate;
            }
        
            // Find current state
            nextState = null;
            
            ArrayList<Integer> candidateStates = new ArrayList<Integer>();
                
            for (int checkState = 0; checkState < probModelPool; checkState++) {
                
                Instant currentInstant = matchRepresentation.get(checkState);
                
                if (matchCurrentState(currentInstant, matchReport.getCurrentState()) && 
                        actionAllowed(currentInstant.Action, matchReport.getCurrentState().getPlayer().getPosition())) {
                    candidateStates.add(checkState);
                }
            }
            
            int checkState = -1;
            
            if (candidateStates.size() == 1) {
                checkState = candidateStates.get(0);
            } else {
                checkState = candidateStates.get(rnd.nextInt(candidateStates.size()));
            }
            
            nextState = processCurrentInstant(matchRepresentation.get(checkState));
            
            matchReport.setCurrentState(nextState);
            
            // put it in tail and head of the new state
            matchReport.getCurrentEvent().setResultState(nextState);
            
            matchReport.submitEvent();
            matchReport.getCurrentEvent().setActionState(nextState);
        }
        
        // Add end of half and end of match events for rewind
        if (timerEnd == Match.halfDuration && !matchRewind.isSecondHalf()) { // Don't replicate half time
            
            Signal endOfHalf = new EndOfHalf(timerEnd);
            
            matchRewind.addSignal(endOfHalf);
        
            return endOfHalf;
            
        } else if (timerEnd == 2 * Match.halfDuration) {
            
            Signal endOfMatch = new EndOfMatch(timerEnd);
            
            matchRewind.addSignal(endOfMatch);
            
            return endOfMatch;
        }
        
        return null;
        
    }

    /**
     * Print highlight to System.out and append it in highlight structure to be included in match report
     * @param time The virtual time that the highlight has taken place. It should be above zero to be included in match report to avoid redundancy there
     * @param highlight The String representation of the highlight as it will be displayed
     */
    private void logHighlight(Integer time, String highlight) {
        System.out.println(highlight);
        if (time > 0)
            matchReport.appendHighlight(time, highlight);
    }
    
    /**
     * Print team stats to System.out
     * @param team The team object
     * @param ballInPlay The match duration in virtual time
     */
    private static void printTeamStats(Team team, int ballInPlay) {
        
        double ballPossession = (double) team.getStats().getPossessionCount() / (double) ballInPlay;
        ballPossession *= 100;
        
        System.out.println("Ball possession: " + new Double(Math.round(ballPossession)).intValue() + "%");
        
        System.out.println("Shots on target: " + team.getStats().getShotOnTarget());
        System.out.println("Shots off target: " + team.getStats().getShotsOffTarget());
        System.out.println("Corner kicks: " + team.getStats().getCornerKicks());
        System.out.println("Offsides: " + team.getStats().getOffsides());
        System.out.println("Free kicks: " + team.getStats().getFreeKicks());
    }
    
    /**
     * Calculate the ball possession of a team for a specific total "ball in play" virtual time. Special rounding is applied so that the total ball
     * possession of both teams does not exceed 100%
     * @param team The team object
     * @param ballInPlay The total virtual time steps that the ball is in play
     * @return The team's ball possession as percentage
     */
    private int getBallPossession(Team team, int ballInPlay) {
        
        boolean dominant = true;
        double oppBallPossession = 0;
        
        // Quick check if the team's possession is dominant
        if (team.getStats().getPossessionCount() < (ballInPlay - team.getStats().getPossessionCount())) {
            dominant = false;
        }
        
        double ballPossession = (double) team.getStats().getPossessionCount() / (double) ballInPlay;
        ballPossession *= 100;
        
        if (!dominant) {
            oppBallPossession = (double) (ballInPlay - team.getStats().getPossessionCount()) / (double) ballInPlay;
            oppBallPossession *= 100;
        }
        
        int retBallPossession = new Double(Math.round(ballPossession)).intValue();
        int oppBallPossessionInt = 0;
        
        if (oppBallPossession > 0) {
            oppBallPossessionInt = new Double(Math.round(oppBallPossession)).intValue();
        }
        
        if (retBallPossession + oppBallPossessionInt <= 100)
            return retBallPossession;
        else 
            return new Double(Math.round(ballPossession - 0.5)).intValue();
        
    }

    /**
     * Print team main events descriptions (goals and missed penalties) to System.out 
     * @param team The team object
     */
    private static void printTeamSummary(Team team) {
        for (Team.GoalDetails goalEvent:team.getMatchEvents().getGoalDetails()) {
            System.out.println(goalEvent);
        }
        for (Team.MissedPenaltyDetails missedPenalty:team.getMatchEvents().getPenaltyDetails()) {
            System.out.println(missedPenalty);
        }
    }
    
    /**
     * Calculate the action result based on weighted outcomes
     * @param outcomes The weighted outcomes each corresponding to a specific action
     * @return The index of the "selected" outcome 
     */
    private int getActionResult(double [] outcomes) {
        
        double total = 0;
        
        for (int i = 0; i < outcomes.length; i++) {
            total += outcomes[i];
        }
        
        double outcome = rnd.nextDouble() * total;
        
        double currentTotal = 0;
        for (int i = 0; i < outcomes.length; i++) {
            currentTotal += outcomes[i];
            if (outcome < currentTotal) return i;
        }
        return -1;
    }
    
    /**
     * Finds an instant with a 'cross' action in the probability model compatible with the given parameters
     * @param initInstant The initial instant
     * @param selector The classification of the desired outcome
     * @return The matching instant
     */
    private Instant getMatchingCross(Instant initInstant, int selector) {
        
        ArrayList<Instant> candidateInstants = new ArrayList<Instant>();
        
        boolean selCondition = false;
        
        for (Instant instant:matchRepresentation) {
            
            switch (selector) {
            case OOConstants.OPPOSITION:
                selCondition = (instant.outcome instanceof Opponent);
                break;
            case OOConstants.SUCC_CH:
                selCondition =  ((instant.outcome instanceof Success) || (instant.outcome instanceof Challenge));
                break;
            case OOConstants.PASS_INTERCEPTION:
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_PassInterception));
                break;
            case OOConstants.MAN_CHALLENGE_LOST:
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_ManChallengeLost));
                break;
            case OOConstants.UNFORCED_POSSESSION_CHANGE:
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_Normal));
                break;
            case OOConstants.GK_INTERCEPTION:
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_Gk));
                break;
            case OOConstants.GOAL_OPPORTUNITY_CROSS:
                selCondition = goalScoringOpportunity(initInstant);
            } 
            
            if (instant.equals(initInstant) && selCondition) {
                candidateInstants.add(instant);
            }
        }
        
        if (candidateInstants.size() == 0) {
            
            // System.out.println("Could not find matching cross: " + initInstant);
            
            if (selector == OOConstants.OPPOSITION || selector == OOConstants.SUCC_CH || selector == OOConstants.GOAL_OPPORTUNITY_CROSS) { // The most generic cases
                return initInstant;
            } else {
                return getMatchingInstant(initInstant, OOConstants.OPPOSITION, true); // Specialised cases lead to opposition ball possession
            }
        }
        
        if (candidateInstants.size() == 1) {
            return candidateInstants.get(0);
        }
        
        return candidateInstants.get(rnd.nextInt(candidateInstants.size()));
        
    }
    
    /**
     * Finds an instant in the probability model compatible with the given parameters
     * @param initInstant The initial instant
     * @param selector The classification of the outcome
     * @param pressed True if the holder of the ball is under pressing
     * @return A matching instant
     */
    private Instant getMatchingInstant(Instant initInstant, int selector, boolean pressed) {
        
        ArrayList<Instant> candidateInstants = new ArrayList<Instant>();
        ArrayList<Instant> paceInstants = new ArrayList<Instant>();
        
        boolean selCondition = false;
        
        for (Instant instant:matchRepresentation) {
            
            switch (selector) {
            case OOConstants.OPPOSITION:
                selCondition = (instant.outcome instanceof Opponent);
                break;
            case OOConstants.SUCC_CH:
                selCondition =  (((instant.outcome instanceof Success) || (instant.outcome instanceof Challenge)) && 
                        !isResultFoul(instant.outcome.condition));
                break;
            case OOConstants.PASS_INTERCEPTION:
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_PassInterception));
                break;
            case OOConstants.MAN_CHALLENGE_LOST:
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_ManChallengeLost));
                break;
            case OOConstants.UNFORCED_POSSESSION_CHANGE:
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_Normal));
                break;
            case OOConstants.GK_INTERCEPTION:
                // System.out.println("Gk interception");
                selCondition = ((instant.outcome instanceof Opponent) && (
                        ((Opponent) instant.outcome).possessionChange == Constants.BPC_Gk));
                break;
            }
            
            if (instant.equals(initInstant) && selCondition && pressureMatched(instant, pressed)) {
                candidateInstants.add(instant);
            }
            
            if (instant.equals(initInstant) && selCondition) {
                paceInstants.add(instant);
            }
        }
        
        if (candidateInstants.size() == 0) {
            
            // System.out.println("Could not find matching instant: " + initInstant);
            
            if (paceInstants.size() > 0) {
                // System.out.println("Excluding pressure");
                return paceInstants.get(rnd.nextInt(paceInstants.size()));
            }
            
            if (selector == OOConstants.OPPOSITION || selector == OOConstants.SUCC_CH) { // The most generic cases
                // System.out.println("Generic");
                return initInstant;
            } else {
                // System.out.println("Opposition");
                return getMatchingInstant(initInstant, OOConstants.OPPOSITION, pressed); // Specialised cases lead to opposition ball possession
            }
        } 
        
        if (candidateInstants.size() == 1) {
            return candidateInstants.get(0);
        }
        
        return candidateInstants.get(rnd.nextInt(candidateInstants.size()));
        
    }
    
    /**
     * Processing of the current instant. The instant is a record in the probability model file which is a sample simulation of a football match.
     * It essentially has a format of 'initial event -> outcome'. The processing is done to put this specification into the context
     * of the current state of the current football match.
     * @param instant The current instant
     * @return The next state
     */
    private State processCurrentInstant(Instant instant) {
        
        // System.out.println("Processing instant: " + instant);
        
        // Keep track of ball possession
        matchReport.getCurrentState().getTeam().getStats().addPossession();
                
        if (matchReport.getCurrentState().getX().equals(State.X.CORNER_KICK)) {
            matchReport.getCurrentState().getTeam().getStats().addCornerKick();
            matchRewind.addSignal(new CornerKick(matchReport.getTimer(), matchReport.getCurrentState().getTeam().getName()));
        }
        
        boolean pressed = false;
        
        double ownPace = 0;
        double oppPace = 0;
        
        if (matchReport.getCurrentState().getY() == State.Y.DEFENCE) {
            ownPace = matchReport.getCurrentState().getTeam().getAverageFromAttribute(Constants.MIDFIELDER, "Pace");
            oppPace = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.MIDFIELDER, "Pace");
        } else if (matchReport.getCurrentState().getY() == State.Y.CENTRE) {
            ownPace = matchReport.getCurrentState().getTeam().getAverageFromAttribute(Constants.MIDFIELDER, "Pace");
            oppPace = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.MIDFIELDER, "Pace");
        } else if (matchReport.getCurrentState().getY() == State.Y.ATTACK) {
            ownPace = matchReport.getCurrentState().getTeam().getAverageFromAttribute(Constants.FORWARD, "Pace");
            oppPace = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.DEFENDER, "Pace");
        }
        
        double total = ownPace + oppPace;
        
        double pressing = rnd.nextDouble() * total;
        
        if (pressing < ownPace) {
            pressed = false;
        } else {
            pressed = true;
        }
        
        matchReport.getCurrentEvent().setAction(instant.Action);
        
        if (goalScoringAction(instant.Action)) {
            
            // System.out.println("Goal scoring action");
            
            return assessGSOpportunity(instant);
            
        } 
        else if (instant.Action == Constants.Pass) {
            
            // System.out.println("Processing passing");
            
            State foulState = checkFoul(instant);
            
            if (foulState != null)  {
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().pass.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().pass.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
            
            double currentPassing = 0;
            double currentTarget = 0;
            double currentDefence;
            
            switch(instant.Y) {
            case Constants.DEFENCE:
               
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                currentTarget = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentPassing *= currentTarget;
                
                break;
            case Constants.CENTRE:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                currentTarget = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentPassing *= currentTarget;
                
                break;
            case Constants.ATTACK:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                currentTarget = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentPassing *= currentTarget;
                
                break;
            default:
                currentPassing = 1;
            }
            
            double currentInterception = 0;
            
            switch(instant.Y) {
            case Constants.DEFENCE:
                currentInterception = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ZonalMarking");
                break;
            case Constants.CENTRE:
                currentInterception = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ZonalMarking");
                break;
            case Constants.ATTACK:
                currentInterception = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ZonalMarking");
                break;
            default:
                currentInterception = 1;
            }
            
            double currentMMLost = 0;
            
            switch(instant.Y) {
            case Constants.DEFENCE:
                currentMMLost = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ManMarking");
                break;
            case Constants.CENTRE:
                currentMMLost = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ManMarking");
                break;
            case Constants.ATTACK:
                currentMMLost = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ManMarking");
                break;
            default:
                currentMMLost = 1;
            }
            
            double successPerc = RealWorldMapping.SUCC_Pass;
            
            double [] passOutcomes = {successPerc * currentPassing, currentInterception * RealWorldMapping.PassInterception, 
                    currentMMLost * RealWorldMapping.PassMarking};
            
            int passOutcome = getActionResult(passOutcomes);
            
            // System.out.println("Pass outcome: " + passOutcome);
            
            switch(passOutcome) {
            case 0: // success
                // System.out.println("Successful Pass");
                matchReport.getCurrentState().getPlayer().getStats().pass.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
            case 1:
                // System.out.println("Pass Interception");
                matchReport.getCurrentState().getPlayer().getStats().pass.registerFailure(matchReport.getTimer());
                
                State nextIntState = 
                    simulatePossessionChangeCausal(instant, Constants.BPC_PassInterception, OOConstants.PASS_INTERCEPTION, pressed);
                
                if (nextIntState.getTeam() != matchReport.getCurrentState().getTeam()) {
                    nextIntState.getPlayer().getStats().interceptions.addOccurence(matchReport.getTimer());
                }
                
                return nextIntState;
            case 2:
                // System.out.println("Man marking Pass");
                matchReport.getCurrentState().getPlayer().getStats().pass.registerFailure(matchReport.getTimer());
                
                State nextChState = 
                    simulatePossessionChangeCausal(instant, Constants.BPC_ManChallengeLost, OOConstants.MAN_CHALLENGE_LOST, pressed);
                
                if (nextChState.getTeam() != matchReport.getCurrentState().getTeam()) {
                    nextChState.getPlayer().getStats().personalChallenges.addOccurence(matchReport.getTimer());
                }
                
                return nextChState;
            }
                
        } else if (instant.Action == Constants.GkLongPass) {
            
            State foulState = checkFoul(instant);
            if (foulState != null) {
                if (foulState.getTeam() == matchReport.getCurrentState().getTeam()) {
                    matchReport.getCurrentState().getTeam().getGK().getStats().gkLongPass.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getTeam().getGK().getStats().gkLongPass.registerFailure(matchReport.getTimer());
                }
                return foulState;
            }
            
            double currentPassing = matchReport.getCurrentState().getTeam().getGK().getSkill("Passing");
            
            double successPerc = RealWorldMapping.SUCC_GkLongPass;
            
            double [] gkLongPassOutcomes = {successPerc * currentPassing, RealWorldMapping.UF_GkLongPass * RealWorldMapping.avgSkill};
            
            int gkLongPassOutcome = getActionResult(gkLongPassOutcomes);
            
            switch (gkLongPassOutcome) {
            case 0:
                // System.out.println("Gk Long Pass Success: " + matchReport.getCurrentState().getTeam().getGK().getFamilyName());
                matchReport.getCurrentState().getTeam().getGK().getStats().gkLongPass.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                // System.out.println("Gk Long Pass Failure: " + matchReport.getCurrentState().getTeam().getGK().getFamilyName());
                matchReport.getCurrentState().getTeam().getGK().getStats().gkLongPass.registerFailure(matchReport.getTimer());
                return simulateBallPossessionChange(instant, pressed);
                
            }
            
        } else if (instant.Action == Constants.LongPass) {
            
            State foulState = checkFoul(instant);
            if (foulState != null)  {
                
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().longPass.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().longPass.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
        
            double currentPassing = 0;
            double tacticsFilter = 0;
            
            switch(instant.Y) {
            case Constants.DEFENCE:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentPassing *= tacticsFilter;
                
                break;
            case Constants.CENTRE:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentPassing *= tacticsFilter;
                
                break;
            case Constants.ATTACK:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentPassing *= tacticsFilter;
                
                break;
            default:
                currentPassing = 1;
            }
            
            double successPerc = RealWorldMapping.SUCC_LongPass;
            
            double [] longPassOutcomes = {successPerc * currentPassing, RealWorldMapping.UF_LongPass * RealWorldMapping.avgSkill};
            
            int longPassOutcome = getActionResult(longPassOutcomes);
            
            switch (longPassOutcome) {
            case 0:
                matchReport.getCurrentState().getPlayer().getStats().longPass.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                matchReport.getCurrentState().getPlayer().getStats().longPass.registerFailure(matchReport.getTimer());
                return simulateBallPossessionChange(instant, pressed);
            }
            
        } else if (instant.Action == Constants.ForwardPass) {
            
            // System.out.println("Initial instant: " + instant);
            
            State foulState = checkFoul(instant);
            if (foulState != null) {
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().forwardPass.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().forwardPass.registerFailure(matchReport.getTimer());
                }
                return foulState;
            }
            
            double tacticsFilter = 0;
            
            double currentPassing = 0;
            double currentGk = 0;
            double currentInterception = 0;
            double currentMarking = 0;
            
            double defGk = 0;
            double defInterception = 0;
            double defMarking = 0;
            
            switch(instant.Y) {
            case Constants.DEFENCE:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentPassing *= tacticsFilter;
                
                currentGk = 0;
                
                currentInterception = 
                    RealWorldMapping.ForwardPassInterception + (RealWorldMapping.ForwardPassGkCentre - RealWorldMapping.ForwardPassGk) / 2;
                
                defInterception = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ZonalMarking");
                
                currentMarking = RealWorldMapping.ForwardPassMarking + (RealWorldMapping.ForwardPassGkCentre - RealWorldMapping.ForwardPassGk) / 2;
                
                defMarking = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ManMarking");
                
                break;
            case Constants.CENTRE:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentPassing *= tacticsFilter;
                
                currentGk = RealWorldMapping.ForwardPassGkCentre;
                
                defGk = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.GK, "RushingOut");
                
                currentInterception = RealWorldMapping.ForwardPassInterception + 
                    (RealWorldMapping.ForwardPassGk - RealWorldMapping.ForwardPassGkCentre) / 2;
                
                defInterception = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ZonalMarking");
                
                currentMarking = RealWorldMapping.ForwardPassMarking + (RealWorldMapping.ForwardPassGk - RealWorldMapping.ForwardPassGkCentre) / 2;
                
                defMarking = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ManMarking");
                
                break;
            case Constants.ATTACK:
                
                currentPassing = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentPassing *= tacticsFilter;
                
                currentGk = 0;
                
                currentInterception = 
                    RealWorldMapping.ForwardPassInterception + (RealWorldMapping.ForwardPassGkCentre - RealWorldMapping.ForwardPassGk) / 2;
                
                currentMarking = RealWorldMapping.ForwardPassMarking + (RealWorldMapping.ForwardPassGkCentre - RealWorldMapping.ForwardPassGk) / 2;
                
                defInterception = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ZonalMarking");
                
                defMarking = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ManMarking");
                
                break;
                default:
                    currentPassing = 1; 
            }
            
            double successPerc = RealWorldMapping.SUCC_ForwardPass;
            
            double [] forwardPassOutcomes = {successPerc * currentPassing, RealWorldMapping.UF_LongPass * RealWorldMapping.avgSkill, 
                    currentGk * defGk, currentInterception * defInterception, currentMarking * defMarking};
           
            int forwardPassOutcome = getActionResult(forwardPassOutcomes);
            
            switch (forwardPassOutcome) {
            case 0:
                // System.out.println("Forward pass success");
                matchReport.getCurrentState().getPlayer().getStats().forwardPass.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                // System.out.println("Unforced forward pass to opposition");
                matchReport.getCurrentState().getPlayer().getStats().forwardPass.registerFailure(matchReport.getTimer());
                return simulatePossessionChangeCausal(instant, Constants.BPC_Normal, OOConstants.UNFORCED_POSSESSION_CHANGE, pressed);
                
            case 2:
                // System.out.println("Ball goes to Gk from forward pass: " + toggleTeam(matchReport.getCurrentState().getTeam()).getGK());
                matchReport.getCurrentState().getPlayer().getStats().forwardPass.registerFailure(matchReport.getTimer());
                toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().interceptions.addOccurence(matchReport.getTimer());
                return simulatePossessionChangeCausal(instant, Constants.BPC_Gk, OOConstants.GK_INTERCEPTION, pressed);
                
            case 3:
                
                matchReport.getCurrentState().getPlayer().getStats().forwardPass.registerFailure(matchReport.getTimer());
                
                State nextState = simulatePossessionChangeCausal(instant, Constants.BPC_PassInterception, OOConstants.PASS_INTERCEPTION, pressed);
                
                // Verify that the ball went to the other team
                if (matchReport.getCurrentState().getTeam() != nextState.getTeam()) {
                    nextState.getPlayer().getStats().interceptions.addOccurence(matchReport.getTimer());
                }
                
                return nextState;
                
            case 4:
                // System.out.println("Forward pass challenge");
                
                matchReport.getCurrentState().getPlayer().getStats().forwardPass.registerFailure(matchReport.getTimer());
                
                State nextChState = simulatePossessionChangeCausal(instant, Constants.BPC_ManChallengeLost, OOConstants.MAN_CHALLENGE_LOST, pressed);
                
                if (matchReport.getCurrentState().getTeam() != nextChState.getTeam()) {
                    nextChState.getPlayer().getStats().personalChallenges.addOccurence(matchReport.getTimer());
                }
                
                return nextChState;
                
            }
               
        } else if (instant.Action == Constants.Combination) {
            
            State foulState = checkFoul(instant);
            if (foulState != null) return foulState;
            
            double currentSkill = 0;
            double tacticsFilter = 0;
            
            switch (instant.Y) {
            case Constants.DEFENCE:
                
                currentSkill = matchReport.getCurrentState().getTeam().getAverageFromAction(Constants.MIDFIELDER, Constants.Combination);
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentSkill *= tacticsFilter;
                
                break;
            case Constants.CENTRE:
                
                currentSkill = matchReport.getCurrentState().getTeam().getAverageFromAction(Constants.MIDFIELDER, Constants.Combination);
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentSkill *= tacticsFilter;
                
                break;
            case Constants.ATTACK:
                
                currentSkill = matchReport.getCurrentState().getTeam().getAverageFromAction(Constants.FORWARD, Constants.Combination);
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentSkill *= tacticsFilter;
                
                break;
            }
            
            double successPerc = RealWorldMapping.SUCC_Combination;
            
            double [] combinationOutcomes = {successPerc * currentSkill, RealWorldMapping.UF_Combination * RealWorldMapping.avgSkill};
            
            int combinationOutcome = getActionResult(combinationOutcomes);
            
            switch (combinationOutcome) {
            case 0:
                return simulateSuccess(instant, pressed);
                
            case 1:
                return simulateBallPossessionChange(instant, pressed);
                
            }
            
        } else if (instant.Action == Constants.FlankPass) {
            
            State foulState = checkFoul(instant);
            if (foulState != null) {
                
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().flankPass.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().flankPass.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
          
            double currentSkill = 0;
            double tacticsFilter = 0;
            
            switch (instant.Y) {
            case Constants.DEFENCE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = 
                    matchReport.getCurrentState().getTeam().getTargetStrength(Constants.DEFENDER) * RealWorldMapping.FlankPass_D2D +
                    matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER) * RealWorldMapping.FlankPass_D2C;
                
                currentSkill *= tacticsFilter;
                
                break;
            case Constants.CENTRE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = 
                    matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER) * RealWorldMapping.FlankPass_C2C +
                    matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD) * RealWorldMapping.FlankPass_C2A;
                
                currentSkill *= tacticsFilter;
                
                break;
            case Constants.ATTACK:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentSkill *= tacticsFilter;
                
                break;
            }
            
            double successPerc = RealWorldMapping.SUCC_FlankPass;
            
            double [] flankPassOutcomes = {successPerc * currentSkill, RealWorldMapping.UF_FlankPass * RealWorldMapping.avgSkill};
            
            int flankPassOutcome = getActionResult(flankPassOutcomes);
            
            switch (flankPassOutcome) {
            case 0:
                matchReport.getCurrentState().getPlayer().getStats().flankPass.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                matchReport.getCurrentState().getPlayer().getStats().flankPass.registerFailure(matchReport.getTimer());
                return simulateBallPossessionChange(instant, pressed);
                
            }
            
        } else if (instant.Action == Constants.RunBall) {
            
            State foulState = checkFoul(instant);
            if (foulState != null) {
                
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().runBall.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().runBall.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
            
            double currentSkill = 0;
            double tacticsFilter = 0;
            double defManMarking = 0;
            
            switch (instant.Y) {
            case Constants.DEFENCE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("BallControl");
                tacticsFilter = 
                    matchReport.getCurrentState().getTeam().getTargetStrength(Constants.DEFENDER) * RealWorldMapping.RunBall_D2D + 
                    matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER) * RealWorldMapping.RunBall_D2C;
                
                currentSkill *= tacticsFilter;
                
                defManMarking = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ManMarking");
                
                break;
            case Constants.CENTRE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("BallControl");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentSkill *= tacticsFilter;
                
                defManMarking = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ManMarking");
                
                break;
            case Constants.ATTACK:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("BallControl");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentSkill *= tacticsFilter;
                
                defManMarking = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ManMarking");   
            }
            
            double successPerc = RealWorldMapping.SUCC_RunBall;
            
            double [] runBallOutcomes = {successPerc * currentSkill, defManMarking * RealWorldMapping.RunBallMarking};
            
            int runBallOutcome = getActionResult(runBallOutcomes);
            
            switch (runBallOutcome) { // Since only one opponent outcome is possible, we don't need to specify ball possession change
            case 0:
                matchReport.getCurrentState().getPlayer().getStats().runBall.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                matchReport.getCurrentState().getPlayer().getStats().runBall.registerFailure(matchReport.getTimer());
                return simulateBallPossessionChange(instant, pressed);
            }      
            
        } else if (instant.Action == Constants.LongThrowIn) {
            
            State foulState = checkFoul(instant);
            if (foulState != null) return foulState;
            
            double currentSkill = 0;
            double tacticsFilter = 0;
            
            switch (instant.Y) {
            case Constants.DEFENCE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("ThrowIn");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentSkill *= tacticsFilter;
                
                break;
            case Constants.CENTRE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("ThrowIn");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentSkill *= tacticsFilter;
                
                break;
            case Constants.ATTACK:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("ThrowIn");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentSkill *= tacticsFilter;
                
                break;
            }
            
            double successPerc = RealWorldMapping.SUCC_LongThrowIn;
            
            double [] longThrowInOutcomes = {successPerc * currentSkill, RealWorldMapping.avgSkill * RealWorldMapping.UF_LongThrowIn};
            
            int longThrowInOutcome = getActionResult(longThrowInOutcomes);
            
            switch (longThrowInOutcome) {
            case 0:
                return simulateSuccess(instant, pressed);
            case 1:
                return simulateBallPossessionChange(instant, pressed);
            }
            
        } else if (instant.Action == Constants.Dribbling) {
            
            State foulState = checkFoul(instant);
            if (foulState != null)  {
                
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().dribbling.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().dribbling.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
            
            double currentSkill = 0;
            double defSkill = 0;
            
            switch (instant.Y) {
            case Constants.DEFENCE:
            case Constants.CENTRE:
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Dribbling");
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ManMarking");
                break;
            case Constants.ATTACK:
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Dribbling");
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ManMarking");
                break;
            }
            
            double successPerc = RealWorldMapping.SUCC_Dribbling;
            
            double [] dribblingOutcomes = {successPerc * currentSkill, RealWorldMapping.DribblingManMarking * defSkill, 
                    RealWorldMapping.avgSkill * RealWorldMapping.UF_Dribbling};
            
            int dribblingOutcome = getActionResult(dribblingOutcomes);
            
            switch (dribblingOutcome) {
            case 0:
                matchReport.getCurrentState().getPlayer().getStats().dribbling.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                
                // System.out.println("Man marking from Dribbling");
                
                matchReport.getCurrentState().getPlayer().getStats().dribbling.registerFailure(matchReport.getTimer());
                
                State nextState = simulatePossessionChangeCausal(instant, Constants.BPC_ManChallengeLost, OOConstants.MAN_CHALLENGE_LOST, pressed);
                
                if (nextState.getTeam() != matchReport.getCurrentState().getTeam()) {
                    nextState.getPlayer().getStats().personalChallenges.addOccurence(matchReport.getTimer());
                }
                
                return nextState;
                
            case 2:
                // System.out.println("Opposition from Dribbling");
                matchReport.getCurrentState().getPlayer().getStats().dribbling.registerFailure(matchReport.getTimer());
                return simulatePossessionChangeCausal(instant, Constants.BPC_Normal, OOConstants.UNFORCED_POSSESSION_CHANGE, pressed);
            }
            
        } else if (instant.Action == Constants.LongFlankPass) {
            
            State foulState = checkFoul(instant);
            if (foulState != null)  {
                
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().longFlankPass.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().longFlankPass.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
            
            double currentSkill = 0;
            double defSkill = 0;
            
            double tacticsFilter = 0;
            
            switch (instant.Y) {
            case Constants.DEFENCE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.MIDFIELDER);
                
                currentSkill *= tacticsFilter;
                
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ZonalMarking");
                
                break;
            case Constants.CENTRE:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                currentSkill *= tacticsFilter;
                
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ZonalMarking");
                
                break;
            case Constants.ATTACK:
                
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Passing");
                tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
                
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ZonalMarking");
                
                break;
            }
            
            double successPerc = RealWorldMapping.SUCC_LongFlankPass;
            
            double [] longFlankPassOutcomes = {successPerc * currentSkill, RealWorldMapping.LongFlankPassInterception * defSkill,
                                                    RealWorldMapping.avgSkill * RealWorldMapping.UF_LongFlankPass};
            
            int longFlankPassOutcome = getActionResult(longFlankPassOutcomes);
            
            switch (longFlankPassOutcome) {
            case 0:
                matchReport.getCurrentState().getPlayer().getStats().longFlankPass.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                // System.out.println("Long flank pass interception");
                matchReport.getCurrentState().getPlayer().getStats().longFlankPass.registerFailure(matchReport.getTimer());
                
                State nextState = simulatePossessionChangeCausal(instant, Constants.BPC_PassInterception, OOConstants.PASS_INTERCEPTION, pressed);
                
                if (nextState.getTeam() != matchReport.getCurrentState().getTeam()) {
                    nextState.getPlayer().getStats().interceptions.addOccurence(matchReport.getTimer());
                }
                
                return nextState;
                
            case 2:
                // System.out.println("Long flank pass opposition");
                matchReport.getCurrentState().getPlayer().getStats().longFlankPass.registerFailure(matchReport.getTimer());
                return simulatePossessionChangeCausal(instant, Constants.BPC_Normal, OOConstants.UNFORCED_POSSESSION_CHANGE, pressed);
                
            }
            
        } else if (instant.Action == Constants.AreaPass) {
            
            State foulState = checkFoul(instant);
            if (foulState != null)  {
                
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().areaPass.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().areaPass.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
            
            double currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Passing");
            double tacticsFilter = matchReport.getCurrentState().getTeam().getTargetStrength(Constants.FORWARD);
            
            currentSkill *= tacticsFilter;
            
            double gkSkill = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.GK, "RushingOut");
            
            double successPerc = RealWorldMapping.SUCC_AreaPass;
            
            double [] areaPassOutcomes = {successPerc * currentSkill, RealWorldMapping.AreaPassGk * gkSkill,
                                            RealWorldMapping.avgSkill * RealWorldMapping.UF_AreaPass};
            
            int areaPassOutcome = getActionResult(areaPassOutcomes);
            
            switch (areaPassOutcome) {
            case 0:
                matchReport.getCurrentState().getPlayer().getStats().areaPass.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
                
            case 1:
                // System.out.println("Area pass to gk: " + toggleTeam(matchReport.getCurrentState().getTeam()).getGK());
                matchReport.getCurrentState().getPlayer().getStats().areaPass.registerFailure(matchReport.getTimer());
                toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().interceptions.addOccurence(matchReport.getTimer());
                return simulatePossessionChangeCausal(instant, Constants.BPC_Gk, OOConstants.GK_INTERCEPTION, pressed);
                
            case 2:
                // System.out.println("Area pass to opposition");
                matchReport.getCurrentState().getPlayer().getStats().areaPass.registerFailure(matchReport.getTimer());
                return simulatePossessionChangeCausal(instant, Constants.BPC_Normal, OOConstants.UNFORCED_POSSESSION_CHANGE, pressed);
                
            } 
            
        } else if (instant.Action == Constants.BallControl) {
            
            // System.out.println("Processing ball control");
            
            State foulState = checkFoul(instant);
            if (foulState != null)  {
                
                if (matchReport.getCurrentState().getTeam() == foulState.getTeam()) {
                    matchReport.getCurrentState().getPlayer().getStats().ballControl.registerSuccess(matchReport.getTimer());
                } else {
                    matchReport.getCurrentState().getPlayer().getStats().ballControl.registerFailure(matchReport.getTimer());
                }
                
                return foulState;
            }
            
            double currentSkill = 0;
            double defSkill = 0;
            
            switch (instant.Y) {
            case Constants.DEFENCE:
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("BallControl");
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.FORWARD, "ManMarking");
                break;
            case Constants.CENTRE:
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("BallControl");
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.MIDFIELDER, "ManMarking");
                break;
            case Constants.ATTACK:
                currentSkill = matchReport.getCurrentState().getPlayer().getSkill("BallControl");
                defSkill = applyTacticsFilter(toggleTeam(matchReport.getCurrentState().getTeam()), Constants.DEFENDER, "ManMarking");
                break;
            }
            
            double successPerc = RealWorldMapping.SUCC_BallControl;
            
            double [] ballControlOutcomes = {successPerc * currentSkill, RealWorldMapping.BallControlManMarking * defSkill};
            
            int ballControlOutcome = getActionResult(ballControlOutcomes);
            
            switch (ballControlOutcome) {
            case 0:
                // System.out.println("Success");
                matchReport.getCurrentState().getPlayer().getStats().ballControl.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
            case 1:
                // System.out.println("Opponent");
                matchReport.getCurrentState().getPlayer().getStats().ballControl.registerFailure(matchReport.getTimer());
                return simulateBallPossessionChange(instant, pressed);                
            }
            
        } else if (instant.Action == Constants.Cross) {
            
            // System.out.println("Cross");
            
            double crossingSkill = matchReport.getCurrentState().getPlayer().getSkill("Crossing");
            double footSkill = -1;
            double currentSkill = 0;
            
            int currentSide = matchReport.getCurrentState().getSide();
            
            if (currentSide == OOConstants.LEFT_SIDE) {
                // System.out.println("Crossing from left side");
                footSkill = matchReport.getCurrentState().getPlayer().getSkill("LeftFoot");
            } else if (currentSide == OOConstants.RIGHT_SIDE) {
                // System.out.println("Crossing from right side");
                footSkill = matchReport.getCurrentState().getPlayer().getSkill("RightFoot");
            }
            
            if (footSkill > 0) {
                currentSkill = (crossingSkill + footSkill) / 2;
            } else {
                currentSkill = crossingSkill;
            }
            
            double goalSkill = (currentSkill + matchReport.getCurrentState().getTeam().getAverageFromAttribute(Constants.FORWARD, "Heading")) / 2;
            double gkSkill = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.GK, "RushingOut");
            double defSkill = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.DEFENDER, "Heading");
            
            double successPerc = RealWorldMapping.SUCC_Cross;
            double goalPerc = RealWorldMapping.CrossGoalScoring;
            
            double [] crossOutcomes = {successPerc * currentSkill, goalPerc * goalSkill, RealWorldMapping.CrossGk * gkSkill, 
                    RealWorldMapping.UF_Cross * defSkill};
            
            int crossOutcome = getActionResult(crossOutcomes);
            
            switch (crossOutcome) {
            case 0:
                // System.out.println("Successful cross");
                matchReport.getCurrentState().getPlayer().getStats().cross.registerSuccess(matchReport.getTimer());
                if (!goalScoringOpportunity(instant) && (instant.outcome instanceof Success || instant.outcome instanceof Challenge)){
                    return processCross(instant);
                } else {
                    return processCross(getMatchingCross(instant, OOConstants.SUCC_CH));
                }
            case 1:
                // System.out.println("Goal scoring opportunity from cross");
                matchReport.getCurrentState().getPlayer().getStats().cross.registerSuccess(matchReport.getTimer());
                if (goalScoringOpportunity(instant)) {
                    return processCross(instant);
                } else {
                    return processCross(getMatchingCross(instant, OOConstants.GOAL_OPPORTUNITY_CROSS));
                }
            case 2:
                // System.out.println("Ball to Gk from cross: " + toggleTeam(matchReport.getCurrentState().getTeam()).getGK());
                matchReport.getCurrentState().getPlayer().getStats().cross.registerFailure(matchReport.getTimer());
                toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().interceptions.addOccurence(matchReport.getTimer());
                if (instant.outcome instanceof Opponent && ((Opponent) instant.outcome).possessionChange == Constants.BPC_Gk) {
                    return processCross(instant);
                } else {
                    return processCross(getMatchingCross(instant, OOConstants.GK_INTERCEPTION));
                }
            case 3:
                // System.out.println("Opposition from cross");
                matchReport.getCurrentState().getPlayer().getStats().cross.registerFailure(matchReport.getTimer());
                if (instant.outcome instanceof Opponent && ((Opponent) instant.outcome).possessionChange == Constants.BPC_Normal) {
                    return processCross(instant);
                } else {
                    return processCross(getMatchingCross(instant, OOConstants.OPPOSITION));
                }
            }
            
        } else if (instant.Action == Constants.LowCross) {
            
            // System.out.println("Low cross");
            
            double currentSkill = matchReport.getCurrentState().getPlayer().getSkill("Crossing");
            double defSkill = toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.DEFENDER, "ZonalMarking");
            
            double successPerc = RealWorldMapping.SUCC_LowCross;
            
            double [] lowCrossOutcomes = {successPerc * currentSkill, RealWorldMapping.UF_LowCross * defSkill};
            
            int lowCrossOutcome = getActionResult(lowCrossOutcomes);
            
            switch (lowCrossOutcome) {
            case 0:
                // System.out.println("Successful low cross");
                matchReport.getCurrentState().getPlayer().getStats().lowCross.registerSuccess(matchReport.getTimer());
                return simulateSuccess(instant, pressed);
            case 1:
                // System.out.println("Unsuccesful low cross");
                matchReport.getCurrentState().getPlayer().getStats().lowCross.registerFailure(matchReport.getTimer());
                return simulateBallPossessionChange(instant, pressed);
                
            }
            
        }
        else if (crossingAction(instant.Action)) { // filter crosses that produce goal scoring opportunities
            
            // System.out.println("Crossing action");
            
            /*
            if (instant.Action == Constants.Cross) {
                // System.out.println("That shouldn't have happened");
            }
            */
            
            matchReport.getCurrentState().getPlayer().getStats().cross.registerSuccess(matchReport.getTimer());
            
            return processCross(instant);
            
        } else {
            
            // System.out.println("Executed default action!!!");
            
            State foulState = checkFoul(instant);
            if (foulState != null) 
                return foulState;
            else
                return simulateNextState(instant, true);
            
        }
        return null;
    }
    
    /**
     * Used in calculation of a team's players collective ability
     * @param team The team object
     * @param position The position of the players, e.g. defenders or midfielders
     * @param attribute The attribute on which the collective ability calculation is based: e.g. man or zonal marking
     * @return A factor used in the calculation of the outcome regarding the involved players (by position) and the relevant attribute
     */
    private double applyTacticsFilter(Team team, int position, String attribute) {
        
        double currentDefence;
        double currentSkill;
        
        currentSkill = team.getAverageFromAttribute(position, attribute);
        currentDefence = team.getTargetStrength(position);
        currentSkill *= currentDefence;
        return currentSkill; 
    }
    
    /**
     * Simulates a change in ball possession. If the current instant has an outcome which is compatible with the specification of this function's
     * parameters, the same instant is used, otherwise a compatible instant is searched for and its outcome State is returned 
     * @param instant The current instant
     * @param bpChange The classification of the ball possession change. Used in the matching of the current instant
     * @param OO The classification of the ball possession change. Used in search of a compatible instant
     * @param pressed True if the ball holder was under pressure
     * @return The next state
     */
    private State simulatePossessionChangeCausal(Instant instant, byte bpChange, int OO, boolean pressed) {
        
        boolean matchPressure = pressureMatched(instant, pressed);
        
        if ((instant.outcome instanceof Opponent) && (((Opponent) instant.outcome).possessionChange == bpChange) && matchPressure) {
            // System.out.println("Same instant");
            return simulateNextState(instant, true);
        } else {
            // System.out.println("Different instant");
            return simulateNextState(getMatchingInstant(instant, OO, pressed), true);
        }
    }

    /**
     * Simulates a change in ball possession. If the current instant has an outcome which is compatible with the specification of this function's
     * parameters, the same instant is used, otherwise a compatible instant is searched for and its outcome State is returned
     * @param instant The current instant
     * @param pressed True if the ball holder was under pressure
     * @return The next state
     */
    private State simulateBallPossessionChange(Instant instant, boolean pressed) {
        
        // System.out.println("Ball possession change");
        
        if (instant.outcome instanceof Opponent && pressureMatched(instant, pressed)) {
            return simulateNextState(instant, true);
        } else {
            return simulateNextState(getMatchingInstant(instant, OOConstants.OPPOSITION, pressed), true);
        }
    }

    /**
     * Simulate the success of the current instant's action. If the current instant has a successful outcome this outcome is returned; 
     * if not, a compatible instant's outcome is returned
     * @param instant  The current instant
     * @param pressed True if the ball holder is under pressure
     * @return The next state
     */
    private State simulateSuccess(Instant instant, boolean pressed) {
        
        boolean matchPressure = pressureMatched(instant, pressed);
        
        if (((instant.outcome instanceof Success) || (instant.outcome instanceof Challenge)) && matchPressure) {
            // System.out.println("State matched");
            return simulateNextState(instant, true);
        } else { // Attention: must take into account challenge as well -> use a custom constant as parameter to denote that both success and challenge are looked for
            // System.out.println("State not matched");
            return simulateNextState(getMatchingInstant(instant, OOConstants.SUCC_CH, pressed), true);
        }
    }

    /**
     * Utility method for checking whether an instant is compatible with whether the ball holder is pressed or not
     * @param instant The instant checked
     * @param pressed The 'pressure status' checked
     * @return True if the instant matches the 'pressure status'
     */
    private boolean pressureMatched(Instant instant, boolean pressed) {
        boolean matchPressure = false;
        if ((pressed && (instant.Pressure == Constants.UNDER || instant.Pressure == Constants.AVOID)) || 
                (!pressed && instant.Pressure == Constants.CLEAR)) {
            matchPressure = true;
        } else {
            matchPressure = false;
        }
        return matchPressure;
    }

    /**
     * Assesses a goal scoring opportunity
     * @param instant The current instant
     * @return The calculated outcome of the assessment
     */
    private State assessGSOpportunity(Instant instant) {
        
        double finishing = matchReport.getCurrentState().getTeam().getFinishing() * 
            RealWorldMapping.avgSkill / matchReport.getCurrentState().getPlayer().getSkill("Shooting");
        
        double shooting = matchReport.getCurrentState().getTeam().getShooting() * 
            RealWorldMapping.avgSkill / matchReport.getCurrentState().getPlayer().getSkill("Shooting");
        
        double goalkeeping = 
            RealWorldMapping.avgSkill / toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.GK, "Handling");
        
        // double [] abilityArray = {1d, matchReport.getCurrentState().getTeam().getFinishing(), 
        //         matchReport.getCurrentState().getTeam().getShooting()};
        
        double [] abilityArray = {goalkeeping, finishing, shooting};
        
        int outcome = -1;
        switch(instant.Action) {
        case Constants.LongShot:
            outcome = getOutcome(RealWorldMapping.LongShotStats, abilityArray);
            break;
        case Constants.AreaShot:
            outcome = getOutcome(RealWorldMapping.AreaShotStats, abilityArray); 
        }
        switch(outcome) {
        case RealWorldMapping.GOAL:
            
            matchReport.getCurrentState().getPlayer().getStats().shots.registerSuccess(matchReport.getTimer());
            toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().concedings.addOccurence(matchReport.getTimer());
            
            return processGoal(instant.Action, false);
            
        case RealWorldMapping.SHOT_ON:
            
            matchReport.getCurrentState().getPlayer().getStats().shots.registerFailure(matchReport.getTimer());
            toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().saves.addOccurence(matchReport.getTimer());
            
            return processShotOn(instant.Action, false);
            
        case RealWorldMapping.SHOT_OFF:
            matchReport.getCurrentState().getPlayer().getStats().shots.registerFailure(matchReport.getTimer());
            return processShotOff(instant.Action, false);
        }
        return null;
    }

    /**
     * Process a cross
     * @param instant The current instant
     * @return The next state
     */
    private State processCross(Instant instant) {
        
        if (goalScoringOpportunity(instant)) {
            // System.out.println("Goal scoring opportunity");
            return assessGSOpportunityAir(instant);
        } else {
            State nextState = simulateNextState(instant, true);
            return nextState;
        }
        
    }

    /**
     * Assess a goal scoring opportunity in the 'air', i.e. through a header
     * @param instant The current instant
     * @return The next state
     */
    private State assessGSOpportunityAir(Instant instant) {
        
        // Player having the opportunity should not be the same with the one crossing the ball
        // System.out.println("Cross by: " + matchReport.getCurrentState().getPlayer().getFamilyName());
        
        // Save crosser
        matchReport.getCurrentState().setCrosser(matchReport.getCurrentState().getPlayer());
        
        gameplay.Player airGoalScorer;
        
        do {
            airGoalScorer = matchReport.getCurrentState().getTeam().getGoalScorer();
        } while (airGoalScorer == matchReport.getCurrentState().getPlayer());
        
        matchReport.getCurrentState().setPlayer(airGoalScorer);
        
        double headerOn = matchReport.getCurrentState().getTeam().getFinishing() * 
            RealWorldMapping.avgSkill / airGoalScorer.getSkill("Heading");
        
        double headerOff = matchReport.getCurrentState().getTeam().getShooting() *
            RealWorldMapping.avgSkill / airGoalScorer.getSkill("Heading");
        
        double goalkeeping = 
            RealWorldMapping.avgSkill / toggleTeam(matchReport.getCurrentState().getTeam()).getAverageFromAttribute(Constants.GK, "Handling");
        
        double [] abilityArray = {goalkeeping, headerOn, headerOff};
        
        int outcome = getOutcome(RealWorldMapping.CrossStats, abilityArray);
        switch(outcome) {
        case RealWorldMapping.GOAL:
            matchReport.getCurrentState().getPlayer().getStats().headingsOnTarget.registerSuccess(matchReport.getTimer());
            toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().concedings.addOccurence(matchReport.getTimer());
            return processGoal(instant.Action, true);
        case RealWorldMapping.SHOT_ON:
            matchReport.getCurrentState().getPlayer().getStats().headingsOnTarget.registerFailure(matchReport.getTimer());
            toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().saves.addOccurence(matchReport.getTimer());
            return processShotOn(instant.Action, true);
        case RealWorldMapping.SHOT_OFF:
            matchReport.getCurrentState().getPlayer().getStats().headingsOnTarget.registerFailure(matchReport.getTimer());
            return processShotOff(instant.Action, true);
        }
        return null;
    }
     
    /**
     * Utility function for checking whether the current instant has a foul as an outcome
     * @param instant The current instant
     * @return The outcome state if it is a foul, null otherwise
     */
    private State checkFoul(Instant instant) {
        byte challengeEnding = -1;
        if (instant.outcome instanceof Challenge) {
            
            // System.out.println("Challenge");
            
            challengeEnding = ((Challenge) instant.outcome).challengeEnding;
        }
        if ((isResultFoul(instant.outcome.condition) || 
            isResultFoul(challengeEnding)) &&
            possessionKept(instant)) {
            
            // System.out.println("Defensive foul");
            
            // check for penalty kick
            if (instant.Y == Constants.ATTACK || instant.Y == Constants.CENTRE) {
                double isPenalty = rnd.nextDouble();
                // System.out.println("Is penalty: " + isPenalty);
                if (isPenalty < RealWorldMapping.PENALTY_AWARD_FACTOR) {
                    // System.out.println("***PENALTY AWARDED***");
                    return processPenalty(matchReport.getCurrentState());
                } else {
                    return processFreeKick(instant, false);
                }
            } else {
                return processFreeKick(instant, false);
            }
        } else if (isResultOffensiveFoul(instant)) {
            
            // System.out.println("Offensive foul");
            
            // just keep track of "offensive foul"
            return processFreeKick(instant, true);
        } else { // 'normal flow' 
            // System.out.println("Normal");
            return null;
        }
    }

    /**
     * Process a free kick event
     * @param instant The current instant
     * @param offensive True if the current free kick is an "offensive" one
     * @return The next state
     */
    private State processFreeKick(Instant instant, boolean offensive) {
        
        Team reportTeam = null;
        
        if (offensive) {
            reportTeam = toggleTeam(matchReport.getCurrentState().getTeam());
        } else {
            reportTeam = matchReport.getCurrentState().getTeam();
        }
        
        reportTeam.getStats().addFreeKick();
        matchReport.getCurrentEvent().setSpecial(Report.Foul);
        
        matchRewind.addSignal(new FreeKick(matchReport.getTimer(), reportTeam.getName()));
        
        // Simulate foul earned
        
        if (instant.X != Constants.THROW_IN)
            instant.outcomeState.X = instant.X;
        else
            instant.outcomeState.X = Constants.FLANK;
            
        instant.outcomeState.Y = instant.Y;
        instant.outcomeState.Pressure = Constants.CLEAR;
        
        // System.out.println("Simulating free kick state");
        
        return simulateNextState(instant, false);
    }
    
    /**
     * Simulates the next state based on the binary representation of a sequence matching the current state
     * 
     * @param currentRow A sequence of states. The "cause" state should match the current state
     * @return The next state
     */
    
    /**
     * Used in the restriction of the available actions to each player by its position in the field. Essentially the distinction is between a 
     * goalkeeper and an outfield player
     * @param action The action checked
     * @param position The player's position
     * @return True if the action is compatible with the position
     */
    private boolean actionAllowed(byte action, int position) {
        
        if (position == Constants.GK) {
            if (action == Constants.GkLongPass ||
                action == Constants.Pass ||
                action == Constants.FlankPass ||
                action == Constants.KickAway) 
                    {return true;}
            else {return false;}
        } else {
            if (action == Constants.GkLongPass) {
                return false;
            } else {
                return true;
            }
        }
        
    }
    
    /**
     * Simulation of the next state based on the current instant
     * @param currentInstant
     * @param simulateFouls True if we need to check for fouls. Used for avoiding infinite recursion when we actually simulate a foul
     * @return The next state
     */
    private State simulateNextState(Instant currentInstant, boolean simulateFouls) {
        
        // System.out.println("Simulating next state...");
        
        if (simulateFouls) {
            State foulState = checkFoul(currentInstant);
            if (foulState != null) return foulState;
        }
                
        if (currentInstant.outcome.condition > 0) {
            if (currentInstant.outcome.condition == Constants.RES_Offside) {
                return processOffside();
            } else if (currentInstant.outcome.condition == Constants.RES_GoalKick) {
                return processGoalKick();
            } else if (currentInstant.outcome.condition == Constants.RES_ThrowIn) {
                // System.out.println("Processing throw in");
                return processThrowIn(currentInstant);
            } 
        }
        
        State nextState = null;
        
        if (currentInstant.outcome instanceof Success) {
            
            // System.out.println("Processing success: " + currentInstant);
            
            int playerPossession = OOConstants.PLAYER_POSSESSION_IRRELEVANT;
            
            Team outcomeTeam = matchReport.getCurrentState().getTeam();
             
            nextState = new State(outcomeTeam, State.X.getNativeValue(currentInstant.outcomeState.X), 
                     State.Y.getNativeValue(currentInstant.outcomeState.Y), State.Pressure.getNativeValue(currentInstant.outcomeState.Pressure));
            
            // System.out.println("Resulting state: " + nextState);
            
            if (nextState.inFlank()) {
                if (matchReport.getCurrentState().inFlank()) {
                    if (currentInstant.Action != Constants.LongOppositeFlankPass) {
                        // System.out.println("Success: same side -> " + matchReport.getCurrentState().getSide());
                        nextState.setSide(matchReport.getCurrentState().getSide());
                        // System.out.println("After success (same side): " + nextState.getSide());
                    } else {
                        // System.out.println("Success: opposite side -> " + matchReport.getCurrentState().getSide());
                        nextState.changeSide(matchReport.getCurrentState().getSide());
                        // System.out.println("After success (opposite side) " + nextState.getSide());
                        
                    }
                } else {
                    nextState.setRandomSide();
                }
            }
            
            if (currentInstant.Action == Constants.MoveForward ||
                currentInstant.Action == Constants.BallControl ||
                currentInstant.Action == Constants.RunBall ||
                currentInstant.Action == Constants.HighDribble ||
                currentInstant.Action == Constants.MoveBack || 
                currentInstant.Action == Constants.RunBallFlank ||
                currentInstant.Action == Constants.CirculationDelay ||
                currentInstant.Action == Constants.MoveFlankForward) {
                
                playerPossession = OOConstants.PLAYER_POSSESSION_SAME;
                
            } else if (currentInstant.Action == Constants.LongPass ||
                       currentInstant.Action == Constants.BackPass ||
                       currentInstant.Action == Constants.HighPass ||
                       currentInstant.Action == Constants.ForwardPass ||
                       currentInstant.Action == Constants.Pass ||
                       currentInstant.Action == Constants.FlankPass ||
                       currentInstant.Action == Constants.LongThrowIn ||
                       currentInstant.Action == Constants.EasyPass ||
                       currentInstant.Action == Constants.ForwardCirculation ||
                       currentInstant.Action == Constants.LongFlankPass ||
                       currentInstant.Action == Constants.FirstTouchPass ||
                       currentInstant.Action == Constants.LowCross ||
                       currentInstant.Action == Constants.LowAreaCross ||
                       currentInstant.Action == Constants.LongAreaPass ||
                       currentInstant.Action == Constants.FlankForwardPass ||
                       currentInstant.Action == Constants.AreaPass ||
                       currentInstant.Action == Constants.BackThrowIn ||
                       currentInstant.Action == Constants.LongOppositeFlankPass ||
                       currentInstant.Action == Constants.LongFlankForwardPass ||
                       currentInstant.Action == Constants.KickAway ||
                       currentInstant.Action == Constants.LooseForwardPass ||
                       currentInstant.Action == Constants.ThrowInPass ||
                       currentInstant.Action == Constants.HighFlankPass ||
                       currentInstant.Action == Constants.HighAreaPass ||
                       currentInstant.Action == Constants.Cross ||
                       currentInstant.Action == Constants.LowCross ||
                       currentInstant.Action == Constants.LowAreaCross ||
                       currentInstant.Action == Constants.LongCross) {
                
                       playerPossession = OOConstants.PLAYER_POSSESSION_DIFFERENT; 
                
            } else if (currentInstant.Action == Constants.Dribbling) {
                
                if (currentInstant.outcome.condition == Constants.RES_Tackling) {
                    playerPossession = OOConstants.PLAYER_POSSESSION_DIFFERENT;
                } else {
                    playerPossession = OOConstants.PLAYER_POSSESSION_SAME;
                }
            }
             
             switch (currentInstant.outcomeState.Y) {
             case Constants.DEFENCE:
                 if (currentInstant.Action == Constants.GkPass) {
                     nextState.setPlayer(nextState.getTeam().getGK());
                     nextState.setPressure(State.Pressure.FREE);
                 } else {
                     if (playerPossession == OOConstants.PLAYER_POSSESSION_SAME) {
                         nextState.setPlayer(matchReport.getCurrentState().getPlayer()); // keep the ball's possession
                     } else {
                         
                         gameplay.Player candidatePlayer = null;
                         
                         Tactics.TacticPosition currentPosition = null;
                         
                         if (nextState.inFlank()) {
                             if (nextState.getSide() == OOConstants.LEFT_SIDE) {
                                 currentPosition = Tactics.TacticPosition.LEFT;
                             } else if (nextState.getSide() == OOConstants.RIGHT_SIDE) {
                                 currentPosition = Tactics.TacticPosition.RIGHT;
                             }
                         } else {
                             currentPosition = Tactics.TacticPosition.AXIS;
                         }
                         
                         if (playerPossession == OOConstants.PLAYER_POSSESSION_DIFFERENT) {
                             do {
                                 
                                 candidatePlayer = 
                                     nextState.getTeam().getAnyDefensivePlayer(currentPosition, matchReport.getCurrentState().getPlayer());
                                 
                             } while (candidatePlayer == matchReport.getCurrentState().getPlayer());
                                     
                         } else {
                                 candidatePlayer = nextState.getTeam().getAnyDefensivePlayer(currentPosition);
                         }
                         
                         nextState.setPlayer(candidatePlayer);
                         
                     }
                 }
                 break;
             case Constants.CENTRE:
                 if (playerPossession == OOConstants.PLAYER_POSSESSION_SAME) {
                     nextState.setPlayer(matchReport.getCurrentState().getPlayer()); // keep the ball's possession
                 } else {
                     
                     gameplay.Player candidatePlayer = null;
                     
                     Tactics.TacticPosition currentPosition = null;
                     
                     if (nextState.inFlank()) {
                         if (nextState.getSide() == OOConstants.LEFT_SIDE) {
                             currentPosition = Tactics.TacticPosition.LEFT;
                         } else if (nextState.getSide() == OOConstants.RIGHT_SIDE) {
                             currentPosition = Tactics.TacticPosition.RIGHT;
                         }
                     } else {
                         currentPosition = Tactics.TacticPosition.AXIS;
                     }
                     do {
                         candidatePlayer = 
                             nextState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentPosition, matchReport.getCurrentState().getPlayer());
                     } while (candidatePlayer == matchReport.getCurrentState().getPlayer() && 
                             playerPossession == OOConstants.PLAYER_POSSESSION_DIFFERENT);
                      
                     nextState.setPlayer(candidatePlayer);   
                 }
                 break;
             case Constants.ATTACK:
                 
                 if (currentInstant.outcomeState.X == Constants.CORNER_KICK) {
                     nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.MIDFIELDER));
                 } else {
                     
                     if (playerPossession == OOConstants.PLAYER_POSSESSION_SAME) {
                         nextState.setPlayer(matchReport.getCurrentState().getPlayer()); // keep the ball's possession
                     } else {
                         
                         gameplay.Player candidatePlayer = null;
                         
                         Tactics.TacticPosition currentPosition = null;
                         
                         if (nextState.inFlank()) {
                             if (nextState.getSide() == OOConstants.LEFT_SIDE) {
                                 currentPosition = Tactics.TacticPosition.LEFT;
                             } else if (nextState.getSide() == OOConstants.RIGHT_SIDE) {
                                 currentPosition = Tactics.TacticPosition.RIGHT;
                             }
                         } else {
                             currentPosition = Tactics.TacticPosition.AXIS;
                         }
                         
                         candidatePlayer = nextState.getTeam().getPlayerByPosition(Constants.FORWARD, currentPosition, 
                                 matchReport.getCurrentState().getPlayer());
                         
                         nextState.setPlayer(candidatePlayer);
                     }
                 }
                 break;
             }
             
             return nextState;
             
        } else if (currentInstant.outcome instanceof Opponent) {
            
            // System.out.println("Processing opponent: " + currentInstant);
            
            Team outcomeTeam = toggleTeam(matchReport.getCurrentState().getTeam());
            
            nextState = new State(outcomeTeam, State.X.getNativeValue(currentInstant.outcomeState.X), 
                    State.Y.getNativeValue(currentInstant.outcomeState.Y), State.Pressure.getNativeValue(currentInstant.outcomeState.Pressure));
            
            if (nextState.inFlank()) {
                if (matchReport.getCurrentState().inFlank()) {
                    nextState.changeSide(matchReport.getCurrentState().getSide());
                } else {
                    nextState.setRandomSide();
                }
            }
            
            Tactics.TacticPosition currentPosition = null;
            
            if (nextState.inFlank()) {
                if (nextState.getSide() == OOConstants.LEFT_SIDE) {
                    currentPosition = Tactics.TacticPosition.LEFT;
                } else if (nextState.getSide() == OOConstants.RIGHT_SIDE) {
                    currentPosition = Tactics.TacticPosition.RIGHT;
                }
            } else {
                currentPosition = Tactics.TacticPosition.AXIS;
            }
            
            switch (currentInstant.outcomeState.Y) {
            case Constants.DEFENCE:
                nextState.setPlayer(nextState.getTeam().getAnyDefensivePlayer(currentPosition));
                break;
            case Constants.CENTRE:
                nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentPosition));
                break;
            case Constants.ATTACK:
                nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.FORWARD, currentPosition));
                break;
            }
            
            return nextState;
            
        } else if (currentInstant.outcome instanceof Challenge) {
            
            // System.out.println("Processing challenge: " + currentInstant);
            
            Challenge currentChallenge = (Challenge) currentInstant.outcome;
            
            if (currentChallenge.challengeEnding == Constants.RES_Offside) {
                return processOffside(currentInstant);
            } else if (currentChallenge.challengeEnding == Constants.RES_GoalKick) {
                return processGoalKick(currentInstant);
            } else if (currentChallenge.challengeEnding == Constants.RES_ThrowIn) {
                return processThrowIn(currentInstant);
            } else {
                Team outcomeOtherTeam = (currentChallenge.endTeam == Constants.OWN_TEAM)?
                        matchReport.getCurrentState().getTeam():
                        toggleTeam(matchReport.getCurrentState().getTeam());
                        
                nextState = new State(outcomeOtherTeam, State.X.getNativeValue(currentChallenge.endX), 
                        State.Y.getNativeValue(currentChallenge.endY), State.Pressure.getNativeValue(currentInstant.outcomeState.Pressure));
                
                if (nextState.inFlank()) {
                    if (matchReport.getCurrentState().inFlank()) {
                        if (outcomeOtherTeam == matchReport.getCurrentState().getTeam()) {
                            nextState.setSide(matchReport.getCurrentState().getSide());
                        } else {
                            nextState.changeSide(matchReport.getCurrentState().getSide());
                        }
                    } else {
                        nextState.setRandomSide();
                    }
                }
                
                Tactics.TacticPosition currentPosition = null;
                
                if (nextState.inFlank()) {
                    if (nextState.getSide() == OOConstants.LEFT_SIDE) {
                        currentPosition = Tactics.TacticPosition.LEFT;
                    } else if (nextState.getSide() == OOConstants.RIGHT_SIDE) {
                        currentPosition = Tactics.TacticPosition.RIGHT;
                    }
                } else {
                    currentPosition = Tactics.TacticPosition.AXIS;
                }
                
                switch (currentChallenge.endY) {
                case Constants.DEFENCE:
                    nextState.setPlayer(nextState.getTeam().getAnyDefensivePlayer(currentPosition));
                    break;
                case Constants.CENTRE:
                    nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentPosition));
                    break;
                case Constants.ATTACK:
                    if (currentInstant.outcomeState.X == Constants.CORNER_KICK) {
                        nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentPosition));
                    } else {
                        nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.FORWARD, currentPosition));
                    }
                    break;
                }
                        
                return nextState;
            }
        }
        
        // System.out.println("Processing nothing");
        
        return null;
        
    }
     
    /**
     * Process the current instant as an offside
     * @param currentInstant The current instant
     * @return The next state
     */
    private State processOffside(Instant currentInstant) {
        
        // System.out.println("Offside: with parameter");
        
        Team outcomeTeam = getOutcomeTeam(currentInstant);
        toggleTeam(outcomeTeam).getStats().addOffside();
        matchReport.getCurrentEvent().setSpecial(Report.Offside);
        
        matchRewind.addSignal(new Offside(matchReport.getTimer(), toggleTeam(outcomeTeam).getName()));
        
        State nextState = new State(outcomeTeam, State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
        nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.DEFENDER));
        
        return nextState;
    }
    
    /**
     * Process the current instant as a goal kick
     * @param currentInstant The current instant
     * @return The next state
     */
    private State processGoalKick(Instant currentInstant) {
        
        Team outcomeTeam = getOutcomeTeam(currentInstant);
        
        State nextState = new State(outcomeTeam, State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
        nextState.setPlayer(nextState.getTeam().getGK());
        
        return nextState;
    }
    
    /**
     * Process offside creating an "artificial" next state
     * @return The next state
     */
    private State processOffside() {
        
        // System.out.println("Offside: no parameter");
        
        matchReport.getCurrentState().getTeam().getStats().addOffside();
        matchReport.getCurrentEvent().setSpecial(Report.Offside);
        
        matchRewind.addSignal(new Offside(matchReport.getTimer(), matchReport.getCurrentState().getTeam().getName()));
        
        State nextState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
        nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.DEFENDER));
        
        return nextState; 
        
    }
    
    /**
     * Process goal kick creating an "artificial" next state
     * @return The next state
     */
    private State processGoalKick() {
        
        matchReport.getCurrentEvent().setSpecial(Report.GoalKick);
        
        State nextState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
        nextState.setPlayer(nextState.getTeam().getGK());
        
        return nextState;
    }
     
    /**
     * Process current instant as a throw in
     * @param currentInstant The current instant
     * @return The next state
     */
    private State processThrowIn(Instant currentInstant) {
        
        Team outcomeTeam = getOutcomeTeam(currentInstant);
        
        boolean sameTeam = (outcomeTeam == matchReport.getCurrentState().getTeam());
        
        matchReport.getCurrentEvent().setSpecial(Report.ThrowIn);
        
        State nextState = new State(outcomeTeam, State.X.getNativeValue(currentInstant.outcomeState.X), 
                State.Y.getNativeValue(currentInstant.outcomeState.Y), State.Pressure.getNativeValue(currentInstant.outcomeState.Pressure));
        
        if (matchReport.getCurrentState().inFlank()) {
            // System.out.println("Throw in: same side");
            if (sameTeam) {
                // System.out.println("Throw in: same team");
                nextState.setSide(matchReport.getCurrentState().getSide());
            } else {
                // System.out.println("Throw in: opponent");
                nextState.changeSide(matchReport.getCurrentState().getSide());
            }
        } else {
            // System.out.println("Throw in: random side");
            nextState.setRandomSide();
        }
        
        Tactics.TacticPosition currentPosition = null;
        
        if (nextState.getSide() == OOConstants.LEFT_SIDE) {
            currentPosition = Tactics.TacticPosition.LEFT;
        } else if (nextState.getSide() == OOConstants.RIGHT_SIDE) {
            currentPosition = Tactics.TacticPosition.RIGHT;
        }
        
        if (nextState.getY().equals(State.Y.DEFENCE)) {
            nextState.setPlayer(nextState.getTeam().getAnyDefensivePlayer(currentPosition));
        } else {
            nextState.setPlayer(nextState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentPosition));
        }
        
        return nextState;
    }

    /**
     * Utility function to nominate the 'outcome team' based on current instant
     * @param currentInstant The current instant
     * @return The team having the ball possession in the instant's outcome
     */
    private Team getOutcomeTeam(Instant currentInstant) {
        Team outcomeTeam = null;
        if (currentInstant.outcome instanceof Success) {
            outcomeTeam = matchReport.getCurrentState().getTeam();
        } else if (currentInstant.outcome instanceof Opponent) {
            outcomeTeam = toggleTeam(matchReport.getCurrentState().getTeam());
        } else if (currentInstant.outcome instanceof Challenge) {
            outcomeTeam = ((Challenge) currentInstant.outcome).endTeam == Constants.OWN_TEAM ?
                            matchReport.getCurrentState().getTeam():
                                toggleTeam(matchReport.getCurrentState().getTeam());
        }
        return outcomeTeam;
    }
    
    /**
     * Utility function which returns whether the same team has the ball possession throughout the current instant
     * @param currentInstant The current instant
     * @return True if the same team has the ball possession throughout the current instant
     */
    private boolean possessionKept(Instant currentInstant) {
        
        if (currentInstant.outcome instanceof Success) {
            return true;
        } else if (currentInstant.outcome instanceof Opponent) {
            return false;
        } else if (currentInstant.outcome instanceof Challenge) {
            return (((Challenge) currentInstant.outcome).endTeam == Constants.OWN_TEAM);
        }
        
        return false;
        
    }
    
    /**
     * Process a penalty kick
     * 
     * @param state The state before the penalty is taken
     * @return The resulting state after the penalty is taken
     */
    private State processPenalty(State state) {
        
        gameplay.Player penaltyTaker = state.getTeam().getGoalScorer();
        double minute = matchReport.getTime();
        
        reportPenalty(state.getTeam(), penaltyTaker, minute);
        
        matchReport.getCurrentState().getTeam().getStats().addFreeKick(); // Count the penalty as one of the free kicks
        
        State nextState = null;
        
        int penaltyOutcome = getNormalizedOutcome(RealWorldMapping.PenaltyShotStats);
        
        matchRewind.addSignal(new PenaltyKick(matchReport.getTimer(), penaltyTaker.getFamilyName(), penaltyOutcome, 
                matchReport.getCurrentState().getTeam().getName()));
        
        switch (penaltyOutcome) {
        case RealWorldMapping.PENALTY_GOAL:
            
            matchReport.getCurrentState().getTeam().getStats().scoreGoal();
            penaltyTaker.getStats().addGoal(matchReport.getTimer());
            matchReport.getCurrentState().getTeam().registerGoalEvent(new Team.GoalDetails(penaltyTaker, minute, true, matchReport.getTimer()));
            reportPenaltyGoal(penaltyTaker.getFamilyName());
            matchReport.getCurrentEvent().setSpecial(Report.PenaltyGoal);
            
            nextState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
            nextState.setPlayer(nextState.getTeam().getAnyDefensivePlayer());
            
            return nextState; 
            
        case RealWorldMapping.PENALTY_SAVE_DEFENDER:
            
            gameplay.Player defRebounder = toggleTeam(matchReport.getCurrentState().getTeam()).getDefensiveRebounder();
            
            penaltyTaker.getStats().addPenaltiesMissed(matchReport.getTimer());
            toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getStats().addPenaltiesSaved(matchReport.getTimer());
            
            logHighlight(matchReport.getTimer(), toggleTeam(matchReport.getCurrentState().getTeam()).getGK().getFamilyName() + " saves!");
            logHighlight(matchReport.getTimer(), defRebounder.getFamilyName() + " has the ball");
            matchReport.getCurrentState().getTeam().registerMissedPenalty(new Team.MissedPenaltyDetails(penaltyTaker, minute, matchReport.getTimer()));
            matchReport.getCurrentEvent().setSpecial(Report.PenaltyMissed);
            
            nextState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.PRESSED);
            nextState.setPlayer(defRebounder);
            
            return nextState;
            
        case RealWorldMapping.PENALTY_GOAL_KICK:
            
            penaltyTaker.getStats().addPenaltiesMissed(matchReport.getTimer());
            
            logHighlight(matchReport.getTimer(), "But he puts it wide");
            matchReport.getCurrentState().getTeam().registerMissedPenalty(new Team.MissedPenaltyDetails(penaltyTaker, minute, matchReport.getTimer()));
            matchReport.getCurrentEvent().setSpecial(Report.PenaltyMissed);
            
            nextState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
            nextState.setPlayer(nextState.getTeam().getGK());
            
            return nextState;
            
        }
        
        return null; 
    }
    
    /**
     * Logs a highlight about a goal from a penalty being scored
     * @param name The scorer's surname
     */
    private void reportPenaltyGoal(String name) {
        logHighlight(matchReport.getTimer(), name + " scores!");
        logHighlight(matchReport.getTimer(), matchReport.getScoreLine(this.homeTeam, this.awayTeam));
    }

    /**
     * Get the outcome based on a "single dimension" probabilistic model
     * 
     * @param events The probabilistic model. Each cell value will be the probability of a single event
     * @return The outcome index
     */
    private int getNormalizedOutcome(double [] events) {
        double [] abilityArray = {1, 1, 1};
        return getOutcome(events, abilityArray);
    }
    
    /**
     * Log a highlight about the occurence of a penalty kick
     * @param team The team been awarded the penalty
     * @param taker The taker of the penalty
     * @param minute The minute of the penalty in simulated time
     */
    public void reportPenalty(Team team, Player taker, double minute) {
        logHighlight(matchReport.getTimer(), new Integer(new Double(minute).intValue()).toString() + "'");
        logHighlight(matchReport.getTimer(), "Penalty for " + team.getName() + "!");
        logHighlight(matchReport.getTimer(), taker.getFamilyName() + " is going to take it");
    }
    
    /**
     * Checks whether a 'result' is of type compatible to a foul
     * @param result The result code
     * @return True if the result code is of type compatible to a foul 
     */
    private boolean isResultFoul(byte result) {
        if (result == Constants.RES_Foul_Tackling || 
            result == Constants.RES_Foul) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Checks whether the current instant's outcome results to an offensive foul
     * @param currentInstant The current instant
     * @return True if the current instant's outcome results to a foul
     */
    private boolean isResultOffensiveFoul(Instant currentInstant) {
        
        if (currentInstant.outcome instanceof Challenge) {
            Challenge currentChallenge = (Challenge) currentInstant.outcome;
            if ((currentChallenge.endTeam == Constants.OPP_TEAM) && currentChallenge.challengeEnding == Constants.RES_Foul) {
                return true;
            } else {
                return false;
            }
        }
        
        if (currentInstant.outcome.condition == Constants.RES_OffensiveFoul ||
            currentInstant.outcome.condition == Constants.RES_GkFoul) {
            return true;
        } else {
            return false;
        }
        
    }
    
    /**
     * Process a shot off target event
     * @param action The action resulting to the shot off
     * @param cross The "shot off" is an outcome of cross
     * @return The next state
     */
    private State processShotOff(byte action, boolean cross) {
    
        // Find shooter
        Player shooter = matchReport.getCurrentState().getPlayer();
        
        double minute = matchReport.getTime();
        
        // Get shot outcome
        double shotOutcome = rnd.nextDouble();
        
        matchReport.getCurrentState().getTeam().getStats().addShotOffTarget();
        
        State outcomeState = null;
        
        boolean cornerKick = false;
        
        if (shotOutcome <= RealWorldMapping.SHOT_OFF_GOAL_KICK) {
            // goal kick
            outcomeState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
            outcomeState.setPlayer(outcomeState.getTeam().getGK());
        } else {
            // corner kick
            outcomeState = new State(matchReport.getCurrentState().getTeam(), State.X.CORNER_KICK, State.Y.ATTACK, State.Pressure.FREE);
            
            if (matchReport.getCurrentState().inFlank()) {
                outcomeState.setSide(matchReport.getCurrentState().getSide());
            } else {
                outcomeState.setRandomSide();
            }
            
            Tactics.TacticPosition currentPosition = null;
            
            if (outcomeState.getSide() == OOConstants.LEFT_SIDE) {
                currentPosition = Tactics.TacticPosition.LEFT;
            } else if (outcomeState.getSide() == OOConstants.RIGHT_SIDE) {
                currentPosition = Tactics.TacticPosition.RIGHT;
            }
            
            outcomeState.setPlayer(outcomeState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentPosition));
            
            cornerKick = true;
        }
        
        int cornerKickIndex = cornerKick?1:0;
        
        if (!cross) {
        
            matchRewind.addSignal(new Shot(matchReport.getTimer(), RealWorldMapping.SHOT_OFF, cornerKickIndex, 
                    shooter.getFamilyName(), matchReport.getCurrentState().getTeam().getName()));
            
        } else {
            
            matchRewind.addSignal(new CrossShot(matchReport.getTimer(), RealWorldMapping.SHOT_OFF, cornerKickIndex, 
                    shooter.getFamilyName(), matchReport.getCurrentState().getCrosser().getFamilyName(), 
                    matchReport.getCurrentState().getTeam().getName()));
            
        }
        
        reportShotOff(shooter.getFamilyName(), minute, action, cornerKick);
        matchReport.getCurrentEvent().setSpecial(Report.ShotOff);
        
        return outcomeState;
    }
    
    /**
     * Processes a "shot on target" event
     * 
     * @param action The action causing the "shot on target"
     * @return The next state
     */
    private State processShotOn(byte action, boolean cross) {
        
        Player shooter = matchReport.getCurrentState().getPlayer();
        
        double minute = matchReport.getTime();
        double shotOutcome = rnd.nextDouble() * 100;
        
        double totalOutcome = 0;
        int outcomeIndex = 0;
        for (int i = 0; i < RealWorldMapping.ShotOnStats.length; i++) {
            totalOutcome += RealWorldMapping.ShotOnStats[i];
            if (shotOutcome < totalOutcome) {
                outcomeIndex = i;
                break;
            }
        }
        
        matchReport.getCurrentState().getTeam().getStats().addShotOnTarget();
        
        State outcomeState = null;
        
        switch (outcomeIndex){
        case RealWorldMapping.AFTER_SHOT_DEFENDER:
        case RealWorldMapping.AFTER_SHOT_POST_DEFENDER:
            outcomeState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.PRESSED);
            outcomeState.setPlayer(outcomeState.getTeam().getDefensiveRebounder());
            break;
        case RealWorldMapping.AFTER_SHOT_GK:
        case RealWorldMapping.AFTER_SHOT_POST_GK:
        case RealWorldMapping.AFTER_SHOT_POST_GOAL_KICK:
            outcomeState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
            outcomeState.setPlayer(outcomeState.getTeam().getGK());
            break;
        case RealWorldMapping.AFTER_SHOT_CORNER_KICK:
            outcomeState = new State(matchReport.getCurrentState().getTeam(), State.X.CORNER_KICK, State.Y.ATTACK, State.Pressure.FREE);
            
            if (matchReport.getCurrentState().inFlank()) {
                outcomeState.setSide(matchReport.getCurrentState().getSide());
            } else {
                outcomeState.setRandomSide();
            }
            
            Tactics.TacticPosition currentPosition = null;
            
            if (outcomeState.getSide() == OOConstants.LEFT_SIDE) {
                currentPosition = Tactics.TacticPosition.LEFT;
            } else if (outcomeState.getSide() == OOConstants.RIGHT_SIDE) {
                currentPosition = Tactics.TacticPosition.RIGHT;
            }
            
            outcomeState.setPlayer(outcomeState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentPosition));
            break;
        case RealWorldMapping.AFTER_SHOT_FORWARD:
        case RealWorldMapping.AFTER_SHOT_POST_FORWARD:
        case RealWorldMapping.AFTER_SHOT_DEFENDER_SAVE_FORWARD:
            outcomeState = new State(matchReport.getCurrentState().getTeam(), State.X.AXIS, State.Y.ATTACK, State.Pressure.PRESSED);
            outcomeState.setPlayer(outcomeState.getTeam().getAttackingRebounder());
            break;
        case RealWorldMapping.AFTER_SHOT_DEFENDER_SAVE_THROW_IN:
        case RealWorldMapping.AFTER_SHOT_THROW_IN:
            outcomeState = new State(matchReport.getCurrentState().getTeam(), State.X.THROW_IN, State.Y.ATTACK, State.Pressure.PRESSED);
            
            if (matchReport.getCurrentState().inFlank()) {
                outcomeState.setSide(matchReport.getCurrentState().getSide());
            } else {
                outcomeState.setRandomSide();
            }
            
            Tactics.TacticPosition currentThrowInPos = null;
            
            if (outcomeState.getSide() == OOConstants.LEFT_SIDE) {
                currentThrowInPos = Tactics.TacticPosition.LEFT;
            } else if (outcomeState.getSide() == OOConstants.RIGHT_SIDE) {
                currentThrowInPos = Tactics.TacticPosition.RIGHT;
            }
            
            outcomeState.setPlayer(outcomeState.getTeam().getPlayerByPosition(Constants.MIDFIELDER, currentThrowInPos));
            break;
        }
      
        reportShotOn(shooter.getFamilyName(), minute, action, outcomeIndex, matchReport.getCurrentState(), outcomeState);
        matchReport.getCurrentEvent().setSpecial(Report.ShotOn);
        
        if (!cross) {
        
            matchRewind.addSignal(new Shot(matchReport.getTimer(), RealWorldMapping.SHOT_ON, outcomeIndex, shooter.getFamilyName(), 
                    matchReport.getCurrentState().getTeam().getName()));
            
        } else {
            
            matchRewind.addSignal(new CrossShot(matchReport.getTimer(), RealWorldMapping.SHOT_ON, outcomeIndex, 
                    shooter.getFamilyName(), matchReport.getCurrentState().getCrosser().getFamilyName(), 
                    matchReport.getCurrentState().getTeam().getName()));
            
        }
        
        return outcomeState;
    }
    
    /**
     * Logs a "shot off" event as a highlight
     * @param familyName The shooter's family name
     * @param minute The simulated time's minute
     * @param action The action which resulted in the "shot off"
     * @param cornerKick The shot off event started from a corner kick
     */
    private void reportShotOff(String familyName, double minute, byte action, boolean cornerKick) {
        logHighlight(matchReport.getTimer(), new Integer(new Double(minute).intValue()).toString() + "'");
        describeCurrentAction(familyName, action);
        logHighlight(matchReport.getTimer(), "But he doesn't find target");
        if (cornerKick) {
            logHighlight(matchReport.getTimer(), "The ball must have deflected somewhere...");
            logHighlight(matchReport.getTimer(), "It's a corner");
        }            
    }
    
    /**
     * Logs as "shot on" event as a highlight
     * @param familyName The shooter's family name
     * @param minute The simulated time's minute
     * @param action The action of the "shot on" event
     * @param outcomeIndex The code showing what the outcome is
     * @param state The current state
     * @param outcomeState The resulting state
     */
    private void reportShotOn(String familyName, double minute, byte action, int outcomeIndex, State state, State outcomeState) {
        logHighlight(matchReport.getTimer(), new Integer(new Double(minute).intValue()).toString() + "'");
        describeCurrentAction(familyName, action);
        switch(outcomeIndex) {
        case RealWorldMapping.AFTER_SHOT_DEFENDER:
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getGK().getFamilyName() + " saves");
            logHighlight(matchReport.getTimer(), outcomeState.getPlayer().getFamilyName() + " has the ball");
            break;
        case RealWorldMapping.AFTER_SHOT_POST_DEFENDER:
            logHighlight(matchReport.getTimer(), "Hits the post!");
            logHighlight(matchReport.getTimer(), outcomeState.getPlayer().getFamilyName() + " has the ball");
            break;
        case RealWorldMapping.AFTER_SHOT_GK:
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getGK().getFamilyName() + " saves and holds the ball");
            break;
        case RealWorldMapping.AFTER_SHOT_POST_GK:
            logHighlight(matchReport.getTimer(), "Hits the post!");
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getGK().getFamilyName() + " holds the ball");
            break;
        case RealWorldMapping.AFTER_SHOT_POST_GOAL_KICK:
            logHighlight(matchReport.getTimer(), "Hits the post!");
            logHighlight(matchReport.getTimer(), "The ball is out for a goal kick");
            break;
        case RealWorldMapping.AFTER_SHOT_CORNER_KICK:
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getGK().getFamilyName() + " saves");
            logHighlight(matchReport.getTimer(), "The ball is out for a corner");
            break;
        case RealWorldMapping.AFTER_SHOT_FORWARD:
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getGK().getFamilyName() + " saves");
            logHighlight(matchReport.getTimer(), outcomeState.getPlayer().getFamilyName() + " has the ball");
            break;
        case RealWorldMapping.AFTER_SHOT_POST_FORWARD:
            logHighlight(matchReport.getTimer(), "Hits the post!");
            logHighlight(matchReport.getTimer(), outcomeState.getPlayer().getFamilyName() + " has the ball");
            break;
        case RealWorldMapping.AFTER_SHOT_DEFENDER_SAVE_FORWARD:
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getDefensiveRebounder().getFamilyName() + " saves on the line!");
            logHighlight(matchReport.getTimer(), outcomeState.getPlayer().getFamilyName() + " gets the ball");
            break;
        case RealWorldMapping.AFTER_SHOT_DEFENDER_SAVE_THROW_IN:
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getDefensiveRebounder().getFamilyName() + " saves on the line!");
            logHighlight(matchReport.getTimer(), "The ball is out for a throw in");
            break;
        case RealWorldMapping.AFTER_SHOT_THROW_IN:
            logHighlight(matchReport.getTimer(), toggleTeam(state.getTeam()).getGK().getFamilyName() + " saves");
            logHighlight(matchReport.getTimer(), "The ball is out for a throw in");
            break;
        }
    }
    
    /**
     * 
     * Process a goal as a match event
     * 
     * @param action The action that led to the goal
     * @param cross The goal has come from a cross
     * @return The next state after the goal is scored, i.e. the kick off state
     */
    private State processGoal(byte action, boolean cross) {
        
        gameplay.Player goalScorer = matchReport.getCurrentState().getPlayer(); // No need to find the goal scorer after the fact anymore
        
        double minute = matchReport.getTime();
        
        matchReport.getCurrentState().getTeam().getStats().scoreGoal(); // Update score
        goalScorer.getStats().addGoal(matchReport.getTimer());
        
        // For the statistics
        matchReport.getCurrentState().getTeam().getStats().addShotOnTarget();
        
        matchReport.getCurrentState().getTeam().registerGoalEvent(new Team.GoalDetails(goalScorer, minute, false, matchReport.getTimer()));
        
        reportGoal(goalScorer.getFamilyName(), minute, action);
        
        matchReport.getCurrentEvent().setSpecial(Report.Goal);
        
        State nextState = new State(toggleTeam(matchReport.getCurrentState().getTeam()), State.X.AXIS, State.Y.DEFENCE, State.Pressure.FREE);
        
        nextState.setPlayer(nextState.getTeam().getAnyDefensivePlayer());
        
        if (!cross) {
        
            matchRewind.addSignal(new Shot(matchReport.getTimer(), RealWorldMapping.GOAL, -1, 
                matchReport.getCurrentState().getPlayer().getFamilyName(), matchReport.getCurrentState().getTeam().getName()));
            
        } else {
            
            matchRewind.addSignal(new CrossShot(matchReport.getTimer(), RealWorldMapping.GOAL, -1, 
                    matchReport.getCurrentState().getPlayer().getFamilyName(), matchReport.getCurrentState().getCrosser().getFamilyName(), 
                        matchReport.getCurrentState().getTeam().getName()));
            
        }
        
        return nextState;
        
    }
    
    /**
     * Logs a goal as a highlight
     * 
     * @param familyName The family name of the scorer
     * @param minute The minute of the simulated time the goal was scored
     * @param action The action with which the goal is scored
     */
    private void reportGoal(String familyName, double minute, byte action) {
        logHighlight(matchReport.getTimer(), new Integer(new Double(minute).intValue()).toString() + "'");
        describeCurrentAction(familyName, action);
        logHighlight(matchReport.getTimer(), familyName + " scores!");
        logHighlight(matchReport.getTimer(), matchReport.getScoreLine(this.homeTeam, this.awayTeam));
    }
    
    /**
     * Outputs to the user a description of an action
     * 
     * @param name The family name of the player performing the action
     * @param action The action code
     */
    private void describeCurrentAction(String name, byte action) {
        
        String description = "";
        
        switch(action) {
        case Constants.LongShot:
            description = " attempts a shot from long range";
            break;
        case Constants.AreaShot:
            description = " has the ball inside the penalty area";
            break;
        case Constants.Cross:
        case Constants.LongCross:
            logHighlight(matchReport.getTimer(), matchReport.getCurrentState().getCrosser().getFamilyName() + " crosses the ball");
            description = " gets the ball from the cross!";
            break;
        default:
            description = " didn't expect the ball"; 
        }
        
        logHighlight(matchReport.getTimer(), name + description);
    }
    
    /**
     * Gets the object of the opponent team
     * 
     * @param oldTeam The team whose opponent we want to find
     * @return The opponent team object
     */
    private Team toggleTeam(Team oldTeam) {
        if (this.homeTeam == oldTeam) {
            return this.awayTeam;
        } else {
            return this.homeTeam;
        }
    }
    
    /**
     * Corresponds an outcome array (based on statistics) with the ability of the team for the corresponding outcome to determine 
     * which will finally be the outcome
     * 
     * @param statsArray The outcome array according to statistics
     * @param abilityArray The array of team's abilities
     * @return An index representing the outcome
     */
    private int getOutcome(double [] statsArray, double [] abilityArray) {
        
        double total = 0d;
        double [] mergeArray = new double[statsArray.length];
        
        for (int i = 0; i < statsArray.length; i++) {
            mergeArray[i] = statsArray[i] * abilityArray[i];
            total += mergeArray[i];
        }
        
        double outcome = rnd.nextDouble() * total;
        
        double currentTotal = 0;
        for (int i = 0; i < mergeArray.length; i++) {
            currentTotal += mergeArray[i];
            if (outcome < currentTotal) return i;
        }
        
        return statsArray.length;
    }
    
    /**
     * Checks whether an action is compatible with the outcome of a goal being scored
     * @param action The action to be checked
     * @return The action is compatible with the outcome of a goal being scored
     */
    private boolean goalScoringAction(byte action) {
        if ((action == Constants.LongShot) || 
            (action == Constants.AreaShot)) {
                return true;
        } else {
            return false;
        }
    }
    
    /**
     * Checks whether an action is compatible with the event of a cross
     * @param action The action to be scored
     * @return The action corresponds to a cross event
     */
    private boolean crossingAction(byte action) {
        if ((action == Constants.Cross) || 
            (action == Constants.LongCross)) {
            return true;
        } else {
            return false;
        }
    }
     
    /**
     * Checks whether an instant has a goal scoring opportunity as an outcome
     * @param instant The current instant
     * @return The current instant has a goal scoring opportunity as an outcome
     */
    private boolean goalScoringOpportunity(Instant instant) {
        
        if (instant.outcome.condition < 1)
            return false;
        
        if (instant.outcome.condition == Constants.RES_ShotOff ||
            instant.outcome.condition == Constants.RES_ShotOffCorner ||
            instant.outcome.condition == Constants.RES_ShotOnDefender ||
            instant.outcome.condition == Constants.RES_Goal) {
                return true;
            } else {
                return false;
            }
    }
     
    /**
     * Checks whether an instant (from the probability model) is compatible with the (current) state
     * @param instant The checked instant
     * @param state The current state
     * @return The instant is compatible with the state
     */
    private boolean matchCurrentState(Instant instant, State state) {
        
        return (state.getX().matchValue(instant.X) &&
                state.getY().matchValue(instant.Y) &&
                state.getPressure().matchValue(instant.Pressure));
        
    }
    
    /**
     * Checks whether an instant (from the probability model) is compatible with the (current) state without taking pressure into account
     * @param instant The checked instant
     * @param state The current state
     * @return The instant is compatible with the state
     */
    private boolean matchCurrentStateWithoutPressure(Instant instant, State state) {
        return (state.getX().matchValue(instant.X) && state.getY().matchValue(instant.Y));
    }
    
    /**
     * Transforms the "probability model" from its binary format as it is read from a file to an object-oriented representation
     */
    private void transformProbModel() {
        
        for (int i = 0; i < probModel.size(); i++) {
            
            byte currentY = probModel.get(i).getCurrentRow()[Constants.Y];
            byte currentX = probModel.get(i).getCurrentRow()[Constants.X];
            byte currentPressure = probModel.get(i).getCurrentRow()[Constants.PRESSURE];
            byte currentAction = probModel.get(i).getCurrentRow()[Constants.ACTION];
            
            Instant currentInstant = new Instant(currentY, currentX, currentPressure, currentAction);
            
            byte resY = probModel.get(i).getCurrentRow()[Constants.RES_Y];
            byte resX = probModel.get(i).getCurrentRow()[Constants.RES_X];
            byte resPressure = probModel.get(i).getCurrentRow()[Constants.RES_PRESSURE];
            
            ResultState resultState = new ResultState(resX, resY, resPressure);
            currentInstant.outcomeState = resultState;
            
            // System.out.println(resultState);
            
            // Now the mess starts to untangle
            byte [] currentRow = probModel.get(i).getCurrentRow();
            
            if (currentRow[Constants.RESULT_DESC] == Constants.CONDITION && currentRow[Constants.RES_TEAM] == Constants.OPP_TEAM) {
                
                currentInstant.outcome = new Opponent(OOConstants.COND_NORMAL, currentRow[Constants.BALL_POSSESSION_CHANGE]);
                
            } else if (currentRow[Constants.RESULT_DESC] == Constants.CONDITION && currentRow[Constants.RES_TEAM] == Constants.OWN_TEAM) {
                
                currentInstant.outcome = new Success(OOConstants.COND_NORMAL);
                
            } else if (currentRow[Constants.RESULT_DESC] == Constants.CHALLENGE) {
                
                currentInstant.outcome = new Challenge(currentRow[Constants.CHALLENGE_TEAM], currentRow[Constants.CHALLENGE_Y], 
                                                       currentRow[Constants.RES_TEAM], currentRow[Constants.RES_Y], currentRow[Constants.RES_X], 
                                                       currentRow[Constants.CHALLENGE_ENDING], currentRow[Constants.CHALLENGE_TYPE]);
                
            } else if (currentRow[Constants.RESULT_DESC] == Constants.OTHER) {
                if (currentRow[Constants.RES_TEAM] == Constants.OWN_TEAM) {
                    currentInstant.outcome = new Success(currentRow[Constants.OTHER_RES]);
                } else if (currentRow[Constants.RES_TEAM] == Constants.OPP_TEAM) {
                    currentInstant.outcome = new Opponent(currentRow[Constants.OTHER_RES], currentRow[Constants.BALL_POSSESSION_CHANGE]);
                }
            }
            
            // save id for easier debugging; it doesn't really mean a thing otherwise as ids with the same modulo are duplicated
            currentInstant.rowId = currentRow[Constants.ROW_ID]; 
            
            // System.out.println(currentInstant);
           
            matchRepresentation.add(currentInstant);
        }
        
    }
    
    /**
     * Prints out the probability model after its transformation (used for debugging purposes only)
     */
    public void showProbModel() {
        
        loadProbModel();
        transformProbModel();
        
        for (Instant instant:matchRepresentation) {
            System.out.println(instant);
        } 
    }
    
    /**
     * Reads the binary file describing the match probability model and loads it to the corresponding data structure
     */
    private void loadProbModel() {
        
        try {
            FileInputStream binaryInput = new FileInputStream(probModelFilename);
            
            byte [] currentRow = new byte[Constants.REC_SIZE];
            
            int currentSize = -1;
            while ((currentSize = binaryInput.read(currentRow)) != -1) {
                
                if (currentSize != Constants.REC_SIZE) {
                    System.out.println("Possibly corrupted file. Reading operation failed");
                    return;
                }
                
                byte [] loadedRow = new byte[Constants.REC_SIZE];
                
                for (int i = 0; i < loadedRow.length; i++) {
                    loadedRow[i] = currentRow[i];
                }
                
                WrapperObject currentWO = new WrapperObject(loadedRow);
                
                probModel.add(currentWO);
            }
            
            binaryInput.close();
            
        } catch (FileNotFoundException fnfe) {
            System.out.println("Probabilistic model data not found");
            return;
        } catch (IOException ioe) {
            System.out.println("Error reading probabilistic model file");
            return;
        }
    }
    
    /**
     * Simulates the coin toss to decide whether the home or away team is going to kick off
     * 
     * @return The kick-off team
     */
    public Team decideKickOff() {
        
        boolean homeTeam = rnd.nextBoolean();
        Team kickOffTeam;
        
        if (homeTeam) {
            kickOffTeam = this.homeTeam;
        } else {
            kickOffTeam = this.awayTeam;
        }
        
        return kickOffTeam;
    }
}
