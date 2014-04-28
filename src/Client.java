
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client 
{

    JFrame frame = new JFrame("Chat");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    BufferedReader input;
    PrintWriter output;

    public Client() 
    {
        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "South");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();
  
        // Add Listeners
        textField.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                output.println(textField.getText());
                textField.setText(null);
            }
        });
    }

    private String getServerAddress() 
    {
        return JOptionPane.showInputDialog(
            frame,
            "Enter Server IP:",
            "Server Select",
            JOptionPane.QUESTION_MESSAGE);
    }

    private String getName() 
    {
        return JOptionPane.showInputDialog(
            frame,
            "Enter screen name:",
            "Name Select",
            JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException 
    {

        // Make connection and initialize streams
        String serverAddress = getServerAddress();
        Socket serverSocket = new Socket(serverAddress, 9001);
        input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        output = new PrintWriter(serverSocket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) 
        {
            String line = input.readLine();
            if (line.startsWith("EnterName")) 
            {
                output.println(getName());
            } 
            else if (line.startsWith("NameRegister")) 
            {
                textField.setEditable(true);
            } 
            else if (line.startsWith("Message")) 
            {
                messageArea.append(line.substring(8) + "\n");
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
                
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
