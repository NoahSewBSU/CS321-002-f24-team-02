package cs321.btree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.tree.TreeNode;




public class BTree
{
    private int METADATA_SIZE = Long.BYTES;
    private long nextDiskAddress = METADATA_SIZE;
    private FileChannel file;
    private ByteBuffer buffer;
    private int nodeSize;

    private long rootAddress = METADATA_SIZE;

    private ArrayList<String> values = new ArrayList<String>();
    private ArrayList<Long> values2 = new ArrayList<Long>();


    private Node root = new Node(true, METADATA_SIZE, nextDiskAddress);
    private int height;
    private int degree;
    private int size;
    private int numOfNodes;
    private String filename;
    private int t;




    /*Constructors for BTree*/
    public BTree(String filename) {
        this.filename = filename;
        this.height = 0;
        this.degree = 2;
        this.size = 0;
        this.numOfNodes = 1;
        this.root = new Node(true, 2, METADATA_SIZE);
    }

    public BTree(int degree, String filename) {
        this.height = 0;
        this.degree = degree;
        this.size = 0;
        this.numOfNodes = 1;
        this.root = new Node(true, degree, METADATA_SIZE);
    }

    



    /*Node Class to create a node in the BTRee */
    private class Node {

        private long address;
        private int numOfKeys;
        private boolean isLeaf;
        private TreeObject[] keys;
        private Node[] children;
        private int t;
        

        /*Constructor for a Node in BTree */
        public Node(boolean isLeaf, int t, long address) {
            this.isLeaf = isLeaf;
            this.keys = new TreeObject[2*t - 1];
            this.children = new Node[2 *t];
            this.numOfKeys = 0;
            this.address = address;
            this.t = t;

        }
        /*Constructor for a node in BTree */
        public Node(TreeObject[] keys, Node[] children, boolean isLeaf, int keyCount, long adrresss) {
            this.keys = keys;
            this.children = children;
            this.isLeaf = isLeaf;
            this.numOfKeys = keyCount;
            this.address = adrresss;
        }

        /*Traverses through the btree and adds the keys inside it into an arraylist */
        public ArrayList<String> inorderTransversal() {
            
            int i = 0;
            for(i = 0; i < numOfKeys; i++) {
                if(isLeaf == false) {
                    children[i].inorderTransversal();
                }
                values.add(keys[i].getKey());
            }

            if(isLeaf == false) {
                children[i].inorderTransversal();
            }
    
            return values;
        }

        public ArrayList<Long> inorderTransversal2() {
            
            int i = 0;
            for(i = 0; i < numOfKeys; i++) {
                if(isLeaf == false) {
                    children[i].inorderTransversal2();
                }
                values2.add(keys[i].getCount());
            }

            if(isLeaf == false) {
                children[i].inorderTransversal2();
            }
    
            return values2;
        }




        /*Searches trhough the Btree and checks if the given string is inside of the BTree */
        public TreeObject search(String k) {
            if(numOfKeys == 0) {
                return null;
            }
            int i = 0;
            while(i < numOfKeys && k.compareTo(keys[i].getKey()) > 0) {
                i++;
            }

            if(keys[i] != null && keys[i].getKey().compareTo(k) ==0) {
                return keys[i]; 
            }
            

            if(isLeaf == true) {
                return null;
            }

            return children[i].search(k);
        }
    }

    /*Calls the inorder transversal method if the root it not null */
    public ArrayList<String> inorderTransversal() {
        if(this.root != null) {
            this.root.inorderTransversal();
        }

        return values;
    }

    /*Calls the inorder transversal method if the root it not null */
    public ArrayList<Long> inorderTransversal2() {
        if(this.root != null) {
            this.root.inorderTransversal2();
        }

        return values2;
    }

    /*Calls the search method if the root it not null */
    public TreeObject search(String k) {
        if(this.root == null) {
            return null;
        }else {
            return this.root.search(k);
        }
    }

    
    /*The starting point for the insert method that decides if you are inserting into a root that is full and then you need to split nodes or if
     * the root is not full and the key can be inserted directly into the root node.
    */
    public void insert(TreeObject obj) throws IOException {
        Node r = root;
        if(r.numOfKeys == 2 * degree - 1) {
            Node s = BTreeSplitRoot();
            BTreeInsertNonFull(s, obj);
            incrementNumOfNodes();
            incrementNumOfNodes();
            
        }else {
            BTreeInsertNonFull(r, obj);
        }
    }

    /*Helper function for insert that splits the root if necessary and also calls splitchild*/
    public Node BTreeSplitRoot() {
        Node s = new Node(false, degree, nextDiskAddress);
        s.isLeaf = false;
        s.numOfKeys = 0;
        s.children[0] = root;
        height++;
        root = s;
        BTreeSplitChild(s, 0);
        return s;
    }

