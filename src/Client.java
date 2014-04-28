import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client 
{

	//Defined elements of gui
    JFrame frame = new JFrame("Chat");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    
    //Defines character streams
    BufferedReader input;
    PrintWriter output;

    public Client() 
    {
        //gui layout
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "South");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();
  
        //Action listener(Enter in text field)
        textField.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	output.println(textField.getText());
            	textField.setText(null);
            }
        });
    }

    private String getServerIP() 
    {    	
        return JOptionPane.showInputDialog(frame, "Enter Server IP:", "Server Select", JOptionPane.QUESTION_MESSAGE);
    }

    private String chooseName() 
    {    	
        return JOptionPane.showInputDialog(frame, "Enter screen name:", "Name Select", JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException 
    {

        // Make connection and initialize streams
        String serverAddress = getServerIP();
        Socket serverSocket = new Socket(serverAddress, 9001);
        input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        output = new PrintWriter(serverSocket.getOutputStream(), true);

        // Server message processing according to the protocol.
        while (true) 
        {
        	//get input stream from server
            String line = input.readLine();
            //
            if (line.startsWith("EnterName")) 
            {
                output.println(chooseName());
            } 
            else if (line.startsWith("NameRegister")) 
            {
                textField.setEditable(true);
            } 
            else if (line.startsWith("Message")) 
            {
            	//Command to disconnect from server and close client
            	if (line.endsWith("/quit"))
            	{
            		messageArea.append("[Disconnected]");
            		System.exit(0);
            	}
            	else
            	{
            		System.out.println(textField.getText());
                	messageArea.append(line.substring(8) + "\n");
                	messageArea.setCaretPosition(messageArea.getDocument().getLength());   
            	}
            }
            //Formatting when chat log is requested from the server
            else if (line.startsWith("START OF LOG"))
            {
            	messageArea.append("_____START_OF_LOG_____\n");
            }
            else if (line.startsWith("END OF LOG"))
            {
            	messageArea.append("______END_OF_LOG______\n");
            }
        }
    }

    public static void main(String[] args) throws Exception 
    {
        Client client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
