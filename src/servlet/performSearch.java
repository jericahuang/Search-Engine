package servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import engine.JedisIndex;
import engine.JedisMaker;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import engine.WikiSearch;
import static engine.WikiSearch.search;
import java.util.List;
import redis.clients.jedis.Jedis;

/**
 *
 * @author jericahuang
 */
@WebServlet(urlPatterns = {"/performSearch"})
public class performSearch extends HttpServlet {
 
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet performSearch</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet performSearch at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String query = request.getParameter("searchInput");
        
        out.print("<!DOCTYPE html><html><head><title>Hooli Search</title><link href='https://fonts.googleapis.com/css?family=Open+Sans:400,700' rel='stylesheet' type='text/css'>\n" +
"        <link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>\n" +
"        <link rel=\"stylesheet\" href=\"styles.css\" type=\"text/css\">\n" +
"        <meta charset=\"UTF-8\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link href=\"favicon.ico\" rel=\"icon\" type=\"image/x-icon\" />\n" +
"        <link href=\"favicon.ico\" rel=\"shortcut icon\" type=\"image/x-icon\">\n" +
"    </head>\n" +
"    <body>"+"<div id=\"headerContainer\">\n" +
"            <a href=\"index.html\" id=\"smallLogo\"><span class=\"blue\">H</span><span class=\"red\">o</span><span class=\"yellow\">o</span><span class=\"green\">l</span><span class=\"blue\">i</span></a>\n" +
"\n" +
"            <form action=\"performSearch\" method=\"POST\" id=\"secondSearch\">\n" +
"                <input type=\"text\" name=\"searchInput\" id=\"searchInput\" value=\""+query+"\"> \n" +
"                <input type=\"submit\" value=\"Search\">\n" +
"            </form><hr></div>");
        Jedis jedis = JedisMaker.make();
        JedisIndex index = new JedisIndex(jedis);

        WikiSearch search = search(query, index);
        
        List<String> results = search.searchToHtml(); 
        
        double finalTime = (System.currentTimeMillis() - startTime)/1000.0;
        out.println("<p id=\"stats\">"+results.size()+" results for <span class='strong'>"+query+"</span> ("+finalTime+" seconds)</p><table id=\"results\">");
          
        
        
        //out.println(search.print());
        
        for (String markup : search.searchToHtml()){
                out.print(markup);
            }
        
        
        out.print("</table></div></body></html>");
            
        
        
        out.close();
        
    }
    
    

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
