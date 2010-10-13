package test;

import gameplay.Player;
import gameplay.PlayerAttribute;
import gameplay.PlayerAttributes;
import interactivity.EndOfMatch;
import interactivity.Signal;

import java.util.ArrayList;

import utility.Tactics;
import core.Match;
import core.MatchReport;
import core.Team;

public class HardcodedMatch {
    
    private static final double EXCELLENT = 6d;
    
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
    
    public static void main(String[] args) {
        
        String homeTeamName = "Greece";
        String awayTeamName = "Germany";
        
        Team homeTeam = new Team(homeTeamName);
        Team awayTeam = new Team(awayTeamName);
        
        // Home team players
        
        ArrayList<Player> homeTeamPlayers = new ArrayList<Player>();
        ArrayList<Player> awayTeamPlayers = new ArrayList<Player>();
        
        homeTeamPlayers.add(new Player(1, "", "Plato", Tactics.TacticLine.GK.ordinal() + 1));
        homeTeamPlayers.add(new Player(3, "", "Epictetus", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        homeTeamPlayers.add(new Player(6, "", "Aristotle", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        homeTeamPlayers.add(new Player(7, "", "Sophocles", Tactics.TacticLine.MIDFIELDER.ordinal() + 1));
        homeTeamPlayers.add(new Player(9, "", "Embedocles", Tactics.TacticLine.FORWARD.ordinal() + 1));
        homeTeamPlayers.add(new Player(15, "", "Plotinus", Tactics.TacticLine.MIDFIELDER.ordinal() + 1));
        homeTeamPlayers.add(new Player(17, "", "Epicurus", Tactics.TacticLine.MIDFIELDER.ordinal() + 1));
        homeTeamPlayers.add(new Player(18, "", "Heraklitus", Tactics.TacticLine.FORWARD.ordinal() + 1));
        homeTeamPlayers.add(new Player(21, "", "Democritus", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        homeTeamPlayers.add(new Player(23, "", "Socrates", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        homeTeamPlayers.add(new Player(31, "", "Archimedes", Tactics.TacticLine.MIDFIELDER.ordinal() + 1));
        
        for (Player currentPlayer:homeTeamPlayers) {
            for (PlayerAttribute attr:PlayerAttributes.getAll()) {
                currentPlayer.addSkill(attr.getName(), EXCELLENT);
            }
            
            homeTeam.addPlayer(currentPlayer, db2enum(currentPlayer.getPosition()));
        }
        
        /*
        for (PlayerAttribute attr:PlayerAttributes.getAll()) {
            ph1.addSkill(attr.getName(), EXCELLENT);
        }
        */
        
        // Away team players
        awayTeamPlayers.add(new Player(25, "Gottfried", "Leibniz", Tactics.TacticLine.GK.ordinal() + 1));
        awayTeamPlayers.add(new Player(5, "Immanuel", "Kant", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        awayTeamPlayers.add(new Player(7, "Georg", "Hegel", Tactics.TacticLine.FORWARD.ordinal() + 1));
        awayTeamPlayers.add(new Player(8, "Arthur", "Schopenhauer", Tactics.TacticLine.MIDFIELDER.ordinal() + 1));
        awayTeamPlayers.add(new Player(11, "Friedrich", "Schelling", Tactics.TacticLine.FORWARD.ordinal() + 1));
        awayTeamPlayers.add(new Player(12, "Karl", "Marx", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        awayTeamPlayers.add(new Player(14, "Karl", "Jaspers", Tactics.TacticLine.MIDFIELDER.ordinal() + 1));
        awayTeamPlayers.add(new Player(17, "Karl", "Schegel", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        awayTeamPlayers.add(new Player(18, "Ludwig", "Wittgenstein", Tactics.TacticLine.FORWARD.ordinal() + 1));
        awayTeamPlayers.add(new Player(20, "Friedrich", "Nietzsche", Tactics.TacticLine.MIDFIELDER.ordinal() + 1));
        awayTeamPlayers.add(new Player(23, "Martin", "Heidegger", Tactics.TacticLine.DEFENDER.ordinal() + 1));
        
        for (Player currentPlayer:awayTeamPlayers) {
            for (PlayerAttribute attr:PlayerAttributes.getAll()) {
                currentPlayer.addSkill(attr.getName(), EXCELLENT);
            }
            
            awayTeam.addPlayer(currentPlayer, db2enum(currentPlayer.getPosition()));
        }
        
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
        
    }

}
