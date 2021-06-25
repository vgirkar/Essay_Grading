/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essay;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.text.DecimalFormat;
import java.util.ArrayList;
/**
 *
 * @author admin
 */
public class LSA 
{
    ArrayList lt=new ArrayList();
    
    LSA()
    {
        
    }
    
    public double[][] dimreduction(double term[][])
    {
        double wei[][]=null;
        try
        {
            
            Matrix matrix = new Matrix(term);
            
            System.out.println(matrix.getRowDimension()+" : "+matrix.getColumnDimension());
            SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
            Matrix wordVector = svd.getU();
            Matrix sigma = svd.getS();
            Matrix documentVector = svd.getV();
            
           
            
            int k = (int) Math.floor(Math.sqrt(matrix.getColumnDimension()));
            System.out.println("kk == "+k);
            Matrix reducedWordVector = wordVector.getMatrix(0, wordVector.getRowDimension() - 1, 0, k - 1);
            Matrix reducedSigma = sigma.getMatrix(0, k - 1, 0, k - 1);
            Matrix reducedDocumentVector = documentVector.getMatrix(0, documentVector.getRowDimension() - 1, 0, k - 1);
            Matrix weights = reducedWordVector.times(reducedSigma).times(reducedDocumentVector.transpose());
    
            System.out.println("weights======== "+weights.getRowDimension()+" : "+weights.getColumnDimension());
             
           /* for (int j = 0; j < weights.getColumnDimension(); j++) 
            {
                double sum = sum(weights.getMatrix(0, weights.getRowDimension() - 1, j, j));
                for (int i = 0; i < weights.getRowDimension(); i++) 
                {
                    double va=Math.abs((weights.get(i, j)) / sum);
                    if(Double.isNaN(va))
                        va=0;
                    weights.set(i, j,va );
                }
            }*/
            
            DecimalFormat df=new DecimalFormat("#.####");
            double dd[][]=weights.getArray();
            System.out.println("LSA ===");
            //weights.transpose().print(3, 3);
            
            
            Matrix mt=weights.transpose();
            double ed[][]=mt.getArray();
            
            for(int i=0;i<ed[0].length;i++)
            {
                double sm=0;
                for(int j=0;j<ed.length;j++)
                {
                    sm=sm+ed[j][i];
                }
                if(sm>0.2)
                    lt.add(i);
            }
            System.out.println(ed.length+" : "+ed[0].length+" : "+lt.size());
            wei=new double[ed.length][lt.size()];
            for(int i=0;i<ed.length;i++)
            {
                for(int j=0;j<lt.size();j++)
                {
                    wei[i][j]=Math.abs(ed[i][Integer.parseInt(lt.get(j).toString())]);
                }
            }
            
            for(int i=0;i<wei.length;i++)
            {
                for(int j=0;j<wei[0].length;j++)
                {
                    System.out.print(df.format(wei[i][j])+" ");
                }
                System.out.println();
            }
            //wei=dd;
            System.out.println(wei.length+" : "+wei[0].length);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return wei;
    }
    
    private double sum(Matrix colMatrix) 
    {
        double sum = 0.0D;
        for (int i = 0; i < colMatrix.getRowDimension(); i++) 
        {
            sum += colMatrix.get(i, 0);
        }
    return sum;
  }
}
