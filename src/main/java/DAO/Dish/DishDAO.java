package DAO.Dish;

import DAO.Tag.ITagDAO;
import DAO.Tag.TagDAO;
import config.SingletonConnection;
import model.Dish;
import model.Restaurant;
import model.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static config.SingletonConnection.getConnection;

public class DishDAO implements IDishDAO {
    @Override
    public List<Dish> getListDishByPage(List<Dish> dishes, int start, int end) {
        List<Dish> dishList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            dishList.add(dishes.get(i));
        }
        return dishList;
    }

    public static final String SQL_INSERT_DISH = "insert into mon_an (name, image, note, price, nha_hang_id)values (?,?,?,?,?)";
    public static final String SQL_INSERT_DISH_TAG = "insert into mon_an_tag(the_id, mon_an_id) values (?,?);";
    public static final String SQL_UPDATE_TAG = "update the set luot_them = (luot_them + 1) where id = ?;";
    public static final String SQL_SELECT_THE_BY_ID = "select t.id, t.tagName,t.luot_them,t.luot_xem from the t where id = ?;";
    public static final String SQL_UPDATE_DISH_BY_ID = "update mon_an ma join nha_hang nh on ma.nha_hang_id = nh.id\n" +
            "set ma.name = ? , ma.image = ? , ma.note = ? , ma.price = ?  where ma.id = ?;";
    public static final String SQL_DELETE_DISH_TAG = "delete from mon_an_tag  where mon_an_id = ?;";
    public static final String SQL_DELETE_DISH = "delete from mon_an where id = ?;";

    Connection connection = SingletonConnection.getConnection();

    ITagDAO tagDAO = new TagDAO();
    ITagDAO iTagDAO = new TagDAO();

    @Override
    public void save(Dish dish, int[] tags) {
        int dish_id = 0;

        List<Tag> listTag = new ArrayList<>();
        try {
            connection.setAutoCommit(false);
            PreparedStatement statement1 = connection.prepareStatement(SQL_INSERT_DISH,Statement.RETURN_GENERATED_KEYS);
            statement1.setString(1,dish.getDishName());
            statement1.setString(2,dish.getDishImage());
            statement1.setString(3,dish.getDishNote());
            statement1.setInt(4,dish.getDishPrice());
            statement1.setInt(5,dish.getRestaurant().getId());
            int dishTag =  statement1.executeUpdate();

            ResultSet rs  = statement1.getGeneratedKeys();
            while (rs.next()){
                dish_id = rs.getInt(1);
            }
            PreparedStatement statement2 = connection.prepareStatement(SQL_INSERT_DISH_TAG);
            for (int i = 1; i <= tags.length ; i++) {
                PreparedStatement statement4 = connection.prepareStatement(SQL_SELECT_THE_BY_ID);
                statement4.setInt(1,tags[i-1]);
                ResultSet rs1 = statement4.executeQuery();
                String tagName = "";
                int addNumber = 0;
                int addView = 0;
                while (rs1.next()){
                    tagName =rs1.getString("tagName");
                    addNumber = rs1.getInt("luot_them");
                    addView = rs1.getInt("luot_xem");
                }
                int tag_id = tags[i-1];
                Tag tag = new Tag(tag_id,tagName,addNumber,addView);
                listTag.add(tag);
            }
            for (int tag_id : tags){
                statement2.setInt(1,tag_id);
                statement2.setInt(2,dish_id);
                statement2.executeUpdate();
            }
            if (dishTag > 0){
                for (int i = 0; i < listTag.size(); i++) {
                    PreparedStatement statement3 = connection.prepareStatement(SQL_UPDATE_TAG);
                    statement3.setInt(1,listTag.get(i).getId());
                    statement3.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try{
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<Dish> findAllByDishId(int id) {
        return null;
    }

    @Override
    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        connection = SingletonConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "select mon_an.id as id, mon_an.name as name, mon_an.image as image, mon_an.note as note, mon_an.price as price,\n" +
                        "nh.id as id_restaurant, nh.name as restaurant, nh.address as address, nh.phone,nh.open_time,nh.close_time \n" +
                        "from mon_an\n" +
                        "join nha_hang nh on nh.id = mon_an.nha_hang_id\n")

            ){
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String image = rs.getString("image");
                String note = rs.getString("note");
                int price = rs.getInt("price");

                List<Tag> tags = tagDAO.findAllByDishId(id);

                int id_restaurant = rs.getInt("id_restaurant");
                String restaurantName = rs.getString("restaurant");
                String restaurantAddress = rs.getString("address");
                String restaurantPhone = rs.getString("phone");
                Time openTime = rs.getTime("open_time");
                Time closeTime = rs.getTime("close_time");
                Restaurant restaurant = new Restaurant(id_restaurant, restaurantName, restaurantAddress, restaurantPhone, openTime, closeTime);
                Dish dish = new Dish(id, name, image, note, price, tags, restaurant);
                dishes.add(dish);
            }
            } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
        }

    @Override
    public Dish findById(int id) {
        Dish dish  = null;
        try(
                Connection connection = getConnection();
                PreparedStatement pstm = connection.prepareStatement(
                        "select ma.id as id, ma.name as name, ma.image as image, ma.note as note, ma.price as price, nh.id as id_restaurant, nh.name as restaurant from mon_an ma " +
                                "join nha_hang nh on ma.nha_hang_id = nh.id where ma.id = ?;"
                )
        ) {
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){
                String name = rs.getString("name");
                String image = rs.getString("image");
                String note = rs.getString("note");
                int price = rs.getInt("price");

                List<Tag> tags = tagDAO.findAllByDishId(id);

                int id_restaurant = rs.getInt("id_restaurant");
                String restaurantName = rs.getString("restaurant");
                Restaurant restaurant = new Restaurant(id_restaurant, restaurantName);

                dish = new Dish(id, name, image, note, price, tags, restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dish;
    }

    @Override
    public Dish findByName(String name) {
        Dish dish  = null;
        try(
                Connection connection = getConnection();
                PreparedStatement pstm = connection.prepareStatement(
                        "select ma.id as id, ma.name as name, ma.image as image, ma.note as note, ma.price as price, nh.id as id_restaurant, nh.name as restaurant from mon_an ma " +
                                "join nha_hang nh on ma.nha_hang_id = nh.id where ma.name = ?;"
                )
        ) {
            pstm.setString(1, name);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String image = rs.getString("image");
                String note = rs.getString("note");
                int price = rs.getInt("price");

                List<Tag> tags = tagDAO.findAllByDishId(id);

                int id_restaurant = rs.getInt("id_restaurant");
                String restaurantName = rs.getString("restaurant");
                Restaurant restaurant = new Restaurant(id_restaurant, restaurantName);

                dish = new Dish(id, name, image, note, price, tags, restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dish;
    }

    @Override
    public boolean update(Dish dish) {
        return false;
    }

    public static void main(String[] args) {
        IDishDAO iDishDAO = new DishDAO();
        System.out.println(iDishDAO.findById(2));
    }
    @Override
    public boolean update(Dish dish, int[] tags) {

        List<Tag> listTag = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_DISH_BY_ID);
            preparedStatement.setString(1,dish.getDishName());
            preparedStatement.setString(2,dish.getDishImage());
            preparedStatement.setString(3,dish.getDishNote());
            preparedStatement.setInt(4,dish.getDishPrice());
            preparedStatement.setInt(5,dish.getId());
            preparedStatement.executeUpdate();

            PreparedStatement preparedStatement1 = connection.prepareStatement(SQL_SELECT_THE_BY_ID);
            for (int i = 1; i <= tags.length ; i++) {
                preparedStatement1.setInt(1,tags[i-1]);
                ResultSet rs1 = preparedStatement1.executeQuery();
                String tagName = "";
                int addNumber = 0;
                int addView = 0;
                while (rs1.next()){
                    tagName =rs1.getString("tagName");
                    addNumber = rs1.getInt("luot_them");
                    addView = rs1.getInt("luot_xem");
                }
                int tag_id = tags[i-1];
                Tag tag = new Tag(tag_id,tagName,addNumber,addView);
                listTag.add(tag);
                PreparedStatement preparedStatement2 = connection.prepareStatement("update the set luot_them = (luot_them+1) where id = ?;");
                preparedStatement2.setInt(1,listTag.get(i-1).getId());
                preparedStatement2.executeUpdate();
                PreparedStatement preparedStatement3 = connection.prepareStatement(SQL_INSERT_DISH_TAG);
                preparedStatement3.setInt(1,listTag.get(i-1).getId());
                preparedStatement3.setInt(2,dish.getId());
                preparedStatement3.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(Dish dish) {
        try(
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into mon_an(name, image, note, price, nha_hang_id)\\n\" +\n" +
                                "                            \"VALUE (?,?,?,?,?,?)\\n)")

        ){
            preparedStatement.setString(1, dish.getDishName());
            preparedStatement.setString(2, dish.getDishImage());
            preparedStatement.setString(3, dish.getDishNote());
            preparedStatement.setInt(4, dish.getDishPrice());
            preparedStatement.setInt(5, dish.getRestaurant().getId());
            return preparedStatement.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        boolean rowDeleted = false;
        PreparedStatement preparedStatement;
        PreparedStatement preparedStatement1;
        try {
            preparedStatement = connection.prepareStatement(SQL_DELETE_DISH);
            preparedStatement.setInt(1,id);
            rowDeleted = preparedStatement.executeUpdate() > 0;
            preparedStatement1 = connection.prepareStatement(SQL_DELETE_DISH_TAG);
            preparedStatement1.setInt(1,id);
            preparedStatement1.executeUpdate();
            return rowDeleted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}
