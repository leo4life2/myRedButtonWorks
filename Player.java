// import the API.
// See xxx for the javadocs.
import bc.*;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Player {
    public static Random RAND = new Random();
    public static GameController gc;
    public static Team myteam;
    public static Team enemyTeam;

    public static ArrayList<Unit> vecUnittoArrayList(VecUnit vec){
        ArrayList<Unit> units = new ArrayList<>();
        for(int i =0; i<vec.size(); i++){
            units.add(vec.get(i));
        }
        return units;
    }

    public static Unit[] vecUnittoArray(VecUnit vec){
        Unit[] units = new Unit[(int)vec.size()];
        for(int i =0; i<vec.size(); i++){
            units[i]=(vec.get(i));
        }
        return units;
    }

    public static int[] vecUnitIDtoArray(VecUnitID vec){
        int[] unitsID = new int[(int)vec.size()];
        for(int i =0; i<vec.size(); i++){
            unitsID[i]=(vec.get(i));
        }
        return unitsID;
    }

    public static Direction getDirToTargetMapLocNaive(MapLocation ourloc, MapLocation targetLoc){
        //returns a (naive) direction used for moving towards a target location
        return ourloc.directionTo(targetLoc);
    }

    public static Direction getDirAwayFromTargetMapLocNaive(MapLocation ourloc, MapLocation targetLoc){
        //returns a (naive) direction used for moving towards a target location
        return targetLoc.directionTo(ourloc);
    }

    public static Direction getDirToTargetMapLocGreedy(GameController gc, Unit ourUnit, MapLocation targetLoc) {
        // the unit will try to move greedily to a direction approximately towards the target location.
        Direction dirToTarget = ourUnit.location().mapLocation().directionTo(targetLoc);
        if (dirToTarget == Direction.Center) {
            return dirToTarget;
        }
        int dirIndex = dirToTarget.swigValue();
        for (int i = 0; i < 4; i++) {
            int newDirIndex = (dirIndex += (i+8)) % 8;
            Direction newDirection = Direction.swigToEnum(newDirIndex);
            if (gc.canMove(ourUnit.id(), newDirection)) {
                return newDirection;
            }

            newDirIndex = (dirIndex -= (i+8)) % 8;
            newDirection = Direction.swigToEnum(newDirIndex);
            if (gc.canMove(ourUnit.id(), newDirection)) {
                return newDirection;
            }
        }
        return Direction.Center;
    }


    public static Direction getDirAwayTargetMapLocGreedy(GameController gc, Unit ourUnit, MapLocation targetLoc) {
        // the unit will try to move greedily to a direction approximately towards the target location.
        Direction dirToTarget = targetLoc.directionTo(ourUnit.location().mapLocation());
        if (dirToTarget == Direction.Center) {
            return dirToTarget;
        }
        int dirIndex = dirToTarget.swigValue();
        for (int i = 0; i < 4; i++) {
            int newDirIndex = (dirIndex += (i+8)) % 8;
            Direction newDirection = Direction.swigToEnum(newDirIndex);
            if (gc.canMove(ourUnit.id(), newDirection)) {
                return newDirection;
            }

            newDirIndex = (dirIndex -= (i+8)) % 8;
            newDirection = Direction.swigToEnum(newDirIndex);
            if (gc.canMove(ourUnit.id(), newDirection)) {
                return newDirection;
            }
        }
        return Direction.Center;
    }

    // gc can't be used in helper functions, which is stupid
    /*
    public static boolean distantFromOtherFactory(Unit unit){
        int saftyDisBetweenFactories = 4;
        VecUnit nearbyFactories = gc.senseNearbyUnits();
        float distance = unit1.location().mapLocation().distanceSquaredTo(unit2.location().mapLocation());
        return distance>=saftyDisBetweenFactories;
    }
    */

    public static Direction getRandomDirection(){
        Direction[] directions = Direction.values();
        int i = RAND.nextInt(directions.length);
        return directions[i];
    }

    public static float getDistanceTo(Unit u1, Unit u2){
        return u1.location().mapLocation().distanceSquaredTo(u2.location().mapLocation());
    }
    public static float getDistanceTo(Unit u1, MapLocation u2){
        return u1.location().mapLocation().distanceSquaredTo(u2);
    }

    public static Unit getNearestEnemy(Unit me, VecUnit nearbyEnemiesInVec){
        ArrayList<Unit> nearbyEnemies = vecUnittoArrayList(nearbyEnemiesInVec);

        int size=nearbyEnemies.size();
        Unit[] nearbyEnemiesArray = (Unit[])nearbyEnemies.toArray(new Unit[size]);

        Unit tempNearest = nearbyEnemiesArray[0];

        for (Unit enemy : nearbyEnemiesArray){
            if (getDistanceTo(me, enemy) < getDistanceTo(me, tempNearest)){
                tempNearest = enemy;
            }
        }

        return tempNearest;
    }

    public static Unit getNearestEnemy(Unit me, ArrayList<Unit> nearbyEnemies){

        int size=nearbyEnemies.size();
        Unit[] nearbyEnemiesArray = (Unit[])nearbyEnemies.toArray(new Unit[size]);

        Unit tempNearest = nearbyEnemiesArray[0];

        for (Unit enemy : nearbyEnemiesArray){
            if (getDistanceTo(me, enemy) < getDistanceTo(me, tempNearest)){
                tempNearest = enemy;
            }
        }

        return tempNearest;
    }

    public static Unit getNearestFriendlyRocket(Unit me, ArrayList<Unit> friendlyRockets){

        int size=friendlyRockets.size();
        Unit[] rocketsArray = (Unit[])friendlyRockets.toArray(new Unit[size]);

        Unit tempNearest = rocketsArray[0];

        for (Unit rocket : rocketsArray){
            if (getDistanceTo(me, rocket) < getDistanceTo(me, tempNearest)){
                tempNearest = rocket;
            }
        }

        return tempNearest;

    }

    public static MapLocation getNearestFactoriablePos(GameController gc, Unit me){
        MapLocation myLocation = me.location().mapLocation();
        Planet myPlanet = myLocation.getPlanet();
        if(myPlanet==Planet.Mars){
//            System.out.println("You're on the mars.");
            return null;// you can never build a factory on Mars.
        }
        int workerExploreDis = 5;
        int uid = me.id();
        int meX = myLocation.getX();
        int meY = myLocation.getY();
        for(int dx=0; dx<=workerExploreDis; dx++){
            for(int dy=0; dy<=workerExploreDis; dy++){
                for(int dirX=-1; dirX<=1; dirX+=2){
                    for(int dirY=-1; dirY<=1; dirY+=2){
                        if(dx!=0||dy!=0) {//it's goal should not be itself lol
                            MapLocation goal = new MapLocation(Planet.Earth, meX + dx * dirX, meY + dy * dirY);
//                          System.out.println(goal.getX()+", "+goal.getY());
                            if (isFactoriable(gc, goal))
                                return goal;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isFactoriable(GameController gc, MapLocation goal){
        int factorySafetyDis = 3;
        VecUnit nearbyFactories = gc.senseNearbyUnitsByType(goal, factorySafetyDis, UnitType.Factory);
        for(int i=0; i<nearbyFactories.size(); i++){
            if(nearbyFactories.get(i).team()==gc.team())
                return false;
        }
        return true;
    }

    public static MapLocation getNearestRocketablePos(GameController gc, Unit me){
        MapLocation myLocation = me.location().mapLocation();
        Planet myPlanet = myLocation.getPlanet();
        if(myPlanet==Planet.Mars){
//            System.out.println("You're on the mars.");
            return null;// you can never build a factory on Mars.
        }
        int workerExploreDis = 5;
        int uid = me.id();
        int meX = myLocation.getX();
        int meY = myLocation.getY();
        for(int dx=0; dx<=workerExploreDis; dx++){
            for(int dy=0; dy<=workerExploreDis; dy++){
                for(int dirX=-1; dirX<=1; dirX+=2){
                    for(int dirY=-1; dirY<=1; dirY+=2){
                        if(dx!=0||dy!=0) {//it's goal should not be itself lol
                            MapLocation goal = new MapLocation(Planet.Earth, meX + dx * dirX, meY + dy * dirY);
//                          System.out.println(goal.getX()+", "+goal.getY());
                            if (isRocketable(gc, goal))
                                return goal;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isRocketable(GameController gc, MapLocation goal){
        int factorySafetyDis = 3;
        VecUnit nearbyFactories = gc.senseNearbyUnitsByType(goal, factorySafetyDis, UnitType.Rocket);
        for(int i=0; i<nearbyFactories.size(); i++){
            if(nearbyFactories.get(i).team()==gc.team())
                return false;
        }
        return true;
    }

    public static Unit getNearestEnemyFactory(Unit me, VecUnit factories){
        // i need an array of factories, i need the unit's location
        if(factories.size()==0)
            return null;
        Unit nearest = factories.get(0);
        float minDis = 400;
        for(int i=0; i<factories.size(); i++){
            Unit factory = factories.get(i);
            if (factory.team()!=me.team()){
                float distance = getDistanceTo(me, factory);
                if(distance<=minDis){
                    minDis = distance;
                    nearest = factory;
                }
            }
        }
        return nearest;
    }

    public static Unit getNearestEnemyFactory(Unit me, ArrayList<Unit> factories){
        // i need an array of factories, i need the unit's location
        if(factories.size()==0)
            return null;
        Unit nearest = factories.get(0);
        float minDis = 400;
        for(int i=0; i<factories.size(); i++){
            Unit factory = factories.get(i);
            if (factory.team()!=me.team()){
                float distance = getDistanceTo(me, factory);
                if(distance<=minDis){
                    minDis = distance;
                    nearest = factory;
                }
            }
        }
        return nearest;
    }

    public static MapLocation getRandomMarsLocation(int h, int w){
        int x = (int)(Math.random()*w);
        int y = (int)(Math.random()*h);
        return new MapLocation(Planet.Mars, x, y);
    }

    public static boolean noGuardAroundFactory(GameController gc, Unit factory){
        VecUnit guards = gc.senseNearbyUnitsByTeam(factory.location().mapLocation(), 4, factory.team());
        for (int i=0; i<guards.size(); i++){
            Unit guard = guards.get(i);
            UnitType guardType = guard.unitType();
            if(guardType==UnitType.Worker || guardType==UnitType.Healer|| guardType == UnitType.Factory || guardType==UnitType.Rocket){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

        /* about team array
           0-29: enemy factory    //knight can take care if the factory isn't surrounded by enemies
           30-79: enemy ranger >:O  //mage should take care of this as long as they have a healer helper
           80-97: healer enemy     //knight will kill the healer
           98,99 = current common target location x,y coordinates
        */

    public static int findAChannel(GameController gc, int type){
        Veci32 teamArray = gc.getTeamArray(gc.planet());
        switch (type){
            case 5:{//enemy factories
                for(int i=0; i<30; i++){
                    if(teamArray.get(i)==0){
                        return i;
                    }
                }
                return -1;
            }
            case 2:{//enemy ranger
                for(int i=30; i<80; i++){
                    if(teamArray.get(i)==0){
                        return i;
                    }
                }
                return -1;
            }
            case 4:{//enemy Healer
                for(int i=80; i<100; i++){
                    if(teamArray.get(i)==0){
                        return i;
                    }
                }
                return -1;
            }
            default:
                return -1;
        }
    }

    public static ArrayList<MapLocation> earthAllMapLocs = new ArrayList<>();
    public static ArrayList<MapLocation> marsAllMapLocs = new ArrayList<>();

    public static void fillEarthMapLocs(int mapHeight, int mapWidth){
        for (int i = 0; i < mapWidth; i++){
            for (int q = 0; q < mapHeight; q++){
                earthAllMapLocs.add(new MapLocation(Planet.Earth,i,q));
            }
        }
    }

    public static void fillMarsMapLocs(int mapHeight, int mapWidth){
        for (int i = 0; i < mapWidth; i++){
            for (int q = 0; q < mapHeight; q++){
                marsAllMapLocs.add(new MapLocation(Planet.Mars,i,q));
            }
        }
    }

    public static ArrayList<Unit> allEarthEnemies = new ArrayList<>();
    public static ArrayList<Unit> allMarsEnemies = new ArrayList<>();

    public static void scanForEnemies(){
        //Earth Enemies
        for (MapLocation location : earthAllMapLocs){
            if(gc.canSenseLocation(location) && gc.hasUnitAtLocation(location)) {
                Unit aUnit = gc.senseUnitAtLocation(location);
                if (aUnit.team() == enemyTeam) {
                    allEarthEnemies.add(aUnit);
                }
            }
        }

        //Mars Enemies
        for (MapLocation location : marsAllMapLocs){
            if(gc.canSenseLocation(location) && gc.hasUnitAtLocation(location)) {
                Unit aUnit = gc.senseUnitAtLocation(location);
                if (aUnit.team() == enemyTeam) {
                    allMarsEnemies.add(aUnit);
                }
            }
        }
    }

    public static ArrayList<Unit> allFriendlyRockets = new ArrayList<>();
    // roundNumber is for rocket to check if round matches, then clear arraylist if doesn't match and update info of friendly rockets.
    public static long roundNumber;

//    public static VecUnit senseNearbyUnitsByTeamAndType(GameController gc, Unit me, int radius, UnitType type, Team team){
//        VecUnit byType = gc.senseNearbyUnitsByType(me.location().mapLocation(), radius, type);
//        for(int i =0; i<byType.size(); i++){
//            Unit unit = byType.get(i);
//            if(unit.team()!=team){
//                byType.delete();
//            }
//        }
//    }

    public static MapLocation getANearbyResourceLocation(GameController gc, Unit worker){
        //given a worker's current location, check
        long checkRadius = worker.visionRange();
        VecMapLocation nearbyLocations = gc.allLocationsWithin(worker.location().mapLocation(),checkRadius); //this is all the locations a worker can see

        for (int i = 0; i < nearbyLocations.size(); i++) {
            MapLocation checkLoc = nearbyLocations.get(i);
            if (gc.karboniteAt(checkLoc)>0){
                return checkLoc;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
//        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
//        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
//        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        gc = new GameController();
        AsteroidPattern asteroidPattern = gc.asteroidPattern();
        myteam = gc.team();
        if (myteam==Team.Blue){
            enemyTeam = Team.Red;
        }else{
            enemyTeam = Team.Blue;
        }

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        // get map
        PlanetMap EarthMap = gc.startingMap(Planet.Earth);
        PlanetMap MarsMap = gc.startingMap(Planet.Mars);

        int earthMapHeight = (int)(EarthMap.getHeight());
        int earthMapWidth = (int)(EarthMap.getWidth());
        int marsMapHeight = (int)(MarsMap.getHeight());
        int marsMapWidth = (int)(MarsMap.getWidth());
        int earthMapArea = earthMapHeight*earthMapWidth;
        int marsMapArea = marsMapHeight*marsMapWidth;

        fillEarthMapLocs(earthMapHeight,earthMapWidth);
        fillMarsMapLocs(marsMapHeight,marsMapWidth);

        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Healer);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Healer);
        gc.queueResearch(UnitType.Healer);

        int limit_factory = 6;
        int limit_rocket = 3;
        int limit_worker = 6;
        int limit_knight = (int)(0.02f*earthMapArea);
        int limit_ranger = (int)(0.04f*earthMapArea);


        while (true) {

            scanForEnemies();

            long start = System.currentTimeMillis();
            long current = System.currentTimeMillis();
            // for each round
            int n_worker = 0;
            int n_factory = 0;
            int n_knight = 0;
            int n_ranger = 0;
            int n_mage = 0;
            int n_rocket = 0;
            int n_healer = 0;

            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            // first get to know how many of each unit we have
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                switch (unit.unitType()){
                    case Factory:
                        n_factory ++;
                        break;
                    case Knight:
                        n_knight++;
                        break;
                    case Ranger:
                        n_ranger++;
                        break;
                    case Worker:
                        n_worker++;
                        break;
                    case Mage:
                        n_mage++;
                        break;
                    case Rocket:
                        n_rocket++;
                        break;
                    case Healer:
                        n_healer++;
                        break;
                }
            }


            // then do unit logic
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                long karbonite = gc.karbonite(); // get current karbonite number
                int uid = unit.id(); // Most methods on gc take unit IDs, instead of the unit objects themselves.
                if (!unit.location().isOnMap()){continue;}
                MapLocation maploc = unit.location().mapLocation();
                Veci32 teamArray = gc.getTeamArray(gc.planet());
                VecUnit nearbyTeammates = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(),5,myteam);
                switch(unit.unitType()){
                    case Worker:{
                        try {
//                            System.out.println("The Worker is in!");
                            //current status
                            boolean retreating = false; //running away from an enemy knight
                            boolean leavingHome = false; //running away from our factory
                            boolean building = false; //building or blueprinting

                            MapLocation myLocation = unit.location().mapLocation();

                            if (n_worker <= limit_worker && gc.planet() != Planet.Mars) {//self-reproduction
                                for (int j = 0; j < directions.length; j++) {
                                    Direction d = directions[j];
                                    if (gc.canReplicate(uid, d)) {
                                        gc.replicate(uid, d);
                                        break;
                                    }
                                }
                            }

                            // build nearby buildable structures
                            VecUnit nearbyUnits = gc.senseNearbyUnits(maploc, 2);
                            for (int j = 0; j < nearbyUnits.size(); j++) {
                                Unit other = nearbyUnits.get(j);
                                if (gc.canBuild(uid, other.id())) {
                                    gc.build(uid, other.id());
                                    building = true;
                                    break;
                                }
                                if (gc.canRepair(uid,other.id())){
                                    gc.repair(uid,other.id());
                                    break;
                                }
                            }

                            if(n_factory<3) {
                                MapLocation factoriablePos = getNearestFactoriablePos(gc, unit);
                                if (factoriablePos != null) {
                                    if (factoriablePos.distanceSquaredTo(myLocation) < 2) {
                                        Direction dir = getDirToTargetMapLocNaive(myLocation, factoriablePos);
                                        if (gc.canBlueprint(uid, UnitType.Factory, dir)) {
                                            gc.blueprint(uid, UnitType.Factory, dir);
                                            building = true;
                                        }
                                    } else {
                                        Direction dir = getDirToTargetMapLocGreedy(gc, unit, factoriablePos);
                                        if (gc.isMoveReady(uid) && gc.canMove(uid, dir)) {
                                            gc.moveRobot(uid, dir);
                                        }
                                    }
                                }
                            }else if(n_rocket<limit_rocket) {
                                MapLocation rocketablePos = getNearestRocketablePos(gc, unit);
                                if (rocketablePos != null) {
                                    if (rocketablePos.distanceSquaredTo(myLocation) < 2) {
                                        Direction dir = getDirToTargetMapLocNaive(myLocation, rocketablePos);
                                        if (gc.canBlueprint(uid, UnitType.Rocket, dir)) {
                                            gc.blueprint(uid, UnitType.Rocket, dir);

                                            building = true;
                                        }
                                    } else {
                                        Direction dir = getDirAwayTargetMapLocGreedy(gc, unit, rocketablePos);
                                        if (gc.isMoveReady(uid) && gc.canMove(uid, dir)) {
                                            gc.moveRobot(uid, dir);
                                        }
                                    }
                                }
                            }

                            // move around
                        /*
                            prerequisites:
                            a. there're enemies around, retreat  (safety distance: 3,
                                because the knight's attack range is 1 and it moves and i may get stuck somewhere :( )
                               first, check nearby knights, then ruuuuuuuuun
                            b. there's a factory build around, leave it
                               first, check nearby factories; are they your team's factories?
                                (define nearby as within a radius of
                               wait can i just stuck there when i discover a enemy factory and ask a bunch of knight to come hither so that it stops producint >:D
                               three ways to end an enemy factory : ranger attack/ mage attack/ knight stuck
                            c. too many team mates around, move randomly away.
                         */

                            if (!building) {
//                                System.out.println("I'm not building things!");
                                // retreat away from a knight
                                Direction moveDir = getRandomDirection();
                                VecUnit nearbyEnemy = gc.senseNearbyUnitsByTeam(maploc, 3, enemyTeam);

                                //check enemies and retreat if there's a nearby knight
                                for (int j = 0; j < nearbyEnemy.size(); j++) {
                                    Unit enemy = nearbyEnemy.get(j);
                                    if (enemy.unitType() == UnitType.Knight) {
                                        moveDir = getDirAwayFromTargetMapLocNaive(maploc, enemy.location().mapLocation());
                                        if (gc.isMoveReady(uid) && gc.canMove(uid, moveDir)) {
                                            gc.moveRobot(uid, moveDir);
                                            retreating = true;
                                        }
                                    }
                                }

                                // move away from mother factory and rocket
                                if (!retreating) {//if it's neither building nor retreating, then retreat
                                    VecUnit nearbyFactory = gc.senseNearbyUnitsByType(maploc, 1, UnitType.Factory);
                                    VecUnit nearbyRockets = gc.senseNearbyUnitsByType(maploc, 1, UnitType.Rocket);

                                    if (nearbyFactory.size() != 0) {
//                                        System.out.println("entered escape factory block");
                                        Direction d = getDirAwayFromTargetMapLocNaive(maploc, nearbyFactory.get(0).location().mapLocation());
                                        if (gc.canMove(uid, d) && gc.isMoveReady(uid)) {
                                            leavingHome = true;
//                                            System.out.println("move by escape from factory");
                                            gc.moveRobot(uid, d);
                                        } else {
                                            //if can't move in exact opposite direction, start moving to somewhere random and get space
                                            for (Direction randomDir : directions) {
                                                if (gc.canMove(uid, randomDir) && gc.isMoveReady(uid)) {
                                                    gc.moveRobot(uid, randomDir);
                                                }
                                            }
                                        }
                                    } else if (nearbyRockets.size() != 0) {
                                        Direction d = getDirAwayFromTargetMapLocNaive(maploc, nearbyRockets.get(0).location().mapLocation());
                                        if (gc.canMove(uid, d) && gc.isMoveReady(uid)) {
                                            leavingHome = true;
                                            gc.moveRobot(uid, d);
                                        }
                                    } else if (!leavingHome) {

                                        boolean hasHarvested = false;

                                        // harvest nearby karbonites
                                        for (int j = 0; j < directions.length; j++) {
                                            Direction d = directions[j];
                                            if (gc.canHarvest(uid, d)) {
                                                gc.harvest(uid, d);
                                                hasHarvested = true;
                                                break;
                                            }
                                        }

                                        if(!hasHarvested && gc.isMoveReady(uid)) {

                                            MapLocation nearbyResourceLocation = getANearbyResourceLocation(gc, unit);

                                            if (nearbyResourceLocation != null) {

                                                Direction d = getDirToTargetMapLocGreedy(gc, unit, nearbyResourceLocation);
                                                if (gc.isMoveReady(uid) && gc.canMove(uid, d)) {
                                                    gc.moveRobot(uid, d);
                                                }

                                            }else {
                                                //move randomly
                                                Direction d = getRandomDirection();
                                                if (gc.isMoveReady(uid) && gc.canMove(uid, d)) {
                                                    gc.moveRobot(uid, d);
                                                }
                                            }

                                        }
                                    }
                                }

                            }

                            //System.gc();
                        }catch(Exception e){
                            System.out.println("Worker Exception");
                            e.printStackTrace();
                        }
//                        long t = System.currentTimeMillis();
//                        if(t-current>=500){
//                            current = t;
//                            System.out.println("Worker report: "+(int)((current-start)));
//                        }
                        break;
                    }

                    case Factory:{
                        try {
//                            System.out.println("The factory is working!");
                            // first finish unloading the bot that you're unloading
                            for (int j = 0; j < directions.length; j++) {
                                Direction d = directions[j];
                                if (gc.canUnload(uid, d)) {
                                    gc.unload(uid, d);
                                    break;
                                }
                            }
                            // produce knights if not enough
//                            if (n_knight < limit_knight) {
//                                if (gc.canProduceRobot(uid, UnitType.Knight)) {
//                                    gc.produceRobot(uid, UnitType.Knight);
//                                }
//                            } else if (n_ranger < limit_ranger){ //produce rangers if not enough
//                                if (gc.canProduceRobot(uid, UnitType.Ranger)){
//                                    gc.produceRobot(uid,UnitType.Ranger);
//                                }
//                            } else {// randomly produce knights, mages and rangers
////                                if (Math.random()<0.14) {
//////                                    System.out.print("Trying to produce Mage...");
////                                    if (gc.canProduceRobot(uid, UnitType.Mage)) {
////                                        gc.produceRobot(uid, UnitType.Mage);
////                                    }
////                                }else
                            if(Math.random()<0.33 && gc.round() > 25){ //only make healers after round 25 to ensure there are combat robots already
                                if (gc.canProduceRobot(uid, UnitType.Healer)){
                                    gc.produceRobot(uid,UnitType.Healer);
                                }
//                            } else if (Math.random()>0.14 && Math.random()<0.28) {
//                                if (gc.canProduceRobot(uid, UnitType.Knight)) {
//                                    gc.produceRobot(uid, UnitType.Knight);
//                                }
                            } else {
                                if (gc.canProduceRobot(uid, UnitType.Ranger)){
                                    gc.produceRobot(uid, UnitType.Ranger);
                                }
                            }

                        }catch(Exception e){
                            System.out.println("Factory Exception");
                            e.printStackTrace();
                        }
//                        long t = System.currentTimeMillis();
//                        if(t-current>=500){
//                            current = t;
//                            System.out.println("Factory report: "+(int)((current-start)));
//                        }
                        break;}

                    case Rocket:{
                        try {

                            //add self to list of rockets
                            if (roundNumber != gc.round()){
                                roundNumber = gc.round();
                                allFriendlyRockets.clear();
                                allFriendlyRockets.add(unit);
                            }else{
                                allFriendlyRockets.add(unit);
                            }

                            if(unit.location().isOnPlanet(Planet.Mars)) {
                                //Unload soldiers
                                VecUnitID garrisonInVec = unit.structureGarrison();
                                int[] garrison = vecUnitIDtoArray(garrisonInVec);
                                for (int botID : garrison){
                                    for (Direction dir : directions){
                                        if (gc.canUnload(uid,dir) && gc.isMoveReady(botID)){
                                            gc.unload(uid,dir);
                                        }
                                    }
                                }
                            }
                            else{
                                VecUnit nearbyFriendliesInVec = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(),2,myteam);
                                VecUnitID garrisonRobotIDs = unit.structureGarrison();

                                MapLocation marsLoc = getRandomMarsLocation(marsMapHeight, marsMapWidth);
                                if (gc.canLaunchRocket(uid, marsLoc) && garrisonRobotIDs.size() > 3) { //check rocket is not empty before launching
                                    gc.launchRocket(uid, marsLoc);
                                    // teammates near the rocket should be informed and should run away before the rocket launches.
                                }else if (garrisonRobotIDs.size() < 8){
                                    System.out.println("current garrison size: " + garrisonRobotIDs.size());
                                    for (int x = 0; x < nearbyFriendliesInVec.size(); x++){
                                        System.out.print("nearbyFriendlies size is" + nearbyFriendliesInVec.size());
                                        Unit friendly = nearbyFriendliesInVec.get(x);
                                        int workerCount = 0;
                                        if (gc.canLoad(uid,friendly.id()) && friendly.unitType() == UnitType.Worker && workerCount <= 2){
                                            System.out.println("loaded worker");
                                            gc.load(uid,friendly.id());
                                            workerCount++;
                                            continue;
                                        }else if(gc.canLoad(uid,friendly.id())){
                                            System.out.println("loaded not worker guy");
                                            gc.load(uid,friendly.id());
                                            continue;
                                        }else{
                                            break;
                                        }
                                    }
                                }
                            }
                        }catch(Exception e){
                            System.out.println("Rocket Exception");
                            e.printStackTrace();
                        }
//                        long t = System.currentTimeMillis();
//                        if(t-current>=500){
//                            current = t;
//                            System.out.println("Rocket report: "+(int)((current-start)));
//                        }
                        break;
                    }

                    case Ranger:{
                        try{
//                            System.out.println("The Ranger is in!");
                            //look for nearest enemy and attack while kiting
                            VecUnit nearbyEnemies = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 70, enemyTeam);
                            if (nearbyEnemies.size() != 0){
                                //can see enemies in range

                                Unit nearestEnemy = getNearestEnemy(unit,nearbyEnemies);

                                Direction dirToEnemy = getDirToTargetMapLocNaive(unit.location().mapLocation(), nearestEnemy.location().mapLocation());
                                Direction dirOppositeOfEnemy = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), nearestEnemy.location().mapLocation());

                                if (gc.isAttackReady(uid)) {
                                    //attack if can
                                    if (gc.canAttack(uid, nearestEnemy.id())) {
                                        gc.attack(uid, nearestEnemy.id());
                                    } else {
                                        //not in range or too close
                                        if (gc.isAttackReady(uid) && gc.isMoveReady(uid)) {

                                            //didnt attack yet
                                            if (gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 10, myteam).size() < 3) {
                                                //too less people, wait in safe location
                                                if (getDistanceTo(unit, nearestEnemy) > 25 && gc.canMove(uid, dirToEnemy)) {
                                                    gc.moveRobot(uid, dirToEnemy);
                                                } else if (getDistanceTo(unit, nearestEnemy) < 25 && gc.canMove(uid, dirOppositeOfEnemy)) {
                                                    gc.moveRobot(uid, dirOppositeOfEnemy);
                                                }
                                            } else {
                                                //enough people
                                                if (getDistanceTo(unit, nearestEnemy) > 10 && gc.canMove(uid, dirToEnemy)) {
                                                    gc.moveRobot(uid, dirToEnemy);
                                                } else if (getDistanceTo(unit, nearestEnemy) < 10 && gc.canMove(uid, dirOppositeOfEnemy)) {
                                                    gc.moveRobot(uid, dirOppositeOfEnemy);
                                                }
                                            }

                                            if (gc.canAttack(uid, nearestEnemy.id())) {
                                                gc.attack(uid, nearestEnemy.id());
                                            }

                                        } else {
                                            //attacked
                                            Direction dirAwayOfEnemy = getDirAwayTargetMapLocGreedy(gc,unit,nearestEnemy.location().mapLocation());
                                            if (gc.isMoveReady(uid) && gc.canMove(uid,dirAwayOfEnemy))
                                                gc.moveRobot(uid,dirAwayOfEnemy);
                                        }
                                    }
                                }

                            }else if(gc.planet() == Planet.Earth && allEarthEnemies.size()!=0){

                                // no enemies nearby, go for a scanned enemy
                                Unit nearestEnemy = getNearestEnemy(unit,allEarthEnemies);

                                Direction dirToTargetLoc = getDirToTargetMapLocGreedy(gc,unit,nearestEnemy.location().mapLocation());

                                if (gc.isMoveReady(uid) && gc.canMove(uid,dirToTargetLoc)) {
                                    gc.moveRobot(uid, dirToTargetLoc);
                                }

                            }else if(gc.planet() == Planet.Mars && allMarsEnemies.size()!=0){

                                // no enemies nearby, go for a scanned enemy
                                Unit nearestEnemy = getNearestEnemy(unit,allMarsEnemies);

                                Direction dirToTargetLoc = getDirToTargetMapLocGreedy(gc,unit,nearestEnemy.location().mapLocation());

                                if (gc.isMoveReady(uid) && gc.canMove(uid,dirToTargetLoc)){
                                    gc.moveRobot(uid,dirToTargetLoc);
                                }

                            }else if(allFriendlyRockets.size() != 0 && gc.planet() != Planet.Mars){

                                Unit nearestRocket = getNearestFriendlyRocket(unit,allFriendlyRockets);

                                Direction dirToRocket = getDirToTargetMapLocGreedy(gc,unit,nearestRocket.location().mapLocation());

                                if (gc.isMoveReady(uid) && gc.canMove(uid,dirToRocket) && getDistanceTo(unit,nearestRocket) != 1){
                                    gc.moveRobot(uid,dirToRocket);
                                }

                            }else{
                                // move away from home factory
                                if(gc.isMoveReady(uid)){
                                    VecUnit myFactories = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 3, UnitType.Factory);
//                                    for(int j=0; j<myFactories.size(); j++){
                                    if (myFactories.size() != 0){
                                        Unit factory = myFactories.get(0);
                                        if(factory.team()==myteam){
                                            Direction dir = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), factory.location().mapLocation());
                                            if(gc.canMove(uid, dir)){
                                                gc.moveRobot(uid, dir);
                                            }else{
                                                //loop random directions until finds one that works
                                                Direction randomDir = getRandomDirection();
                                                for(int r=0; r<10; r++){
                                                    if(gc.canMove(uid,randomDir)){
                                                        gc.moveRobot(uid,randomDir);
                                                        break;
                                                    }
                                                    else
                                                        randomDir=getRandomDirection();
                                                }
                                            }
                                        }
                                    }else{
                                        //loop random directions until finds one that works
                                        Direction randomDir = getRandomDirection();
                                        for(int r=0; r<10; r++){
                                            if(gc.canMove(uid,randomDir)){
                                                gc.moveRobot(uid,randomDir);
                                                break;}
                                            else
                                                randomDir=getRandomDirection();
                                        }
                                    }
                                }
                            }


                        }catch(Exception e){
                            System.out.println("Ranger Exception");
                            e.printStackTrace();
                        }
//                        long t = System.currentTimeMillis();
//                        if(t-current>=500){
//                            current = t;
//                            System.out.println("Ranger report: "+(int)((current-start)));
//                        }
                        break;
                    }

                    case Knight:{
                        try{
//                        System.out.println("The Knight is in!");
//                            VecUnit factoriesInVec = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 50, UnitType.Factory);
//                            ArrayList<Unit> factories = vecUnittoArrayList(factoriesInVec);
//                            while(factories.size()!=0){
//                                Unit attackGoal = getNearestEnemyFactory(unit, factories);
//                                if(attackGoal.team()==myteam){
//                                    factories.remove(0);
//                                    continue;
//                                }
//                                if(noGuardAroundFactory(gc, attackGoal)){
//                                    Direction dir = getDirToTargetMapLocNaive(unit.location().mapLocation(), attackGoal.location().mapLocation());
//                                    if(gc.isMoveReady(uid) && gc.canMove(uid, dir))
//                                        gc.moveRobot(uid, dir);
//                                    if(gc.isAttackReady(uid) && gc.canAttack(uid, attackGoal.id())){
//                                        System.out.println("Knight: attacking");
//                                        gc.attack(uid, attackGoal.id());
//                                    }
//                                    break;
//                                }else{
//                                    int channelNum = findAChannel( gc, 5);//5 is the number for factories
//                                    if(channelNum!=-1) {
//                                        gc.writeTeamArray(channelNum, factories.get(0).id());
//                                    }
//                                    factories.remove(0);
//                                }
//                            }
                            // if there's no factories to meddle with, find some worker to meddle with
                            if(gc.isAttackReady(uid)||gc.isMoveReady(uid)){
                                VecUnit enemy = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 50, enemyTeam);
//                                for(int j=0; j<enemy.size(); j++){
                                if (enemy.size()>0) {
                                    Unit eneBot = enemy.get(0);
                                    //set nearest enemy to current common target
                                    gc.writeTeamArray(98,eneBot.location().mapLocation().getX());
                                    gc.writeTeamArray(99,eneBot.location().mapLocation().getY());
                                    switch (eneBot.unitType()) {
                                        case Knight: {
                                            Direction d = getDirToTargetMapLocNaive(unit.location().mapLocation(), eneBot.location().mapLocation());
                                            if (gc.isMoveReady(uid) && gc.canMove(uid, d) && getDistanceTo(unit, eneBot) > 2)
                                                gc.moveRobot(uid, d);
                                            else if (gc.isAttackReady(uid) && gc.canAttack(uid, eneBot.id()))
                                                gc.attack(uid, eneBot.id());
                                            break;
                                        }
                                        case Worker: {
                                            Direction d = getDirToTargetMapLocNaive(unit.location().mapLocation(), eneBot.location().mapLocation());
                                            if (gc.isMoveReady(uid) && gc.canMove(uid, d) && getDistanceTo(unit, eneBot) > 2)
                                                gc.moveRobot(uid, d);
                                            else if (gc.isAttackReady(uid) && gc.canAttack(uid, eneBot.id()))
                                                gc.attack(uid, eneBot.id());
                                            break;
                                        }
                                        case Ranger: {
                                            float dis = getDistanceTo(unit, eneBot);
                                            if (dis <= Math.sqrt(10)) {
                                                Direction d = getDirToTargetMapLocNaive(unit.location().mapLocation(), eneBot.location().mapLocation());
                                                if (gc.isMoveReady(uid) && gc.canMove(uid, d))
                                                    gc.moveRobot(uid, d);
                                                if (gc.isAttackReady(uid) && gc.canAttack(uid, eneBot.id()))
                                                    gc.attack(uid, eneBot.id());
                                            } else if (dis <= Math.sqrt(50)) {
                                                Direction d = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(),
                                                        eneBot.location().mapLocation());
                                                if (gc.isMoveReady(uid) && gc.canMove(uid, d)) {
                                                    gc.moveRobot(uid, d);
                                                }
                                            }
                                        }
                                        default:
                                            Direction d = getDirToTargetMapLocNaive(unit.location().mapLocation(), eneBot.location().mapLocation());
                                            if (gc.isMoveReady(uid) && gc.canMove(uid, d) && getDistanceTo(unit, eneBot) > 2)
                                                gc.moveRobot(uid, d);
                                            if (gc.isAttackReady(uid) && gc.canAttack(uid, eneBot.id()))
                                                gc.attack(uid, eneBot.id());
                                            break;

                                        //run away if that's a ranger or mage! but if that's a ranger within 10 units it should kill it.
                                    }
                                }else if(gc.planet() == Planet.Earth && teamArray.get(98) != 0 && teamArray.get(99) != 0){
                                    // no enemies nearby and has a common target * on earth *
                                    MapLocation commonTargetLocation = new MapLocation(Planet.Earth,teamArray.get(98),teamArray.get(99));
                                    Direction dirToTargetLoc = getDirToTargetMapLocNaive(unit.location().mapLocation(),commonTargetLocation);
                                    if (gc.isMoveReady(uid)&&gc.canMove(uid,dirToTargetLoc)){
                                        gc.moveRobot(uid,dirToTargetLoc);
                                    }
                                }else if(gc.planet() == Planet.Mars && teamArray.get(98) != 0 && teamArray.get(99) != 0){
                                    //mars
                                    MapLocation commonTargetLocation = new MapLocation(Planet.Mars,teamArray.get(98),teamArray.get(99));
                                    Direction dirToTargetLoc = getDirToTargetMapLocNaive(unit.location().mapLocation(),commonTargetLocation);
                                    if(gc.isMoveReady(uid) && gc.canMove(uid,dirToTargetLoc)){
                                        gc.moveRobot(uid,dirToTargetLoc);
                                    }

                                }else{
                                    // move away from home factory
                                    if(gc.isMoveReady(uid)){
                                        VecUnit myFactories = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 3, UnitType.Factory);
                                        if (myFactories.size() != 0){
                                            Unit factory = myFactories.get(0);
                                            if(factory.team()==myteam){
                                                Direction dir = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), factory.location().mapLocation());
                                                if(gc.canMove(uid, dir)){
                                                    gc.moveRobot(uid, dir);
                                                }else{
                                                    //loop random directions until finds one that works
                                                    Direction randomDir = getRandomDirection();
                                                    for(int r=0; r<10; r++){
                                                        if(gc.canMove(uid,randomDir)){
                                                            gc.moveRobot(uid,randomDir);
                                                            break;}
                                                        else
                                                            randomDir=getRandomDirection();
                                                    }
                                                    //System.gc();
                                                }
                                            }
                                        }else{
                                            //loop random directions until finds one that works
                                            Direction randomDir = getRandomDirection();
                                            for(int r=0; r<10; r++){
                                                if(gc.canMove(uid,randomDir)){
                                                    gc.moveRobot(uid,randomDir);
                                                    break;}
                                                else
                                                    randomDir=getRandomDirection();
                                            }
                                            //System.gc();
                                        }
                                    }
                                }
                            }
                        }catch(Exception e){
                            System.out.println("Knight Exception");
                            e.printStackTrace();
                        }
//                        long t = System.currentTimeMillis();
//                        if(t-current>=500){
//                            current = t;
//                            System.out.println("Knight report: "+(int)((current-start)));
//                        }
                        break;}

                    case Healer:{

                        try{

                            MapLocation myLoc = unit.location().mapLocation();

                            VecUnit nearbyFriendliesInVec = gc.senseNearbyUnitsByTeam(myLoc,50,myteam);
                            Unit[] nearbyFriendlies = vecUnittoArray(nearbyFriendliesInVec);
                            if (nearbyFriendlies.length != 0) {
                                for (Unit teamMate : nearbyFriendlies) {
                                    switch (teamMate.unitType()){
                                        case Worker:
                                            if (teamMate.health() < 100){
                                                //heal
                                                if (getDistanceTo(unit,teamMate) > 30 ){
                                                    //too far to heal, move forward
                                                    Direction dir = getDirToTargetMapLocNaive(myLoc,teamMate.location().mapLocation());
                                                    if (gc.isMoveReady(uid)&&gc.canMove(uid,dir)){
                                                        gc.moveRobot(uid,dir);
                                                    }
                                                }else{
                                                    //heal
                                                    if (gc.isHealReady(uid)&&gc.canHeal(uid,teamMate.id())){
                                                        gc.heal(uid,teamMate.id());
                                                    }
                                                }
                                        }
                                        case Healer:
                                            if (teamMate.health() < 100){
                                                //heal
                                                if (getDistanceTo(unit,teamMate) > 30 ){
                                                    //too far to heal, move forward
                                                    Direction dir = getDirToTargetMapLocNaive(myLoc,teamMate.location().mapLocation());
                                                    if (gc.isMoveReady(uid)&&gc.canMove(uid,dir)){
                                                        gc.moveRobot(uid,dir);
                                                    }
                                                }else{
                                                    //heal
                                                    if (gc.isHealReady(uid)&&gc.canHeal(uid,teamMate.id())){
                                                        gc.heal(uid,teamMate.id());
                                                    }
                                                }
                                        }
                                        case Ranger:
                                            if (teamMate.health() < 200){
                                                //heal
                                                if (getDistanceTo(unit,teamMate) > 30 ){
                                                    //too far to heal, move forward
                                                    Direction dir = getDirToTargetMapLocNaive(myLoc,teamMate.location().mapLocation());
                                                    if (gc.isMoveReady(uid)&&gc.canMove(uid,dir)){
                                                        gc.moveRobot(uid,dir);
                                                    }
                                                }else{
                                                    //heal
                                                    if (gc.isHealReady(uid)&&gc.canHeal(uid,teamMate.id())){
                                                        gc.heal(uid,teamMate.id());
                                                    }
                                                }
                                            }
                                    }
                                }
                                //System.gc();
                            }else if(allFriendlyRockets.size() != 0){

                                Unit nearestRocket = getNearestFriendlyRocket(unit,allFriendlyRockets);

                                Direction dirToRocket = getDirToTargetMapLocGreedy(gc,unit,nearestRocket.location().mapLocation());

                                if (gc.isMoveReady(uid) && gc.canMove(uid,dirToRocket)){
                                    gc.moveRobot(uid,dirToRocket);
                                }

                            }else if(gc.planet() == Planet.Earth && allEarthEnemies.size()!=0){

                                // no enemies nearby, go for a scanned enemy
                                Unit nearestEnemy = getNearestEnemy(unit,allEarthEnemies);

                                Direction dirToTargetLoc = getDirToTargetMapLocGreedy(gc,unit,nearestEnemy.location().mapLocation());

                                if (gc.isMoveReady(uid) && gc.canMove(uid,dirToTargetLoc)){
                                    gc.moveRobot(uid,dirToTargetLoc);
                                }

                            }else if(gc.planet() == Planet.Mars && allMarsEnemies.size()!=0){

                                // no enemies nearby, go for a scanned enemy
                                Unit nearestEnemy = getNearestEnemy(unit,allMarsEnemies);

                                Direction dirToTargetLoc = getDirToTargetMapLocGreedy(gc,unit,nearestEnemy.location().mapLocation());

                                if (gc.isMoveReady(uid) && gc.canMove(uid,dirToTargetLoc)){
                                    gc.moveRobot(uid,dirToTargetLoc);
                                }

                            }else{
                                // move away from home factory
                                if(gc.isMoveReady(uid)){
                                    VecUnit myFactories = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 3, UnitType.Factory);
                                    if (myFactories.size() != 0){
                                        Unit factory = myFactories.get(0);
                                        if(factory.team()==myteam){
                                            Direction dir = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), factory.location().mapLocation());
                                            if(gc.canMove(uid, dir)){
                                                gc.moveRobot(uid, dir);
                                            }else{
                                                // if it is already away from home factory or it's stuck for some reason, let it move randomly.
                                                //loop random directions until finds one that works
                                                Direction randomDir = getRandomDirection();
                                                for(int r=0; r<10; r++){
                                                    if(gc.canMove(uid,randomDir)){
                                                        gc.moveRobot(uid,randomDir);
                                                        break;}
                                                    else
                                                        randomDir=getRandomDirection();
                                                }
                                            }
                                        }
                                    }else{
                                        //loop random directions until finds one that works
                                        Direction randomDir = getRandomDirection();
                                        for(int r=0; r<10; r++){
                                            if(gc.canMove(uid,randomDir)){
                                                gc.moveRobot(uid,randomDir);
                                                break;}
                                            else
                                                randomDir=getRandomDirection();
                                        }
                                    }
                                }
                            }

                            //move randomly at the end of everything to make sure it won't stay at one spot & heal and block everyone
                            if (gc.isMoveReady(uid)){
                                Direction randomDir = getRandomDirection();
                                for(int r=0; r<10; r++){
                                    if(gc.canMove(uid,randomDir)){
                                        gc.moveRobot(uid,randomDir);
                                        break;}
                                    else
                                        randomDir=getRandomDirection();
                                }
                            }

                        }catch(Exception e){

                            System.out.println("Healer Exception");
                            e.printStackTrace();

                        }
//                        long t = System.currentTimeMillis();
//                        if(t-current>=500){
//                            current = t;
//                            System.out.println("Healer report: "+(int)((current-start)));
//                        }
                        break;
                    }

                    case Mage:{
                        try {
                            // find a enemy factory (which is broadcasted by the knight) and attack it
                            // read the broadcast
                            teamArray = gc.getTeamArray(gc.planet());
                            // all the factory channels
                            for (int n = 0; n < 30; n++) {
                                if (teamArray.get(n) != -1 && teamArray.get(n) != 0) {
                                    int enemyId = teamArray.get(n);
                                    //check whether the one is in your attack range
                                    if (gc.isAttackReady(uid) && gc.canAttack(uid, enemyId)) {
                                        // if you can , attack and delete the enemy from the channel
                                        gc.attack(uid, enemyId);
                                        gc.writeTeamArray(n, 0);
                                    }
                                }
                            }

                            //get away from home factory
                            if (gc.isMoveReady(uid)) {
                                VecUnit myFactories = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 3, UnitType.Factory);
                                if (myFactories.size() != 0){
                                    Unit factory = myFactories.get(0);
                                    if (factory.team() == myteam) {
                                        Direction dir = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), factory.location().mapLocation());
                                        if (gc.isMoveReady(uid) && gc.canMove(uid, dir)) {
                                            gc.canMove(uid, dir);
                                        }
                                        // i should write a function in case it can't move in that dir and get stuck
                                    } else {
                                        if (gc.isAttackReady(uid)&&gc.canAttack(uid, factory.id())) {
                                            gc.attack(uid, factory.id());
                                        }
                                    }
                                }
                            }
                            // if it is already away from home factory or it's stuck for some reason, let it move randomly.
                            if (gc.isMoveReady(uid)) {
                                Direction d = getRandomDirection();
                                if (gc.canMove(uid, d))
                                    gc.moveRobot(uid, d);
                            }
                        }catch(Exception e){
                            System.out.println("Mage Exception");
                            e.printStackTrace();
                        }
//                        long t = System.currentTimeMillis();
//                        if(t-current>=500){
//                            current = t;
//                            System.out.println("Healer report: "+(int)((current-start)));
//                        }
                        break;
                    }

                }
            }
            //System.gc();
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}
