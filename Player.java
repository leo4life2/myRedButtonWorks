// import the API.
// See xxx for the javadocs.
import bc.*;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    public static Random RAND = new Random();

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
        return (float)Math.sqrt(u1.location().mapLocation().distanceSquaredTo(u2.location().mapLocation()));
    }
    public static float getDistanceTo(Unit u1, MapLocation u2){
        return (float)Math.sqrt(u1.location().mapLocation().distanceSquaredTo(u2));
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
//    public static VecUnit senseNearbyUnitsByTeamAndType(GameController gc, Unit me, int radius, UnitType type, Team team){
//        VecUnit byType = gc.senseNearbyUnitsByType(me.location().mapLocation(), radius, type);
//        for(int i =0; i<byType.size(); i++){
//            Unit unit = byType.get(i);
//            if(unit.team()!=team){
//                byType.delete();
//            }
//        }
//    }

    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        /* about team array
            0-29: enemy factory    //knight can take care if the factory isn't surrounded by enemies
            30-79: enemy ranger
            80-99: healer enemy     //knight will kill the healer
         */

        // Connect to the manager, starting the game
        GameController gc = new GameController();
        Team myteam = gc.team();
        Team enemyTeam;
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
        // is Earth and Mars always in the same size?
        int earthMapHeight = (int)(EarthMap.getHeight());
        int earthMapWidth = (int)(EarthMap.getWidth());
        int marsMapHeight = (int)(MarsMap.getHeight());
        int marsMapWidth = (int)(MarsMap.getWidth());
        int earthMapArea = earthMapHeight*earthMapWidth;
        int marsMapArea = marsMapHeight*marsMapWidth;

        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Mage);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Healer);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Mage);
        gc.queueResearch(UnitType.Healer);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Healer);
        gc.queueResearch(UnitType.Mage);
        gc.queueResearch(UnitType.Mage);

        int limit_factory = 3;//(int)(0.05f*marsMapArea);
        int limit_rocket = 3;
        int limit_worker = 5;//(int)(0.01f*earthMapArea);
        int limit_knight = 10;//(int)(0.02f*earthMapArea);
        int limit_ranger = 5;//(int)(0.04f*earthMapArea);


        while (true) {
            // for each round
            System.out.println("Current round: "+gc.round());
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
                switch(unit.unitType()){
                    case Worker:{
                        try {
//                            System.out.println("The Worker is running!");
                            //current status
                            boolean retreating = false; //running away from an enemy knight
                            boolean leavingHome = false; //running away from our factory
                            boolean building = false; //building or blueprinting

                            if (n_worker <= limit_worker) {//self-reproduction
                                for (int j = 0; j < directions.length; j++) {
                                    Direction d = directions[j];
                                    if (gc.canReplicate(uid, d)) {
                                        gc.replicate(uid, d);
//                                        System.out.println("I'm replicating myself!");
                                        break;
                                    }
                                }
                            }

                            // build rockets!
                            if(n_rocket<=limit_rocket && gc.researchInfo().getLevel(UnitType.Rocket)>=1){
//                                System.out.println("trying to blueprint rocket");
                                for (Direction d : directions) {
                                    if (gc.canBlueprint(uid, UnitType.Rocket, d)) {
                                        gc.blueprint(uid, UnitType.Rocket, d);
//                                        System.out.println("I'm blueprinting a rocket!");
                                        building = true;
                                        break;
                                    }
                                }
                            }

                            // build factories
                            // first, blueprint the factory

                            // will blueprint add numbers to n_factory? if not, it'd better broadcast the news to the team
                            for (int j = 0; j < directions.length; j++) {
                                Direction d = directions[j];
                                if (n_factory <= limit_factory && karbonite >= 100 && gc.canBlueprint(uid, UnitType.Factory, d)) {
                                    gc.blueprint(uid, UnitType.Factory, d);
//                                    System.out.println("I'm blueprinting a factory!");
                                    building = true;
                                    break;
                                }
                            }

                            // then build it
                            VecUnit nearbyUnits = gc.senseNearbyUnits(maploc, 2);
                            for (int j = 0; j < nearbyUnits.size(); j++) {
                                Unit other = nearbyUnits.get(j);
                                if (gc.canBuild(uid, other.id())) {
                                    gc.build(uid, other.id());
//                                    System.out.println("I'm building a factory!");
                                    building = true;
                                    break;
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
//                                            System.out.println("move by retreat from knight");
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
//                                        System.out.println("entered escape rocket block");
                                        Direction d = getDirAwayFromTargetMapLocNaive(maploc, nearbyRockets.get(0).location().mapLocation());
//                                        System.out.println("can move is" + gc.canMove(uid, d));
//                                        System.out.println(" move ready is" + gc.isMoveReady(uid));
                                        if (gc.canMove(uid, d) && gc.isMoveReady(uid)) {
                                            leavingHome = true;
//                                            System.out.println("move by escape from rocket");
                                            gc.moveRobot(uid, d);
                                        }
                                    } else if (!leavingHome) {
//                                        System.out.println("leaving home block");
//                                 find a proper factorying place or something
//                                 or check whether new desposit is discovered? or just move around a chill for a while?
                                        Direction d = getRandomDirection();
//                                        System.out.println("leaving home can move is" + gc.canMove(uid, d));
//                                        System.out.println("leaving home move ready is" + gc.isMoveReady(uid));
                                        if (gc.isMoveReady(uid) && gc.canMove(uid, d)) {
//                                            System.out.println("move by random and !leavinghome");
                                            gc.moveRobot(uid, d);
                                        }
                                    }
                                }
                            }

                            // harvest karbonites
                            for (int j = 0; j < directions.length; j++) {
                                Direction d = directions[j];
                                if (gc.canHarvest(uid, d)) {
                                    gc.harvest(uid, d);
                                    break;
                                }
                            }
                        }catch(Exception e){
                            System.out.println("Worker Exception");
                            e.printStackTrace();
                        }
                        break;}

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
                            if (n_knight < limit_knight) {
                                if (gc.canProduceRobot(uid, UnitType.Knight)) {
                                    gc.produceRobot(uid, UnitType.Knight);
                                }
                            } else if (n_ranger < limit_ranger){ //produce rangers if not enough
                                if (gc.canProduceRobot(uid, UnitType.Ranger)){
                                    gc.produceRobot(uid,UnitType.Ranger);
                                }
                            } else {// randomly produce knights, mages and rangers
                                if (Math.random()<0.16) {
//                                    System.out.print("Trying to produce Mage...");
                                    if (gc.canProduceRobot(uid, UnitType.Mage)) {
                                        gc.produceRobot(uid, UnitType.Mage);
//                                        System.out.println("Mage born.");
                                    }
                                }else if(Math.random()>0.16 && Math.random()<0.33 && gc.round() > 25){ //only make healers after round 25 to ensure there are combat robots already
                                    if (gc.canProduceRobot(uid, UnitType.Healer)){
                                        gc.produceRobot(uid,UnitType.Healer);
                                    }
                                } else if (Math.random()>0.33 && Math.random()<0.66) {
                                    if (gc.canProduceRobot(uid, UnitType.Knight)) {
                                        gc.produceRobot(uid, UnitType.Knight);
                                    }
                                } else {
                                    if (gc.canProduceRobot(uid, UnitType.Ranger)){
                                        gc.produceRobot(uid, UnitType.Ranger);
                                    }
                                }
                            }
                        }catch(Exception e){
                            System.out.println("Factory Exception");
                            e.printStackTrace();
                        }
                        break;}

                    case Rocket:{
                        try {
                            if(unit.location().isOnPlanet(Planet.Mars)) {
                                //Unload soldiers
                                VecUnitID garrisonInVec = unit.structureGarrison();
                                int[] garrison = vecUnitIDtoArray(garrisonInVec);
                                for (int botID : garrison){
                                    for (Direction dir : directions){
                                        if (gc.canUnload(uid,dir)){
                                            gc.unload(uid,dir);
                                        }
                                    }
                                }
                            }
                            else{
                                VecUnit nearbyFriendliesInVec = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(),3,myteam);
                                VecUnitID garrisonRobotIDs = unit.structureGarrison();
                                Unit[] nearbyFriendlies = vecUnittoArray(nearbyFriendliesInVec);

                                //Loading soldiers
                                for (Unit friendly : nearbyFriendlies){
                                    if (gc.canLoad(uid,friendly.id()) && friendly.unitType() != UnitType.Worker){
                                        gc.load(uid,friendly.id());
                                    }
                                }

                                MapLocation marsLoc = getRandomMarsLocation(marsMapHeight, marsMapWidth);
                                if (gc.canLaunchRocket(uid, marsLoc)) {
                                    System.out.println("Rocket is launched");
                                    gc.launchRocket(uid, marsLoc);
                                    // teammates near the rocket should be informed and should run away before the rocket launches.
                                }
                            }
                        }catch(Exception e){
                            System.out.println("Rocket Exception");
                            e.printStackTrace();
                        }
                        break;
                    }

                    case Ranger:{
                        try{
//                            System.out.println("The Ranger is in!");
                            //look for nearest enemy and attack while kiting
                            VecUnit nearbyEnemies = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 70, enemyTeam);
                            ArrayList<Unit> nearbyEnemiesAsArrayList = vecUnittoArrayList(nearbyEnemies);
                            if (nearbyEnemiesAsArrayList.size() != 0){
                                Unit nearestEnemy = getNearestEnemy(unit, nearbyEnemies);

                                Direction dirToEnemy = getDirToTargetMapLocNaive(unit.location().mapLocation(), nearestEnemy.location().mapLocation());
                                Direction dirOppositeOfEnemy = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), nearestEnemy.location().mapLocation());

                                if (gc.isAttackReady(uid) && gc.canAttack(uid, nearestEnemy.id())){
                                    gc.attack(uid, nearestEnemy.id());
                                }else if(gc.isMoveReady(uid)){
                                    if (getDistanceTo(unit, nearestEnemy) > 10 && gc.canMove(uid, dirToEnemy)) {
                                        gc.moveRobot(uid, dirToEnemy);
                                    } else if (getDistanceTo(unit, nearestEnemy) < 10 && gc.canMove(uid, dirOppositeOfEnemy)) {
                                        gc.moveRobot(uid, dirOppositeOfEnemy);
                                    }
                                }

                            }else{
                                // move away from home factory
                                if(gc.isMoveReady(uid)){
                                    VecUnit myFactories = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 3, UnitType.Factory);
                                    for(int j=0; j<myFactories.size(); j++){
                                        Unit factory = myFactories.get(j);
                                        if(factory.team()==myteam){
                                            Direction dir = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), factory.location().mapLocation());
                                            if(gc.isMoveReady(uid) && gc.canMove(uid, dir)){
                                                gc.moveRobot(uid, dir);
                                            }
                                            // i should write a function in case it can't move in that dir and get stuck
                                        }
                                    }
                                }
                                // if it is already away from home factory or it's stuck for some reason, let it move randomly.
                                if(gc.isMoveReady(uid)){
                                    Direction d = getRandomDirection();
                                    if(gc.canMove(uid, d))
                                        gc.moveRobot(uid, d);
                                }
                            }

                        }catch(Exception e){
                            System.out.println("Ranger Exception");
                            e.printStackTrace();
                        }
                        break;
                    }

                    case Knight:{
                        try{
//                        System.out.println("The Knight is in!");
                            VecUnit factoriesInVec = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 50, UnitType.Factory);
                            ArrayList<Unit> factories = vecUnittoArrayList(factoriesInVec);
                            while(factories.size()!=0){
                                Unit attackGoal = getNearestEnemyFactory(unit, factories);
                                if(attackGoal.team()==myteam){
                                    factories.remove(0);
                                    continue;
                                }
                                if(noGuardAroundFactory(gc, attackGoal)){
                                    Direction dir = getDirToTargetMapLocNaive(unit.location().mapLocation(), attackGoal.location().mapLocation());
                                    if(gc.isMoveReady(uid) && gc.canMove(uid, dir))
                                        gc.moveRobot(uid, dir);
                                    if(gc.isAttackReady(uid) && gc.canAttack(uid, attackGoal.id())){
//                                        System.out.println("Knight: attacking");
                                        gc.attack(uid, attackGoal.id());
                                    }
                                    break;
                                }else{
                                    int channelNum = findAChannel( gc, 5);//5 is the number for factories
                                    if(channelNum!=-1) {
                                        gc.writeTeamArray(channelNum, factories.get(0).id());
                                    }
                                    factories.remove(0);
                                }
                            }
                            // if there's no factories to meddle with, find some worker to meddle with
                            if(gc.isAttackReady(uid)||gc.isMoveReady(uid)){
                                VecUnit enemy = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 50, enemyTeam);
                                for(int j=0; j<enemy.size(); j++){
                                    Unit eneBot = enemy.get(j);
                                    switch (eneBot.unitType()){
                                        case Knight:{
                                            Direction d = getDirToTargetMapLocNaive(unit.location().mapLocation(), eneBot.location().mapLocation());
                                            if(gc.isMoveReady(uid)&&gc.canMove(uid, d))
                                                gc.moveRobot(uid, d);
                                            if(gc.isAttackReady(uid)&&gc.canAttack(uid, eneBot.id()))
                                                gc.attack(uid, eneBot.id());
                                            break;
                                        }
                                        case Worker:{
                                            Direction d = getDirToTargetMapLocNaive(unit.location().mapLocation(), eneBot.location().mapLocation());
                                            if(gc.isMoveReady(uid)&&gc.canMove(uid, d))
                                                gc.moveRobot(uid, d);
                                            if(gc.isAttackReady(uid)&&gc.canAttack(uid, eneBot.id()))
                                                gc.attack(uid, eneBot.id());
                                            break;
                                        }
                                        case Ranger:{
                                            float dis = getDistanceTo(unit, eneBot);
                                            if(dis<=Math.sqrt(10)){
                                                Direction d = getDirToTargetMapLocNaive(unit.location().mapLocation(), eneBot.location().mapLocation());
                                                if(gc.isMoveReady(uid)&&gc.canMove(uid, d))
                                                    gc.moveRobot(uid, d);
                                                if(gc.isAttackReady(uid)&&gc.canAttack(uid, eneBot.id()))
                                                    gc.attack(uid, eneBot.id());
                                            }else if (dis<=Math.sqrt(50)){
                                                Direction d = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(),
                                                        eneBot.location().mapLocation());
                                                if(gc.isMoveReady(uid)&&gc.canMove(uid, d)){
                                                    gc.moveRobot(uid, d);
                                                }
                                            }
                                        }
                                        //run away if that's a ranger or mage! but if that's a ranger within 10 units it should kill it.
                                    }
                                }
                            }
                            // move away from home factory
                            if(gc.isMoveReady(uid)){
                                VecUnit myFactories = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 3, UnitType.Factory);
                                for(int j=0; j<myFactories.size(); j++){
                                    Unit factory = myFactories.get(j);
                                    if(factory.team()==myteam){
                                        Direction dir = getDirAwayFromTargetMapLocNaive(unit.location().mapLocation(), factory.location().mapLocation());
                                        if(gc.isMoveReady(uid) && gc.canMove(uid, dir)){
                                            gc.canMove(uid, dir);
                                        }
                                        // i should write a function in case it can't move in that dir and get stuck
                                    }
                                }
                            }
                            // if it is already away from home factory or it's stuck for some reason, let it move randomly.
                            if(gc.isMoveReady(uid)){
                                Direction d = getRandomDirection();
                                if(gc.canMove(uid, d))
                                    gc.moveRobot(uid, d);
                            }
                        }catch(Exception e){
                            System.out.println("Knight Exception");
                            e.printStackTrace();
                        }
                        break;}

                    case Healer:{

                        try{

                            MapLocation myLoc = unit.location().mapLocation();

                            VecUnit nearbyFriendliesInVec = gc.senseNearbyUnitsByTeam(myLoc,50,myteam);
                            Unit[] nearbyFriendlies = vecUnittoArray(nearbyFriendliesInVec);
                            if (nearbyFriendlies.length != 0) {
                                for (Unit teamMate : nearbyFriendlies) {
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

                        }catch(Exception e){

                            System.out.println("Healer Exception");
                            e.printStackTrace();

                        }
                        break;
                    }

                    case Mage:{
                        try {
//                            System.out.println("The Mage is in!");
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
//                                        System.out.println("Mage: attacking");
                                        gc.attack(uid, enemyId);
                                        gc.writeTeamArray(n, 0);
                                    }
                                }
                            }

                            //get away from home factory
                            if (gc.isMoveReady(uid)) {
                                VecUnit myFactories = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 3, UnitType.Factory);
                                for (int j = 0; j < myFactories.size(); j++) {
                                    Unit factory = myFactories.get(j);
                                    if (factory.team() == myteam) {
//                                        System.out.println("bye home factory!");
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
                        break;
                    }

                }
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}
