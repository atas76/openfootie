package test;

import test.exceptions.InvalidPosValueException;
import utility.Tactics;

public class MatchUtil {

    /**
     * Utility: Transform player position as read from database to the corresponding enum value
     * @param pos
     * @return
     */
    protected static Tactics.TacticLine db2enum(int pos) throws InvalidPosValueException {
        
        Tactics.TacticLine retVal = null;
        
        switch (pos) {
        case 1:
            retVal = Tactics.TacticLine.GK;
            break;
        case 2:
            retVal = Tactics.TacticLine.DEFENDER;
            break;
        case 3:
            retVal = Tactics.TacticLine.MIDFIELDER;
            break;
        case 4:
            retVal = Tactics.TacticLine.FORWARD;
            break;
        }
        
        if (retVal == null) throw new InvalidPosValueException("Invalid position value");
        
        return retVal;
        
    }

}
