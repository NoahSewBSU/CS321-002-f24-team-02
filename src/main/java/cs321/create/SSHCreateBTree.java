package cs321.create;

import cs321.btree.BTree;
import cs321.btree.BTreeException;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;



/**
 * The driver class for building a BTree representation of an SSH Log file.
 *
 * @author 
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
		System.out.println("Hello world from cs321.create.SSHCreateBTree.main");
        SSHCreateBTreeArguments myArgs = parseArguments(args); // Save into variable myArgs based on arguments from args; object = SSHCreateBTreeArguments
        // other code 
        
        /* TODO: call the BTree constructor to construct empty BTree
        *  Waiting on BTree constructor to be completed */      
        BTree newTree = new BTree(myArgs.getDegree(), myArgs.getFileName());
        // scan files, logs, and insert keys based on BTree

        // TODO: comb through file and add assets to the new BTree
        /* while (!newline = null) {
                iterate line 0;
                if (string after space 2 == start of treeType) {
                    record that string + }
            insert.node();
        } */

        // TODO: Add in if-statements for what to do based on treeType
        /* if (myargs.getTreeType() == "acceptedip") {
                if database is yes, setup database file of fileName given
         *      comb through fileName looking for designated data needed for that treeType
         *      1. check each line; if line is correct, goto 2. if not, goto next line
         *      2. find proper data from line and if database is yes, place into database given. otherwise, place into arbitrary tree (insert())
         *      go through each newline '\n' and count white spaces; this will tell us where the data is
         *      since all data is structured the same by ending in '\n' and using the same amount of white spaces
         * } else if (myargs.get)
        } */

        // TODO: Types needed
        /*  
            accepted/failed/invalid/reverseaddress/user
            ip/timestamp

            when getting tree-type, consider saving first and second parts seperately

            Accepted IPs (accepted-ip: Accepted log entry along with its IP address)
            Accepted timestamps (accepted-timestamp: Accepted log entry along with its timestamp)
            Failed IPs (failed-ip: Failed log entry along with its IP address)
            Failed timestamps (failed-timestamp: Failed log entry along with its timestamp)
            Invalid IPs (invalid-ip: Invalid log entry along with its IP address)
            Invalid timestamps (invalid-timestamp: Invalid log entry along with its timestamp)
            Reverse or Address IPs (reverseaddress-ip: Reverse or Address log entry along with its IP address)
            Reverse or Address timestamps (reverseaddress-timestamp: Reverse or Address log entry along with its timestamp)
            User's name and their IPs (user-ip: User name and IP address from all log entries)
         */

         /* TODO: Create function to pull key words from file 
          *     
            String type1Options = {"accepted","failed","invalid","reverseaddress","user"};
            String type2Options = {"ip","timestamp"};
            String type1Selected;
            String type2Selected;
          
            for (arg : args) {
                if (arg.startsWith("--type=")) {
                    //copy string until '-'
                    //compare to type1Options {
                        //assign type1Selection to matching option
                        //}
                    //copy string after '-' until null
                    //compare to type2Options {
                        //assign type2Selection to matching option
                        //}
                } 
            }
         */



        
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
        boolean database;

       
        /* Create object from arguments and return */
        /* Make getters based on args */

        /* Parse arguments here and assign them */
        for (String arg : args){
            if (arg.startsWith("--cache=")) {
                useCache = Boolean.parseBoolean(arg.substring("--cache=".length()));
            } else if (arg.startsWith("--degree=")) {
                degree = Integer.parseInt(arg.substring("--degree=".length()));
            } else if (arg.startsWith("--sshFileName=")){
                SSHFileName = arg.substring("--sshFileName".length());
            } else if (arg.startsWith("--type=")) {
                treeType = arg.substring("--type=".length());
                treeType = treeType.replaceAll("-",""); // remove all '-'s. SQLite has issues with '-' in naming conventions
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
        return new SSHCreateBTreeArguments(useCache, degree, SSHFileName, treeType, cacheSize, debugLevel); // missing database <yes/no>
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
