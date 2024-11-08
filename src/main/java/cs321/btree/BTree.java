package cs321.btree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
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



    private Node root = new Node(false, METADATA_SIZE, nextDiskAddress);
    private int height;
    private int degree;
    private int size;
    private int numOfNodes;
    private String filename;




    /*Constructor */
    public BTree(String filename) {
        this.filename = filename;
        this.height = 0;
        this.degree = 2;
        this.size = 0;
        this.numOfNodes = 1;
    }

    public BTree(int degree, String filename) {
        this.height = 0;
        this.degree = degree;
        this.size = 0;
        this.numOfNodes = 1;
        this.root = new Node(true, 2, METADATA_SIZE);
    }

    



    
    private class Node {

        private long address;
        private int numOfKeys;
        private boolean isLeaf;
        private TreeObject[] keys;
        private Node[] children;
        

        public Node(boolean isLeaf, int t, long address) {
            this.isLeaf = isLeaf;
            this.keys = new TreeObject[2*t - 1];
            this.children = new Node[2 *t];
            this.numOfKeys = 0;
            this.address = address;
        }
    }

    
    void insert(TreeObject obj) throws IOException {
        
        Node r = root;

        if(r.numOfKeys == 2 *degree -1) {
            Node s = BTreeSplitRoot(root);
            BTreeInsertNonFull(s, obj);
        }else {
            BTreeInsertNonFull(r, obj);
        }
    }

    public Node BTreeSplitRoot(Node T) {
        Node s = new Node(false, METADATA_SIZE, nextDiskAddress);
        s.isLeaf = false;
        s.numOfKeys = 0;
        s.children[0] = root;

        root = s;
        BTreeSplitChild(s, 0);
        return s;

    }

    public void BTreeInsertNonFull (Node x, TreeObject k) {


        size++;
        
        int i = x.numOfKeys - 1;
        
        if(x.isLeaf) {
            while(i >= 0 && k.compareTo( x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                i = i - 1;
            }
            x.keys[i+1] = k;
            x.numOfKeys = x.numOfKeys + 1;
            try {
            diskWrite(x);
            }catch(IOException ie) {

            }
        }
        
        else {
            while(i >= 0 && k.compareTo( x.keys[i]) < 1) {
                i = i - 1;
            }
            i = i + 1;
            try{
            diskRead(root.address);
            }catch(IOException ie) {

            }
            if(x.children[i].numOfKeys == 2 * degree -1) {
                BTreeSplitChild(x, i);
                if(k.compareTo( x.keys[i]) < 0) {
                    i = i + 1;
                    try {
                    diskWrite(x.children[i]);
                    }catch(IOException ie) {

                    }
                }
            }
            BTreeInsertNonFull(x.children[i], k);
        }
    }



    public void BTreeSplitChild(Node x, int i) {
        Node y = x.children[i];
        Node z = new Node(false, METADATA_SIZE, nextDiskAddress);
        z.isLeaf = y.isLeaf;
        z.numOfKeys = degree - 1;
        for(int j = 0; j < degree - 1; j ++) {
            z.keys[j] = y.keys[j + degree];
        }

        if(!y.isLeaf) {
            for(int j = 0; j < degree - 1; j++) {
                z.children[j] = y.children[j + degree];
            }
        }
        y.numOfKeys = degree - 1;
        for(int j = x.numOfKeys; j > i + degree; i--) {
            x.children[j + 1] = x.children[j];
        }
        x.children[i + 1] = z;
        for(int j = x.numOfKeys; j > i; i--) {
            x.keys[j + 1] =x.keys[j];
        }
        x.keys[i] = y.keys[degree -1];
        x.numOfKeys = x.numOfKeys + 1;
        try{
        diskWrite(y);
        diskWrite(z);
        diskWrite(x);
        }catch(IOException ie) {

        }
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

        byte flag = buffer.get();
        boolean leaf = false;
        if(flag == 1) {
            leaf = true;
        }



        Node x = new Node(leaf, 1, diskAddress);



        return x;
    }



    
    /*DiskWrite for BTree */
    
    public void diskWrite(Node x) throws IOException{

        
        try {

        File myFile = new File(filename);

        RandomAccessFile dataFile = new RandomAccessFile(filename, "rw");
        file = dataFile.getChannel();

        
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

        dataFile.close();

        } catch (Exception e) {

        }
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
