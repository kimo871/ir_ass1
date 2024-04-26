/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

/**
 *
 * @author ehab
 */
public class DictEntry {
    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    Posting pList = null;
    Posting last = null;

    boolean postingListContains(int i) {
        Posting p = pList;
        while (p != null) {
            if (p.docId == i) {
                return true;
            }
            p = p.next;
        }
        return false;
    }

    int getPosting(int i) {
        int found = 0;
        Posting p = pList;
        while (p != null) {
            if (p.docId >= i) {
                if (p.docId == i) {
                    return p.dtf;
                } else {
                    return 0;
                }
            }
            p = p.next;
        }
        return found;
    }

    void addPosting(int i) {
        if (pList == null) {
            pList = new Posting(i);
            last = pList;
        } else {
            last.next = new Posting(i);
            last = last.next;
        }
    }

    void addPosting(int i, int position) {
        if(!postingListContains(i)){
            doc_freq++;
            if (pList == null) {
                pList = new Posting(i, 1, position);
                last = pList;
            } else {
                last.next = new Posting(i, 1, position);
                last = last.next;
            }
        }else{
            Posting p = pList;
            while(p != null){
                if(p.docId == i){
                    p.addPosition(position);
                    break;
                }
                p = p.next;
            }
            term_freq ++;
        }

    }
    DictEntry() {
        //  postingList = new HashSet<Integer>();
    }

    DictEntry(int df, int tf) {
        doc_freq = df;
        term_freq = tf;
    }
}
