/* Project 1
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.*;

public class CBIR extends JFrame{

	private JLabel photographLabel = new JLabel();  //container to hold a large 
	private PicButton [] button; //creates an array of JButtons
	private int [] buttonOrder = new int [101]; //creates an array to keep up with the image order
	private double [] imageSize = new double[101]; //keeps up with the image sizes
	private GridLayout gridLayout1;
	private GridLayout gridLayout2;
	private GridLayout gridLayout3;
	private GridBagLayout gridBagLayout1;
	private JPanel panelBottom1;
	private JPanel panelTop1;
	private JPanel buttonPanel1;
	private JLabel pageNum;
	private JCheckBox checkbox;
	private Double [][] intensityMatrix = new Double [100][26];
	private Double [][] colorCodeMatrix = new Double [100][64];
	private	Double [][] featureMatrix = new Double [100][90]; //feature matrix for intensity + colorCode
	private Map <Double , LinkedList<Integer>> map;
	int picNo = 0;
	int imageCount = 1; //keeps up with the number of images displayed since the first page.
	int pageCount = 0;

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CBIR app = new CBIR();
				app.setVisible(true);
			}
		});
	}
	/*
	 * Use this class for creating picture icon buttons with
	 * check boxes associated with them.
	 */
	private class PicButton extends JPanel {
		JCheckBox box; //check box that determines whether the image is relevant
		JButton button; //button containing the image icon

		/*
		 * PicButton constructor. Takes a JButton and
		 * adds it to a BorderLayout and adds an empty
		 * border the height of a checkbox (24) to the
		 * bottom.
		 */
		public PicButton(JButton button) {
			this.button = button;
			box = new JCheckBox("Relevant");
			BorderLayout layout = new BorderLayout();
			this.setLayout(layout);
			this.add(button);
			this.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

		}

		public JButton getButton() {
			return button;
		}

		public JCheckBox getCheckBox() {
			return box;
		}

		//removes the empty border and adds the checkbox to the south of the layout.
		public void addCheckBox() {
			this.setBorder(null);
			this.add(box, BorderLayout.SOUTH);
		}

	}

	public CBIR() {
		//The following lines set up the interface including the layout of the buttons and JPanels.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Icon Demo: Please Select an Image");        
		panelBottom1 = new JPanel();
		panelTop1 = new JPanel();
		buttonPanel1 = new JPanel();
		pageNum = new JLabel();
		checkbox = new JCheckBox();
		gridLayout1 = new GridLayout(4, 5, 5, 0);
		gridLayout2 = new GridLayout(2, 1, 5, 5);
		gridLayout3 = new GridLayout(1, 2, 5, 5);
		gridBagLayout1 = new GridBagLayout();
		setLayout(gridLayout2);
		panelBottom1.setLayout(gridLayout1);
		panelTop1.setLayout(gridLayout3);
		add(panelTop1);
		add(panelBottom1);
		photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
		photographLabel.setHorizontalTextPosition(JLabel.CENTER);
		photographLabel.setHorizontalAlignment(JLabel.CENTER);
		photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel1.setLayout(gridBagLayout1);
		pageNum.setText("Page 1 of 5");	//Added in page number next to the nextpage button
		checkbox.setText("Relevance");
		panelTop1.add(photographLabel);
		panelTop1.add(buttonPanel1);


		/*HTML is used to add spaces on the button text */

		JButton previousPage = new JButton("<html>View Previous<br />20 Pictures<html>");
		JButton nextPage = new JButton("<html>View Next<br />20 Pictures<html>");
		JButton intensity = new JButton("<html>Query Images<br />By Intensity<html>");
		JButton colorCode = new JButton("<html>Query Images<br />By Color Code<html>");
		JButton returnOrder = new JButton("<html>Return Pictures<br />to Original Order<html>");
		JButton intenColorCode = new JButton("<html>Query Images By<br />Intensity + Color Code<html>");

		/*Buttons must be added in this order for the correct display*/
		/*Used the GridBagLayout for more button freedom*/
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		buttonPanel1.add(intensity, c);
		c.gridx = 0;
		c.gridy = 1;
		buttonPanel1.add(colorCode, c);
		c.gridx = 1;
		c.gridy = 0;
		buttonPanel1.add(intenColorCode,c);
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 50;
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		buttonPanel1.add(previousPage,c);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.ipadx = 20;
		c.anchor = GridBagConstraints.CENTER;
		buttonPanel1.add(returnOrder, c);
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 80;
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		buttonPanel1.add(nextPage, c);
		c.ipadx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		buttonPanel1.add(checkbox, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.ipadx = 20;
		c.gridy = 3;
		buttonPanel1.add(pageNum, c);


		nextPage.addActionListener(new nextPageHandler());
		previousPage.addActionListener(new previousPageHandler());
		intensity.addActionListener(new intensityHandler());
		colorCode.addActionListener(new colorCodeHandler());

		//Added for addition features
		returnOrder.addActionListener(new returnOrderHandler());
		intenColorCode.addActionListener(new intenColorCodeHandler());
		//Added for control of the check for relevance.
		checkbox.addItemListener(new relevanceHandler());
		setSize(1100, 750);
		// this centers the frame on the screen
		setLocationRelativeTo(null);


		button = new PicButton[101];
		/*This for loop goes through the images in the database and stores them as icons and adds
		 * the images to JButtons and then to the JButton array
		 */
		for (int i = 1; i < 101; i++) {
			ImageIcon icon;
			icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));

			/*
			 * Scales the image inside of the button to ensure that the entire image can be seen
			 */
			Image img = icon.getImage();
			Image newimg = img.getScaledInstance(350, 90,  java.awt.Image.SCALE_FAST);
			ImageIcon icon2 = new ImageIcon(newimg);

			if(icon != null){
				//Place scaled icon in the button
				button[i] = new PicButton(new JButton(icon2));
				//Place the regular icon in the action handler
				button[i].getButton().addActionListener(new IconButtonHandler(i, icon));
				buttonOrder[i] = i;
			}
		}

		readIntensityFile();
		readColorCodeFile();
		//Used for debugging
