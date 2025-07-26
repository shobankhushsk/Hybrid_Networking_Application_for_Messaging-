import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

 class GUIFrame extends JFrame implements ActionListener{
    
	
    JTextArea chatArea = new JTextArea();
    private JTextField msgJTextField = new JTextField();
    private JLabel msgJLabel = new JLabel("Message:");
	private JList<String> onlinePeersList = new JList<>();
    private JLabel heading3Label = new JLabel("Online Peers");
	private JLabel heading4Label = new JLabel("*************HYBRED NET WORKING*************");
 
 public GUIFrame(){
		
		Container con=getContentPane();
		
        con.setLayout(null);
        con.setBackground(new Color(173, 216, 230));
		setBounds(300, 50, 650, 500);

        Font stylishFont = new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14);
        Font style = new Font("and", Font.BOLD, 17);
		
		Font style2 = new Font("and", Font.BOLD, 20);
		
        chatArea.setBounds(50, 50, 310, 350);
        chatArea.setBackground(Color.WHITE);
        chatArea.setEditable(false);
        chatArea.setFont(stylishFont);

        msgJLabel.setBounds(55, 410, 100, 30);
        msgJLabel.setFont(style);
        con.add(msgJLabel);

        msgJTextField.setBounds(160, 410, 200, 30);
        msgJTextField.setBackground(Color.WHITE);
        msgJTextField.setFont(stylishFont);
        con.add(msgJTextField); 
      
	  
	    heading3Label.setBounds(450, 0, 270, 70);
        heading3Label.setFont(style);
        con.add(heading3Label);
	    heading3Label.setFont(style);
		

		heading4Label.setBounds(110, -20, 500, 70);
		con.add(heading4Label);
         heading4Label.setFont(style2);
		 
        onlinePeersList.setBounds(400, 50, 200, 330);
        con.add(onlinePeersList);
        con.add(chatArea);
        msgJTextField.addActionListener(this);
 }// end of cons...

  
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == msgJTextField) {
          
            String msg = msgJTextField.getText();
       
		  String ipAddress = (String) onlinePeersList.getSelectedValue();
		  
         try{ 
           Socket socket = new Socket(ipAddress,1010);
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(msg);
            chatArea.append("you Says: " + msg + "\n");
            out.close();
            socket.close();
         }catch(Exception ee){ee.printStackTrace();}
            
      }   
    
    }// end actionPerformed

    public String getIPAddress(){
	   return (String) onlinePeersList.getSelectedValue();
	}
	
    public void setListData(java.util.Vector v){
	  onlinePeersList.setListData(v);
    }
	
   
}// end class 



class Receiver extends Thread {
    private GUIFrame frame;

    public Receiver(GUIFrame frame) {
        this.frame = frame;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1010);

            do {
                Socket socket = serverSocket.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
               
                InetAddress addr=socket.getInetAddress();   
                String hostName = addr.getHostName();
                System.out.println("Client Connected: "+hostName); 

                String msg = in.readLine();
                frame.chatArea.append(hostName +"  " +  msg + "\n");
               
                in.close();
                socket.close();
            }while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
}// end class Receiver

 class HandleRegistryConn extends Thread {
    private GUIFrame frame;
	
	HandleRegistryConn(GUIFrame frame){
		this.frame=frame;
	}

    public void run() {
        try {
            Socket socket = new Socket("shobanPc", 9090);
			System.out.println("Connected with Registry Service...");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            do {
                String clientList = in.readLine();
                System.out.println(clientList);

                java.util.Vector v = getClientList(clientList);
                frame.setListData(v);

                System.out.println("*****************************");
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
  		    JOptionPane.showMessageDialog(null, "Error: "+e.getMessage());
        }
    }//end run method

    private java.util.Vector getClientList(String clientList) {
        java.util.StringTokenizer t = new java.util.StringTokenizer(clientList, ":");
        java.util.Vector v = new java.util.Vector();

        while (t.hasMoreElements()) {
            String hostName = (String) t.nextToken();
            v.addElement(hostName);
        }
        return v;
    }//end method
}//end HandleRegistryConn class
    

public class Conversation_Service_MessagingP2p{
	
	 public static void main(String[] args) {
       
      GUIFrame frame = new GUIFrame();
        frame.setVisible(true);
      
	  Receiver t = new Receiver(frame);
      t.start();
	  
        HandleRegistryConn regCon = new HandleRegistryConn(frame);
        regCon.start();
   
   }
	
}// end of main class 