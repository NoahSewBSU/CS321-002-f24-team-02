package cs321.search;


import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;


import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class SSHSearchBTree {
   public static void main(String[] args) {
       SSHSearchBTreeArguments arguments;


       // Step 1: Parse command-line arguments
       try {
           arguments = SSHSearchBTreeArguments.parseArguments(args);
       } catch (ParseArgumentException e) {
           // If argument parsing fails, print an error and usage information, then exit
           System.err.println("Error parsing arguments: " + e.getMessage());
           printUsageAndExit();
           return; // Exit early due to argument parsing error
       }


       BTree bTree = null; // BTree object to perform the search
       BufferedReader queryReader = null; // Reader to process the query file


       try {
           // Step 2: Open the BTree file
           // Initialize the BTree object with the degree and file path from the arguments
           bTree = new BTree(arguments.getDegree(), arguments.getBTreeFile());


           // Step 3: Open the query file
           queryReader = new BufferedReader(new FileReader(arguments.getQueryFile()));


           // Step 4: Read all query keys from the query file into a list
           List<String> queryKeys = new ArrayList<>();
           String line;
           while ((line = queryReader.readLine()) != null) {
               queryKeys.add(line.trim()); // Trim whitespace and store each query key
           }


           // Step 5: Create a priority queue to sort the results
           PriorityQueue<Entry<String, Integer>> pq = new PriorityQueue<>(
               // Sort by frequency in descending order, then alphabetically by key
               Comparator.<Entry<String, Integer>>comparingInt(Entry::getValue).reversed()
                       .thenComparing(Entry::getKey)
           );


           // Step 6: Search the BTree for each query key
           for (String key : queryKeys) {
               TreeObject treeObj = bTree.search(key); // Search for the key in the BTree
               if (treeObj != null) {
                   // If found, add the key and its frequency to the priority queue
                   pq.offer(new AbstractMap.SimpleEntry<>(key, (int) treeObj.getCount()));
               }
           }


           // Step 7: Print the results
           if (arguments.getTopFrequency() > 0) {
               // If top frequency is specified, print the top results
               System.out.println("Top " + arguments.getTopFrequency() + " results:");
               for (int i = 0; i < arguments.getTopFrequency() && !pq.isEmpty(); i++) {
                   Entry<String, Integer> entry = pq.poll(); // Get the next top entry
                   System.out.println(entry.getKey() + ": " + entry.getValue());
               }
           } else {
               // If no top frequency is specified, print results for all query keys
               System.out.println("Results for all query keys:");
               while (!pq.isEmpty()) {
                   Entry<String, Integer> entry = pq.poll(); // Get the next entry
                   System.out.println(entry.getKey() + ": " + entry.getValue());
               }
           }
       } catch (IOException e) {
           // Handle any IO exceptions that occur during file operations
           System.err.println("Error reading query file: " + e.getMessage());
       } finally {
           // Step 8: Clean up resources
           // Close the query file reader if it was successfully opened
           try {
               if (queryReader != null) {
                   queryReader.close();
               }
           } catch (IOException e) {
               // Handle any exceptions that occur while closing the query file
               System.err.println("Error closing query file: " + e.getMessage());
           }
       }
   }


   /**
    * Print usage information and exit the program.
    */
   private static void printUsageAndExit() {
       System.out.println("Usage: java -jar SSHSearchBTree.jar --cache=<0/1> --degree=<btree-degree> \\\n" +
               "\t--btree-file=<btree-filename> --query-file=<query-file> [--top-frequency=<10/25/50>] \\\n" +
               "\t[--cache-size=<n>] [--debug=<0|1>]");
       System.exit(1); // Exit the program with a non-zero status
   }
}




