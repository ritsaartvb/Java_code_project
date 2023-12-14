import java.util.ArrayList;
import java.util.Arrays;

public class Excecuter extends VEnergia {
    private static final double[][] Precios = new double[][]{{400.0D, 300.0D, 50.0D}, {500.0D, 400.0D, 50.0D}, {600.0D, 500.0D, 50.0D}};
    private static final double[][] Costes = new double[][]{{50.0D, 20000.0D, 15000.0D}, {80.0D, 10000.0D, 5000.0D}, {150.0D, 5000.0D, 1500.0D}};
    private static final double[][] Perdida = new double[][]{{10.0D, 0.0D}, {25.0D, 0.1D}, {50.0D, 0.2D}, {75.0D, 0.4D}, {1000.0D, 0.6D}};

    public Excecuter() {
    }

    public double getDistance(Client client, Centrales plant, int indexC, int indexP) {
        return plant.getDistances().get(indexP).get(indexC);
    }


    public double getCost(Centrales plant) throws Exception {
        double cost = 0.0;
        for (int i = 0; i < plant.getSize(); i++) {
            if (plant.getClientList().get(i).size() > 1) {    //checks if plant was used
                int index = plant.getClientList().get(i).get(0);
                double prod = plant.get(index).getProduccion();
                int tipo = plant.get(index).getTipo();
                double marcha = getCosteMarcha(tipo);
                double parada = getCosteParada(tipo);
                double costProd = prod * getCosteProduccionMW(tipo);
                cost += (marcha + parada + costProd);
            }
        }
        return cost;
    }

    public double getIncome(Client client) throws Exception {
        double income = 0.0;
        for (int i = 0; i < client.getSize(); i++) {
            double tarifa = 0.0;
            if (client.getPlantList().get(i).get(1) >= 0) {
                int index = client.getPlantList().get(i).get(0);
                int tipo = client.get(index).getTipo();
                int contracto = client.get(index).getContrato();
                if (contracto == 0) {
                    tarifa = getTarifaClienteGarantizada(tipo);
                } else {
                    tarifa = getTarifaClienteNoGarantizada(tipo);
                }
                income += tarifa;
            }
        }
        return income;
    }
    public void intialiseState(Client clients, Centrales centrales) {
        // Creating and setting up our centrales and clients
        prepareClients(clients);
        prepareCentrales(centrales, clients);
        assignClientsToPlants(clients, centrales);
    }
    
    // Prepare the clients by creating demand list, sorting it, and making the plant list
    private void prepareClients(Client clients) {
        clients.makeDemandList();
        int[] y = clients.prepareSort();
        clients.mergeSort(y, clients.getSize());
        clients.sortDemandList();
        clients.makePlantList();
    }

    
    
    // Prepare the centrales by creating the supply list, getting client size, sorting the supply list, making the client list, and calculating distances
    private void prepareCentrales(Centrales centrales, Client clients) {
        centrales.makeSupplyList();
        centrales.getClientSize(clients);
        int[] x = centrales.prepareSort();
        centrales.mergeSort(x, centrales.getSize());
        centrales.sortSupplyList();
        centrales.makeClientList();
        centrales.makeDistances(clients);
    }
    
