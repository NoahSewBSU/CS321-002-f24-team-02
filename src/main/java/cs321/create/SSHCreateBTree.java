package cs321.create;

import cs321.btree.BTree;
import cs321.btree.BTreeException;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import java.io.File;
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
		//System.out.println("Hello world from cs321.create.SSHCreateBTree.main");
        SSHCreateBTreeArguments myArgs = parseArguments(args); // Save into variable myArgs based on arguments from args; object = SSHCreateBTreeArguments 
        
        BTree newTree = new BTree(myArgs.getDegree(), myArgs.getFileName());

        /* TESTER */
        // change this later...   
        // BTree newTree = new BTree(2,"test_doc.txt");
        // scan files, logs, and insert keys based on BTree

        // try-catch is a requirement to go through files in java
        try {

            Scanner input = new Scanner("test_doc.txt"); // choose file to view

            File file = new File(input.nextLine()); // set file as chosen file

            input = new Scanner(file); 

            String fullType = myArgs.getTreeType(); // init type of tree
            String typeParts[] = fullType.split("-"); // parse tree type into 2 parts

            // go through the file until no more lines
            while (input.hasNextLine()) {
                String line = input.nextLine();
                String[] arr = line.split(" ");

                // check for "userip" case
                if (typeParts[0].compareTo("user") == 0) {
                    newTree.insert(new TreeObject(arr[3] + " " + arr[4]));
                }

                // check for all other cases
                if (typeParts[0].compareTo(arr[2]) == 0) {
                    switch(typeParts[0]){
                        case "accepted":
                            if(typeParts[1] == "ip"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                            break;
                        case "failed":
                            if(typeParts[1] == "ip"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                        case "reverse":
                            if(typeParts[1] == "ip"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                        case "invalid":
                            if(typeParts[1] == "ip"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[4]));
                            } else if(typeParts[1] == "timestamp"){
                                newTree.insert(new TreeObject(arr[2] + " " + arr[1]));
                            }
                        default:
                            break;
                        

                    }
                }
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
            } else if (arg.startsWith("--sshFileName=")){
                SSHFileName = arg.substring("--sshFileName=".length());
            } else if (arg.startsWith("--type=")) {
                treeType = arg.substring("--type=".length());
                //treeType = treeType.replaceAll("-",""); // remove all '-'s. SQLite has issues with '-' in naming conventions
            } else if (arg.startsWith("--cache-size=")) {
                cacheSize = Integer.parseInt(arg.substring("--cache-size=".length())); // Run an if statement after useCache, and then see if there is a value for this
            } else if (arg.startsWith("--database=")) {
                if(arg.substring("--database=".length()) == "yes"){
                    database = true;
                } else if(arg.substring("--database=".length()) == "no"){
                    database = false;
                }
            } else if (arg.startsWith("--debug=")) {
                debugLevel = Integer.parseInt(arg.substring("--debug=".length()));
            }
        }
        
        //return new SSHCreateBTreeArguments(/* input previously parsed variables */); // Have it return object with organized arguments
        return new SSHCreateBTreeArguments(useCache, degree, SSHFileName, treeType, cacheSize, debugLevel, database); // missing database <yes/no>
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
