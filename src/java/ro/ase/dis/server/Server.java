package ro.ase.dis.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/Server"})
public class Server extends HttpServlet {

    @EJB
    MessageTaskSender taskSender;
    @EJB
    MessageSubjectSender subjectSender;
    private static long passwordCounter;
    private final int validationCounter = 2;
    private final int bunchSize = 10;
    private final int maxPasswordsLength = 2;

    
    private enum taskTypes {

        BRUTE_FORCE, DICTIONARY
    };
    private final taskTypes taskType = taskTypes.BRUTE_FORCE;

    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<br/>");
            out.println("<br/>");
            out.println("Messages sent:");
            out.println("<br/>");
            out.println("<br/>");

            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    String encryptMessage = EncryptFile.getEncryptFile();
                    subjectSender.sendMessage(encryptMessage);
                    System.out.println("Subject sent:" + encryptMessage + " Time: " + new Date().getTime());
                }
            }, 1000 * 30, 1000 * 30);

            String encryptMessage = EncryptFile.getEncryptFile();
            subjectSender.sendMessage(encryptMessage);
            System.out.println("Subject sent:" + encryptMessage + " Time: " + new Date().getTime());

            taskSender.connect();
            switch (taskType) {
                case DICTIONARY:
                    dictionaryTaskSender();
                    break;
                case BRUTE_FORCE:
                    bruteForceTaskSender(maxPasswordsLength);
                    break;
            }
            
            taskSender.disconnect();
            out.format(" %1$s  passwords sent!", passwordCounter);
            out.println("<br/>");
            t.cancel();
        }
    }

    private void dictionaryTaskSender() {
        for (int i = 0; i < 500; i++) {
            String message = "";
            for (int j = 0; j < bunchSize; j++) {
                message += PasswordReader.getInstance().readPassword() + ",";
            }
            message += PasswordReader.getInstance().readPassword();
            for (int k = 0; k < validationCounter; k++) {
                taskSender.sendMessage(message);
            }

            passwordCounter += validationCounter * bunchSize;
        }
    }

    private void bruteForceTaskSender(int maxPasswordsLength)  {
        if(maxPasswordsLength < 1){
            throw new IllegalArgumentException();
        }
        createWords("", maxPasswordsLength);
    }

    private void createWords(String base, int maxPasswordsLength) {
        for(char c='a';c<='z';c++){
            if(maxPasswordsLength == 1){
                taskSender.sendMessage(base+c);
                passwordCounter += 1;
            } else {
                createWords(base+c, maxPasswordsLength-1);
            }
        }
    }

    protected void processPostRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<br/>");
            out.println("<br/>");
            out.println("Not implemented...");
            out.println("<br/>");
            out.println("<br/>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processGetRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPostRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