    // Assign clients to plants based on demand, supply, and other conditions
    private void assignClientsToPlants(Client clients, Centrales centrales) {
        int pSize = centrales.getSize();
        int cSize = clients.getSize();

        //Setting the biggest centrales as our starting plant, this will be the first one we try to fill
        double[] current = new double[2];
        current = centrales.getSupplyList()[pSize-1];
        int c = 0;

        //Setting up copies of the demand and supply lists so that we don't actually modify the original lists
        double[][] sListCopy = new double[pSize][2];
        for (int i = 0; i < pSize; i++) {
            double[] tup = centrales.getSupplyList()[i].clone();
            sListCopy[i] = tup.clone();
        }

        ArrayList<ArrayList<Double>> dListCopy = new ArrayList<ArrayList<Double>>(cSize);
        for (int i = 0; i < cSize; i++) {
            dListCopy.add((ArrayList<Double>) clients.getDemandList().get(i).clone());
        }
        //------------------------------------------------------------------------------------------------------------//
        // Starting the assignment of clients, we try to fill up each plant(in descending order), assigning bigger clients before trying smaller ones
        for (int i = pSize - 1; i >= 0; i--) {
            current = sListCopy[i];
            c = 0;
            while (sListCopy[i][1] != 0.0 && c < cSize) {
                for (int j = cSize - 1; j >= 0; j--) {
                    int index = (int) (clients.getDemandList().get(j).get(0) * 100 / 100);
                    if (clients.get(index).getContrato() == 0) {
                        double dist = getDistance(clients, centrales, (int) (dListCopy.get(j).get(0) * 100 / 100), (int) sListCopy[i][0]);
                        double loss = getPerdida(dist);
                        if (dListCopy.get(j).get(1) + loss <= current[1]) {
                            centrales.addClient((int) (dListCopy.get(j).get(0) * 100 / 100), i);
                            sListCopy[i][1] -= (dListCopy.get(j).get(1) + loss);
                            clients.assignClient(j, (int) sListCopy[i][0]);
                            dListCopy.get(j).remove(1);
                            dListCopy.get(j).add(9999.00);//This is to make sure we won't assign this client again
                        }
                    }
                    c++;
                }
            }
        }
        for (int i = 0; i < pSize; i++) {
            centrales.setSupplyListCopyI(sListCopy[i][0], sListCopy[i][1]);
        }

    }
    




    public double getOccupation(Client clients, Centrales centrales, int c, int p) {
        double dist = getDistance(clients, centrales, c, p);
        double loss = getPerdida(dist);
        int index = getSortedClientIndex(clients, centrales, c);
        double demand = clients.getDemandList().get(index).get(1);

        return demand + loss;
    }

    public double getProfit(Client client, Centrales plant) throws Exception {
        return getIncome(client) - getCost(plant);
    }


    public double[] getSpaceDemand(Centrales centrales, Client clients, int A, int B) {

        //Getting the index of the client within the PlantList and DemandList
        int indexCA = getSortedClientIndex(clients, centrales, A);




        //Getting the index of the plant in SupplyList
        int indexA = getSortedPlantIndex(centrales, B);


        //Getting the remaining space of the plant
        double spaceA = centrales.getSupplyListCopy()[B][1];


        //Getting the demand of the client
        double demandA = clients.getDemandList().get(indexCA).get(1);


        double[] tup = new double[]{spaceA, demandA};

        return tup;

    }



    public boolean checkSwap(Centrales centrales, Client clients, int A, int B) {
        // Cannot swap with itself
        if (A != B) {



            int indexA = getSortedClientIndex(clients, centrales, A);
            int indexB = getSortedClientIndex(clients, centrales, B);
            // Cannot swap if either A or B has not been assigned
            if (clients.getPlantList().get(indexA).get(1) < 0 || clients.getPlantList().get(indexB).get(1) < 0) {
                return false;
            }



            if (indexA < 0 || indexB < 0) {
                return false;
            }

            int plantA = clients.getPlantList().get(indexA).get(1);
            int plantB = clients.getPlantList().get(indexB).get(1);

            if (centrales.getSupplyListCopy()[plantB][1] + getOccupation(clients, centrales, B, plantB) < getOccupation(clients, centrales, A, plantB) || centrales.getSupplyListCopy()[plantA][1] + getOccupation(clients, centrales, A, plantA) < getOccupation(clients, centrales, B, plantA) ) {
                return false;
            }


            if ((getOccupation(clients, centrales, A, plantA) - getOccupation(clients, centrales, A, plantB)) + (getOccupation(clients, centrales, B, plantB) - getOccupation(clients, centrales, B, plantA)) <= 0) {
                return false;
            }
            if (plantA == plantB) {
                return false;
            }



            double spaceA = getSpaceDemand(centrales, clients, A, plantA)[0];
            double demandA = getSpaceDemand(centrales, clients, A, plantA)[1];
            double spaceB = getSpaceDemand(centrales, clients, B, plantB)[0];
            double demandB = getSpaceDemand(centrales, clients, B, plantB)[1];

            if (demandA <= (demandB + spaceB) && demandB <= (demandA + spaceA)) {

                return true;
            } else {
                return false;
            }

        }
        return false;
    }

