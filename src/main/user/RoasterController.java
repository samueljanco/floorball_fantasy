package main.user;

import main.database.DatabaseController;

import java.util.*;

/**
 * Contorller for roaster
 */
public class RoasterController {

    private final int ID;
    private final ArrayList<PlayerInfo> roaster;

    HashMap<RoasterPosition, ArrayList<Integer>> spots = new HashMap<>();

    private PlayerInfo substitutePlayer = new PlayerInfo();

    /**
     * @param ID *
     * @param roaster *
     */
    public RoasterController(int ID, ArrayList<PlayerInfo> roaster){
        this.ID = ID;
        this.roaster = roaster;
        spots.put(RoasterPosition.G, new ArrayList<>(List.of(1)));
        spots.put(RoasterPosition.F, new ArrayList<>(Arrays.asList(2,3,4,7,8,9)));
        spots.put(RoasterPosition.D, new ArrayList<>(Arrays.asList(5,6,10,11)));
        spots.put(RoasterPosition.BN, new ArrayList<>(Arrays.asList(12,13,14)));
    }

    /**
     * @return id
     */
    public int getID(){
        return ID;
    }

    /**
     * @return substitute id
     */
    public int getSubstitutePlayerID() {
        return substitutePlayer.ID;
    }

    /**
     * @param player *
     */
    public void setSubstitutePlayer(PlayerInfo player){
        substitutePlayer = player;
    }

    /**
     * @return roaster spot
     */
    public int getSubstitutePlayerRoasterSpot(){
        return substitutePlayer.roasterSpot;
    }

    /**
     * @return roaster
     */
    public ArrayList<PlayerInfo> getRoaster() {
        return roaster;
    }

    /**
     * Drops the player
     * @param player *
     */
    public void dropPlayer(PlayerInfo player){
        PlayerInfo emptyPlayer = new PlayerInfo(0,"Empty",player.teamID,"","",  player.roasterSpot, 0);
        roaster.set(player.roasterSpot-1, emptyPlayer);
        player.drop();
        DatabaseController.dropPlayer(player.ID);
    }

    /**
     * @param player *
     * @return true if successfully added
     */
    public boolean addPlayer(PlayerInfo player){
        player.roasterSpot = getFreeRosterSpot(player.position);
        if(player.roasterSpot > 0) {
            player.teamID = ID;
            roaster.set(player.roasterSpot-1, player);
            DatabaseController.addPlayer(player.ID, player.teamID, player.roasterSpot);

            return true;
        }
        return false;

    }

    /**
     * swaps players
     * @param player *
     */
    public void swapRoasterSpots(PlayerInfo player){
        int x = player.roasterSpot;
        player.roasterSpot = substitutePlayer.roasterSpot;
        substitutePlayer.roasterSpot = x;
        Collections.swap(roaster, player.roasterSpot-1, substitutePlayer.roasterSpot-1);
        DatabaseController.changeRosterSpot(player.ID, player.roasterSpot);
        DatabaseController.changeRosterSpot(substitutePlayer.ID, substitutePlayer.roasterSpot);
    }

    /**
     * @param player *
     * @return true if can swap, false otherwise
     */
    public boolean canSwap(PlayerInfo player){
        return player.position == substitutePlayer.position ||
                (player.ID == 0 && spots.get(positionToRoasterPosition(substitutePlayer.position)).contains(player.roasterSpot)) ||
                (substitutePlayer.ID == 0 && spots.get(positionToRoasterPosition(player.position)).contains(substitutePlayer.roasterSpot)) ||
                (player.ID == 0 && spots.get(RoasterPosition.BN).contains(player.roasterSpot)) ||
                (spots.get(RoasterPosition.BN).contains(substitutePlayer.roasterSpot) && substitutePlayer.ID == 0);
    }

    /**
     * @param p *
     * @return position
     */
    private RoasterPosition positionToRoasterPosition(Position p){
        if (p == Position.G){
            return RoasterPosition.G;
        } else if (p == Position.F) {
            return RoasterPosition.F;
        } else if (p == Position.D) {
            return RoasterPosition.D;
        }else{
            return RoasterPosition.BN;
        }
    }

    /**
     * @param p *
     * @return free spot on roaster
     */
    private int getFreeRosterSpot(Position p){
       ArrayList<Integer> freeSpots = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14));
       for(PlayerInfo player: roaster){
           if(player.ID > 0){
               freeSpots.remove(Integer.valueOf(player.roasterSpot));
           }
       }
       if(freeSpots.size() > 0){
           RoasterPosition rp = positionToRoasterPosition(p);
           for (int i: freeSpots) {
               if(spots.get(rp).contains(i)){
                   return i;
               }
           }
           for (int i: freeSpots) {
               if(spots.get(RoasterPosition.BN).contains(i)){
                   return i;
               }
           }
       }
       return 0;
    }



}
