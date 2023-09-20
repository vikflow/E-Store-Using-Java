/* Name: Julian Harlan
Course: CNT 4714 – Fall 2023
Assignment title: Project 1 – Event-driven Enterprise Simulation
Date: Sunday September 17, 2023
*/



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import javax.swing.*;


public class NileDotCom extends JFrame{
	
	
	private static final long serialVersionUID = 1L;
	// variable to hold dimensions pixel
	private static final int WIDTH = 700;
	private static final int HEIGHT = 430;
	
	// labels, fields, and buttons
	private JLabel blankLabel, idLabel, qtyLabel, itemLabel, totalLabel;
	private JTextField blankTextField, idTextField, qtyTextField, itemTextField, totalTextField;
	private JButton findBtn, addBtn, viewCartBtn, finishBtn, newBtn, exitBtn;
	
	// declare reference variables for event handlers
	private ProcessButtonHandler procBtnHandler;
	private ConfirmButtonHandler confBtnHandler;
	private ViewButtonHandler viewBtnHandler;
	private FinishButtonHandler finBtnHandler;
	private NewButtonHandler newBtnHandler;
	private ExitButtonHandler exitBtnHandler;
	
	// formatting variables
	static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
	static NumberFormat percentFormatter = NumberFormat.getPercentInstance();
	static DecimalFormat decimalFormatter = (DecimalFormat) percentFormatter;
	
	//arrays to hold shopping cart items for later processing
	static String [] itemIDArray;
	static String[] itemTitleArray;
	static String[] itemInStockArray;
	static double[] itemPriceArray;
	static int[] itemQtyArray;
	static double[] itemDiscountArray;
	static double[] itemSubtotalArray;
	
	// initialize variables as needed
	static String itemID = "", itemTitle = "", outputStr = "", maxArraySizeStr = "",
			itemPriceStr = "", itemInStock = "", itemQtyStr = "", itemSubtotalStr = "", itemDiscountStr = "", orderSubtotalStr = "";
	static double itemPrice = 0, itemSubtotal = 0, orderSubtotal = 0, orderTotal = 0,
			itemDiscount = 0, orderTaxAmount = 0;
	
	private static int itemCount = 0, itemQuantity = 0, maxArraySize = 0;
	
	final static double TAX_RATE = 0.060,
						DISCOUNT_FOR_05 = .10,
						DISCOUNT_FOR_10 = .15,
						DISCOUNT_FOR_15 = .20;
	String fileName;
	
