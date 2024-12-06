package cs321.create;

import cs321.btree.BTree;
import cs321.btree.BTreeException;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;


import java.io.File;
import java.sql.*;
import java.util.Scanner;



/**
 * The driver class for building a BTree representation of an SSH Log file.
 *
 * @author Noah Seward
 */
public class SSHCreateBTree {
    /**
     * Main driver of program.
     * @param args
     */
    public static void main(String[] args) throws Exception 
	{
        if(args.length < 5){
            System.out.println("Usage: java -jar build/libs/SSHCreateBTree.jar --cache=<0/1> --degree=<btree-degree> \\\n");
            System.out.println("\t--sshFile=<ssh-file> --type=<tree-type> [--cache-size=<n>]\\\n");
            System.out.println("\t--database=<yes/no> [--debug=<0|1>]\n");
            return;
        }
		System.out.println("Parsing arguments...");
        SSHCreateBTreeArguments myArgs = parseArguments(args); // Save into variable myArgs based on arguments from args; object = SSHCreateBTreeArguments 
        
        BTree btree = new BTree(myArgs.getDegree(), myArgs.getFileName());

        /* TESTER */
        // change this later...   
        // BTree newTree = new BTree(2,"test_doc.txt");
        // scan files, logs, and insert keys based on BTree

        // try-catch is a requirement to go through files in java
        try (Scanner input = new Scanner(new File(myArgs.getFileName()))){

            String fullType = myArgs.getTreeType(); // init type of tree
            String typeParts[] = fullType.split("-"); // parse tree type into 2 parts

            // go through the file until no more lines
            while (input.hasNextLine()) {
                String line = input.nextLine();
                String[] arr = line.split(" ");

                // check for "userip" case
                if (typeParts[0].equals("user")) {
                    btree.insert(new TreeObject(arr[3] + " " + arr[4]));
                }

                // check for all other cases
                else if (typeParts[0].equals(arr[2])) {
                    switch(typeParts[0]){
                        case "accepted":
                            if(typeParts[1] == "ip"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                            break;
                        case "failed":
                            if(typeParts[1] == "ip"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                            break;
                        case "reverse":
                            if(typeParts[1] == "ip"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                            break;
                        case "invalid":
                            if(typeParts[1] == "ip"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                btree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                            break;
                        default:
                            break;
                        

                    }
                }
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /* Argument to dump into a .txt file */
        // name: dump-<tree-type>.<degree-number>.txt

        /* Save to database file if database is checked 'yes' */
        if(myArgs.getDatabase()) {
            saveBTreeToDatabase(btree, myArgs.getTreeType());
        }

        /* Confirmations of progress */
        System.out.println("Reading file: " + myArgs.getFileName());
        System.out.println("Tree type: " + myArgs.getTreeType());
        System.out.println("Keys inserted: " + btree.getSortedKeyArray().length);

	}

    /**
     * Process command line arguments; create a saved file of arguments to later use to create a BTree
     * @param args  The command line arguments passed to the main method.
     */
    public static SSHCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException
    {
        
        /* init variables */
        boolean useCache = false;
        int degree = 0;
        String SSHFileName = null;
        String treeType = null;
        int cacheSize = 100; // Optional (100 - 1000) // dependent on useCache
        int debugLevel = 0; // Optional (0 = optimal degree)
        boolean database = false;

        /* Parse arguments here and assign them */
        for (String arg : args){
            if (arg.startsWith("--cache=")) {
                useCache = Boolean.parseBoolean(arg.substring("--cache=".length()));
            } else if (arg.startsWith("--degree=")) {
                degree = Integer.parseInt(arg.substring("--degree=".length()));
            } else if (arg.startsWith("--sshFile=")){
                SSHFileName = arg.substring("--sshFile=".length());
            } else if (arg.startsWith("--type=")) {
                treeType = arg.substring("--type=".length());
            } else if (arg.startsWith("--cache-size=")) {
                cacheSize = Integer.parseInt(arg.substring("--cache-size=".length())); // Run an if statement after useCache, and then see if there is a value for this
            } else if (arg.startsWith("--database=")) {
                if(arg.substring("--database=".length()).equals("yes")){
                    database = true;
                } else if(arg.substring("--database=".length()).equals("no")){
                    database = false;
                }
            } else if (arg.startsWith("--debug=")) {
                debugLevel = Integer.parseInt(arg.substring("--debug=".length()));
            }
        }
        
        //return new SSHCreateBTreeArguments(/* input previously parsed variables */); // Have it return object with organized arguments
        return new SSHCreateBTreeArguments(useCache, degree, SSHFileName, treeType, cacheSize, debugLevel, database); // missing database <yes/no>
    }

    /* Method for adding to database */
    private static void saveBTreeToDatabase(BTree btree, String treeType) {

        /* TODO: Create column for keys and an associated column for frequency */
        
        String url = "jdbc:sqlite:output/db-search/SSHLogDB.db";

        treeType = treeType.replaceAll("-","_"); // remove all '-'s. SQLite has issues with '-' in naming conventions

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                System.out.println("Connected to the database.");

                // Create the BTree Table
                Statement statement = connection.createStatement();
                statement.executeUpdate("drop table if exists " + treeType);
                statement.executeUpdate("create table " + treeType + " (Key string, Frequency integer)");


                // Insert BTree keys

                String[] sortedKeys = btree.getSortedKeyArray();
                Long[] sortedFrequencies = btree.getSortedCount();

                for (String key : sortedKeys) {
                    String insertSQL = "INSERT OR IGNORE INTO " + treeType + " (Key) VALUES (?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                        pstmt.setString(1, key.toString());
                        pstmt.executeUpdate();
                        System.out.println("Inserting key: " + key);
                    }
                }

                for (Long frequency : sortedFrequencies) {
                    String insertSQL = "INSERT OR IGNORE INTO " + treeType + " (Frequency) VALUES (?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                        pstmt.setInt(1, frequency.toInt());
                        pstmt.executeUpdate();
                        System.out.println("Inserting frequency: " + frequency);
                    }
                }

                System.out.println("BTree saved to database successfully.");

            }
        } catch (SQLException e) {
            System.out.println("Error saving to database: " + e.getMessage());
        }
    }

    private static void saveToTextFile(BTree btree, String treeType){



    }

	/** 
	 * Print usage message and exit.
	 * @param errorMessage the error message for proper usage
	 */
	private static void printUsageAndExit(String errorMessage)
    {
        System.exit(1);
	}

}
