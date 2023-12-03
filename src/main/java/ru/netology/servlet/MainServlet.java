package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private static final String POSTS_PATH = "/api/posts";
    private static final String POST_BY_ID_PATH = "/api/posts/\\d+";
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String DELETE_METHOD = "DELETE";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            long id = 0;

            if (path.matches(POST_BY_ID_PATH)) {
                id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            }
            // primitive routing
            if (method.equals(GET_METHOD) && path.equals(POSTS_PATH)) {
                controller.all(resp);
                return;
            }
            if (method.equals(GET_METHOD) && path.matches(POST_BY_ID_PATH)) {
                // easy way
                controller.getById(id, resp);
                return;
            }
            if (method.equals(POST_METHOD) && path.equals(POSTS_PATH)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(DELETE_METHOD) && path.matches(POST_BY_ID_PATH)) {
                // easy way
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (NotFoundException notFoundException) {
            notFoundException.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

