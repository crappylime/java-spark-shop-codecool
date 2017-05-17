package com.codecool.shop.controller;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductCategoryDaoSqlite;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.ProductDaoSqlite;
import com.codecool.shop.model.Basket;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import com.codecool.shop.view.ProductView;
import com.codecool.shop.view.UserInput;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.*;

public class ProductController extends BaseController{
    private ProductDao productDao = new ProductDaoSqlite();
    private ProductCategoryDao productCategoryDao = new ProductCategoryDaoSqlite();
    private ProductView view = new ProductView();



    public String showProducts(Request req, Response res) {
        Map params = new HashMap<>();

        List<Product> products = productDao.getAll();
        params.put("products", products);

        params.put("basket", req.session().attribute("basket"));

        ModelAndView modelAndView = new ModelAndView(params, "product/index");

        return render(modelAndView);
    }

    public void listProductsByCategory() {
        List<ProductCategory> categories = productCategoryDao.getAll();
        view.displayCategoriesList(categories);

        Integer categoryId = UserInput.getUserInput();
        ProductCategory category = productCategoryDao.find(categoryId);
        List<Product> products = productDao.getBy(category);
        view.displayProductsList(products);
    }

    public String addToCard(Request req, Response res) {
        Integer productId = Integer.parseInt(req.params(":id"));
        productId = 1;
        Product productToAdd = productDao.find(productId);
        Integer quantity = req.queryMap("quantity").integerValue();
        quantity = 2;
        Basket basket = req.session().attribute("basket");
        basket.add(productToAdd, quantity);
          res.redirect("/");
        return null;
    }
}
