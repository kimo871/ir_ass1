package invertedIndex;
import java.util.*;

public class PositionalModel extends Index5 {
    public PositionalModel(){
        this.Name="Positional Model";
    }
    public int indexOneLine(String ln, int fid){
        int flen = 0;
        String[] words = ln.split("\\W+");
        int position = 0;
        for(String word : words){

            word = word.toLowerCase();
            if(stopWord(word)){
                position ++;
                continue;
            }
            word = stemWord(word);
            if(!index.containsKey(word)){
                index.put(word, new DictEntry());
            }

            DictEntry dictEntry = index.get(word);
            if(!dictEntry.postingListContains(fid)){
                dictEntry.addPosting(fid, position);
                dictEntry.doc_freq += 1; // Increment document frequency
            }

            dictEntry.term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {
                System.out.println("<<" + dictEntry.getPosting(1) + ">> " + ln);
            }

            position++;
        }
        return flen;
    }

    public String find_24_01(String phrase){
        String[] words = phrase.split("\\W+");

        List<List<Posting>> phrasePostings = new ArrayList<>();

        for(String word : words){
            word = word.toLowerCase();
            List<Posting> postings = getPostingsForWord(word);
            phrasePostings.add(postings);
        }
        List<Integer> matchingDocumentIds = validatePhraseInDocuments(phrasePostings);

        StringBuilder result = new StringBuilder();
        for(int docId : matchingDocumentIds){
            result.append(docId).append(",");
        }
        return result.toString().isEmpty() ? "Phrase not found.." : result.toString();
    }

    private List<Integer> extractDocumentIds(List<Posting> postings) {
        List<Integer> documentIds = new ArrayList<>();
        for(Posting posting: postings){
            documentIds.add(posting.docId);
        }
        return documentIds;
    }

    private List<Integer> validatePhraseInDocuments(List<List<Posting>> phrasePostings){
        List<Integer> validDocumentsIds = extractDocumentIds(phrasePostings.get(0));

        for (int i = 1; i < phrasePostings.size(); i++){
            List<Posting> currentPostings = phrasePostings.get(i);
            validDocumentsIds.retainAll(extractDocumentIds(currentPostings));
        }
        List<Integer> finalValidDocumentIds = new ArrayList<>();
        for(int docId : validDocumentsIds){
            List<Integer> positionsInDoc = getPositionsForDocument(docId, phrasePostings);
            if(isValidPhraseProximity(positionsInDoc)){
                finalValidDocumentIds.add(docId);
            }
        }
        return finalValidDocumentIds;
    }

    private boolean isValidPhraseProximity(List<Integer> postingsInDoc) {
        if(postingsInDoc == null || postingsInDoc.isEmpty() || postingsInDoc.size() == 1){
            return false;
        }
        Collections.sort(postingsInDoc);

        for(int i = 0; i < postingsInDoc.size()-1; i++){
            if(postingsInDoc.get(i + 1) - postingsInDoc.get(i) != 1){
                return false;
            }
        }
        return true;
    }

    private List<Integer> getPositionsForDocument(int docId, List<List<Posting>> postings) {
        List<Integer> positions = new ArrayList<>();

        for (List<Posting> documentPostings : postings) {
            for (Posting posting : documentPostings) {
                if (posting.docId == docId) {
                    positions.addAll(posting.getPositions());
                    break;
                }
            }
        }

        return positions;
    }


    private List<Posting> getPostingsForWord(String word){
        List<Posting> postings = new ArrayList<>();

        if(index.containsKey(word)){
            DictEntry dictEntry = index.get(word);
            Posting posting = dictEntry.pList;

            while(posting != null){
                postings.add(posting);
                posting = posting.next;
            }
        }
        return postings;
    }
}