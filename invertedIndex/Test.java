/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author ehab
 */
public class Test {

    public static void main(String args[]) throws IOException {
        // Choosing Retreival Model Menu Display
        HashMap<Integer,Index5> Models = new HashMap<Integer,Index5>();
        Models.put(1, new BooleanModel());
        Models.put(2, new BiWordModel());
        Models.put(3, new PositionalModel());

        HashMap<Integer,String> Path  = new HashMap<Integer,String>();
        Path.put(1, "BooleanIndex");
        Path.put(2, "BiWordIndex");
        Path.put(3, "PositionalIndex");
        
        System.out.println("\n ");
        for (HashMap.Entry<Integer, Index5> entry : Models.entrySet()) {
            System.out.println(entry.getKey() + "- " +(entry.getValue().getName()) );
        }
        System.out.println("\n Choose Model To Retreive Queries : ");
        Scanner in_ = new Scanner(System.in);
        int choice = in_.nextInt();
        for (HashMap.Entry<Integer, Index5> entry : Models.entrySet()) {
            if(choice == entry.getKey()){
//                String files = "C:\\Users\\iTECH\\OneDrive\\Desktop\\20210350_20210201_20211060_20210533\\20210350_20210201_20211060_20210533\\is322_HW_1\\src\\invertedIndex\\data\\tmp11\\rl\\collection\\";
                String files = "C:\\Users\\iTECH\\OneDrive\\Desktop\\20210350_20210201_20211060_20210533\\20210350_20210201_20211060_20210533\\is322_HW_1\\src\\invertedIndex\\data\\tmp11\\rl\\collection\\" ;
                File file = new File(files);

                //|** String[] 	list()
                //|**  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
                String[] fileList = file.list();
                fileList = entry.getValue().sort(fileList);
                entry.getValue().N = fileList.length;
                System.out.println(fileList[0]);

                for (int i = 0; i < fileList.length; i++) {
                    fileList[i] = files + fileList[i];
                 }
                 

                 entry.getValue().buildIndex(fileList);
                 System.out.println(Path.get(entry.getKey()));
                 entry.getValue().store(Path.get(entry.getKey()));

                 String phrase = "";

                 do {
                   System.out.println("Print search phrase: ");
                   BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                   phrase = in.readLine();
                   // if pharase is not empty -> print it's content
                   if (!phrase.isEmpty()) {
                    System.out.println("Search result: ");
                    String searchResult = entry.getValue().find_24_01(phrase);
                    System.out.println(searchResult);
                    //System.out.println("ss "+index.intersect(index.index.get("episodic").pList,index.index.get("wastes").pList));
                   }

                } while (!phrase.isEmpty());
            }
        }

        
       
        // ========================================
        Index5 index = new Index5();
        //|**  change it to your collection directory 
        //|**  in windows "C:\\tmp11\\rl\\collection\\"       
        String files = "C:\\Users\\iTECH\\OneDrive\\Desktop\\20210350_20210201_20211060_20210533\\20210350_20210201_20211060_20210533\\is322_HW_1\\src\\invertedIndex\\data\\tmp11\\rl\\collection";

        File file = new File(files);
        System.out.println(file);

        //|** String[] 	list()
        //|**  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
        String[] fileList = file.list();
        fileList = index.sort(fileList);
        index.N = fileList.length;
        System.out.println(fileList[0]);

      for (int i = 0; i < fileList.length; i++) {
           fileList[i] = files + fileList[i];
        }
        index.buildIndex(fileList);
        index.store("index");
        //index.printDictionary();

        String test3 = "data should plain greatest comif"; // data  should plain greatest comif
       // System.out.println("Boo0lean Model result = \n" + index.find_24_01(test3));

        String phrase = "";
        do {
            System.out.println("Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            phrase = in.readLine();
            // if pharase is not empty -> print it's content
            if (!phrase.isEmpty()) {
                System.out.println("Search result: ");
                String searchResult = index.find_24_01(phrase);
                System.out.println(searchResult);
                //System.out.println("ss "+index.intersect(index.index.get("episodic").pList,index.index.get("wastes").pList));
            }
        } while (!phrase.isEmpty());

}

}
