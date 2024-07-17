import java.io.*;
import java.util.*;

//K-means clustering class
public class CustomerCluster
{
    /*************************************************************************/

    private int numberRecords;            //number of records
    private int numberAttributes;         //number of attributes
    private int numberClusters;           //number of clusters
    private int numberIterations;         //number of iterations

    private double[][] records;           //array of records
    private double[][] centroids;         //array of centroids
    private int[] clusters;               //clusters of records
    private Random rand;                  //random number generator

    /*************************************************************************/

    //Constructor of CustomerCluster
    public CustomerCluster()
    {
        //initial data is empty
        numberRecords = 0;
        numberAttributes = 0;
        numberClusters = 0;
        numberIterations = 0;
        records = null;
        centroids = null;
        clusters = null;
        rand = null;
    }

    /*************************************************************************/

    //Method loads records from input file
    public void load(String inputFile) throws IOException
    {
        Scanner inFile = new Scanner(new File(inputFile));

        //read number of records, attributes
        numberRecords = inFile.nextInt();
        numberAttributes = inFile.nextInt();

        //create array of records
        records = new double[numberRecords][numberAttributes];

        //for each record
        for (int i = 0; i < numberRecords; i++)
        {
            //read attributes
            for (int j = 0; j < numberAttributes; j++)
                records[i][j] = inFile.nextDouble();
        }

        inFile.close();
    }

    /*************************************************************************/

    //Method sets parameters of clustering
    public void setParameters(int numberClusters, int numberIterations, int seed)
    {
        //set number of clusters
        this.numberClusters = numberClusters;

        //set number of iterations
        this.numberIterations = numberIterations;

        //create random number generator with seed
        this.rand = new Random(seed);
    }

    /*************************************************************************/

    //Method performs k-means clustering
    public void cluster()
    {
        //initialize clusters of records
        initializeClusters();

        //initialize centroids of clusters
        initializeCentroids();

        //repeat iterations times
        for (int i = 0; i < numberIterations; i++)
        {
            //assign clusters to records
            assignClusters();

            //update centroids of clusters
            updateCentroids();
        }
    }

    /*************************************************************************/

    //Method initializes clusters of records
    private void initializeClusters()
    {
        //create array of cluster labels
        clusters = new int[numberRecords];

        //assign -1 to all records, cluster labels are unknown
        for (int i = 0; i < numberRecords; i++)
            clusters[i] = -1;
    }

    /*************************************************************************/

    //Method initializes centroids of clusters
    private void initializeCentroids()
    {
        //create array of centroids
        centroids = new double[numberClusters][numberAttributes];

        //for each cluster
        for (int i = 0; i < numberClusters; i++)
        {
            //randomly pick a record
            int index = rand.nextInt(numberRecords);

            //use the record as centroid
            for (int j = 0; j < numberAttributes; j++)
                centroids[i][j] = records[index][j];
        }
    }

    /*************************************************************************/

    //Method assigns clusters to records
    private void assignClusters()
    {
        //go thru records and assign clusters to them
        for (int i = 0; i < numberRecords; i++)
        {
            //find distance between record and first centroid
            double minDistance = distance(records[i], centroids[0]);
            int minIndex = 0;

            //go thru centroids and find closest centroid
            for (int j = 0; j < numberClusters; j++)
            {
                //find distance between record and centroid
                double distance = distance(records[i], centroids[j]);

                //if distance is less than minimum, update minimum
                if (distance < minDistance)
                {
                    minDistance = distance;
                    minIndex = j;
                }
            }

            //assign closest cluster to record
            clusters[i] = minIndex;
        }
    }

    /*************************************************************************/

    //Method updates centroids of clusters
    private void updateCentroids()
    {
        //create array of cluster sums and initialize
        double[][] clusterSum = new double[numberClusters][numberAttributes];
        for (int i = 0; i < numberClusters; i++)
            for (int j = 0; j < numberAttributes; j++)
                clusterSum[i][j] = 0;

        //create array of cluster sizes and initialize
        int[] clusterSize = new int[numberClusters];
        for (int i = 0; i < numberClusters; i++)
            clusterSize[i] = 0;

        //for each record
        for (int i = 0; i < numberRecords; i++)
        {
            //find cluster of record
            int cluster = clusters[i];

            //add record to cluster sum
            clusterSum[cluster] = sum(clusterSum[cluster], records[i]);

            //increment cluster size
            clusterSize[cluster] += 1;
        }

        //find centroid of each cluster
        for (int i = 0; i < numberClusters; i++)
            if (clusterSize[i] > 0)
                centroids[i] = scale(clusterSum[i], 1.0/clusterSize[i]);
    }

