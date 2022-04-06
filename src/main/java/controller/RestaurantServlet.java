package controller;

import DAO.deal.DealDAO;
import DAO.deal.IDealDAO;
import DAO.restaurant.IRestaurantDAO;
import DAO.restaurant.RestaurantDAO;
import model.Deal;
import model.Restaurant;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "RestaurantServlet", urlPatterns = "/restaurant")
public class RestaurantServlet extends HttpServlet {
    private IDealDAO dealDAO = new DealDAO();
    private IRestaurantDAO restaurantDAO = new RestaurantDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


//                request.setAttribute("restaurant", restaurantDAO.findAll());
//        RequestDispatcher requestDispatcher = request.getRequestDispatcher("create.jsp");
//        requestDispatcher.forward(request, response);
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "sort":

                break;
            case "create":

                break;
            case "edit":

                break;
            case "delete":
                showDeleteForm(request, response);
                break;
            case "search":

                break;
            case "permision":

                break;
            case "test-without-tran":

                break;
            default:
                listDeal(request, response);
                break;
        }
    }

    private void showDeleteForm(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        Deal deal = this.dealDAO.findById(id);
        RequestDispatcher dispatcher;
        request.setAttribute("deal", deal);
        dispatcher = request.getRequestDispatcher("restaurant/delete.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listDeal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                        request.setAttribute("deal", dealDAO.findAll());
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String nameRestaurant = request.getParameter("name");
//        String image = request.getParameter("image");
//        String description = request.getParameter("description");
//        int price = Integer.parseInt(request.getParameter("price"));
//        String idString = request.getParameter("restaurant");
//        int idRestaurant = Integer.parseInt(idString);
//        Restaurant restaurant = restaurantDAO.findById(idRestaurant);
//        Deal deal = new Deal(nameRestaurant, image, description, price, restaurant);
//    dealDAO.save(deal);
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":

                break;
            case "edit":

                break;
            case "search":

                break;
            case "delete":
                deleteDeal(request, response);

                break;
            case "sort":

                break;
        }
    }

    private void deleteDeal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        dealDAO.delete(id);
//        Deal deal = dealDAO.findById(id);
//        request.setAttribute("deal", deal);
        request.setAttribute("message", "This deal was deleted");
        RequestDispatcher dispatcher = request.getRequestDispatcher("restaurant/displayDelete.jsp");
        dispatcher.forward(request, response);
    }
}