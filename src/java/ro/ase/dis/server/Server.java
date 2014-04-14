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
            for (int i = 0; i < 50; i++) {
                String message = "";
                for (int j = 0; j < 10; j++) {
                    message += PasswordReader.getInstance().readPassword() + ",";
                }
                message += PasswordReader.getInstance().readPassword();
                taskSender.sendMessage(message);
                passwordCounter += 20;
            }
            taskSender.disconnect();
            out.format(" %1$s  passwords sent!", passwordCounter);
            out.println("<br/>");
            t.cancel();
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