    /*Helper function for insert that is used to insert into a node if it is not full*/
    public void BTreeInsertNonFull (Node x, TreeObject k) {
        
        int i = x.numOfKeys - 1;
        
        if(x.isLeaf) {
            while(i >= 0 && k.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            if(i >= 0 && k.compareTo(x.keys[i]) == 0) {
                x.keys[i].incCount();
            }else {
                x.keys[i+1] = k;
                x.numOfKeys = x.numOfKeys + 1;
                size++;
            }
                
            //diskwrite(x)
        }else {
            while(i >= 0 && k.compareTo( x.keys[i]) < 0) {
                i = i - 1;
            }
            if(i >= 0 && k.compareTo(x.keys[i]) == 0) {
                x.keys[i].incCount();
                return;
            }else {
                i = i + 1;

                //diskread(x.children[i])
                if(x.children[i].numOfKeys == 2 * degree - 1) {
                    
                    for(int h = 0; h < x.children[i].numOfKeys; h++) {
                        
                        if(x.children[i].keys[h].compareTo(k) == 0) {
                            x.children[i].keys[h].incCount();
                            return;
                        }   
                    }
                    BTreeSplitChild(x, i);
                    if(k.compareTo( x.keys[i]) > 0) {
                        i = i + 1;
                        //diskread(x.children[i])
                    }
                }
                BTreeInsertNonFull(x.children[i], k);    
            }
        }
    }


    /*Helper function for insert that splits children nodes if necessary*/
    public void BTreeSplitChild(Node x, int i) {
        Node y = x.children[i]; 
        Node z = new Node(true, degree, nextDiskAddress);
        z.isLeaf = y.isLeaf;
        z.numOfKeys = degree - 1;
        for(int j = 0; j <= degree - 2; j ++) {  
            z.keys[j] = y.keys[j + degree];
            y.keys[j + degree] = null;
        }
        if(!y.isLeaf) {
            for(int j = 0; j <= degree - 1; j++) {
                z.children[j] = y.children[j + degree];
            }
        }
        y.numOfKeys = degree - 1;

        for(int j = x.numOfKeys; j >= i + 1; j--) {
            x.children[j + 1] = x.children[j];
        }
        x.children[i + 1] = z;

        for(int j = x.numOfKeys - 1; j >= i; j--) {
            x.keys[j + 1] = x.keys[j];
        }
        x.keys[i] = y.keys[degree - 1];
        y.keys[degree - 1] = null;
        x.numOfKeys = x.numOfKeys + 1;
        //diskwrite(y)
        //diskwrite(z)
        //diskwrite(x)
    }

    


    /*Read from the disk*/
    public Node diskRead(long diskAddress) throws IOException{
        
        if(diskAddress == 0) {
            return null;
        }
        file.position(diskAddress);
        buffer.clear();
        file.read(buffer);
        buffer.flip();
        int numberKeys = buffer.getInt();

        //make treeobject array
        TreeObject[] keyRead = new TreeObject[2* degree - 1];

        for(int i = 0; i < numberKeys; i++) {
            byte[] keyBytes = new byte[TreeObject.BYTES];
            buffer.get(keyBytes, i, TreeObject.BYTES);
            String key = new String(keyBytes, StandardCharsets.UTF_16BE).trim();
            long count = buffer.getLong();
            keyRead[i] = new TreeObject(key, count);
        }

        Node[] childrenRead = new Node[2* degree];

        for(int i = 0; i < degree * 2; i++){
            //byte[] childrenBytes = new byte[];
            //buffer.get(childrenBytes, i, ;
            Node child = new Node(false, i, diskAddress);
            childrenRead[i] = new Node(false, i, diskAddress);
        }

        byte flag = buffer.get();
        boolean leaf = false;
        if(flag == 1) {
            leaf = true;
        }

        Node x = new Node(leaf, flag, diskAddress);
        x.numOfKeys = numberKeys;
        x.isLeaf = leaf;
        x.children = childrenRead;
        x.keys = keyRead;
        x.address = diskAddress;
        
        return null;
    }



    
    /*DiskWrite for BTree */
    public void diskWrite(Node x) throws IOException{

        file.position(x.address);
        buffer.clear();
        buffer.putInt(x.numOfKeys);
        

        for(int i = 0; i < x.numOfKeys; i++) {
            TreeObject theKey = x.keys[i];

            buffer.put(theKey.getKey().getBytes(StandardCharsets.UTF_8));
            buffer.putLong(theKey.getCount());
        }

        
        if(x.isLeaf) {
            buffer.put((byte)1);
        }else {
            buffer.put((byte)0);
            for(int i = 0; i < x.numOfKeys; i++) {
                buffer.putLong(x.children[i].address);
            }
        }

        buffer.flip();
        file.write(buffer);
    }

    /*Method used to get the elements inside of the BTreeand then returns them as an array of strings */
    public String[] getSortedKeyArray() {

        ArrayList<String> inputs = inorderTransversal();

        String[] arr = new String[inputs.size()];
        arr = inputs.toArray(arr);

        return arr;

    }

    /*Method used to get the counts of each treeobject inside the btree and then retuns an array of longs */
    public long[] getSortedCount() {
        ArrayList<Long> inputs2 = inorderTransversal2();

        long[] arr2 = new long[inputs2.size()];
        System.out.println("\n--------------------\n");
        System.out.println(inputs2.size()+ "\n");
        int index = 0;
        //arr2 = inputs2.toArray(arr2);
        for(final long value : inputs2) {
            arr2[index++] = value;
            
        }

        return arr2;
    }

    void incrementNumOfNodes() {
        numOfNodes++;
    }


    long getSize() {
        return size;
    }


    int getDegree() {
        return degree;
    }

    int getNumberOfNodes() {
        return numOfNodes;
    }

    int getHeight() {
        return height;
    }

}


