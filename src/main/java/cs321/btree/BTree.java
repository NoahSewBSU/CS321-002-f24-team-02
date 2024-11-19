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



    private Node root = new Node(true, METADATA_SIZE, nextDiskAddress);
    private int height;
    private int degree;
    private int size;
    private int numOfNodes;
    private String filename;
    private int t;




    /*Constructors */
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
        

        public Node(boolean isLeaf, int t, long address) {
            this.isLeaf = isLeaf;
            this.keys = new TreeObject[2*t - 1];
            this.children = new Node[2 *t];
            this.numOfKeys = 0;
            this.address = address;
            this.t = t;

        }

        public Node(TreeObject[] keys, Node[] children, boolean isLeaf, int keyCount, long adrresss) {
            this.keys = keys;
            this.children = children;
            this.isLeaf = isLeaf;
            this.numOfKeys = keyCount;
            this.address = adrresss;
        }


        public TreeObject search(String k) {
            int i = 0;
            while(i < root.numOfKeys && k.compareTo(keys[i].getKey()) > 0) {
                i++;
            }

            if(numOfKeys == 0) {
                return null;
            }

            if(keys[i].getKey() == k) {
                return this.keys[i];
            }
            if(isLeaf == true) {
                return null;
            }

            return children[i].search(k);
        }
    }

    public TreeObject search(String k) {
        if(this.root == null) {
            return null;
        }else {
            return this.root.search(k);
        }
    }

    



    /*Insert function */
    
    public void insert(TreeObject obj) throws IOException {
        
        Node r = root;

        if(r.numOfKeys == 2 * degree - 1) {
            Node s = BTreeSplitRoot();
            BTreeInsertNonFull(s, obj);
            incrementNumOfNodes();
            size++;

        }else {
            BTreeInsertNonFull(r, obj);
            size++;
        }
    }

    /*Helper function for insert */
    
    public Node BTreeSplitRoot() {
        Node s = new Node(false, METADATA_SIZE, nextDiskAddress);
        s.isLeaf = false;
        s.numOfKeys = 0;
        s.children[0] = root;
        height++;
        root = s;
        BTreeSplitChild(s, 0);
        return s;

    }

        /*Helper function for insert */
        
    public void BTreeInsertNonFull (Node x, TreeObject k) {
        
        int i = x.numOfKeys - 1;
        
        if(x.isLeaf) {
            while(i >= 0 && k.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i+1] = k;
            x.numOfKeys = x.numOfKeys + 1;  
            //diskwrite(x)
        }else {
            while(i >= 0 && k.compareTo( x.keys[i]) < 0) {
                i = i - 1;
            }
            i = i + 1;
            //diskread(x.children[i])
            if(x.children[i].numOfKeys == 2 * degree - 1) {
                BTreeSplitChild(x, i);
                if(k.compareTo( x.keys[i]) > 0) {
                    i = i + 1;
                    //diskread(x.children[i])
                }
            }
            BTreeInsertNonFull(x.children[i], k);
        }
    }


    /*Helper function for insert */
    
    public void BTreeSplitChild(Node x, int i) {
        Node y = x.children[i]; 
        Node z = new Node(true, METADATA_SIZE, nextDiskAddress);
        z.isLeaf = y.isLeaf;
        z.numOfKeys = degree - 1;
        for(int j = 0; j <= degree - 2; j ++) {  
            z.keys[j] = y.keys[j + degree];
        }
        if(!y.isLeaf) {
            for(int j = 0; j <= degree - 1; j++) {
                z.children[j] = y.children[j + degree];
            }
        }
        y.numOfKeys = degree - 1;

        for(int j = x.numOfKeys; j >= i + 1; i--) {
            x.children[j + 1] = x.children[j];
        }
        x.children[i + 1] = z;


        for(int j = x.numOfKeys - 1; j >= i; i--) {
            x.keys[j + 1] = x.keys[j];
        }
        x.keys[i] = y.keys[degree - 1];
        x.numOfKeys = x.numOfKeys + 1;

        //diskwrite(y)
        //diskwrite(z)
        //diskwrite(x)
    }

    


    /*Read from the disk*/
    
    public Node diskRead(long diskAddress) throws IOException{
        /*
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
            byte[] childrenBytes = new byte[Node.BYTES];
            buffer.get(childrenBytes, i, Node.BYTES);
            Node child = new Node ()
            childrenRead[i] = new Node(false, i, diskAddress);
        }




        byte flag = buffer.get();
        boolean leaf = false;
        if(flag == 1) {
            leaf = true;
        }




        Node x = new Node();
        x.numOfKeys = numberKeys;
        x.isLeaf = leaf;
        x.children = childrenRead;
        x.keys = keyRead;
        x.address = diskAddress;
        
        return x;
        */
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


    

    private ArrayList<String> values = new ArrayList<String>();
    String[] getSortedKeyArray() {

        ArrayList<String> inputs = inorderTransversal(root);

        String[] arr = new String[inputs.size()];
        arr = inputs.toArray(arr);

        return arr;

    }

    ArrayList<String> inorderTransversal(Node node) {
        
        for(int i = 0; i < node.children.length; i++) {
            if(node.children[i] != null) {
                inorderTransversal(node.children[i]);
            }
        }

        for(int j = 0; j < node.numOfKeys; j++) {
            values.add(node.keys[j].getKey());
        }

        return values;
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


