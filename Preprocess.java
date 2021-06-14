/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essay;
import java.util.ArrayList;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 *
 * @author admin
 */
public class Preprocess 
{
    Details dt=new Details();
    
    String reg="(?<=\\w[\\w\\)\\]][\\.\\?\\!]\\s)";
    
    Preprocess()
    {
        
    }
    
    public String process()
    {
        String res="";
        try
        {
            ArrayList<String> stop=read_stopwd();
            MaxentTagger ob=new MaxentTagger(".\\models\\left3words-wsj-0-18.tagger");
            
            for(int i=0;i<dt.org.length;i++)
            {
                String g1=dt.org[i][2].trim().toLowerCase();
                
                String sen[]=g1.split(reg);
                ArrayList at1=new ArrayList();
                for(int j=0;j<sen.length;j++)
                {
                    String g2=sen[j].replaceAll("[^a-zA-Z]", " ");
                    at1.add(g2);
                }
                
                dt.senEss[i]=at1;
                
                String wd[]=g1.split("\\s+");
                ArrayList at2=new ArrayList();
                int ch=0;
                for(int j=0;j<wd.length;j++)
                {
                    String g2=wd[j].replaceAll("[^a-zA-Z]", " ").trim();
                    ch=ch+g2.length();
                    at2.add(g2);
                }
                
                dt.wordEss[i]=at2;
                
                String g2=g1.replaceAll("[^a-zA-Z]", " ");
                String s2[]=g2.trim().toLowerCase().split(" ");
                String sm="";
                for(int j=0;j<s2.length;j++)
                {
                    if(!stop.contains(s2[j].trim()))
                    {
                        if(!s2[j].trim().equals(""))
                        {
                            String ret=ob.tagString(s2[j]).trim();
                            if(ret.endsWith("/NN")||ret.endsWith("/NNS")||ret.endsWith("/NNP"))
                            {
                                if(s2[j].trim().length()>2)
                                    sm=sm+s2[j].trim()+" ";
                            }
                        }
                    }
                }
                System.out.println(at1.size()+" : "+at2.size()+" : "+ch+" : "+sm);
                dt.CWScnt[i][0]=ch; // char
                dt.CWScnt[i][1]=at2.size(); // word
                dt.CWScnt[i][2]=at1.size(); // sent
                
                dt.preEss[i]=sm;
                res=res+sm+"\n";
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    
    static ArrayList<String> read_stopwd()throws Exception
    {
        ArrayList<String> st=new ArrayList<String>();
        Scanner scan1 = null;

        scan1 = new Scanner(new BufferedReader(new FileReader("stopwords1.txt")));
        while (scan1.hasNext())
            st.add(scan1.next());

       return(st);
    }
}
