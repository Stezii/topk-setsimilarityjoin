# topk-setsimilarityjoin

JAVA implementation of *Top-kSimilarityJoin (algorithm 3)* from
*C. Xiao, W. Wang, X. Lin, and H. Shang. Top-k set similarity joins. In ICDE, pages 916–927, 2009.*

# Usage
## Load from file
* One set by line
* The first elements specifies the record id and is followed by one or more tokens
* The collection of sets is sorted by set size
* The integer tokens need to sorted
* The integers should be chosen according to token frequency - the larger the integer, the more frequent the corresponding token
~~~java
SetSimJoin ssj = new SetSimJoin();
ssj.readFromFile("ssj_set.txt");
~~~

## Load from LinkedHashMap`<String, ArrayList<Integer>>`
~~~java
SetSimJoin ssj = new SetSimJoin();
ssj.setRecords(records);
~~~
or
~~~java
SetSimJoin ssj = new SetSimJoin(records);
~~~

## Perform Top-k Set Similarity Join
Calculate either the k most similar record pairs in the whole dataset:
~~~java
ssj.topkGlobal(1000);
~~~
or the k most similar partners for each record:
~~~java
ssj.topkLocal(10);
~~~
## Output results
~~~java
ssj.saveToFile("ssj_graph.txt");
ssj.printResults();
~~~
