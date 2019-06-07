import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.awt.Color;
import java.awt.Font;

public class ObjectCounting extends JFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton NesneEkleB ;
    JButton NesneSayýmButonu ;
    JLabel lblLtfenGrntEkleyiniz;   
    
    
    public ObjectCounting(){
    super("Görüntü Ýçerisindeki Nesneleri Sayýmý");
    getContentPane().setBackground(new Color(25, 25, 112));
    NesneEkleB = new JButton("Nesne Ekle");
    NesneEkleB.setFont(new Font("Tahoma", Font.BOLD, 14));
    NesneEkleB.setForeground(new Color(0, 0, 255));
    NesneEkleB.setBackground(new Color(255, 255, 255));
    NesneEkleB.setBounds(557,597,141,50);
    NesneSayýmButonu = new JButton("Nesne Sayýmý");
    lblLtfenGrntEkleyiniz = new JLabel();
    lblLtfenGrntEkleyiniz.setIcon(new ImageIcon("C:\\Users\\ebru\\Desktop\\picturesdepos\\ig\u00FC.png"));
    lblLtfenGrntEkleyiniz.setBackground(Color.WHITE);
    lblLtfenGrntEkleyiniz.setBounds(180,24,1004,496);
    getContentPane().add(NesneEkleB);
    getContentPane().add(lblLtfenGrntEkleyiniz);
  
   
    NesneEkleB.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        
          JFileChooser file = new JFileChooser();
          file.setCurrentDirectory(new File(System.getProperty("user.home")));
          //filter the files
          FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
          file.addChoosableFileFilter(filter);
          int result = file.showSaveDialog(null);
           //if the user click on save in Jfilechooser
          if(result == JFileChooser.APPROVE_OPTION){
              File selectedFile = file.getSelectedFile();
              String path = selectedFile.getAbsolutePath();
              lblLtfenGrntEkleyiniz.setIcon(ResizeImage(path));   
              edge_count( path);

          }
           //if the user click on save in Jfilechooser

          else if(result == JFileChooser.CANCEL_OPTION){
              System.out.println("No File Select");
          }
        }
        private Random rng = new Random(12345);


		private void edge_count(String path) {
			 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		 	 Mat goruntu =new Mat();
		     goruntu = Imgcodecs.imread(path);
		 	 Mat hedefgoruntu =new Mat();
		     if(!goruntu.empty()) {
		     Imgproc.cvtColor(goruntu,hedefgoruntu,Imgproc.COLOR_BGR2GRAY);//canny icin gerekli
		     Imgproc.GaussianBlur(hedefgoruntu, hedefgoruntu, new Size(3,3),0);
		     Mat yenihedef =new Mat();  
		     Imgproc.threshold(hedefgoruntu,hedefgoruntu,240,250, Imgproc.THRESH_BINARY); 
		     Imgproc.Canny(hedefgoruntu, yenihedef,0,500);  
		     Imgproc.morphologyEx(yenihedef,yenihedef,Imgproc.MORPH_CLOSE, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3))) ; 
		     Mat hierarchy = new Mat();
		     List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		     Imgproc.findContours(yenihedef, contours, hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);    
		     MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
		     for (int i = 0; i < contours.size(); i++) {
		    	contoursPoly[i] = new MatOfPoint2f();
		     	Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);		     
		     }        
		     List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);
		     for (MatOfPoint2f poly : contoursPoly) {
		       contoursPolyList.add(new MatOfPoint(poly.toArray()));
		     }
		     Rect[] boundRect = new Rect[contours.size()];//her bir nesne kadar boundrect olusturur
		     Mat cizim = Mat.zeros(yenihedef.size(), CvType.CV_8UC3);
		     int i= 0;
		     for ( i = 0; i < contours.size(); i++) {
		       Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
		       Imgproc.drawContours(cizim, contoursPolyList, i, color,-2); 
		       boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));   		     	
                Imgproc.rectangle(cizim, boundRect[i].tl(), boundRect[i].br(), color,0 );
		     }  
		     Imgcodecs.imwrite("C:\\Users\\ebru\\Desktop\\sonuc.jpg", cizim);
		 	 JOptionPane.showMessageDialog(NesneSayýmButonu, "Nesne Sayýsý "+i);
		 	  
		     }	
		    
		     
		      }
    });
 
    getContentPane().setLayout(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setSize(1200,720);//acýlan pencere boyutu verilir
    setVisible(true);
    
    }
     
     // Methode to resize imageIcon with the same size of a Jlabel
    public ImageIcon ResizeImage(String ImagePath)
    {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(lblLtfenGrntEkleyiniz.getWidth(), lblLtfenGrntEkleyiniz.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
    
    public static void main(String[] args){
        new ObjectCounting();
    }
}