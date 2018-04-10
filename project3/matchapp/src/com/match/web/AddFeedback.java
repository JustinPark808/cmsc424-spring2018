package com.match.web; 

import com.match.model.Match;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AddFeedback extends HttpServlet {

    private static final Logger logger = LogManager.getLogger("match");

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, NumberFormatException {
            // Gets the string data of all of the fields in the form to register
            String id1 = request.getParameter("id1");
            String id2 = request.getParameter("id2");
            String approval = request.getParameter("approval");

            // Get integer values of string fields 
            int id1Int = Integer.parseInt(id1);
            int id2Int = Integer.parseInt(id2);
            int approvalInt = Integer.parseInt(approval);

            // check to see if the values entered are valid input, if not, redirects
            // the response to the invalid input page
            if (id1Int < 0 || id2Int < 0) {
                response.sendRedirect("invalidinput");
            } else {
                // Here we make the call to the method in Person that connects to
                // the database and inserts the person with the given values
                // Your addPerson method should accept one more argument at the end
                // which contains the field you created
                double rating = Match.addFeedback(id1Int, id2Int, approvalInt);

                // Sends the response to the add page so that another person can
                // be added. ID is passed as a parameter to display the id for
                // the new user to refer to get and view matches
                response.sendRedirect("feedback?rating=" + rating);
            }
    }

}
