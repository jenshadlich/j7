package de.jeha.j7.servlets;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author jenshadlich@googlemail.com
 */
public class VersionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        resp.setContentType("application/json");
        try (PrintWriter writer= resp.getWriter()) {
            writer.println(Resources.toString(Resources.getResource("version.json"), Charsets.UTF_8));
        }
    }
}
