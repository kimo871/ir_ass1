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

        int len = words.length;
        List<Integer> matchingDocumentsIds = new ArrayList<>();

        for(int i = 0; i < len; i++){
            String word = words[i].toLowerCase();
            List<Posting> postings = getPostingsForWord(word);
            if(postings.isEmpty()){
                return "Phrase Not Found!!";
            }

            if(i > 0){
                matchingDocumentsIds = validatePhraseInDocuments(matchingDocumentsIds, postings);
            }else{
                matchingDocumentsIds = extractDocumentIds(postings);
            }
        }

        StringBuilder result = new StringBuilder();
        for(int docId : matchingDocumentsIds){
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

    private List<Integer> validatePhraseInDocuments(List<Integer> documentsIds, List<Posting> currentPostings){
        List<Integer> validDocumentsIds = new ArrayList<>();

        for(int docId : documentsIds){
            List<Integer> postingsInDoc = getPositionsForDocument(docId, currentPostings);

            if(isValidPhraseProximity(postingsInDoc)){
                validDocumentsIds.add(docId);
            }
        }
        return validDocumentsIds;
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

    private List<Integer> getPositionsForDocument(int docId, List<Posting> postings){
        List<Integer> positions = new ArrayList<>();
        for(Posting posting : postings){
            if(posting.docId == docId){
                positions.addAll(posting.getPositions());
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
                System.out.println("positions: " + posting.getPositions());
                postings.add(posting);
                posting = posting.next;
            }
        }
        return postings;
    }
}