package invertedIndex;

public class BiWordModel extends Index5 {
     public BiWordModel(){
        this.Name="BiWord Model";
     }

     // Maintaing Index DataStructure through extracting every word from every line in each file and skipping the stopped ones then add the rest to index mapped to their posting list .
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+"); // split by whitespace lines to words 
      //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (int i=0; i<words.length; i++) {
            words[i] = words[i].toLowerCase();
            if (stopWord(words[i])) {
                continue;
            }

            //add consecutive words

            
            words[i] = stemWord(words[i]);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(words[i])) {
                index.put(words[i], new DictEntry());
            }

            if(words[i] != words[0]){
               index.put(words[i-1]+"_"+words[i],new DictEntry());
            }

            // add document id to the posting list
            if (!index.get(words[i]).postingListContains(fid)) {
                index.get(words[i]).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                if (index.get(words[i]).pList == null) {
                    index.get(words[i]).pList = new Posting(fid);
                    index.get(words[i]).last = index.get(words[i]).pList;
                } else {
                    index.get(words[i]).last.next = new Posting(fid);
                    index.get(words[i]).last = index.get(words[i]).last.next;
                }
            } else {
                index.get(words[i]).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(words[i]).term_freq += 1;
            if (words[i].equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(words[i]).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }


    // Used for passing query wanted to be retreived and getting the result of which document it is included & document length
    public String find_24_01(String phrase) { // any mumber of terms non-optimized search 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        // loop over each words
        
        
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
        else{
            return "Not Found";
        }
        return result;
    }

}