    public void swap(Centrales centrales, Client clients, int A, int B) {
        if (checkSwap(centrales, clients, A, B)) {

            int indexPA = clients.getPlantList().get(getSortedClientIndex(clients, centrales, A)).get(1);
            int indexPB = clients.getPlantList().get(getSortedClientIndex(clients, centrales, B)).get(1);

            remove(centrales, clients, A);

            remove(centrales, clients, B);


            int indexPA2 = clients.getPlantList().get(getSortedClientIndex(clients, centrales, A)).get(1);

            int indexPB2 = clients.getPlantList().get(getSortedClientIndex(clients, centrales, B)).get(1);



            add(centrales, clients, A, indexPB);

            int indexPA3 =clients.getPlantList().get(getSortedClientIndex(clients, centrales, A)).get(1);

            add(centrales, clients, B, indexPA);

            int indexPB3 = clients.getPlantList().get(getSortedClientIndex(clients, centrales, B)).get(1);



        }
    }


    public void remove(Centrales centrales, Client clients, int a) {
        int indexC = getSortedClientIndex(clients, centrales, a);
        int indexP = clients.getPlantList().get(indexC).get(1);
        clients.getPlantList().get(indexC).remove(1);
        clients.getPlantList().get(indexC).add(-1);
        int iP = getSortedPlantIndex(centrales, indexP);
        for (int i = 1; i < centrales.getClientList().get(iP).size(); i++) {
            if (centrales.getClientList().get(iP).get(i) == a) {
                centrales.getClientList().get(iP).remove(i);

            }
        }
        double occupation = getOccupation(clients, centrales, a, indexP);


        centrales.setSupplyListCopy(occupation, indexP, this);
    }

    public boolean checkAdd(Centrales centrales, Client clients, int c, int p){
        int indexC= getSortedClientIndex(clients, centrales, c);
        double occupation = getOccupation(clients, centrales, c, p);
        if (centrales.getSupplyListCopy()[p][1] >= occupation && clients.getPlantList().get(indexC).get(1) == -1) {
            return true;
        }
        else{
            return false;
        }
    }


    public void add(Centrales centrales, Client clients, int c, int p) {
        double space = getSpaceDemand(centrales, clients, c, p)[0];
        double demand = getSpaceDemand(centrales, clients, c, p)[1];
        int indexC= getSortedClientIndex(clients, centrales, c);
        double occupation = getOccupation(clients, centrales, c, p);

        if (centrales.getSupplyListCopy()[p][1] >= occupation && clients.getPlantList().get(indexC).get(1) == -1) {

            centrales.addClient(c, getSortedPlantIndex(centrales, p));
            clients.assignClient(getSortedClientIndex(clients, centrales, c), p);
        }

        centrales.setSupplyListCopy(-occupation, p, this);
    }


    public int getSortedPlantIndex(Centrales centrales, int a) {
        for (int i = 0; i < centrales.getClientList().size(); i++) {
            if (centrales.getClientList().get(i).get(0) == a) {
                return i;
            }
        }
        return -1;
    }

    public int getSortedClientIndex(Client clients, Centrales centrales, int a) {
        for (int i = 0; i < clients.getPlantList().size(); i++) {
            if (clients.getPlantList().get(i).get(0) == a) {
                return i;
            }
        }
        return -1;
    }

