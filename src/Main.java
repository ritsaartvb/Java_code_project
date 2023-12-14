import java.util.Arrays;


public class Main {
    public static void main(String[] args) throws Exception {

    /**
     * First we set the scale factor for the number of plants of each type and the seed for the random number generator.
     * Then we set the ratios for each type of plant and the ratios for each type of client.
     * We create the plants and clients objects and an executer object energia, which will excecute the alogrithm
     * We initialise the excecuter by running the intialiseStateG method, which will create the initial simple solution
     */


        int a = 1; //scale factor, set to 1 by default
        int seed = 1; //seed for random number generator, set to 1 by default
        
        int[] ratios = new int[]{a*1,a*2,a*5};
        double[] ratiosClientes = new double[]{0.25, 0.3, 0.45};
        
        Centrales centrales = new Centrales(ratios, seed);
        Client clients = new Client(200 , ratiosClientes, 0.75, seed);

        Excecuter energia = new Excecuter();
        energia.intialiseState(clients, centrales);
        
    /**
     * We report the results, then run the hillclimbing alorithm and report the results again.
     */

        centrales.calcUnused();
        System.out.println("Total unused before hill climbing");
        System.out.println(centrales.getUnused());
        centrales.makeProfitList(clients);
        centrales.createAverageDistances(clients);
        System.out.println("Total profit before hill climbing");
        System.out.println(centrales.getTotalProfit());
        energia.hillClimbing(centrales, clients);
        centrales.calcUnused();
        System.out.println("Total unused AFTER hill climbing");
        System.out.println(centrales.getUnused());
        centrales.makeProfitList(clients);
        centrales.createAverageDistances(clients);
        System.out.println("Total profit AFTER hill climbing");
        System.out.println(centrales.getTotalProfit());


        
    };
    }
