
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * The Clientes class represents an ArrayList of Cliente objects.
 */

public class Clientes extends ArrayList<Cliente> {
    private static final long                           serialVersionUID = 1L;
    private Random                                      myRandom;
    private static final int[]                          TIPOCL = new int[]{0, 1, 2};
    private static final int[]                          TIPOCNT = new int[]{0, 1};
    private static final double[][]                     consumos = new double[][]{{15.0D, 5.0D}, {3.0D, 2.0D}, {2.0D, 1.0D}};
    private static int                                  size = 200;
    private static int                                  gSize = (int)(size* 0.75);
    private static ArrayList<ArrayList<Double>>         demandList = new ArrayList<ArrayList<Double>>(size);
    private static ArrayList<ArrayList<Double>>         demandListG = new ArrayList<ArrayList<Double>>(size);
    private static int[]                                demandOnly = new int[size];
    private static int[]                                demandOnlyG = new int[gSize];
    private static ArrayList<ArrayList<Integer>>        plantList = new ArrayList<ArrayList<Integer>>(size);
    private static ArrayList<ArrayList<Integer>>        plantListG = new ArrayList<ArrayList<Integer>>(size);
    private static int[][]                              locList = new int[size][2];   

    /**
     * Constructs a Centrales object with the given ratios and seed.
     * The ratios array should have exactly 3 elements, representing the ratios for each type of plant.
     * The seed is used to initialize the random number generator.
     *
     * @param quantity the number of clients
     * @param ratios the ratios for each type of Client
     * @param garantizado the proportion of clients with guaranteed contracts
     * @param seed   the seed for the random number generator
     * @throws Exception if any input is outside of its limits
     */

    public Clientes(int quantity, double[] ratios, double garantizado, int seed) throws Exception {
        if (ratios.length != 3) {
            throw new Exception("Vector proporciones tipos clientes de tamaño incorrecto");
        } else if (ratios[0] + ratios[1] + ratios[2] != 1.0D) {
            throw new Exception("Vector proporciones tipos clientes no suma 1");
        } else if (garantizado < 0.0D && garantizado > 1.0D) {
            throw new Exception("Proporcion garantizado fuera de limites");
        } else {
            this.myRandom = new Random((long)(seed + 1));

            for(int i = 0; i < quantity; ++i) {
                double rand = this.myRandom.nextDouble();
                byte clientType;
                if (rand < ratios[0]) {
                    clientType = 0;
                } else if (rand < ratios[0] + ratios[1]) {
                    clientType = 1;
                } else {
                    clientType = 2;
                }

                rand = this.myRandom.nextDouble();
                byte contractType;
                if (rand < garantizado) {
                    contractType = 0;
                } else {
                    contractType = 1;
                }

                double cons = this.myRandom.nextDouble() * consumos[clientType][0] + consumos[clientType][1];
                this.add(new Cliente(TIPOCL[clientType], truncate(cons), TIPOCNT[contractType], this.myRandom.nextInt(100), this.myRandom.nextInt(100)));
            }

        }
    }

    private static double truncate(double a) {
        return Math.floor(a * 100.0D) / 100.0D;
    }
    public void makeDemandList() {
        for(int i = 0; i < this.size(); ++i){
            ArrayList<Double> tup = new ArrayList<Double>(2);
            double demand = this.get((int) i).getConsumo();
            tup.add((double) i);
            tup.add(demand);
            this.demandList.add(tup);
        }
    }

    public ArrayList<ArrayList<Double>> getDemandList(){
        return this.demandList;
    }
    /**
    * prepares demand list for sorting
    */

    public int[] prepareSort() {
        for(int i = 0; i < this.size(); ++i){
            this.demandOnly[i] = (int )(this.getDemandList().get(i).get(1)*100);
        }
        return this.demandOnly;
    }

    public int[] prepareSortG() {
        for(int i = 0; i < this.gSize; ++i){
            this.demandOnlyG[i] = (int )(this.getDemandListG().get(i).get(1)*100);
        }
        return this.demandOnlyG;
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

    /**
     * Since we sort the list of only demand, we need to sort the demanlist, with tupoles of index and demand, according to the sorted demand list
     */

    public void sortDemandList(){
        for(int i = 0; i < this.size(); ++i){
            for(int j = 0; j < this.size(); ++j){
                if((int) (this.demandList.get(j).get(1)*100) == this.demandOnly[i]){
                    Collections.swap(this.demandList, i, j);
                }
            }
        }
    }

    public void sortDemandListG(){
        for(int i = 0; i < gSize; ++i){
            for(int j = 0; j < gSize; ++j){
                if((int) (this.demandListG.get(j).get(1)*100) == this.demandOnlyG[i]){
                    Collections.swap(this.demandListG, i, j);
                }
            }
        }
    }

    /**
     * We make a list that for each client, stores its demand and the index of the plant it is assigned to
     */

    public void makePlantList() {
        for (int i = 0; i < this.size(); ++i) {
            ArrayList<Integer> tup = new ArrayList<Integer>(2);
            tup.add((int)(this.demandList.get(i).get(0)*100)/100);
            tup.add(-1);
            this.plantList.add(tup);
        }
    }

    public void makePlantListG() {
        for(int i = 0; i < this.size; i++){
            int index = (int) this.plantList.get(i).get(0);
            int contract = this.get(index).getContrato();
            if(contract == 0){
                this.plantListG.add(this.plantList.get(i));
            }
        }

    }

    public void makeDemandListG(){
        for(int i = 0; i < this.size; i++){
            int index = (int)(this.demandList.get(i).get(0)*100/100);
            int contract = this.get(index).getContrato();
            if(contract == 0){
                this.demandListG.add(this.demandList.get(i));
            }
        }
    }

    public  ArrayList<ArrayList<Integer>> getPlantList(){
        return this.plantList;
    }

    public  ArrayList<ArrayList<Integer>> getPlantListG(){
        return this.plantListG;
    }

    public ArrayList<ArrayList<Double>> getDemandListG(){
        return this.demandListG;
    }



    public void assignClient(int client, int plant){
        this.plantList.get(client).remove(1);
        this.plantList.get(client).add(plant);
    }

    public void removeClient(int client){
        this.plantList.get(client).remove(1);
        this.plantList.get(client).add(0);
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

    public int getSize(){
        return this.size;
    }

    public int getSizeG(){
        return this.gSize;
    }
    
}

