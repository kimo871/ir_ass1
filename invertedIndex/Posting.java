/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ehab
 */

public class Posting {
    // posting is data structure holding list of documents the word mentioned in
    public Posting next = null;
    int docId;
    int dtf = 1;
    public List<Integer> positions = new ArrayList<>();

    Posting(int id, int t) {
        docId = id;
        dtf=t;
    }
    Posting(int id, int t, int position) {
        docId = id;
        dtf=t;
        positions.add(position);
    }
    public void addPosition(int position){
        positions.add(position);
        dtf++;
    }
    public List<Integer> getPositions(){
        return positions;
    }
    Posting(int id) {
        docId = id;
    }
    @Override
    public String toString() {
        return "Posting{docId=" + docId + ", dtf=" + dtf + "}";
    }
}