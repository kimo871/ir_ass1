/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;

import java.util.*;
import java.io.PrintWriter;

/**
 *
 * @author ehab
 */
public class Index5 {

    public String Name ; 
    
    public String getName (){
        return Name;
       }

    //--------------------------------------------
    int N = 0; // number of documents
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }


    // Printing posing list for sepecfic word
    public void printPostingList(Posting p) { // 
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            if(p.next != null){
                System.out.print(p.docId + "," );
            }
            p = p.next;
        }
        System.out.println("]");
    }

    // Printing dictionary that includes term freq & and it's freq in every document also holding the posting list.
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }
 
    // Mainly it maintains sources data structure of loading document as objects with ids in memory then calling indexonline to maintain index datastructure which holds each word mapped to it's posting list.
    public void buildIndex(String[] files) {  // list of files with their pathes to read it
        int fid = 0; // intialize document id
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) { // check if sources map already have this document
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                    //System.out.println(fid );
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    flen+=indexOneLine(ln, fid); // LINE ADDED
                    ///**** hint   flen +=  ________________(ln, fid);
                  
                }
                sources.get(fid).length = flen;
                //System.out.println("document length "+fid+" " + flen);

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
           
    }


    // Maintaing Index DataStructure through extracting every word from every line in each file and skipping the stopped ones then add the rest to index mapped to their posting list .
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+"); // split by whitespace lines to words 
      //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }

            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

// Checking if passed string argument is stopword or not .
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
// Reduce a given word to its base or root form .

    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    // Returning The matched document that contains the two words  from two posting lists . 
    Posting intersect(Posting pL1, Posting pL2) {
///****  -1-   complete after each comment ****
//   INTERSECT ( p1 , p2 )
//          1  answer ←      {}
        Posting answer = null;
        Posting last = null;
//      2 while p1  != NIL and p2  != NIL
            while(pL1 != null && pL2 != null) {
//          3 do if docID ( p 1 ) = docID ( p2 )
            if(pL1.docId == pL2.docId) {
                Posting newNode = new Posting(pL1.docId);
//          4   then ADD ( answer, docID ( p1 ))
                // answer.add(pL1.docId);
                if(answer == null){
                    answer = newNode ;
                    last = newNode;
                }
                else{
                    last.next = newNode;
                    last = newNode;
                }

//          5       p1 ← next ( p1 )
                pL1 = pL1.next ;
//          6       p2 ← next ( p2 )
                pL2 = pL2.next;
            }
//           7   else if docID ( p1 ) < docID ( p2 )
            else if(pL1.docId < pL2.docId) {
//          8        then p1 ← next ( p1 )
                    pL1 = pL1.next;
                }
            else{
    //          9        else p2 ← next ( p2 )
                    pL2 = pL2.next;
                }

            }

            
//      10 return answer
        return answer;
    }


    // Used for passing query wanted to be retreived and getting the result of which document it is included & document length
    public String find_24_01(String phrase) { // any mumber of terms non-optimized search 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        
        //fix this if word is not in the hash table will crash...   --> fixed 
        
        if (index.get(words[0].toLowerCase())!=null){
            DictEntry ans =index.get(words[0].toLowerCase());
            Posting posting = ans.pList;
            int i = 1;
            while (i < len) {
                posting = intersect(posting, index.get(words[i].toLowerCase()).pList);
                i++;
            }
            while (posting != null) {
                //System.out.println("\t" + sources.get(num));
                result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
                posting = posting.next;
            }
        }
        return result;
    }
    
    
    // Sorts an array of words or file names lexicographically using the bubble sort algorithm.
    String[] sort(String[] words ) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }



     // Saving  The Maintained Datastructure  Sources & index to file index which will be first section of each document with it's details (path,..etc..) then second section of each word mapped to which documents included in by their ids.

    public void store(String storageName) {
        try {
//            String pathToStorage = "C:\\Users\\iTECH\\OneDrive\\Desktop\\20210350_20210201_20211060_20210533\\20210350_20210201_20211060_20210533\\is322_HW_1\\src\\invertedIndex\\data\\tmp11\\rl\\ "+storageName;
            String pathToStorage = "D:\\IR\\invertedIndex\\data\\tmp11\\rl\\" + storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                  System.out.println("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";"+"\n");
                Posting p = dd.pList;
                while (p != null) {
                    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// Checking if specific file exists .  
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("D:\\IR\\invertedIndex\\data\\tmp11\\rl\\collection\\"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;       
    }
// Creating File .  
    public void createStore(String storageName) {
        try {
//            String pathToStorage = "C:\\Users\\iTECH\\OneDrive\\Desktop\\20210350_20210201_20211060_20210533\\20210350_20210201_20211060_20210533\\is322_HW_1\\src\\invertedIndex\\data\\tmp11\\"+storageName;
            String pathToStorage = "D:\\IR\\invertedIndex\\data\\tmp11\\rl\\collection\\";
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// Loading Data Saved in index file  To construct Sources & index .   
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "D:\\IR\\invertedIndex\\data\\tmp11\\rl\\collection\\" + storageName;
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

//=====================================================================
