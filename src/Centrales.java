import java.util.ArrayList;
import java.util.Random;

/**
 * The Centrales class represents an ArrayList of Central objects.
 */
public class Centrales extends ArrayList<Central> {
    private static final long                       serialVersionUID    = 1L;
    private Random                                  myRandom;
    private static final double[][]                 Prod                = new double[][]{{500.0D, 250.0D}, {150.0D, 100.0D}, {90.0D, 10.0D}};
    private static final int[]                      Tipo                = new int[]{0, 1, 2};
    private static int                              size                = 8;
    private static double[][]                       supplyList          = new double[size][2];
    private static double[][]                       supplyListCopy      = new double[size][2];
    private static int[]                            supplyOnly          = new int[size];
    private static ArrayList<ArrayList<Integer>>    clientList          = new ArrayList<ArrayList<Integer>>(size);
    private static int[][]                          locList             = new int[size][2];
    private static ArrayList<ArrayList<Double>>     distances           = new ArrayList<ArrayList<Double>>(size*1000);
    private static int                              clientSize          = 1000;
    private static double[][]                       profitList          = new double[size][2];
    private static double[][]                       averageDistance     = new double[size][2];
    private double                                  totalProfit         =0;
    public double                                   totalIncome         =0;
    public double                                   totalCost           =0;
    private double                                  unused              = 0.0;