//		runTeacherExample();
		createFeatureMatrix(intensityMatrix, colorCodeMatrix);
		displayFirstPage();
	}

	/*This method opens the intensity text file containing the intensity matrix with the histogram bin values for each image.
	 * The contents of the matrix are processed and stored in a two dimensional array called intensityMatrix.
	 */
	public void readIntensityFile(){
		Scanner read = null;
		try{
			read =new Scanner(new File (getClass().getResource("intensity.txt").toURI()));
			read.useDelimiter(",");
			/////////////////////
			///your code///
			/////////////////


			/* loops through the files and constructs a matrix with
			 * intensity values.
			 */
			for(int i = 0; i < intensityMatrix.length; i++) {
				for(int j = 0; j < intensityMatrix[0].length; j++) {
					intensityMatrix[i][j] = read.nextDouble();
				}
				imageSize[i] = intensityMatrix[i][0];
			}
		}
		catch(FileNotFoundException | URISyntaxException EE){
			System.out.println("The file intensity.txt does not exist");
		}
		finally {
			read.close();
		}
	}

	/*This method opens the color code text file containing the color code matrix with the histogram bin values for each image.
	 * The contents of the matrix are processed and stored in a two dimensional array called colorCodeMatrix.
	 */
	private void readColorCodeFile(){
		Scanner read = null;
		try{
			read =new Scanner(new File (getClass().getResource("colorCodes.txt").toURI()));
			read.useDelimiter(",");
			/////////////////////
			///your code///
			/////////////////


			/* loops through the files and constructs a matrix with
			 * colorCode values.
			 */
			for(int i = 0; i < colorCodeMatrix.length; i++) {
				for(int j = 0; j < colorCodeMatrix[0].length; j++) {
					colorCodeMatrix[i][j] = read.nextDouble();
				}
			}
		}
		catch(FileNotFoundException | URISyntaxException EE){
			System.out.println("The file intensity.txt does not exist");
		}
		finally {
			read.close();
		}
	}

	/*This method displays the first twenty images in the panelBottom.  The for loop starts at number one and gets the image
	 * number stored in the buttonOrder array and assigns the value to imageButNo.  The button associated with the image is 
	 * then added to panelBottom1.  The for loop continues this process until twenty images are displayed in the panelBottom1
	 */
	private void displayFirstPage(){
		int imageButNo = 0;
		//added this to make sure every time this method is called imageCount is 1
		imageCount = 1;
		pageCount = 1;
		panelBottom1.removeAll();
		for(int i = 1; i < 21; i++){
			imageButNo = buttonOrder[i];
			//Check if the relevant box is checked
			if(checkbox.isSelected()) {
				/*
				 * if it is checked, put the checkbox under the image
				 * then add it to the panel
				 */
				button[imageButNo].addCheckBox();
				panelBottom1.add(button[imageButNo]);
			}
			else {
				/*
				 * If it is not checked, remove a button if its showing
				 * and set the border to the same size as a checkbox
				 */
				button[imageButNo].remove(button[imageButNo].getCheckBox());
				button[imageButNo].setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
				panelBottom1.add(button[imageButNo]);
			}
			imageCount ++;
		}

		/*This was added to set the next and previous buttons
		 * to grey out when they cannot be used.
		 */
		buttonPanel1.getComponent(3).setEnabled(false);
		if(imageCount > 20) {
			buttonPanel1.getComponent(5).setEnabled(true);
		}
		//display the page count
		pageNum.setText("Page " + pageCount + " of " + 5);
		panelBottom1.revalidate();  
		panelBottom1.repaint();
	}

	/*This class implements an ActionListener for each iconButton.  When an icon button is clicked, the image on the 
	 * the button is added to the photographLabel and the picNo is set to the image number selected and being displayed.
	 */ 
	private class IconButtonHandler implements ActionListener{
		int pNo = 0;
		ImageIcon iconUsed;

		IconButtonHandler(int i, ImageIcon j){
			pNo = i;
			iconUsed = j;  //sets the icon to the one used in the button
		}

		public void actionPerformed( ActionEvent e){
			photographLabel.setIcon(iconUsed);
			//Added to show picture name under the photo
			photographLabel.setText(String.valueOf(pNo) + ".jpg");
			picNo = pNo;
		}

	}

	/*This class implements an ActionListener for the nextPageButton.  The last image number to be displayed is set to the 
	 * current image count plus 20.  If the endImage number equals 101, then the next page button does not display any new 
	 * images because there are only 100 images to be displayed.  The first picture on the next page is the image located in 
	 * the buttonOrder array at the imageCount
	 */
	private class nextPageHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			int imageButNo = 0;
			pageCount++;
			int endImage = imageCount + 20;
			if(endImage <= 101){
				panelBottom1.removeAll(); 
				for (int i = imageCount; i < endImage; i++) {
					imageButNo = buttonOrder[i];
					/*
					 * Incase a checkbox is on a button not on the first page, remove it
					 */
					button[imageButNo].remove(button[imageButNo].getCheckBox());
					button[imageButNo].setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
					panelBottom1.add(button[imageButNo]);
					imageCount++;

				}

				pageNum.setText("Page " + pageCount + " of " + 5);
				panelBottom1.revalidate();  
				panelBottom1.repaint();
			}
			/*This was added to set the next and previous buttons
			 * to grey out when they cannot be used.
			 */
			if(imageCount > 20) {
				buttonPanel1.getComponent(3).setEnabled(true);
			}
			if(imageCount > 99) {
				buttonPanel1.getComponent(5).setEnabled(false);
			}
		}

	}

	/*This class implements an ActionListener for the previousPageButton.  The last image number to be displayed is set to the 
	 * current image count minus 40.  If the endImage number is less than 1, then the previous page button does not display any new 
	 * images because the starting image is 1.  The first picture on the next page is the image located in 
	 * the buttonOrder array at the imageCount
	 */
	private class previousPageHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			int imageButNo = 0;
			pageCount--;
			int startImage = imageCount - 40;
			int endImage = imageCount - 20;
			if(startImage >= 1){
				panelBottom1.removeAll();
				/*The for loop goes through the buttonOrder array starting with the startImage value
				 * and retrieves the image at that place and then adds the button to the panelBottom1.
				 */
				for (int i = startImage; i < endImage; i++) {
					imageButNo = buttonOrder[i];
					if(checkbox.isSelected() && pageCount == 1) {
						/*
						 * If it is page 1 and the box is checked, all
						 * pictures will have a checkbox.
						 */
						button[imageButNo].addCheckBox();
						panelBottom1.add(button[imageButNo]);
					}
					else {
						/*
						 * Remove all boxes if the checkbox is not checked.
						 */
						button[imageButNo].remove(button[imageButNo].getCheckBox());
						button[imageButNo].setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
						panelBottom1.add(button[imageButNo]);
					}
					imageCount--;
				}
				buttonPanel1.getComponent(5).setEnabled(true);
				pageNum.setText("Page " + pageCount + " of " + 5);
				panelBottom1.revalidate();  
				panelBottom1.repaint();
			}
			/*This was added to set the next and previous buttons
			 * to grey out when they cannot be used.
			 */
			if(startImage == 1) {
				buttonPanel1.getComponent(3).setEnabled(false);
			}
		}

	}

	/*This methods implements an action listener to reorder the elements
	 * based on the order that they were first displayed in.
	 */
	private class returnOrderHandler implements ActionListener {

		public void actionPerformed( ActionEvent e) {
			for (int i = 1; i < buttonOrder.length; i++) {
				buttonOrder[i] = i;
			}

			displayFirstPage();

			/*This was added to set the next and previous buttons
			 * to grey out when they cannot be used.
			 */
			buttonPanel1.getComponent(5).setEnabled(true);
		}
	}


	/*This class implements an ActionListener when the user selects the intensityHandler button.  The image number that the
	 * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
	 * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one.
	 * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
	 * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
	 * The images are then arranged from most similar to the least.
	 */
	private class intensityHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			double [] distance = new double [101];
			map = new HashMap<Double, LinkedList<Integer>>();
			int pic = (picNo - 1);
			int startIndex = 1;
			if(pic == -1) {
				photographLabel.setText("Please select an image to query.");
			}
			else {

				/////////////////////
				///your code///
				/////////////////


				//These methods are at the bottom.

				distance = findDistance(intensityMatrix, startIndex);
				sortByDistance(distance);

				/*
				 * Puts all of the pictures and order by distance using
				 * the displayFirstPage method.
				 */
				displayFirstPage();

			}
		}
	}

	/*This class implements an ActionListener when the user selects the colorCode button.  The image number that the
	 * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
	 * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one. 
	 * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
	 * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
	 * The images are then arranged from most similar to the least.
	 */ 
	private class colorCodeHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			double [] distance;
			map = new HashMap<Double, LinkedList<Integer>>();
			int pic = (picNo - 1);
			int startIndex = 0;
			if(pic == -1) {
				photographLabel.setText("Please select an image to query.");
			}
			else {
				/////////////////////
				///your code///
				/////////////////
				distance = findDistance(colorCodeMatrix, startIndex);

				sortByDistance(distance);

				/*
				 * Puts all of the pictures and order by distance using
				 * the displayFirstPage method.
				 */
				displayFirstPage();
			}
		}
	}

	/*
	 * This method blends both the intensity and colorCode query. The distances of
	 * both methods are summed together and placed in a map. Then the distance array
	 * is passed to the sortByDistance method.
	 */
	private class intenColorCodeHandler implements ActionListener {
		public void actionPerformed( ActionEvent e){
			double [] distance = new double[101];
			map = new HashMap<Double, LinkedList<Integer>>();
			int pic = (picNo - 1);
			if(pic == -1) {
				photographLabel.setText("Please select an image to query.");
			}
			else {
				/////////////////////
				///your code///
				/////////////////

				distance = findWeightedDistance(featureMatrix, 1);	
				sortByDistance(distance);

				/*
				 * Puts all of the pictures and order by distance using
				 * the displayFirstPage method.
				 */
				displayFirstPage();
			}
		}
	}

	/*
	 * This class is used to handled the relevance checkbox,
	 * and it simply passes it to displayFirstPage method to handle
	 * and start from the first page.
	 */
	private class relevanceHandler implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			displayFirstPage();			
		}

	}
	/*
	 * This method takes a matrix and the index in that matrix where picture features
	 * start, it then calculates the distance from each picture, and adds the value of the
	 * picture index to a Hashmap with the distance as the key. It returns a Double Array
	 * that contains all of the distances.
	 */
	private double[] findDistance(Double[][] matrix, int picFeatureStartIndex) {
		double[] result = new double[matrix.length + 1];
		int compareImage = 1;
		int pic = (picNo - 1);
		int picFeature;
		double d = 0;
		try {
			double picSize = imageSize[pic];
			while(compareImage < 101) {
				d = 0;
				picFeature = picFeatureStartIndex;
				while(picFeature < matrix[0].length) {
					d += Math.abs((matrix[pic][picFeature]/picSize)
							-(matrix[compareImage - 1][picFeature]/intensityMatrix[compareImage - 1][0]));
					picFeature++;
				}
				result[compareImage] = d;
				if(map.containsKey(d)) {
					map.get(d).add(compareImage + 1);
				}
				else {
					LinkedList<Integer> listStart = new LinkedList<Integer>();
					listStart.add(compareImage);
					map.put(d, listStart);
				}
				compareImage++;
			}
		} catch (ArrayIndexOutOfBoundsException e){
			photographLabel.setText("Please select an image to query.");
		}

		return result;
	}

	private double[] findWeightedDistance(Double[][] matrix, int picFeatureStartIndex) {
		double[] result = new double[matrix.length + 1];
		int compareImage = 1;
		int pic = (picNo - 1);
		int picFeature;
		ArrayList<Integer> relevantImages = new ArrayList<Integer>(); //used to add checked images
		double[] averages = new double[matrix[0].length]; //used to keep up with the averages of checked images
		double[] weightDeviations = new double[matrix[0].length]; //keeps up with deviations of checked images.
		double[] updatedWeights = new double[matrix[0].length]; //has the weights to be used for the distance formula.
		double d = 0;
		
		/*
		 * If the relevance box is checked, we change the weights of each feature, unless
		 * only the queried image box is checked, then we keep all of the feature weights
		 * even.
		 */
		if(checkbox.isSelected()) {
			//Make the query image a relevant image.
			button[picNo].getCheckBox().setSelected(true);
			
			//Find all of the images that have been checked as relevant
			//and add them the arraylist.
			for(int i = 1; i < matrix.length; i++) {
				if(button[i].getCheckBox().isSelected()) {
					relevantImages.add(i - 1);
				}
			}
			/*
			 * If the queried image is the only one selected, all feature weights
			 * are the same, if not we have the calculate them.
			 */
			if(relevantImages.size() == 1) {
				for (int j = 1; j < updatedWeights.length; j++) {
					updatedWeights[j] = (double) 1/(matrix[0].length - 1);
				}
			}
			else {
				//Create a relevant matrix with the proper dimensions
				Double[][] relevantMatrix = new Double[relevantImages.size()][matrix[0].length];
				
				//Get the selected matrix value from the feature matrix.
				for(int i = 0; i < relevantImages.size(); i++) {
					relevantMatrix[i] = matrix[relevantImages.get(i)]; 
				}
				double sum = 0;
				
				//Get the average of each column in the matrix of relevant images.
				System.out.println("Sum of Weights: ");
				for(int j = 1; j < relevantMatrix[0].length; j++) {
					for(int i = 0; i < relevantMatrix.length; i++) {
						sum += relevantMatrix[i][j];
					}
					System.out.println(j + " " + sum);
					averages[j] = sum / relevantMatrix.length;
					sum = 0;
				}

				double deviation = 0;
				//Use the average to get the standard deviation of each column.
				for(int j = 1; j < relevantMatrix[0].length; j++) {
					for(int i = 0; i < relevantMatrix.length; i++) {
						deviation += Math.pow(relevantMatrix[i][j] - averages[j], 2);
					}
					weightDeviations[j] = Math.sqrt(deviation/(relevantMatrix.length - 1));
					deviation = 0;
				}

				double min = Double.MAX_VALUE;
				//Find the none zero minimum deviation.
				for(int j = 1; j < weightDeviations.length; j++) {
					if(weightDeviations[j] != 0 && weightDeviations[j] < min) {
						min = weightDeviations[j];
					}
				}

				/*
				 * If the standard deviation is 0 but the weight is not zero, set
				 * standard deviation to half of the minimum. Otherwise, let it stay
				 * 0.
				 */
				for(int j = 1; j < weightDeviations.length; j++) {
					if(weightDeviations[j] == 0) {
						if(averages[j] != 0) {
							weightDeviations[j] = 0.5*min;
						}
					}
				}
				
				System.out.println("Normalized Weights: ");
				//Invert the standard deviation to get the updated feature weights.
				for(int j = 1; j < updatedWeights.length; j++) {
					if(weightDeviations[j] != 0) {
						updatedWeights[j] = (double) 1 / weightDeviations[j];
					}
					else {
						updatedWeights[j] = 0;
					}
					System.out.println(j + " " + updatedWeights[j]);
				}
			}



		}
		else {
			for (int j = 1; j < updatedWeights.length; j++) {
				updatedWeights[j] = (double) 1/(matrix[0].length - 1);
			}
		}
		try {
			//Same exact distance method but with a weight added in distance calculations.
			double picSize = imageSize[pic];
			while(compareImage < 101) {
				d = 0;
				picFeature = picFeatureStartIndex;
				while(picFeature < matrix[0].length) {
					d += updatedWeights[picFeature] * Math.abs((matrix[pic][picFeature]/picSize)
							-(matrix[compareImage - 1][picFeature]/intensityMatrix[compareImage - 1][0]));
					picFeature++;
				}
				result[compareImage] = d;
				if(map.containsKey(d)) {
					map.get(d).add(compareImage + 1);
				}
				else {
					LinkedList<Integer> listStart = new LinkedList<Integer>();
					listStart.add(compareImage);
					map.put(d, listStart);
				}
				compareImage++;
			}
		} catch (ArrayIndexOutOfBoundsException e){
			photographLabel.setText("Please select an image to query.");
		}

		return result;
	}

	private void createFeatureMatrix(Double[][] matrix1, Double [][] matrix2) {
		double [][] result;
		//But both of the matrix together if they have the same number of rows.
		int columnsize = matrix1[0].length + matrix2[0].length;
		double[] columnMean = new double[columnsize];
		double[] columnDeviation = new double[columnsize];
		if(matrix1.length != matrix2.length) {
			System.out.println("Matrices must be of the same height to form a feature matrix.");
		}

		result = new double[matrix1.length][columnsize];

		for(int i = 0; i < matrix1.length; i++) {
			result[i][0] = matrix1[i][0];
			for(int j = 1; j < matrix1[0].length; j++) {
				result[i][j] = matrix1[i][j]/matrix1[i][0];
			}
			for(int k = matrix1[0].length; k < columnsize; k++) {
				result[i][k] = matrix2[i][k - matrix1[0].length]/matrix1[i][0];
			}

		}

		double sum = 0;
		//Find the average of each column.
		for(int j = 1; j < result[0].length; j++) {
			for(int i = 0; i < result.length; i++) {
				sum += result[i][j];
			}
			columnMean[j] = sum/result.length;
			sum = 0;
		}

		double deviation = 0;
		//Find the standard deviation of each column.
		for(int j = 1; j < result[0].length; j++) {
			for(int i = 0; i < result.length; i++) {
				deviation += Math.pow(result[i][j] - columnMean[j], 2);
			}

			columnDeviation[j] = Math.sqrt(deviation/(result.length - 1));
			deviation = 0;
		}
		
		//Create the feature matrix by doing the (value - the mean of the column) /
		// the deviation of the column for each value.
		for(int j = 1; j < result[0].length; j++) {
			for(int i = 0; i < result.length; i++) {
				/*
				 * Make sure that if standard deviation is 0 we get a 0 in the feature matrix
				 * instead of Nan (Not a Number)
				 */
				if(columnDeviation[j] != 0) {
					featureMatrix[i][j] = (result[i][j] - columnMean[j])/columnDeviation[j];
				}
				else {
					featureMatrix[i][j] = (double) 0;
				}
			}
		}

	}
	/*
	 * This method takes a double array and sorts it, then iterates through the
	 * that array to take values from a map and put them in a LinkedHashSet, ordered
	 * by distance, then it reorders the buttons that contain the pictures.
	 */
	private void sortByDistance(double[] distance) {
		Arrays.sort(distance);
		LinkedHashSet<Integer> ordered = new LinkedHashSet<Integer>();
		int i = 1;
		while(i < distance.length) {
			ordered.addAll(map.get(distance[i]));
			i++;
		}
		i = 1;
		for(int j: ordered) {
			buttonOrder[i] = j;
			i++;
		}	
	}

	/*
	 * Used for debugging to calculate example values from excel sheet.
	 */
	@SuppressWarnings("unused")
	private void runTeacherExample() {
		intensityMatrix = new Double[4][4];
		intensityMatrix[0][0] = (double) 80;
		intensityMatrix[0][1] = (double) 20;
		intensityMatrix[0][2] = (double) 30;
		intensityMatrix[0][3] = (double) 30;
		intensityMatrix[1][0] = (double) 10;
		intensityMatrix[1][1] = (double) 1;
		intensityMatrix[1][2] = (double) 5;
		intensityMatrix[1][3] = (double) 4;
		intensityMatrix[2][0] = (double) 5;
		intensityMatrix[2][1] = (double) 2;
		intensityMatrix[2][2] = (double) 2;
		intensityMatrix[2][3] = (double) 1;
		intensityMatrix[3][0] = (double) 10;
		intensityMatrix[3][1] = (double) 4;
		intensityMatrix[3][2] = (double) 4;
		intensityMatrix[3][3] = (double) 2;
		colorCodeMatrix = new Double[4][4];
		colorCodeMatrix[0][0] = (double) 20;
		colorCodeMatrix[0][1] = (double) 20;
		colorCodeMatrix[0][2] = (double) 20;
		colorCodeMatrix[0][3] = (double) 20;
		colorCodeMatrix[1][0] = (double) 0;
		colorCodeMatrix[1][1] = (double) 0;
		colorCodeMatrix[1][2] = (double) 5;
		colorCodeMatrix[1][3] = (double) 5;
		colorCodeMatrix[2][0] = (double) 2;
		colorCodeMatrix[2][1] = (double) 2;
		colorCodeMatrix[2][2] = (double) 1;
		colorCodeMatrix[2][3] = (double) 0;
		colorCodeMatrix[3][0] = (double) 2;
		colorCodeMatrix[3][1] = (double) 2;
		colorCodeMatrix[3][2] = (double) 2;
		colorCodeMatrix[3][3] = (double) 4;
	}
}
