package invertedIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

public class PositionalModel extends Index5 {
    public PositionalModel(){
        this.Name="Positional Model";
    }


    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+"); // split by whitespace lines to words 
      //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        int pointer=0;
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
           
            index.get(word).last.positions.add(pointer);
        System.out.println(word + " " + index.get(word).last.positions); 
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

            pointer++;

        }
        return flen;
    }


    public void store(String storageName) {
    try {
        String pathToStorage = "C:\\Users\\iTECH\\OneDrive\\Desktop\\20210350_20210201_20211060_20210533\\20210350_20210201_20211060_20210533\\is322_HW_1\\src\\invertedIndex\\data\\tmp11\\rl\\" + storageName;
        Writer wr = new FileWriter(pathToStorage);

        // Store source records
        for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
            wr.write(entry.getKey().toString() + ",");
            wr.write(entry.getValue().URL.toString() + ",");
            wr.write(entry.getValue().title.replace(',', '~') + ",");
            wr.write(entry.getValue().length + ",");
            wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
            wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
        }

        wr.write("section2" + "\n");

        // Store index
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();

            wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";" + "\n");

            Posting p = dd.pList;
            while (p != null) {
                wr.write(p.docId + "," + p.dtf + ":");
                List<Integer> positions = p.positions;
                for (Integer position : positions) {
                    wr.write(position + ",");
                }
                wr.write(";");

                p = p.next;
            }
            wr.write("\n");
        }

        wr.write("end" + "\n");
        wr.close();
        System.out.println("=============END STORE=============");

    } catch (Exception e) {
        e.printStackTrace();
    }
}



     // Returning The matched document that contains the two words  from two posting lists . 
     Posting intersect(Posting pL1, Posting pL2) {
        ///****  -1-   complete after each comment ****
        int k = 1; // Adjusted value of k to 1
        Posting answer = null; // List to store intersected positions

        //   INTERSECT ( p1 , p2 )
        //          1  answer ←      {}

             
                Posting last = null;
        //      2 while p1  != NIL and p2  != NIL
                    while(pL1 != null && pL2 != null) {
        //          3 do if docID ( p 1 ) = docID ( p2 )
                    if(pL1.docId == pL2.docId) {
                        Posting newNode = new Posting(pL1.docId);
                        List<Integer> list = new ArrayList<>();
                        List<Integer> pp1 = pL1.positions;
                        List<Integer> pp2 = pL2.positions;
        //          4   then ADD ( answer, docID ( p1 ))
                        // answer.add(pL1.docId);
                        int x = 0, y = 0;
            while (x < pp1.size() && y < pp2.size()) {
                int pos1 = pp1.get(x);
                int pos2 = pp2.get(y);
                if (Math.abs(pos1 - pos2) <= k) {
                    list.add(pos2);
                    x++; // Move to next position in pp1
                } else if (pos2 > pos1) {
                    x++; // Move to next position in pp1
                } else {
                    y++; // Move to next position in pp2
                }
            }

                     // Update answer if there are matches
            if (!list.isEmpty()) {
                if (answer == null) {
                    answer = new Posting(pL1.docId, list);
                } else {
                    // Append matched positions to the existing answer
                    answer.positions.addAll(list);
                }
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


            public HashMap<String, DictEntry> load(String storageName) {
    try {
        String pathToStorage = "C:\\Users\\iTECH\\OneDrive\\Desktop\\20210350_20210201_20211060_20210533\\20210350_20210201_20211060_20210533\\is322_HW_1\\src\\invertedIndex\\data\\tmp11\\rl\\collection" + storageName;
        sources = new HashMap<>();
        index = new HashMap<>();
        BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
        String ln = "";
        while ((ln = file.readLine()) != null) {
            if (ln.equalsIgnoreCase("section2")) {
                break;
            }
            String[] ss = ln.split(",");
            int fid = Integer.parseInt(ss[0]);
            try {
                System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                sources.put(fid, sr);
            } catch (Exception e) {
                System.out.println(fid + "  ERROR  " + e.getMessage());
                e.printStackTrace();
            }
        }
        while ((ln = file.readLine()) != null) {
            if (ln.equalsIgnoreCase("end")) {
                break;
            }
            String[] ss1 = ln.split(";");
            String[] ss1a = ss1[0].split(",");
            String[] ss1b = ss1[1].split(":");
            index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
            for (String posting : ss1b) {
                String[] ss1bx = posting.split(",");
                Posting newPosting = new Posting(Integer.parseInt(ss1bx[0]));
                for (int i = 1; i < ss1bx.length; i++) {
                    newPosting.Positions.add(Integer.parseInt(ss1bx[i]));
                }
                if (index.get(ss1a[0]).pList == null) {
                    index.get(ss1a[0]).pList = newPosting;
                    index.get(ss1a[0]).last = newPosting;
                } else {
                    index.get(ss1a[0]).last.next = newPosting;
                    index.get(ss1a[0]).last = newPosting;
                }
            }
        }
        System.out.println("============= END LOAD =============");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return index;
}





    


}



    