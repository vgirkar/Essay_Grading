/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essay;
import java.util.ArrayList;
import java.text.DecimalFormat;
/**
 *
 * @author admin
 */
public class FeatureExtraction 
{
    Details dt=new Details();
    ArrayList feat1=new ArrayList();
    ArrayList feature=new ArrayList();
    ArrayList featCnt=new ArrayList();
    double term[][];
    
    DecimalFormat df=new DecimalFormat("#.####");
    
    FeatureExtraction()
    {
        
    }
    
    public ArrayList extractTerm()
    {
        try
        {
            for(int i=0;i<dt.preEss.length;i++)
            {
                String g1=dt.preEss[i].trim();
                String g2[]=g1.trim().split(" ");
                for(int j=0;j<g2.length;j++)
                {
                    if(!feat1.contains(g2[j].trim()))
                        feat1.add(g2[j].trim());
                }
            }
            System.out.println(feat1.size());
            
            for(int k=0;k<feat1.size();k++)
           {
               String f1=feat1.get(k).toString();
               int cnt=0;
                for(int i=0;i<dt.preEss.length;i++)
                {
                    int c1=0;
                    String g1[]=dt.preEss[i].trim().split(" ");
                    for(int j=0;j<g1.length;j++)
                    {
                        if(f1.equals(g1[j]))
                        {
                            cnt++;
                            c1++;
                        }
                    }
                }
                //System.out.println("=== "+f1+" : "+cnt);
               // if(cnt>1 && f1.length()>2)
                if(cnt>3 && f1.length()>2)
                {
                    
                    feature.add(f1);
                    featCnt.add(cnt);
                    System.out.println("=== "+f1+" : "+cnt);
                }
           }
            System.out.println(feature.size());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return feature;
    }
    
    public double[][] findtfidf()
    {
        double tfidf[][]=new double[dt.preEss.length][feature.size()];
        try
        {
            //termsFre=feature; // comment this line for onto vsm
            
            term=new double[dt.preEss.length][feature.size()];
            
            for(int i=0;i<dt.preEss.length;i++)
            {
                String g1=dt.preEss[i].trim();
                for(int j=0;j<feature.size();j++)
                {
                    String f1=feature.get(j).toString();
                   double tm=findTermFre(f1,g1);
                  //  double tm=findTermFreBin(f1,g1);
                    term[i][j]=tm;
                }
            }
            
            double in1[]=findInverse(feature);
            
            
            
            for(int i=0;i<term.length;i++)
            {
                for(int j=0;j<term[0].length;j++)
                {
                    tfidf[i][j]=term[i][j]*in1[j];
                }
            }
            String sg="";
            for(int i=0;i<term.length;i++)
            {
                for(int j=0;j<term[0].length;j++)
                {
                    sg=sg+tfidf[i][j]+" ";
                   // sg=sg+term[i][j]+" ";
                    System.out.print(df.format(tfidf[i][j])+"  ");
                }
                sg=sg+"\n";
                System.out.println();
            }
           // System.out.println("tfidf===");
            //System.out.println(sg);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return tfidf;
    }
    
    public double findTermFre(String tm,String cn)
    {
        double tr=0;
        try
        {
            double cc=0;
            String g1[]=cn.split(" ");
            for(int i=0;i<g1.length;i++)
            {
                if(g1[i].equals(tm))
                    cc++;
            }
            
            tr=cc/(double)g1.length;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return tr;
    }
     public double[] findInverse(ArrayList ft)
    {
        double de[]=new double[ft.size()];
        try
        {
            for(int i=0;i<ft.size();i++)
            {
                String g1=ft.get(i).toString();
                double cn1=0;
                for(int j=0;j<dt.preEss.length;j++)
                {
                    String g2=dt.preEss[j].trim();
                    if(g2.contains(g1))
                        cn1++;
                }
                if(cn1==0)
                    cn1=1;
                double in1=Math.log10((double)dt.preEss.length/cn1);
                de[i]=in1;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return de;
    }
     
   
}