	public NileDotCom() {
		setTitle("Nile Dot Com - Fall 2023");
		setSize(WIDTH, HEIGHT);
		
		
		
		// instantiate JLabel objects
		blankLabel = new JLabel(" ", SwingConstants.RIGHT);
		idLabel = new JLabel("Enter item ID for Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT);
		qtyLabel = new JLabel("Enter quantity for Item #"+ (itemCount + 1) + ":", SwingConstants.RIGHT);
		itemLabel = new JLabel("Details for Item #"+ (itemCount + 1) + ":", SwingConstants.RIGHT);
		totalLabel = new JLabel("Order subtotal for " + itemCount + " item(s):", SwingConstants.RIGHT);
		
		// instantiate JTextField objects
		blankTextField = new JTextField();
		idTextField = new JTextField();
		qtyTextField = new JTextField();
		itemTextField = new JTextField();
		totalTextField = new JTextField();
		
		// set the layout - use a border layout to hold a grid layout in the north panel and a flow layout in the south panel
		Container pane = getContentPane();
		
		// create a 6-by-2 grid layout with a horizontal gap of 8 and a vertical gap of 2
		GridLayout grid6by3 = new GridLayout(6,3,8,3);
		GridLayout myGrid = new GridLayout(3, 3, 10, 10);// flow layout for buttons
		
		// create panels
		JPanel inputPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		// set layouts for panels
		inputPanel.setLayout(grid6by3);
		buttonPanel.setLayout(myGrid);
		
		// add labels to panel
		inputPanel.add(blankLabel);
		inputPanel.add(blankTextField);
		
		idLabel.setForeground(Color.yellow);
		inputPanel.add(idLabel);
		inputPanel.add(idTextField);
		
		inputPanel.add(qtyLabel);
		inputPanel.add(qtyTextField);	
		qtyLabel.setForeground(Color.yellow);
		
		inputPanel.add(itemLabel);
		inputPanel.add(itemTextField);	
		itemLabel.setForeground(Color.red);
		
		inputPanel.add(totalLabel);
		inputPanel.add(totalTextField);	
		totalLabel.setForeground(Color.red);
		

		
		
		// instantiate buttons and register handlers
		
		findBtn = new JButton("Find Item #" + (itemCount + 1));
		procBtnHandler = new ProcessButtonHandler();
		findBtn.addActionListener(procBtnHandler);
		
		addBtn = new JButton("Add Item #" + (itemCount + 1));
		confBtnHandler = new ConfirmButtonHandler();
		addBtn.addActionListener(confBtnHandler);
		
		viewCartBtn = new JButton("View cart");
		viewBtnHandler = new ViewButtonHandler();
		viewCartBtn.addActionListener(viewBtnHandler);
		
		finishBtn = new JButton("Checkout");
		finBtnHandler = new FinishButtonHandler();
		finishBtn.addActionListener(finBtnHandler);

		newBtn = new JButton("Empty cart New Order");
		newBtnHandler = new NewButtonHandler();
		newBtn.addActionListener(newBtnHandler);
		
		exitBtn = new JButton("Exit (Close App)");
		exitBtnHandler = new ExitButtonHandler();
		exitBtn.addActionListener(exitBtnHandler);
		//etra button size
		findBtn.setPreferredSize(new Dimension(150, 50));
		// initial visibility settings for buttons, fields
		addBtn.setEnabled(false);
		viewCartBtn.setEnabled(false);
		finishBtn.setEnabled(false);
		itemTextField.setEditable(false);
		totalTextField.setEditable(false);
		blankTextField.setEditable(false);
		blankTextField.setForeground(Color.black);
		blankTextField.setVisible(false);
		
		// add buttons to panel
		buttonPanel.add(findBtn);
		buttonPanel.add(addBtn);
		buttonPanel.add(viewCartBtn);
		buttonPanel.add(finishBtn);
		buttonPanel.add(newBtn);
		buttonPanel.add(exitBtn);
		
		// add panels to content pane using BorderLayout
		pane.add(inputPanel, BorderLayout.NORTH);
		pane.add(buttonPanel, BorderLayout.SOUTH);
		
		centerFrame(WIDTH, HEIGHT);
		pane.setBackground(Color.black);
		inputPanel.setBackground(Color.gray);
		buttonPanel.setBackground(Color.black);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 30, 10));
	}
	
	public void centerFrame(int frameWidth, int frameHeight) {
		// create a Toolkit object
		Toolkit aToolkit = Toolkit.getDefaultToolkit();
		
		// create a Dimension object with user screen information
		Dimension screen = aToolkit.getScreenSize();
		
		// assign x, y position of upper-left corner of frame
		int xPositionOfFrame = (screen.width - frameWidth) / 2;
		int yPositionOfFrame = (screen.height - frameHeight) / 2;
		setBounds(xPositionOfFrame, yPositionOfFrame, frameWidth, frameHeight);
	}
	
	// private class for process item button
	private class ProcessButtonHandler implements ActionListener{
		@SuppressWarnings("resource")
		public void actionPerformed(ActionEvent ae) {
			int availableQuantity = 0;
			String outputMessage, priceData, itemIDFromFile;
			boolean foundID = false, isNumItemsOk = false, isItemQtyOk = false, isItemInStock = true;
			
			// define file, file reader and buffered readers, plus a scanner object
			File priceFile= new File("inventory.csv");
			FileReader priceFileReader = null;
			BufferedReader priceBuffReader = null;
			Scanner scnr = null;
			
			try {
				maxArraySize = 20;
				
				// check if quantity is in bounds?
				if(itemCount <= maxArraySize) {
					isNumItemsOk = true;
				}
				if(Integer.parseInt(qtyTextField.getText()) > 0){
					isItemQtyOk = true;
				}
				
				//create arrays to hold shopping cart items
				if(itemCount == 0) {
					// declare arrays
					itemIDArray = new String[maxArraySize];
					itemTitleArray = new String[maxArraySize];
					itemInStockArray = new String[maxArraySize];
					itemPriceArray = new double[maxArraySize];
					itemQtyArray = new int[maxArraySize];
					itemDiscountArray = new double[maxArraySize];
					itemSubtotalArray = new double[maxArraySize];
				}
				
				itemID = idTextField.getText();
				itemQuantity = Integer.parseInt(qtyTextField.getText());
				
				// read a line of the inventory file
				priceFileReader = new FileReader(priceFile);
				priceBuffReader = new BufferedReader(priceFileReader);
				priceData = priceBuffReader.readLine();
				
				// while loop
				while(priceData != null && isItemInStock) {
					scnr = new Scanner(priceData);
					scnr.useDelimiter(", ");
					itemIDFromFile = scnr.next();
					
					if(itemID.equals(itemIDFromFile)) {
						foundID = true;
						itemTitle = scnr.next();
						itemInStock = scnr.next();
						
						if(!itemInStock.equals("true")) {
							isItemInStock = false;
						}
						else {

				            availableQuantity = Integer.parseInt(scnr.next());
				        }
						itemPriceStr = scnr.next();
						itemPrice = Double.parseDouble(itemPriceStr);
						break;
					}
//					itemTitle = scnr.next();
					priceData = priceBuffReader.readLine();
					
				}// end while
				
				if(foundID == false || isNumItemsOk == false || isItemQtyOk == false || isItemInStock == false ) {
					if(foundID == false) {
						outputMessage = "Item ID " + itemID + " not in file";
						JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
						idTextField.setText("");
						qtyTextField.setText("");
					}// end if
					
					if(isNumItemsOk == false || isItemQtyOk == false) {
						outputMessage = "Please enter positive numbers for number of items";
						JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
					}
					
					if(isItemInStock == false) {
						outputMessage = "Sorry... That item is out of stock, please try another item";
						JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
						idTextField.setText("");
						qtyTextField.setText("");
					}// end if
					
					
				}// end if
				else {
					itemQuantity = Integer.parseInt(qtyTextField.getText());
					itemQtyStr = String.valueOf(itemQuantity);
					if (itemQuantity > availableQuantity) {
					    outputMessage = "Insuficient stock. Only " + availableQuantity + " on hand. Please reduce the quantity.";
					    JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
					    qtyTextField.setText("");
					    return; // Do not proceed further
					}
					if(itemQuantity > 0 && itemQuantity < 5) {
						itemDiscount = 0;
					}
					else if(itemQuantity >= 5 && itemQuantity < 10) {
						itemDiscount = DISCOUNT_FOR_05;
					}
					else if(itemQuantity >= 10 && itemQuantity < 15) {
						itemDiscount = DISCOUNT_FOR_10;
					}
					else {
						itemDiscount = DISCOUNT_FOR_15;
					}// end if
					
					// calculating sub-totals
					itemSubtotal = itemPrice * (1 - itemDiscount) * itemQuantity;
					orderSubtotal += itemSubtotal;
					
					// set currency format and percentage format for output strings
					itemPriceStr = currencyFormatter.format(itemPrice);
					itemDiscountStr = decimalFormatter.format(itemDiscount);
					itemSubtotalStr = currencyFormatter.format(itemSubtotal);
					
					// build out message string
					outputMessage = itemID + " " + itemTitle + " " + itemPriceStr + " "
							+ itemQuantity + " " + itemDiscountStr + " " + itemSubtotalStr;
					// update labels
					idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
					qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1) + ":");
					itemLabel.setText("Details for Item #" + (itemCount + 1) + ":");
					
					itemTextField.setText(outputMessage);
					findBtn.setEnabled(false);
					addBtn.setEnabled(true);
				} // end else
				
			} catch(NumberFormatException e) {
				outputMessage = "Invalid input for number of line items or quantity of items";
				JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.err.println("File not Found");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Error reading file");
			}
		}
		
	}
	
	// private class for process item button
	private class ConfirmButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			String confirmStr = "Item #" + (itemCount+1) + " accepted. Added to your cart.";
			
			// set array values with items from inventory file user has confirmed - added to cart
			itemIDArray[itemCount] = itemID;
			itemTitleArray[itemCount] = itemTitle;
			itemPriceArray[itemCount] = itemPrice;
			itemQtyArray[itemCount] = itemQuantity;
			itemDiscountArray[itemCount] = itemDiscount;
			itemSubtotalArray[itemCount] = itemSubtotal;
			
			// increment item count in order
			itemCount++; // count the number of items in order
			assert itemCount > 0: "Item count is <= 0";
			
			// dump item confirmed message
			orderSubtotalStr = currencyFormatter.format(orderSubtotal);
			totalTextField.setText(orderSubtotalStr);
			JOptionPane.showMessageDialog(null, confirmStr, "Nile Dot Com - Item Confirmed", JOptionPane.INFORMATION_MESSAGE);
			
			// reset labels, text fields, and buttons
			idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
			qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1) + ":");
			itemLabel.setText("Details for Item #" + itemCount + ":");
			totalLabel.setText("Order subtotal for " + itemCount + " item(s)" + ":");
			
			
			finishBtn.setEnabled(true);	
			viewCartBtn.setEnabled(true);
			findBtn.setEnabled(true);
			findBtn.setText("Find item #" + (itemCount + 1));
			addBtn.setEnabled(false);
			addBtn.setText("Add Item #" + (itemCount + 1));
			
			idTextField.setText("");
			qtyTextField.setText("");
		}
	}
	
	// private class for view order button
	private class ViewButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			outputStr = "";
			
			//iterate through current arrays and dump view of shopping cart
			for (int i = 0 ; i < itemCount; i++) {
				outputStr = outputStr.concat((i + 1) + ". " 
						+ itemIDArray[i] + " " 
						+ itemTitleArray[i] + " "
						+ currencyFormatter.format(itemPriceArray[i]) + " "
						+ itemQtyArray[i] + " " 
						+ decimalFormatter.format(itemDiscountArray[i]) + " " 
						+ currencyFormatter.format(itemSubtotalArray[i]) + "\n");
			}
			JOptionPane.showMessageDialog(null, outputStr, "Nile Dot Com - Current Shopping Cart Status", JOptionPane.INFORMATION_MESSAGE);

		}
	}
	
	// private class for finish button
	private class FinishButtonHandler implements ActionListener {
	    public void actionPerformed(ActionEvent ae) {
	        String totalMessage = "";

	     // Get the current date and time
	        Date today = new Date(System.currentTimeMillis());
	        
	     //   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	        DateFormat usaDateTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
	     
	     // Create a SimpleDateFormat object to format the date in the desired format
	        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss z");

	        // Format the date and time
	        String formattedDate1 = formatter.format(today);

	        FileWriter transactionFile;
	        PrintWriter aPrintWriter = null;
	        String prevTimestamp = ""; 

	        try {
	            transactionFile = new FileWriter("transaction.csv", true);
	            aPrintWriter = new PrintWriter(transactionFile);

	            String todayStr = usaDateTime.format(today);	           
	            StringBuilder outputSB = new StringBuilder();

	            // calculate total and format to two decimals
	            orderTaxAmount = orderSubtotal * TAX_RATE;
	            orderTotal = orderSubtotal + orderTaxAmount;
	            
	        



	            // iterate through shopping cart items and add to invoice message
	            for (int i = 0; i < itemCount; i++) {
	                // generate  permutation
	                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
	                LocalDateTime myDateObj = LocalDateTime.now();
	                String formattedDate = myDateObj.format(myFormatObj);
	             
	               
	           //     String formattedDate1 = now.format(formatter);
	                // Check if the current time-stamp is different from the previous one
	                if (!formattedDate.equals(prevTimestamp)) {
	                    // Add a blank line if the time-stamp is different
	                    if (prevTimestamp.length() > 0) {
	                        aPrintWriter.println();
	                    }
	                    prevTimestamp = formattedDate; // Update the previous timestamp
	                }

	                // print to the screen
	                totalMessage +=
	                		((i + 1) + " "
	                        + itemIDArray[i] + " "
	                        + itemTitleArray[i] + " "
	                        + currencyFormatter.format(itemPriceArray[i]) + " "
	                        + itemQtyArray[i] + " "
	                        + decimalFormatter.format(itemDiscountArray[i]) + " "
	                        + currencyFormatter.format(itemSubtotalArray[i]) + "\n");

	                // write to transaction file
	                outputSB.setLength(0); // Clear the StringBuilder
	                outputSB.append(formattedDate + ", "
	                        + itemIDArray[i] + ", "
	                        + itemTitleArray[i] + ", "
	                        + itemPriceArray[i] + ", "
	                        + itemQtyArray[i] + ", "
	                        + itemDiscountArray[i] + ", "
	                        + currencyFormatter.format(itemSubtotalArray[i]) + ", "
	                        + todayStr);
	                aPrintWriter.println(outputSB.toString());
	            }
	        } catch (IOException ioException) {
	            JOptionPane.showMessageDialog(null, "Error: Problem writing to file", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
	        } finally {
	            if (aPrintWriter != null) {
	                aPrintWriter.close();
	            }
	        }//

	        //final invoice head
	        totalMessage =
	                "Date:" + formattedDate1+"\n\n"+
	                "Number of line items: " + itemCount + "\n\n" +
	                "Item# / ID /Title / Price / Qty / Disc / % / Subtotal:\n" +
	                totalMessage;


	        // total message footer
	        totalMessage +=
	                "\n\nOrder Subtotal: " + currencyFormatter.format(orderSubtotal)
	                        + "\n\nTax Rate:      " + percentFormatter.format(TAX_RATE)
	                        + "\n\nTax Amount:    " + currencyFormatter.format(orderTaxAmount)
	                        + "\n\nOrder Total:   " + currencyFormatter.format(orderTotal)
	                        + "\n\nThank you for shopping at Nile Dot Com";
	        JOptionPane.showMessageDialog(null, totalMessage, "Nile Dot Com - Final Invoice", JOptionPane.INFORMATION_MESSAGE);

	        // set text fields
	        idTextField.setEditable(false);
	        qtyTextField.setEditable(false);

	        // set buttons
	        viewCartBtn.setEnabled(true);
	        findBtn.setEnabled(false);
	        finishBtn.setEnabled(false);
	    }
	}

	
	// private class for new order button
	private class NewButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			// reset arrays
			itemIDArray = new String[200];
			itemTitleArray = new String[200];
			itemPriceArray  = new double[200];
			itemQtyArray = new int[200];
			itemDiscountArray = new double[200];
			itemSubtotalArray = new double[200];
			
			// reset variables
			itemID = ""; itemTitle = ""; outputStr = ""; maxArraySizeStr = "";
					itemPriceStr = ""; itemInStock = ""; itemQtyStr = ""; itemSubtotalStr = ""; itemDiscountStr = ""; orderSubtotalStr = "";
			itemPrice = 0; itemSubtotal = 0; orderSubtotal = 0; orderTotal = 0;
					itemDiscount = 0; orderTaxAmount = 0;
			
			itemCount = 0; itemQuantity = 0; maxArraySize = 0;
			
			// reset labels
			idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
			qtyLabel.setText("Enter quantity for Item #"+ (itemCount + 1) + ":");
			itemLabel.setText("Details for Item #"+ (itemCount + 1) + ":");
			totalLabel.setText("Order subtotal for " + itemCount + " item(s):");
			
			// reset fields
			idTextField.setText("");
			idTextField.setEditable(true);
			qtyTextField.setText("");
			qtyTextField.setEditable(true);
			itemTextField.setText("");
			totalTextField.setText("");
			
			// reset buttons
			findBtn.setEnabled(true);
			findBtn.setText("Find Item #" + (itemCount + 1));
			addBtn.setText("Add Item #" + (itemCount + 1));
			addBtn.setEnabled(false);
			viewCartBtn.setEnabled(false);
			finishBtn.setEnabled(false);
		}
	}
	
	// private class for exit button
	private class ExitButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			System.exit(0);
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NileDotCom project = new NileDotCom();
		project.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		project.setVisible(true);
	}

}