    public void hillClimbingSwap(Centrales centrales, Client clients) throws Exception {
        double current = centrales.getTotalProfit();
        boolean fin = false;

        while (!fin) {
            for (int i = 0; i < clients.getSize(); i++) {
                for (int j = 0; j < clients.getSize(); j++) {
                    swap(centrales, clients, i, j);


                    centrales.makeProfitList(clients);
                    double profit = centrales.getTotalProfit();
                    if (profit > current) {
                        current = profit;
                    }
                    swap(centrales, clients, i, j);
                }
            }
            fin = true;
        }


    }



    public void hillClimbing(Centrales centrales, Client clients) throws Exception {
        int adds = 0;
        centrales.calcUnused();

        //Setting up variables
        double h = 0.0;
        double avI = averageClientIncome(clients); 
        double avD; // average distance between client and plant
        int c1;
        int c2;
        int p1;
        int p2;
        int iPerUnit = 470;
        double unused = centrales.getUnused();
        double avLoss;
        double incomeC1;
        double ratio;
        double occ;
        double costP;
        double potentialIncome;
        boolean fin = false;
        double k = 1.5;
        double estIncome;
        int unAssigned = 0;
        for (int i = 0; i < clients.size(); i++) {
            if (!(isAssigned(clients, i))) {
                unAssigned++;
            }
        }


        //

        while (!fin) {
            int count=0;
            int iAN = 0;
            int iA = 0;
            int s = 0;
            while(iAN <clients.getSize()) {
                //execute all possible adds------------------------------------------------------------------------
                while (iA < clients.getSize()) {

                    //execute all possible swaps------------------------------------------------------------------------
                    while (s < clients.getSize()) {
                        centrales.calcUnused();
                        unused = centrales.getUnused();
                        int j = 0;
                        int c = 0;
                        while (j < clients.getSize()) {

                            swap(centrales, clients, s, j);
                            centrales.calcUnused();
                            double newUnused = centrales.getUnused();
                            h = newUnused - unused;
                            if (h > 0) {
                                count++;
                                unused = newUnused;
                                j = clients.getSize();
                            } else {
                                swap(centrales, clients, j, s);
                                centrales.calcUnused();
                                c++;
                            }
                            j++;
                        }
                        if (c == clients.getSize()) {
                            s++;
                        }
                    }
                    //End of swap loop--------------------------------------------------------------------------------------



                    //start of addition---------------------------------------------------------------------------------
                    int jA = 0;
                    int cA = 0;
                    while (jA < centrales.getSize()) {
                        centrales.calcUnused();
                        unused = centrales.getUnused();
                        c1 = iA;
                        p1 = jA;
                        avLoss = averageClientLoss(centrales, clients);
                        incomeC1 = getClientIncome(clients, c1);

                        occ = getOccupation(clients, centrales, c1, p1);
                        ratio = occ / unused;
                        estIncome = unused * iPerUnit * avLoss;
                        if (checkAdd(centrales, clients, iA, jA) && centrales.getClientList().get(getSortedPlantIndex(centrales, jA)).size() > 1) {
                            add(centrales, clients, iA, jA);

                            boolean check = (centrales.getClientList().get(getSortedPlantIndex(centrales, jA)).size() > 1);


                            h = incomeC1 * k - (ratio * estIncome);

                            if (h > 0 && check) {
                                s = 0;
                                adds++;
                                unAssigned--;
                                jA = clients.getSize();
                            } else {
                                remove(centrales, clients, iA);
                                cA++;
                            }
                        } else {
                            cA++;

                        }
                        jA++;
                        if (cA == centrales.getSize()) {
                            iA++;
                        }

                    }
                }//end of adds loop------------------------------------------------------------------------

                //start of addition to new centrales---------------------------------------------------------------------------------
                int jAN = 0;
                int cAN = 0;


                while (jAN < centrales.getSize()) {

                    c1 = iAN;
                    incomeC1 = getClientIncome(clients, c1);
                    costP = potentialCostOfPlant(centrales, jAN); // getCost
                    boolean ca= checkAdd(centrales, clients, iAN, jAN);
                    if (ca && centrales.getClientList().get(getSortedPlantIndex(centrales, jAN)).size() == 1) {
                        add(centrales, clients, iAN, jAN);

                        boolean check = !(centrales.getClientList().get(getSortedPlantIndex(centrales, jAN)).size() == 1);
                        h = unAssigned * avI - (costP - incomeC1);
                        if (h > 0 && check) {
                            s = 0;
                            iA = 0;
                            unAssigned--;
                            jAN = clients.getSize();

                        }
                        else {
                            remove(centrales, clients, iAN);
                            cAN++;
                        }


                    }
                    else {
                        cAN++;

                    }
                    jAN++;
                    if (cAN == centrales.getSize()) {
                        iAN++;
                    }

                }

            }
            fin = true;
        }
    }

