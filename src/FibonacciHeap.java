

/**
 * name: Avital Gendelev
318266020
userName: gendelev1

name: Mirit Hadas
305248262
userName: mirithadas

 */

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	public HeapNode min = null;
	public int size = 0;
	public int marked = 0;
	public HeapNode firstRoot = null;
	public static int cuts = 0;
	public static int links = 0;
	public int trees = 0;
	
	
	public FibonacciHeap (HeapNode firstRoot) {//constructor
		this.firstRoot = firstRoot;
		
	}
	
	public FibonacciHeap () {//constructor
		this(null);
	}
	
	public void setSize(int size) {//Function to set the size of the heap
		this.size = size;
	}
	
	public void setMarked(int marked) {//Function to set the number of marked nodes
		this.marked = marked;
	}
	

   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()//Function to return true if the pile is empty
    {
    	if(this.size == 0) {
    		return true;
    	}
    	return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Returns the new node created. 
    */
    public HeapNode insert(int key)//Function to insert a new node
    { 
    	HeapNode node = new HeapNode(key);//new node
    	++trees;//a new tree
    	node.setRank(0);
    	this.size = this.size + 1;//increase the size
    	if(size == 1) {//our node is the first to be inserted
    		node.next = node;
    		node.prev = node;
    		this.min = node;//the min is the node because this is the first node
    	}
    	else if(firstRoot != null) {
    		if(key < this.min.key) {//update the minimum
        		this.min = node;
        	}
    		if(firstRoot.getPrev() == firstRoot) {//our node is the second to be inserted
    			firstRoot.setNext(node);//special case- the next and prev pointers are the same
    			node.setPrev(firstRoot);//special case- the next and prev pointers are the same
    		}
    		else {
    			node.setPrev(firstRoot.getPrev());//update the circular pointers
    			firstRoot.getPrev().setNext(node);//update the circular pointers
    		}
    	firstRoot.setPrev(node);// update the prev pointer
    	node.setNext(firstRoot); // update the next pointer
    	}
    	firstRoot = node;//update the firstRoot to be the new node that was inserted
    	
    	return node;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {//to check about the  marked
    	--trees;//we will delete the minimum node
    	if(size == 1) {//if the pile's size is 1, after the deletion its 0
    		this.firstRoot = null;
    		--size;
    		this.min = null;
    		return;
    	}
    	boolean notDeleted = true;
    	HeapNode[] listByRanks = new HeapNode[this.size + 1];//our buckets
    	HeapNode curr = this.firstRoot;
    	HeapNode next = curr.getNext();
    	notDeleted = chekingIfNeedToDelete(notDeleted, listByRanks, curr);
    	while(next != this.firstRoot) {//while we didn't complete a hole circle
    		curr = next;
    		next = next.getNext();
    		notDeleted = chekingIfNeedToDelete(notDeleted, listByRanks, curr);
    	}
    	boolean first = true;
    	trees = 0;
    	for(int i = 0; i < listByRanks.length; i++) {//iterating over the list
    		if(listByRanks[i] != null) {
    			if(first) {
    				++trees;
    				this.firstRoot = listByRanks[i];//update the first root
    				firstRoot.setNext(firstRoot);
    				firstRoot.setPrev(firstRoot);
    				if(this.min == null) {
    					this.min = this.firstRoot;
    				}
    				else {
    					if(this.firstRoot.getKey() < this.min.getKey()) {
        					this.min = this.firstRoot;
        				}
    				}
    				
    				first = false;
    			}
    			else {
    				FibonacciHeap tmpHeap = new FibonacciHeap();
    				tmpHeap.firstRoot = listByRanks[i];
    				listByRanks[i].setNext(listByRanks[i]);
    				listByRanks[i].setPrev(listByRanks[i]);
					tmpHeap.min = listByRanks[i];
					tmpHeap.trees = 1;
					this.meld(tmpHeap);//melding all the trees to our heap
    			}	
    		}
    	}
    	--this.size;//we deleted a node
    }
    
    /**
     * 
     * @param notDeleted- indicates if we already deleted the min
     * @param listByRanks- the buckets
     * @param curr- our location in the pile
     * @return boolean that indicates if we deleted the min
     */
    public boolean chekingIfNeedToDelete(boolean notDeleted, HeapNode[] listByRanks,  HeapNode curr) {
    	if(curr.getKey() == this.min.getKey() && notDeleted) {//we need to ignore the min, and iterate over his children
			if(curr.getNext() == this.firstRoot) {
				if(curr.getLeftChild() != null) {
					this.min = curr.getLeftChild();//we deleted the min
				}
				else {
					this.min = null;
				}
			}
			else {
				this.min = curr.getNext();//we deleted the min
			}
			curr.setNext(null);//update his pointers to null
			curr.setPrev(null);//update his pointers to null
			notDeleted = false;
			
			HeapNode child = curr.getLeftChild();
			if(child != null) {//the node has children
				++trees;
				if(child.getMark() == true) {
					child.setMark(false);//children of the deleted item are no longer marked
					--this.marked;
				}
				HeapNode tmp = child.getNext();//saving his next child
				updatingThePointers(child, listByRanks);
				child = tmp;
    			while(curr.getLeftChild() != child && child != null) {//while we didn't complete a hole circle
    				++trees;
    				HeapNode tmp2 = child.getNext();//saving his next child
    				updatingThePointers(child, listByRanks);
    				child = tmp2;
    			}
    			curr.setLeftChild(null);
			}
		}
		else {
			curr.setNext(null);
			curr.setPrev(null);
			insertIntoBucket(listByRanks, curr);//insert or link
		}
    	return notDeleted;
    }
    
    public void updatingThePointers(HeapNode node, HeapNode[] listByRanks) {
		node.setNext(null);
		node.setPrev(null);
		
		insertIntoBucket(listByRanks, node);//insert or link
		
    }
    /**
     * 
     * @param listByRanks - the buckets
     * @param node - root
     * if there is a tree in the list: tree.rank == child.rank ---> link
     * else: insert into the bucket[child.rank]
     * updating the min
     */
    public void insertIntoBucket (HeapNode[] listByRanks,  HeapNode node) {
    	while(listByRanks[node.getRank()] != null) {//we need to link the left child
    		HeapNode tmp = listByRanks[node.getRank()];
    		listByRanks[node.getRank()] = null;
			node = this.link(tmp, node);//link
    	}
    	listByRanks[node.getRank()]= node;//adding to the bucket
    	if(listByRanks[node.getRank()].getKey() < this.min.getKey()) {//update the min
				this.min = listByRanks[node.getRank()];
		}
    }
    /**
     * 
     * @param a is not empty
     * @param b is not empty
     * @return a HeapNode (the first root)
     */
    public HeapNode link(HeapNode a, HeapNode b) {//function to link two trees with the same rank. a, b are pointers to the FirstRoot of the trees
    	--trees;//we link two trees;
    	if(a.key > b.key) {//a is always the smaller one
    		HeapNode tmp = a;
    		a = b;
    		b = tmp;
    	}
    	if(a.getLeftChild() == null) {//our node is a single node
    		a.setLeftChild(b);//b is the left child of a
    		b.setParent(a);//a is the parent of a
    		a.setRank(1);//the rank of a is 1
    		a.setNext(a);
    		a.setPrev(a);
    		b.setNext(b);
    		b.setPrev(b);
    	}
    	else {
    		a.getLeftChild().getPrev().setNext(b);//update the circular pointers
    		b.setPrev(a.leftChild.getPrev());//update the circular pointers
    		b.setNext(a.leftChild);//update the next pointer
    		a.getLeftChild().setPrev(b);//update the prev pointer
    		a.setLeftChild(b);//connecting to the child
    		b.setParent(a);//connecting to the parent
    		a.setRank(a.getRank() + 1);//the rank of a is rank(a) + 1
    	}
    	FibonacciHeap heap = new FibonacciHeap(a);//creating a new heap
    	int sum = a.getRank() + b.getRank();//the size of the new heap is 2^( rank of a and b)
    	int size = (int) Math.pow(2, sum);
    	heap.setSize(size);
    	heap.min = a;
    	
    	//we need to figure out about the new heap "marked" field
    	
        ++links;    
    	return a;//returning a heap
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()//Function to return the minimum in the heap
    {
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	  if(heap2.min.getKey() < this.min.getKey()) {//checking if we need to update the min
    		  this.min = heap2.min;
    	  }
    	  this.size = this.size + heap2.size;//update the size
    	  this.marked = this.marked + heap2.marked;//update the marked
    	  this.trees = this.trees + heap2.trees;//update the count of trees;
    	  
    	  HeapNode tmpHeap2Last = heap2.firstRoot.getPrev();//tmp pointer to the last element of heap2
    	  HeapNode tmpThisLast = this.firstRoot.getPrev();//tmp pointer to the last element of this
    	  heap2.firstRoot.getPrev().setNext(this.firstRoot);//update the circular pointers
    	  this.firstRoot.setPrev(tmpHeap2Last);//update the circular pointers
    	  heap2.firstRoot.setPrev(tmpThisLast);// update the prev pointer
    	  tmpThisLast.setNext(heap2.firstRoot);// update the next pointer
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()//Function to return the size of the pile
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
	int[] arr = new int[this.size];//count list
    HeapNode curr = this.firstRoot;//starting to iterate from the first root
    ++arr[curr.getRank()];//updating the cell who's index is the rank of the first tree
    curr = curr.getNext();//going left
    while(curr != this.firstRoot) {//while we didn't complete a hole circle
    	++arr[curr.getRank()];//updating the cell who's index is the rank of the first tree
    	curr = curr.getNext();
    }
    return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	 decreaseKey(x, Integer.MAX_VALUE);
         deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	x.setKey(x.getKey() - delta);//update the key to be key = key - delta
    	
    	if(x.getParent() != null) {//x is not a root
    		if(x.getKey() < x.getParent().getKey()) {//a violation 
        		cascadingCut(x);//Performing cuts
        	}
    	}
    	
    	if(x.getKey() < this.min.getKey()) {//we need to update the min
    		this.min = x;
    	}
    }
    
    public void cascadingCut (HeapNode x) {
    	HeapNode parent = x.getParent();
    	cut(x);
    	if(parent.getParent() != null) {//parent is not a root
    		if(parent.getMark() == false) {//if this is his first lost child we will mark him
    			parent.setMark(true);
    			++this.marked;
    		}
    		else {//we need to do another cut because only one cut is aloud
    			cascadingCut(parent);
    		}
    	}
    }
    /**
     * 
     * @param x is not a root
     */
    public void cut (HeapNode x) {//cut the node x from his parent
    	++cuts;
    	HeapNode y = x.getParent();
    	x.setParent(null); //his parent is null now
    	if(x.getMark()) {//he is marked
    		--this.marked;
    		x.setMark(false);//he is unmarked
    	}

    	y.setRank(y.getRank() - 1);//the rank of the parent decremented
    	
    	if(x.getNext() == x) {//x is the only child of y
    		y.setLeftChild(null);
    	}
    	else {//we need to remove x from the list of the children of y
    		HeapNode curr = y.getLeftChild();
    		if(curr == x) {//the first child need to be deleted
    			y.setLeftChild(curr.getNext());//his parent's first left child is curr.next
    			x.getNext().setPrev(x.getPrev());
    			x.getPrev().setNext(x.getNext());
    		}
    		else {
    			x.getNext().setPrev(x.getPrev());
    			x.getPrev().setNext(x.getNext());
    		}
    	}
    	FibonacciHeap tmpHeap = new FibonacciHeap();
		tmpHeap.firstRoot = x;
		x.setNext(x);
		x.setPrev(x);
		tmpHeap.min = x;
		tmpHeap.trees = 1;
		tmpHeap.size = 0;
		tmpHeap.marked = 0;
		this.meld(tmpHeap);//the deleted node is inserted as the most left node
    }
    	
 

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() //Function to return the potential
    {    
    	return this.trees + (2 * this.marked);
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()//Function to return the total links
    {    
    	return links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()//Function to return the number of cuts
    {    
    	return cuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[k];
        boolean first = true;
        FibonacciHeap virtualHeap = new FibonacciHeap();
        HeapNode curr = H.firstRoot;
        for(int i = 0; i < k; i++) {//we want to find k minimal elements
        	if(curr != null) {
        		HeapNode firstNode = curr;
        		first = true;
        		while(curr != firstNode || first) {//we didn't complete a hole circle
        			first = false;
        			HeapNode tmp = virtualHeap.insert(curr.getKey());//insert to the virtualHeap
        			tmp.setValue(curr);//the connection to the real pile
        			curr = curr.getNext();//going to the next brother
        		}
        	}
        	curr = virtualHeap.findMin().getValue();//the real minimum node
        	arr[i] = curr.getKey();//insert to the array
        	virtualHeap.deleteMin();//delete from the virtualHeap
        	curr = curr.getLeftChild();//going to the children of the min
        }
        return arr;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	public HeapNode value = null;
	private HeapNode leftChild = null;
	private HeapNode next = null;
	private HeapNode prev = null;
	private HeapNode parent = null;
	private boolean mark = false;
	private int rank = 0;

  	public HeapNode(int key) {//Constructor
	    this.key = key;
	    
      }

  	public int getKey() {//Function to get the key
	    return this.key;
      }
  	public void setKey(int key) {//Function to set the key
  		this.key = key;
  	}
  	
  	public void setValue(HeapNode val) {//Function to set the val
  		this.value = val;
  	}
  	
  	public HeapNode getValue() {//Function to return the val
  		return this.value;
  	}
  	
  	public HeapNode getLeftChild() {//Function to get the left child
  		return this.leftChild;
  	}
  	public void setLeftChild(HeapNode leftChild) {//Function to set the left child
  		this.leftChild = leftChild;
  	}
  	
  	public HeapNode getNext() {//Function to get the next node
  		return this.next;
  	}
  	public void setNext (HeapNode next) {//Function to set the next node
  		this.next = next;
  	}
  	
  	public HeapNode getPrev() {//Function to get the prev node
  		return this.prev;
  	}
  	public void setPrev (HeapNode prev) {//Function to set the prev node
  		this.prev = prev;
  	}
  	
  	public HeapNode getParent() {//Function to get the parent of the node
  		return this.parent;
  	}
  	public void setParent (HeapNode parent) {//Function to set the parent of the node
  		this.parent = parent;
  	}
  	
  	public int getRank() {//Function to get the rank of the node
  		return this.rank;
  	}
  	public void setRank (int rank) {//Function to set the rank of the node
  		this.rank = rank;
  	}
  	
  	public boolean getMark() {//Function to get the mark of the node
  		return this.mark;
  	}
  	public void setMark (boolean newMark) {//Function to set the mark of the node to be marked
  		this.mark = newMark;
  	}

    }
}