    /*************************************************************************/

    //Method finds distance between two records, square of euclidean distance
    private double distance(double[] u, double[] v)
    {
        double sum = 0;

        //square of euclidean distance between two records
        for (int i = 0; i < u.length; i++)
            sum += (u[i] - v[i])*(u[i] - v[i]);

        return sum;
    }

    /*************************************************************************/

    //Method finds sum of two records
    private double[] sum(double[] u, double[] v)
    {
        double[] result = new double[u.length];

        //add corresponding attributes of records
        for (int i = 0; i < u.length; i++)
            result[i] = u[i] + v[i];

        return result;
    }

    /*************************************************************************/

    //Method finds scaler multiple of a record
    private double[] scale(double[] u, double k)
    {
        double[] result = new double[u.length];

        //multiply attributes of record by scaler
        for (int i = 0; i < u.length; i++)
            result[i] = u[i]*k;

        return result;
    }

    /*************************************************************************/

    //Method writes records and their clusters to output file
    public void display(String outputFile) throws IOException
    {
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //for each record
        for (int label = 1; label <= numberClusters ; label++) {
            for (int i = 0; i < numberRecords; i++) {  // Print records in order of class
                if (label == clusters[i] + 1) {
                    //write attributes of record
                    for (int j = 0; j < numberAttributes; j++)
                        outFile.print(records[i][j] + " ");

                    //write cluster label
                    outFile.println(clusters[i] + 1);
                }

            }
            outFile.println();
            outFile.println();
        }
        outFile.close();
    }
    private static double normalizeAge(double age) {
        return (age - 20) / 80;
    }
    private static double normalizeIncome(double income) {
        return (income - 20) / 80;
    }
    private static double normalizeScore(double score) {
        return (score - 500) / 400;
    }

    private static void convertTrainingFile(String inputFile, String outputFile) throws IOException {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records, attributes, classes
        int numberRecords = inFile.nextInt();
        int numberAttributes = inFile.nextInt();

        //write number of records, attributes, classes
        outFile.println(numberRecords + " " + numberAttributes);

        //for each record
        for (int i = 0; i < numberRecords; i++) {

            int age = inFile.nextInt();
            double ageNumber = normalizeAge(age);             //convert age
            outFile.print(ageNumber + " ");

            int income = inFile.nextInt();                  //convert income
            double incomeNumber = normalizeIncome(income);
            outFile.print(incomeNumber + " ");

            int score = inFile.nextInt();                      //convert score
            double scoreNumber = normalizeScore(score);
            outFile.println(scoreNumber + " ");

        }

        inFile.close();
        outFile.close();
    }
    private double computeSumSquaredError() {
        double sse = 0;
        for (int i = 0; i < records.length; i++) {
            // Get distance from record to its class's centroid
            double dist = distance(records[i], centroids[clusters[i]]);
            // Add square of distance to total error
            sse += (dist * dist);
        }
        return sse;
    }
    /*************************************************************************/
    public static void main(String[] args) throws IOException {
        Scanner userInput = new Scanner(System.in);

        System.out.print("Enter input file: ");
        String inputFile = userInput.nextLine();

        System.out.println("Enter output file: ");
        String outputFile = "output/" + userInput.nextLine();

        String normalizedPath = "output/normalizedTemp.txt";
        convertTrainingFile(inputFile, normalizedPath);

        int numberOfClusters = 4;
        //create clustering object
        CustomerCluster k = new CustomerCluster();

        //load records
        k.load(normalizedPath);

        //set parameters
        k.setParameters(numberOfClusters, 100, 58947);

        //perform clustering
        k.cluster();

        //display records and clusters
        k.display(outputFile);

        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile, true));
        outFile.printf("\nNumber of Clusters: %d", numberOfClusters);
        outFile.close();

        double sse = k.computeSumSquaredError();

        System.out.printf("\nSSE: %f", sse);
    }
}

/*
age: 20-100
income: 20k-100k
credit score: 500-900
*/
