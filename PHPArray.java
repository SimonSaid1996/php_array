import java.util.*;
import java.util.Iterator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class PHPArray<V> implements Iterable<V>{	
	public static Node firstNode;
	public int totalSize;
	public int currentN;
	public Node[] hashTable;
	public static Node IterNode;
	public int currentTracker;
	public Node[] tempTable;
	public Node[] tempTableStore;
	 
	public PHPArray(int size){
		totalSize=size;
		currentN=0;
		currentTracker=0;
		firstNode=null;
		hashTable=new Node[size];
		IterNode=firstNode;
		tempTable=null;
		tempTableStore=null;
				
	}
	
	public int getTotalSize(){
		return totalSize;
	}
	
	public void setFirstNode(Node obj){
		firstNode=obj;
	}
	
	public Node getFirstNode(){		
		return firstNode;
	}
	
	public Iterator<V> iterator() {		
		return new PHPIterator<V>();
	}
	
	private static class Node<V> implements Comparable<Node<V>>{
       private Node next;//to go to the next node
	   private Node prev;
	   private  V value;
       private String key;//to check if the words end6
	   
       private Node(Node nextN,Node prevN,String k,V currentVal){//node class has also be comparable and write a compareTo method inside to compare the value to compare the value 
       this.next=nextN;
	   this.key=k;
       this.value=currentVal;
	   this.prev=prevN;
       }
		public String getKey(){
			return key;
		}
		
		public V getValue(){
			return value;
		}
		
		public void setPrev(Node p){
			prev=p;
		}
		
		public void setNext(Node n){
			next=n;
		}
		
		public Node getPrevNode(){
			return prev;
		}
		
		public Node getNextNode(){
			return next;
		}
		
		public int compareTo(Node<V> anotherNode){
			//if object doesn't implement comparable, then throws the illegalargumentexception, think abt it later
			try{
				((Comparable<V>)value).compareTo(anotherNode.value);
			}
			catch (Exception e){//it is hard to compare the generic types and this is as close to the correct answer as i can implement
				throw new IllegalArgumentException("can't compare two different classes");
			}
			
			return   ((Comparable<V>)value).compareTo(anotherNode.value);			
		} 
       
    }
	
	public static class Pair<V> implements Comparable<Pair<V>>{
		private  V value;
		private String key;//to check if the words end6
		
		private Pair(String k,V currentVal){//node class has also be comparable and write a compareTo method inside to compare the value to compare the value 
			this.key=k;
			this.value=currentVal;
		}
			
		public int compareTo(Pair<V> anotherPair){//this method will never be called, don't really need to implement it 
			return  0;//i made it here just to keep consistant with the node class			
		} 
	}
	
	public Pair<V> each(){//not so formally implemented, i guess
	Pair myPair=null;	
		if(IterNode!=null){
			myPair=new Pair(IterNode.getKey(),IterNode.getValue());
			IterNode=IterNode.getNextNode();
			currentTracker++;
			return myPair;
		}
		else{
			if(currentTracker==0){
				IterNode=firstNode;
				myPair=new Pair(IterNode.getKey(),IterNode.getValue());
				IterNode=IterNode.getNextNode();	
			}
		}
		return myPair;	
	}
	
	public ArrayList<String> keys(){//to return keys
		ArrayList<String> mykeys=new ArrayList<String>();
		Node myNode=firstNode;
			while(myNode.getNextNode()!=null){//iterate through the linked list
				mykeys.add(myNode.getKey());
				myNode=myNode.getNextNode();
			}
			mykeys.add(myNode.getKey());
		return mykeys;
	}
	
	public ArrayList<V> values(){//to return values
		ArrayList<V> myValues=new ArrayList<V>();
		Node myNode=firstNode;
			while(myNode.getNextNode()!=null){//iterate through the linked list
				myValues.add((V)myNode.getValue());
				myNode=myNode.getNextNode();
			}
			myValues.add((V)myNode.getValue());
		return myValues;		
	}
	
	private static class PHPIterator<V> implements Iterator<V>{//check about how to use the iterator, not super sure 6
		private static Node nextNode;
		private static Node lastAccessed=null;		
		
		public PHPIterator(){
			nextNode=firstNode;
		}
		public boolean hasNext(){			
			return nextNode!=null;
		}
		public V getCurrent(){
			
			return (V)nextNode;
		}
		
		public V next(){
			//V returnValue;
			if(hasNext()){//think about this line more
				Node returnNode=nextNode;
				nextNode=returnNode.next;
				return (V)returnNode.getValue();
			}
			else{
				throw new NoSuchElementException("Iterator at end of list");
			}
		}
		
		public V gotoNext(){
			if(hasNext()){//think about this line more
				Node returnNode=nextNode;
				nextNode=returnNode.next;
				return (V)returnNode;
			}
			else{
				throw new NoSuchElementException("Iterator at end of list");
			}			
		}
		
	}
	
	public void put(int k,V value){
		//change the k into string here and call the otehr put functions
		String key=Integer.toString(k);
		put(key,value);
	}
	//method overload, 
	public void put(String k, V value){//need to add the iterator part later, right now only assume adding time not 1
		int probN;
		int position=hashingFunction(k);
		Node addedNode=new Node(null,null,k,value);
		//adding node into the list first so that we will be able to store nodes with pointers in the array which will grant us direct access to the nodes in the list and 
		//achieve delete time of O(1)
			if(firstNode==null){
				firstNode=addedNode;//replace first node with my node
			}
			else{
				addedNode.next=firstNode;
				firstNode.prev=addedNode;
				firstNode=addedNode;//move the first node to myNode
			}
		
		if(currentN>=totalSize/2){
			doubleSize();//reset size to two times as now 
		}
		
		if(hashTable[position]==null){//direct adding
			hashTable[position]=addedNode;//the first node to be add in the index
			probN=-1;//set up a flag to end prob
		}
		else{//has to probing
			if(position==hashTable.length-1)
				probN=0;//need to go to the beginning again
			else
				probN=position+1;//prob to the next index			
		}
		
		while((probN!=position)&&(probN!=-1)){//probing situation, exit when probN set to -1 or come back the original position again
			if(hashTable[probN]==null){
				hashTable[probN]=addedNode;//this node should connect to the prev node
				probN=-1;//to get out of the loop
			}
			else{
				if(probN==totalSize-1)//go back from the beginning
					probN=0;
				else
				probN++;
			}
		}
		if(probN==-1){
			currentN++;
		}		
	}
	
	public void asort(){
		preProcessSort();
			for(int j=currentN-1;j>=0;j--){
				Node anotherNode=new Node(null,null,tempTable[j].getKey(),tempTable[j].getValue());
				if(firstNode==null){
					firstNode=anotherNode;
				}
				else{
					anotherNode.next=firstNode;
					firstNode.prev=anotherNode;
					firstNode=anotherNode;
				}
			}
		IterNode=firstNode;	//reset everything
	}
	
	public void sort(){//i used mergesort here to finish my sorting
		preProcessSort();
			for(int j=currentN-1;j>=0;j--){
				Node anotherNode=new Node(null,null,Integer.toString(j),tempTable[j].getValue());//reassign the key value
				if(firstNode==null){
					firstNode=anotherNode;
				}
				else{
					anotherNode.next=firstNode;
					firstNode.prev=anotherNode;
					firstNode=anotherNode;
				}
			}
		IterNode=firstNode;	//reset everything
	}
	public void preProcessSort(){
		tempTable=new Node[currentN];
		tempTableStore=new Node[currentN];
		Node temp=firstNode;
		int i=0;
			while(temp.getNextNode()!=null){
				tempTable[i]=temp;
				temp=temp.getNextNode();
				i++;
			}
		tempTable[i]=temp;//add the last node
		sortHelper1(0,currentN-1);
		firstNode=null;
	}
	
	public void sortHelper1(int lowN, int highN){
		if(lowN<highN){
			int middleN=lowN+(highN-lowN)/2;
			sortHelper1(lowN,middleN);
			sortHelper1(middleN+1,highN);
			sortHelper2(lowN,middleN,highN);//to sort all
		}
	}
	
	public void sortHelper2(int lowN,int middleN,int highN){
		for(int i=lowN;i<=highN;i++){
			tempTableStore[i]=tempTable[i];
		}
		int i=lowN;
		int j=middleN+1;
		int k=lowN;
			while(i<middleN&&j<=highN){
				int compareResult=tempTableStore[i].compareTo(tempTableStore[j]);
				if(compareResult>=0){//might have issues here
					tempTable[k]=tempTableStore[i];
					i++;
				}
				else{
					tempTable[k]=tempTableStore[j];
					j++;
				}
				k++;
			}
			while(i<middleN){
				tempTable[k]=tempTableStore[i];
				i++;
				k++;
			}
	}
		
	public void doubleSize(){
		totalSize=totalSize*2;
		Node[] newHashTable=new Node[totalSize];
			for(int i=0;i<=(totalSize/2)-1;i++){//move every elements in the hash table into the new hashtable
				if(hashTable[i]!=null){
					int relocIndex=hashingFunction(hashTable[i].getKey());
						if(newHashTable[relocIndex]==null){
							newHashTable[relocIndex]=hashTable[i];
						}
						else{
							while(newHashTable[relocIndex]!=null){//already doubled the size, so won't need to worry about relocIndex get out of bound
								relocIndex++;
							}
							newHashTable[relocIndex]=hashTable[i];
						}
				}
			}
		hashTable=newHashTable;//replace the hashTable	
	}
	
	public V get(int i){
		String key=Integer.toString(i);
		return get(key);
	}
	
	public V get(String k){//this one need to be changed too, retriving time not o1
		int probN=0;
		int position=hashingFunction(k);
		if(hashTable[position]==null)//direct retrieve
			return null;
		else if(hashTable[position].getKey().equals(k))//directly get the required value
			return (V)hashTable[position].getValue();	
		else{//the probing situations
			if(position==totalSize-1)
				probN=0;
			else
				probN=position+1;
		}
		while((probN!=-1&&probN!=position)){//search here
			if(hashTable[probN]==null)//if meeting null in the middle, terminate searching
				return null;
			else if(hashTable[probN].getKey().equals(k)){//find the node and value
				return (V)hashTable[probN].getValue();
			}
			else{
				if(probN==totalSize-1)//go back to the begining of the table
					probN=0;
				else//adding the probing num
					probN++;
			}				
		}
		return null;//default not find		
	}
	
	public void unset(int i){
		String key=Integer.toString(i);
		unset(key);		
	}
	
	public void unset(String k){//to remove a key from the PHPArray
		int probN=0;
		int position=hashingFunction(k);
		Node deletedNode=null;
		
		//deleting node check in the array first, if not found in the array, no operations in the list either
		if(hashTable[position]==null){//nothing found, already deleted
						
		}
		else if(hashTable[position].getKey().equals(k)){//find it ,need to remove it
			deletedNode=hashTable[position];
			hashTable[position]=null;//set it back to null
			probN=-1;
		}
		else{//need to prob to find the key
			if(position!=totalSize-1){
				probN=position+1;
			}
			else{
				probN=0;
			}
		}
		while(probN!=-1&&probN!=position){
			if(hashTable[probN]==null){//break in the middle, no need to find
				
			}
			else if(hashTable[probN].getKey().equals(k)){//find case
				deletedNode=hashTable[position];
				hashTable[probN]=null;
			}
			else{//moving to the next 
				if(probN==totalSize-1){
					probN=0;
				}
				else{
					probN++;
				}
			}
		}
		Node curNode=deletedNode;
			if(curNode!=null){//update the previous and latter node to connect them together
				Node prevN=curNode.getPrevNode();
				Node nextN=curNode.getNextNode();
				prevN.setNext(nextN);
				nextN.setPrev(prevN);
				currentN--;//update the number of currentN
			}
	}
	
	public void showTable(){//to print the array table
		for(int i=0;i<=totalSize-1;i++){
			if(hashTable[i]!=null){
				System.out.println(i+". key is "+hashTable[i].getKey()+". value is "+hashTable[i].getValue());
			}
			else{
				System.out.println(i+". null");
			}
		}
	}
	
	public int hashingFunction(String input){//to get the hash for everyinput
		return (input.hashCode() & 0x7fffffff) % totalSize;//(Math.abs(input.hashCode())%totalSize);
	}
	
	public PHPArray<V> array_reverse(){//to reverse the order of the array
		Node CurNode=this.getFirstNode();
		Node replacedFirstNode=null;
			while(CurNode.getNextNode()!=null){
				Node tempNode;
				if(replacedFirstNode==null){
				tempNode=new Node(null,null,CurNode.getKey(),CurNode.getValue());
				replacedFirstNode=tempNode;
				}
				else{
					tempNode=new Node(null,null,CurNode.getKey(),CurNode.getValue());
					tempNode.next=replacedFirstNode;
					replacedFirstNode.prev=tempNode;
					replacedFirstNode=tempNode;
				}
				CurNode=CurNode.getNextNode();
			}
		Node tempNode=new Node(null,null,CurNode.getKey(),CurNode.getValue());
		tempNode.next=replacedFirstNode;
		replacedFirstNode.prev=tempNode;
		replacedFirstNode=tempNode;
		this.setFirstNode(replacedFirstNode);
		return this;
	}
	
	public boolean array_key_exists(String k){
		boolean KeyExist=false;
		Node CurNode=firstNode;
		while(CurNode.getNextNode()!=null){
			if(CurNode.getKey().equals(k)){
				KeyExist=true;
				break;
			}
			CurNode=CurNode.getNextNode();
		}
		if(CurNode.getKey().equals(k)){//check the last node
				KeyExist=true;
		}
		return KeyExist;
	}
	
	public PHPArray<String> array_flip(){//attempt to get the class of PHPArray, but can only get Object type, not the class which implements Object
		Node curNode=this.getFirstNode();
		//Type genericSuperClass = this.getClass().getGenericSuperclass();
		//System.out.println(this.getClass().getGenericSuperclass());
		//ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();		
		
		PHPArray<String> flipped=new PHPArray(this.getTotalSize());
			while(curNode.getNextNode()!=null){
				flipped.put((String)curNode.getValue(),curNode.getKey());
				curNode=curNode.getNextNode();
			}
		flipped.put((String)curNode.getValue(),curNode.getKey());//add the last key
		return flipped;
	}
	
	
	public static <V> void testFlip(PHPArray<V> Ar)//a function to test array_flip
	{
		try
		{
			PHPArray<String> Aflip =Ar.array_flip();
			PHPArray.Pair<String> currA;
			System.out.println("\tFlipped data:");
			while ((currA = Aflip.each()) != null)
			{
				System.out.println("Key: " + currA.key + " Value: " + currA.value);
			}
			System.out.println();
		}
		catch (ClassCastException e)
		{
			System.out.println(e.toString());
			System.out.println();
		}
	}
	
	public static void main(String [] args) 
	{
	PHPArray<Integer> A = new PHPArray<Integer>(7);
	A.put("www",2);
	A.put(2,4);
	A.put("SNL",5);
	A.put("ooo",1);
	A.put("xxx",9);
	A.put("qqq",11);
	A.put("ede",3);
	
	A.unset("xxx");
	System.out.println("int is "+A.get("www"));
	System.out.println("int is "+A.get(2));
	A.unset(2);
	A.showTable();
	
	Iterator<Integer> iter = A.iterator();	
		while (iter.hasNext())
		{
			System.out.println("Next item is " + iter.next());
		}
		System.out.println();
		
	PHPArray.Pair<Integer> curr;
	System.out.println("test pair and each method");
		while ((curr = A.each()) != null)
		{
			System.out.println("Key: " + curr.key + " Value: " + curr.value);
		}
		System.out.println();
	
	ArrayList<String> kees = A.keys();
		for (String s: kees)
			System.out.print(s + " ");
		System.out.println("\n");	
		
	
	ArrayList<Integer> vals = A.values();
		for (Integer x: vals)
			System.out.print(x + " ");
		System.out.println("\n");	
		
		A.asort();
		while ((curr = A.each()) != null)
		{
			System.out.println("Key: " + curr.key + " Value: " + curr.value);
		}
		System.out.println();

		
		PHPArray<String> B = new PHPArray<String>(7);
	B.put("www","2");
	B.put("2","4");
	B.put("SNL","5");
	B.put("ooo","1");
	B.put("xxx","9");
	B.put("qqq","11");
	B.put("mmm","3");
		testFlip(B);
		
	B.array_reverse();
	PHPArray.Pair<String> curr1;
	System.out.println("test pair and each method");
		while ((curr1 = B.each()) != null)
		{
			System.out.println("Key: " + curr1.key + " Value: " + curr1.value);
		}
		System.out.println();
	
	boolean test=B.array_key_exists("2");
		if(test){
			System.out.println("key exists");
		}
		
	}
	
}	
	
	

