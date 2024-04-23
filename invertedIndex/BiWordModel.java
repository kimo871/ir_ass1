package invertedIndex;

import java.util.ArrayList;

public class BiWordModel extends Index5 {
     public BiWordModel(){
        this.Name="BiWord Model";
     }

     // Maintaing Index DataStructure through extracting every word from every line in each file and skipping the stopped ones then add the rest to index mapped to their posting list .
    public int indexOneLine(String ln, int fid)
    {
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
            if(i+1 < words.length){
                String nxt = null;
                for (int j = i+1; j < words.length; j++) {
                    if(!this.stopWord(words[j])){
                        words[j] = words[j].toLowerCase();
                        nxt = words[j];
                        break;
                    }
                }
                if(nxt != null) {
                    String byWord = words[i] + '_' + nxt;
                    addByWordToIndex(byWord, fid, ln);
                }
            }

            words[i] = stemWord(words[i]);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(words[i])) {
                index.put(words[i], new DictEntry());
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

    public void addByWordToIndex(String byWord , int fid , String ln)
    {
        byWord = stemWord(byWord);
        // check to see if the word is not in the dictionary
        // if not add it
        if (!index.containsKey(byWord)) {
            index.put(byWord, new DictEntry());
        }

        // add document id to the posting list
        if (!index.get(byWord).postingListContains(fid)) {
            index.get(byWord).doc_freq += 1; //set doc freq to the number of doc that contain the term
            if (index.get(byWord).pList == null) {
                index.get(byWord).pList = new Posting(fid);
                index.get(byWord).last = index.get(byWord).pList;
            } else {
                index.get(byWord).last.next = new Posting(fid);
                index.get(byWord).last = index.get(byWord).last.next;
            }
        } else {
            index.get(byWord).last.dtf += 1;
        }

        //set the term_fteq in the collection
        index.get(byWord).term_freq += 1;
        if (byWord.equalsIgnoreCase("lattice")) {

            System.out.println("  <<" + index.get(byWord).getPosting(1) + ">> " + ln);
        }

    }

    // Used for passing query wanted to be retreived and getting the result of which document it is included & document length
    public String find_24_01(String phrase)
    {
        String[] words = phrase.split("\\s+");
        int len = words.length;
        if(len == 1){
            return searchForPhrase(words[0]);
        }

       if(len == 2){
           if(this.stopWord(words[0]) || this.stopWord(words[1])){
               return "Not found";
           }
           String searchResult = words[0] + '_' + words[1];
           return searchForPhrase(searchResult);
       }

       int st = -1 , end = -1;
        for (int i = 0; i < len; i++) {
            if(words[i].startsWith("\"") && words[i].endsWith("\"")){
                words[i] = words[i].substring(1,words[i].length()-1);
                st = i ;
                end = i;
                continue;
            }
            if(words[i].startsWith("\"")){
                words[i] = words[i].substring(1);
                st = i ;
            }
            if(words[i].endsWith("\"")){
                words[i] = words[i].substring(0,words[i].length()-1);
                end = i ;
            }
        }

        // no quotes , just normal sentence
        if(st == -1 || end == -1){
            return searchForPhrase(phrase);
        }


        StringBuilder searchResult = new StringBuilder();
        for (int i = 0; i < st; i++) {
            searchResult.append(words[i].toLowerCase()).append(' ');
        }


        for (int i = st; i <= end; i++) {
            if(this.stopWord(words[i])){
                continue;
            }
            for (int j = i+1; j <= end; j++) {
                if(!this.stopWord(words[j])){
                    String res = words[i].toLowerCase() + '_' + words[j].toLowerCase() + ' ';
                    searchResult.append(res);
                    break;
                }
            }
        }

        for (int i = end + 1; i < len; i++) {
            searchResult.append(words[i].toLowerCase()).append(' ');
        }


       return searchForPhrase(String.valueOf(searchResult));
    }


    public String searchForPhrase(String phrase)
    {
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
        else{
            return "Not Found";
        }
        return result;
    }





}