    /**
     * Constructs a Centrales object with the given ratios and seed.
     * The ratios array should have exactly 3 elements, representing the ratios for each type of plant.
     * The seed is used to initialize the random number generator.
     *
     * @param ratios the ratios for each type of plant
     * @param seed   the seed for the random number generator
     * @throws Exception if the ratios array does not have exactly 3 elements
     */
    public Centrales(int[] ratios, int seed) throws Exception {
        if (ratios.length != 3) {
            throw new Exception("Vector Centrales de tamaño incorrecto");
        } else {
            this.myRandom = new Random((long) seed);
    /**
     * For each type of plant, we create the number of plants specified by the ratios array.
     * The production of each plant is a random number between the minimum and maximum production for that type of plant.
     * The coordinates of each plant are random numbers between 0 and 100. Since the space is 100kmx100km
     * We add the plant to the Centrales list
     */


            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < ratios[i]; ++j) {
                    double production = this.myRandom.nextDouble() * Prod[i][0] + Prod[i][1];
                    Central central = new Central(Tipo[i], truncate(production), this.myRandom.nextInt(100), this.myRandom.nextInt(100));
                    this.add(central);
                }
            }
        }
    }

    /**
     * Simple truncate function to round a double to 2 decimal places
     */
    private static double truncate(double var0) {
        return Math.floor(var0 * 100.0D) / 100.0D;
    }


    public void getClientSize(Client clients){
        this.clientSize = clients.getSize();
    }

    public void makeSupplyList() {
        for(int i = 0; i < this.size(); ++i){
            double[] tup = new double[2];
            double supply = this.get((int) i).getProduccion();
            tup[0] = (double) i;
            tup[1] = supply;
            this.supplyList[i] = tup;
        }
    }

    public double[][] getSupplyList() {
        return this.supplyList;
    }

    /**
     * Prepare the supply list for sorting
     */

    public int[] prepareSort() {
        for(int i = 0; i < this.size(); ++i){
            this.supplyOnly[i] = (int) this.getSupplyList()[i][1]*100;
        }
        return this.supplyOnly;
    }

    /**
     * Merge sort algorithm
     */

    public static void mergeSort(int[] a, int n) {
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        int[] l = new int[mid];
        int[] r = new int[n - mid];

        for (int i = 0; i < mid; i++) {
            l[i] = a[i];
        }
        for (int i = mid; i < n; i++) {
            r[i - mid] = a[i];
        }
        mergeSort(l, mid);
        mergeSort(r, n - mid);

        merge(a, l, r, mid, n - mid);
    }

    public static void merge(
            int[] a, int[] l, int[] r, int left, int right) {

        int i = 0, j = 0, k = 0;
        while (i < left && j < right) {
            if (l[i] <= r[j]) {
                a[k++] = l[i++];
            }
            else {
                a[k++] = r[j++];
            }
        }
        while (i < left) {
            a[k++] = l[i++];
        }
        while (j < right) {
            a[k++] = r[j++];
        }
    }

    public void sortSupplyList(){
        for(int i = 0; i < this.size(); ++i){
            for(int j = 0; j < this.size(); ++j){
                if((int) this.supplyList[j][1]*100 == this.supplyOnly[i]){
                    double[] a = this.supplyList[i];
                    this.supplyList[i] = this.supplyList[j];
                    this.supplyList[j] = a;
                }
            }
        }
    }

    /**
    * Creates a list of lists, where each list contains the index of the plant and the index of the clients assigned to that plant, initialised without any cliets assigned 
    */


    public void makeClientList() {
        for (int i = 0; i < this.size(); ++i) {
            ArrayList<Integer> tup = new ArrayList<Integer>(this.clientSize);
            tup.add((int) this.supplyList[i][0]);
            this.clientList.add(tup);
        }
    }


    public ArrayList<ArrayList<Integer>> getClientList(){
        return this.clientList;
    }

    public void addClient(int client, int plant){
        this.clientList.get(plant).add(client);
    }

    public void removeClient(int client, int plant){
        for (int i = 1; i < this.clientList.get(plant-1).size(); ++i) {
            if(client == this.clientList.get(plant-1).get(i)){
                this.clientList.get(plant-1).remove(i);
            }
        }
    }

    public void takeSupply(int plant, double used){
        this.supplyList[plant][1] -= used;
    }

    public void makeLocList(){
        for(int i = 0; i < this.size; i++){
            int[] cood = new int[2];
            cood[0] = this.get(i).getCoordX();
            cood[1] = this.get(i).getCoordY();
            this.locList[i] = cood;
        }
    }

    public int[][] getLocList(){
        return this.locList;
    }

    public double calcDistance(int plantX, int plantY, int clientX, int clientY){
        int diffX2 = (plantX - clientX)*(plantX - clientX);
        int diffY2 = (plantY - clientY)*(plantY - clientY);
        double dist = Math.sqrt(diffX2 + diffY2);
        return dist;
    }

    /**
    * Computes the distance from each plant to each client and stores it in a list of lists
    */

    public void makeDistances(Client clients) {
        for(int i = 0; i < this.size; i++) {
            int plantX = this.get(i).getCoordX();
            int plantY = this.get(i).getCoordY();
            ArrayList<Double> tup = new ArrayList<Double>(this.clientSize);
            int clientXI = clients.get(0).getCoordX();
            int clientYI = clients.get(0).getCoordY();
            double disti = calcDistance(plantX, plantY, clientXI, clientYI);
            tup.add(disti);
            this.distances.add(tup);
            for(int j = 1; j < this.clientSize; j++) {
                int clientX = clients.get(j).getCoordX();
                int clientY = clients.get(j).getCoordY();
                double dist = calcDistance(plantX, plantY, clientX, clientY);
                this.distances.get(i).add(dist);
            }
        }
    }

    public static ArrayList<ArrayList<Double>> getDistances() {
        return distances;
    }

    public int getSize(){
        return this.size;
    }




    public void makeProfitList(Client client) throws Exception {
        totalProfit = 0.0;
        double totalCost = 0.0;
        double totalIncome = 0.0;

        //calculating the total cost
        for(int i = 0; i < this.getSize(); i++) {
            double cost = 0.0;
            if (this.getClientList().get(i).size() > 1) {   //checks if plant was used
                int index = this.getClientList().get(i).get(0);
                double prod = this.get(index).getProduccion();
                int tipo = this.get(index).getTipo();
                double marcha = VEnergia.getCosteMarcha(tipo);
                double parada = VEnergia.getCosteParada(tipo);
                double costProd = prod * VEnergia.getCosteProduccionMW(tipo);
                cost = (marcha + parada + costProd);
            }
            totalCost += cost;
        }

        // calculating total income
        for(int i = 0; i < client.size(); i++) {
            if (client.getPlantList().get(i).get(1) >= 0) { // check if client was assigned
                int index = client.getPlantList().get(i).get(0);
                int tipo = client.get(index).getTipo();
                int contracto = client.get(index).getContrato();
                double demand = client.getDemandList().get(i).get(1);
                if (contracto == 0) {
                    totalIncome += demand * VEnergia.getTarifaClienteGarantizada(tipo);
                } else {
                    totalIncome += demand * VEnergia.getTarifaClienteNoGarantizada(tipo);
                }
            }
        }


        this.totalProfit = totalIncome - totalCost;

    }


    public double[][] getProfitList(){
        return this.profitList;
    }

    public void createAverageDistances(Client client) {
        for (int i = 0; i < this.getSize(); i++) {
            int totalDistance=0;
            int c=0;
            for (int j = 1; j < client.getSize(); j++) {
                totalDistance+= this.distances.get(i).get(j);
                c++;
            }
            double[] tup = new double[2];
            totalDistance=totalDistance/c;
            tup[0] = (double) i;
            tup[1] = totalDistance;
            this.averageDistance[i] = tup;
        }
    }

    public double[] getMaxAverageDistance() {
        double a = this.averageDistance[0][1];
        double aa = this.averageDistance[0][0];
        double b = this.averageDistance[1][1];
        double bb = this.averageDistance[1][0];
        if (a<b) {
            double c=b; b=a; a=c;
        }
        for (int i = 0; i < this.getSize(); i++) {
            if (this.averageDistance[i][1]>a) {
                b=a; a=this.averageDistance[i][1];
                bb=aa; aa=this.averageDistance[i][0];
            }
            else if (this.averageDistance[i][1]>b) {
                b=this.averageDistance[i][1];
                bb=this.averageDistance[i][0];
            }
        }
        double[] maxAverageDistance = new double[2];
        maxAverageDistance[0]=aa;
        maxAverageDistance[1]=bb;
        return maxAverageDistance;
    }



    public double[][] getAverageDistance() {
        return this.averageDistance;
    }


    public double[][] getSupplyListCopy() {
        return supplyListCopy;
    }

    public void setSupplyListCopyI(double i, double sup){
        this.supplyListCopy[(int)i][0] = i;
        this.supplyListCopy[(int)i][1] = sup;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void calcUnused(){
        this.unused = 0.0;

        //Checking for each plant whether that plant has been used and if so, how much unused supply it has left
        for (int i = 0; i < this.getSize(); i++) {
            int index = i;
            int indexj=0;
            //Checking for current plant whether that plant has been used
            for (int j = 0; j < this.getSize(); j++) {
                if (getClientList().get(j).get(0) == index) {
                    indexj = j;
                }
            }

            if(this.clientList.get(indexj).size() > 1){

                double u = this.supplyListCopy[index][1];
                this.unused += u;
            }
        }

    }

    public double getUnused(){
        return this.unused;
    }

    public void setSupplyListCopy(double occ, int plant, Excecuter energia){
        //int index = energia.getClientListIndex(this, plant);
        this.supplyListCopy[plant][1] += occ;
    }

}


    


