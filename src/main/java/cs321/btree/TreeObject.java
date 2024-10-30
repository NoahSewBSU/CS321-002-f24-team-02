package cs321.btree;

/**
 * A class that holds a key value and its count. The key value is a string of up 
 * to 32 characters. The size is limited to make data storage on disk simpler.
 * 
 * @author amit, andre, natalie
 */
public class TreeObject implements Comparable<TreeObject> {

    private String key;
    private long count;
    private static final int SIZE  = 64; //#bytes

    /**
     * Create a TreeObject with the given key.
     * @param key
     */
    public TreeObject(String key) {
        this.key = key;
        this.count = 1;
    }

    /**
     * Create a TreeObject with the given key and count.
     * @param key
     * @param count
     */
    public TreeObject(String key, long count) {
        this.key = key;
        this.count = count;
    }

    /**
     * Getter for the key.
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter for the key. Unlikely to be needed.
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get the frequency (aka count) for the key contained in this TreeObject.
     * @return
     */
    public long getCount() {
        return count;
    }

    /**
     * Set the count value. 
     * @param count
     */
    public void setCount(long count) {
        this.count = count;
    }
   
    
    /**
     * Increment the count for the key.
     */
    public void incCount() {
    	count++;
    }
    

	/**
	 * {@inheritDoc}
	 */
    @Override
	public String toString() {
		return key + " " + this.count;
	}


    /**
     * Returns the size of the disk
     * @return SIZE - the Disk's size
     */
    public static int getDiskSize() { return SIZE;}
    

    /**
	 * Two TreeObjects are equal if they contain equal keys. 
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(TreeObject o) {
        return this.key.compareTo(o.getKey());
    }
}