    public double averageClientIncome(Client clients) throws Exception {
        double income = 0.0;
        for (int i = 0; i < clients.getSize(); i++) {
            income += this.getClientIncome(clients, i);
        }
        double averageIncome = income / clients.getSize();
        return averageIncome;
    }

    public boolean isAssigned(Client clients, int c) {
        if (clients.getPlantList().get(c).get(1)>=0) {
            return true;
        }
        else { return false;}
    }

    public boolean hasClients(Centrales centrales, int p) {
        if (centrales.getClientList().get(p).size()>1) {
            return true;
        }
        else { return false;}
    }

    public double averageAssignedDistance(Centrales centrales, Client clients) {
        centrales.makeDistances(clients);
        int totalAssignedDistance = 0;
        for (int i = 0; i < centrales.getSize(); i++) {
            for (int j = 0; j < clients.getSize(); j++) {
                if (clients.getPlantList().get(j).get(1)==i) {
                    totalAssignedDistance += centrales.getDistances().get(i).get(j);
                }
            }
        }
        int usedClients=0;
        for (int i=0; i<clients.getSize(); i++) {
            if (isAssigned(clients, i)) {
                usedClients++;
            }
        }
        totalAssignedDistance = totalAssignedDistance / usedClients;
        return totalAssignedDistance;
    }

    public double averageDistance(Centrales centrales, Client clients) {
        int totalDistance = 0;
        for (int i = 0; i < centrales.getSize(); i++) {
            for (int j = 0; j < clients.getSize(); j++) {
                totalDistance += centrales.getDistances().get(i).get(j);
            }
        }
        totalDistance = totalDistance / (centrales.getSize() * clients.getSize());
        return totalDistance;
    }

    public double averageClientLoss(Centrales centrales, Client clients) {
        double averageDistance=this.averageDistance(centrales, clients);
        if (averageDistance<10.0) {
            return 1;
        }
        else if (averageDistance<25.0) {
            return 0.9;
        }
        else if (averageDistance<50.0) {
            return 0.8;
        }
        else if (averageDistance<75.0) {
            return 0.6;
        }
        else {
            return 0.4;
        }
    }

    public double getClientIncome(Client clients, int c) throws Exception {
        double tarifa = 0.0;
        int tipo = clients.get(c).getTipo();
        int contrato = clients.get(c).getContrato();
        if (contrato == 0) {
            tarifa = getTarifaClienteGarantizada(tipo);
        } else {
            tarifa = getTarifaClienteNoGarantizada(tipo);
        }
        return clients.get(c).getConsumo()*tarifa;
    }

    public double potentialCostOfPlant(Centrales centrales, int p) {
        double cost=0;
        if (centrales.get(p).getTipo()==0) {
            cost+=centrales.get(p).getProduccion()*50;
            cost+=20000;
        }
        else if (centrales.get(p).getTipo()==1) {
            cost+= centrales.get(p).getProduccion()*80;
            cost+=10000;
        }
        else {
            cost+= centrales.get(p).getProduccion()*150;
            cost+=5000;
        }
        return cost;
    }

}


