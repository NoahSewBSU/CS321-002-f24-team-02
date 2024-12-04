package cs321.create;

/**
 * SSHCreateBTreeArguments parses command line arguments for SSHCreateBTree.
 *
 *  @author
 *
 */
public class SSHCreateBTreeArguments
{
	/* TODO: Complete this class */
    /* Class to save the arguments as an object */

    private final boolean useCache;
    private final int degree;
    private final String SSHFileName;
    private final String treeType;
    private final int cacheSize;
    private final int debugLevel;
    private final boolean database;

    /**
     * Builds a new SSHCreateBTreeArguments with the specified
     * command line arguments and tests their validity.
     *
     * @param useCache boolean for using cache or not
     * @param degree degree for BTree
     * @param SSHFileName String of filename
     * @param treeType type of tree
     * @param cacheSize size of cache if using
     * @param debugLevel level of debugging
     * @param database <yes/no> send to .db file
     */
    public SSHCreateBTreeArguments(boolean useCache, int degree, String SSHFileName, String treeType, int cacheSize, int debugLevel, boolean database)
    {
        this.useCache = useCache;
        this.degree = degree;
        this.SSHFileName = SSHFileName;
        this.treeType = treeType;
        this.cacheSize = cacheSize;
        this.debugLevel = debugLevel;
        this.database = database;
    }

    /* Getters */
    public boolean getCache(){
        return useCache;
    }
    public int getDegree(){
        return degree;
    }
    public String getFileName(){
        return SSHFileName;
    }
    public String getTreeType(){
        return treeType;
    }
    
    public int getCacheSize(){
        return cacheSize;
    } 
    public int getDebugLevel(){
        return debugLevel;
    }

    public boolean getDatabase(){
        return database;
    }


    @Override
    public String toString()
    {
        return "SSHFileNameCreateBTreeArguments{" +
                "useCache=" + useCache +
                ", degree=" + degree +
                ", SSH_Log_File='" + SSHFileName + '\'' +
                ", TreeType=" + treeType +
                ", cacheSize=" + cacheSize +
                ", debugLevel=" + debugLevel +
                ", database=" + database +
                '}';
    }
}
